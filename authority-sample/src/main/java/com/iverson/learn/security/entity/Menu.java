package com.iverson.learn.security.entity;

import java.util.List;

/**
 *
 **/
public class Menu {
    private Long id;
    private String pattern;
    
    /**
     * 哪些角色可以访问
     */
    private List<Role> roles;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public List<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
