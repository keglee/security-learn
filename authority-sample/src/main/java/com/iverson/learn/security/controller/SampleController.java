package com.iverson.learn.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 **/
@RestController
public class SampleController {
    
    /**
     * 只要通过认证的就可以访问
     * @return
     */
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
    
    /**
     * 具有管理员角色才可以访问
     * @return
     */
    @GetMapping("/admin/hello")
    public String admin() {
        return "hello admin";
    }
    
    /**
     * 具有用户角色才可以访问
     * @return
     */
    @GetMapping("/user/hello")
    public String user() {
        return "hello user";
    }
    
    @GetMapping("/getinfo")
    public String getInfo() {
        return "getinfo";
    }
}
