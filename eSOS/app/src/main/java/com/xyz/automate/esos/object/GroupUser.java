package com.xyz.automate.esos.object;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luongdolong on 9/8/2017.
 */

public class GroupUser {
    public List<User> users = new ArrayList<>();

    public LatLng getLocationGroup() {
        if (users.isEmpty()) {
            return null;
        }
        return new LatLng(users.get(0).getLat(), users.get(0).getLng());
    }

    public int getAgentGroup() {
        if (users.isEmpty()) {
            return -1;
        }
        return users.get(0).getAgent();
    }

    public String getTitleGroup(Context context) {
        if (users.isEmpty()) {
            return "";
        }
        String title = "";
        if (Constants.END_USER == getAgentGroup()) {
            title = users.get(0).getUserName();
        } else if (Constants.LOCAL_HOSPITAL == getAgentGroup()){
            MedicalAgent medicalAgent = CommonUtils.findHospital(CommonUtils.getFixHospital(context), users.get(0).getAgent(), users.get(0).getType());
            if (medicalAgent != null && users.size() == 1) {
                title = medicalAgent.getUnitName();
            } else if (users.size() != 1) {
                title = context.getString(R.string.health_establishment);
            }
        } else if (Constants.MOBILE_MEDICAL == getAgentGroup()) {
            title = context.getString(R.string.emergency_group);
        } else if (Constants.POLICEMAN == getAgentGroup()) {
            title = context.getString(R.string.traffic_police);
        } else if (Constants.CENTER_HOSPITAL == getAgentGroup()) {
            title = context.getString(R.string.coordination_center);
        }
        return title;
    }

    public String getSnippetGroup(Context mContext) {
        if (users.isEmpty()) {
            return "";
        }
        String snippet;
        if (Constants.END_USER == getAgentGroup()) {
            snippet = users.get(0).getPhoneNumber();
        } else {
            if (users.size() == 1) {
                snippet = String.format("%s (%s)", users.get(0).getUserName(), users.get(0).getPhoneNumber());
            } else {
                snippet = String.format(mContext.getString(R.string.info_msg_002), users.size());
            }
        }
        return snippet;
    }

    public LatLng centerPoint() {
        if (users == null && users.isEmpty()) {
            return null;
        }
        double lat = 0;
        double lng = 0;
        for (User u : users) {
            lat += u.getLat();
            lng += u.getLng();
        }
        lat = lat / (double)users.size();
        lng = lng / (double)users.size();
        return new LatLng(lat, lng);
    }

    public void addListGroup(List<User> userList) {
        if (userList == null || userList.isEmpty()) {
            return;
        }
        users.addAll(userList);
    }
}
