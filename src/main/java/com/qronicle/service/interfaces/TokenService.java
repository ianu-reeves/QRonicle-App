package com.qronicle.service.interfaces;

import org.springframework.security.core.Authentication;

public interface TokenService {
    String generateToken(Authentication authentication);
}
