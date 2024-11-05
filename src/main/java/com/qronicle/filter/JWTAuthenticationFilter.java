package com.qronicle.filter;

import com.qronicle.service.interfaces.TokenService;
import com.qronicle.service.interfaces.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Filter to intercept incoming authentication requests in order to validate an existing or provide a new JWT
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final TokenService tokenService;

    public JWTAuthenticationFilter(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain) throws ServletException, IOException {
        String accessToken = tokenService.extractAccessToken(req);
        if (accessToken != null) {
            String username = tokenService.extractUsernameFromToken(accessToken);
            if (username != null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                if (tokenService.isNotExpired(accessToken)) {
                    // access token is still valid; store authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        chain.doFilter(req, resp);
    }
}
