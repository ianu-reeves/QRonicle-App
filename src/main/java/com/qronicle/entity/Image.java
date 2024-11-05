package com.qronicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.qronicle.serializer.ImageSerializer;

import javax.persistence.*;
import java.util.Objects;

@Entity
@JsonSerialize(using = ImageSerializer.class)
public class Image {
    public Image() {
    }

    public Image(String fileName, String originalFileName, long fileSize) {
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
    }

    public Image(long id, String fileName, String originalFileName, long fileSize) {
        this.id = id;
        this.fileName = fileName;
        this.originalFileName = originalFileName;
        this.fileSize = fileSize;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    @Column(name = "file_name")
    @JsonIgnore
    private String fileName;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "file_size")
    private long fileSize;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String filePath) {
        this.fileName = filePath;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return id == image.id && fileSize == image.fileSize && Objects.equals(fileName, image.fileName) && Objects.equals(originalFileName, image.originalFileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, originalFileName, fileSize);
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
