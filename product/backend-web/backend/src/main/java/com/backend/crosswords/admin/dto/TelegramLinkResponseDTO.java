package com.backend.crosswords.admin.dto;

public class TelegramLinkResponseDTO {
    private boolean success;
    private Long userId;
    private String userName;
    private String error;

    public TelegramLinkResponseDTO() {}

    public TelegramLinkResponseDTO(boolean success, Long userId, String userName, String error) {
        this.success = success;
        this.userId = userId;
        this.userName = userName;
        this.error = error;
    }

    // Геттеры и сеттеры
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}