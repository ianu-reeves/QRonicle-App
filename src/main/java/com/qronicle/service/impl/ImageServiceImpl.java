package com.qronicle.service.impl;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import com.qronicle.repository.interfaces.ImageRepository;
import com.qronicle.service.interfaces.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ImageServiceImpl implements ImageService {
    private final ImageRepository imageRepository;

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Transactional
    public Image findImageById(long id) {
        return imageRepository.findImageById(id);
    }

    @Override
    @Transactional
    public Set<Image> findImagesByItem(Item item) {
        return imageRepository.findImagesByItem(item);
    }
}
