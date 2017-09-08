package com.xyz.automate.esos.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.skyfishjy.library.RippleBackground;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.MenuContactAdapter;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.object.GroupUser;
import com.xyz.automate.esos.object.User;
import com.xyz.automate.esos.service.LocationService;
import com.xyz.automate.esos.service.MapManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tvUnitname;
    private TextView tvFullname;
    private TextView tvPhonenumber;
    private ImageView avatar;
    private RippleBackground rippleBackground;
    private ImageView centerImageSoS;
    private FloatingActionButton fab;
    private Intent serviceBg;
    private boolean isMapInitSuccess = false;
    private Constants.UserType userType;
    private String currentCall = "";
    private DatabaseReference mDatabase;
    private MapManager mapManager;
    private boolean isSoSing;

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
        rippleBackground = (RippleBackground) findViewById(R.id.rippleSoS);
        centerImageSoS = (ImageView) findViewById(R.id.centerImageSoS);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        isSoSing = false;
        FirebaseApp.initializeApp(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").onDisconnect().setValue(Constants.OFFLINE);

        displayUserInfo();
        updateUserInfo();
        initMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").setValue(Constants.ONLINE);
        if (isMapInitSuccess) {
            mapManager.requestCurrentLocation();
        }
        if (serviceBg != null) {
            stopService(serviceBg);
        }
        displayUserInfo();
        if (!isMapInitSuccess) {
            initMap();
        }
        setupControl();
    }

    @Override
    public void onPause() {
        if (serviceBg == null) {
            serviceBg = new Intent(this, LocationService.class);
        }
        startService(serviceBg);
        super.onPause();
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
            startActivityForResult(intent, Constants.RC_SETTING_SCREEN);
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
        if (Constants.RC_SETTING_SCREEN == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                updateUserInfo();
                displayUserInfo();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == Constants.RC_ALLOW_GPS) {
            initMap();
        } else if (requestCode == Constants.RC_ALLOW_CALL) {
            actionCall(currentCall);
        }
    }

    public void updateLocation(LatLng latLng) {
    }

    public void actionChooseLocation(final GroupUser groupUser) {
        if (Constants.UserType.EndUser == groupUser.getTypeGroup()) {

        } else {
            if (groupUser.users.size() == 1 &&  !ESoSApplication.getInstance().uDiD().equals(groupUser.users.get(0).getUserId())) {
                callPhone(groupUser.users.get(0).getPhoneNumber());
            } else {
                makeListCall(groupUser);
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
                        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").setValue(Constants.OFFLINE);
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

    private void setupControl() {
        if (Constants.UserType.HealthEstablishment == userType || Constants.UserType.CoordinationCenter == userType) {
            fab.hide();
        } else {
            fab.show();
            if (Constants.UserType.TrafficPolice == userType || Constants.UserType.EmergencyGroup == userType) {
                fab.setImageResource(R.drawable.ic_cancel48);
            }
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionEmergency();
                }
            });
        }
    }

    private void actionEmergency() {
        String title;
        if (Constants.UserType.TrafficPolice == userType || Constants.UserType.EmergencyGroup == userType) {
            title = getString(R.string.info_msg_004);
        }   else if (isSoSing) {
            title = getString(R.string.info_msg_004);
        } else {
            title = getString(R.string.info_msg_003);
        }
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Xác nhận")
                .setMessage(title)
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (Constants.UserType.TrafficPolice == userType || Constants.UserType.EmergencyGroup == userType) {
                            //TODO action cancel sos
                            return;
                        }
                        if (isSoSing) {
                            isSoSing = false;
                            centerImageSoS.setVisibility(View.GONE);
                            rippleBackground.stopRippleAnimation();
                            fab.setImageResource(R.drawable.ic_call48);
                            //TODO action cancel sos
                        } else {
                            isSoSing = true;
                            centerImageSoS.setVisibility(View.VISIBLE);
                            rippleBackground.startRippleAnimation();
                            fab.setImageResource(R.drawable.ic_cancel48);
                            //TODO action call sos
                        }
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void initMap() {
        boolean allow = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 130);
                    allow = false;
                } else {
                    allow = false;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.RC_ALLOW_GPS);
                }
            }
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            allow = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.RC_ALLOW_GPS);
        }
        if (!allow) {
            isMapInitSuccess = false;
            return;
        }

        final MapFragment mapFragment = MapFragment.newInstance();
        getFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (mapManager == null) {
                    mapManager = new MapManager(googleMap, HomeActivity.this);
                }
                isMapInitSuccess = mapManager.initMap();
                Log.d("Map initMap = ", isMapInitSuccess + "");
                if (isMapInitSuccess) {
                    mapManager.updateUserLocation();
                    mapManager.zoom(12F);
                }
            }
        });
    }

    private void exitHome() {
        Intent intent = new Intent(this, LoginActivity.class);
        if (CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.SIGNIN_METHOD_KEY) == Constants.SignIn.GOOGLE.ordinal()) {
            intent.putExtra(Constants.FROM_HOME_TO_LOGIN_FLAG, Constants.FROM_HOME_TO_LOGIN_FLAG);
        }
        startActivity(intent);
        finish();
    }

    private void makeListCall(final GroupUser groupUser) {
        final ArrayList<User> users = new ArrayList<>();
        for(User u : groupUser.users) {
            if (!ESoSApplication.getInstance().uDiD().equals(u.getUserId())) {
                users.add(u);
            }
        }
        if (users.isEmpty()) {
            return;
        } else if (users.size() == 1) {
            callPhone(users.get(0).getPhoneNumber());
            return;
        }
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_menu_list);
        dialog.setTitle("Hãy chọn số điện thoại để gọi");
        ListView listView = (ListView) dialog.findViewById(R.id.menuList);
        MenuContactAdapter adapter = new MenuContactAdapter(new ArrayList<>(users), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                callPhone(users.get(position).getPhoneNumber());
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }

    private void callPhone(final String phone) {
        currentCall = phone;
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Xác nhận")
                .setMessage(String.format("Bạn muốn gọi tới số: %s", phone))
                .setPositiveButton("Gọí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        actionCall(phone);
                    }
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void actionCall(String mobileNo) {
        boolean allow = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CALL_PHONE)) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, Constants.RC_ALLOW_CALL);
                    allow = false;
                } else {
                    allow = false;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constants.RC_ALLOW_CALL);
                }
            }
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            allow = false;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, Constants.RC_ALLOW_CALL);
        }
        if (!allow) {
            return;
        }

        try {
            String uri = "tel:" + mobileNo.trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            this.startActivity(intent);
        } catch (SecurityException ex) {
            Toast.makeText(getApplicationContext(), "Error in your phone call "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error in your phone call "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
