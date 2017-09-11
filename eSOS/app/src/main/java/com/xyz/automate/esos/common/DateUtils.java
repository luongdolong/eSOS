package com.xyz.automate.esos.common;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by LuongDoLong on 9/10/2017.
 */

public class DateUtils {
    public static Date toDate(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            Date d = sdf.parse(value);
            return d;
        } catch (ParseException ex) {
            Log.d("ESOS", ex.getMessage());
        }
        return null;
    }

    public static boolean expire(Date d, int hour) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR_OF_DAY, hour);

        return !now.before(cal);
    }
}
