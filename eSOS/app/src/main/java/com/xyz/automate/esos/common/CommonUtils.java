package com.xyz.automate.esos.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Created by LuongDoLong on 8/9/2017.
 */

public class CommonUtils {
    // Sharedpref file name
    private static final String PREF_NAME = "AutomateTrackingPref";

    /**
     * put data in SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key get value
     * @param value value
     */
    public static void putPref(Context context, String key, Object value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean)value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer)value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float)value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long)value);
        } else {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }

    /**
     * get String from SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key
     * @return value
     */
    public static String getPrefString(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getString(key, Constants.EMPTY);
    }

    /**
     * get Boolean from SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key
     * @return value
     */
    public static boolean getPrefBoolean(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    /**
     * get Integer from SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key
     * @return value
     */
    public static int getPrefInteger(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(key, 0);
    }

    /**
     * get Long from SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key
     * @return value
     */
    public static long getPrefLong(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

    /**
     * get Float from SharedPreferences
     *
     * @author luongdolong
     * @param context app context
     * @param key key
     * @return value
     */
    public static float getPrefFloat(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return pref.getFloat(key, 0);
    }

    /**
     * remove value from SharedPreferences with key
     *
     * @author luongdolong
     * @param context context
     * @param key key
     */
    public static void removePref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * check input is empty
     *
     * @author luongdolong
     * @param string input
     * @return true if empty, false if not
     */
    public static boolean isEmpty(String string) {
        if (string == null) {
            return true;
        }
        if (string.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Check connection state internet
     *
     * @uthor longld
     * @param context
     * @return
     */
    public static boolean checkConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

        if (activeNetworkInfo != null) { // connected to the internet
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                return true;
            }
        }
        return false;
    }

    /**
     * check internet
     *
     * @author luongdolong
     * @param context
     * @return true if have internet, false if not
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * convert date to string with format
     *
     * @author longld
     * @param date date
     * @param format format
     * @return string date with format pass
     */
    public static String date2str(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat mySimpleDateFormat = new SimpleDateFormat(format);
        return mySimpleDateFormat.format(date);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
