package br.edu.uepb.nutes.haniot.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.objectbox.converter.PropertyConverter;

public class ConverterStringToDatabase implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        return new Gson().fromJson(databaseValue, new TypeToken<List<String>>(){}.getType());
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        return new Gson().toJson(entityProperty);
    }
}