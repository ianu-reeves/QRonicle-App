package com.qronicle.service.impl;

import com.qronicle.dao.interfaces.TagDao;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.service.interfaces.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    @Transactional
    public List<Tag> getAll() {
        return tagDao.getAll();
    }

    @Override
    @Transactional
    public List<Tag> getTagsByUser(User user) {
        return tagDao.getTagsByUser(user);
    }

    @Override
    @Transactional
    public List<Tag> getTagsByItem(Item item) {
        return tagDao.getTagsByItem(item);
    }
}
