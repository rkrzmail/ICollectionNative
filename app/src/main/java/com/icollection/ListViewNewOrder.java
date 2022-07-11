package com.icollection;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.icollection.adapter.ListViewMenuNewOrderAdapter;
import com.icollection.fragment.ListViewNewOrderFragment;
import com.icollection.fragment.ListViewRekapBayarFragment;
import com.icollection.fragment.Report2Fragment;
import com.icollection.fragment.Report3Fragment;
import com.icollection.fragment.ReportFragment;
import com.icollection.modelservice.MenuItems;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.OrderItem_Table;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.tracker.DB;
import com.icollection.tracker.GpsTrackerIntentService;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;

import com.naa.data.Nson;
import com.naa.data.UtilityAndroid;
import com.naa.utils.InternetX;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;


import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icollection.util.AppUtil.NowX;

/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewNewOrder extends AppCompatActivity implements ListViewNewOrderFragment.OnItemSelectedListener,ListViewRekapBayarFragment.OnItemSelectedListener {
    private static final String LOG_TAG = ListViewNewOrder.class.getSimpleName();
    SlidingPaneLayout mSlidingPanel;
    ListView mMenuList;
    ArrayList<MenuItems> menuItems;
    ListViewMenuNewOrderAdapter listViewMenuNewOrderAdapter;
    boolean isTwoPane = false;
    private Fragment contentFragment;
    FrameLayout frameLayout;
    ListViewNewOrderFragment listViewNewOrderFragment;
    ListViewRekapBayarFragment listViewRekapBayarFragment;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            reload();
        }
    } ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    public void reload(){
        if (contentFragment!=null){
            if (contentFragment instanceof Runnable){
                ((Runnable) contentFragment).run();
                afterReload();
            }
        }
    }
    protected void onResumeA() {
        super.onResume();

        if (contentFragment instanceof ListViewNewOrderFragment) {
            listViewNewOrderFragment.onResume();
        }else if (contentFragment instanceof ListViewRekapBayarFragment) {
           listViewRekapBayarFragment.onResume();
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    AppController.setMode("new");
                    //  listViewNewOrderFragment.onResume();
                    //switchContent(listViewNewOrderFragment, ListViewNewOrderFragment.ARG_ITEM_ID);

                    switchContent("New Order", new ListViewNewOrderFragment(), ListViewNewOrderFragment.ARG_ITEM_ID);
                    return true;
                case R.id.navigation_activity:
                    AppController.setMode("old");
                    AppController.setRekap("bayarandsend");

                    switchContent("Rekap Bayar",new ListViewRekapBayarFragment(), ListViewRekapBayarFragment.ARG_ITEM_ID);
                    return true;
                case R.id.navigation_syncy:
                    syncclosing();
                    return true;
            }
            return false;
        }
    };
    private void closing(){
        if (getDbNewOrderCountWithBHC()>=1     ){
            Messagebox.showDialog(ListViewNewOrder.this, "Selesaikan seluruh  Order baru dan BHC, baru bisa closing ?", new String[]{"OK"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return;
        }
        if (getDbNewOrderAll()==0){
            Toast.makeText(ListViewNewOrder.this,"Tidak ada data yang perlu diclosing",Toast.LENGTH_SHORT).show();
        }else{
            List list = SQLite.select(OrderItem_Table.status).from(OrderItem.class).where(OrderItem_Table.status.in("pending_janji","pending_bayar")).queryList();
            if (list.size()>=1){
                Toast.makeText(ListViewNewOrder.this,"Masih ada data pending, sync terlebih dahulu",Toast.LENGTH_SHORT).show();
                return;
            }
            Messagebox.showDialog(ListViewNewOrder.this, "Apakah anda ingin closing ?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                    if (i == 0) {
                                /*final ProgressDialog mProgressDialog = new ProgressDialog(ListViewNewOrder.this);
                                mProgressDialog.setMessage("Please Wait . . . ");
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();

                                APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
                                Call updateService = apiInterface.getClosing( AppController.getToken(), AppController.getUsername() );
                                updateService.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if (response != null) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    Utility.setSetting(ListViewNewOrder.this, Utility.MD5("9"), Utility.MD5("9") );
                                                    deleteDbUnused();
                                                    mProgressDialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        mProgressDialog.dismiss();
                                    }
                                });*/


                        Messagebox.showProsesBar(ListViewNewOrder.this, new Messagebox.DoubleRunnable() {
                            String out = "";
                            @Override
                            public void run() {
                                String url = AppUtil.BASE_URL + "apicoll/cls9/";
                                Hashtable hashtable = new Hashtable();
                                hashtable.put("key",AppController.getToken());
                                hashtable.put("username",AppController.getUsername());

                                out = Utility.getHttpConnectionPost(url, hashtable);

                            }

                            @Override
                            public void runUI() {
                                if (out != null) {
                                    Utility.setSetting(ListViewNewOrder.this, Utility.MD5("9"), Utility.MD5("9") );
                                    deleteDbUnused();
                                    //reloase otomatis ke menu order
                                    AppController.setMode("new");
                                    switchContent("New Order",new ListViewNewOrderFragment(), ListViewNewOrderFragment.ARG_ITEM_ID);
                                }
                            }
                        });



                    }
                }
            });
        }
    }
    private void syncData(){
        Intent intent = new Intent(ListViewNewOrder.this, GpsTrackerIntentService.class);
        intent.putExtra("smode","close");
        startService(intent);
        //close

        /*if (getDbNewOrderCountWithBHC()>=1 ){
            Messagebox.showDialog(ListViewNewOrder.this, "Selesaikan seluruh  Order baru dan BHC, baru bisa closing ?", new String[]{"OK"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return;
        }*/

        Toast.makeText(ListViewNewOrder.this,MENU_SYNC + " berjalan dibackgroud",Toast.LENGTH_SHORT).show();
    }
    private void syncclosing(){
        Intent intent = new Intent(ListViewNewOrder.this, GpsTrackerIntentService.class);
        intent.putExtra("smode","close");
        startService(intent);
        //close

        if (getDbNewOrderCountWithBHC()>=1  ){
            Messagebox.showDialog(ListViewNewOrder.this, "Selesaikan seluruh  Order baru dan BHC, baru bisa closing ?", new String[]{"OK"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return;
        }
        if (getDbNewOrderCount()==0){
            Toast.makeText(ListViewNewOrder.this,"Tidak ada daya yang perlu diclosing",Toast.LENGTH_SHORT).show();
        }else{
            Messagebox.showDialog(ListViewNewOrder.this, "Apakah anda ingin closing ?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                if (i == 0) {
                                /*final ProgressDialog mProgressDialog = new ProgressDialog(ListViewNewOrder.this);
                                mProgressDialog.setMessage("Please Wait . . . ");
                                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                mProgressDialog.setCancelable(false);
                                mProgressDialog.show();

                                APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
                                Call updateService = apiInterface.getClosing( AppController.getToken(), AppController.getUsername() );
                                updateService.enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        if (response != null) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    Utility.setSetting(ListViewNewOrder.this, Utility.MD5("9"), Utility.MD5("9") );
                                                    deleteDbUnused();
                                                    mProgressDialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call call, Throwable t) {
                                        mProgressDialog.dismiss();
                                    }
                                });*/


                    Messagebox.showProsesBar(ListViewNewOrder.this, new Messagebox.DoubleRunnable() {
                        String out = "";
                        @Override
                        public void run() {
                            String url = AppUtil.BASE_URL + "apicoll/cls9/";
                            Hashtable hashtable = new Hashtable();
                            hashtable.put("key",AppController.getToken());
                            hashtable.put("username",AppController.getUsername());

                            out = Utility.getHttpConnectionPost(url, hashtable);

                        }

                        @Override
                        public void runUI() {
                            if (out != null) {
                                Utility.setSetting(ListViewNewOrder.this, Utility.MD5("9"), Utility.MD5("9") );
                                deleteDbUnused();
                                //reloase otomatis ke menu order
                                AppController.setMode("new");
                                switchContent("New Order",new ListViewNewOrderFragment(), ListViewNewOrderFragment.ARG_ITEM_ID);
                            }
                        }
                    });



                }
            }
        });
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_first_design);

        Intent intent = new Intent(ListViewNewOrder.this, GpsTrackerIntentService.class);
        intent.putExtra("smode","start");//intent.putExtra("smode","bhc");//syn bhc only
        startService(intent);

        String dbfile = FlowManager.getContext().getDatabasePath(AppController.NAME).getAbsolutePath();


        String versi = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versi =String.valueOf( pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {  }
        ((TextView)findViewById(R.id.txtV)).setText("Version "+versi);


        if (ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ListViewNewOrder.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


        ColorStateList iconsColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#ffbfbfbf"),
                        Color.parseColor("#ff0000ff")
                });

        ColorStateList textColorStates = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                },
                new int[]{
                        Color.parseColor("#ffbfbfbf"),
                        Color.parseColor("#ff0000ff")
                });


        mSlidingPanel = (SlidingPaneLayout) findViewById(R.id.sliding_panel);
        mSlidingPanel.setPanelSlideListener(panelSlideListener);
        mSlidingPanel.setParallaxDistance(100);
        mSlidingPanel.setSliderFadeColor(ContextCompat.getColor(this, android.R.color.transparent));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, R.mipmap.ic_menu_white_24dp);
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
            actionBar.setHomeAsUpIndicator(upArrow);
        }

        menuItems = getMenuItems();

        mMenuList = (ListView) findViewById(R.id.menu_list);
        listViewMenuNewOrderAdapter = new ListViewMenuNewOrderAdapter(this, menuItems);
        mMenuList.setAdapter(listViewMenuNewOrderAdapter);
        mMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                flagChoose=false;
                if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_NEW) ) {
                  AppController.setMode("new");
                  //  listViewNewOrderFragment.onResume();
                    //switchContent(listViewNewOrderFragment, ListViewNewOrderFragment.ARG_ITEM_ID);
                    switchContent(MENU_NEW, new ListViewNewOrderFragment(), ListViewNewOrderFragment.ARG_ITEM_ID);
                } else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_SENT)) {
                    AppController.setMode("old");
                   // switchContent(listViewNewOrderFragment, ListViewNewOrderFragment.ARG_ITEM_ID);
                    switchContent(MENU_SENT, new ListViewNewOrderFragment(), ListViewNewOrderFragment.ARG_ITEM_ID);
                }else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_REKAP)) {
                    AppController.setMode("old");
                    AppController.setRekap("bayar");
                   // switchContent(listViewRekapBayarFragment, ListViewRekapBayarFragment.ARG_ITEM_ID);
                    switchContent(MENU_REKAP,new ListViewRekapBayarFragment(), ListViewRekapBayarFragment.ARG_ITEM_ID);
               /* }else if (i == 3) {
                    AppController.setMode("old");
                    AppController.setRekap("janji");
                    switchContent("Rekap Janji Bayar",new ListViewRekapBayarFragment(), ListViewRekapBayarFragment.ARG_ITEM_ID);
                */
                }else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_REPORT)) {
                    //report 1
                    AppController.setMode("");
                    AppController.setRekap("");
                    switchContent(MENU_REPORT,new ReportFragment(), "report1");
                }else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_ANDA)) {
                    //report 1
                    AppController.setMode("");
                    AppController.setRekap("");
                    switchContent(MENU_ANDA,new ReportFragment(), "anda");



                /*}else if (i == 5) {
                    //report 2
                    AppController.setMode("");
                    AppController.setRekap("");
                     switchContent("Lapran Prestasi",new Report2Fragment(), "report2");
                }else if (i == 6) {
                    //report 2
                    AppController.setMode("");
                    AppController.setRekap("");
                    switchContent("Laporan Performance",new Report3Fragment(), "reportx");//PERFORMANCE
                */
                }else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_LOCAL)) {
                    //report local
                    AppController.setMode("all");
                    switchContent(MENU_LOCAL,new ListViewNewOrderFragment(), ListViewNewOrderFragment.ALL_ID_STATE);
                }else if(menuItems.get(i).getTitle().equalsIgnoreCase(MENU_SYNC)){
                    //sendDataPending();
                    syncData();//syncclosing
                }else if(menuItems.get(i).getTitle().equalsIgnoreCase(MENU_CLOSING)){
                    //sendDataPending();
                    closing();

                } else if (menuItems.get(i).getTitle().equalsIgnoreCase(MENU_LOGOUT)) {
                    Messagebox.showDialog(ListViewNewOrder.this, "Apakah anda ingin logout?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            if (i == 0) {
                                //checnl data sebelumnya
                                List list = SQLite.select(OrderItem_Table.status).from(OrderItem.class).where(OrderItem_Table.status.in("pending_janji","pending_bayar")).queryList();
                                if (list.size()==0){
                                    Utility.removeSettingAll(ListViewNewOrder.this);
                                    SQLite.delete().from(OrderItem.class);
                                }else{
                                    Toast.makeText(ListViewNewOrder.this,"Masih ada data pending, Sync Data dahulu",Toast.LENGTH_SHORT).show();
                                    return;
                                    //Utility.setSetting(ListViewNewOrder.this, Utility.MD5("LG"), Utility.MD5("UI") );
                                }
                                finish();
                            }
                        }
                    });
                }
                mSlidingPanel.closePane();
            }
        });

        /*// Determine the Current Layout
        frameLayout = (FrameLayout) findViewById(R.id.content_fragment_detail);
        // Check that the activity is using the layout version with
        // the content_fragment_detail FrameLayout
        if (frameLayout != null) {
            *//* The application is in the dual-pane mode, clicking on an item on the left pane will
            simply display the content on the right pane. *//*
            isTwoPane = true;
            Log.d(LOG_TAG, "we are in the dual-pane mode!");
        }*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        // Used for orientation change.
            AppController.setMode("new");
            listViewNewOrderFragment = new ListViewNewOrderFragment();
            listViewRekapBayarFragment = new ListViewRekapBayarFragment();

            switchContent("New Order",listViewNewOrderFragment, ListViewNewOrderFragment.ARG_ITEM_ID);



        IntentFilter filter = new IntentFilter();
        filter.addAction("com.icollection.reload");
        registerReceiver(receiver, filter);
    }

    SlidingPaneLayout.PanelSlideListener panelSlideListener = new SlidingPaneLayout.PanelSlideListener() {

        @Override

        public void onPanelClosed(View arg0) {
            // TODO Auto-genxxerated method stub

        }

        @Override
        public void onPanelOpened(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPanelSlide(View arg0, float arg1) {
            // TODO Auto-generated method stub

        }

    };

    public int getDbNewOrderCountWithBHC(){
        ArrayList<OrderItem> order= (ArrayList)SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in( "0"))
                .queryList();
        if (order.size()>=1){
            return order.size();
        }else{
            order= (ArrayList)SQLite.select().from(OrderItem.class).queryList();
            Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(this, "BHC","{}"));
            for (int i = 0; i < order.size(); i++) {
                if (existBHC(nbhc, order.get(i) )){
                }else{
                    return 1;//bypas masih ada yg belum isi bhc
                }
            }
            return 0;//bypass suah isi bhc semua
        }


    }
    public int getDbNewOrderAll(){
        ArrayList<OrderItem> order= (ArrayList)SQLite.select(OrderItem_Table.status).from(OrderItem.class)
                .queryList();
        return order.size();
    }
    public int getDbNewOrderCount(){
        ArrayList<OrderItem> order= (ArrayList)SQLite.select(OrderItem_Table.status).from(OrderItem.class)
                .where(OrderItem_Table.status.in( "0"))
                .queryList();
        return order.size();
    }
    public void deleteDbUnused(){
        ArrayList<OrderItem> orderDelete = (ArrayList)SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in( "0"))
                .queryList();
        FlowManager.getDatabase(AppController.class).beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                new ProcessModelTransaction.ProcessModel<OrderItem>() {
                    public void processModel(OrderItem orderItem, DatabaseWrapper wrapper) {
                        orderItem.delete();
                    }

                }).addAll(orderDelete).build())  // add elements (can also handle multiple)
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {

                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {

                    }
                }).build().execute();




    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppController.REQUEST_DETAIL || requestCode == AppController.REQUEST_TK_SEMPAT){
            if (contentFragment instanceof ListViewNewOrderFragment) {
                if (listViewNewOrderFragment.getActivity()!=null){
                    listViewNewOrderFragment.getOrderList(null);
                }
            }else if (contentFragment instanceof ListViewRekapBayarFragment) {
                if (listViewRekapBayarFragment.getActivity()!=null) {
                    listViewRekapBayarFragment.getOrderList(null);
                }
            }
        }
        if (requestCode == AppController.REQUEST_TK_SEMPAT) {

        }else{
            if (contentFragment instanceof ListViewRekapBayarFragment) {
                // listViewRekapBayarFragment.onActivityResult(requestCode,  resultCode, data);
            }
            if (data!=null && String.valueOf(data.getStringExtra("smode")).equalsIgnoreCase("bhc")){
                startBHC(myOrder);
            }
            if (data!=null && String.valueOf(data.getStringExtra("belumsempat")).equalsIgnoreCase("simpan")){
            /*myOrder.setStatusBayar("TK BELUM SEMPAT");
            myOrder.save();*/
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Before the activity is destroyed, onSaveInstanceState() gets called.
     * The onSaveInstanceState() method saves the current fragment.
     *
     * @param outState bundle
     */


    protected void onSaveInstanceStateA(Bundle outState) {
        if (contentFragment instanceof ListViewNewOrderFragment) {
            outState.putString("content", ListViewNewOrderFragment.ARG_ITEM_ID);
        }else if (contentFragment instanceof ListViewRekapBayarFragment) {
            outState.putString("content", ListViewRekapBayarFragment.ARG_ITEM_ID);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){

        }

    }

    @Override
    public void onBackPressed() {
        flagChoose=false;
        if (mSlidingPanel.isOpen()) {
            mSlidingPanel.closePane();
        } else {
           Messagebox.showDialog(ListViewNewOrder.this, "Apakah anda ingin tutup applikasi?", new String[]{"YA", "TIDAK "}, new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int i) {
                   dialog.dismiss();

                   if (i == 0) {
                       /*List list = SQLite.select(OrderItem_Table.status).from(OrderItem.class).where(OrderItem_Table.status.in("pending_janji","pending_bayar")).queryList();
                       if (list.size()==0){
                           Utility.removeSettingAll(ListViewNewOrder.this);
                           SQLite.delete().from(OrderItem.class);
                       }else{
                           Utility.setSetting(ListViewNewOrder.this, Utility.MD5("LG"), Utility.MD5("UI") );
                       }*/
                       //Utility.setSetting(ListViewNewOrder.this, Utility.MD5("LG"), Utility.MD5("UI") );
                       setResult(RESULT_OK);
                       finish();
                   }
               }
           });
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (mSlidingPanel.isOpen()) {
                    mSlidingPanel.closePane();
                } else {
                    mSlidingPanel.openPane();
                }
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);
    }

    private final String MENU_NEW              = "Tugas Anda";
    private final String MENU_SENT             = "Tugas Terkirim";
    private final String MENU_REPORT           = "Laporan";
    private final String MENU_REKAP            = "Rekap";
    private final String MENU_SYNC             = "Sync Data";
    private final String MENU_CLOSING          = "Closing";
    private final String MENU_LOCAL            = "Report Local";
    private final String MENU_ANDA             = "Untuk Anda";
    private final String MENU_LOGOUT           = "Logout";
    private ArrayList<MenuItems> getMenuItems() {
        ArrayList<MenuItems> menuItemsList = new ArrayList<MenuItems>();
        /*menuItemsList.add(new MenuItems("New Order"));
        menuItemsList.add(new MenuItems("Proceed/Sent Order"));
        menuItemsList.add(new MenuItems("Rekap Bayar"));
        menuItemsList.add(new MenuItems("Rekap Janji Bayar"));
        menuItemsList.add(new MenuItems("Laporan Kinerja"));
        menuItemsList.add(new MenuItems("Laporan Prestasi"));
        menuItemsList.add(new MenuItems("Laporan Performance"));
        //menuItemsList.add(new MenuItems("Report Local"));
        menuItemsList.add(new MenuItems("Sync & Closing"));
        menuItemsList.add(new MenuItems("Logout"));*/
        menuItemsList.add(new MenuItems(MENU_NEW));
        menuItemsList.add(new MenuItems(MENU_SENT));
        menuItemsList.add(new MenuItems(MENU_REKAP));
        menuItemsList.add(new MenuItems(MENU_REPORT));
        menuItemsList.add(new MenuItems(MENU_SYNC));
        menuItemsList.add(new MenuItems(MENU_CLOSING));
        menuItemsList.add(new MenuItems(MENU_LOCAL));
        menuItemsList.add(new MenuItems(MENU_ANDA));

        menuItemsList.add(new MenuItems(MENU_LOGOUT));
        return menuItemsList;
    }

    private  OrderItem myOrder;
    private  OrderItem myBhcOrder;
    private boolean flagChoose = false;

    public void onItemSelected2(OrderItem order, View view) {

    }
    private String[] arrayPilih = new String[0];
    private final String BAYAR              = "BAYAR ANGSURAN";
    private final String BAYAR_LAINYA       = "BAYAR LAIN2";
    private final String JANJI_BAYAR        = "JANJI BAYAR";
    private final String GAGAL              = "GAGAL";
    private final String TIDAK_KUNJUNGAN    = "TIDAK KUNJUNGAN";
    private final String SUDAH_BAYAR        = "SUDAH BAYAR";
    private final String TARIK_BARANG        = "TARIK BARANG";
    private boolean existBHC(OrderItem order){
        Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(this, "BHC","{}"));
        return existBHC(nbhc, order);
    }

    private boolean existBHC(Nson nbhc, OrderItem order){
        return BHCActivity.existBHC(nbhc, order);
        /*if (nbhc.get("data").containsKey(order.getNoPsb() + BHCActivity.getStatusBHC(order.getStatusBayar()))) {
            return true;
        }
        return false;*/
    }
    private AlertDialog alertDialog;
    private void onSelectStartNow(OrderItem order, View view){
        onSelectStartNow(order, view, false);
    }
    boolean lockClicked = false;


    private void onSelectStartNow(OrderItem order, View view, boolean bbhc){
        if (lockClicked){
            return;
        }
        lockClicked = true;
        myOrder = order;
        String status = "";
        if (myOrder.getStatus().equalsIgnoreCase("0")) {
            status = "0";
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA, JANJI_BAYAR, GAGAL, TIDAK_KUNJUNGAN};
        }else if (myOrder.getStatus().equalsIgnoreCase("2")){
            status = "2";
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().startsWith("TK ")) {
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA, JANJI_BAYAR, GAGAL, TIDAK_KUNJUNGAN};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("GAGAL")) {
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA, JANJI_BAYAR, GAGAL};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("BAYAR")) {
            Toast.makeText(ListViewNewOrder.this,"SUDAH BAYAR",Toast.LENGTH_SHORT).show();
            return;
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("TB")||String.valueOf(myOrder.getStatusBayar()).trim().equals("TARIK BARANG")) {
            //Toast.makeText(ListViewNewOrder.this,"TARIK BARANG",Toast.LENGTH_SHORT).show();
            //return;
            status = "2";
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("JB")) {
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA, JANJI_BAYAR};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("SUDAH BAYAR")) {
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA};
        }else if (String.valueOf(myOrder.getStatusBayar()).trim().equals("LAIN")) {
            status = "2";
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA};
        }else{
            arrayPilih = new String[]{TARIK_BARANG, BAYAR, BAYAR_LAINYA, JANJI_BAYAR, GAGAL, TIDAK_KUNJUNGAN};
        }

        String xhc = "";//String.valueOf(view.getTag())
        //Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(this, "BHC","{}"));
        if (bbhc){
            xhc = "TRUE";
        }else  if (existBHC(order)) {
            xhc = "EXIST";
        }

        if (alertDialog!=null){
            if (alertDialog.isShowing()){
                try {
                    alertDialog.dismiss();
                }catch (Exception e){}
            }
        }


        alertDialog =  Messagebox.showDialogCustomA(ListViewNewOrder.this, status + ":" + xhc, arrayPilih, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int wh) {
                        dialog.dismiss();


                        flagChoose = false;
                        if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewNewOrder.this)) {
                            Toast.makeText(getApplicationContext(), "Maaf, GPS anda belum aktif", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //wailt for gps
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setExpirationDuration(6000);// 1 menit
                        if (mFusedLocationClient == null) {
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ListViewNewOrder.this);
                        }
                        if (ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Maaf, permision GPS ditolak", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(ListViewNewOrder.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Utility.setSetting(ListViewNewOrder.this, "GPS", location.getLatitude() + "," + location.getLongitude());
                                    currlocation = location;
                                }
                            }
                        });
                        if (ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

                                    }
                                }
                            }

                            ;
                        };
                        mFusedLocationClient.requestLocationUpdates(locationRequest,
                                mLocationCallback,
                                null /* Looper */);
                        final int which = wh;


                        final ProgressDialog mProgressDialog = new ProgressDialog(ListViewNewOrder.this);
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
                                        mProgressDialog.dismiss();

                                        myOrder.setStatusBayarX();
                                        if (loc1 != null) {
                                            Utility.setSetting(ListViewNewOrder.this, "GPS", loc1.getLatitude() + "," + loc1.getLongitude());
                                            Toast.makeText(getApplicationContext(), "Lokasi anda " + loc1.getLatitude() + "," + loc1.getLongitude(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Maaf, Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();

                                        }
                                        //dari (stop)pindah disini
                                        if (which == 9) {
                                            //sudah bayar kunungan
                                            myOrder.setStatusBayar("SUDAH BAYAR");
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateKunjunganSudahBayar(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateKunjunganSudahBayar(ListViewNewOrder.this, myOrder, "0");
                                            }
                                            return;
                                        } else if (which == 123 || arrayPilih[which].equalsIgnoreCase(BAYAR_LAINYA)) {//BAYAR_LAINNYA
                                            if (!Utility.getSetting(getCurrActivity(), Utility.MD5("FB"), "").trim().equalsIgnoreCase("KK")) {
                                                myOrder.setStatusBayar("LAIN");
                                                ListViewDetails2FirstDesignActivity.navigateLain(ListViewNewOrder.this, myOrder);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Maaf, Access tidak diizinkan", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (arrayPilih[which].equalsIgnoreCase(BAYAR)) {
                                            if (!Utility.getSetting(getCurrActivity(), Utility.MD5("FB"), "").trim().equalsIgnoreCase("KK")) {
                                                myOrder.setStatusBayar("BAYAR");
                                                ListViewDetails2FirstDesignActivity.navigate(ListViewNewOrder.this, myOrder);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Maaf, Access tidak diizinkan", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (arrayPilih[which].equalsIgnoreCase(TARIK_BARANG)) {
                                            if (!Utility.getSetting(getCurrActivity(), Utility.MD5("FB"), "").trim().equalsIgnoreCase("KK")) {
                                                myOrder.setStatusBayar("TB");//tarik barang BHC
                                                ListViewDetails2FirstDesignActivity.navigateTB(ListViewNewOrder.this, myOrder);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Maaf, Access tidak diizinkan", Toast.LENGTH_SHORT).show();
                                            }
                                        } else if (arrayPilih[which].equalsIgnoreCase(JANJI_BAYAR)) {
                                            myOrder.setStatusBayar("JB");
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateKunjunganJB(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateKunjunganJB(ListViewNewOrder.this, myOrder, "0");
                                            }

                                        } else if (arrayPilih[which].equalsIgnoreCase(GAGAL)) {
                                            myOrder.setStatusBayar("GAGAL");
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateKunjunganGagal(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateKunjunganGagal(ListViewNewOrder.this, myOrder, "0");
                                            }

                                        }
                                    }
                                });
                            }
                        }).start();
                        //stop


                    }

                }, /*new DialogInterface.OnKeyListener() {
                            @Override
                                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                        flagChoose=false;
                                        return false;
                                    }
                    }*/
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int wh) {
                        dialog.dismiss();


                        if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewNewOrder.this)) {
                            Toast.makeText(getApplicationContext(), "Maaf, GPS anda belum aktif", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //wailt for gps
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setExpirationDuration(6000);// 1 menit
                        if (mFusedLocationClient == null) {
                            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ListViewNewOrder.this);
                        }
                        if (ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(getApplicationContext(), "Maaf, permision GPS ditolak", Toast.LENGTH_LONG).show();
                            return;
                        }
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(ListViewNewOrder.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Utility.setSetting(ListViewNewOrder.this, "GPS", location.getLatitude() + "," + location.getLongitude());
                                    currlocation = location;
                                }
                            }
                        });
                        if (ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ListViewNewOrder.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

                                    }
                                }
                            }

                            ;
                        };
                        mFusedLocationClient.requestLocationUpdates(locationRequest,
                                mLocationCallback,
                                null /* Looper */);
                        final int which = wh;


                        final ProgressDialog mProgressDialog = new ProgressDialog(ListViewNewOrder.this);
                        mProgressDialog.setMessage("Mohon menunggu, sedang mencari lokasi GPS ...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(true);
                        mProgressDialog.setCanceledOnTouchOutside(false);

                        mProgressDialog.show();
                        new Thread(new Runnable() {
                            public void run() {

                                Location location = null;
                                ob = System.currentTimeMillis();
                                int timeout = 10000;
                                if (Utility.getSetting(getApplicationContext(), "GPS", "").length() >= 10) {
                                    timeout = 3000;
                                }
                                while (Math.abs(System.currentTimeMillis() - ob) < timeout) {
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
                                        mProgressDialog.dismiss();
                                        if (loc1 != null) {
                                            Utility.setSetting(ListViewNewOrder.this, "GPS", loc1.getLatitude() + "," + loc1.getLongitude());
                                            Toast.makeText(getApplicationContext(), "Lokasi anda " + loc1.getLatitude() + "," + loc1.getLongitude(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Maaf, Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
                                        }
                                        //pindahsini
                                        if (which == -1) {
                                            //BHC
                                            myOrder.setStatusBayar(myOrder.getStatusBayarX());
                                            startBHC(myOrder);
                                            return;
                                        } else if (which == -2) {
                                            //Lihat // http://mobilekamm.ddns.net:8080/   //http://202.56.171.19:8080/
                                            Intent intent = new Intent(ListViewNewOrder.this, WebUI.class);
                                            intent.putExtra("url", "http://mobilekamm.ddns.net:8080/map/customer.php?no_psb=" + myOrder.getNoPsb() + "&key=" + AppController.getToken());
                                            intent.putExtra("title", "Lihat Detail");
                                            //intent.putExtra("content", "hide");
                                            intent.putExtra("palamat", myOrder.getAlamat1());
                                            intent.putExtra("pbayar", myOrder.getText2());
                                            intent.putExtra("psurvey", myOrder.getText3());

                                            startActivity(intent);
                                            return;

                                        } else if (which == 10) {
                                            //sudah bayar tidak kunjungan
                                            myOrder.setStatusBayar("TK SUDAH BAYAR");
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateTidakKunjunganSudah(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateTidakKunjunganSudah(ListViewNewOrder.this, myOrder, "0");
                                            }
                                            return;
                                        } else if (which == 11) {
                                            //janji bayar tk
                                            myOrder.setStatusBayar("TK JB");
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateTidakKunjunganJanji(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateTidakKunjunganJanji(ListViewNewOrder.this, myOrder, "0");
                                            }
                                            return;
                                        } else if (which == 12) {
                                            //tidak sempat
                                            myOrder.setStatusBayar("TK BELUM SEMPAT");
                                            //add
                                            myOrder.setText2(Utility.Now());
                                            myOrder.setText3(Utility.getSetting(getApplicationContext(), "GPS", ""));

                                            /* if (AppController.getMode().equalsIgnoreCase("old")) {
                                                JanjiBayarActivity.navigateTidakKunjungan(ListViewNewOrder.this, myOrder, "1");
                                            } else {
                                                JanjiBayarActivity.navigateTidakKunjungan(ListViewNewOrder.this, myOrder, "0");
                                            }*/
                                            if (AppController.getMode().equalsIgnoreCase("old")) {
                                                Toast.makeText(ListViewNewOrder.this, "TK BELUM SEMPAT sudah isi BHC", Toast.LENGTH_SHORT).show();
                                            } else {
                                                startBHC(myOrder, AppController.REQUEST_TK_SEMPAT);
                                            }
                                            return;
                                        }

                                    }
                                });
                            }
                        }).start();
                        //stop

                        //sebelum pindah




                        /*//if (which==4){
                        try {
                            String latlon = myOrder.getText1();
                                        *//*if (latlon.equalsIgnoreCase("null")||latlon.equalsIgnoreCase("")){
                                        }else{
                                            latlon = myOrder.getAlamat1();
                                        }*//*
                            switch (which){
                                case 0:
                                    latlon = myOrder.getText1();//alamta
                                    break;
                                case 1:
                                    latlon = myOrder.getText2();//bayar
                                    break;
                                case 2:
                                    latlon = myOrder.getText3();//survey
                                    break;

                            }


                            String enclatlon = Utility.urlEncode(latlon);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + enclatlon + "&mode=m");

                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }*/
                        return;
                        //}
                    }

                },
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        //lockClicked = false;
                        reload();
                    }
                }

        );
        alertDialog.show();
    }
    private void startBHC(OrderItem myOrder){
        startBHC(myOrder, -1);
    }
    private void startBHC(OrderItem myOrder, int req){
        /*Intent intent =  new Intent(ListViewNewOrder.this, BHCActivity.class);
        intent.putExtra("nopsb", myOrder.getNoPsb());
        intent.putExtra("nama", myOrder.getNama());
        intent.putExtra("statusbayar", myOrder.getStatusBayar());
        intent.putExtra("order",new Gson().toJson(myOrder, OrderItem.class));
        intent.putExtra("kode_ao", myOrder.getKodeAo());
        intent.putExtra("ke_awal", myOrder.getKeAwal());
        intent.putExtra("ke_akhir", myOrder.getKeAkhir());
        intent.putExtra("posko", myOrder.getPosko());
        intent.putExtra("orderX", (Parcelable) myOrder);
        intent.putExtra("tgl_update", Utility.Now().substring(0,10));
        intent.putExtra("jam_update",  Utility.Now().substring(11).trim());
        intent.putExtra("tgl_kirim", myOrder.getData1());
        intent.putExtra("jam_kirim", "");
        intent.putExtra("id", myOrder.getId());
        if (req == -1 ){
            startActivity(intent);
        }else{
            startActivityForResult(intent, req);
        }*/
        BHCActivity.startBHC(ListViewNewOrder.this, (myOrder==null?myBhcOrder:myOrder), req);
    }
    public void afterReload(){
        lockClicked = false;
    }

    @Override
    public void onItemSelected(OrderItem order, View view1) {
        //view1.setEnabled(false);
        if (flagChoose==false){
            //flagChoose = true;

            myBhcOrder = order;
            if (AppController.getMode().equalsIgnoreCase("all")) {
                    //none
                flagChoose=false;
                ListViewDetailsFirstDesignActivity.navigateAllX(ListViewNewOrder.this, order, order.getId());
            }else if (order.getStatus().equalsIgnoreCase("1") && AppController.getMode().equalsIgnoreCase("old")){
                flagChoose=false;
                if (order.getStatusBayar().equalsIgnoreCase("TK SUDAH BAYAR")) {
                    JanjiBayarActivity.navigateTidakKunjunganSudah(ListViewNewOrder.this, order, "1");
                }else if (order.getStatusBayar().equalsIgnoreCase("TK JB")){
                    JanjiBayarActivity.navigateTidakKunjunganJanji(ListViewNewOrder.this, order,"1");
                }else if (order.getStatusBayar().equalsIgnoreCase("TK BELUM SEMPAT")){
                    //Tidak ada
                }else if (order.getStatusBayar().startsWith("JB")){
                    //JanjiBayarActivity.navigateKunjunganJB(ListViewNewOrder.this, order,"1");
                    ListViewDetailsFirstDesignActivity.navigate(ListViewNewOrder.this, order);
                }else if (order.getStatusBayar().startsWith("BAYAR")){
                    JanjiBayarActivity.navigateKunjunganJB(ListViewNewOrder.this, order,"1");
                }else if (order.getStatusBayar().startsWith("TARIK BARANG")||order.getStatusBayar().startsWith("TB")){//?
                    //JanjiBayarActivity.navigateKunjunganJB(ListViewNewOrder.this, order,"1");
                    flagChoose=false;
                    if (AppController.getMode().equalsIgnoreCase("new")){
                        if (String.valueOf(order.getClosing()).equalsIgnoreCase("8")||String.valueOf(order.getClosing()).equalsIgnoreCase("9")) {
                            Toast.makeText(ListViewNewOrder.this,"Setor ke Kasir terlebih dahulu..",Toast.LENGTH_SHORT).show();
                            if (!order.getStatus().equalsIgnoreCase("0")){
                                onSelectStartNow(order, view1, true);
                            }
                            return;//bypass
                        }
                        onSelectStartNow(order, view1);
                    }else{
                        ListViewDetailsFirstDesignActivity.navigate(ListViewNewOrder.this, order);
                    }
                }else if (order.getStatusBayar().startsWith("SUDAH BAYAR")){
                    JanjiBayarActivity.navigateKunjunganSudahBayar(ListViewNewOrder.this, order,"1");
                }else if (order.getStatusBayar().startsWith("GAGAL")){
                    JanjiBayarActivity.navigateKunjunganGagal(ListViewNewOrder.this, order,"1");
                }else{
                    //JanjiBayarActivity.navigate(ListViewNewOrder.this, order,"1");
                }
            }else if (order.getStatus().equalsIgnoreCase("2")){
                flagChoose=false;
                if (AppController.getMode().equalsIgnoreCase("new")){
                    if (String.valueOf(order.getClosing()).equalsIgnoreCase("8")||String.valueOf(order.getClosing()).equalsIgnoreCase("9")) {
                        Toast.makeText(ListViewNewOrder.this,"Setor ke Kasir terlebih dahulu.",Toast.LENGTH_SHORT).show();
                        if (!order.getStatus().equalsIgnoreCase("0")){
                            onSelectStartNow(order, view1, true);
                        }
                        return;//bypass
                    }
                    onSelectStartNow(order, view1);
                }else{
                    ListViewDetailsFirstDesignActivity.navigate(ListViewNewOrder.this, order);
                }
            }else  if (order.getStatus().equalsIgnoreCase("pending_janji")) {
                flagChoose=false;

            }else  if (order.getStatus().equalsIgnoreCase("pending_bayar")) {
                flagChoose=false;

            //}else if (String.valueOf(view.getTag()).startsWith("position:")) {

            }else{
                if (String.valueOf(order.getClosing()).equalsIgnoreCase("8")||String.valueOf(order.getClosing()).equalsIgnoreCase("9")) {

                    Toast.makeText(ListViewNewOrder.this,"Setor ke Kasir terlebih dahulu",Toast.LENGTH_SHORT).show();
                    //isi bhc
                    if (!order.getStatus().equalsIgnoreCase("0")){
                        onSelectStartNow(order, view1, true);
                    }
                    return;//bypass //wakakaka
                }
                if (Utility.getSetting(ListViewNewOrder.this, Utility.MD5("9"), Utility.MD5("9") ).equalsIgnoreCase(Utility.MD5("9")) ){
                    //Toast.makeText(ListViewNewOrder.this,"Setor ke Kasir terlebih dahulu",Toast.LENGTH_SHORT).show();
                    //return;
                }
                //strt hire
                if (!checFirstStart()){//order.getStatus().equals("0") && !checkBHC(order)
                    android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(this);
                    //dlg.setTitle("Selamat Memulai Pekerjaan");
                    dlg.setMessage("Selamat Memulai Pekerjaan");
                    dlg.setPositiveButton("MULAI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            UtilityAndroid.setSetting(ListViewNewOrder.this, "START", Utility.Now());
                            //send to server
                            Messagebox.newTask(new Messagebox.DoubleRunnable() {
                                String res;
                                public void run() {
                                    Map<String, String> args = new HashMap<>();
                                    args.put("datetime",  Utility.Now() );
                                    res = (InternetX.postHttpConnection( AppUtil.BASE_URL + "apicoll/start/?key="+AppController.getToken(), args));
                                }
                                public void runUI() {
                                    Nson nson = Nson.readNson(res);
                                    if (nson.get("status").asString().equalsIgnoreCase("true")){
                                    }else{}
                                    onSelectStartNow(order, view1);
                                }
                            });
                        }
                    });
                    dlg.setNegativeButton("LAIN KALI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });

                    final android.app.AlertDialog alertDialog = dlg.create();


                    alertDialog.show();
                }else{
                    onSelectStartNow(order, view1);
                }



            }
        }


       // view1.setEnabled(true);
    }
    private boolean checFirstStart(){
        boolean b =false ;
        String st= UtilityAndroid.getSetting(ListViewNewOrder.this, "START", "");
        if (st.length()>=10){
            if (st.substring(0,10).equalsIgnoreCase(Utility.Now().substring(0,10))){
                return true;
            }
        }

        return  b;
    }
    private boolean checkBHC(OrderItem order){
        Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(this, "BHC","{}"));
        /*boolean b = nbhc.get("data").containsKey(order.getNoPsb() + BHCActivity.getStatusBHC(order.getStatusBayar()));
        return  b;*/
        return BHCActivity.existBHC(nbhc, order);
    }
    private double gpsLive =0;
    Location currlocation;
    private long ob = 0;
    private LocationCallback mLocationCallback;
    FusedLocationProviderClient mFusedLocationClient;
    /**
     * Used to add Fragment and replace One Fragment with Another
     * and add the transaction to the back if there is more than
     * one Fragment.
     *
     * @param fragment the fragment.
     * @param tag      the fragment tag.
     */
    public void switchContent(String title, Fragment fragment, String tag) {
        Bundle arguments = new Bundle();
        arguments.putString("TITLE", title);
        fragment.setArguments(arguments);

        TextView textView = findViewById(R.id.txtTitle);
        textView.setText(title);

        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.popBackStackImmediate()) ;

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                            android.R.anim.fade_in, android.R.anim.fade_out);
            // Replace whatever is in the content_fragment view with this fragment
            transaction.replace(R.id.content_fragment, fragment, tag);

            if (!(fragment instanceof ListViewNewOrderFragment) || !(fragment instanceof ListViewRekapBayarFragment)) {
                // add the transaction to the back stack so the user can navigate back
               // transaction.addToBackStack(tag);
            }

            // Commit the transaction
            transaction.commit();
            contentFragment = fragment;
        }
    }




    public void sendDataPending(){
        List<OrderItem> orderLists = new ArrayList<OrderItem>();

        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in("pending_janji","pending_bayar"))
                .queryList();

        for(int x =0 ; x < orderLists.size();x++){
            savePending(orderLists.get(0));
        }
    }


    public void savePending(OrderItem order) {

        String filename ="";

        if(order.getStatus().equalsIgnoreCase("pending_janji")){
            filename = order.getPhotoJanjiBayar();
            order.setStatus("1");
        }else  if(order.getStatus().equalsIgnoreCase("pending_bayar")){
            filename = order.getPhotoBayar();
            order.setStatus("2");
        }

        Bitmap myBitmap =null;
        File imgFile = new  File(filename);
        if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
        }

        byte[] byteArray = new byte[0];
        if (myBitmap !=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }

        Bitmap bitmap2 =null;
        File imgFile2 = new  File(order.getQue3());
        if(imgFile2.exists()){
            bitmap2 = BitmapFactory.decodeFile(imgFile2.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
        }
        byte[] byteArray2 = new byte[0];
        if (bitmap2!=null){
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, stream2);
            byteArray2 = stream2.toByteArray();
        }

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);

// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),byteArray2);
        MultipartBody.Part bukti =
                MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);



        Call<UpdateOrder> updateService = apiInterface.updateData( toRequestBody(AppController.getImei()),
                toRequestBody(AppController.getToken()),
                toRequestBody(order.getId()),
                toRequestBody(AppUtil.replaceNull(order.getNoPsb()+"-").trim()),
                toRequestBody(AppUtil.replaceNull(order.getNama() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getAlamat1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getAlamat2() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKodepos() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKota() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getArea() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKeAwal() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKeAkhir() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTglJtempo1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTglJtempo2() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKodeAo() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getNamaAo() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJmlAngsuran() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJmlDenda() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJmlBayarTagih() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJmlTotTerbayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTglBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJamBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getLongitude() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getLangitude() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getDiscount() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getNoKwitansi() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJenis() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getStatus() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getPrintStatus() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getReprintStatus() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getSisaAngsuran() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJumlahSisa() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKecamatan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getKelurahan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJmlSisa() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getQue() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJanjiBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getAlasan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getUpData() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getDownloadPertanyaan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getStatusBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getNopol() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTelepon() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getPosko() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getDendasisa() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getAngsisa() ).trim()),
                body,
                toRequestBody(AppUtil.replaceNull(order.getTypeKendaraan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getByrDenda() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getByrSisa() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTenor() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJumlahangsuran() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getAlasan1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getIsi_alasan1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getInformasi() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getIsi_informasi() ).trim()),

                //qu
                toRequestBody(AppUtil.replaceNull(order.getQue1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getQue2() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getQue3() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getData1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getData2() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getData3() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getText1() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getText2() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getText3() ).trim()),
                bukti,
                JanjiBayarActivity.getBlankImagePart("photo_tarik1","photo_tarik1"),
                JanjiBayarActivity.getBlankImagePart("photo_tarik2","photo_tarik2")


        );
        updateService.enqueue(new Callback<UpdateOrder>() {
            @Override
            public void onResponse(Call<UpdateOrder> call, Response<UpdateOrder> response) {

                if (response != null) {

                    if (response.message().equalsIgnoreCase("Ok")) {
                        Log.d("Test", response.message());
                        //  order.setStatus("99");
                        //  order.save();
                        order.delete();
                        Utility.delSetting(getApplicationContext(),order.getId() +"-"+ order.getNoPsb());
                    }else{
                        Utility.setSetting(getApplicationContext(),order.getId() +"-"+ order.getNoPsb(),response.code() + " : " + String.valueOf(response.message()));
                    }
                }else{


                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Log.d("Test",jObjError.getString("message"));
                        Utility.setSetting(getApplicationContext(),order.getId() +"-"+ order.getNoPsb(), "null : " + String.valueOf(response.errorBody().string()));

                    } catch (Exception e) {
                        Log.d("Test", e.getMessage());

                    }
                }
                LoginActivity.autoLogout(ListViewNewOrder.this, response);

            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                Log.d("Failure", t.getMessage());
            }
        });


    }

    public RequestBody toRequestBody(String value) {
        if (value==null){
            value="";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    public Activity getCurrActivity(){
        return  this;
    }

}
