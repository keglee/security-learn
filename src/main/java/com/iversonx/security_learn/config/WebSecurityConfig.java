package com.iversonx.security_learn.config;

import com.iversonx.security_learn.service.AuthenticationProviderService;
import com.iversonx.security_learn.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.security.crypto.password.PasswordEncoder;


/**
 *
 **/
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    /**
     * 为HTTP请求配置基于web的安全性
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // authorizeRequests()作用：对所有基于HttpServletRequest的访问进行限制
        // anyRequest().authenticated(): 对所有请求都需要执行认证
        // formLogin(): 使用表单登录作为认证方式
        // httpBasic(): 可以使用HTTP基础认证(Basic Authentication)来完成认证
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and().formLogin();
    }
    
    
    
    @Autowired
    private AuthenticationProviderService authenticationProviderService;
    
    /**
     * 配置身份验证方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        // 在内存中配置两个账号
        builder.authenticationProvider(authenticationProviderService);
    }
    
    @Bean
    public PasswordEncoder md5PasswordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Md5Util.encode((String)rawPassword);
            }
        
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String password = Md5Util.encode((String)rawPassword);
                return encodedPassword.equals(password);
            }
        };
    }
}
