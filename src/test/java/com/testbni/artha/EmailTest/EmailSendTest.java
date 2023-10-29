package com.testbni.artha.EmailTest;

import com.icegreen.greenmail.util.GreenMail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailSendTest {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private GreenMail greenMail;

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
