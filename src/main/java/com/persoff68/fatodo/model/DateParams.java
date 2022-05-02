package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateParams implements Serializable {
    private int time;
    private int date;
    private int month;
    private int year;
    private String timezone;
}
