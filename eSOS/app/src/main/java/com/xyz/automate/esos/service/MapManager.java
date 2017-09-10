package com.xyz.automate.esos.service;

import android.Manifest;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.activity.HomeActivity;
import com.xyz.automate.esos.activity.LoginActivity;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.object.GroupUser;
import com.xyz.automate.esos.object.MedicalAgent;
import com.xyz.automate.esos.object.User;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private int agent;
    private int userType;
    private int sos = Constants.OFF_SOS;
    private List<GroupUser> users = new ArrayList<>();
    private List<ValueAnimator> valueAnimators = new ArrayList<>();
    private Map<String, GroupUser> mapUser = new HashMap<String, GroupUser>();

    public MapManager(GoogleMap map, HomeActivity context) {
        mMap = map;
        mLocationMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mContext = context;
        myLocationListener = new EoSLocationListener();
        userType = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY);
        agent = CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_AGENT_KEY);
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

    private Marker addMarkerDefault(GroupUser group, boolean makeMove) {
        if (group.users == null && group.users.isEmpty()) {
            return null;
        }
        int agentGroup = group.getAgentGroup();
        MarkerOptions mark = new MarkerOptions();
        mark.title(group.getTitleGroup());
        mark.snippet(group.getSnippetGroup(mContext));
        mark.visible(true);
        mark.position(group.getLocationGroup());
        mark.draggable(false);

        if (Constants.CENTER_HOSPITAL == agentGroup) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_hospital_center), 40, 40)));
        } else if (Constants.LOCAL_HOSPITAL == agentGroup) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_medical_bag), 40, 40)));
        } else if (Constants.MOBILE_MEDICAL == agentGroup) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_ambulance), 40, 40)));
        } else if (Constants.POLICEMAN == agentGroup) {
            mark.icon(BitmapDescriptorFactory.fromBitmap(CommonUtils.getResizedBitmap(
                    BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_policeman), 40, 40)));
        } else if (Constants.END_USER == agentGroup){
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
        user.setAgent(agent);
        user.setType(userType);
        user.setUserId(ESoSApplication.getInstance().uDiD());
        user.setUserName(CommonUtils.getPrefString(mContext, Constants.USER_NAME_KEY));
        user.setPhoneNumber(CommonUtils.getPrefString(mContext, Constants.PHONE_NUMBER_KEY));
        MedicalAgent medicalAgent = CommonUtils.findHospital(CommonUtils.getFixHospital(mContext), agent, userType);
        if (medicalAgent != null) {
            user.setLat(medicalAgent.getLat());
            user.setLng(medicalAgent.getLng());
            user.setUnitName(medicalAgent.getUnitName());
        } else {
            user.setLat(mCurrentLocation.getLatitude());
            user.setLng(mCurrentLocation.getLongitude());
            if (Constants.MOBILE_MEDICAL == agent) {
                user.setUnitName(mContext.getString(R.string.emergency_group));
            } else if (Constants.POLICEMAN == agent) {
                user.setUnitName(mContext.getString(R.string.traffic_police));
            } else {
                user.setUnitName(mContext.getString(R.string.end_user));
            }
        }

        group.users.add(user);
        addMarkerDefault(group, false);
    }

    private void addLocation(GroupUser groupUser) {
        addMarkerDefault(groupUser, false);
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
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
        drawDirection();
    }

    public void updatePartner(List<GroupUser> mapLocationList, boolean reset) {
        users.clear();
        users.addAll(mapLocationList);
        if (reset) {
            resetMap();
        }
    }

    public void zoom(float v) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(v), 2000, null);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    public void  drawDirection() {
        //TODO
        MedicalAgent medicalAgent = CommonUtils.findHospital(CommonUtils.getFixHospital(mContext), agent, userType);
        LatLng origin = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        LatLng dest = new LatLng(medicalAgent.getLat(), medicalAgent.getLng());
        // Getting URL to the Google Directions API
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();

        // Start downloading json data from Google Directions API
        FetchUrl.execute(url);
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

    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");
            }
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}


