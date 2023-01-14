package com.persoff68.fatodo.model.constant;

import java.util.Arrays;

public enum ItemPriority {
    LOW(1),
    NORMAL(2),
    HIGH(3);

    private final int value;

    ItemPriority(int value) {
        this.value = value;
    }

    public static ItemPriority getByValue(int value) {
        return Arrays.stream(values())
                .filter(v -> v.value == value)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    public static int getValueByName(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name().equals(name))
                .map(v -> v.value)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

}
