package com.qronicle.config;

import com.qronicle.entity.User;
import com.qronicle.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Handles the response behaviour when a user is successfully authenticated
@Component
public class LoginAuthenticationHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    // field injection rather than constructor injection used for dependencies to break dependency loop
    @Autowired
    private Environment env;
    @Autowired
    private UserService userService;

    // Redirect user to the page they attempted to access before authenticating, if applicable.
    // Otherwise, redirect to home page
    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException, ServletException {
        String username = auth.getName();
        User user = userService.findUserByUsername(username);

        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        setDefaultTargetUrl("/");   // Return to homepage if no SavedRequest attached to session
        setUseReferer(useReferer(req.getHeader("referer")));
        super.onAuthenticationSuccess(req, resp, auth);
    }

    // Helper to determine whether referer should be used.
    // Returns false if coming from login page, otherwise true
    protected Boolean useReferer(String referer) {
        String loginURL = env.getProperty("app.url.login");
        return loginURL != null && !referer.startsWith(loginURL);
    }
}
