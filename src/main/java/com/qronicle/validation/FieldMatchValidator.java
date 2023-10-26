package com.qronicle.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstField();
        secondFieldName = constraintAnnotation.secondField();
        message = constraintAnnotation.message();
    }

    // Returns true if values of both fields passed to annotation match
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        boolean valid = true;

        try {
            Object firstFieldValue = new BeanWrapperImpl(o).getPropertyValue(firstFieldName);
            Object secondFieldValue = new BeanWrapperImpl(o).getPropertyValue(secondFieldName);
            valid = firstFieldValue == null && secondFieldValue == null
                || firstFieldValue != null && firstFieldValue.equals(secondFieldValue);
        } catch (Exception e) {

        }

        if (!valid) {
            // fields do not match; create constraint violation
            constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(firstFieldName)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        }

        return valid;
    }

}
