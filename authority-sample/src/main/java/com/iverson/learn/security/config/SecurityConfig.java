package com.iverson.learn.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 **/
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}123").roles("ADMIN").and().withUser("test")
                .password("{noop}123").roles("USER").and().withUser("kobe").password("{noop}123").roles("guest").and()
                .withUser("lijie").password("{noop}123").authorities("REAL_DATA");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 拥有ADMIN角色才可以访问
                .antMatchers("/admin/**").hasRole("ADMIN")
                // 拥有USER角色才可以访问
                .antMatchers("/user/**").access("hasAnyRole('USER')")
                // 拥有REAL_DATA权限才可以访问
                .antMatchers("/getinfo").hasAuthority("REAL_DATA")
                // 通过认证的用户就可以访问
                .anyRequest().access("isAuthenticated()").and().formLogin().and().csrf().disable();
    }
    
    /**
     * 定义角色继承: ROLE_ADMIN继承ROLE_USER
     * @return
     */
    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;// .access("isAuthenticated() and @permissionExpression.check(request)") //省略其他
    }
}
