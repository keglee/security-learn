package com.iversonx.learn.security.config;

import com.iversonx.learn.security.service.CustomizeUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /*@Bean
    public CustomizeUserDetailsService customizeUserDetailsService() {
        return new CustomizeUserDetailsService(Collections.singletonList("admin"));
    }*/

   /* @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new CustomizeUserDetailsService(Collections.singletonList("user"));
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new CustomizeUserDetailsService(Collections.singletonList("admin")));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomizeUserDetailsService userDetailsService = new CustomizeUserDetailsService(Collections.singletonList("test"));
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.getWriter().write("login success");
                    }
                })
                .and().userDetailsService(userDetailsService)
                .csrf().disable();
    }
}
