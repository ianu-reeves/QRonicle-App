package com.qronicle.service.interfaces;

import com.qronicle.entity.PasswordResetRequest;
import com.qronicle.entity.User;

public interface PasswordResetRequestService {
    PasswordResetRequest getRequestByCode(String code);
    PasswordResetRequest getRequestByUser(User user);
    void save(PasswordResetRequest request);
    void delete(PasswordResetRequest request);
    Boolean validate(PasswordResetRequest request);
}
