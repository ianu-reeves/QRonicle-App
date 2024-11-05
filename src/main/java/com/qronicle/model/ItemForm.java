package com.qronicle.model;

import com.qronicle.entity.*;
import com.qronicle.enums.PrivacyStatus;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

// TODO: remove date field & default to upload date when Item is created
public class ItemForm {

    public ItemForm() {
    }

    public ItemForm(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.user = item.getOwner();
        this.date = item.getUploadDate();
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
    @Size(max = 50)
    private Set<Tag> tags;
    @Size(max = 10)
    private Set<Image> images;
    private Location location;
    @NotNull
    private PrivacyStatus privacyStatus;

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

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public PrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(PrivacyStatus privacyStatus) {
        this.privacyStatus = privacyStatus;
    }
}
