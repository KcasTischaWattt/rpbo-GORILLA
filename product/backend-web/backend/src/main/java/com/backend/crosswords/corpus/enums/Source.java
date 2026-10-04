package com.backend.crosswords.corpus.enums;

import java.util.HashMap;
import java.util.Map;

public enum Source {
    INTER("Интерфакс"),
    COMMER("Коммерсантъ"),
    CBUZ("Центробанк Узбекистана"),
    CBTAJ("Центробанк Таджикистана"),
    CBKIR("Центробанк Кыргызстана"),
    CBAZER("Центробанк Азербайджана"),
    CBRF("Центробанк РФ"),
    CBGRUZ("Центробанк Грузии");
    private final String russianName;
    private static final Map<String, Source> RUSSIAN_NAME_MAP = new HashMap<>();
    static {
        for (Source source : Source.values()) {
            RUSSIAN_NAME_MAP.put(source.russianName.toLowerCase(), source);
        }
    }
    Source(String russianName) {
        this.russianName = russianName;
    }
    public String getRussianName() {
        return russianName;
    }
    public static Source fromRussianName(String russianName) {
        if (russianName == null || russianName.isEmpty()) {
            throw new IllegalArgumentException("Название источника не может быть пустым или null!");
        }
        Source source = RUSSIAN_NAME_MAP.get(russianName.toLowerCase());
        if (source == null) {
            throw new IllegalArgumentException("Неизвестное русское название: " + russianName);
        }
        return source;
    }
}
