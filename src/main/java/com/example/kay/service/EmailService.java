package com.example.kay.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Welcome to Kays Book Store");

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("date", LocalDate.now().toString());

        String content = templateEngine.process("welcomeEmail", context);

        helper.setText(content, true);

        try{
            ClassPathResource banner = new ClassPathResource("static/kays.png");
            if (banner.exists()){
                helper.addInline("banner", banner);
            }}
            catch(Exception e){
                System.err.println(e.getMessage());
            }


        mailSender.send(message);
    }
}