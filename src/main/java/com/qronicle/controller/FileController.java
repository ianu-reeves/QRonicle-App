package com.qronicle.controller;

import com.qronicle.service.interfaces.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("${app.api.v1.prefix}/files")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(fileService.retrieveFile(filename));
    }
}
