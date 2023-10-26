package com.qronicle.dao.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;

import java.util.List;

public interface ItemDao {
    List<Item> getAll();
    Item findItemById(long id);
    List<Item> findItemsByTag(Tag tag);
    List<Item> findItemsByUser(User user);
    void save(Item item);
    void delete(Item item);
}
