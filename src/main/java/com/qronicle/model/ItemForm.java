package com.qronicle.model;

import com.qronicle.entity.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ItemForm {

    public ItemForm() {
    }

    public ItemForm(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.user = item.getOwner();
        this.date = item.getDate();
        this.tags = item.getTags();
        this.images = item.getImages();
        this.location = item.getLocation();
    }

    private long id;
    @NotNull
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    private User user;
    private String description;
    @NotNull
    private LocalDate date;
    @Size(max = 10)
    private Set<Tag> tags;
    @Size(max = 10)
    private List<Image> images;
    private Location location;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
