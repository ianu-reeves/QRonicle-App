package com.qronicle.repository.impl;

import com.qronicle.entity.PasswordResetRequest;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.PasswordResetRequestRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetRequestRepositoryImpl implements PasswordResetRequestRepository {
    private SessionFactory sessionFactory;

    public PasswordResetRequestRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public PasswordResetRequest getRequestByCode(String code) {
        PasswordResetRequest request = null;
        Session session = sessionFactory.getCurrentSession();
        Query<PasswordResetRequest> query =
            session.createQuery("FROM PasswordResetRequest WHERE code=:code", PasswordResetRequest.class);
        query.setParameter("code", code);
        try {
            request = query.getSingleResult();
        } catch (Exception e) {
        }

        return request;
    }

    @Override
    public PasswordResetRequest getRequestByUser(User user) {
        PasswordResetRequest request = null;
        Session session = sessionFactory.getCurrentSession();
        Query<PasswordResetRequest> query =
                session.createQuery("FROM PasswordResetRequest WHERE user=:user", PasswordResetRequest.class);
        query.setParameter("user", user);
        try {
            request = query.getSingleResult();
        } catch (Exception e) {
        }

        return request;
    }

    @Override
    public void save(PasswordResetRequest request) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(request);
    }

    @Override
    public void delete(PasswordResetRequest request) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(request);
    }
}
