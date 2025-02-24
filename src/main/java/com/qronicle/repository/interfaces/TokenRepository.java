package com.qronicle.repository.interfaces;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;

public interface TokenRepository {
    RefreshToken getTokenByValue(String tokenValue);
    void save(RefreshToken refreshToken);
    void delete(RefreshToken refreshToken);
    void deleteAllForUser(User user);
    void deleteAllForDevice(User user, String userAgent);
}
