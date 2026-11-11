package com.eduplatform.common.dtos.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReponseUtil<T> {

    private String code;
    private String message;
    private String otherMessage;
    private T data;

    public static <T> ReponseUtil<T> success(T data) {
        return new ReponseUtil<>("200", "Success", data);
    }

    public static <T> ReponseUtil<T> success() {
        return new ReponseUtil<>("200", "Success", null);
    }

    public static <T> ReponseUtil<T> of(String code, String message, T data) {
        return new ReponseUtil<>(code, message, data);
    }

    public static ReponseUtil<Void> error(String code, String message, String otherMessage) {
        return new ReponseUtil<>(code, message, otherMessage, null);
    }

    public ReponseUtil(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
