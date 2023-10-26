package com.qronicle.dao.impl;

import com.qronicle.dao.interfaces.TagDao;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TagDaoImpl implements TagDao {
    private final SessionFactory sessionFactory;

    public TagDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Tag> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("FROM Tag", Tag.class);

        return query.getResultList();
    }

    @Override
    public List<Tag> getTagsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery(
                "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.items i WHERE i.owner=:u", Tag.class);
        query.setParameter("u", user);
        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        return query.getResultList();
    }

    @Override
    public List<Tag> getTagsByItem(Item item) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("FROM Tag t LEFT JOIN FETCH t.items i WHERE i=:it", Tag.class);
        query.setParameter("it", item);

        return query.getResultList();
    }
}
