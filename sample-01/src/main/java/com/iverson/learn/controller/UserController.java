package com.iverson.learn.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 **/
@RestController
public class UserController {
    
    @GetMapping("/user/info")
    public String info() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object details = authentication.getDetails();
        Object principal = authentication.getPrincipal();
        return "11111";
    }
}
