package com.qronicle.dao.impl;

import com.qronicle.dao.interfaces.ImageDao;
import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImageDaoImpl implements ImageDao {
    private final SessionFactory sessionFactory;

    public ImageDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public Image findImageById(long id) {
        Session session = sessionFactory.getCurrentSession();
        Query<Image> query = session.createQuery("FROM Image WHERE id=:iid", Image.class);
        query.setParameter("iid", id);

        return query.getSingleResult();
    }

    @Override
    public List<Image> findImagesByItem(Item item) {
        Session session = sessionFactory.getCurrentSession();
        Query<Image> query = session.createQuery("FROM Image WHERE item=:i", Image.class);
        query.setParameter("i", item);

        return query.getResultList();
    }

    @Override
    public void save(Image image) {
        Session session = sessionFactory.getCurrentSession();
        session.save(image);
    }

    @Override
    public void delete(Image image) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(image);
    }
}
