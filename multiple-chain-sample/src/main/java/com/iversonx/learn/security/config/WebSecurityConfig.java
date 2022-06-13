package com.iversonx.learn.security.config;

import com.iversonx.learn.security.service.CustomizeUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 **/
@Configuration
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomizeUserDetailsService userDetailsService = new CustomizeUserDetailsService(Arrays.asList("admin", "test"));
        http.antMatcher("/test/**").
                authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/test/login")
                .loginPage("/test/login.html")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                            Authentication authentication) throws IOException, ServletException {
                        response.getWriter().write("login success");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                            AuthenticationException exception) throws IOException, ServletException {
                        response.getWriter().write("login failed");
                    }
                })
                .permitAll().and().csrf().disable().userDetailsService(userDetailsService);
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        CustomizeUserDetailsService userDetailsService = new CustomizeUserDetailsService(Arrays.asList("kobe"));
        auth.userDetailsService(userDetailsService);
    }
}
