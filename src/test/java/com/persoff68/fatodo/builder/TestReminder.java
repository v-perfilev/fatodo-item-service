package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.Builder;

import java.util.List;

public class TestReminder extends Reminder {

    @Builder
    TestReminder(Periodicity periodicity,
                 DateParams date,
                 List<Integer> weekDays,
                 List<Integer> monthDays) {
        super(periodicity, date, weekDays, monthDays);
    }

    public static TestReminderBuilder defaultBuilder() {
        DateParams dateParams = TestDateParams.defaultBuilder().build().toParent();
        return TestReminder.builder()
                .periodicity(Periodicity.ONCE)
                .date(dateParams);
    }

    public Reminder toParent() {
        Reminder reminder = new Reminder();
        reminder.setPeriodicity(getPeriodicity());
        reminder.setDate(getDate());
        reminder.setWeekDays(getWeekDays());
        reminder.setMonthDays(getMonthDays());
        return reminder;
    }

}
