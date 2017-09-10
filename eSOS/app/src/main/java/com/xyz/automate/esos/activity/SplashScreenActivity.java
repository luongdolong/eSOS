package com.xyz.automate.esos.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.custom.ProgressInfDialog;

public class SplashScreenActivity extends AppCompatActivity {

    private ProgressInfDialog mProgressBar;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgressBar = new ProgressInfDialog(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mProgressBar.show();
        new AuthenticateFirebaseTask(this).execute();
    }

    private class AuthenticateFirebaseTask extends AsyncTask<Void, Void, FirebaseUser> {
        private Context mContext;

        public AuthenticateFirebaseTask(Context context) {
            mContext = context;
        }

        protected FirebaseUser doInBackground(Void...params) {
            FirebaseUser user = null;

            try {
                // [START initialize_auth]
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                // [END initialize_auth]
            } catch (Exception ex) {
                Log.d("ESOS", ex.getMessage());
            }
            mProgressBar.dismiss();

            return user;
        }

        protected void onPostExecute(FirebaseUser result) {
            if (result == null) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, HomeActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
