package com.qronicle.model;

import com.qronicle.validation.FieldMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch(firstField = "newPassword", secondField = "matchingNewPassword", message = "Passwords do not match")
public class ChangePasswordForm {
    @NotNull
    private String oldPassword;

    @NotNull
    @Size(min = 10, max = 50)
    private String newPassword;

    @NotNull
    @Size(min = 10, max = 50)
    private String matchingNewPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getMatchingNewPassword() {
        return matchingNewPassword;
    }

    public void setMatchingNewPassword(String matchingNewPassword) {
        this.matchingNewPassword = matchingNewPassword;
    }
}
