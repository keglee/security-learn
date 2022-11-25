# 简单示例

这是一个简单的Spring Security示例，主要功能：开启访问限制，并且所有请求都需要进行认证；同时开启了表单登录，以及在`application.yml`中配置了用户名和密码。

如果不配置用户名和密码，则默认的用户名为user，在启动服务时自动生成默认密码并输出到日志

## 代码讲解

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

