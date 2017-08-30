package com.xyz.automate.esos.common;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class Constants {
    public enum TypeUser { CoordinationCenter, HealthEstablishment, EmergencyGroup, TrafficPolice, EndUser}
    public enum SignIn { GOOGLE, FACEBOOK}

    public final static String EMPTY = "";
    public final static String FROM_HOME_TO_LOGIN_FLAG = "FROM_HOME_TO_LOGIN_FLAG";
    public final static String PHONE_NUMBER_KEY = "PhoneNumber";
    public final static String USER_NAME_KEY = "UserName";
    public final static String USER_TYPE_KEY = "UserType";
    public final static String SIGNIN_METHOD_KEY = "SigninMethod";
    public final static String USER_NAME_DEFAULT = "Họ và tên";
}
