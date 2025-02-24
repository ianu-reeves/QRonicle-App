package com.qronicle.model;

import com.qronicle.entity.User;
import com.qronicle.enums.AccountProvider;
import com.qronicle.enums.PrivacyStatus;
import com.qronicle.validation.FieldMatch;
import com.qronicle.validation.ValidEmail;
import com.qronicle.validation.ValidUsername;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch.List({
        @FieldMatch(firstField = "password", secondField = "confirmPassword", message = "Passwords must match"),
        @FieldMatch(firstField = "email", secondField = "confirmEmail", message = "Emails must match")
})
public class UserForm {

    public UserForm() {
    }

    public UserForm(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.confirmPassword = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.confirmEmail = user.getEmail();
        this.userType = user.getUserType().toString();
        this.privacyStatus = user.getPrivacyStatus();
        this.providerId = user.getUsername();
    }

    private long id;

    @NotNull(message = "Please choose a username")
    @ValidUsername
    private String username;

    @NotNull
    @Size(min = 10, max = 50)
    private String password;

    @NotNull
    @Size(min = 10, max = 50)
    private String confirmPassword;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull
    @ValidEmail
    private String email;

    @NotNull
    @ValidEmail
    private String confirmEmail;

    //@NotNull
    //@ValidUserType
    private String userType;

    //@NotNull
    private PrivacyStatus privacyStatus;

    @NotNull
    private AccountProvider provider = AccountProvider.LOCAL;

    private String providerId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public PrivacyStatus getPrivacyStatus() {
        return privacyStatus;
    }

    public void setPrivacyStatus(PrivacyStatus privacyStatus) {
        this.privacyStatus = privacyStatus;
    }

    public AccountProvider getProvider() {
        return provider;
    }

    public void setProvider(AccountProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
