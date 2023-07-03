package com.icollection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.icollection.util.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    private LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;
    private double gpsLive =0;
    Location currlocation;
    private long ob = 0;
    public Activity getActivity(){
        return this;
    }

    public void waitGPS(){
        //wailt for gps
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setExpirationDuration(6000);// 1 menit
        if (mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Maaf, permision GPS ditolak", Toast.LENGTH_LONG).show();
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currlocation = location;
                    exitIfMockLocationOn(location, getActivity());
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Maaf, permision Storage ditolak", Toast.LENGTH_LONG).show();
            return;
        }

        gpsLive = System.currentTimeMillis();
        mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        currlocation = location;

                        if (Math.abs(System.currentTimeMillis() - gpsLive) > 30000) {
                            if (mLocationCallback != null && mFusedLocationClient != null) {
                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                            }
                        }

                        exitIfMockLocationOn(location, getActivity());
                    }
                }
            }

            ;
        };
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                mLocationCallback,
                null /* Looper */);



        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Mohon menunggu, sedang mencari lokasi GPS ...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mProgressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                Location location = null;
                ob = System.currentTimeMillis();
                while (Math.abs(System.currentTimeMillis() - ob) < 10000) {
                    //ob = System.currentTimeMillis();
                    try {
                        Thread.sleep(1000);//1 detik
                    } catch (Exception e) {
                    }
                    location = currlocation;
                    try {
                        if (location != null) {
                            if (location.getAccuracy() <= 100) {
                                break;
                            }
                        }
                    } catch (Exception e) {
                    }
                }


                final Location loc1 = location;
                runOnUiThread(new Runnable() {
                    public void run() {
                        exitIfMockLocationOn(loc1, getActivity());
                        if (loc1!=null){
                            Utility.setSetting(getActivity(), "GPS", loc1.getLatitude() + "," + loc1.getLongitude());
                        }
                        mProgressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    public static void exitApp(Activity activity){
        activity.finishAffinity();
        System.exit(0);
    }
    public static void exitIfMockLocationOn(Location location, Activity activity) {
        if (location == null){
            if (isMockSettingsON(activity)) {
                if (areThereMockPermissionApps(activity)){
                    Toast.makeText(activity, "Face Gps Found!", Toast.LENGTH_LONG).show();
                    exitApp(activity);

                }else{
                    Toast.makeText(activity, "MockSettingsON, berpotensi menggunkan Face Gps!", Toast.LENGTH_LONG).show();
                    exitApp(activity);
                }
            }
        }else if (isMockLocationOn(location, activity )){
            Toast.makeText(activity, "Face Gps Found!.", Toast.LENGTH_LONG).show();
            exitApp(activity);
        }
    }
    public static boolean isMockLocationOn(Location location, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            boolean b =location.isFromMockProvider();
            return b;
        } else {
            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }
    }

    public static boolean isMockSettingsON(Context context) {
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
            return false;
        else
            return true;
    }

    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());

            }
        }

        if (count > 0)
            return true;
        return false;
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3() || isRooted();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[] { "/system/xbin/which", "su" });
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static boolean isRooted(Context context) {
        boolean isEmulator = isEmulator(context);
        String buildTags = Build.TAGS;
        if (!isEmulator && buildTags != null && buildTags.contains("test-keys")) {
            return true;
        } else {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            } else {
                file = new File("/system/xbin/su");
                return !isEmulator && file.exists();
            }
        }
    }

    public static boolean isRooted(){
        String commandToExecute = "su";
        return executeShellCommand(commandToExecute);
    }

    private static boolean executeShellCommand(String command){
        Process process = null;
        try{
            process = Runtime.getRuntime().exec(command);
            return true;
        } catch (Exception e) {
            return false;
        } finally{
            if(process != null){
                try{
                    process.destroy();
                }catch (Exception e) {
                }
            }
        }
    }
    private static boolean isEmulator(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), "android_id");
        return "sdk".equals(Build.PRODUCT) || "google_sdk".equals(Build.PRODUCT) || androidId == null;
    }
}
