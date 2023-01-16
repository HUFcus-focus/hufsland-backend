package com.hufcusfocus.hufsland.domain.entity.studentMajor;

import lombok.Getter;

@Getter
public enum MajorType {
    MAIN(0), DUAL(1), DOUBLE(2), INTENSIFIED(3);

    private int value;

    MajorType(int value) {
        this.value = value;
    }

    public static MajorType valueOf(int value) {
        switch(value) {
            case 0: return MAIN;
            case 1: return DUAL;
            case 2: return DOUBLE;
            case 3: return INTENSIFIED;
            default: throw new AssertionError("잘못된 입력입니다.");
        }
    }
}
