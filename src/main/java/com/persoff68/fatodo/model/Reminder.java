package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reminder extends AbstractModel {

    private Periodicity periodicity;

    private DateParams date;

    private List<Integer> weekDays;

    private List<Integer> monthDays;

}
