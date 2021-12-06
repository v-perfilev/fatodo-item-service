package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.model.constant.ItemStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemStatusValidator implements ConstraintValidator<ItemStatusConstraint, String> {

    @Override
    public void initialize(ItemStatusConstraint itemArchived) {
    }

    @Override
    public boolean isValid(String status,
                           ConstraintValidatorContext cxt) {
        if (status == null) {
            return false;
        }
        try {
            ItemStatus.valueOf(status);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
