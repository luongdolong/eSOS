package com.xyz.automate.esos.logic;

import android.content.Context;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.xyz.automate.esos.common.DateUtils;
import com.xyz.automate.esos.logic.model.CallDataModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by luongdolong on 11/7/2016.
 */
public class DataManager {

    private static DataManager instance;

    public static DataManager getInstance() {
        if (instance == null) throw new RuntimeException("Reference to SharedDataObject was null");
        return instance;
    }

    public static DataManager createInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        return instance = new DataManager(context.getApplicationContext());
    }

    public DataManager(Context context) {

    }

    public void insertCallData(CallDataModel model) {
        deleteCallDataExpire();
        model.save();
    }

    public List<CallDataModel> getCallData(String id) {
        List<CallDataModel> resultSet = new Select().from(CallDataModel.class)
                .where("sender = ?", id)
                .orderBy("time DESC")
                .execute();
        return resultSet;
    }

    public void deleteCallDataExpire() {
        Date d = DateUtils.addDate(Calendar.getInstance().getTime(), -30);
        new Delete().from(CallDataModel.class)
                .where("time < ?", d.getTime())
                .execute();
    }

}
