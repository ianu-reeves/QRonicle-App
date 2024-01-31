package com.qronicle.service.interfaces;

import com.qronicle.entity.User;
import com.qronicle.model.ChangeEmailForm;
import com.qronicle.model.ChangePasswordForm;
import com.qronicle.model.OAuth2UserDto;
import com.qronicle.model.UserForm;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getCurrentlyAuthenticatedUser();
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User addUser(UserForm userForm);
    User addNewOAuth2User(OAuth2UserDto OAuth2UserDto);
    User changePassword(ChangePasswordForm form, String username);
    User changeEmail(ChangeEmailForm form, String username);
    void save(User user);
    void delete(User user);
}
