package edu.wgu.android.studentscheduler.util;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeUtil {

    private static final SimpleDateFormat DATE_FORMATTER_ISO_8601 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Simple method that takes the year, month, and day of month and converts it to an ISO 8601
     * conforming string representation of that date.
     *
     * @param year - the integer year (e.g. 1970, or 2020)
     * @param month - the integer month (1 = January,..., 12 = December)
     * @param day_of_month - the integer day of month (1 - 31)
     * @return a string formatted in ISO 8601 for the date (year, month, day)
     */
    public static String getDateString(int year, int month,  int day_of_month) {
        String dateString;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dateString = LocalDate.of(year, month, day_of_month).toString();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month + 1, day_of_month);
            dateString = DATE_FORMATTER_ISO_8601.format(calendar.getTime());
        }
        return dateString;
    }
}
