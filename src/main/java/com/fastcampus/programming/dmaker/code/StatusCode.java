package com.fastcampus.programming.dmaker.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {
    // enum 으로 값을 지정해두는 것이 편함
    EMPLOYED("고용"),
    RETIRED("퇴직");

    private final String description;
}
