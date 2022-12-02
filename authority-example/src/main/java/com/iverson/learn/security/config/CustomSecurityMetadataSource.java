package com.iverson.learn.security.config;

import com.iverson.learn.security.entity.Menu;
import com.iverson.learn.security.entity.Role;
import com.iverson.learn.security.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;

/**
 * 通过当前请求地址，获取该地址需要的用户角色
 * http://vhr.javaboy.org/2020/0203/vhr-03#32-%E8%87%AA%E5%AE%9A%E4%B9%89filterinvocationsecuritymetadatasource
 **/
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    
    @Autowired
    private MenuService menuService;
    
    AntPathMatcher antPathMatcher = new AntPathMatcher();
    
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        String requestURI = ((FilterInvocation) object).getRequest().getRequestURI();
        List<Menu> allMenu = menuService.listAll();
        for (Menu menu : allMenu) {
            if (antPathMatcher.match(menu.getPattern(), requestURI) && menu.getRoles().size() > 0) {
                String[] roles = menu.getRoles().stream().map(Role::getName).toArray(String[]::new);
                return SecurityConfig.createList(roles);
            }
        }
        return SecurityConfig.createList("ROLE_LOGIN");
    }
    
    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}
