package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.CallLogListAdapter;
import com.xyz.automate.esos.custom.ProgressInfDialog;
import com.xyz.automate.esos.logic.DataManager;
import com.xyz.automate.esos.logic.model.CallDataModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luongdolong on 9/12/2017.
 */

public class CallLogActivity extends Activity {
    private ProgressInfDialog mProgressBar;
    private ListView listLog;
    private Button btCalllogCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        btCalllogCancel = (Button) findViewById(R.id.btCalllogCancel);
        listLog = (ListView) findViewById(R.id.listCallLog);
        mProgressBar = new ProgressInfDialog(this);
        btCalllogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressBar.show();
        new LoadCallLogTask(this).execute();
    }

    private class LoadCallLogTask extends AsyncTask<Void, Void, ArrayList<CallDataModel>> {
        private Context mContext;

        public LoadCallLogTask(Context context) {
            mContext = context;
        }

        protected ArrayList<CallDataModel> doInBackground(Void...params) {

            ArrayList<CallDataModel> result = DataManager.getInstance().getCallData(ESoSApplication.getInstance().uDiD());
            return result;
        }

        protected void onPostExecute(ArrayList<CallDataModel> result) {
            CallLogListAdapter adapter = new CallLogListAdapter(result, mContext);
            listLog.setAdapter(adapter);
            mProgressBar.dismiss();
        }
    }
}
