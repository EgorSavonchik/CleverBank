package ru.clevertec.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {
    @Override
    public LocalDate read(JsonReader in) throws IOException {
        try {
            return LocalDate.parse(in.nextString());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void write(JsonWriter out, LocalDate value) throws IOException {
        out.value(value.toString());
    }
}