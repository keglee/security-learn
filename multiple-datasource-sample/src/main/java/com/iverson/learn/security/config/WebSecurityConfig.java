package com.iverson.learn.security.config;

import com.iverson.learn.security.service.Datasource1UserDetailService;
import com.iverson.learn.security.service.Datasource2UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 *
 **/
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .defaultSuccessUrl("/index")
                .permitAll()
                .and()
                .csrf().disable();
    }
    
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        DaoAuthenticationProvider provider1 = new DaoAuthenticationProvider();
        provider1.setUserDetailsService(datasource1UserDetailService());
    
        DaoAuthenticationProvider provider2 = new DaoAuthenticationProvider();
        provider2.setUserDetailsService(datasource2UserDetailService());
    
        return new ProviderManager(provider1, provider2);
    }
    
    @Bean
    public Datasource1UserDetailService datasource1UserDetailService() {
        return new Datasource1UserDetailService();
    }
    
    @Bean
    public Datasource2UserDetailService datasource2UserDetailService() {
        return new Datasource2UserDetailService();
    }
}
