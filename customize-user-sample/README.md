# 案例：自定义用户

在[快速入门]()中采用了默认用户名和密码进行演示，但在实际应用中，通常需要从数据库中获取用户信息。这部分演示在Spring Security中如何自定义用户。

## Spring Security中UserDetails相关类

`UserDetails`来表示用户的核心信息：权限集，密码，用户名，账户是否过期，账户是否锁定，凭证是否过期，用户是否可用。可以通过实现扩展`UserDetails`来自定义存储更多的用户信息。

`UserDetailsService`提供了根据用户名查找用户的方法。`UserDetailsManager`继承自`UserDetailsService`，并提供了管理用户的方法。

当需要从数据库查询用户时，就需要实现`UserDetailsService`接口；如果还需要对用户进行管理，则可以实现`UserDetailsManager`接口。

```java

public class CustomizeUserDetailsService implements UserDetailsService {
    // 存储用户,用于模拟从数据库查询
    private final List<String> usernameList;
    public CustomizeUserDetailsService(List<String> usernameList) {
        this.usernameList = usernameList;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!exists(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(username).authorities("TEST").password("{noop}123456").build();
    }

    private boolean exists(String username) {
        boolean exist = false;
        for(String item : usernameList) {
            if(item.equals(username)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}

```

自定义UserDetailsService后，还需要将其配置到Spring Security中。


## 方式一: 重写configure(AuthenticationManagerBuilder auth)

```java

@Configuration
public class WebSecurityConfig1 extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new CustomizeUserDetailsService(Collections.singletonList("user")));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable();
    }
}

```

## 方式二: 注入UserDetailsService

```java

@Configuration
public class WebSecurityConfig2 extends WebSecurityConfigurerAdapter {
    
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        return new CustomizeUserDetailsService(Collections.singletonList("user"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable();
    }
}

```

## 方式三: 使用HttpSecurity设置UserDetailsService

```java

@Configuration
public class WebSecurityConfig3 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .userDetailsService(new CustomizeUserDetailsService(Collections.singletonList("user")))
                .csrf().disable();
    }
}

```

先简单对比以上三种方式：

- 方式一和方式二的作用都是为全局AuthenticationManager配置一个UserDetailsService实例，如果同时使用这两种方式，则以第一种方式为准。
Spring Security默认策略是如果`configure(AuthenticationManagerBuilder)`方法被重写，则使用传入`AuthenticationManagerBuilder`来
构建`AuthenticationManager`；否则，按类型自动构建`AuthenticationManager`。
- 方式三配置的是一个局部AuthenticationManager配置。如果同时使用第一种和第三种配置方式，当进行用户身份验证时，首先会通过局部的
`AuthenticationManager`对象进行验证，如果验证失败，则会调用其parent也就是全局的`AuthenticationManager`再次进行验证。


