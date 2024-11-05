package com.qronicle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qronicle.exception.GenericErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Entry point for protected API resources. If no valid credentials are found in the session,
 * sends an HTTP 401: unauthorized response.
 * Client should redirect to login page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), new GenericErrorResponse(
            HttpServletResponse.SC_UNAUTHORIZED,
                "Login credentials missing or expired. Please log in again.",
            System.currentTimeMillis()
        ));
    }
}
