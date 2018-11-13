package br.edu.uepb.nutes.haniot.utils;

/**
 * Maps the column names used in the server database.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class NameColumnsDB {
    public static String USER = "users";
    public static String USER_ID = "_id";
    public static String USER_NAME = "name";
    public static String USER_GENDER = "gender";
    public static String USER_DATE_BIRTH = "dateOfBirth";
    public static String USER_HEIGHT = "height";
    public static String USER_EMAIL = "email";
    public static String USER_PASSWORD = "password";
    public static String USER_GROUP_ID = "groupId";

    public static String MEASUREMENT = "measurements";
    public static String MEASUREMENT_ID = "_id";
    public static String MEASUREMENT_VALUE = "value";
    public static String MEASUREMENT_UNIT = "unit";
    public static String MEASUREMENT_REGISTRATION_DATE = "registrationDate";
    public static String MEASUREMENT_TYPE_ID = "typeId";
    public static String MEASUREMENT_USER_ID = "userId";
    public static String MEASUREMENT_DEVICE_ID = "deviceId";
    public static String MEASUREMENT_CONTEXTS = "contexts";
    public static String MEASUREMENT_MEASUREMENTS = "measurements";

    public static String DEVICE = "devices";
    public static String DEVICE_ID = "_id";
    public static String DEVICE_ADDRESS = "address";
    public static String DEVICE_NAME = "name";
    public static String DEVICE_MANUFACTURER = "manufacturer";
    public static String DEVICE_MODEL_NUMBER = "modelNumber";
    public static String DEVICE_USER_ID = "userId";
    public static String DEVICE_TYPE_ID = "typeId";
    public static String DEVICE_CREATED_AT = "created_at";

    public static String CONTEXT = "contexts";
    public static String CONTEXT_ID = "_id";
    public static String CONTEXT_VALUE_ID = "valueId";
    public static String CONTEXT_TYPE_ID = "typeId";

    public static String ELDERLY = "externalData";
    public static String ELDERLY_ID = "_id";
    public static String ELDERLY_NAME = "name";
    public static String ELDERLY_DATE_BIRTH = "date_birth";
    public static String ELDERLY_WEIGHT = "weight";
    public static String ELDERLY_HEIGHT = "height";
    public static String ELDERLY_SEX = "sex";
    public static String ELDERLY_PHONE = "phone";
    public static String ELDERLY_DEVICE_PIN = "encodedId";
    public static String ELDERLY_MARITAL_STATUS = "marital_status";
    public static String ELDERLY_DEGREE_EDUCATION = "degree_education";
    public static String ELDERLY_LIVE_ALONE = "live_alone";
    public static String ELDERLY_MEDICATIONS = "medications";
    public static String ELDERLY_ACCESSORIES = "accessories";
    public static String ELDERLY_FALL_RISK = "fall_risk";
    public static String ELDERLY_ITEMS_INDEX = "index";
    public static String ELDERLY_ITEMS_NAME = "name";
}
