package com.qronicle.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;
import com.qronicle.enums.AccountProvider;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.OAuth2UserDto;
import com.qronicle.service.interfaces.CustomOAuth2UserService;
import com.qronicle.service.interfaces.MailService;
import com.qronicle.service.interfaces.UserService;
import com.qronicle.service.interfaces.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    // field injection to prevent cyclical dependencies
    @Autowired
    private UserService userService;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    private final MailService mailService;

    private final VerificationTokenService verificationTokenService;

    OAuth2AuthenticationSuccessHandler(
            @Autowired MailService mailService,
            @Autowired VerificationTokenService verificationTokenService) {
        this.mailService = mailService;
        this.verificationTokenService = verificationTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = customOAuth2UserService.extractEmailFromToken(token);
        AccountProvider provider = AccountProvider.valueOf(token.getAuthorizedClientRegistrationId().toUpperCase());
        User user = userService.findUserByProvider(email, provider);
        if (user == null) {
            OAuth2UserDto oAuth2UserDto = customOAuth2UserService.convertOAuth2TokenToUserDto(token);
            try {
                user = userService.addNewOAuth2User(oAuth2UserDto);
                String verificationCode = verificationTokenService.generateVerificationToken();
                VerificationToken verificationToken = new VerificationToken(verificationCode, user);
                verificationTokenService.save(verificationToken);
                String emailBody = "Welcome to QRonicle, the digital scrapbooking app!" +
                        "\n\nBefore you can explore all the app has to offer, you will need to verify your email address." +
                        "\n\nCopy the verification code below and enter it in the app to verify your account. Your code expires in 5 minutes" +
                        "\n\nYour verification code: ";

                mailService.sendEmail(
                        user.getEmail(),
                        "QRonicle - Finish setting up your account",
                        "Hello, " + user.getFirstName() + "! " + emailBody + verificationCode
                );
            } catch (Exception e) {
                throw new RuntimeException("Error with OAuth2 signin");
            }
        }

//        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        response.setContentType("application/json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        AuthResponse authResponse = new AuthResponse(user, System.currentTimeMillis());
        String json = mapper.writeValueAsString(authResponse);
        response.getWriter().write(json);

        // TODO: extract following lines to utility method
        //  and add process whereby oauth user prompted to create username
//        String userAgent = request.getHeader("User-Agent");
//        ResponseCookie newAccessCookie = tokenService.createAccessCookie(user);
//        tokenService.invalidateDeviceTokens(user, userAgent);
//        RefreshToken newRefreshToken = tokenService.createRefreshToken(user, userAgent);
//        tokenService.addRefreshToken(newRefreshToken);
//        ResponseCookie newRefreshCookie = tokenService.createRefreshCookie(newRefreshToken.getTokenValue());
//
//        response.addHeader(HttpHeaders.SET_COOKIE, newAccessCookie.toString());
//        response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());
//        response.getHeaders(HttpHeaders.SET_COOKIE).forEach(System.out::println);
        response.sendRedirect("/auth/grantCredentials/" + user.getUsername());
//        redirectStrategy.sendRedirect(request, response, FRONTEND_URL);
    }
}