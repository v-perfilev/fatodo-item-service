package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateParams {
    private int time;
    private int date;
    private int month;
    private int year;
    private int dateOffset;
}
