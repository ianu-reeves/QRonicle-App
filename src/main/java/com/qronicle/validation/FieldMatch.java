package com.qronicle.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FieldMatchValidator.class)
@Documented
public @interface FieldMatch {
    String message() default "Fields do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String firstField();
    String secondField();

    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        FieldMatch[] value();
    }
}
