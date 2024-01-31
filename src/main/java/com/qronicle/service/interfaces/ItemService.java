package com.qronicle.service.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.model.ItemForm;

import java.util.List;

public interface ItemService {
    List<Item> getAll();
    Item findItemById(long id);
    List<Item> findItemsByTag(Tag tag);
    List<Item> findItemsByUser(User user);
    Item addItem(ItemForm itemForm);
    void save(Item item);
    void delete(Item item);
}
