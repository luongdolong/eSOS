package com.xyz.automate.esos;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.google.firebase.FirebaseApp;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class ESoSApplication extends Application {

    private static ESoSApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        try {
            FirebaseApp.initializeApp(this);
        }
        catch (Exception e) {
        }
    }

    public static ESoSApplication getInstance() {
        return mInstance;
    }
    public String uDiD() {
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

}
