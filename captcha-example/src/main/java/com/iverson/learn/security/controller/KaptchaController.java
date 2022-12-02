package com.iverson.learn.security.controller;

import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 **/
@Controller
public class KaptchaController {
    @Autowired
    private Producer producer;
    
    @GetMapping("/code.jpg")
    public void verifyCode(HttpServletResponse response, HttpSession session) throws IOException {
        response.setContentType("image/jpeg");
        String text = producer.createText();
        session.setAttribute("captchaCode", text);
        BufferedImage image = producer.createImage(text);
        try(ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image,"jpg", out);
        }
    }
}
