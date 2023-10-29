package com.testbni.artha.EmailTest;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private GreenMail greenMail;

    @Configuration
    static class TestConfig {
        @Bean
        public GreenMail greenMail() {
            ServerSetup serverSetup = new ServerSetup(3025, null, "smtp");
            GreenMail greenMail = new GreenMail(serverSetup);
            greenMail.start();
            return greenMail;
        }

        @Bean
        public JavaMailSenderImpl javaMailSender(GreenMail greenMail) {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            mailSender.setPort(3025);
            return mailSender;
        }
    }

    @Test
    public void testSendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("recipient@example.com");
        message.setSubject("Test Email");
        message.setText("This is a test email message.");
        javaMailSender.send(message);

    }

    @AfterEach
    public void tearDown() {
        greenMail.stop();
    }
    
}
