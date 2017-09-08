package com.xyz.automate.esos.service;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.activity.HomeActivity;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.object.GroupUser;
import com.xyz.automate.esos.object.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LuongDoLong on 9/4/2017.
 */

public class MapManager implements GoogleMap.OnInfoWindowClickListener {
    private GoogleMap mMap;
    private LocationManager mLocationMgr;
    private HomeActivity mContext;
    private EoSLocationListener myLocationListener;
    private Location mCurrentLocation;
    private Constants.UserType userType;
    private int sos = Constants.OFF_SOS;
    private List<GroupUser> users = new ArrayList<>();
    private List<ValueAnimator> valueAnimators = new ArrayList<>();
    private Map<String, GroupUser> mapUser = new HashMap<String, GroupUser>();

    public MapManager(GoogleMap map, HomeActivity context) {
        mMap = map;
        mLocationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext = context;
        myLocationListener = new EoSLocationListener();
        int type = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY);
        switch (type) {
            case 0:
                userType = Constants.UserType.CoordinationCenter;
                break;
            case 1:
                userType = Constants.UserType.HealthEstablishment;
                break;
            case 2:
                userType = Constants.UserType.EmergencyGroup;
                break;
            case 3:
                userType = Constants.UserType.TrafficPolice;
                break;
            case 4:
                userType = Constants.UserType.EndUser;
                break;
            default:
                userType = Constants.UserType.EndUser;
                break;
        }
    }

    public boolean initMap() {
        if (mMap == null) {
            return false;
        }
        if (!checkPermission()) {
            return false;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        mMap.setOnInfoWindowClickListener(MapManager.this);
        requestCurrentLocation();

        return true;
    }

    public void requestCurrentLocation() {
        if (checkPermission()) {
            Location current;

            mLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, myLocationListener);
            current = mLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (current == null) {
                mLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, myLocationListener);
                mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mapUser.containsKey(marker.getId())) {
            mContext.actionChooseLocation(mapUser.get(marker.getId()));
        }
    }

    private boolean checkPermission() {
        boolean allow = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && mContext.getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP MANAGER", "PERMISSION_GRANTED FAILED");
                Toast.makeText(mContext, "Không thể xác định vị trí\nLàm ơn kiểm tra quyền sử dụng GPS và kết nối mạng", Toast.LENGTH_LONG).show();
                allow = false;
            }
        } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MAP MANAGER", "PERMISSION_GRANTED FAILED");
            Toast.makeText(mContext, "Không thể xác định vị trí\nLàm ơn kiểm tra quyền sử dụng GPS và kết nối mạng", Toast.LENGTH_LONG)
                    .show();
            allow = false;
        }
        return allow;
    }

    private void move(LatLng location, Float zoom) {
        CameraPosition.Builder builder = new CameraPosition.Builder();
        builder.target(location);
        if (zoom == null) {
            zoom = mMap.getCameraPosition().zoom;
        }
        builder.zoom(zoom);
        CameraPosition camera = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));
    }

    public void zoom(float v) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(v), 2000, null);
    }

    private Marker addMarkerDefault(GroupUser group, boolean makeMove) {
        if (group.users == null && group.users.isEmpty()) {
            return null;
        }
        Constants.UserType type = group.getTypeGroup();
        MarkerOptions mark = new MarkerOptions();
        mark.title(group.getTitleGroup(mContext));
        mark.snippet(group.getSnippetGroup(mContext));
        mark.visible(true);
        mark.position(group.getLocationGroup());
        mark.draggable(false);

        if (Constants.UserType.CoordinationCenter == type) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_hospital_center), 40, 40)));
        } else if (Constants.UserType.HealthEstablishment == type) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_medical_bag), 40, 40)));
        } else if (Constants.UserType.EmergencyGroup == type) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_ambulance), 40, 40)));
        } else if (Constants.UserType.TrafficPolice == type) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_policeman), 40, 40)));
        } else if (Constants.UserType.EndUser == type){
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_user_avatar), 40, 40)));
            if (Constants.ON_SOS == group.users.get(0).getSos()) {
                final Circle circle = mMap.addCircle(new CircleOptions()
                        .center(group.getLocationGroup())
                        .radius(1000)
                        .strokeWidth(2)
                        .strokeColor(0xffff0000)
                        .fillColor(0x44ff0000));

                ValueAnimator valueAnimator = new ValueAnimator();
                valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
                valueAnimator.setRepeatMode(ValueAnimator.RESTART);
                valueAnimator.setIntValues(0, 1000);
                valueAnimator.setDuration(6000);
                valueAnimator.setEvaluator(new IntEvaluator());
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float animatedFraction = valueAnimator.getAnimatedFraction();
                        circle.setRadius(animatedFraction * 1000 * 2);
                    }
                });
                valueAnimator.start();
                valueAnimators.add(valueAnimator);
            }
        } else {
            mark.icon(BitmapDescriptorFactory.defaultMarker());
        }
        Marker marker = mMap.addMarker(mark);
        mapUser.put(marker.getId(), group);
        if (makeMove) {
            move(group.getLocationGroup(), null);
        }
        return marker;
    }

    public void updateUserLocation() {
        if (mCurrentLocation == null) {
            Log.d("updateUserLocation", "mCurrentLocation == null");
            if (checkPermission()) {
                mLocationMgr.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, myLocationListener, null);
                mLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            return;
        }
        GroupUser group = new GroupUser();
        User user = new User();
        user.setMe(true);
        user.setStatus(Constants.ONLINE);
        user.setSos(sos);
        user.setType(userType);
        user.setUserId(ESoSApplication.getInstance().uDiD());
        user.setUserName(CommonUtils.getPrefString(mContext, Constants.USER_NAME_KEY));
        user.setPhoneNumber(CommonUtils.getPrefString(mContext, Constants.PHONE_NUMBER_KEY));
        if (Constants.UserType.CoordinationCenter == userType) {
            user.setLat(Double.parseDouble(mContext.getString(R.string.lat_location_198_hospital)));
            user.setLng(Double.parseDouble(mContext.getString(R.string.lon_location_198_hospital)));
            user.setUnitName(mContext.getString(R.string.coordination_center));
        } else if (Constants.UserType.HealthEstablishment == userType) {
            user.setLat(mCurrentLocation.getLatitude());
            user.setLng(mCurrentLocation.getLongitude());
            user.setUnitName(mContext.getString(R.string.health_establishment));
        } else if (Constants.UserType.EmergencyGroup == userType) {
            user.setLat(mCurrentLocation.getLatitude());
            user.setLng(mCurrentLocation.getLongitude());
            user.setUnitName(mContext.getString(R.string.emergency_group));
        } else if (Constants.UserType.TrafficPolice == userType) {
            user.setLat(mCurrentLocation.getLatitude());
            user.setLng(mCurrentLocation.getLongitude());
            user.setUnitName(mContext.getString(R.string.traffic_police));
        } else {
            user.setLat(mCurrentLocation.getLatitude());
            user.setLng(mCurrentLocation.getLongitude());
            user.setUnitName(mContext.getString(R.string.end_user));
        }
        group.users.add(user);
        addMarkerDefault(group, false);
    }

    private void addLocation(GroupUser groupUser) {
        addMarkerDefault(groupUser, false);
    }

    public void resetMap() {
        if (mCurrentLocation == null) {
            return;
        }
        mMap.clear();
        mapUser.clear();
        for (ValueAnimator v : valueAnimators) {
            v.cancel();
            v.end();
        }
        valueAnimators.clear();
        updateUserLocation();
        for (GroupUser groupUser : users) {
            if (groupUser == null || groupUser.users.isEmpty()) {
                continue;
            }
            addLocation(groupUser);
        }
    }

    private class EoSLocationListener implements LocationListener {
        private static final String TAG = "MY_LOCATION_LISTENER";

        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                Log.v(TAG, "Location = NULL ");
                return;
            }
            mCurrentLocation = location;
            Log.v(TAG, "Latitude: " + location.getLatitude() + "/Longitude: " + location.getLongitude());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            mMap.animateCamera(cameraUpdate);
            mContext.updateLocation(latLng);
            resetMap();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            Log.v(TAG, "onStatusChanged: " + provider + ", " + status);
        }
    }
}


