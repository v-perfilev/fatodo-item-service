package com.persoff68.fatodo.web.rest.validator.util;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class DateUtils {
    private static final int MINUTES_IN_HOUR = 60;
    private static final int MINUTES_IN_DAY = 60 * 24;

    private DateUtils() {
    }

    public static int getHours(int time) {
        return time / MINUTES_IN_HOUR;
    }

    public static int getMinutes(int time) {
        return time % MINUTES_IN_HOUR;
    }

    public static boolean isTimeValid(int time) {
        return time >= 0 && time < MINUTES_IN_DAY;
    }

    public static boolean isDateValid(int date, int month, int year) {
        if (year < 0 || year > 2100) {
            return false;
        }
        if (month < 0 || month > 11) {
            return false;
        }
        int lengthOfMonth = YearMonth.of(year, month + 1).lengthOfMonth();
        return date >= 1 || date <= lengthOfMonth;
    }

    public static boolean isTimezoneValid(String timezone) {
        return Set.of(TimeZone.getAvailableIDs()).contains(timezone);
    }

    public static boolean isDateInFuture(int time, int date, int month, int year) {
        int hours = getHours(time);
        int minutes = getMinutes(time);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hours, minutes);
        Instant reminderDate = calendar.toInstant();
        Instant now = Instant.now();
        return reminderDate.compareTo(now) > 0;
    }

    public static boolean areWeekDaysValid(List<Integer> weekDayList) {
        boolean weekDaysValid = weekDayList.stream().allMatch(d -> d >= 0 && d <= 6);
        return isListOfUniqueValues(weekDayList) && weekDaysValid;
    }

    public static boolean areMonthDaysValid(List<Integer> monthDays) {
        boolean weekDaysValid = monthDays.stream().allMatch(d -> d >= 1 && d <= 31);
        return isListOfUniqueValues(monthDays) && weekDaysValid;
    }

    private static boolean isListOfUniqueValues(List<Integer> list) {
        Set<Integer> set = new HashSet<>(list);
        return set.size() == list.size();
    }
}
