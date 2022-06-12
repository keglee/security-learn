package com.iversonx.learn.security.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

public class CustomUserDetailService implements UserDetailsService {
    private List<String> usernameList = Arrays.asList("admin", "test", "user");
    @Override
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
