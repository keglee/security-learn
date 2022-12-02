package com.iversonx.learn.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Keg Lee
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 开启访问限制, 且所有请求都需要进行认证
        http.authorizeRequests()
                .anyRequest().authenticated();
        // 开启表单登录
        http.formLogin();
    }
}
