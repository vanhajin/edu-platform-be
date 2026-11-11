package com.eduplatform.common.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumRole {
    ADMIN("ADMIN", "Tài khoản quản trị"),
    INSTRUCTOR("INSTRUCTOR", "Giảng viên"),
    STUDENT("STUDENT", "Học viên"),
    ;
    private final String code;
    private final String moTa;
}
