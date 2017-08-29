package com.xyz.automate.esos.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Spinner;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.SpinnerTypeUserAdapter;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.object.UserTypeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class LoginActivity extends AppCompatActivity {
    private Spinner objectTypeSpinner;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        objectTypeSpinner = (Spinner)findViewById(R.id.objectTypeSpinner);
        initControl();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginFBbutton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    private void initControl() {
        ArrayList<UserTypeData> list = new ArrayList<>();
        list.add(new UserTypeData(Constants.TypeUser.CoordinationCenter, getString(R.string.coordination_center)));
        list.add(new UserTypeData(Constants.TypeUser.HealthEstablishment, getString(R.string.health_establishment)));
        list.add(new UserTypeData(Constants.TypeUser.EmergencyGroup, getString(R.string.emergency_group)));
        list.add(new UserTypeData(Constants.TypeUser.TrafficPolice, getString(R.string.traffic_police)));
        list.add(new UserTypeData(Constants.TypeUser.EndUser, getString(R.string.end_user)));
        SpinnerTypeUserAdapter adapter = new SpinnerTypeUserAdapter(this, R.layout.layout_spinner_type_user, R.id.txtSpnNameTypeUser, list);
        objectTypeSpinner.setAdapter(adapter);

    }
}
