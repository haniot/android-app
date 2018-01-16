package br.edu.uepb.nutes.haniot.utils;

import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Provides useful methods for handling date/time.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class DateUtils {

    /**
     * Get/Retrieve calendar instance.
     *
     * @return The Calendar.
     */
    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    /**
     * Returns the current datetime in format of format_date string passed as parameter.
     * If "format_date" is null the default value: "yyyy-MM-dd HH:mm:ss" will be used.
     *
     * @param format_date The datetime format
     * @return Datetime formatted
     */
    public static String getCurrentDatetime(String format_date) {
        if (format_date == null)
            format_date = "yyyy-MM-dd HH:mm:ss";

        Calendar calendar = GregorianCalendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Returns the current datetime in milliseconds.
     *
     * @return long
     */
    public static long getCurrentDatetime() {
        return GregorianCalendar.getInstance().getTimeInMillis();
    }

    /**
     * Returns the current timestamp.
     * - millis - long
     * - timezone - Object (TimeZone)
     * - value - String (yyyy-MM-dd HH:mm:ss)
     *
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject getCurrentTimestamp() throws JSONException {
        return new JSONObject().put("timezone", TimeZone.getDefault())
                .put("mills", DateUtils.getCurrentDatetime())
                .put("value", DateUtils.getCurrentDatetime(null)); // yyyy-MM-dd HH:mm:ss
    }

    /**
     * Convert a calendar using the format_date passed as parameter.
     *
     * @param calendar    Instância do calendario
     * @param format_date Formato da data/hora
     * @return Date/Time formatted
     */
    public static String calendarToString(Calendar calendar, String format_date) {
        if (format_date == null || calendar == null)
            return null;

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convert year, month and day using the format_date passed as parameter.
     *
     * @param year
     * @param month
     * @param day
     * @param format_date
     * @return
     */
    public static String dateToString(int year, int month, int day, String format_date) {
        if (format_date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendarToString(calendar, format_date);
    }

    /**
     * Returns array with the date values
     *
     * @param milliseconds
     * @return
     */
    public static int[] getDateValues(long milliseconds) {
        int[] dateValues = new int[6];
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        dateValues[0] = calendar.get(Calendar.YEAR);
        dateValues[1] = calendar.get(Calendar.MONTH);
        dateValues[2] = calendar.get(Calendar.DAY_OF_MONTH);
        dateValues[3] = calendar.get(Calendar.HOUR);
        dateValues[4] = calendar.get(Calendar.MINUTE);
        dateValues[5] = calendar.get(Calendar.SECOND);

        return dateValues;
    }

    /**
     * Retrieve a date/time passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds
     * @param format_date
     * @return String
     */
    public static String getDatetime(long milliseconds, String format_date) {
        if (format_date == null)
            return null;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(format_date);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Converts string to Calendar Object.
     *
     * @param timeStamp
     * @param date_input - Format: yyyy-MM-dd hh:mm:ss
     * @return Calendar
     */
    public static Calendar stringToCalendar(String timeStamp, String date_input) throws ParseException {
        Calendar c = Calendar.getInstance();

        try {
            /**
             * Checks if the date is in the correct format
             */
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
            format.parse(date_input);

            /**
             * Take the string data
             */
            int year = Integer.parseInt(date_input.substring(0, 4));
            int month = Integer.parseInt(date_input.substring(5, 7)) - 1;
            int day = Integer.parseInt(date_input.substring(8, 10));
            int hour = Integer.parseInt(date_input.substring(11, 13));
            int minute = Integer.parseInt(date_input.substring(14, 16));
            int second = Integer.parseInt(date_input.substring(17, 19));

            c.set(year, month, day, day, minute, second);

            return c;
        } catch (ParseException e) {
            throw new ParseException(e.getMessage(), e.getErrorOffset());
        }
    }

    /**
     * Add days to calendar.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static Calendar addDays(int days) {
        Calendar c = GregorianCalendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 0); // 0 hours
        c.set(Calendar.MINUTE, 0); // 0 minute
        c.set(Calendar.SECOND, 1); // 1 second

        return c;
    }

    /**
     * returns the current year in milliseconds.
     * Example YEAR-01-01 00:00:00
     *
     * @return long
     */
    public static long getCurrentYear() {
        Calendar c = GregorianCalendar.getInstance();

        c.set(Calendar.DAY_OF_YEAR, c.getActualMinimum(Calendar.DAY_OF_YEAR));
        c.set(Calendar.HOUR_OF_DAY, c.getActualMinimum(Calendar.HOUR_OF_DAY));
        c.set(Calendar.MINUTE, c.getActualMinimum(Calendar.MINUTE));
        c.set(Calendar.SECOND, c.getActualMinimum(Calendar.SECOND));
        c.set(Calendar.MILLISECOND, c.getActualMinimum(Calendar.MILLISECOND));

        return c.getTimeInMillis();
    }

    /**
     * Validate date.
     *
     * @param date   String
     * @param format String
     * @return boolean
     */
    public static boolean isDateValid(String date, @Nullable String format) {
        if (format == null || format.length() == 0)
            format = "yyyy-MM-dd";

        try {
            DateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            df.parse(date);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}
