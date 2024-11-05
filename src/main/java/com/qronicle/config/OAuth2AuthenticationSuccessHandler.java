package com.qronicle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qronicle.entity.User;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.OAuth2UserDto;
import com.qronicle.service.interfaces.CustomOAuth2UserService;
import com.qronicle.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private UserService userService;
    private CustomOAuth2UserService customOAuth2UserService;

    OAuth2AuthenticationSuccessHandler(
            @Autowired UserService userService,
            @Autowired CustomOAuth2UserService customOAuth2UserService) {
        this.userService = userService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;

        String email = customOAuth2UserService.extractEmailFromToken(token);
        User user = (User) userService.loadUserByUsername(email);
        if (user == null) {
            OAuth2UserDto oAuth2UserDto = customOAuth2UserService.convertOAuth2TokenToUserDto(token);
            try {
                user = userService.addNewOAuth2User(oAuth2UserDto);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        // TODO: figure out how to redirect to source location instead of hardcoded target
        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), new AuthResponse(user, System.currentTimeMillis()));
        response.sendRedirect("http://localhost:3000");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}