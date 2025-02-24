package com.qronicle.service.impl;

import com.qronicle.entity.PasswordResetRequest;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.PasswordResetRequestRepository;
import com.qronicle.service.interfaces.PasswordResetRequestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PasswordResetRequestServiceImpl implements PasswordResetRequestService {
    private PasswordResetRequestRepository repository;

    public PasswordResetRequestServiceImpl(PasswordResetRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public PasswordResetRequest getRequestByCode(String code) {
        return repository.getRequestByCode(code);
    }

    @Override
    @Transactional
    public PasswordResetRequest getRequestByUser(User user) {
        return repository.getRequestByUser(user);
    }

    @Override
    @Transactional
    public void save(PasswordResetRequest request) {
        repository.save(request);
    }

    @Override
    @Transactional
    public void delete(PasswordResetRequest request) {
        repository.delete(request);
    }

    @Override
    public Boolean validate(PasswordResetRequest request) {
        return
            request != null
            && !request.getExpiry().isBefore(Instant.now());
    }
}
