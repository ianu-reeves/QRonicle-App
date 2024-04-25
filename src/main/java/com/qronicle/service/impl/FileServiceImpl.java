package com.qronicle.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.qronicle.entity.Image;
import com.qronicle.exception.InvalidFileException;
import com.qronicle.repository.interfaces.ImageRepository;
import com.qronicle.service.interfaces.FileService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Service
public class FileServiceImpl implements FileService {
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 client;

    private final Environment env;
    private final ImageRepository imageRepository;

    public FileServiceImpl(Environment env, ImageRepository imageRepository) {
        this.env = env;
        this.imageRepository = imageRepository;
    }



    @Override
    public String storeFile(MultipartFile mp) {
        File file = convertMultipartToFile(mp);
        String filename = generateFilename(file);
        client.putObject(
                new PutObjectRequest(bucketName, filename, file));

        file.delete();
        return filename;
    }

    @Override
    public byte[] retrieveFile(String fileName) {
        S3Object s3Object = client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new InvalidFileException("Error retrieving file " + fileName);
        }
    }

    @Override
    @Transactional
    public boolean deleteFile(Image image) {
        client.deleteObject(bucketName, image.getFileName());
        imageRepository.delete(image);
        return true;
    }


    public String storeLocalFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String hashedFileName = getHashedFileName(Objects.requireNonNull(originalFileName));
        String extension = FilenameUtils.getExtension(originalFileName).toLowerCase();
        String fileName = hashedFileName + "." + extension;
        String filepath = generateFilePath(fileName);
        try {
            Files.createDirectories(Paths.get(filepath));
            file.transferTo(new File(filepath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return fileName;
    }

    public byte[] findLocalFileByName(String fileName) {
        byte[] fileData;
        String imageFolderPath = env.getProperty("app.upload.image.folder");
        String subDirectoryChain = getDirectoryChain(fileName);
        String absolutePath = imageFolderPath + "\\" + subDirectoryChain + fileName;
        try {
            fileData = Files.readAllBytes(new File(absolutePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileData;
    }

    public boolean deleteLocalFile(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    private String generateFilename(File file) {
        String filename = file.getName();
        String extension = FilenameUtils.getExtension(filename);
        String hashedFilename = getHashedFileName(filename);

        return hashedFilename + "." + extension;
    }

    // Returns file path from the root directory to the file name
    private String generateFilePath(String fileName) {
        String directoryChain = getDirectoryChain(fileName);
        return env.getProperty("app.upload.image.folder") + "\\" + directoryChain + "\\" + fileName;
    }

    // Returns a nested directory based on the name of the file
    // Intended to be used on hashed file names to generate a distributed file system
    // E.g. "test_filename.txt" hashed to "351133bac485da3eeb2b576280df426f.txt" generates directory chain of
    // 35\\11\\33 based on first 6 characters in file name
    private String getDirectoryChain(String fileName) {
        return "" + fileName.charAt(0) + fileName.charAt(1) + "\\" +
                fileName.charAt(2) + fileName.charAt(3) + "\\" +
                fileName.charAt(4) + fileName.charAt(5) + "\\";
    }

    //
    private String getHashedFileName(String fileName) {
        String hashedFileName;
        Long timestamp = System.currentTimeMillis();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(fileName.getBytes());
            md.update(timestamp.byteValue());
            byte[] digest = md.digest();
            hashedFileName = DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return hashedFileName;
    }

    // TODO: Check file is image
    private File convertMultipartToFile(MultipartFile file) {
        File output = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream outputStream = new FileOutputStream(output)){
            outputStream.write(file.getBytes());
        } catch (IOException e) {
            throw new InvalidFileException("Error converting MultipartFile to File");
        }

        return output;
    }
}
