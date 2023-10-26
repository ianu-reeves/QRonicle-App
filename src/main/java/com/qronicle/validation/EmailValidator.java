package com.qronicle.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Returns true if valid email has been entered or email is null.
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {
    // regex of valid email pattern. e.g. something@mail.net; something@mail.co.uk; some.th_ing+el_se@mail.co.uk
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9]+(\\.[A-Za-z0-9-]+|_[A-Za-z0-9-]+)*(\\+[A-Za-z0-9]+)?" +
            "@[A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        if (email == null) {
            return true;
        }
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
}
