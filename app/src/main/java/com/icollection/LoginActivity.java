package com.icollection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.icollection.modelservice.Authentication;
import com.icollection.modelservice.Order;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.OrderItem_Table;
import com.icollection.tracker.FileDB;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.SharedPreferencesUtil;
import com.icollection.util.Utility;
import com.naa.data.Nson;
import com.naa.data.UtilityAndroid;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    EditText mInputUsername, mInputPassword;
    TextView mSignInButton, mForgotPasswordButton, mCreateAccountButton;

    APIInterfacesRest apiInterface;

    private static final String TAG = "LocationsActivity";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //private static final String START_JOB_BROADCAST_RECEIVER = "com.icollection.intent.action.START_JOB_FIRSTTIME";
    public static final String GET_NEW_LOCATION_BROADCAST_RECEIVER = "com.icollection.intent.action.GET_NEW_LOCATION";
    public static final String GET_NEW_LOCATION_PARAM = "new_location";

    public void checkset(Nson nson, String permisiion){
        if (ActivityCompat.checkSelfPermission(this, permisiion)!= PackageManager.PERMISSION_GRANTED){
            nson.add(permisiion);
        }
    }
    private SharedPreferencesUtil sharedPreferencesUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FileDB.A();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        mInputUsername = (EditText) findViewById(R.id.user_name);
        mInputPassword = (EditText) findViewById(R.id.password);



        Nson nson = Nson.newArray();
        checkset(nson, Manifest.permission.ACCESS_COARSE_LOCATION);
        checkset(nson, Manifest.permission.ACCESS_FINE_LOCATION);
        checkset(nson, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        checkset(nson, Manifest.permission.READ_EXTERNAL_STORAGE);
        checkset(nson, Manifest.permission.READ_PHONE_STATE);

        if (nson.size()>=1){
            ActivityCompat.requestPermissions(this, nson.asStringArray(), 1);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            ((TextView)findViewById(R.id.devid)).setText(AppController.getImei());
        }


        mSignInButton = findViewById(R.id.sign_in_button);
        mForgotPasswordButton =  findViewById(R.id.forgot_pass_button);
        mCreateAccountButton =  findViewById(R.id.sign_up_button);
        mSignInButton.setOnClickListener(this);
        mForgotPasswordButton.setOnClickListener(this);
        mCreateAccountButton.setOnClickListener(this);

        if (Utility.getSetting(this, Utility.MD5("LG"), "").equalsIgnoreCase(Utility.MD5("SI"))){
            sharedPreferencesUtil.setUsername(Utility.getSetting(this, Utility.MD5("US"), ""));
            AppController.setUsername(Utility.getSetting(this, Utility.MD5("US"), ""),Utility.getSetting(this, Utility.MD5("TK"), ""));
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    sendStartJobBroadcast();
                }else{
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                }
            }else{
                sendStartJobBroadcast();
            }
        }
    }

    public static void autoLogout(Activity activity, Response response){
        if (response!=null && response.code()==401){
            Toast.makeText(activity.getApplicationContext(), String.valueOf(response.message()), Toast.LENGTH_LONG).show();
            Utility.setSetting(activity, Utility.MD5("LG"), Utility.MD5("UI") );
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            activity.finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                boolean isEmptyEmail = AppUtil.isEmpty(mInputUsername);
                boolean isEmptyPassword = AppUtil.isEmpty(mInputPassword);
                if (isEmptyEmail) {
                    mInputUsername.setError("Enter your Username!");
                    mInputPassword.setError(null);
                } else if (isEmptyPassword) {
                    mInputPassword.setError("Enter your Password!");
                    mInputUsername.setError(null);
                } else {
                    mInputUsername.setError(null);
                    mInputPassword.setError(null);
                    final String username = mInputUsername.getText().toString().trim();
                    final String password = mInputPassword.getText().toString().trim();

                    List list = SQLite.select(OrderItem_Table.status).from(OrderItem.class).where(OrderItem_Table.status.in("pending_janji","pending_bayar")).queryList();
                    if (list.size()>=1){
                        if ( Utility.getSetting(LoginActivity.this, Utility.MD5("US"), "").equalsIgnoreCase(username)){
                            callAuthentication(username,password);
                        }else{
                            Toast.makeText(LoginActivity.this, "Harus Login dengan user : " +Utility.getSetting(LoginActivity.this, Utility.MD5("US"), ""), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        callAuthentication(username,password);
                    }
                }
                break;

            case R.id.sign_up_button:




                break;

                /*
            case R.id.forgot_pass_button:
                Intent intent1 = new Intent(SignInFourthDesignActivity.this, ResetPasswordFourthDesignActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
            case R.id.sign_up_button:
                Intent intent2 = new Intent(SignInFourthDesignActivity.this, SignUpFourthDesignActivity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
                */
        }
    }

    public static String getVersionCode(Context context){
        String versi = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versi = Utility.getNumberPointOnly(String.valueOf( pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {  }
        return versi;
    }
    ProgressDialog progressDialog;
    public void callAuthentication(final String username, String password){
        Utility.setSetting(LoginActivity.this, Utility.MD5("LG"), Utility.MD5("UI") );
        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        String versi = getVersionCode(this);


        Call<Authentication> call3 =  apiInterface.getAuthentication(AppController.getImei(),username,password, versi);
        call3.enqueue(new Callback<Authentication>() {
            @Override
            public void onResponse(Call<Authentication> call, Response<Authentication> response) {
                progressDialog.dismiss();
                Authentication userList = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();

                if (userList !=null) {
                    if (String.valueOf(userList.getStatus()).equalsIgnoreCase("true")){
                        sharedPreferencesUtil.setUsername(username);

                        AppController.setUsername(username, String.valueOf(userList.getToken()));
                        Utility.setSetting(LoginActivity.this, Utility.MD5("US"), username);//first
                        Utility.setSetting(LoginActivity.this, Utility.MD5("TK"), String.valueOf(userList.getToken()));
                        Utility.setSetting(LoginActivity.this, Utility.MD5("FB"), String.valueOf(userList.getFlag_beban()));


                        if (userList.getMessage()!=null){
                            if (userList.getMessage().trim().length()>=5){
                                Toast.makeText(LoginActivity.this, userList.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        if (String.valueOf(userList.getChangepassword()).equalsIgnoreCase("true")){
                            Intent intent = new Intent(LoginActivity.this,ChangeActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Utility.setSetting(LoginActivity.this, Utility.MD5("LG"), Utility.MD5("SI") );
                            Utility.setSetting(LoginActivity.this, Utility.MD5("NM"), String.valueOf(userList.getNama()));
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    sendStartJobBroadcast();
                                }else{
                                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            }else{
                                sendStartJobBroadcast();
                            }
                        }
                    }else{
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                   // startActivity(new Intent(getApplicationContext(), ListViewNewOrder.class));
                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(LoginActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Authentication> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }

    @Override
    public void onBackPressed() {
        Messagebox.showDialog(LoginActivity.this, "Apakah anda ingin keluar?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if (i == 0) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendStartJobBroadcast();
                }
            }
        }
    }

    private void sendStartJobBroadcast(){
   //     if (sharedPreferencesUtil.getFlagJob()== false) {
            sharedPreferencesUtil.setFlagJob(true);
            /*Intent intent = new Intent();
            intent.setAction(START_JOB_BROADCAST_RECEIVER);
            sendBroadcast(intent);*/
   //     }


        /*Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(LoginActivity.this, "BHC","{}"));
        if (nbhc.isNsonObject()){
            if (nbhc.get("date").asString().equalsIgnoreCase(com.naa.data.Utility.Now().substring(0,10))){
            }else{
                //reset
                //**********************************************BHC***********************************
                nbhc.set("date", com.naa.data.Utility.Now().substring(0,10));
                nbhc.set("data", Nson.newObject());
                UtilityAndroid.setSetting(LoginActivity.this, "BHC",nbhc.toJson());
            }
        }*/
        BHCActivity.resetBHC(LoginActivity.this);
        startActivityForResult(new Intent(getApplicationContext(), ListViewNewOrder.class), 1234);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1234 && resultCode ==RESULT_OK){
            finish();
        }
    }
}
