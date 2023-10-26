package com.qronicle.dao.impl;

import com.qronicle.dao.interfaces.RoleDao;
import com.qronicle.entity.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class RoleDaoImpl implements RoleDao {
    private final SessionFactory sessionFactory;

    public RoleDaoImpl(SessionFactory sessionFactory) {
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
