package br.edu.uepb.nutes.haniot.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.List;

import io.objectbox.converter.PropertyConverter;

public class ConverterStringToDatabase implements PropertyConverter<List<String>, String> {
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        List<String> listOfStrings = new Gson().fromJson(databaseValue, List.class);
        return listOfStrings;
    }

    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        String json = new Gson().toJson(entityProperty);
        return json;
    }
}