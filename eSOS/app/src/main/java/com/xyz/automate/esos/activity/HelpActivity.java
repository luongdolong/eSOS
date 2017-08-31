package com.xyz.automate.esos.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.xyz.automate.esos.R;

/**
 * Created by luongdolong on 8/31/2017.
 */

public class HelpActivity extends Activity {

    private Button btCancel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        btCancel = (Button) findViewById(R.id.btHelpCancel);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
