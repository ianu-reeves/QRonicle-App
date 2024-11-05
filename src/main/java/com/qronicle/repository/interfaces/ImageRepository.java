package com.qronicle.repository.interfaces;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;

import java.util.Set;

public interface ImageRepository {
    Image findImageById(long id);
    Set<Image> findImagesByItem(Item item);
    void save(Image image);
    void delete(Image image);
}
