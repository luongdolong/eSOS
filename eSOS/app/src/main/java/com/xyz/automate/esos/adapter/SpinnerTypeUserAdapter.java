package com.xyz.automate.esos.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyz.automate.esos.R;
import com.xyz.automate.esos.object.UserTypeData;

import java.util.ArrayList;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class SpinnerTypeUserAdapter extends ArrayAdapter<UserTypeData> {

    private int groupid;
    private Activity context;
    private ArrayList<UserTypeData> list;
    private LayoutInflater inflater;

    public SpinnerTypeUserAdapter(Activity context, int groupid, int id, ArrayList<UserTypeData> list){
        super(context,id,list);
        this.list = list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent ){
        View itemView=inflater.inflate(groupid,parent,false);
        ImageView imageView=(ImageView)itemView.findViewById(R.id.imgSpnLogoTypeUser);
        switch (list.get(position).getTypeUser()) {
            case CoordinationCenter:
                imageView.setImageResource(R.mipmap.ic_hospital_center);
                break;
            case HealthEstablishment:
                imageView.setImageResource(R.mipmap.ic_medical_bag);
                break;
            case EmergencyGroup:
                imageView.setImageResource(R.mipmap.ic_ambulance);
                break;
            case TrafficPolice:
                imageView.setImageResource(R.mipmap.ic_policeman);
                break;
            case EndUser:
                imageView.setImageResource(R.mipmap.ic_user_avatar);
                break;
            default:
                imageView.setImageResource(R.mipmap.ic_user_avatar);
                break;
        }
        TextView textView=(TextView)itemView.findViewById(R.id.txtSpnNameTypeUser);
        textView.setText(list.get(position).getText());
        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent){
        return getView(position,convertView,parent);
    }
}
