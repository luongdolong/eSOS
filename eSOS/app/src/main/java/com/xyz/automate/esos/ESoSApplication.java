package com.xyz.automate.esos;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.activeandroid.ActiveAndroid;
import com.google.firebase.FirebaseApp;
import com.xyz.automate.esos.logic.DataManager;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class ESoSApplication extends com.activeandroid.app.Application {

    private static ESoSApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        DataManager.createInstance(getApplicationContext());
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
