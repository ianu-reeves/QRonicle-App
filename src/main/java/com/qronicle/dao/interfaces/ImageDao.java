package com.qronicle.dao.interfaces;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;

import java.util.List;

public interface ImageDao {
    Image findImageById(long id);
    List<Image> findImagesByItem(Item item);
    void save(Image image);
    void delete(Image image);
}
