package com.xyz.automate.esos.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.object.MedicalAgent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by LuongDoLong on 8/9/2017.
 */

public class CommonUtils {
    // Sharedpref file name
    private static final String PREF_NAME = "AutomateTrackingPref";
    private static ArrayList<MedicalAgent> fixMedicalAgents;

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

    public static float meterDistanceBetweenPoints(double startLat, double startLng, double endLat, double endLng) {
        Location locationA = new Location("point A");

        locationA.setLatitude(startLat);
        locationA.setLongitude(startLng);

        Location locationB = new Location("point B");

        locationB.setLatitude(endLat);
        locationB.setLongitude(endLng);

        float distance = locationA.distanceTo(locationB);
        return distance;
    }

    public static MedicalAgent findHospital(ArrayList<MedicalAgent> medicalAgents, int agent, int type) {
        if (medicalAgents == null || medicalAgents.isEmpty()) {
            return null;
        }
        for (MedicalAgent e : medicalAgents) {
            if (e.getAgent() == agent && e.getUnitType() == type) {
                return e;
            }
        }
        return null;
    }

    public static ArrayList<MedicalAgent> getFixHospital(Context context) {
        if (fixMedicalAgents != null) {
            return fixMedicalAgents;
        }
        InputStream is = context.getResources().openRawResource(R.raw.hospital);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            String jsonString = writer.toString();
            fixMedicalAgents = new Gson().fromJson(jsonString, new TypeToken<ArrayList<MedicalAgent>>(){}.getType());
        }  catch (Exception ex) {
            Log.d("ESOS", ex.toString());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
                Log.d("ESOS", ex.toString());
            }
        }
        return fixMedicalAgents;
    }
}
