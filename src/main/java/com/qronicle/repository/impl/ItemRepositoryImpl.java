package com.qronicle.repository.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.ItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final SessionFactory sessionFactory;

    public ItemRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Item> getAll() {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery("FROM Item", Item.class);

        return query.getResultList();
    }

    @Override
    public Item findItemById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Item item = null;
        // eliminate cartesian product of query by executing two statements
//        Query<Item> query = session.createQuery("FROM Item WHERE id=:iid", Item.class);
//        query.setParameter("iid", id);
//        Query<Item> query = session.createQuery("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.images WHERE i.id=:iid", Item.class);
//        query.setParameter("iid", id);
//        query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);
//        List<Item> items = query.getResultList();
//
        Query<Item> query = session.createQuery(
            "FROM Item i LEFT JOIN FETCH i.tags LEFT JOIN FETCH i.images WHERE i.id=:iid", Item.class
        );
        query.setParameter("iid", id);

        try {
            item = query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return item;
    }

    @Override
    public Set<Item> findItemsByTag(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery("FROM Item i LEFT JOIN FETCH i.tags t WHERE t=:tg", Item.class);
        query.setParameter("tg", tag);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Item> findItemsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery(
            "FROM Item i LEFT JOIN FETCH i.images LEFT JOIN FETCH i.tags WHERE i.owner=:o", Item.class
        );
        query.setParameter("o", user);

        return new HashSet<>(query.getResultList());
    }

    @Override
    public void save(Item item) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(item);
    }

    @Override
    @Transactional
    public void delete(Item item) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(item);
    }
}
