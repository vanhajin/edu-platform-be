package org.store.authsvc.utils.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnumAuthError {
    LOGIN_ACCOUNT_PASSWORD_FAIL("LOGIN_ACCOUNT_PASSWORD_KHONG_DUNG", "Tài khoản hoặc mật khẩu không đúng"),
    REFRESH_TOKEN_INVALID("REFRESH_TOKEN_KHONG_HOP_LE", "Refresh token không hợp lệ hoặc đã hết hạn")
    ;

    private final String ma;
    private final String moTa;
}
