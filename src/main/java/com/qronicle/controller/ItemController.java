package com.qronicle.controller;

import com.qronicle.entity.Image;
import com.qronicle.entity.Item;
import com.qronicle.entity.Tag;
import com.qronicle.entity.User;
import com.qronicle.exception.FileNotFoundException;
import com.qronicle.exception.ItemNotFoundException;
import com.qronicle.exception.UserNotFoundException;
import com.qronicle.model.ItemForm;
import com.qronicle.service.interfaces.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@CrossOrigin
@RestController
@RequestMapping("${app.api.v1.prefix}/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final FileService fileService;
    private final ImageService imageService;
    private final TagService tagService;

    @Value("${app.upload.image.max}")
    private int MAX_IMAGES;

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
    public ResponseEntity<Set<Item>> getItemsByTag(@PathVariable Tag tag) {
        Set<Item> items = itemService.findItemsByTag(tag);

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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Item> addItem(
            @RequestPart("itemForm") ItemForm itemForm,
            @RequestPart(value = "files", required = false)
                @Size(max = 10, message = "You may upload a maximum of 10 images.")
                List<MultipartFile> files
    ) {
        Set<Image> images = null;
        if (files != null) {
            images =
                files.stream().map(file -> {
                    String filename = fileService.storeFile(file);
                    return new Image(filename, file.getOriginalFilename(), file.getSize());
                }).collect(Collectors.toSet());
        }
        itemForm.setImages(images);
        itemForm.setUser(userService.getCurrentlyAuthenticatedUser());
        itemForm.setDate(LocalDate.now());
        Item item = itemService.addItem(itemForm);
        if (item == null && images != null) {
            // failed to create item; delete files in s3 bucket
            for (Image image : images) {
                fileService.deleteFile(image);
            }
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    //    @PreAuthorize("authentication.name == #item.ownerName")
    @PutMapping
    public ResponseEntity<Item> updateItem(@RequestBody Item item) {
        Item oldItem = itemService.findItemById(item.getId());
        item.setId(oldItem.getId());
        item.setOwner(oldItem.getOwner());
        item.setImages(oldItem.getImages());
        itemService.save(item);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<Set<Item>> getItemsByUser(@PathVariable String username) {
        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("User with username '" + username +
                "' does not exist or could not be found.");
        }
        return ResponseEntity.ok(itemService.findItemsByUser(user));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<Set<Image>> getImagesByItem(@PathVariable long id) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + id);
        }
        Set<Image> images = imageService.findImagesByItem(item);

        return ResponseEntity.ok(images);
    }

    // POST method used because CommonsMultipartResolver does not accept PUT method
    @PostMapping(path = "/{id}/images", consumes = "multipart/form-data")
    public ResponseEntity<Item> updateItemImages(
            @RequestPart("item") Item item,
            @RequestPart("files")
            @Size(max = 10, message = "You may upload a maximum of 10 images.")
            List<MultipartFile> files
    ) {
        item.setImages(imageService.findImagesByItem(item));
        if (item.getImages().size() + files.size() > MAX_IMAGES) {
            throw new RuntimeException("Items cannot have more than " + MAX_IMAGES + " images");
        }
        Set<Image> images = new HashSet<>();
        try {
            files.forEach(file -> {
                String filename = fileService.storeFile(file);
                images.add(new Image(filename, file.getOriginalFilename(), file.getSize()));
            });
            images.forEach(item::addImage);
            itemService.save(item);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            images.forEach(fileService::deleteFile);
        }

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable long id, Authentication auth) {
        Item item = itemService.findItemById(id);
        if (item == null) {
            throw new ItemNotFoundException("No item found with id of " + id);
        }
        if (!item.getOwner().getUsername().equals(auth.getName())) {
            throw new AccessDeniedException("You do not have permission to make changes to this item");
        }
        try {
            Set<Image> images = imageService.findImagesByItem(item);
            // delete all item's images
            images.forEach(System.out::println);
            images.forEach(fileService::deleteFile);
            item.setImages(null);
            itemService.delete(item);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting item with id " + id);
        }

        return ResponseEntity.ok("Successfully deleted item with id " + id);
    }

    @DeleteMapping("/{itemId}/images/{imageId}")
    public ResponseEntity<Item> deleteImage(@PathVariable long itemId, @PathVariable long imageId, Authentication auth) {
        Item item = itemService.findItemById(itemId);
        if (item == null) {
            throw new ItemNotFoundException("No item found with ID of " + itemId);
        }
        if (!item.getOwner().getUsername().equals(auth.getName())) {
            throw new AccessDeniedException("You do not have permission to make changes to this item");
        }
        Image image = imageService.findImageById(imageId);
        if (image == null) {
            throw new FileNotFoundException("No image found with an ID of " + imageId);
        }
         try {
             item.removeImage(image);
             fileService.deleteFile(image);
             itemService.save(item);
             return ResponseEntity.ok(item);
         } catch (Exception e) {
             throw new RuntimeException("Failed to delete image with ID " + image.getId());
         }
    }
}
