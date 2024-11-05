package com.qronicle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("${app.api.v1.prefix}/test")
public class TestController {
    @GetMapping({"", "/"})
    public ResponseEntity<String> test() {
        String[] allowedEndpoints = {"/items", "/tags", "/files", "/users"};
        String prefix = "/api/v1/";
        for (int i = 0; i < allowedEndpoints.length; i++) {
            allowedEndpoints[i] = prefix + allowedEndpoints[i];
        }
        for (String endpoint : allowedEndpoints) {
            System.out.println(endpoint);
        }
        return ResponseEntity.ok("Authorized");
    }
}
