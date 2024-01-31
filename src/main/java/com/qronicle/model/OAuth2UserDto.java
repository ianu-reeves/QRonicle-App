package com.qronicle.model;

import com.qronicle.enums.AccountProvider;
import com.qronicle.validation.ValidEmail;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

public class OAuth2UserDto {

    @NotNull
    private String providerId;

    @NotNull
    private AccountProvider accountProvider;

    @Max(50)
    private String firstName;

    @Max(50)
    private String lastName;

    @NotNull
    @ValidEmail
    private String email;

    public OAuth2UserDto() {
    }

    public OAuth2UserDto(String providerId, AccountProvider accountProvider, String firstName, String lastName, String email) {
        this.providerId = providerId;
        this.accountProvider = accountProvider;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public AccountProvider getAccountProvider() {
        return accountProvider;
    }

    public void setAccountProvider(AccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
