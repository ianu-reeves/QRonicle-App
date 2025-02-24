package com.qronicle.repository.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.enums.SortMethod;
import com.qronicle.repository.interfaces.ItemRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

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
    public Set<Item> userSearchByTermsAndTags(
        Set<Tag> tags, String searchTerm, int pageSize, int page, SortMethod sortMethod, Boolean useAnd, User user
    ) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> idQuery = session
            .createQuery(
                "SELECT DISTINCT i " +
                "FROM Item i " +
                "LEFT JOIN i.tags t " +
                "WHERE (i.owner = :user OR i.privacyStatus = 'PUBLIC') " +
                getSearchQueryString(searchTerm, tags, useAnd) +
                "ORDER BY i." + convertSortMethod(sortMethod),
                Item.class)
            .setFirstResult(pageSize * page)
            .setMaxResults(pageSize)
            .setParameter("user", user);
        if (searchTerm != null) {
            idQuery.setParameter("searchTerm", searchTerm);
        }

        if (!tags.isEmpty()) {
            idQuery.setParameter("tags", tags);
        }

        Set<Item> results = new HashSet<>(idQuery.getResultList());
        Query<Item> query = session.createQuery(
                "FROM Item i " +
                "LEFT JOIN FETCH i.tags " +
                "LEFT JOIN FETCH i.images " +
                "WHERE i IN :ids"
            , Item.class)
            .setParameter("ids", results);
        return new HashSet<>(query.getResultList());
    }

    @Override
    public Set<Item> getFullUserSearchResults(Set<Tag> tags, String searchTerm, Boolean useAnd) {
        Session session = sessionFactory.getCurrentSession();
        Query<Item> query = session.createQuery(
            "FROM Item i " +
                "LEFT JOIN FETCH i.tags t " +
                "WHERE i.privacyStatus = 'PUBLIC' " +
                getSearchQueryString(searchTerm, tags, useAnd)
            , Item.class);
        if (searchTerm != null) {
            query.setParameter("searchTerm", searchTerm);
        }
        if (!tags.isEmpty()) {
            query.setParameter("tags", tags);
        }

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
    public void delete(Item item) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(item);
    }

    private String convertSortMethod(SortMethod sortMethod) {
        String result;

        switch (sortMethod) {
            case DATE_ASC:
                result = "uploadDate ASC";
                break;
            case NAME_ASC:
                result = "name ASC";
                break;
            case NAME_DESC:
                result = "name DESC";
                break;
            default:
                return "uploadDate DESC";
        }

        return result;
    }

    private String getSearchQueryString(String searchTerm, Set<Tag> tags, Boolean useAnd) {
        String query = "AND ";
        if (searchTerm != null) {
            query += "(LOWER(i.name) LIKE CONCAT('%', LOWER(:searchTerm), '%')" +
                "OR LOWER(i.description) LIKE CONCAT('%', LOWER(:searchTerm), '%')) "
            + (
                !tags.isEmpty()
                    ? useAnd
                        ? "AND "
                        : "OR "
                    : ""
            );
        }
        if (!tags.isEmpty()) {
            query += " t in :tags ";
        }

        return query;
    }
}
