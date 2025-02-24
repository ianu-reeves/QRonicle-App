package com.qronicle.controller;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;
import com.qronicle.enums.AccountProvider;
import com.qronicle.exception.InvalidVerificationCodeException;
import com.qronicle.exception.MissingRefreshTokenException;
import com.qronicle.exception.StaleRefreshTokenException;
import com.qronicle.exception.UserAlreadyExistsException;
import com.qronicle.model.AuthRequest;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.UserForm;
import com.qronicle.model.VerificationRequest;
import com.qronicle.service.interfaces.MailService;
import com.qronicle.service.interfaces.TokenService;
import com.qronicle.service.interfaces.UserService;
import com.qronicle.service.interfaces.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Instant;

@Controller
@CrossOrigin(origins = {"${app.frontend.url}"}, allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final MailService mailService;
    private final VerificationTokenService verificationTokenService;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final String VERIFICATION_EMAIL_BODY = "Welcome to QRonicle, the digital scrapbooking app!" +
        "\n\nBefore you can explore all the app has to offer, you will need to verify your email address." +
        "\n\nCopy the verification code below and enter it in the app to verify your account. Your code expires in 5 minutes" +
        "\n\nYour verification code: ";

    @Value("${app.frontend.rootUrl}")
    private String FRONTEND_URL;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            MailService mailService,
            VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
        @RequestBody @Valid UserForm userForm,
        HttpServletRequest req
    ) {
        User existingUser = (User) userService.loadUserByUsername(userForm.getUsername());
        if (existingUser != null) {
            throw new UserAlreadyExistsException("Username is not valid.");
        }
        // explicitly set ID on form to 0 in case an ID is passed with JSON request so new entry is created
        userForm.setId(0);
        // supply userForm with local provider details
        userForm.setProvider(AccountProvider.LOCAL);
        userForm.setProviderId(userForm.getUsername());

        // save user & generate verification token
        User addedUser = userService.addUser(userForm);
        String token = verificationTokenService.generateVerificationToken();
        VerificationToken verificationToken = new VerificationToken(token, addedUser);
        verificationTokenService.save(verificationToken);

        mailService.sendEmail(
            userForm.getEmail(),
            "QRonicle - Finish setting up your account",
            "Hello, " + addedUser.getFirstName() + "! " + VERIFICATION_EMAIL_BODY + token
        );
        // TODO: add mail event here

        return createAndStoreTokenCredentials(addedUser, req);
    }

    @PostMapping(value = "/verifyRegistration", consumes = "application/json")
    public ResponseEntity<?> verifyRegistration(
            @RequestBody VerificationRequest verificationRequest,
            HttpServletRequest request) {
        try {
            String accessToken = tokenService.extractAccessToken(request);
            String username = tokenService.extractUsernameFromToken(accessToken);
            User user = (User) userService.loadUserByUsername(username);

            if (username.equals(user.getEmail())) {
                if (verificationRequest.getUsername() == null) {
                    throw new RuntimeException("Accounts created with third party credentials must have a username");
                }
                User existingUser = (User) userService.loadUserByUsername(verificationRequest.getUsername());
                if (existingUser != null) {
                    throw new UserAlreadyExistsException("Username already exists");
                }
                user.setUsername(verificationRequest.getUsername());
            }
            VerificationToken verificationToken = verificationTokenService.getVerificationTokenByValue(verificationRequest.getVerificationToken());
            if (verificationToken.getExpiry().isBefore(Instant.now())
                || !verificationToken.getUser().equals(user)
            ) {
                throw new InvalidVerificationCodeException("");
            }

            userService.verifyUser(user);
            userService.save(user);
            verificationTokenService.delete(verificationToken);
            return createAndStoreTokenCredentials(user, request);
        } catch(Exception e) {
            String exceptionReason = "No further information was provided by the server";
            if (e instanceof UserAlreadyExistsException) {
                exceptionReason = "Username already exists";
            }
            if (e instanceof InvalidVerificationCodeException) {
                exceptionReason = "Verification code was malformed or expired";
            }

            return ResponseEntity.status(403).body("Verification was not successful: " + exceptionReason);
        }
    }

    @GetMapping("/reSendVerification")
    public ResponseEntity<?> reSendVerification(HttpServletRequest request) {
        String token = tokenService.extractAccessToken(request);
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        String username = tokenService.extractUsernameFromToken(token);
        User user = (User) userService.loadUserByUsername(username);
        if (user.isVerified()) {
            return ResponseEntity.status(403).body("Account already verified");
        }
        VerificationToken existingToken = verificationTokenService.getVerificationTokenByUser(user);
        if (existingToken != null) {
            // delete existing token so only one token is ever active
            verificationTokenService.delete(existingToken);
        }
        String newVerificationToken = verificationTokenService.generateVerificationToken();
        verificationTokenService.save(new VerificationToken(newVerificationToken, user));

        mailService.sendEmail(
            user.getEmail(),
            "QRonicle - Finish setting up your account",
            VERIFICATION_EMAIL_BODY + newVerificationToken
        );

        return ResponseEntity.ok().build();
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
                    user,
                    System.currentTimeMillis()
                ));
        }

        return createAndStoreTokenCredentials(user, request);
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
            tokenService.invalidateAllUserTokens(user);
            throw new StaleRefreshTokenException(STALE_REFRESH_MSG);
        }

        return createAndStoreTokenCredentials(user, request);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest req) {
        String token = tokenService.extractAccessToken(req);

        String username = tokenService.extractUsernameFromToken(token);
        User user = (User) userService.loadUserByUsername(username);
        tokenService.invalidateDeviceTokens(user, req.getHeader("User-Agent"));
        ResponseCookie emptyAccessCookie = tokenService.createEmptyAccessCookie();
        ResponseCookie emptyRefreshCookie = tokenService.createEmptyRefreshCookie();

        return ResponseEntity
                .status(204)
                .header(HttpHeaders.SET_COOKIE, emptyAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, emptyRefreshCookie.toString())
                .build();
    }


    @GetMapping("/grantCredentials/{username}")
    public void grantCredentials(HttpServletRequest request, HttpServletResponse response, @PathVariable String username) throws IOException {
        User user = userService.findUserByUsername(username);

        createAndStoreTokenCredentials(user, request);
        response.sendRedirect(FRONTEND_URL);
    }

    /**
     * Convenience method for creating access/ refresh tokens & associating them with the session. The refresh token
     * is also stored in the database.
     * @param user {@link User} to generate credentials for
     * @return {@link ResponseEntity} containing the access & refresh headers as well as the {@link AuthResponse}
     */
    private ResponseEntity<AuthResponse> createAndStoreTokenCredentials(User user, HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        ResponseCookie newAccessCookie = tokenService.createAccessCookie(user);
        tokenService.invalidateDeviceTokens(user, userAgent);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user, userAgent);
        tokenService.addRefreshToken(newRefreshToken);
        ResponseCookie newRefreshCookie = tokenService.createRefreshCookie(newRefreshToken.getTokenValue());

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, newRefreshCookie.toString())
            .body(new AuthResponse(
                user,
                System.currentTimeMillis()
            ));
    }

}
