package com.qronicle.service.interfaces;

import com.qronicle.model.OAuth2UserDto;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface CustomOAuth2UserService {
    OAuth2UserDto convertOAuth2TokenToUserDto(OAuth2AuthenticationToken oAuth2AuthenticationToken);
    String extractEmailFromToken(OAuth2AuthenticationToken token);
}
