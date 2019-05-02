package br.edu.uepb.nutes.haniot.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides useful methods for handling date/time.
 *
 * @author @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class DateUtils {
    public static final String DATE_FORMAT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Get/Retrieve calendar instance.
     *
     * @return The Calendar.
     */
    public static Calendar getCalendar() {
        return GregorianCalendar.getInstance();
    }

    /**
     * Convert date time in Object Date.
     *
     * @param datetime {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static Date convertDateTime(String datetime) {
        if (datetime == null) return null;

        DateFormat dateFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
        try {
            return dateFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Convert date time in Object Date.
     *
     * @param datetime {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTime(@NonNull String datetime, String formatDate) {
        if (formatDate == null) formatDate = DateUtils.DATE_FORMAT_DATE_TIME;

        DateFormat format = new SimpleDateFormat(formatDate, Locale.getDefault());
        return format.format(datetime);
    }

    /**
     * Convert date time in UTC format.
     *
     * @param datetime {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeToUTC(@NonNull String datetime) {
        Date dateUTC = null;
        DateFormat formatUTC = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());

        try {
            dateUTC = formatUTC.parse(datetime);
            formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatUTC.format(dateUTC);
    }

    /**
     * Convert date time in UTC format.
     *
     * @param datetime - {@link Date}
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeToUTC(Date datetime) {
        DateFormat formatUTC = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
        formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatUTC.format(datetime);
    }

    /**
     * Convert date and time in UTC to local time and date.
     *
     * @param datetime   {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @param formatDate {@link String} Output format. Default yyy-MM-dd'T'HH:mm:ss
     * @param timezone   {@link TimeZone} Your timezone
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeUTCToLocale(@NonNull String datetime, String formatDate, TimeZone timezone) {
        if (timezone == null) timezone = TimeZone.getDefault();
        if (formatDate == null) formatDate = DateUtils.DATE_FORMAT_DATE_TIME;

        Date dateUTC = null;
        DateFormat formatLocale = new SimpleDateFormat(formatDate, Locale.getDefault());
        formatLocale.setTimeZone(timezone);

        try {
            DateFormat utcFormat = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dateUTC = utcFormat.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formatLocale.format(dateUTC);
    }

    /**
     * Convert date and time in UTC to local time and date.
     *
     * @param datetime   {@link String} Date time in format yyy-MM-dd'T'HH:mm:ss
     * @param formatDate {@link String} Output format. Default yyy-MM-dd'T'HH:mm:ss
     * @return Date time in format yyy-MM-dd'T'HH:mm:ss
     */
    public static String convertDateTimeUTCToLocale(@NonNull String datetime, String formatDate) {
        return DateUtils.convertDateTimeUTCToLocale(datetime, formatDate, null);
    }

    /**
     * Returns the current datetime in format of formatDate string passed as parameter.
     * If "formatDate" is null the default value: "yyyy-MM-dd HH:mm:ss" will be used.
     *
     * @param formatDate The datetime format
     * @return Datetime formatted
     */
    public static String getCurrentDatetime(String formatDate) {
        if (formatDate == null) formatDate = "yyyy-MM-dd HH:mm:ss";

        Calendar calendar = GregorianCalendar.getInstance();

        DateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Validate datetime.
     *
     * @param str_date String
     * @return boolean
     */
    public static boolean isDateTimeValid(String str_date) {
        if (str_date == null) return false;

        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DATE_TIME, Locale.getDefault());
        dateFormat.setLenient(false);

        try {
            return (dateFormat.parse(str_date) != null);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Retrieve a date/time passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds long
     * @param formatDate   String
     * @return String
     */
    public static String formatDate(long milliseconds, String formatDate) {
        if (formatDate == null) return null;

        Calendar calendar = DateUtils.getCalendar();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Convert string date in string format.
     *
     * @param date       {@link String}
     * @param formatDate {@link String}
     * @return String
     */
    public static String formatDate(@Nullable String date, @Nullable String formatDate) {
        if (formatDate == null || formatDate.length() == 0)
            formatDate = "yyyy-MM-dd";

        return getFormatDataTime(date, formatDate, true, false);
    }

    /**
     * Convert string date in string format.
     *
     * @param date       {@link String}
     * @param formatDate {@link String}
     * @return String
     */
    public static String formatDateTime(String date, @Nullable String formatDate) {
        if (formatDate == null || formatDate.length() == 0) formatDate = DATE_FORMAT_DATE_TIME;

        return getFormatDataTime(date, formatDate, true, true);
    }

    /**
     * Retrieve a datetime passed in milliseconds to the format passed as a parameter.
     *
     * @param milliseconds long
     * @param formatDate   String
     * @return String
     */
    public static String formatDateTime(long milliseconds, String formatDate) {
        if (formatDate == null || formatDate.length() == 0) formatDate = DATE_FORMAT_DATE_TIME;

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private static String getFormatDataTime(String date_input, String formatDate,
                                            boolean date, boolean time) {
        String result = "";

        if (date && !time) {
            result = getDataTime(Integer.parseInt(date_input.substring(0, 4)),
                    Integer.parseInt(date_input.substring(5, 7)) - 1,
                    Integer.parseInt(date_input.substring(8, 10)),
                    0, 0, 0, formatDate);
        } else if (time && !date) {
            result = getDataTime(0, 0, 0,
                    Integer.parseInt(date_input.substring(11, 13)),
                    Integer.parseInt(date_input.substring(14, 16)),
                    Integer.parseInt(date_input.substring(17, 19)), formatDate);
        } else {
            result = getDataTime(Integer.parseInt(date_input.substring(0, 4)),
                    Integer.parseInt(date_input.substring(5, 7)) - 1,
                    Integer.parseInt(date_input.substring(8, 10)),
                    Integer.parseInt(date_input.substring(11, 13)),
                    Integer.parseInt(date_input.substring(15, 16)),
                    Integer.parseInt(date_input.substring(17, 19)),
                    formatDate);
        }
        return result;
    }

    /**
     * Returns datetime according to the parameters.
     *
     * @param year       The year
     * @param month      The month
     * @param day        The day
     * @param hourOfDay  The hour
     * @param minute     The minute
     * @param formatDate The format date output.
     * @return The string datetime.
     */
    private static String getDataTime(int year, int month, int day, int hourOfDay,
                                      int minute, int milliseconds, String formatDate) {
        Calendar calendar = GregorianCalendar.getInstance();
        // Value to be used for MONTH field. 0 is January
        calendar.set(year, month, day, hourOfDay, minute, milliseconds);

        DateFormat dateFormat = new SimpleDateFormat(formatDate, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    /**
     * Retrieve the current date according to timezone UTC.
     *
     * @return String
     */
    public static String getCurrentDateTimeUTC() {
        SimpleDateFormat format = new SimpleDateFormat(DateUtils.DATE_FORMAT_DATE_TIME, Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(new Date());
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
     * Add days in string date.
     *
     * @param days (-) to decrease the days (+) to advance
     * @return Calendar
     */
    public static String addDaysToDateString(String date, int days) {
        Calendar c = convertStringDateToCalendar(date, null);

        c.add(Calendar.DAY_OF_YEAR, days);
        c.set(Calendar.HOUR_OF_DAY, 0); // 0 hours
        c.set(Calendar.MINUTE, 0); // 0 minute
        c.set(Calendar.SECOND, 1); // 1 second

        return formatDate(c.getTimeInMillis(), "yyyy-MM-dd");
    }

    public static Calendar convertStringDateToCalendar(String date, String format) {
        if (format == null || format.length() == 0) format = "yyyy-MM-dd";

        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            calendar.setTime(df.parse(date));
        } catch (ParseException e) {
        }

        return calendar;
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime     Datetime.
     * @param milliseconds Total in milliseconds.
     * @return String
     */
    public static String addMillisecondsToString(String datetime, int milliseconds) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_DATE_TIME);
        calendar.add(Calendar.MILLISECOND, milliseconds);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_DATE_TIME);
    }

    /**
     * Add minutes in datetime.
     *
     * @param datetime Datetime.
     * @param minutes  Total in minutes.
     * @return String
     */
    public static String addMinutesToString(String datetime, int minutes) {
        Calendar calendar = convertStringDateToCalendar(datetime, DATE_FORMAT_DATE_TIME);
        calendar.add(Calendar.MINUTE, minutes);

        return formatDate(calendar.getTimeInMillis(), DATE_FORMAT_DATE_TIME);
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
            DateFormat df = new SimpleDateFormat(format, Locale.getDefault());
            df.setLenient(false);
            df.parse(date);

            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks if the date passed as parameter is today.
     *
     * @param dateMills long
     * @return boolean
     */
    public static boolean isToday(long dateMills) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c1.setTimeInMillis(dateMills);
        c2.setTimeInMillis(new Date().getTime());

        return c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c2.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
    }

    /**
     * Checks if the date passed as parameter is this year.
     *
     * @param strDate String
     * @return boolean
     */
    public static boolean isYear(String strDate) {
        Calendar c1 = GregorianCalendar.getInstance();
        Calendar c2 = GregorianCalendar.getInstance();
        c2.setTimeInMillis(new Date().getTime());

        c1.setTime(DateUtils.convertDateTime(strDate));

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR));
    }
}
