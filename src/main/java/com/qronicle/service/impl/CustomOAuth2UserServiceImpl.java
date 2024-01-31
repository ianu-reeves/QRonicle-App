package com.qronicle.service.impl;

import com.qronicle.enums.AccountProvider;
import com.qronicle.model.OAuth2UserDto;
import com.qronicle.service.interfaces.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CustomOAuth2UserServiceImpl implements CustomOAuth2UserService {
    private final OAuth2AuthorizedClientService clientService;

    public CustomOAuth2UserServiceImpl(@Autowired OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public OAuth2UserDto convertOAuth2TokenToUserDto(OAuth2AuthenticationToken token) {
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        );
        String providerId = token.getPrincipal().getName();
        AccountProvider provider = AccountProvider.valueOf(client.getClientRegistration().getRegistrationId().toUpperCase());
        List<String> name = extractNameFromToken(token);
        String firstName = name.get(0);
        String lastName = name.get(1);
        String email = extractEmailFromToken(token);

        return new OAuth2UserDto(providerId, provider, firstName, lastName, email);
    }

    private List<String> extractNameFromToken(OAuth2AuthenticationToken token) {
        List<String> name = new ArrayList<>();
        String providerName = token.getAuthorizedClientRegistrationId();

        if (providerName.equals("github")) {
            String principalName = token.getPrincipal().getAttribute("name");
            name.add((principalName == null) ? null : principalName.split(" ")[0]);
            name.add((principalName == null) ? null : principalName.split(" ")[1]);
        } else if (providerName.equals("google")) {
            name.add(token.getPrincipal().getAttribute("given_name"));
            name.add(token.getPrincipal().getAttribute("family_name"));
        }

        return name;
    }

    @Override
    public String extractEmailFromToken(OAuth2AuthenticationToken token) {
        String email = null;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                token.getAuthorizedClientRegistrationId(),
                token.getName()
        );

        // if github-issued token, send request to github emails endpoint
        if (token.getAuthorizedClientRegistrationId().equals("github")) {
            email = Objects.requireNonNull(
                    getGithubEmails(client.getAccessToken().getTokenValue()).getBody())[0]
                    .getEmail();
        // if google-issued token, get email from claims.
        } else if (token.getAuthorizedClientRegistrationId().equals("google")) {
            email = token.getPrincipal().getAttribute("email");
        }

        return email;
    }

    private ResponseEntity<CustomOAuth2UserServiceImpl.GithubEmail[]> getGithubEmails(String token) {
        String URL = "https://api.github.com/user/emails";
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> request = new HttpEntity<>(headers);

        return template.exchange(URL, HttpMethod.GET, request, CustomOAuth2UserServiceImpl.GithubEmail[].class);
    }

    static class GithubEmail {
        String email;
        boolean primary;
        boolean verified;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        @Override
        public String toString() {
            return "GithubEmail{" +
                    "email='" + email + '\'' +
                    ", primary=" + primary +
                    ", verified=" + verified +
                    '}';
        }
    }
}
