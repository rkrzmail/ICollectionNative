package com.icollection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icollection.modelservice.Authentication;
import com.icollection.modelservice.ChangeResult;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.SharedPreferencesUtil;
import com.icollection.util.Utility;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String LOG_TAG = ChangeActivity.class.getSimpleName();
    EditText mInputPasswordNew, mInputPassword;
    TextView mChangeButton;

    APIInterfacesRest apiInterface;

    private static final String TAG = "LocationsActivity";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //rivate static final String START_JOB_BROADCAST_RECEIVER = "com.icollection.intent.action.START_JOB_FIRSTTIME";
    public static final String GET_NEW_LOCATION_BROADCAST_RECEIVER = "com.icollection.intent.action.GET_NEW_LOCATION";
    public static final String GET_NEW_LOCATION_PARAM = "new_location";

    private SharedPreferencesUtil sharedPreferencesUtil;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        mInputPasswordNew = (EditText) findViewById(R.id.passwordNew);
        mInputPassword = (EditText) findViewById(R.id.password);

        if (ActivityCompat.checkSelfPermission(ChangeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChangeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ChangeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //Utility.getSetting()

        mChangeButton = findViewById(R.id.change_button);

        mChangeButton.setOnClickListener(this);

     //   mInputUsername.setText("BT.386");
     //   mInputPassword.setText("123456");

        if (Utility.getSetting(this, Utility.MD5("LG"), "").equalsIgnoreCase(Utility.MD5("SI"))){
            sharedPreferencesUtil.setUsername(Utility.getSetting(this, Utility.MD5("US"), ""));
            AppController.setUsername(Utility.getSetting(this, Utility.MD5("US"), ""),Utility.getSetting(this, Utility.MD5("TK"), ""));

        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_button:
                boolean isEmptyEmail = AppUtil.isEmpty(mInputPasswordNew);
                boolean isEmptyPassword = AppUtil.isEmpty(mInputPassword);
                boolean isEqual = AppUtil.isEqual(mInputPasswordNew, mInputPassword);
                if (isEmptyEmail) {
                    mInputPasswordNew.setError("Enter your Password!");
                    mInputPassword.setError(null);
                } else if (isEmptyPassword) {
                    mInputPassword.setError("Enter your Password!");
                    mInputPasswordNew.setError(null);
                } else if (!isEqual) {
                    mInputPassword.setError("New Password not Equal");
                    mInputPasswordNew.setError("New Password not Equal");
                } else {
                    mInputPasswordNew.setError(null);
                    mInputPassword.setError(null);
                    final String username = Utility.getSetting(this, Utility.MD5("US"), "");
                    final String password = mInputPassword.getText().toString().trim();
                    callAuthenticationChange(username, password);
                }
                break;


        }
    }
    ProgressDialog progressDialog;
    public void callAuthenticationChange(final String username, String password){
        Utility.setSetting(ChangeActivity.this, Utility.MD5("LG"), Utility.MD5("UI") );
        apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        progressDialog = new ProgressDialog(ChangeActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Call<ChangeResult> call3 =  apiInterface.getChangePassword(AppController.getImei(), AppController.getToken(), username,password,password);
        call3.enqueue(new Callback<ChangeResult>() {
            @Override
            public void onResponse(Call<ChangeResult> call, Response<ChangeResult> response) {
                progressDialog.dismiss();
                ChangeResult userList = response.body();
                //Toast.makeText(LoginActivity.this,userList.getToken().toString(),Toast.LENGTH_LONG).show();
                if (userList !=null) {
                    if (String.valueOf(userList.getStatus()).equalsIgnoreCase("true")){
                        AlertDialog.Builder dlg = new AlertDialog.Builder(ChangeActivity.this);
                        dlg.setCancelable(false);
                        dlg.setMessage("Password berhasil di update");
                        dlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Utility.setSetting(ChangeActivity.this, Utility.MD5("LG"), Utility.MD5("UI") );

                                Intent intent = new Intent(ChangeActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dlg.create().show();
                    }else{
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(ChangeActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(ChangeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                   // startActivity(new Intent(getApplicationContext(), ListViewNewOrder.class));
                }else{

                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(ChangeActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(ChangeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ChangeResult> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Maaf koneksi bermasalah",Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });




    }

    @Override
    public void onBackPressed() {
        Messagebox.showDialog(ChangeActivity.this, "Apakah anda ingin keluar?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
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
        BHCActivity.resetBHC(ChangeActivity.this);
        startActivity(new Intent(getApplicationContext(), ListViewNewOrder.class));

    }
}
