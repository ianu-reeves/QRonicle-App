package com.qronicle.model;

import com.qronicle.validation.FieldMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@FieldMatch(firstField = "newPassword", secondField = "confirmNewPassword", message = "Passwords do not match")
public class ResetPasswordForm {
    public ResetPasswordForm() {
    }

    public ResetPasswordForm(String newPassword, String confirmNewPassword, String code) {
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.code = code;
    }

    @NotNull
    @Size(min = 10, max = 50)
    private String newPassword;

    @NotNull
    @Size(min = 10, max = 50)
    private String confirmNewPassword;

    @NotNull
    private String code;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
