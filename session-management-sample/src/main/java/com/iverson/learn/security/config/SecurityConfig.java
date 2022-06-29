package com.iverson.learn.security.config;

import com.iverson.learn.security.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 **/
@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .csrf().disable()
                .sessionManagement()
                // 控制用户的最大会话数
                .maximumSessions(1)
                // session失效策略
                .expiredSessionStrategy(new SessionInformationExpiredStrategy() {
                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent event)
                            throws IOException, ServletException {
                        HttpServletResponse response = event.getResponse();
                        response.setContentType("application/json;charset=utf-8");
                        response.getWriter().println("当前会话已失效，请重新登录");
                        response.flushBuffer();
                    }
                })
                // 阻止用户在达到最大会话数时进行身份验证
                .maxSessionsPreventsLogin(true);
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new UserService(Arrays.asList("admin", "test")));
    }
}
