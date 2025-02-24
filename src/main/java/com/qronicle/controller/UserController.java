package com.qronicle.controller;

import com.qronicle.entity.PasswordResetRequest;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;
import com.qronicle.exception.UserNotFoundException;
import com.qronicle.model.ChangeEmailForm;
import com.qronicle.model.ChangePasswordForm;
import com.qronicle.model.ResetPasswordForm;
import com.qronicle.service.interfaces.MailService;
import com.qronicle.service.interfaces.PasswordResetRequestService;
import com.qronicle.service.interfaces.TagService;
import com.qronicle.service.interfaces.UserService;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Validated
@RestController
@CrossOrigin
@RequestMapping("${app.api.prefix.v1}/users")
public class UserController {
    private final UserService userService;
    private final TagService tagService;
    private final PasswordResetRequestService resetRequestService;
    private final MailService mailService;
    private final Environment env;

    public UserController(
            UserService userService,
            TagService tagService,
            PasswordResetRequestService resetRequestService,
            MailService mailService,
            Environment env) {
        this.userService = userService;
        this.tagService = tagService;
        this.resetRequestService = resetRequestService;
        this.mailService = mailService;
        this.env = env;
    }

    @GetMapping("/{username}")
    public User getUserById(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '"
                    + username +"' does not exist or could not be found.");
        }

        return  user;
    }

    @PreAuthorize("authentication.name == #username")
    @GetMapping("/{username}/tags")
    public ResponseEntity<Set<Tag>> getTagsByUser(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '\"\n" +
                username + "' does not exist or could not be found");
        }
        Set<Tag> tags = tagService.getTagsByUser(user);

        return ResponseEntity.ok(tags);
    }

    @PreAuthorize("authentication.name == #user.username")
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User oldUser = userService.findUserByUsername(user.getUsername());
        if (oldUser == null) {
            throw new UserNotFoundException(
                "User with username '" + user.getUsername() + "' does not exist or could not be found");
        }
        user.setId(oldUser.getId());
        // password & email cannot be updated this way; dedicated methods exist
        user.setPassword(oldUser.getPassword());
        user.setEmail(oldUser.getEmail());
        user.setProviderId(oldUser.getProviderId());
        user.setVerified(oldUser.isVerified());
        user.setRoles(oldUser.getRoles());
        userService.save(user);

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("authentication.name == #username")
    @PatchMapping("/{username}/update/password")
    public ResponseEntity<User> changePassword(
            @RequestBody @ Valid ChangePasswordForm form,
            @PathVariable String username) {

        User updatedUser = userService.changePassword(form, username);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/requestPasswordReset")
    public void requestPasswordReset(@RequestBody Map<String, String> email) {
        String emailAddress = email.get("emailAddress");
        User user = userService.findUserByEmail(emailAddress);
        if (user == null || user.getAccountProvider() != AccountProvider.LOCAL) {
            return;
        }
        String requestCode = UUID.randomUUID().toString();
        try {
            resetRequestService.save(new PasswordResetRequest(
                requestCode,
                user,
                Instant.now().plus(30, ChronoUnit.MINUTES))
            );
            String passwordResetRequestEmail = "A password reset for your account was requested. " +
                    "If you did not make this request, you can ignore this email.\n\n" +
                    "Please follow the link below to change the password for your account. This link will " +
                    "expire in 30 minutes.\n\n" +
                    env.getProperty("app.frontend.rootUrl") + "/resetPassword?code=" + requestCode + "\n\n" +
                    "If the link above says it is expired, please request another password reset.";
            mailService.sendEmail(emailAddress, "QRonicle - Password Reset Request", passwordResetRequestEmail);
        } catch (Exception e) {
        }
    }

    @GetMapping("/validatePasswordReset/{code}")
    public ResponseEntity<?> validatePasswordReset(@PathVariable String code) {
        PasswordResetRequest request = resetRequestService.getRequestByCode(code);
        if (!resetRequestService.validate(request)) {
            return ResponseEntity.status(401).body("Valid password reset request not found");
        }

        return ResponseEntity.ok(request.getUser().getEmail());
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody @Valid ResetPasswordForm resetPasswordForm) {
        PasswordResetRequest request = resetRequestService.getRequestByCode(resetPasswordForm.getCode());
        if (!resetRequestService.validate(request)) {
            return ResponseEntity.status(401).body("Valid password reset request not found");
        }
        User user = request.getUser();

        userService.changePassword(resetPasswordForm, user);
        resetRequestService.delete(request);

        return ResponseEntity.status(204).body("Password updated successfully");
    }

    @GetMapping({"/test", "/test/"})
    public String test() {
        return "yup";
    }

    @PreAuthorize("authentication.name == #username")
    @PatchMapping("/{username}/update/email")
    public ResponseEntity<User> changeEmail(
            @RequestBody @Valid ChangeEmailForm form,
            @PathVariable String username) {
        User user = (User) userService.loadUserByUsername(username);
        if (user.getAccountProvider() != AccountProvider.LOCAL) {
            throw new RuntimeException("Cannot change email address for accounts registered via third party authentication methods");
        }
        User updatedUser = userService.changeEmail(form, username);

        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN') OR authentication.name == #username")
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        User userToDelete = userService.findUserByUsername(username);
        if (userToDelete != null) {
            userService.delete(userToDelete);
        } else {
            throw new UserNotFoundException("User with username '"
                    + username + "' does not exist or could not be found");
        }

        return ResponseEntity.ok("User deleted");
    }
}
