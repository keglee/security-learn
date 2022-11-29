# 认证流程分析

## Spring Security大局观
以下内容基于Servlet应用进行分析。首先通过官方的Spring Security在基于Servlet应用的架构图，了解下请求是如何到达Spring Security的。

[官方图](aaaa)

Spring在Filter Chain中加入`DelegatingFilterProxy`过滤器；`DelegatingFilterProxy`将所有请求委托给`FilterChainProxy`；`FilterChainProxy`根据请求调用合适的`SecurityFilterChain`。

- `DelegatingFilterProxy`: Spring提供的一个Filter实现，其作用是将所有请求委托给一个由Spring容器管理的Filter Bean。
- `FilterChainProxy`: Spring Security提供的Filter，通常被包装在`DelegatingFilterProxy`中；它将过滤器请求委托给 Spring 管理的过滤器 bean 列表。
- `SecurityFilterChain`: 定义能够与`HttpServletRequest`匹配的过滤器链。`FilterChainProxy`使用`SecurityFilterChain`来确定应为请求调用哪些Spring Security过滤器。

## AbstractAuthenticationProcessingFilter

`AbstractAuthenticationProcessingFilter`是Spring Security用于处理认证请求的抽象Security Filter。`AbstractAuthenticationProcessingFilter`通过`requiresAuthenticationRequestMatcher`属性来指定需要身份认证的请求，如果请求与`requiresAuthenticationRequestMatcher`匹配，就拦截请求并尝试进行身份认证。

## 认证过程

`AbstractAuthenticationProcessingFilter`要求必须设置`authenticationManager`属性，因为它需要`AuthenticationManager`来完成身份认证请求。
`AbstractAuthenticationProcessingFilter`中身份认证由`attemptAuthentication`方法执行，该方法需要创建身份认证令牌，并调用`AuthenticationManager`来完成身份认证。
Spring Security默认只提供了`UsernamePasswordAuthenticationFilter`实现类，用于处理基于表单提交的认证请求，默认匹配/login请求，以下是`UsernamePasswordAuthenticationFilter`对`attemptAuthentication`方法的实现。

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

从上面代码可以看到最终调用`AuthenticationManager`的`authenticate`完成的身份验证。

### AuthenticationManager

`AuthenticationManager`是用于处理认证请求的接口，只声明了一个`authenticate(Authentication)`方法，该方法尝试对传递的`Authentication`对象进行身份验证，如果成功则返回一个完全填充的`Authentication`对象。

在`AuthenticationManager`众多的实现类中，最常用的实现类是`ProviderManager`。`ProviderManager`有两个重要的属性`List<AuthenticationProvider> providers`和`AuthenticationManager parent`。

`AuthenticationProvider`用于针对不同`Authentication`进行具体的认证。例如`DaoAuthenticationProvider`针对`UsernamePasswordAuthenticationToken`进行认证。

#### ProviderManager逻辑

1. 首先通过循环`providers`进行身份认证，直到得到非空响应(非空响应表示认证成功)；
2. 当`providers`前面的`AuthenticationProvider`出现了除`AccountStatusException`和`InternalAuthenticationServiceException`之外的异常，会使用`lastException`变量记录异常，然后遍历下一个`AuthenticationProvider`进行认证；如果后续的`AuthenticationProvider`认证成功，则忽略前面的异常。
3. 如果`providers`中的`AuthenticationProvider`都认证失败，且指定了`parent`，则使用`parent`进行认证。

### 认证成功

### 认证失败

## 事件发布

## 
- `ProviderManager`
- `AuthenticationProvider`