package com.qronicle.controller;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.exception.ItemNotFoundException;
import com.qronicle.exception.UserNotFoundException;
import com.qronicle.model.ItemForm;
import com.qronicle.service.interfaces.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@CrossOrigin
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final FileService fileService;
    private final ImageService imageService;
    private final TagService tagService;

    public ItemController(ItemService itemService, UserService userService, FileService fileService, ImageService imageService, TagService tagService) {
        this.itemService = itemService;
        this.userService = userService;
        this.fileService = fileService;
        this.imageService = imageService;
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable long id) {
        Item item = itemService.findItemById(id);

        return ResponseEntity.ok(item);
    }

    @GetMapping("/tags/{tag}")
    public ResponseEntity<List<Item>> getItemsByTag(@PathVariable Tag tag) {
        List<Item> items = itemService.findItemsByTag(tag);

        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<List<Tag>> getTagsByItem(@PathVariable long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + id);
        }
        List<Tag> tags = tagService.getTagsByItem(item);

        return ResponseEntity.ok(tags);
    }

    @PreAuthorize("authentication.name == #item.ownerName")
    @PutMapping
    public ResponseEntity<Item> updateItem(@RequestBody Item item) {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        Item oldItem = itemService.findItemById(item.getId());
        item.setOwner(oldItem.getOwner());
        item.setTags(oldItem.getTags());
        itemService.save(item);

        return ResponseEntity.ok(item);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Item> addItem(
            @RequestPart("itemForm") ItemForm itemForm,
            @RequestPart(value = "files", required = false)
                @Size(max = 10, message = "You may upload a maximum of 10 images.")
                List<MultipartFile> files) {
        List<Image> images = null;
        if (files != null) {
            images =
                files.stream().map(file -> {
                    String filename = fileService.storeFile(file);
                    return new Image(filename, file.getOriginalFilename(), file.getSize());
                }).collect(Collectors.toList());
        }
        itemForm.setId(0);
        itemForm.setImages(images);
        itemForm.setUser(userService.getCurrentlyAuthenticatedUser());
        Item item = itemService.addItem(itemForm);
        if (item == null && images != null) {
            // failed to create item; delete files in s3 bucket
            for (Image image : images) {
                fileService.deleteFile(image);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Item>> getItemsByUser(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '" + username +
                "' does not exist or could not be found.");
        }
        return ResponseEntity.ok(user.getItems());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<Image>> getImagesByItem(@PathVariable long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + id);
        }
        List<Image> images = imageService.findImagesByItem(item);

        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + id);
        }
        try {
            List<Image> images = imageService.findImagesByItem(item);
            // delete all item's images
            // basic for-loop used to avoid Iterator errors when removing values
            for (int i = 0; i < images.size(); i++) {
                Image image = images.get(i);
                fileService.deleteFile(image);
            }
            itemService.delete(item);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting item with id " + id);
        }

        return ResponseEntity.ok("Successfully deleted item with id " + id);
    }

    //@PreAuthorize("authentication.name == #image.item.owner.username")
    @DeleteMapping("/{itemId}/images")
    public ResponseEntity<Item> deleteImage(@PathVariable long itemId, @RequestBody Image image) {
        Item item = itemService.findItemById(itemId);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + itemId);
        }
         try {
             image.setItem(null);
             item.removeImage(image);
             fileService.deleteFile(image);
             itemService.save(item);
             return ResponseEntity.ok(item);
         } catch (Exception e) {
             throw new RuntimeException("Failed to delete the image");
         }
    }
}
