package com.qronicle.model;

import com.qronicle.validation.FieldMatch;
import com.qronicle.validation.ValidEmail;

import javax.validation.constraints.NotNull;

@FieldMatch(firstField = "newEmail", secondField = "matchingNewEmail", message = "Emails do not match")
public class ChangeEmailForm {
    @NotNull
    @ValidEmail
    private String newEmail;

    @NotNull
    @ValidEmail
    private String matchingNewEmail;

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
