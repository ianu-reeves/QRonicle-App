package com.qronicle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Tag {

    public Tag() {
        this.firstUsed = LocalDate.now();
    }

    public Tag(String description) {
        this.description = description;
        this.firstUsed = LocalDate.now();
    }

    @Id
    @Column(name = "description", unique = true)
    private String description;

    @Column(name = "first_used", updatable = false)
    private LocalDate firstUsed;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private Set<Item> items = new HashSet<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getFirstUsed() {
        return firstUsed;
    }

    public void setFirstUsed(LocalDate firstUsed) {
        this.firstUsed = firstUsed;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(description, tag.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "description='" + description + '\'' +
                ", firstUsed=" + firstUsed +
                '}';
    }
}
