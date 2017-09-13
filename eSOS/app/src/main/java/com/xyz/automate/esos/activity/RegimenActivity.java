package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.custom.ProgressInfDialog;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by LuongDoLong on 9/13/2017.
 */

public class RegimenActivity extends Activity {

    private Button btCancel;
    private TextView tvContentRegimen;
    private ProgressInfDialog mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regimen);
        btCancel = (Button) findViewById(R.id.btRegimenCancel);
        tvContentRegimen = (TextView) findViewById(R.id.tvContentRegimen);
        mProgressBar = new ProgressInfDialog(this);
        btCancel.setOnClickListener(new View.OnClickListener() {
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
        new LoadContentRegimenTask(this).execute();
    }

    private class LoadContentRegimenTask extends AsyncTask<Void, Void, String> {
        private Context mContext;

        public LoadContentRegimenTask(Context context) {
            mContext = context;
        }

        protected String doInBackground(Void...params) {
            InputStream is = mContext.getResources().openRawResource(R.raw.regimen);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            String result = "";
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
                result = writer.toString();
            }  catch (Exception ex) {
                Log.d("ESOS", ex.toString());
            } finally {
                try {
                    is.close();
                } catch (Exception ex) {
                    Log.d("ESOS", ex.toString());
                }
            }
            return result;
        }

        protected void onPostExecute(String result) {
            mProgressBar.dismiss();
            tvContentRegimen.setText(result);
        }
    }
}