package com.qronicle.controller;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.service.interfaces.ItemService;
import com.qronicle.service.interfaces.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("${app.api.v1.prefix}/tags")
public class TagController {
    private final TagService tagService;
    private final ItemService itemService;

    public TagController(TagService tagService, ItemService itemService) {
        this.tagService = tagService;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<Tag>> getAll() {
        List<Tag> tags = tagService.getAll();

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{tag}/items")
    public ResponseEntity<Set<Item>> getItemsByTag(@PathVariable Tag tag) {
        Set<Item> items = itemService.findItemsByTag(tag);

        return ResponseEntity.ok(items);
    }
}
