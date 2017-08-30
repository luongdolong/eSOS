package com.xyz.automate.esos.object;

import com.xyz.automate.esos.common.Constants;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class UserTypeData {
    private Constants.UserType userType;
    private String text;

    public UserTypeData(Constants.UserType userType, String text){
        this.text=text;
        this.userType = userType;
    }

    public String getText(){
        return text;
    }

    public Constants.UserType getTypeUser(){
        return userType;
    }
}
