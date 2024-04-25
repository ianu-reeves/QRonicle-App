package com.qronicle.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {
    private final OAuth2AuthorizedClientService service;

    public OAuth2Controller(OAuth2AuthorizedClientService service) {
        this.service = service;
    }

//    @GetMapping("/code/{provider}")
//    public ResponseEntity oAuth2LoginSuccess(@PathVariable String provider, OAuth2AuthenticationToken token) {
//        OAuth2AuthorizedClient client = service.loadAuthorizedClient(
//                token.getAuthorizedClientRegistrationId(),
//                token.getName()
//        );
//
//        String email = token.getPrincipal().getAttribute("email");
//    }
}
