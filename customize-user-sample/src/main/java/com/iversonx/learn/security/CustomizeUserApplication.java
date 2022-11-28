package com.iversonx.learn.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Map;

@SpringBootApplication
public class CustomizeUserApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(CustomizeUserApplication.class, args);
        Map<String, UserDetailsService> map = context.getBeansOfType(UserDetailsService.class);
        System.out.println();
    }
}
