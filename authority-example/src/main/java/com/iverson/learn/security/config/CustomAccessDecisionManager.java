package com.iverson.learn.security.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 *
 **/
public class CustomAccessDecisionManager implements AccessDecisionManager {
    
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        for(ConfigAttribute attribute : configAttributes) {
            String needRole = attribute.getAttribute();
            if("ROLE_LOGIN".equals(needRole)) {
                if(authentication instanceof AnonymousAuthenticationToken) {
                    throw new BadCredentialsException("未登录");
                }
                return;
            }
            
            // 如果需要其他角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for(GrantedAuthority authority : authorities) {
                if(authority.getAuthority().equals(needRole)) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("权限不足");
    }
    
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }
    
    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
