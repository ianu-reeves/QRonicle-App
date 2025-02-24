package com.qronicle.service.interfaces;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.function.Function;

public interface TokenService {
    <T> T extractClaimFromToken(String token, Function<Claims, T> claimsTFunction);
    String extractUsernameFromToken(String token);
    Date extractExpirationFromToken(String token);
    Boolean isNotExpired(String token);
    ResponseCookie createAccessCookie(User user);
    ResponseCookie createRefreshCookie(String tokenValue);
    ResponseCookie createEmptyAccessCookie();
    ResponseCookie createEmptyRefreshCookie();
    RefreshToken createRefreshToken(User user, String userAgent);
    String extractAccessToken(HttpServletRequest request);
    String extractRefreshToken(HttpServletRequest request);
    RefreshToken findRefreshTokenByValue(String tokenValue);
    void addRefreshToken(RefreshToken refreshToken);
    void delete(RefreshToken refreshToken);
    void invalidateAllUserTokens(User user);
    void invalidateDeviceTokens(User user, String userAgent);
}
