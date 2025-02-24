package com.qronicle.service.interfaces;

import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;
import com.qronicle.model.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getCurrentlyAuthenticatedUser();
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByProvider(String email, AccountProvider provider);
    User addUser(UserForm userForm);
    User addNewOAuth2User(OAuth2UserDto OAuth2UserDto);
    User changePassword(ChangePasswordForm form, String username);
    User changePassword(ResetPasswordForm form, User user);
    User changeEmail(ChangeEmailForm form, String username);
    void verifyUser(User user);
    void save(User user);
    void delete(User user);
}
