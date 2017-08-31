package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.xyz.automate.esos.R;

/**
 * Created by luongdolong on 8/31/2017.
 */

public class AboutActivity extends Activity {

    private Button btCancel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        btCancel = (Button) findViewById(R.id.btAboutCancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
