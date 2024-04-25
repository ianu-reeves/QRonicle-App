package com.qronicle.service.interfaces;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;

import java.util.Set;

public interface ImageService {
    Image findImageById(long id);
    Set<Image> findImagesByItem(Item item);
}
