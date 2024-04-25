package com.qronicle.service.impl;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.model.ItemForm;
import com.qronicle.repository.interfaces.ItemRepository;
import com.qronicle.service.interfaces.ItemService;
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
    public List<Item> getAll() {
        return itemRepository.getAll();
    }

    @Override
    @Transactional
    public Item findItemById(long id) {
        return itemRepository.findItemById(id);
    }

    @Override
    @Transactional
    public Set<Item> findItemsByTag(Tag tag) {
        return itemRepository.findItemsByTag(tag);
    }

    @Override
    @Transactional
    public Set<Item> findItemsByUser(User user) {
        return itemRepository.findItemsByUser(user);
    }

    @Override
    @Transactional
    public Item addItem(ItemForm itemForm) {
        Item item = new Item(
                itemForm.getUser(),
                itemForm.getName(),
                itemForm.getDescription(),
                itemForm.getDate(),
                itemForm.getLocation()
        );
        if (itemForm.getImages() != null) {
            itemForm.getImages().forEach(item::addImage);
        }
        if (itemForm.getTags() != null) {
            itemForm.getTags().forEach(item::addTag);
            System.out.println("TAGS: ");
            item.getTags().forEach(tag -> System.out.println(tag.getDescription()));
        }
        itemRepository.save(item);
        return item;
    }

    @Override
    @Transactional
    public void save(Item item) {
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Item item) {
        itemRepository.delete(item);
    }

}
