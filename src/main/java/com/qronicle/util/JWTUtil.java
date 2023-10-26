package com.qronicle.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Inspired by class written by shabbirdwd53
// Repo located at https://github.com/dailycodebuffer/Spring-MVC-Tutorials

@Component
public class JWTUtil {
    private static final long TOKEN_LIFE_TIME = 2 * 60 * 60;    // valid for 2 hours after issue

    @Value("${jwt.secret}")
    private String key;

    @Value("${jwt.label}")
    private String tokenLabel;

    // Returns a claim from the passed token based on the function passed
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Analyze token signature using secret key & return all claims made in the JWT
    private Claims getAllClaimFromToken(String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    // Returns subject (i.e. username) present in token's claims
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Returns expiry date of token
    public Date getTokenExpirationDate(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Convenience method to determine if token has expired.
    // Returns true if token has expired
    public Boolean isExpired(String token) {
        return getTokenExpirationDate(token).before(new Date());
    }

    // Creates a new JWT using information from passed UserDetails
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        String sub = userDetails.getUsername();
        Date now = new Date(System.currentTimeMillis());
        Date expiry = new Date(System.currentTimeMillis() + (TOKEN_LIFE_TIME * 1000));  //current time + 5 hours in ms

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(sub)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    // Validate token by comparing the sub in the JWT & the username of the user submitting it
    // Returns true if the username and sub claim match and the token has not expired
    public Boolean validate(String token, UserDetails userDetails) {
        String tokenSub = getUsernameFromToken(token);
        String username = userDetails.getUsername();
        return (tokenSub.equals(username) && !isExpired(token));
    }

    public String getTokenFromHeader(HttpServletRequest req) {
        String token = null;
        String fullAuthString = req.getHeader("Authorization");
        if (fullAuthString != null && fullAuthString.startsWith(tokenLabel + " ")) {
            token = fullAuthString.substring(tokenLabel.length() + 1);
        }

        return token;
    }
}
