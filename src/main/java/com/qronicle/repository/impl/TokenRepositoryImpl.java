package com.qronicle.repository.impl;

import com.qronicle.entity.RefreshToken;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.TokenRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepositoryImpl implements TokenRepository {
    private final SessionFactory sessionFactory;

    public TokenRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public RefreshToken getTokenByValue(String tokenValue) {
        Session session = sessionFactory.getCurrentSession();
        Query<RefreshToken> query = session.createQuery("FROM refresh_token WHERE tokenValue=:val", RefreshToken.class);
        query.setParameter("val", tokenValue);
        return query.getSingleResult();
    }

    @Override
    public void save(RefreshToken refreshToken) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(refreshToken);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(refreshToken);
    }

    @Override
    public void deleteAllForUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        session
            .createQuery("DELETE FROM refresh_token WHERE sub=:user")
            .setParameter("user", user)
            .executeUpdate();

    }

    @Override
    public void deleteAllForDevice(User user, String userAgent) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM refresh_token WHERE userAgent=:userAgent AND sub=:user")
            .setParameter("userAgent", userAgent)
            .setParameter("user", user)
            .executeUpdate();
    }
}
