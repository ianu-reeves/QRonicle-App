package com.qronicle.filter;

import com.qronicle.service.interfaces.UserService;
import com.qronicle.util.JWTUtil;
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
    private final JWTUtil jwtUtil;

    public JWTAuthenticationFilter(UserService userService, JWTUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain) throws ServletException, IOException {

        String token = jwtUtil.getTokenFromHeader(req);

        if (token != null) {
            String username = jwtUtil.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // username present in token, but no auth attached to SecurityContext yet.
                // get UserDetails to authenticate against
                UserDetails userDetails = userService.loadUserByUsername(username);

                if (jwtUtil.validate(token, userDetails)) {
                    // token is valid; store new authentication in SecurityContext
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }


        // allow FilterChain to move on to next filter
        chain.doFilter(req, resp);
    }
}
