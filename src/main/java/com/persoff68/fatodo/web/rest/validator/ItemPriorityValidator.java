package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.model.constant.ItemPriority;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ItemPriorityValidator implements ConstraintValidator<ItemPriorityConstraint, String> {

    @Override
    public void initialize(ItemPriorityConstraint itemPriority) {
    }

    @Override
    public boolean isValid(String priority,
                           ConstraintValidatorContext cxt) {
        if (priority == null) {
            return false;
        }
        try {
            ItemPriority.valueOf(priority);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
