package com.qronicle.service.impl;

import com.qronicle.dao.interfaces.ItemDao;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.model.ItemForm;
import com.qronicle.service.interfaces.ItemService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDao itemDao;
    private final Environment env;

    public ItemServiceImpl(ItemDao itemDao, Environment env) {
        this.itemDao = itemDao;
        this.env = env;
    }

    @Override
    @Transactional
    public List<Item> getAll() {
        return itemDao.getAll();
    }

    @Override
    @Transactional
    public Item findItemById(long id) {
        return itemDao.findItemById(id);
    }

    @Override
    @Transactional
    public List<Item> findItemsByTag(Tag tag) {
        return itemDao.findItemsByTag(tag);
    }

    @Override
    @Transactional
    public List<Item> findItemsByUser(User user) {
        return itemDao.findItemsByUser(user);
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
        itemDao.save(item);
        return item;
    }

    @Override
    @Transactional
    public void save(Item item) {
        itemDao.save(item);
    }

    @Override
    @Transactional
    public void delete(Item item) {
        itemDao.delete(item);
    }

}
