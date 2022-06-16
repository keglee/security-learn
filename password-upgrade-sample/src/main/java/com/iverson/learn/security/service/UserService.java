package com.iverson.learn.security.service;

import com.iverson.learn.security.entity.User;
import com.iverson.learn.security.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 **/
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userMapper.loadByUsername(username);
    }
    
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User u = (User)user;
        userMapper.updatePassword(u.getId(), newPassword);
        ((User) user).setPassword(newPassword);
        return user;
    }
}
