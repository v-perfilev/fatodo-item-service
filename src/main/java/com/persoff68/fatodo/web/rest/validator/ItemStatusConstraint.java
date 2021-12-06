package com.persoff68.fatodo.web.rest.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ItemStatusValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ItemStatusConstraint {
    String message() default "Invalid item status";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
