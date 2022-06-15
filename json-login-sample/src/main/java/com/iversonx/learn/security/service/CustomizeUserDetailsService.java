package com.iversonx.learn.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public class CustomizeUserDetailsService implements UserDetailsService {
    private final List<String> usernameList;
    public CustomizeUserDetailsService(List<String> usernameList) {
        this.usernameList = usernameList;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!exists(username)) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(username).authorities("TEST").password("{noop}123456").build();
    }

    private boolean exists(String username) {
        boolean exist = false;
        for(String item : usernameList) {
            if(item.equals(username)) {
                exist = true;
                break;
            }
        }
        return exist;
    }
}
