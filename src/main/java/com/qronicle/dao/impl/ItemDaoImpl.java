package com.qronicle.dao.impl;

import com.qronicle.dao.interfaces.ItemDao;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ItemDaoImpl implements ItemDao {
    private final SessionFactory sessionFactory;

    public ItemDaoImpl(SessionFactory sessionFactory) {
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
        Query<Item> query = session.createQuery("FROM Item i LEFT JOIN FETCH i.tags WHERE i.id=:iid", Item.class);
        query.setParameter("iid", id);

        try {
            item = query.getSingleResult();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return item;
    }

    @Override
    public List<Item> findItemsByTag(Tag tag) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery("FROM Item i LEFT JOIN FETCH i.tags t WHERE t=:tg", Item.class);
        query.setParameter("tg", tag);

        return query.getResultList();
    }

    @Override
    public List<Item> findItemsByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery("FROM Item i LEFT JOIN FETCH i.images LEFT JOIN FETCH i.tags WHERE i.owner=:o", Item.class);
        query.setParameter("o", user);

        return query.getResultList();
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
