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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.MenuContactAdapter;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.common.DateUtils;
import com.xyz.automate.esos.object.GroupUser;
import com.xyz.automate.esos.object.MedicalAgent;
import com.xyz.automate.esos.object.User;
import com.xyz.automate.esos.service.LocationService;
import com.xyz.automate.esos.service.MapManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int agent;
    private int userType;
    private String currentCall = "";
    private DatabaseReference mDatabase;
    private MapManager mapManager;
    private boolean isSoSing;
    private String objective;
    private User destination;
    private List<GroupUser> listMapLoc = new ArrayList<>();

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
        destination = null;
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
        listenerDatabase();
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Constants.RC_ALLOW_GPS) {
            initMap();
            mapManager.updatePartner(listMapLoc, true);
        } else if (requestCode == Constants.RC_ALLOW_CALL) {
            actionCall(currentCall);
        }
    }

    public void updateLocation(LatLng latLng) {
        MedicalAgent medicalAgent = CommonUtils.findHospital(CommonUtils.getFixHospital(this), agent, userType);
        if (medicalAgent != null) {
            mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("lat").setValue(medicalAgent.getLat());
            mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("lng").setValue(medicalAgent.getLng());
        } else {
            mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("lat").setValue(latLng.latitude);
            mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("lng").setValue(latLng.longitude);
        }
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("status").setValue(Constants.ONLINE);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("lastupdate").setValue(CommonUtils.date2str(Calendar.getInstance().getTime(), "yyyyMMddHHmmss"));
    }

    public void actionChooseLocation(final GroupUser groupUser) {
        if (Constants.END_USER == groupUser.getAgentGroup() && (Constants.MOBILE_MEDICAL == agent || Constants.POLICEMAN == agent)) {
            if (isSoSing) {
                callPhone(groupUser.users.get(0).getPhoneNumber());
            } else {
                //TODO action setup sos
            }

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
        userType = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY);
        agent = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_AGENT_KEY);
        MedicalAgent medicalAgent = CommonUtils.findHospital(CommonUtils.getFixHospital(this), agent, userType);
        switch (agent) {
            case Constants.CENTER_HOSPITAL:
                this.tvUnitname.setText(medicalAgent.getUnitName());
                avatar.setImageResource(R.mipmap.ic_hospital_center);
                break;
            case Constants.LOCAL_HOSPITAL:
                this.tvUnitname.setText(medicalAgent.getUnitName());
                avatar.setImageResource(R.mipmap.ic_medical_bag);
                break;
            case Constants.MOBILE_MEDICAL:
                this.tvUnitname.setText(getString(R.string.emergency_group));
                avatar.setImageResource(R.mipmap.ic_ambulance);
                break;
            case Constants.POLICEMAN:
                this.tvUnitname.setText(getString(R.string.traffic_police));
                avatar.setImageResource(R.mipmap.ic_policeman);
                break;
            case Constants.END_USER:
                this.tvUnitname.setText(getString(R.string.end_user));
                avatar.setImageResource(R.mipmap.ic_user_avatar);
                break;
            default:
                this.tvUnitname.setText("");
                avatar.setImageResource(R.mipmap.ic_user_avatar);
                break;
        }
    }

    private void updateUserInfo() {
        String value = CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.USER_NAME_KEY);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("userName").setValue(value);
        value = CommonUtils.getPrefString(ESoSApplication.getInstance(), Constants.PHONE_NUMBER_KEY);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("phoneNumber").setValue(value);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("type").setValue(userType);
        mDatabase.child("users").child(ESoSApplication.getInstance().uDiD()).child("agent").setValue(agent);
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
        if (Constants.LOCAL_HOSPITAL == agent || Constants.CENTER_HOSPITAL == agent) {
            fab.hide();
        } else {
            fab.show();
            if (Constants.POLICEMAN == agent || Constants.MOBILE_MEDICAL == agent) {
                if (!isSoSing) {
                    fab.hide();
                }
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
        if (Constants.POLICEMAN == agent || Constants.MOBILE_MEDICAL == agent) {
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
                        if (Constants.POLICEMAN == agent || Constants.MOBILE_MEDICAL == agent) {
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

    private void listenerDatabase() {
        Query myTopPostsQuery = mDatabase.child("users");
        myTopPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, List<User>> mapUser = new HashMap<>();
                List<GroupUser> listUser = new ArrayList<>();
                for (DataSnapshot e : dataSnapshot.getChildren()) {
                    User user = e.getValue(User.class);
                    user.setUserId(e.getKey());
                    if (e.getKey().equals(ESoSApplication.getInstance().uDiD())) {
                        if (isExistTarget(user)) {
                            objective = user.getObjective();
                        } else {
                            objective = Constants.EMPTY;
                        }
                        continue;
                    }
                    if (Constants.END_USER != user.getAgent() && Constants.ONLINE != user.getStatus()) {
                        continue;
                    }
                    if (Constants.END_USER == agent) {
                        if (agent != user.getAgent()) {
                            addUser2Set(mapUser, user);
                        }
                    } else {
                        if (endUserSoS(user)) {
                            addUser2Set(mapUser, user);
                        } else if (agent != user.getAgent() && Constants.END_USER != user.getAgent()) {
                            addUser2Set(mapUser, user);
                        }
                    }
                }
                GroupUser groupUser = new GroupUser();
                groupUser.addListGroup(mapUser.get(Constants.CENTER_HOSPITAL));
                listUser.add(groupUser);
                listUser.addAll(groupUsernearest(mapUser.get(Constants.LOCAL_HOSPITAL)));
                listUser.addAll(groupUsernearest(mapUser.get(Constants.MOBILE_MEDICAL)));
                listUser.addAll(groupUsernearest(mapUser.get(Constants.POLICEMAN)));
                if  (mapUser.get(Constants.END_USER) != null) {
                    for (int i = 0; i < mapUser.get(Constants.END_USER).size(); i++) {
                        GroupUser g = new GroupUser();
                        g.users.add(mapUser.get(Constants.END_USER).get(i));
                        listUser.add(g);
                    }
                }
                determineDestination(mapUser);
                if (mapManager != null) {
                    mapManager.updatePartner(listUser, true);
                } else {
                    listMapLoc.clear();
                    listMapLoc.addAll(listUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addUser2Set(Map<String, List<User>> mapUser, User user) {
        List<User> tmp;
        String key = String.valueOf(user.getAgent());
        if (mapUser.containsKey(key)) {
            tmp = mapUser.get(key);
        } else {
            tmp = new ArrayList<>();
        }
        tmp.add(user);
        mapUser.put(key, tmp);
    }

    private List<GroupUser> groupUsernearest(List<User> listUser) {
        List<GroupUser> listGroup = new ArrayList<>();
        if  (listUser == null || listUser.isEmpty()) {
            return listGroup;
        }
        boolean exist;
        for (User u : listUser) {
            exist = false;
            for (int i = 0; i < listGroup.size(); i++) {
                GroupUser groupUser = listGroup.get(i);
                if (groupUser.users.isEmpty()) {
                    continue;
                }
                LatLng latLng = groupUser.centerPoint();
                if (CommonUtils.meterDistanceBetweenPoints(u.getLat(), u.getLng(), latLng.latitude, latLng.longitude) < 200) {
                    exist = true;
                    listGroup.get(i).users.add(u);
                    break;
                }
            }
            if (!exist) {
                GroupUser g = new GroupUser();
                g.users.add(u);
                listGroup.add(g);
            }
        }
        return listGroup;
    }

    private boolean endUserSoS(User user) {
        if (Constants.END_USER != user.getAgent()) {
            return false;
        }
        if (Constants.ON_SOS == user.getSos() && !CommonUtils.isEmpty(user.getSostime())) {
            Date d = DateUtils.toDate(user.getSostime(), "yyyyMMddHHmmss");
            if (!DateUtils.expire(d, 4)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExistTarget(User user) {
        if (!isSoSing) {
            return false;
        }
        if (Constants.END_USER == agent &&
                (Constants.MOBILE_MEDICAL == user.getAgent() || Constants.POLICEMAN == user.getAgent())) {
            if (!CommonUtils.isEmpty(user.getObjective())) {
                return true;
            }
        }
        if ((Constants.MOBILE_MEDICAL == agent || Constants.POLICEMAN == agent) && Constants.END_USER == user.getAgent()) {
            if (!CommonUtils.isEmpty(user.getObjective())) {
                return true;
            }
        }
        return false;
    }

    private void determineDestination(Map<String, List<User>> mapUser) {
        destination = null;
        if (!isSoSing || CommonUtils.isEmpty(objective) || mapUser == null) {
            return;
        }
        if (Constants.END_USER != agent && Constants.POLICEMAN != agent && Constants.MOBILE_MEDICAL != agent) {
            return;
        }
        if (Constants.END_USER == agent) {
            ArrayList<User> listUser = new ArrayList<>();
            if (mapUser.get(Constants.MOBILE_MEDICAL) != null) {
                listUser.addAll(mapUser.get(Constants.MOBILE_MEDICAL));
            }
            if (mapUser.get(Constants.POLICEMAN) != null) {
                listUser.addAll(mapUser.get(Constants.POLICEMAN));
            }
            for (User u : listUser) {
                if (objective.equals(u.getUserId())) {
                    destination = u;
                    break;
                }
            }
        } else if (Constants.POLICEMAN == agent || Constants.MOBILE_MEDICAL == agent) {
            if (mapUser.get(Constants.END_USER) == null) {
                return;
            }
            for (User u : mapUser.get(Constants.END_USER)) {
                if (objective.equals(u.getUserId())) {
                    destination = u;
                    break;
                }
            }
        }
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
