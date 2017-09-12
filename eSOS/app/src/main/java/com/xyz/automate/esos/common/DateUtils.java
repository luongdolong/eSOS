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

    public static String convertDateCallLog(Date d) {
        String result;
        SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
        result = mySimpleDateFormat.format(d);
        String week = "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        int dayValue = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayValue) {
            case Calendar.SUNDAY:
                week = "Chủ nhật";
                break;
            case Calendar.MONDAY:
                week = "Thứ Hai";
                break;
            case Calendar.TUESDAY:
                week = "Thứ Ba";
                break;
            case Calendar.WEDNESDAY:
                week = "Thứ Tư";
                break;
            case Calendar.THURSDAY:
                week = "Thứ Năm";
                break;
            case Calendar.FRIDAY:
                week = "Thứ Sáu";
                break;
            case Calendar.SATURDAY:
                week = "Thứ Bảy";
                break;
            default:
                break;
        }
        return result.replace("-", week + ",  Ngày");
    }

    public static Date addDate(Date fromDate, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static boolean expire(Date d, int hour) {
        Calendar now = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.HOUR_OF_DAY, hour);

        return !now.before(cal);
    }
}
