package com.icollection;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.icollection.modelservice.News;
import com.icollection.modelservice.OrderItem;
import com.icollection.tracker.DB;
import com.icollection.tracker.FileDB;
import com.icollection.util.Utility;
import com.naa.data.UtilityAndroid;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Mounzer on 8/22/2017.
 */
@Database(name = AppController.NAME, version = AppController.VERSION )

public class AppController extends Application {

    public static final String NAME = "ICollectionV3";
    public static final int VERSION = 11;//naik dari 8

    public static final String TAG = AppController.class.getSimpleName();
    private List<OrderItem> favorites = new ArrayList<>();
    public static int REQUEST_DETAIL = 1;
    public static int REQUEST_TK_SEMPAT = 24242;
    public static int CLOSE_CODE = 99;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;

    public static String getUsername() {
        return String.valueOf(username);
    }
    public static String getToken() {
        return String.valueOf(token);
    }
    public static void setUsername(String username, String token) {
        AppController.username = username;
        AppController.token = token;
    }
    public static String getImei() {
        /*TelephonyManager mTelephonyMgr = (TelephonyManager) appController.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(appController, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }else{
            try {
                return String.valueOf(mTelephonyMgr.getDeviceId()) ;
            }catch (Exception e){
                return "";
            }
        }*/
        String devid = getDeviceId(appController);
        return devid;
    }
    public static String getDeviceId(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            try {
                String s = String.valueOf(((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getImei()).toUpperCase(Locale.ENGLISH);
                return s.equalsIgnoreCase("NULL")?deviceId:s;
            }catch (Exception e){}
            return deviceId;
        } else{
            return String.valueOf(((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()).toUpperCase(Locale.ENGLISH);
        }
    }

    public static String username;
    public static String token;

    //public static int IMAGE_REQUEST = 7;


    public static String getRekap() {
        return rekap;
    }

    public static void setRekap(String rekap) {
        AppController.rekap = rekap;
    }

    private static String rekap ="bayar";


    public static String getMode() {
        return mode;
    }

    public static void setMode(String mode) {
        AppController.mode = mode;
    }

    public static String mode ="new";

    private static AppController appController;

    public static AppController getInstanceA(){
        return appController;
    }
    public final int version = 10;
    public static OrderItem currentOrder;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        FlowManager.init(new FlowConfig.Builder(this).build());
   /*     if (com.naa.data.UtilityAndroid.getSetting(this, "_app", "").equalsIgnoreCase("")){
            //restore
            DB.restore(this);
            //
        }
        com.naa.data.UtilityAndroid.setSetting(this, "_app", "true");
*/


        String cVer = UtilityAndroid.getSetting(this,"Version", "");
        if (Utility.getInt(cVer) < version){
            UtilityAndroid.clearSetting(this);
            UtilityAndroid.setSetting(this, "Version", String.valueOf(version));
            FileDB.clear(this,"dbhc");


        }else{
            String day = UtilityAndroid.getSetting(this,"day", "");
            if (!day.equalsIgnoreCase(Utility.Date())){
                UtilityAndroid.setSetting(this, "day", Utility.Date());
                DB.restore(this);
            }
        }

        DB.restore(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);

        //new FlowConfig.Builder(this).addDatabaseConfig()


        /*Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {

            }
        });*/




        this.appController = this;

        DatabaseDefinition db = FlowManager.getDatabase(AppController.class);
        Log.i("DBDatabase", db.getDatabaseFileName());
        Log.i("DBDatabase", db.getDatabaseName());
        Log.i("DBDatabase", FlowManager.getContext().getDatabasePath(AppController.NAME).getAbsolutePath() );
            ///data/user/0/com.icollection/databases/ICollection
        AppController.setUsername(Utility.getSetting(this, Utility.MD5("US"), ""),Utility.getSetting(this, Utility.MD5("TK"), ""));
      }

    public void addToFavorites(OrderItem order) {
        this.favorites.add(order);
    }
    public void removeFromFavorites(OrderItem order) {
        for (int i=0; i<favorites.size(); i++){
            if(favorites.get(i).getNama().equalsIgnoreCase(order.getNama())){
                this.favorites.remove(i);
            }
        }
    }
    public boolean isAddedToFavorites(OrderItem order) {
        for (OrderItem a : favorites){
            if(a.getNama().equalsIgnoreCase(order.getNama())){
                return true;
            }
        }
        return false;
    }
    public static AppController getInstance(){
        return appController;
    }
    Handler lochandler;
    public Handler getLocationHandler(){
        return lochandler;
    }
    Handler currhandler;

}
