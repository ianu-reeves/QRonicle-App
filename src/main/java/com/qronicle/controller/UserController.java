package com.qronicle.controller;

import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.exception.UserNotFoundException;
import com.qronicle.model.ChangeEmailForm;
import com.qronicle.model.ChangePasswordForm;
import com.qronicle.service.interfaces.TagService;
import com.qronicle.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@CrossOrigin
@RequestMapping("${app.api.v1.prefix}/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;

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
    public ResponseEntity<List<Tag>> getTagsByUser(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '\"\n" +
                username + "' does not exist or could not be found");
        }
        List<Tag> tags = tagService.getTagsByUser(user);

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

    @PreAuthorize("authentication.name == #username")
    @PatchMapping("/{username}/update/email")
    public ResponseEntity<User> changeEmail(
            @RequestBody @Valid ChangeEmailForm form,
            @PathVariable String username) {
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
