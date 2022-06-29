package com.iverson.learn.security.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 **/
@RestController
public class IndexController {
    @Value("${server.port}")
    private Integer port;
    @RequestMapping("/index")
    public String index() {
        return "index:" + port;
    }
}
