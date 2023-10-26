package com.qronicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@DynamicUpdate
public class Item {

    public Item() {
    }

    public Item(User owner, String name, String description, LocalDate date, Location location) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.ownerName = owner.getUsername();
    }

    public Item(long id, String name, String description, LocalDate date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnore
    private User owner;

    @Transient
    private String ownerName;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "date")
    private LocalDate date;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "item", orphanRemoval = true)
    @JsonIgnore
    private List<Image> images = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "item_tag",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "location_id")
    private Location location;

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

    public String getOwnerName() {
        if (ownerName == null) {
            ownerName = owner.getUsername();
        }
        return ownerName;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Image generateQRImage() {
        //TODO: Implement QR generation
        return null;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void addImage(Image image) {
        this.images.add(image);
        image.setItem(this);
    }

    public void removeImage(Image image) {
        this.images.remove(image);
        image.setItem(null);
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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id && Objects.equals(owner, item.owner) && Objects.equals(ownerName, item.ownerName) && Objects.equals(name, item.name) && Objects.equals(description, item.description) && Objects.equals(date, item.date) && Objects.equals(location, item.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, owner, ownerName, name, description, date, location);
    }
}
