package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import lombok.Builder;

public class TestDateParams extends DateParams {

    @Builder
    TestDateParams(int time, int date, int month, int year, String timezone) {
        super(time, date, month, year, timezone);
    }

    public static TestDateParamsBuilder defaultBuilder() {
        return TestDateParams.builder()
                .time(600)
                .date(10)
                .month(10)
                .year(2090)
                .timezone("Europe/Berlin");
    }

}
