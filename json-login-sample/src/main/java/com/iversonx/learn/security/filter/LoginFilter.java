package com.iversonx.learn.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 *
 **/
public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    
    public LoginFilter() {
        super(new AntPathRequestMatcher("/login2", "POST"));
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String method = request.getMethod();
        if(!HttpMethod.POST.matches(method)) {
            throw new AuthenticationServiceException("Authentication method not supported :" + method);
        }
        
        String contentType = request.getContentType();
        if(!contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("Authentication contentType not supported :" + contentType);
        }
    
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> loginInfo = objectMapper.readValue(request.getInputStream(), Map.class);
            String username = loginInfo.get("username");
            String password = loginInfo.get("password");
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication failed :" + e.getMessage());
        }
    }
}
