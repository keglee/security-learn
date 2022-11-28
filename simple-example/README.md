# 案例：Quick Start

这是一个简单的Spring Security示例，主要功能：开启访问限制，并且所有请求都需要进行认证；同时开启了表单登录，以及在`application.yml`中配置了用户名和密码。

如果不配置用户名和密码，则默认的用户名为user，在启动服务时自动生成默认密码并输出到日志

## 主要代码

```java

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
        http.formLogin();
    }
}

```

`http.authorizeRequests()`用于开启访问限制；`anyRequest().authenticated()`表示对所有请求都需要经过认证才能访问。

`http.formLogin()`用于开启表单登录。

## 实现原理

默认用户和密码是由`org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration`配置类生成的。

```java

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(AuthenticationManager.class)
@ConditionalOnBean(ObjectPostProcessor.class)
@ConditionalOnMissingBean(
		value = { AuthenticationManager.class, AuthenticationProvider.class, UserDetailsService.class },
		type = { "org.springframework.security.oauth2.jwt.JwtDecoder",
				"org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector" })
public class UserDetailsServiceAutoConfiguration {
    // 省略若干代码
    @Bean
	@ConditionalOnMissingBean(
			type = "org.springframework.security.oauth2.client.registration.ClientRegistrationRepository")
	@Lazy
	public InMemoryUserDetailsManager inMemoryUserDetailsManager(SecurityProperties properties,
			ObjectProvider<PasswordEncoder> passwordEncoder) {
		SecurityProperties.User user = properties.getUser();
		List<String> roles = user.getRoles();
		return new InMemoryUserDetailsManager(
				User.withUsername(user.getName()).password(getOrDeducePassword(user, passwordEncoder.getIfAvailable()))
						.roles(StringUtils.toStringArray(roles)).build());
	}
}

```

从注解上可以得知，当类路径下存在`AuthenticationManager`类型, 且Spring容器中存在`ObjectPostProcessor`实例，
但不存在`AuthenticationManager`, `AuthenticationProvider`, `UserDetailsService`实例的情况下，提供一个基于内存的`UserDetailsManager`

