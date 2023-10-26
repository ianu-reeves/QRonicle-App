package com.qronicle.service.interfaces;

import com.qronicle.entity.Image;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String storeFile(MultipartFile file);
    byte[] retrieveFile(String fileName);
    boolean deleteFile(Image image);
}
