package com.hufcusfocus.hufsland.domain.entity.student;

import lombok.Getter;

@Getter
public enum Grade {
    FRESHMAN(1), SOPHOMORE(2), JUNIOR(3), SENIOR(4);

    private final int value;
    Grade(int value) {
        this.value = value;
    }

    public static Grade valueOf(int value) {
        switch (value) {
            case 1: return FRESHMAN;
            case 2: return SOPHOMORE;
            case 3: return JUNIOR;
            case 4: return SENIOR;
            default: throw new AssertionError("잘못된 입력입니다.");
        }
    }
}
