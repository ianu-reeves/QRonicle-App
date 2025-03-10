package com.qronicle.service.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.enums.SortMethod;
import com.qronicle.model.ItemForm;

import java.util.List;
import java.util.Set;

public interface ItemService {
    List<Item> getAll();
    Item findItemById(long id);
    Set<Item> findItemsByTag(Tag tag);
    Set<Item> searchItemsByTermAndTags(
    Set<Tag> tags, String searchTerm, int pageSize, int page, SortMethod sortMethod, Boolean useAnd, User user);
    Set<Item> getFullSearchResults(Set<Tag> tags, String searchTerm, Boolean useAnd);
    Set<Item> findItemsByUser(User user);
    Item addItem(ItemForm itemForm);
    void save(Item item);
    void delete(Item item);
}
