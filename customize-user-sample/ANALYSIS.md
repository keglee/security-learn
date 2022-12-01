# 认证流程分析

通过分析Spring Security认证流程，认识了认证过程几个重要的类：`AbstractAuthenticationProcessingFilter`，`UsernamePasswordAuthenticationFilter`, `AuthenticationManager`, `ProviderManager`, `AuthenticationProvider`。

## 1. Spring Security大局观
以下内容基于Servlet应用进行分析。首先通过官方的Spring Security在基于Servlet应用的架构图，了解下请求是如何到达Spring Security的。

[官方图](aaaa)

Spring在Filter Chain中加入`DelegatingFilterProxy`过滤器；`DelegatingFilterProxy`将所有请求委托给`FilterChainProxy`；`FilterChainProxy`根据请求调用合适的`SecurityFilterChain`。

- `DelegatingFilterProxy`: Spring提供的一个Filter实现，其作用是将所有请求委托给一个由Spring容器管理的Filter Bean。
- `FilterChainProxy`: Spring Security提供的Filter，通常被包装在`DelegatingFilterProxy`中；它将过滤器请求委托给 Spring 管理的过滤器 bean 列表。
- `SecurityFilterChain`: 定义能够与`HttpServletRequest`匹配的过滤器链。`FilterChainProxy`使用`SecurityFilterChain`来确定应为请求调用哪些Spring Security过滤器。

## 2. 简述Security Filter

Spring Security内置了一些Filter，这些Filter被称为Security Filter。Security Filter通过`SecurityFilterChain`插入到`FilterChainProxy`。下面介绍一些Filter：

- `UsernamePasswordAuthenticationFilter`: 用于处理基于表单提交的用户和密码认证过滤器；
- `AnonymousAuthenticationFilter`: 匿名认证过滤器，在Spring Security中，所有对资源的访问都要有`Authentication`，使用匿名身份访问不需要认证的资源；
- `RememberMeAuthenticationFilter`: 用于处理 记住我 功能的过滤器；
- `SessionManagementFilter`: 用于管理session的过滤器；
- `ConcurrentSessionFilter`: 并发session处理过滤器，主要有两个功能判断session是否过期和更新最新的访问时间。
- ......

### 2.1 AbstractAuthenticationProcessingFilter

`AbstractAuthenticationProcessingFilter`是Spring Security用于处理认证请求的抽象Security Filter。`AbstractAuthenticationProcessingFilter`通过`requiresAuthenticationRequestMatcher`属性来指定需要身份认证的请求，如果请求与`requiresAuthenticationRequestMatcher`匹配，就拦截请求并尝试进行身份认证。

`AbstractAuthenticationProcessingFilter`要求必须设置`authenticationManager`属性，因为它需要`AuthenticationManager`来完成身份认证请求。

`AbstractAuthenticationProcessingFilter`中身份认证由`attemptAuthentication`方法执行，该方法需要创建身份认证令牌，并调用`AuthenticationManager`来完成身份认证。

`UsernamePasswordAuthenticationFilter`是`AbstractAuthenticationProcessingFilter`的实现类，用于处理基于表单提交的认证请求，默认匹配/login请求。

`UsernamePasswordAuthenticationFilter`对`attemptAuthentication`方法的实现: 从`HttpServletRequest`中提取出username和password，并根据username和password创建`UsernamePasswordAuthenticationToken`实例，最后调用`AuthenticationManager`的`authenticate`方法进行认证。

`UsernamePasswordAuthenticationToken`是`Authentication`的一种实现，用于简单表示用户名和密码的请求令牌。

```java

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";

	public static final String SPRING_SECURITY_FORM_PASSWORD_KEY = "password";

	// 省略其他代码

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		if (this.postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
		String username = obtainUsername(request);
		username = (username != null) ? username : "";
		username = username.trim();
		String password = obtainPassword(request);
		password = (password != null) ? password : "";
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
		// Allow subclasses to set the "details" property
		setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}

	protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    // 省略其他代码
}

```

### 2.2 AuthenticationManager

`AuthenticationManager`是用于处理认证请求的接口，只声明了一个`authenticate(Authentication)`方法，该方法尝试对传递的`Authentication`对象进行身份验证，如果成功则返回一个完全填充的`Authentication`对象。

`ProviderManager`是一个常用的`AuthenticationManager`实现。`ProviderManager`有两个重要的属性`List<AuthenticationProvider> providers`和`AuthenticationManager parent`。 

`AuthenticationProvider`用于针对不同`Authentication`进行具体的认证。例如`DaoAuthenticationProvider`针对`UsernamePasswordAuthenticationToken`进行认证。

为了文章篇幅简洁，这里简述下`ProviderManager`的处理逻辑，不进行代码展示:

1. 首先通过循环`providers`进行身份认证，直到得到非空响应(非空响应表示认证成功)；
2. 当`providers`前面的`AuthenticationProvider`出现了除`AccountStatusException`和`InternalAuthenticationServiceException`之外的异常，会使用`lastException`变量记录异常，然后遍历下一个`AuthenticationProvider`进行认证；如果后续的`AuthenticationProvider`认证成功，则忽略前面的异常。
3. 如果`providers`中的`AuthenticationProvider`都认证失败，且指定了`parent`，则使用`parent`进行认证。

## 3. 认证过程

1. 在Spring Security中，当前请求到达`DelegatingFilterProxy`时，`DelegatingFilterProxy`会调用`FilterChainProxy`的`doFilter`方法来处理请求；
2. `FilterChainProxy`先从`filterChains`集合中获取匹配请求的`SecurityFilterChain`，接着调用`SecurityFilterChain`的`getFilters`方法获得`Filter`集合；最后以`Filter Chain`方式执行`Filter`集合；
3. 当执行到`UsernamePasswordAuthenticationFilter`，且是/login请求时，则开始尝试进行认证处理。先从`HttpServletRequest`中提取username和password，并根据username和password构建`UsernamePasswordAuthenticationToken`；
4. 接着将`UsernamePasswordAuthenticationToken`作为`AuthenticationManager`的`authenticate`方法的参数，`UsernamePasswordAuthenticationFilter`默认使用`ProviderManager`实例；
5. `ProviderManager`首先通过循环`providers`集合进行身份认证，如果`providers`集合都认证失败，就尝试交给`parent`进行认证；如果认证成功，则返回一个新的`Authentication`；
6. 如果认证成功，`UsernamePasswordAuthenticationFilter`将新的`Authentication`存放到当前当前线程的`SecurityContext`中；发布`InteractiveAuthenticationSuccessEvent`事件；进行`RememberMe`处理；调用`AuthenticationSuccessHandler`；
7. 如果认证失败，如果身份验证失败，它将委托给配置的`AuthenticationFailureHandler`以允许将失败信息传达给客户端。 默认实现是`SimpleUrlAuthenticationFailureHandler`，它向客户端发送 401 错误代码。