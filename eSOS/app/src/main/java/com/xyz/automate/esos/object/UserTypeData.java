package com.xyz.automate.esos.object;

import com.xyz.automate.esos.common.Constants;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class UserTypeData {
    private Constants.TypeUser typeUser;
    private String text;

    public UserTypeData(Constants.TypeUser typeUser, String text){
        this.text=text;
        this.typeUser = typeUser;
    }

    public String getText(){
        return text;
    }

    public Constants.TypeUser getTypeUser(){
        return typeUser;
    }
}
