package com.qronicle.service.impl;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.TokenRepository;
import com.qronicle.service.interfaces.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

// Inspired by class written by shabbirdwd53
// Repo located at https://github.com/dailycodebuffer/Spring-MVC-Tutorials

// TODO: add functionality for checking access expiry & refreshing token
@Component
public class TokenServiceImpl implements TokenService {
    @Value("${jwt.lifetime.access}")
    private int accessTokenLifetime;

    @Value("${jwt.cookie.name.access}")
    private String accessCookieName;

    @Value("${jwt.lifetime.refresh}")
    private int refreshTokenLifetime;

    @Value("${jwt.cookie.name.refresh}")
    private String refreshCookieName;

    @Value("${jwt.secret}")
    private String key;

    private final TokenRepository tokenRepository;

    private final String REFRESH_ENDPOINT = "/auth/refresh";

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // Returns a claim from the passed token based on the function passed
    @Override
    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Analyze token signature using secret key & return all claims made in the JWT
    private Claims getAllClaimsFromToken(String token) {

        return Jwts
            .parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // Returns subject (i.e. username) present in token's claims
    @Override
    public String extractUsernameFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    // Returns expiry date of token
    @Override
    public Date extractExpirationFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

    // Convenience method to determine if token has expired.
    // Returns true if token has expired
    @Override
    public Boolean isNotExpired(String token) {
        return !extractExpirationFromToken(token).before(new Date());
    }

    private String generateToken(User user, int expiry) {
        return Jwts.builder()
            .subject(user.getUsername())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(expiry)))
            .signWith(getSigningKey())
            .compact();
    }

    // Creates a new JWT using information from passed UserDetails
    @Override
    public ResponseCookie createAccessCookie(User user) {
        String tokenValue = generateToken(user, accessTokenLifetime);
        return ResponseCookie
            .from(accessCookieName, tokenValue)
            .httpOnly(true)
            .maxAge(accessTokenLifetime)
            .sameSite("None")
            .secure(true)
            .path("/")
            .build();
    }

    @Override
    public ResponseCookie createRefreshCookie(String tokenValue) {
        return ResponseCookie
            .from(refreshCookieName, tokenValue)
            .httpOnly(true)
            .maxAge(refreshTokenLifetime)
            .sameSite("None")
            .secure(true)
            .path(REFRESH_ENDPOINT)
            .build();
    }

    @Override
    public ResponseCookie createEmptyAccessCookie() {
        return ResponseCookie
            .from(refreshCookieName, "")
            .httpOnly(true)
            .maxAge(0)
            .sameSite("None")
            .secure(true)
            .path(REFRESH_ENDPOINT)
            .build();
    }

    @Override
    public ResponseCookie createEmptyRefreshCookie() {
        return ResponseCookie
            .from(accessCookieName, "")
            .httpOnly(true)
            .maxAge(0)
            .sameSite("None")
            .secure(true)
            .path("/")
            .build();
    }

    @Override
    public RefreshToken createRefreshToken(User user, String userAgent) {
        return new RefreshToken(
            generateToken(user, refreshTokenLifetime),
            Instant.now().plusSeconds(refreshTokenLifetime),
            user,
            userAgent
        );
    }

    @Override
    public String extractAccessToken(HttpServletRequest request) {
        return extractTokenByName(request, accessCookieName);
    }

    @Override
    public String extractRefreshToken(HttpServletRequest request) {
        return extractTokenByName(request, refreshCookieName);
    }

    @Override
    @Transactional
    public RefreshToken findRefreshTokenByValue(String tokenValue) {
        return tokenRepository.getTokenByValue(tokenValue);
    }

    @Override
    @Transactional
    public void addRefreshToken(RefreshToken token) {
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void delete(RefreshToken refreshToken) {
        tokenRepository.delete(refreshToken);
    }

    @Override
    @Transactional
    public void invalidateAllUserTokens(User user) {
        tokenRepository.deleteAllForUser(user);
    }

    @Override
    @Transactional
    public void invalidateDeviceTokens(User user, String userAgent) {
        tokenRepository.deleteAllForDevice(user, userAgent);
    }

    private SecretKey getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String extractTokenByName(HttpServletRequest request, String cookieName) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        Optional<Cookie> refreshCookie = Arrays.stream(request.getCookies()).filter(cookie ->
                cookie.getName().equals(cookieName)).findFirst();
        if (refreshCookie.isPresent()) {
            token = refreshCookie.get().getValue();
        }
        return token;
    }
}
