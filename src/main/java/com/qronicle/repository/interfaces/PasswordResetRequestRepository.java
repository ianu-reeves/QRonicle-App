package com.qronicle.repository.interfaces;

import com.qronicle.entity.PasswordResetRequest;
import com.qronicle.entity.User;

public interface PasswordResetRequestRepository {
    PasswordResetRequest getRequestByCode(String code);
    PasswordResetRequest getRequestByUser(User user);
    void save(PasswordResetRequest request);
    void delete(PasswordResetRequest request);
}
