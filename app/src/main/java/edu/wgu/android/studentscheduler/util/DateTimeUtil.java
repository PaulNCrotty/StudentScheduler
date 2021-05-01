package edu.wgu.android.studentscheduler.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.wgu.android.studentscheduler.fragment.GeneralErrorDialogFragment;

public class DateTimeUtil {

    public static final long MILLISECONDS_PER_SECOND = 1000L;
    private static final SimpleDateFormat DATE_FORMATTER_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat DATETIME_FORMATTER_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSS", Locale.US);


    public static long getSecondsSinceEpoch() {
        return new Date().getTime()/MILLISECONDS_PER_SECOND;
    }

    public static long getSecondsSinceEpoch(String isoDate) throws java.text.ParseException {
        return DATE_FORMATTER_ISO_8601.parse(isoDate).getTime()/MILLISECONDS_PER_SECOND;
    }

    /**
     * Simple method that takes the year, month, and day of month and converts it to an ISO 8601
     * conforming string representation of that date.
     *
     * @param year - the integer year (e.g. 1970, or 2020)
     * @param month - the integer month per the java.util.Calendar implementation (0 = January,..., 11 = December)
     * @param dayOfMonth - the integer day of month (1 - 31)
     * @return a string formatted in ISO 8601 for the date (year, month, day)
     */
    public static String getDateString(int year, int month, int dayOfMonth) {
        String dateString;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateString = LocalDate.of(year, month + 1, dayOfMonth).toString();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            dateString = DATE_FORMATTER_ISO_8601.format(calendar.getTime());
        }
        return dateString;
    }

    public static String getDateString(Calendar calendar) {
        return DATE_FORMATTER_ISO_8601.format(calendar.getTime());
    }

    public static String getDateString(long secondsSinceEpoch) {
        return DATE_FORMATTER_ISO_8601.format(new Date(secondsSinceEpoch * MILLISECONDS_PER_SECOND));
    }

    public static String getDateTimeString(Calendar calendar) {
        return DATETIME_FORMATTER_ISO_8601.format(calendar.getTime());
    }

    public static String getBeginningOfMonth(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return DATE_FORMATTER_ISO_8601.format(calendar.getTime());
    }

    public static String getBeginningOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return DATE_FORMATTER_ISO_8601.format(calendar.getTime());
    }
}
