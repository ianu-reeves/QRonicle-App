package com.qronicle.service.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;

import java.util.Set;

public interface TagService {
    Tag findTagByName(String name);
    Set<Tag> getAll();
    Set<Tag> getTagsByUser(User user);
    Set<Tag> getTagsByItem(Item item);
    Set<Tag> searchTagsByName(String name);
}
