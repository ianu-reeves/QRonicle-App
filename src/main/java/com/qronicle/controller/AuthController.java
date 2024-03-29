package com.qronicle.controller;

import com.qronicle.entity.User;
import com.qronicle.exception.UserAlreadyExistsException;
import com.qronicle.model.AuthRequest;
import com.qronicle.model.AuthResponse;
import com.qronicle.model.UserForm;
import com.qronicle.service.interfaces.TokenService;
import com.qronicle.service.interfaces.UserService;
import com.qronicle.util.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@Controller
@CrossOrigin
@RequestMapping("/")
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final TokenService tokenService;
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(
            UserService userService,
            JWTUtil jwtUtil,
            AuthenticationManager authenticationManager,
            TokenService tokenService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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
        UserDetails newUser = userService.loadUserByUsername(addedUser.getUsername());
        String authToken = jwtUtil.generateToken(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(authToken));
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        String username = request.getUsername();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid username or password", e);
        }
        UserDetails userDetails = userService.loadUserByUsername(username);
        String token = jwtUtil.generateToken(userDetails);
        System.out.println("Generated token for " +username + " with value: " + token);

        return ResponseEntity.ok(new AuthResponse(token));
    }

    // Endpoint for authenticating with oAuth2 login.
    // Returns a ResponseEntity containing an access token authorizing use of resource server API endpoints
    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> getToken(Authentication auth) {
        logger.info("Token requested for user: " + auth.getName());
        String token = tokenService.generateToken(auth);
        logger.info("Token granted for user " + auth.getName());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @GetMapping("/secure")
    public String test(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }

    @GetMapping("/test")
    public void loginSuccess() throws ParseException {
//        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
//                authenticationToken.getAuthorizedClientRegistrationId(),
//                authenticationToken.getName());
//
//        String userEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();

//        if (!userEndpointUri.isEmpty()) {
//            RestTemplate restTemplate = new RestTemplate();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken());
//        }

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
}
