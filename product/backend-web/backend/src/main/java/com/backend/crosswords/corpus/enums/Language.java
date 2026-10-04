package com.backend.crosswords.corpus.enums;

public enum Language {
    RU,
    ENG;
    public static Language getValueOf(String language) {
        if (language == null || language.isEmpty()) {
            throw new IllegalArgumentException("Language's name can't be null or empty!");
        }
        return Language.valueOf(language.toUpperCase());
    }
}
