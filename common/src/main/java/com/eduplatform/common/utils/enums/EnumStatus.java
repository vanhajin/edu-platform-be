package com.eduplatform.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumStatus {
    ACTIVE(1, "Hoạt động"),
    INACTIVE(0, "Vô hiệu hóa"),
    DELETED(-1, "Đã bị xóa"),

    ;
    private final int code;
    private final String moTa;
}
