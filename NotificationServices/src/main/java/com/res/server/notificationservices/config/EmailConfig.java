package com.res.server.notificationservices.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Bean
    JavaMailSenderImpl getEmailSender() {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.gmail.com");

        javaMailSender.setPort(587);

        javaMailSender.setUsername("harshitesting07@gmail.com");

        javaMailSender.setPassword("vbfecugljpgyoted");

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.debug", true);
        properties.put("mail.smtp.starttls.enable", true);
        return javaMailSender;
    }

    @Bean
    SimpleMailMessage getMailMessage() {
        return new SimpleMailMessage();
    }



}