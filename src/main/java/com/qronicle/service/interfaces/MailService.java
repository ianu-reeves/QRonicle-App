package com.qronicle.service.interfaces;

public interface MailService {
    void sendEmail(String to, String subj, String content);
}
