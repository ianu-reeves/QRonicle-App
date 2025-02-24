package com.qronicle.repository.impl;

import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;
import com.qronicle.repository.interfaces.UserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// TODO: Test methods without left join fetch clause
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User findUserByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u LEFT JOIN FETCH u.roles WHERE lower(u.username) = lower(:uname)", User.class);
        query.setParameter("uname", username);
        try {
            user = query.getSingleResult();
        } catch (Exception e) {
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("SELECT u FROM User u", User.class);

        return query.getResultList();
    }

    @Override
    public User findUserById(int id) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u WHERE u.id=:uid", User.class);
        query.setParameter("uid", id);

        try {
            user = query.getSingleResult();
        } catch (Exception e) {
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u WHERE u.email=:uemail", User.class);
        query.setParameter("uemail", email);

        try {
            user = query.getSingleResult();
            System.out.println("Found user: " + user);
        } catch (Exception e) {
            System.out.println("Encountered exception while fetching user: " + e.getMessage());
        }
        return user;
    }

    @Override
    public User findUserByProvider(String email, AccountProvider provider) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u WHERE u.email=:email AND u.accountProvider=:provider", User.class);
        query.setParameter("email", email);
        query.setParameter("provider", provider);

        try {
            user = query.getSingleResult();
        } catch (Exception e) {

        }

        return user;
    }

    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(user);
    }

    @Override
    public void delete(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(user);
    }
}
