package com.onix.recorder.lame.data.utils;

import android.support.annotation.IntRange;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class UnixTimeUtils {

    public static String DATE_FORMAT_DD_MM_YYYY_HH_MM = "dd-MM-yyyy HH:mm";
    public static String DATE_FORMAT_HH_MM = "HH:mm";

    public static String convertToString(long unixSeconds, String dateFormat) {
        Date date = new Date(unixSeconds * 1000L);

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);
    }

    public static long getCurrentUnixTime() {
        return Calendar.getInstance().getTime().getTime()/ 1000;
    }

    public static long convertToUnix(int day, @IntRange(from = 0, to = 11) int month, int year, int hours, int minutes) {
        long unixTime = 0;

        // increase month index to get real month number
        ++month;

        String dateString = String.format(Locale.getDefault(), "%02d-%02d-%d %02d:%02d", day, month, year, hours, minutes);
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY_HH_MM, Locale.getDefault());

        try {
            Date date = dateFormat.parse(dateString);
            unixTime = date.getTime() / 1000;
        } catch (ParseException e) {
        }

        return unixTime;
    }
}
