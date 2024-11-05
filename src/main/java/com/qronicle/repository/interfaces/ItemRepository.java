package com.qronicle.repository.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;

import java.util.List;
import java.util.Set;

public interface ItemRepository {
    List<Item> getAll();
    Item findItemById(long id);
    Set<Item> findItemsByTag(Tag tag);
    Set<Item> findItemsByUser(User user);
    void save(Item item);
    void delete(Item item);
}
