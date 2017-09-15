package com.xyz.automate.esos.common;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class Constants {
    //public enum UserType { CoordinationCenter, HealthEstablishment, EmergencyGroup, TrafficPolice, EndUser}
    public static final int CENTER_HOSPITAL = 1;
    public static final int LOCAL_HOSPITAL  = 2;
    public static final int MOBILE_MEDICAL  = 3;
    public static final int POLICEMAN       = 4;
    public static final int END_USER        = 5;
    public static final int UNIT_TYPE_CENTER_HOSPITAL = 1000;
    public static final int UNIT_TYPE_OTHER_HOSPITAL  = 2500;
    public static final int UNIT_TYPE_MOBILE_MEDICAL  = 3000;
    public static final int UNIT_TYPE_POLICEMAN       = 4000;
    public static final int UNIT_TYPE_END_USER        = 5000;
    public enum SignIn { GOOGLE, FACEBOOK}

    public static final int RC_SETTING_SCREEN = 1900;
    public static final int RC_ALLOW_GPS = 1901;
    public static final int RC_ALLOW_CALL = 1902;
    public static final int RC_SIGN_IN = 1903;

    public final static int ONLINE = 1;
    public final static int OFFLINE = 0;
    public final static int ON_SOS = 1;
    public final static int OFF_SOS = 0;
    public final static String EMPTY = "";
    public final static String FROM_HOME_TO_LOGIN_FLAG = "FROM_HOME_TO_LOGIN_FLAG";

    public final static String PHONE_NUMBER_KEY = "PhoneNumber";
    public final static String USER_NAME_KEY = "UserName";
    public final static String USER_AGENT_KEY = "UserAgent";
    public final static String USER_TYPE_KEY = "UserType";
    public final static String USER_HEALTH_INSURANCE_KEY = "UserHealthInsurance";


    public final static String SIGNIN_METHOD_KEY = "SigninMethod";
    public final static String USER_NAME_DEFAULT = "";

    public final static int CALL_OUT = 1;
    public final static int CALL_IN = 0;
    public final static int CALL_SOS = 1;
    public final static int CALL_TEL = 2;
}
