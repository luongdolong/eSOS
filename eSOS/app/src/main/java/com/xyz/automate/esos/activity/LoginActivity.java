package com.xyz.automate.esos.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import android.Manifest;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.SpinnerTypeUserAdapter;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.object.UserTypeData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;

    private EditText edPhoneNumber;
    private EditText edFullname;
    private Spinner objectTypeSpinner;
    private SignInButton googleSignIn;
    private LoginButton loginButton;
    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        objectTypeSpinner = (Spinner)findViewById(R.id.objectTypeSpinner);
        edPhoneNumber = (EditText)findViewById(R.id.edPhoneNumber);
        edFullname = (EditText)findViewById(R.id.edFullname);
        googleSignIn = (SignInButton)findViewById(R.id.google_button);
        mAuth = FirebaseAuth.getInstance();

        initControl();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.loginFBbutton);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 120:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String mPhoneNumber = "";
                    try {
                        TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                        mPhoneNumber = tMgr.getLine1Number();
                    } catch (Exception ex) {
                        Log.d("ESOS", ex.getMessage());
                    }
                    if (!CommonUtils.isEmpty(mPhoneNumber)) {
                        CommonUtils.putPref(this, Constants.PHONE_NUMBER_KEY, mPhoneNumber);
                    }
                    edPhoneNumber.setText(mPhoneNumber);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showMessage("Đăng nhập thất bại!!!");
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initControl() {
        loadUserInfo();
        edFullname.setText(CommonUtils.getPrefString(this, Constants.USER_NAME_KEY));
        edPhoneNumber.setText(CommonUtils.getPrefString(this, Constants.PHONE_NUMBER_KEY));

        ArrayList<UserTypeData> list = new ArrayList<>();
        list.add(new UserTypeData(Constants.TypeUser.CoordinationCenter, getString(R.string.coordination_center)));
        list.add(new UserTypeData(Constants.TypeUser.HealthEstablishment, getString(R.string.health_establishment)));
        list.add(new UserTypeData(Constants.TypeUser.EmergencyGroup, getString(R.string.emergency_group)));
        list.add(new UserTypeData(Constants.TypeUser.TrafficPolice, getString(R.string.traffic_police)));
        list.add(new UserTypeData(Constants.TypeUser.EndUser, getString(R.string.end_user)));
        SpinnerTypeUserAdapter adapter = new SpinnerTypeUserAdapter(this, R.layout.layout_spinner_type_user, R.id.txtSpnNameTypeUser, list);
        objectTypeSpinner.setAdapter(adapter);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validate()) {
                    return;
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void loadUserInfo() {
        boolean allow = true;
        if (CommonUtils.isEmpty(CommonUtils.getPrefString(this, Constants.PHONE_NUMBER_KEY))) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_PHONE_STATE)) {
                        allow = false;
                    } else {
                        allow = false;
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 120);
                    }
                }
            } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                allow = false;
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 120);
            }
            String mPhoneNumber = "";
            if (allow) {
                try {
                    TelephonyManager tMgr = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                    mPhoneNumber = tMgr.getLine1Number();

                } catch (Exception ex) {
                    Log.d("ESOS", ex.getMessage());
                }
            }
            if (!CommonUtils.isEmpty(mPhoneNumber)) {
                CommonUtils.putPref(this, Constants.PHONE_NUMBER_KEY, mPhoneNumber);
            }
        }

        if (CommonUtils.isEmpty(CommonUtils.getPrefString(this, Constants.USER_NAME_KEY))) {
            CommonUtils.putPref(this, Constants.USER_NAME_KEY, Constants.USER_NAME_DEFAULT);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("ESOS", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            signInSuccessfull();
                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("Đăng nhập thất bại!!!");
                        }
                    }
                });
    }

    private void signInSuccessfull() {
        CommonUtils.putPref(ESoSApplication.getInstance(), Constants.PHONE_NUMBER_KEY, edPhoneNumber.getText());
        CommonUtils.putPref(ESoSApplication.getInstance(), Constants.USER_NAME_KEY, edFullname.getText());
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean validate() {
        if (CommonUtils.isEmpty(edFullname.getText().toString().trim())) {
            showMessage("Hãy nhập tên!");
            return false;
        }
        if (CommonUtils.isEmpty(edPhoneNumber.getText().toString().trim())) {
            showMessage("Hãy nhập số điện thoại!");
            return false;
        }
        return true;
    }

    private void showMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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
}
