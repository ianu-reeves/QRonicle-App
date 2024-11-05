package com.qronicle.controller;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;
import com.qronicle.enums.AccountProvider;
import com.qronicle.exception.MissingRefreshTokenException;
import com.qronicle.exception.StaleRefreshTokenException;
import com.qronicle.exception.UserAlreadyExistsException;
import com.qronicle.model.AuthRequest;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.UserForm;
import com.qronicle.service.interfaces.MailService;
import com.qronicle.service.interfaces.TokenService;
import com.qronicle.service.interfaces.UserService;
import com.qronicle.service.interfaces.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.Instant;
import java.util.Map;

@Controller
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final String VERIFICATION_EMAIL_BODY = "Welcome to QRonicle, the digital scrapbooking app!" +
        "\n\nBefore you can explore all the app has to offer, you will need to verify your email address." +
        "\n\nCopy the verification code below and enter it in the app to verify your account. Your code expires in 5 minutes" +
        "\n\nYour verification code: ";

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(
            UserService userService,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            MailService mailService,
            PasswordEncoder passwordEncoder,
            VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.mailService = mailService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody @Valid UserForm userForm) {
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
            VERIFICATION_EMAIL_BODY + token
        );
        // TODO: add mail event here

        return createAndStoreTokenCredentials(addedUser);
    }

    @PostMapping(value = "/verifyRegistration", consumes = "application/json")
    public ResponseEntity<?> verifyRegistration(@RequestBody Map<String, String> verificationCode, HttpServletRequest request) {
        System.out.println(verificationCode.get("verificationCode"));
        try {
            String accessToken = tokenService.extractAccessToken(request);
            String username = tokenService.extractUsernameFromToken(accessToken);
            User user = (User) userService.loadUserByUsername(username);
            VerificationToken verificationToken = verificationTokenService.getVerificationTokenByValue(verificationCode.get("verificationCode"));
            if (verificationToken.getExpiry().isBefore(Instant.now())
                || !verificationToken.getUser().equals(user)
            ) {
                throw new RuntimeException();
            }
            user.setVerified(true);
            userService.save(user);
            verificationTokenService.delete(verificationToken);
            return createAndStoreTokenCredentials(user);
        } catch(Exception e) {
            return ResponseEntity.status(403).body("Verification code was not valid.");
        }
    }

//    @GetMapping("/test")
//    public void test(HttpServletResponse response) throws IOException {
//        System.out.println(verificationTokenService.generateVerificationToken());
//    }

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

        return createAndStoreTokenCredentials(user);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOut(HttpServletRequest req) {
        String token = tokenService.extractAccessToken(req);

        String username = tokenService.extractUsernameFromToken(token);
        User user = (User) userService.loadUserByUsername(username);
        tokenService.invalidateUserTokens(user);
        ResponseCookie emptyAccessCookie = tokenService.createEmptyAccessCookie();
        ResponseCookie emptyRefreshCookie = tokenService.createEmptyRefreshCookie();

        return ResponseEntity
                .status(204)
                .header(HttpHeaders.SET_COOKIE, emptyAccessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, emptyRefreshCookie.toString())
                .build();
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
