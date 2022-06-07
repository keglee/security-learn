package com.iverson.learn.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 *
 **/
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/code.jpg").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html")
                .permitAll()
                .and()
                .csrf().disable();
    }
    
    
    
    @Bean
    AuthenticationProvider kaptchaAuthenticationProvider() {
        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager(User.builder()
                .username("admin").password("{noop}123456").roles("admin").build());
        
        KaptchaAuthenticationProvider provider = new KaptchaAuthenticationProvider();
        provider.setUserDetailsService(users);
        return provider;
    }
    
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        ProviderManager manager = new ProviderManager(kaptchaAuthenticationProvider());
        return manager;
    }
}
