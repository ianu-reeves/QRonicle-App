package com.qronicle.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuth2Controller {
    private final OAuth2AuthorizedClientService service;

    public OAuth2Controller(OAuth2AuthorizedClientService service) {
        this.service = service;
    }
}
