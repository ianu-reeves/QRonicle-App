package com.qronicle.service.impl;

import com.qronicle.service.interfaces.MailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender sender;

    @Value("${app.mail.sender.username}")
    private static String senderAddress;

    public MailServiceImpl(JavaMailSender sender){
        this.sender = sender;
    }

    @Override
    public void sendEmail(String to, String subj, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(to);
        message.setSubject(subj);
        message.setText(content);
        sender.send(message);
    }
}
