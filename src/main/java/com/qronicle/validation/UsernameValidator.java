package com.qronicle.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    private static final String USERNAME_PATTERN = "^[a-zA-Z]([_]?[a-zA-Z0-9]){2,24}$";
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        if (username == null) {
            return true;
        }

        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }
}
