package com.qronicle.service.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.TagRepository;
import com.qronicle.service.interfaces.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public List<Tag> getAll() {
        return tagRepository.getAll();
    }

    @Override
    @Transactional
    public List<Tag> getTagsByUser(User user) {
        return tagRepository.getTagsByUser(user);
    }

    @Override
    @Transactional
    public List<Tag> getTagsByItem(Item item) {
        return tagRepository.getTagsByItem(item);
    }
}
