package com.qronicle.service.interfaces;

import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;

public interface VerificationTokenService {
    VerificationToken getVerificationTokenByValue(String value);
    VerificationToken getVerificationTokenByUser(User user);
    String generateVerificationToken();
    void save(VerificationToken verificationToken);
    void delete(VerificationToken verificationToken);
}
