package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.web.rest.validator.util.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateParamsValidator implements ConstraintValidator<DateParamsConstraint, DateParams> {

    @Override
    public void initialize(DateParamsConstraint dateParams) {
    }

    @Override
    public boolean isValid(DateParams dateParams,
                           ConstraintValidatorContext cxt) {
        if (dateParams == null) {
            return true;
        }
        int time = dateParams.getTime();
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = dateParams.getYear();
        int dateOffset = dateParams.getDateOffset();
        return DateUtils.isTimeValid(time)
                && DateUtils.isDateValid(date, month, year)
                && DateUtils.isDateOffsetValid(dateOffset);
    }

}
