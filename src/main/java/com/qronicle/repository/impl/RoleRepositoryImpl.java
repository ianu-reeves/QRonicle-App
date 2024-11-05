package com.qronicle.repository.impl;

import com.qronicle.entity.Role;
import com.qronicle.repository.interfaces.RoleRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class RoleRepositoryImpl implements RoleRepository {
    private final SessionFactory sessionFactory;

    public RoleRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Role findRoleByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Role> query = session.createQuery("FROM Role WHERE name=:rname", Role.class);
        query.setParameter("rname", name);
        return query.getSingleResult();
    }
}
