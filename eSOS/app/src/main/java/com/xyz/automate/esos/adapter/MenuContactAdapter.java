package com.xyz.automate.esos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xyz.automate.esos.R;
import com.xyz.automate.esos.object.User;

import java.util.ArrayList;

/**
 * Created by luongdolong on 9/8/2017.
 */

public class MenuContactAdapter extends ArrayAdapter<User> {
    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtTel;
        TextView txtUnit;
    }

    private ArrayList<User> dataSet;
    private Context mContext;

    public MenuContactAdapter(ArrayList<User> data, Context context) {
        super(context, R.layout.row_item_contact, data);
        this.dataSet = data;
        this.mContext=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_contact, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.menuNameContact);
            viewHolder.txtTel = (TextView) convertView.findViewById(R.id.menuTelContact);
            viewHolder.txtUnit = (TextView) convertView.findViewById(R.id.menuUnitname);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(dataModel.getUserName());
        viewHolder.txtTel.setText(dataModel.getPhoneNumber());
        viewHolder.txtUnit.setText(dataModel.getUnitName());
        // Return the completed view to render on screen
        return convertView;
    }
}
