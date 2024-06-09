package com.example.finalproject.service;

import android.os.Build;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String dateString = json.getAsString();

            try {
                if (dateString.contains("T")) {
                    return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    return LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            } catch (DateTimeParseException e) {
                throw new JsonParseException("Unable to parse date: " + dateString);
            }
        }
        return null;
    }
}