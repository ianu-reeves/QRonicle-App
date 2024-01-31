package com.qronicle.service.impl;

import com.qronicle.dao.interfaces.RoleDao;
import com.qronicle.dao.interfaces.UserDao;
import com.qronicle.entity.Role;
import com.qronicle.entity.User;
import com.qronicle.enums.PrivacyStatus;
import com.qronicle.enums.UserType;
import com.qronicle.exception.InvalidCredentialsException;
import com.qronicle.model.ChangeEmailForm;
import com.qronicle.model.ChangePasswordForm;
import com.qronicle.model.OAuth2UserDto;
import com.qronicle.model.UserForm;
import com.qronicle.security.UserPrincipal;
import com.qronicle.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final RoleDao roleDao;
    @Autowired
    private PasswordEncoder encoder;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
    }

    @Override
    @Transactional
    public User getCurrentlyAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)
            return null;

        return userDao.findUserByUsername(auth.getName());
    }

    @Override
    @Transactional
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    @Transactional
    public User findUserById(int id) {
        return userDao.findUserById(id);
    }

    @Override
    @Transactional
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    // Maps data transfer object representing User to a User object & saves it
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
            userForm.getPrivacyStatus()
        );
        Role defaultRole = roleDao.findRoleByName("ROLE_USER");
        user.addRole(defaultRole);
        userDao.save(user);
        return user;
    }

    @Override
    @Transactional
    public User addNewOAuth2User(OAuth2UserDto oAuth2UserDto) {
        User user = new User();
        user.setFirstName(oAuth2UserDto.getFirstName());
        user.setLastName(oAuth2UserDto.getLastName());
        user.setEmail(oAuth2UserDto.getEmail());
        user.setAccountProvider(oAuth2UserDto.getAccountProvider());
        user.setProviderId(oAuth2UserDto.getProviderId());
        Role defaultRole = roleDao.findRoleByName("ROLE_USER");
        user.addRole(defaultRole);
        user.setPrivacyStatus(PrivacyStatus.PRIVATE);
        user.setUserType(UserType.CASUAL);
        user.setSignupDate(LocalDate.now());

        userDao.save(user);

        return  user;
    }

    @Override
    @Transactional
    public User changePassword(ChangePasswordForm form, String username) {
        User user = findUserByUsername(username);
        if (encoder.matches(form.getOldPassword(), user.getPassword())) {
            user.setPassword(encoder.encode(form.getNewPassword()));
        } else {
            throw new InvalidCredentialsException("Old password not valid");
        }
        userDao.save(user);

        return user;
    }

    @Override
    @Transactional
    public User changeEmail(ChangeEmailForm form, String username) {
        User user = findUserByUsername(username);
        if (form.getOldEmail().equals(user.getEmail())) {
            user.setEmail(form.getNewEmail());
        } else {
            throw new InvalidCredentialsException("Old email not valid");
        }
        userDao.save(user);

        return user;
    }

    @Override
    @Transactional
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    @Transactional
    public void delete(User user) {
        userDao.delete(user);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findUserByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Invalid username or password");

        return new UserPrincipal(user);
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role ->
                new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());
    }
}
