package com.qronicle.service.interfaces;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;

import java.util.List;

public interface TagService {
    List<Tag> getAll();
    List<Tag> getTagsByUser(User user);
    List<Tag> getTagsByItem(Item item);
}
