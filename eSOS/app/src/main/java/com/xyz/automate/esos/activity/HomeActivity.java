package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvUnitname;
    private TextView tvFullname;
    private TextView tvPhonenumber;
    private ImageView avatar;
    private Constants.UserType userType;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("e-SoS");
        View header = navigationView.getHeaderView(0);
        tvUnitname = (TextView) header.findViewById(R.id.tvHeaderUnitname);
        tvFullname = (TextView) header.findViewById(R.id.tvHeaderFullname);
        tvPhonenumber = (TextView) header.findViewById(R.id.tvHeaderTelNo);
        avatar = (ImageView) header.findViewById(R.id.imageHeaderAvatar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseApp.initializeApp(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").onDisconnect().setValue(Constants.OFFLINE);

        displayUserInfo();
        updateUserInfo();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivityForResult(intent, SettingActivity.RC_SETTING_SCREEN);
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            actionLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SettingActivity.RC_SETTING_SCREEN == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                updateUserInfo();
                displayUserInfo();
            }
        }
    }

    private void displayUserInfo() {
        tvFullname.setText(CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.USER_NAME_KEY));
        tvPhonenumber.setText(CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.PHONE_NUMBER_KEY));
        int type = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY);
        switch (type) {
            case 0:
                userType = Constants.UserType.CoordinationCenter;
                this.tvUnitname.setText(getString(R.string.coordination_center));
                avatar.setImageResource(R.mipmap.ic_hospital_center);
                break;
            case 1:
                userType = Constants.UserType.HealthEstablishment;
                this.tvUnitname.setText(getString(R.string.health_establishment));
                avatar.setImageResource(R.mipmap.ic_medical_bag);
                break;
            case 2:
                userType = Constants.UserType.EmergencyGroup;
                this.tvUnitname.setText(getString(R.string.emergency_group));
                avatar.setImageResource(R.mipmap.ic_ambulance);
                break;
            case 3:
                userType = Constants.UserType.TrafficPolice;
                this.tvUnitname.setText(getString(R.string.traffic_police));
                avatar.setImageResource(R.mipmap.ic_policeman);
                break;
            case 4:
                userType = Constants.UserType.EndUser;
                this.tvUnitname.setText(getString(R.string.end_user));
                avatar.setImageResource(R.mipmap.ic_user_avatar);
                break;
            default:
                userType = Constants.UserType.EndUser;
                this.tvUnitname.setText("");
                avatar.setImageResource(R.mipmap.ic_user_avatar);
                break;
        }
    }

    private void updateUserInfo() {
        String value = CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.USER_NAME_KEY);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("name").setValue(value);
        value = CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.PHONE_NUMBER_KEY);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("tel").setValue(value);
        int type = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("type").setValue(type);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").setValue(Constants.ONLINE);
    }

    private void actionLogout() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Xác nhận")
                .setMessage(getString(R.string.info_msg_001))
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        if (CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.SIGNIN_METHOD_KEY) == Constants.SignIn.FACEBOOK.ordinal()) {
                            LoginManager.getInstance().logOut();
                        }
                        exitHome();
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void exitHome() {
        Intent intent = new Intent(this, LoginActivity.class);
        if (CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.SIGNIN_METHOD_KEY) == Constants.SignIn.GOOGLE.ordinal()) {
            intent.putExtra(Constants.FROM_HOME_TO_LOGIN_FLAG, Constants.FROM_HOME_TO_LOGIN_FLAG);
        }
        startActivity(intent);
        finish();
    }
}
