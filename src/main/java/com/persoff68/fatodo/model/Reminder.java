package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.Periodicity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reminder extends AbstractModel implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private Periodicity periodicity;
    private DateParams date;
    private List<Integer> weekDays;
    private List<Integer> monthDays;
}
