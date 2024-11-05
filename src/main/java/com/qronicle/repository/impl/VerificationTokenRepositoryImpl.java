package com.qronicle.repository.impl;

import com.qronicle.entity.User;
import com.qronicle.entity.VerificationToken;
import com.qronicle.repository.interfaces.VerificationTokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {
    private final SessionFactory sessionFactory;

    public VerificationTokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public VerificationToken getVerificationTokenByValue(String value) {
        Session session = sessionFactory.getCurrentSession();
        Query<VerificationToken> query = session.createQuery(
            "FROM VerificationToken vt LEFT JOIN FETCH vt.user WHERE vt.token =: val",
            VerificationToken.class
        );
        query.setParameter("val", value);

        return query.getSingleResult();
    }

    @Override
    public VerificationToken getVerificationTokenByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        VerificationToken token = null;
        Query<VerificationToken> query = session.createQuery(
            "FROM VerificationToken WHERE user =: usr",
            VerificationToken.class
        );
        query.setParameter("usr", user);
        try {
            token = query.getSingleResult();
        } catch (Exception e) {
//            System.out.println(e.getMessage());
        }
        return token;
    }

    @Override
    public void save(VerificationToken verificationToken) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(verificationToken);
    }

    @Override
    public void delete(VerificationToken verificationToken) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(verificationToken);
    }
}
