package com.xyz.automate.esos.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xyz.automate.esos.ESoSApplication;
import com.xyz.automate.esos.R;
import com.xyz.automate.esos.adapter.SpinnerTypeUserAdapter;
import com.xyz.automate.esos.common.CommonUtils;
import com.xyz.automate.esos.common.Constants;
import com.xyz.automate.esos.custom.ProgressInfDialog;
import com.xyz.automate.esos.object.MedicalAgent;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by LuongDoLong on 8/28/2017.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private EditText edPhoneNumber;
    private EditText edFullname;
    private Spinner objectTypeSpinner;
    private SignInButton googleSignIn;
    private LoginButton facebookSignIn;

    private ProgressInfDialog mProgressBar;
    private CallbackManager mCallbackManager;

    private GoogleApiClient mGoogleApiClient;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private ArrayList<MedicalAgent> listMedicalUser = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_login);
        objectTypeSpinner = (Spinner)findViewById(R.id.objectTypeSpinner);
        edPhoneNumber = (EditText)findViewById(R.id.edPhoneNumber);
        edFullname = (EditText)findViewById(R.id.edFullname);
        googleSignIn = (SignInButton)findViewById(R.id.google_button);
        facebookSignIn = (LoginButton) findViewById(R.id.loginFBbutton);
        mAuth = FirebaseAuth.getInstance();

        initControl();
        Bundle extra = getIntent().getExtras();
        if (extra != null && mGoogleApiClient != null && mGoogleApiClient.isConnected() &&
                Constants.FROM_HOME_TO_LOGIN_FLAG.equals(extra.getString(Constants.FROM_HOME_TO_LOGIN_FLAG)) ) {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            Log.d("ESOS", status.getStatusMessage());
                        }
                    });
        }
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
           if (requestCode == Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showMessage("Đăng nhập thất bại!!!");
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void initControl() {
        mProgressBar = new ProgressInfDialog(this);
        loadUserInfo();
        edFullname.setText(CommonUtils.getPrefString(this, Constants.USER_NAME_KEY));
        edPhoneNumber.setText(CommonUtils.getPrefString(this, Constants.PHONE_NUMBER_KEY));

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
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    return;
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
            }
        });

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        facebookSignIn.setReadPermissions("email", "public_profile");
        facebookSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (!validate()) {
                    LoginManager.getInstance().logOut();
                    return;
                }
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                showMessage("Đăng nhập thất bại!!!");
            }
        });
        mProgressBar.show();
        new LoadMedicalAgentTask(this).execute();
    }

    private void initUsertype() {

        listMedicalUser.clear();
        listMedicalUser.addAll(CommonUtils.getFixHospital(this));
        MedicalAgent agent = new MedicalAgent();
        agent.setAgent(Constants.LOCAL_HOSPITAL);
        agent.setUnitType(Constants.UNIT_TYPE_OTHER_HOSPITAL);
        agent.setUnitName(getString(R.string.other_medical));
        listMedicalUser.add(agent);

        agent = new MedicalAgent();
        agent.setAgent(Constants.MOBILE_MEDICAL);
        agent.setUnitType(Constants.UNIT_TYPE_MOBILE_MEDICAL);
        agent.setUnitName(getString(R.string.emergency_group));
        listMedicalUser.add(agent);

        agent = new MedicalAgent();
        agent.setAgent(Constants.POLICEMAN);
        agent.setUnitType(Constants.UNIT_TYPE_POLICEMAN);
        agent.setUnitName(getString(R.string.traffic_police));
        listMedicalUser.add(agent);

        agent = new MedicalAgent();
        agent.setAgent(Constants.END_USER);
        agent.setUnitType(Constants.UNIT_TYPE_END_USER);
        agent.setUnitName(getString(R.string.end_user));
        listMedicalUser.add(agent);

        SpinnerTypeUserAdapter adapter = new SpinnerTypeUserAdapter(this, R.layout.layout_spinner_type_user, R.id.txtSpnNameTypeUser, listMedicalUser);
        objectTypeSpinner.setAdapter(adapter);
        for(int i = 0; i < listMedicalUser.size(); i++) {
            if (CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY) == listMedicalUser.get(i).getUnitType() &&
                    CommonUtils.getPrefInteger(ESoSApplication.getInstance(), Constants.USER_AGENT_KEY) == listMedicalUser.get(i).getAgent()) {
                objectTypeSpinner.setSelection(i);
                break;
            }
        }
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
        mProgressBar.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            CommonUtils.putPref(ESoSApplication.getInstance(), Constants.SIGNIN_METHOD_KEY, Constants.SignIn.GOOGLE.ordinal());
                            signInSuccessfull();
                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("Đăng nhập thất bại!!!");
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("ESOS", "handleFacebookAccessToken:" + token);
        mProgressBar.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mProgressBar.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            CommonUtils.putPref(ESoSApplication.getInstance(), Constants.SIGNIN_METHOD_KEY, Constants.SignIn.FACEBOOK.ordinal());
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
        CommonUtils.putPref(ESoSApplication.getInstance(), Constants.USER_TYPE_KEY, listMedicalUser.get(objectTypeSpinner.getSelectedItemPosition()).getUnitType());
        CommonUtils.putPref(ESoSApplication.getInstance(), Constants.USER_AGENT_KEY, listMedicalUser.get(objectTypeSpinner.getSelectedItemPosition()).getAgent());
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

    private class LoadMedicalAgentTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;

        public LoadMedicalAgentTask(Context context) {
            mContext = context;
        }

        protected Void doInBackground(Void...params) {
            CommonUtils.getFixHospital(mContext);
            return null;
        }

        protected void onPostExecute(Void result) {
            initUsertype();
            mProgressBar.dismiss();
        }
    }
}
