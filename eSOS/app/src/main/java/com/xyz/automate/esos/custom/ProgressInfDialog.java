package com.xyz.automate.esos.custom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.xyz.automate.esos.R;

/**
 * Created by luongdolong on 8/29/2017.
 */

public class ProgressInfDialog extends Dialog {
    /**
     * Constructor
     *
     * @param context This is context of view
     */
    public ProgressInfDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);

        getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        setContentView(R.layout.layout_progress_dialog);
    }
}
