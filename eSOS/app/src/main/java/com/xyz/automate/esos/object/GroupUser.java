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

    public Constants.UserType getTypeGroup() {
        if (users.isEmpty()) {
            return null;
        }
        return users.get(0).getType();
    }

    public String getTitleGroup(Context mContext) {
        if (users.isEmpty()) {
            return "";
        }
        String title;
        Constants.UserType type = getTypeGroup();
        if (Constants.UserType.CoordinationCenter == type) {
            title = mContext.getString(R.string.coordination_center);
        } else if (Constants.UserType.HealthEstablishment == type) {
            title = mContext.getString(R.string.health_establishment);
        } else if (Constants.UserType.EmergencyGroup == type) {
            title = mContext.getString(R.string.emergency_group);
        } else if (Constants.UserType.TrafficPolice == type) {
            title = mContext.getString(R.string.traffic_police);
        } else {
            title = users.get(0).getUserName();
        }
        return title;
    }

    public String getSnippetGroup(Context mContext) {
        if (users.isEmpty()) {
            return "";
        }
        String snippet;
        Constants.UserType type = getTypeGroup();
        if (Constants.UserType.CoordinationCenter == type) {
            if (users.size() == 1) {
                snippet = String.format("%s (%s)", users.get(0).getUserName(), users.get(0).getPhoneNumber());
            } else {
                snippet = String.format(mContext.getString(R.string.info_msg_002), users.size());
            }
        } else if (Constants.UserType.HealthEstablishment == type) {
            if (users.size() == 1) {
                snippet = String.format("%s (%s)", users.get(0).getUserName(), users.get(0).getPhoneNumber());
            } else {
                snippet = String.format(mContext.getString(R.string.info_msg_002), users.size());
            }
        } else if (Constants.UserType.EmergencyGroup == type) {
            if (users.size() == 1) {
                snippet = String.format("%s (%s)", users.get(0).getUserName(), users.get(0).getPhoneNumber());
            } else {
                snippet = String.format(mContext.getString(R.string.info_msg_002), users.size());
            }
        } else if (Constants.UserType.TrafficPolice == type) {
            if (users.size() == 1) {
                snippet = String.format("%s (%s)", users.get(0).getUserName(), users.get(0).getPhoneNumber());
            } else {
                snippet = String.format(mContext.getString(R.string.info_msg_002), users.size());
            }
        } else {
            snippet = users.get(0).getPhoneNumber();
        }
        return snippet;
    }
}
