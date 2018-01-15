package br.edu.uepb.nutes.haniot.utils;

/**
 * Maps the column names used in the server database.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class NameColumnsDB {

    public static String USER_ID = "_id";
    public static String USER_NAME = "name";
    public static String USER_GENDER= "gender";
    public static String USER_DATE_OD_BIRTH = "dateOfBirth";
    public static String USER_HEIGHT= "height";
    public static String USER_EMAIL= "email";
    public static String USER_PASSWORD= "password";
    public static String USER_GROUP_ID = "groupId";

    public static String MEASUREMENT_ID = "_id";
    public static String MEASUREMENT_VALUE = "value";
    public static String MEASUREMENT_UNIT = "unit";
    public static String MEASUREMENT_REGISTRATION_DATE = "registrationDate";
    public static String MEASUREMENT_TYPE_ID = "typeId";
    public static String MEASUREMENT_USER_ID = "userId";
    public static String MEASUREMENT_DEVICE_ID = "deviceId";
    public static String MEASUREMENT_CONTEXTS = "contexts";
    public static String MEASUREMENT_MEASUREMENTS = "measurements";

    public static String DEVICE_ID = "_id";
    public static String DEVICE_ADDRESS = "address";
    public static String DEVICE_NAME = "name";
    public static String DEVICE_MANUFACTURER = "manufacturer";
    public static String DEVICE_MODEL_NUMBER = "modelNumber";
    public static String DEVICE_USER_ID = "userId";
    public static String DEVICE_TYPE_ID = "typeId";
    public static String DEVICE_CREATED_AT = "created_at";

    public static String CONTEXT_ID = "_id";
    public static String CONTEXT_VALUE_ID = "valueId";
    public static String CONTEXT_TYPE_ID = "typeId";
}
