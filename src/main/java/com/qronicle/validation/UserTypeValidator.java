package com.qronicle.validation;


import com.qronicle.enums.UserType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserTypeValidator implements ConstraintValidator<ValidUserType, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = false;
        UserType userType;
        try {
            userType = UserType.valueOf(s);
            isValid = true;
        } catch (IllegalArgumentException e) {
        }

        return isValid;
    }
}
