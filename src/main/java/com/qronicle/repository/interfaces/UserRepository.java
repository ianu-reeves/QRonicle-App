package com.qronicle.repository.interfaces;

import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;

import java.util.List;

public interface UserRepository {
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    User findUserByEmail(String email);
    User findUserByProvider(String email, AccountProvider provider);
    void save(User user);
    void delete(User user);
}
