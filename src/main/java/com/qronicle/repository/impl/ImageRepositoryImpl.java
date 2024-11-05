package com.qronicle.repository.impl;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import com.qronicle.repository.interfaces.ImageRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class ImageRepositoryImpl implements ImageRepository {
    private final SessionFactory sessionFactory;

    public ImageRepositoryImpl(SessionFactory sessionFactory) {
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
    public Set<Image> findImagesByItem(Item item) {
        Session session = sessionFactory.getCurrentSession();
        Query<Image> query = session.createQuery("FROM Image WHERE item=:i", Image.class);
        query.setParameter("i", item);

        return new HashSet<>(query.getResultList());
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
