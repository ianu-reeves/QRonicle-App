package com.qronicle.service.impl;

import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;
import com.qronicle.repository.interfaces.VerificationTokenRepository;
import com.qronicle.service.interfaces.VerificationTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository repository) {
        this.verificationTokenRepository = repository;
    }

    @Override
    @Transactional
    public VerificationToken getVerificationTokenByValue(String value) {
        return verificationTokenRepository.getVerificationTokenByValue(value);
    }

    @Override
    @Transactional
    public VerificationToken getVerificationTokenByUser(User user) {
        return verificationTokenRepository.getVerificationTokenByUser(user);
    }

    @Override
    public String generateVerificationToken() {
        SecureRandom random = new SecureRandom();

        return String.format("%06X", random.nextInt(16777216));
    }

    @Override
    @Transactional
    public void save(VerificationToken verificationToken) {
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    @Transactional
    public void delete(VerificationToken verificationToken) {
        verificationTokenRepository.delete(verificationToken);
    }
}
