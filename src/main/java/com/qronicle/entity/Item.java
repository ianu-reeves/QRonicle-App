package com.qronicle.entity;

import com.qronicle.enums.PrivacyStatus;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@DynamicUpdate
public class Item {

    public Item() {
    }

    public Item(User owner, String name, String description, LocalDate uploadDate, Location location, PrivacyStatus privacyStatus) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.uploadDate = uploadDate;
        this.location = location;
        this.privacyStatus = privacyStatus != null ? privacyStatus : PrivacyStatus.PUBLIC;
    }

    public Item(long id, String name, String description, LocalDate uploadDate, PrivacyStatus privacyStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.uploadDate = uploadDate;
        this.privacyStatus = privacyStatus != null ? privacyStatus : PrivacyStatus.PUBLIC;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "upload_date", updatable = false)
    private LocalDate uploadDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "item", orphanRemoval = true)
    private Set<Image> images = new HashSet<>();
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST,
        }
    )
    @Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinTable(
            name = "item_tag",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "privacy_status")
    private PrivacyStatus privacyStatus;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate date) {
        this.uploadDate = date;
    }

    public Image generateQRImage() {
        //TODO: Implement QR generation
        return null;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        image.setItem(this);
        this.images.add(image);
    }

    public void removeImage(Image image) {
        image.setItem(null);
        this.images.remove(image);
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getItems().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getItems().remove(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                '}';
    }
}
