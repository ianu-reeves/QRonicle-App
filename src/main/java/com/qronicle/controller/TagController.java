package com.qronicle.controller;

import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.service.interfaces.ItemService;
import com.qronicle.service.interfaces.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/tags")
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
    public ResponseEntity<List<Item>> getItemsByTag(@PathVariable Tag tag) {
        List<Item> items = itemService.findItemsByTag(tag);

        return ResponseEntity.ok(items);
    }
}
