package com.qronicle.service.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.model.ItemForm;
import com.qronicle.repository.interfaces.ItemRepository;
import com.qronicle.service.interfaces.ItemService;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    @PostFilter(
        "filterObject.privacyStatus.toString() == 'PUBLIC'" +
        "|| filterObject.owner.username == authentication.name")
    public List<Item> getAll() {
        return itemRepository.getAll();
    }

    @Override
    @Transactional
    // TODO: secure
    public Item findItemById(long id) {
        return itemRepository.findItemById(id);
    }

    @Override
    @Transactional
    @PostFilter(
        "filterObject.privacyStatus.toString() == 'PUBLIC'" +
        "|| filterObject.owner.username == authentication.name")
    public Set<Item> findItemsByTag(Tag tag) {
        return itemRepository.findItemsByTag(tag);
    }

    @Override
    @Transactional
    @PostFilter(
        "filterObject.privacyStatus.toString() == 'PUBLIC'" +
        "|| filterObject.owner.username == authentication.name")
    public Set<Item> findItemsByUser(User user) {
        return itemRepository.findItemsByUser(user);
    }

    @Override
    @Transactional
    @PreAuthorize(
        "hasRole('USER')" +
        "&& itemForm.user.username == authentication.name")
    public Item addItem(ItemForm itemForm) {
        Item item = new Item(
                itemForm.getUser(),
                itemForm.getName(),
                itemForm.getDescription(),
                itemForm.getDate(),
                itemForm.getLocation(),
                itemForm.getPrivacyStatus()
        );
        if (itemForm.getImages() != null) {
            itemForm.getImages().forEach(item::addImage);
        }
        if (itemForm.getTags() != null) {
            itemForm.getTags().forEach(item::addTag);
        }
        itemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    @PreAuthorize("authentication.name == #item.owner.username")
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Override
    @Transactional
    @PreAuthorize("authentication.name == #item.owner.username")
    public void delete(Item item) {
        itemRepository.delete(item);
    }

}
