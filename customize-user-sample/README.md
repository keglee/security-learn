# 自定义用户

在[快速入门]()中通过在`application.yml`配置用户名和密码的方式初步了解如何应用Spring Security；但在实际应用中，通常需要从数据库中获取用户信息。

## Spring Security中UserDetails相关类

- `UserDetails`来表示用户的核心信息：权限集，密码，用户名，账户是否过期，账户是否锁定，凭证是否过期，用户是否可用。在实际开发中，需要扩展`UserDetails`来自定义存储更多的用户信息。
- `UserDetailsService`是Spring Security加载用户数据的核心接口，提供了根据用户名查找用户的方法。
- `UserDetailsManager`继承自`UserDetailsService`，并提供了管理用户的方法。

## 扩展UserDetailsService

在`UserDetailsService`接口中只声明了一个`loadUserByUsername(String username)`方法，用于根据用户名查找用户的方法，返回`UserDetails`。

```java

public class CustomizeUserDetailsService implements UserDetailsService {
    // 用于模拟从数据库查询
    private final List<String> usernameList;
    public CustomizeUserDetailsService(List<String> usernameList) {
        this.usernameList = usernameList;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!exists(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // 此处的TEST表示用户的权限, {xxx}指定密码加密的方式, {noop}表示不加密，采用明文
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

自定义`UserDetailsService`后还需要将其配置到Spring Security中，才能在用户认证时被使用，大致有以下三种方式将自定义`UserDetailsService`配置Spring Security中。

## 方式一: 重写configure(AuthenticationManagerBuilder auth)

```java

@Configuration
public class WebSecurityConfig1 extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        List<String> usernameList = Collections.singletonList("user");
        auth.userDetailsService(new CustomizeUserDetailsService(usernameList));
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

## 方式二: 往Spring容器中注入UserDetailsService实例

```java

@Configuration
public class WebSecurityConfig2 extends WebSecurityConfigurerAdapter {
    
    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        List<String> usernameList = Collections.singletonList("user");
        return new CustomizeUserDetailsService(usernameList);
    }
    
    /* 
    此处作用与userDetailsService()一样，但二者不能同时存在，否则会导致AuthenticationManager构建失败
    @Bean
    public UserDetailsService customizeUserDetailsService() {
        List<String> usernameList = Collections.singletonList("user");
        return new CustomizeUserDetailsService(usernameList);
    }*/

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

## 方式三: 使用HttpSecurity设置局部UserDetailsService

```java

@Configuration
public class WebSecurityConfig3 extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        List<String> usernameList = Collections.singletonList("user");
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .userDetailsService(new CustomizeUserDetailsService(usernameList))
                .csrf().disable();
    }
}

```

先简单对比以上三种方式：

- 方式一和方式二的作用都是为全局`AuthenticationManager`配置一个`UserDetailsService`实例，如果同时使用这两种方式，则以第一种方式为准。
Spring Security默认策略是如果`configure(AuthenticationManagerBuilder)`方法被重写，则使用传入`AuthenticationManagerBuilder`来
构建`AuthenticationManager`；否则，按类型自动构建`AuthenticationManager`。
- 方式三配置的是一个局部`AuthenticationManager`配置。如果同时使用第一种和第三种配置方式，当进行用户身份验证时，首先会通过局部的
`AuthenticationManager`对象进行验证，如果验证失败，则会调用其parent也就是全局的`AuthenticationManager`再次进行验证。