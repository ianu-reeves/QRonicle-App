package com.qronicle.repository.interfaces;

import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;

public interface VerificationTokenRepository {
    VerificationToken getVerificationTokenByValue(String value);
    VerificationToken getVerificationTokenByUser(User user);
    void save(VerificationToken verificationToken);
    void delete(VerificationToken verificationToken);
}
