package com.iversonx.security_learn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 **/
@Service
public class AuthenticationProviderService implements AuthenticationProvider {
    @Autowired
    private UserService userDetailsService;
    
    @Autowired
    @Qualifier("md5PasswordEncoder")
    private PasswordEncoder encoder;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = authentication.getCredentials().toString();
        //根据用户名从数据库中获取CustomUserDetails
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if(user == null) {
            throw new BadCredentialsException("Bad");
        }
        
        if(!user.isAccountNonExpired()) {
            throw new AccountExpiredException("账号已过期");
        }
    
        if (encoder.matches(rawPassword, user.getPassword())) {
            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        } else {
            throw new BadCredentialsException("Bad credentials");
        }
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
