package com.qronicle.dao.impl;

import com.qronicle.dao.interfaces.UserDao;
import com.qronicle.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO: Test methods without left join fetch clause
@Repository
public class UserDaoImpl implements UserDao {
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User findUserByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u LEFT JOIN FETCH u.items WHERE u.username LIKE :uname", User.class);
        query.setParameter("uname", username);
        List<User> users = query.getResultList();

        query = session.createQuery("FROM User u LEFT JOIN FETCH u.roles WHERE u IN :users", User.class);
        query.setParameter("users", users);

        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("SELECT u FROM User u LEFT JOIN FETCH u.items", User.class);

        return query.getResultList();
    }

    @Override
    public User findUserById(int id) {
        Session session = sessionFactory.getCurrentSession();
        User user = null;
        Query<User> query = session.createQuery("FROM User u LEFT JOIN FETCH u.items WHERE u.id=:uid", User.class);
        query.setParameter("uid", id);

        try {
            user = query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
