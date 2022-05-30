package com.iverson.learn.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

/**
 *
 **/
public class Datasource1UserDetailService implements UserDetailsService {
    private final List<String> usernameList = Arrays.asList("test","test2","test3");
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean exist = false;
        for(String item : usernameList) {
            if(item.equals(username)) {
                exist = true;
                break;
            }
        }
        
        if(!exist) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(username).authorities("TEST").password("{noop}123456").build();
    }
}
