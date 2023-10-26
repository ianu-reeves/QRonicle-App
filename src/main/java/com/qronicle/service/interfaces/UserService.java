package com.qronicle.service.interfaces;

import com.qronicle.model.ChangeEmailForm;
import com.qronicle.model.ChangePasswordForm;
import com.qronicle.model.UserForm;
import com.qronicle.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User getCurrentlyAuthenticatedUser();
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    User addUser(UserForm userForm);
    User changePassword(ChangePasswordForm form, String username);
    User changeEmail(ChangeEmailForm form, String username);
    void save(User user);
    void delete(User user);
}
