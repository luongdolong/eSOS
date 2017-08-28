package com.xyz.automate.esos.activity;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.xyz.automate.esos.R;

public class SplashScreenActivity extends AppCompatActivity {

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseUser user = null;

        try {
            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
        } catch (Exception ex) {
            Log.d("ESOS", ex.getMessage());
        }
        // [END initialize_auth]
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
