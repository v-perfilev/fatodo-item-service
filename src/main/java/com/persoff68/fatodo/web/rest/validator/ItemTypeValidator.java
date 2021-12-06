package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.model.constant.ItemType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemTypeValidator implements ConstraintValidator<ItemTypeConstraint, String> {

    @Override
    public void initialize(ItemTypeConstraint itemType) {
    }

    @Override
    public boolean isValid(String type,
                           ConstraintValidatorContext cxt) {
        if (type == null) {
            return false;
        }
        try {
            ItemType.valueOf(type);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
