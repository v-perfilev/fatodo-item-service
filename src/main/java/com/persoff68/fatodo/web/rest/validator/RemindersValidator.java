package com.persoff68.fatodo.web.rest.validator;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.Periodicity;
import com.persoff68.fatodo.web.rest.validator.util.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class RemindersValidator implements ConstraintValidator<RemindersConstraint, List<Reminder>> {

    @Override
    public void initialize(RemindersConstraint reminders) {
        // unimportant required method
    }

    @Override
    public boolean isValid(List<Reminder> reminderList,
                           ConstraintValidatorContext cxt) {
        if (reminderList == null) {
            return true;
        }
        for (Reminder reminder : reminderList) {
            boolean valid = isReminderValid(reminder);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    private boolean isReminderValid(Reminder reminder) {
        Periodicity periodicity = reminder.getPeriodicity();
        if (periodicity == null) {
            return false;
        }
        return switch (periodicity) {
            case ONCE -> isOnceReminderValid(reminder);
            case DAILY -> isDailyReminderValid(reminder);
            case WEEKLY -> isWeeklyReminderValid(reminder);
            case MONTHLY -> isMonthlyReminderValid(reminder);
            case YEARLY -> isYearlyReminderValid(reminder);
        };
    }

    private boolean isOnceReminderValid(Reminder reminder) {
        DateParams dateParams = reminder.getDate();
        int time = dateParams.getTime();
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = dateParams.getYear();
        return DateUtils.isTimeValid(time)
                && DateUtils.isDateValid(date, month, year)
                && DateUtils.isDateInFuture(time, date, month, year);
    }

    private boolean isDailyReminderValid(Reminder reminder) {
        DateParams dateParams = reminder.getDate();
        int time = dateParams.getTime();
        return DateUtils.isTimeValid(time);
    }

    private boolean isWeeklyReminderValid(Reminder reminder) {
        DateParams dateParams = reminder.getDate();
        int time = dateParams.getTime();
        List<Integer> weekDays = reminder.getWeekDays();
        return DateUtils.isTimeValid(time) && DateUtils.areWeekDaysValid(weekDays);
    }

    private boolean isMonthlyReminderValid(Reminder reminder) {
        DateParams dateParams = reminder.getDate();
        int time = dateParams.getTime();
        List<Integer> monthDays = reminder.getMonthDays();
        return DateUtils.isTimeValid(time) && DateUtils.areMonthDaysValid(monthDays);
    }

    private boolean isYearlyReminderValid(Reminder reminder) {
        DateParams dateParams = reminder.getDate();
        int time = dateParams.getTime();
        int date = dateParams.getDate();
        int month = dateParams.getMonth();
        int year = 2000;
        return DateUtils.isTimeValid(time) && DateUtils.isDateValid(date, month, year);
    }

}
