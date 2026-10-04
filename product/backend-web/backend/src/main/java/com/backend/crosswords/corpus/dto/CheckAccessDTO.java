package com.backend.crosswords.corpus.dto;

public class CheckAccessDTO {
    private Boolean isAvailable;

    public CheckAccessDTO() {
    }

    public CheckAccessDTO(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "CheckAccessDTO{" +
                "isAvailable=" + isAvailable +
                '}';
    }
}
