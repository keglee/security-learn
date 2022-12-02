package com.iverson.learn.security.config;

import com.iverson.learn.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 **/
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private UserService userService;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /*http.authorizeRequests()
                // 拥有ADMIN角色才可以访问
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 拥有USER角色才可以访问
                .antMatchers("/user/**").access("hasAnyRole('USER')")
                // 拥有REAL_DATA权限才可以访问
                .antMatchers("/getinfo").hasAuthority("REAL_DATA")
                // 通过认证的用户就可以访问
                .anyRequest().authenticated();*///.access("isAuthenticated()");
        
        http.authorizeRequests().anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customAccessDecisionManager());
                        object.setSecurityMetadataSource(customSecurityMetadataSource());
                        return object;
                    }
                })
                .and().formLogin().and()
                .csrf().disable()
        ;
    }
    
    @Bean
    public CustomAccessDecisionManager customAccessDecisionManager() {
        return new CustomAccessDecisionManager();
    }
    
    @Bean
    public CustomSecurityMetadataSource customSecurityMetadataSource() {
        return new CustomSecurityMetadataSource();
    }
    
    /**
     * 定义角色继承: ROLE_ADMIN继承ROLE_USER
     * @return
     */
    // @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;// .access("isAuthenticated() and @permissionExpression.check(request)") //省略其他
    }
}
