package com.qronicle.service.impl;

import com.qronicle.dao.impl.ImageDaoImpl;
import com.qronicle.dao.interfaces.ImageDao;
import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import com.qronicle.service.interfaces.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageDao imageDao;

    public ImageServiceImpl(ImageDao imageDao) {
        this.imageDao = imageDao;
    }

    @Override
    @Transactional
    public Image findImageById(long id) {
        return imageDao.findImageById(id);
    }

    @Override
    @Transactional
    public List<Image> findImagesByItem(Item item) {
        return imageDao.findImagesByItem(item);
    }
}
