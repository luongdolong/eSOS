package com.xyz.automate.esos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyz.automate.esos.R;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.common.DateUtils;
import com.xyz.automate.esos.logic.model.CallDataModel;

import java.util.ArrayList;

/**
 * Created by luongdolong on 9/12/2017.
 */

public class CallLogListAdapter extends ArrayAdapter<CallDataModel> {
    // View lookup cache
    private static class ViewHolder {
        ImageView imgTypeCall;
        TextView tvCallTime;
        TextView tvCallName;
        TextView tvCallTel;
        ImageView logoCallReceiver;
        TextView tvCallUnitName;
    }

    private ArrayList<CallDataModel> dataSet;
    private Context mContext;

    public CallLogListAdapter(ArrayList<CallDataModel> data, Context context) {
        super(context, R.layout.row_item_call_log, data);
        this.dataSet = data;
        this.mContext = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        CallDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        CallLogListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new CallLogListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_call_log, parent, false);
            viewHolder.imgTypeCall = (ImageView) convertView.findViewById(R.id.imgTypeCall);
            viewHolder.tvCallTime = (TextView) convertView.findViewById(R.id.tvCallTime);
            viewHolder.tvCallName = (TextView) convertView.findViewById(R.id.tvCallName);
            viewHolder.tvCallTel = (TextView) convertView.findViewById(R.id.tvCallTel);
            viewHolder.tvCallUnitName = (TextView) convertView.findViewById(R.id.tvCallUnitName);
            viewHolder.logoCallReceiver = (ImageView) convertView.findViewById(R.id.logoCallReceiver);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CallLogListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.tvCallTime.setText(DateUtils.convertDateCallLog(dataModel.time));
        if (Constants.CALL_SOS == dataModel.kind) {
            viewHolder.imgTypeCall.setImageResource(R.drawable.ic_siren48_pink);
            viewHolder.tvCallName.setText("Gọi cấp cứu");
            viewHolder.tvCallTel.setText("");
            viewHolder.tvCallUnitName.setText("");
            viewHolder.logoCallReceiver.setVisibility(View.INVISIBLE);
        } else {
            if (Constants.CALL_IN == dataModel.type) {
                viewHolder.imgTypeCall.setImageResource(R.drawable.ic_down_left48);
            } else if (Constants.CALL_OUT == dataModel.type) {
                viewHolder.imgTypeCall.setImageResource(R.drawable.ic_up_right48);
            }
            viewHolder.tvCallName.setText(dataModel.userName);
            viewHolder.tvCallTel.setText(dataModel.tel);
            viewHolder.logoCallReceiver.setVisibility(View.VISIBLE);
            if (Constants.CENTER_HOSPITAL == dataModel.agent) {
                viewHolder.logoCallReceiver.setImageResource(R.mipmap.ic_hospital_center);
                viewHolder.tvCallUnitName.setText(dataModel.unitName);
            } else if (Constants.LOCAL_HOSPITAL == dataModel.agent) {
                viewHolder.logoCallReceiver.setImageResource(R.mipmap.ic_medical_bag);
                viewHolder.tvCallUnitName.setText(dataModel.unitName);
            } else if (Constants.MOBILE_MEDICAL == dataModel.agent) {
                viewHolder.logoCallReceiver.setImageResource(R.mipmap.ic_ambulance);
                viewHolder.tvCallUnitName.setText(mContext.getString(R.string.emergency_group));
            } else if (Constants.POLICEMAN == dataModel.agent) {
                viewHolder.logoCallReceiver.setImageResource(R.mipmap.ic_policeman);
                viewHolder.tvCallUnitName.setText(mContext.getString(R.string.traffic_police));
            } else if (Constants.END_USER == dataModel.agent) {
                viewHolder.logoCallReceiver.setImageResource(R.mipmap.ic_user_avatar);
                viewHolder.tvCallUnitName.setText(mContext.getString(R.string.end_user));
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
