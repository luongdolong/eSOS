package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.xyz.automate.esos.R;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;

/**
 * Created by LuongDoLong on 8/10/2017.
 */

public class SettingActivity extends Activity {
    public static final int RC_SETTING_SCREEN = 1900;

    private Button btSettingCancel;
    private Button btSettingSave;
    private EditText editUnitname;
    private EditText editPhonenumber;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btSettingCancel = (Button) findViewById(R.id.btSettingCancel);
        btSettingSave = (Button) findViewById(R.id.btSettingSave);
        editUnitname = (EditText) findViewById(R.id.editSettingUnitname);
        editPhonenumber = (EditText) findViewById(R.id.editSettingPhonenumber);

        btSettingCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnMainPage(false);
            }
        });
        btSettingSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    CommonUtils.putPref(SettingActivity.this, Constants.USER_NAME_KEY, editUnitname.getText().toString().trim());
                    CommonUtils.putPref(SettingActivity.this, Constants.PHONE_NUMBER_KEY, editPhonenumber.getText().toString().trim());
                    Toast.makeText(SettingActivity.this, "Thành công!", Toast.LENGTH_SHORT).show();
                    returnMainPage(true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editUnitname.setText(CommonUtils.getPrefString(this, Constants.USER_NAME_KEY));
        editPhonenumber.setText(CommonUtils.getPrefString(this, Constants.PHONE_NUMBER_KEY));
    }

    private boolean validate() {
        if (CommonUtils.isEmpty(editUnitname.getText().toString().trim())) {
            showMessage("Hãy nhập tên!");
            return false;
        }
        if (CommonUtils.isEmpty(editPhonenumber.getText().toString().trim())) {
            showMessage("Hãy nhập số điện thoại!");
            return false;
        }
        return true;
    }

    private void showMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void returnMainPage(boolean flagOK) {
        Intent returnIntent = new Intent();
        if (flagOK) {
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        finish();
    }
}

