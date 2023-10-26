package com.qronicle.dao.interfaces;

import com.qronicle.entity.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();
    User findUserById(int id);
    User findUserByUsername(String username);
    void save(User user);
    void delete(User user);
}
