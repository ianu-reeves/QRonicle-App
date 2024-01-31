package com.qronicle.dao.interfaces;

import com.qronicle.entity.User;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;

import java.util.List;

public interface UserDao {
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByOAuth2UserRequest(OAuth2UserRequest request);
    void save(User user);
    void delete(User user);
}
