package com.qronicle.model;

import com.qronicle.validation.FieldMatch;
import com.qronicle.validation.ValidEmail;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch(firstField = "newEmail", secondField = "matchingNewEmail", message = "Emails do not match")
public class ChangeEmailForm {

    @NotNull
    @ValidEmail
    private String oldEmail;

    @NotNull
    @ValidEmail
    private String newEmail;

    @NotNull
    @ValidEmail
    private String matchingNewEmail;

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getMatchingNewEmail() {
        return matchingNewEmail;
    }

    public void setMatchingNewEmail(String matchingNewEmail) {
        this.matchingNewEmail = matchingNewEmail;
    }
}
