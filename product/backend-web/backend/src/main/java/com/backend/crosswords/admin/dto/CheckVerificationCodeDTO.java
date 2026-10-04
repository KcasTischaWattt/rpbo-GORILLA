package com.backend.crosswords.admin.dto;

public class CheckVerificationCodeDTO {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CheckVerificationCodeDTO{" +
                "code='" + code + '\'' +
                '}';
    }
}
