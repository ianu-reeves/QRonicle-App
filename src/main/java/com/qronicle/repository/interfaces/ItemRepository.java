package com.qronicle.repository.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.enums.SortMethod;

import java.util.List;
import java.util.Set;

public interface ItemRepository {
    List<Item> getAll();
    Item findItemById(long id);
    Set<Item> findItemsByTag(Tag tag);
    Set<Item> userSearchByTermsAndTags(
    Set<Tag> tags, String searchTerm, int pageSize, int page, SortMethod sortMethod, Boolean useAnd, User user);
    Set<Item> getFullUserSearchResults(Set<Tag> tags, String searchTerm, Boolean useAnd);
    Set<Item> findItemsByUser(User user);
    void save(Item item);
    void delete(Item item);
}
