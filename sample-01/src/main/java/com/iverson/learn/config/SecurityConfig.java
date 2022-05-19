package com.iverson.learn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启权限配置
        http.authorizeRequests()
                // 所有的请求都要认证
                .anyRequest().authenticated()
                .and()
                // 开启表单登录
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/index")
                // 此次确保loginPage与loginProcessUrl被所有人访问，如果不配置，那么loginPage和loginProcessUrl都需要认证
                .permitAll()
                .and()
                // 关闭CSRF防御功能
                .csrf().disable();
    }
}
