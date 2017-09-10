package com.xyz.automate.esos.object;

/**
 * Created by LuongDoLong on 9/4/2017.
 */

public class User {
    private String userId;
    private int agent;
    private int type;
    private String unitName;
    private String userName;
    private String phoneNumber;
    private double lng;
    private double lat;
    private int status; // 0: offline, 1: online
    private boolean me;
    private int sos;    // 0: normal,  1: call sos
    private String objective;
    private String sostime; //yyyyMMddHHmmss
    private String lastupdate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAgent() {
        return agent;
    }

    public void setAgent(int agent) {
        this.agent = agent;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public int getSos() {
        return sos;
    }

    public void setSos(int sos) {
        this.sos = sos;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getSostime() {
        return sostime;
    }

    public void setSostime(String sostime) {
        this.sostime = sostime;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }
}
