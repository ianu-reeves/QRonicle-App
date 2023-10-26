package com.qronicle.service.interfaces;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;

import java.util.List;

public interface ImageService {
    Image findImageById(long id);
    List<Image> findImagesByItem(Item item);
}
