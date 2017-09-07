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
    private List<User> users = new ArrayList<>();
    private List<ValueAnimator> valueAnimators = new ArrayList<>();
    private Map<String, String> mapUser = new HashMap<String, String>();

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

    private Marker addMarkerDefault(String title, String snippet, LatLng location, Constants.UserType type, boolean makeMove) {
        MarkerOptions mark = new MarkerOptions();
        mark.visible(true);
        mark.title(title);
        mark.snippet(snippet);
        mark.position(location);
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
        } else {
            mark.icon(BitmapDescriptorFactory.defaultMarker());
        }
        Marker marker = mMap.addMarker(mark);
        final Circle circle = mMap.addCircle(new CircleOptions()
                .center(location)
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
        if (makeMove) {
            move(location, null);
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
        String title;
        String snippet = CommonUtils.getPrefString(mContext, Constants.USER_NAME_KEY);
        if (Constants.UserType.CoordinationCenter == userType) {
            title = mContext.getString(R.string.coordination_center);
            LatLng latLng = new LatLng(Double.parseDouble(mContext.getString(R.string.lat_location_198_hospital)),
                    Double.parseDouble(mContext.getString(R.string.lon_location_198_hospital)));
            addMarkerDefault(title, snippet, latLng, userType, true);
        } else if (Constants.UserType.HealthEstablishment == userType) {
            title = mContext.getString(R.string.health_establishment);
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            addMarkerDefault(title, snippet, latLng, userType, true);
        } else if (Constants.UserType.EmergencyGroup == userType) {
            title = mContext.getString(R.string.emergency_group);
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            addMarkerDefault(title, snippet, latLng, userType, true);
        } else if (Constants.UserType.TrafficPolice == userType) {
            title = mContext.getString(R.string.traffic_police);
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            addMarkerDefault(title, snippet, latLng, userType, true);
        } else {
            title = CommonUtils.getPrefString(mContext, Constants.USER_NAME_KEY);
            snippet = CommonUtils.getPrefString(mContext, Constants.PHONE_NUMBER_KEY);
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            addMarkerDefault(title, snippet, latLng, null, true);
        }
    }

    private void addLocation(String username, String tel, LatLng latLng) {
        addMarkerDefault(username, tel, latLng, null, false);
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
        for (User u : users) {
//            if (u == null || u.getLatLng() == null) {
//                continue;
//            }
//            addLocation(loc.getUsername(), loc.getPhoneNumber(), loc.getLatLng());
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


