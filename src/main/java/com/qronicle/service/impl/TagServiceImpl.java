package com.qronicle.service.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.repository.interfaces.TagRepository;
import com.qronicle.service.interfaces.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public Tag findTagByName(String name) {
        return tagRepository.findTagByName(name);
    }

    @Override
    @Transactional
    public Set<Tag> getAll() {
        return tagRepository.getAll();
    }

    @Override
    @Transactional
    public Set<Tag> getTagsByUser(User user) {
        return tagRepository.getTagsByUser(user);
    }

    @Override
    @Transactional
    public Set<Tag> getTagsByItem(Item item) {
        return tagRepository.getTagsByItem(item);
    }

    @Override
    @Transactional
    public Set<Tag> searchTagsByName(String name) {
        return tagRepository.searchTagsByName(name);
    }
}
