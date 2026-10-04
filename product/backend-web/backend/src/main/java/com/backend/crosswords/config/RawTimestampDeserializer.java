package com.backend.crosswords.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;

public class RawTimestampDeserializer extends JsonDeserializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String dateStr = p.getText();
        return Timestamp.valueOf(dateStr.replace("Z", "").replace("T", " "));
    }
}
