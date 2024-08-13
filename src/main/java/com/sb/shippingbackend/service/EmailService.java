package com.sb.shippingbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    public void sendVerificationCode(String to, int code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);
        emailSender.send(message);
    }
    public void sendPasswordEmployee(String to, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password");
        message.setText("Your password is: " + password);
        emailSender.send(message);
    }

    public int generateVerificationCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
