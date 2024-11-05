package com.qronicle.model;

import com.qronicle.entity.Item;
import com.qronicle.entity.Location;

import java.time.LocalDate;

public class ItemDto {
    private String name;
    private String ownerName;
    private String description;
    private LocalDate date;
    private Location location;

    public ItemDto(Item item) {
        this.name = item.getName();
        this.ownerName = item.getOwner().getUsername();
        this.description = item.getDescription();
        this.date = item.getUploadDate();
        this.location = item.getLocation();
    }
}
