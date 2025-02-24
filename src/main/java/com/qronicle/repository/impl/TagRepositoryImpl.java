package com.qronicle.repository.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.TagRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private final SessionFactory sessionFactory;

    public TagRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Tag findTagByName(String name) {
        Tag tag = null;
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("FROM Tag WHERE description=:n", Tag.class);
        query.setParameter("n", name);
        try {
            tag = query.getSingleResult();
        } catch (Exception e) {
        }

        return tag;
    }

    @Override
    public Set<Tag> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("FROM Tag", Tag.class);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Tag> getTagsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery(
                "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.items i WHERE i.owner=:u", Tag.class);
        query.setParameter("u", user);
        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Tag> getTagsByItem(Item item) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery("FROM Tag t LEFT JOIN FETCH t.items i WHERE i=:it", Tag.class);
        query.setParameter("it", item);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Tag> searchTagsByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Query<Tag> query = session.createQuery(
            "FROM Tag t WHERE LOWER(t.description) LIKE CONCAT(LOWER(:name), '%')",Tag.class);
        query.setParameter("name", name);

        return new HashSet<>(query.getResultList());
    }
}
