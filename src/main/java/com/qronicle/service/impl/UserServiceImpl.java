package com.qronicle.service.impl;

import com.qronicle.entity.Role;
import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;
import com.qronicle.enums.PrivacyStatus;
import com.qronicle.enums.UserType;
import com.qronicle.exception.InvalidCredentialsException;
import com.qronicle.model.*;
import com.qronicle.repository.interfaces.RoleRepository;
import com.qronicle.repository.interfaces.UserRepository;
import com.qronicle.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User getCurrentlyAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;

        return userRepository.findUserByUsername(auth.getName());
    }

    @Override
    @Transactional
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    @Transactional
    public User findUserById(int id) {
        return userRepository.findUserById(id);
    }

    @Override
    @Transactional
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    @Transactional
    public User findUserByProvider(String email, AccountProvider provider) {
        return userRepository.findUserByProvider(email, provider);
    }

    // Maps dto representing User to a User object & saves it
    @Override
    @Transactional
    public User addUser(UserForm userForm) {
        User user = new User(
            userForm.getUsername(),
            encoder.encode(userForm.getPassword()),
            userForm.getFirstName(),
            userForm.getLastName(),
            userForm.getEmail(),
            null,
            userForm.getPrivacyStatus(),
            userForm.getProvider(),
            userForm.getProviderId(),
            false   // Verification status false when first added
        );
        Role defaultRole = roleRepository.findRoleByName("ROLE_USER");
        user.addRole(defaultRole);
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional
    public User addNewOAuth2User(OAuth2UserDto oAuth2UserDto) {
        User user = new User();
        user.setUsername(oAuth2UserDto.getEmail());
        user.setFirstName(oAuth2UserDto.getFirstName());
        user.setLastName(oAuth2UserDto.getLastName());
        user.setEmail(oAuth2UserDto.getEmail());
        user.setAccountProvider(oAuth2UserDto.getAccountProvider());
        user.setProviderId(oAuth2UserDto.getProviderId());
        Role defaultRole = roleRepository.findRoleByName("ROLE_USER");
        user.addRole(defaultRole);
        user.setPrivacyStatus(PrivacyStatus.PRIVATE);
        user.setUserType(UserType.CASUAL);
        user.setSignupDate(LocalDate.now());

        userRepository.save(user);

        return  user;
    }

    @Override
    @Transactional
    public User changePassword(ChangePasswordForm form, String username) {
        User user = (User) loadUserByUsername(username);
        if (encoder.matches(form.getOldPassword(), user.getPassword())) {
            user.setPassword(encoder.encode(form.getNewPassword()));
        } else {
            throw new InvalidCredentialsException("Old password not valid");
        }
        userRepository.save(user);

        return user;
    }

    @Override
    @Transactional
    public User changePassword(ResetPasswordForm form, User user) {
        user.setPassword(encoder.encode(form.getNewPassword()));
        userRepository.save(user);

        return user;
    }

    @Override
    @Transactional
    public User changeEmail(ChangeEmailForm form, String username) {
        User user = (User) loadUserByUsername(username);
        user.setEmail(form.getNewEmail());
        userRepository.save(user);

        return user;
    }

    @Override
    @Transactional
    public void verifyUser(User user) {
        if (!user.getUsername().equals(user.getEmail())) {
            user.addRole(roleRepository.findRoleByName("ROLE_VERIFIED"));
        }
    }

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username);
    }
}
