package com.iverson.learn.security.service;


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

/**
 *
 **/
public class UserService implements UserDetailsService {
    private final List<String> usernameList;
    public UserService(List<String> usernameList) {
        this.usernameList = usernameList;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!usernameList.contains(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(username).authorities("TEST").password("{noop}123456").build();
    }
}
