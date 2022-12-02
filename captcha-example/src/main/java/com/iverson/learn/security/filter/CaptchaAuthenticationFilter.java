package com.iverson.learn.security.filter;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *  图片验证码过滤器
 **/
public class CaptchaAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String inputCaptchaCode = request.getParameter("captchaCode");
        if(inputCaptchaCode == null || inputCaptchaCode.trim().isEmpty()) {
            throw new BadCredentialsException("请输入图片验证码");
        }
        
        String captchaCode = (String)request.getSession().getAttribute("captchaCode");
        
        if(captchaCode == null || captchaCode.trim().isEmpty()) {
            // 没有验证码，就直接交给UsernamePasswordAuthenticationFilter处理
            return super.attemptAuthentication(request, response);
        }
        
        if(captchaCode.equalsIgnoreCase(inputCaptchaCode)) {
            return super.attemptAuthentication(request, response);
        }
        throw new BadCredentialsException("图片验证码错误");
    }
}