package com.qronicle.controller;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import com.qronicle.exception.MissingRefreshTokenException;
import com.qronicle.exception.StaleRefreshTokenException;
import com.qronicle.exception.UserAlreadyExistsException;
import com.qronicle.model.AuthRequest;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.UserForm;
import com.qronicle.service.interfaces.TokenService;
import com.qronicle.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(
            UserService userService,
            TokenService tokenService,
            AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserForm userForm) {
        User existingUser = userService.findUserByUsername(userForm.getUsername());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("Username is not valid.");
        }

        // explicitly set ID on form to 0 in case an ID is passed with JSON request so new entry is created
        userForm.setId(0);
        User addedUser = userService.addUser(userForm);
        User newUser = (User) userService.loadUserByUsername(addedUser.getUsername());

        return createAndStoreTokenCredentials(newUser);
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest, HttpServletRequest request) {
        String username = authRequest.getUsername();

        // ensure credentials are valid
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }

        User user =  (User) userService.loadUserByUsername(username);
        String accessToken = tokenService.extractAccessToken(request);
        // return response without refreshing credentials if user already has valid access token
        if (accessToken != null && tokenService.isNotExpired(accessToken)) {
            return ResponseEntity
                .ok()
                .body(new AuthResponse(
                    user.getUsername(),
                    user.getAuthorities(),
                    System.currentTimeMillis()
                ));
        }

        return createAndStoreTokenCredentials(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshCredentials(HttpServletRequest request) {
        final String STALE_REFRESH_MSG = "Refresh token expired. Please log in again.";
        final String MISSING_REFRESH_MSG = "No refresh token was found in the request. Please retry or log in again if the issue persists";
        String refreshToken = tokenService.extractRefreshToken(request);
        // check token in request is valid
        if (refreshToken == null) {
            throw new MissingRefreshTokenException(MISSING_REFRESH_MSG);
        }
        RefreshToken oldToken = tokenService.findRefreshTokenByValue(refreshToken);
        if (!tokenService.isNotExpired(refreshToken)) {
            throw new StaleRefreshTokenException(STALE_REFRESH_MSG);
        }

        String username = tokenService.extractUsernameFromToken(refreshToken);
        User user = (User) userService.loadUserByUsername(username);

        // revoke all refresh tokens in DB if token being presented is not in DB (i.e. is stale)
        if (oldToken == null) {
            tokenService.invalidateUserTokens(user);
            throw new StaleRefreshTokenException(STALE_REFRESH_MSG);
        }

        ResponseEntity<AuthResponse> response = createAndStoreTokenCredentials(user);
        return response;
    }

    @GetMapping("/secure")
    public String test(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }

    @GetMapping("/test")
    public void loginSuccess() {
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authenticationToken;
        System.out.println(oAuth2AuthenticationToken.isAuthenticated());


        System.out.println("Logged in as " + SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @GetMapping("/test2")
    public ResponseEntity getOauthUser(@AuthenticationPrincipal OAuth2User user) {
        System.out.println(
                "oAuth2User attributes: " + user.getAttributes() + "\n" +
                "SecurtyContextHolder attributes: " + SecurityContextHolder.getContext().getAuthentication());
        return ResponseEntity.ok(user.getAttributes());
    }

    /**
     * Convenience method for creating access/ refresh tokens & associating them with the session. The refresh token
     * is also stored in the database.
     * @param user {@link User} to generate credentials for
     * @return {@link ResponseEntity} containing the access & refresh headers as well as the {@link AuthResponse}
     */
    private ResponseEntity<AuthResponse> createAndStoreTokenCredentials(User user) {
        ResponseCookie newAccessCookie = tokenService.createAccessCookie(user);
        tokenService.invalidateUserTokens(user);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user);
        tokenService.addRefreshToken(newRefreshToken);
        ResponseCookie newRefreshCookie = tokenService.createRefreshCookie(newRefreshToken.getTokenValue());

        ResponseEntity<AuthResponse> response = ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
            .body(new AuthResponse(
                user.getUsername(),
                user.getAuthorities(),
                System.currentTimeMillis()
            ));
        return response;
    }
}
