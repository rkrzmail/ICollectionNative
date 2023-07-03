package com.icollection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.rxgps.RxGps;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.tracker.DB;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;

import com.icollection.util.Utility;
import com.naa.data.Nson;
import com.naa.data.UtilityAndroid;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.sdk.bluetooth.android.BluetoothPrinter;

import com.zj.btsdk.BluetoothService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icollection.util.AppUtil.Now;
import static com.icollection.util.AppUtil.NowX;
import static com.icollection.util.AppUtil.isAlpa;


/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewDetails3FirstDesignActivity extends AppActivity {
    private static final String LOG_TAG = ListViewDetails3FirstDesignActivity.class.getSimpleName();
    Toolbar toolbar;
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;

    private Handler m_handler = new Handler(); // Main thread
    // Intent request codes


    private String SelectedBDAddress="";
    private String SelectedDevices="";
    private String curStatusMsg;

    private Hashtable<String, String> buf = new Hashtable<String, String>();
    private String status;
    private String rprint;
    int dendasisa=0;
    int angsisa  =0;
    String jumlahangsuran;

    Button btnPrint,btnScan;
    CheckBox checklist;

   /* static  Bitmap bitmap;
    static  Bitmap bitmap2;
    static  Bitmap bitmap3;
    static  Bitmap bitmap4;*/

    OrderItem order;
    private View parentView;
    private FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    boolean isFABHided = false;
    AppController appController;
    String angsuranAkhir="";

    private String sbitmap;
    private String sbitmap2;
    private String sbitmap3;
    private String sbitmap4;

    public static void navigateL(AppCompatActivity activity, OrderItem obj, String sbitmap, String denda, String kunjungan, String titip) {
        Intent intent = new Intent(activity, ListViewDetails3FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("denda",denda);
        intent.putExtra("kunjungan",kunjungan);
        intent.putExtra("titip",titip);
        intent.putExtra("status","LAIN");
        intent.putExtra("image",sbitmap);
        //ListViewDetails3FirstDesignActivity.bitmap= bitmap;

        AppController.currentOrder = obj;
        ActivityCompat.startActivityForResult(activity, intent,AppController.REQUEST_DETAIL, null);
    }
    public static void navigateA(AppCompatActivity activity, OrderItem obj,String sbitmap,String angsuranakhir,String jumlahangsuran) {
        Intent intent = new Intent(activity, ListViewDetails3FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("angsuranakhir",angsuranakhir);
        intent.putExtra("jumlahangsuran",jumlahangsuran);
        intent.putExtra("status","BAYAR");
        intent.putExtra("image",sbitmap);
         //ListViewDetails3FirstDesignActivity.bitmap= bitmap;
        AppController.currentOrder = obj;
        ActivityCompat.startActivityForResult(activity, intent,AppController.REQUEST_DETAIL, null);
    }
    public static void navigateJ(AppCompatActivity activity, OrderItem obj,String sbitmap) {
        Intent intent = new Intent(activity, ListViewDetails3FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("status","JB");
        intent.putExtra("image",sbitmap);

        AppController.currentOrder = obj;
        ActivityCompat.startActivityForResult(activity, intent, JanjiBayarActivity.REQUEST_JB, null);
    }
    public static void navigateTBA(AppCompatActivity activity, OrderItem obj, String bitmap1,String bitmap2,String bitmap3,String bitmap4  ) {
        Intent intent = new Intent(activity, ListViewDetails3FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("angsuranakhir","");
        intent.putExtra("jumlahangsuran","");
        intent.putExtra("status","TB");
        intent.putExtra("image",bitmap1);//overide
        intent.putExtra("image2",bitmap2);
        intent.putExtra("image3",bitmap3);
        intent.putExtra("image4",bitmap4);
        //ListViewDetails3FirstDesignActivity.bitmap= bitmap;

        AppController.currentOrder = obj;
        ActivityCompat.startActivityForResult(activity, intent,AppController.REQUEST_DETAIL, null);
    }
    public String getIntentStringExtra(String key) {
        return getIntentStringExtra(getIntent(), key);
    }
    public String getIntentStringExtra(Intent intent, String key) {
        if (intent!=null && intent.getStringExtra(key)!=null){
            return intent.getStringExtra(key);
        }
        return "";
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_details_3_first_design);
        parentView = findViewById(android.R.id.content);
        appController = (AppController) getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnPrint =(Button)findViewById(R.id.btnPrint);
        btnScan =(Button)findViewById(R.id.btnScan);


        ViewCompat.setTransitionName(findViewById(R.id.image), "order");
        order = getIntent().getParcelableExtra("order");
        angsuranAkhir = getIntent().getStringExtra("angsuranakhir");
        jumlahangsuran = getIntent().getStringExtra("jumlahangsuran");
        TextView title = findViewById(R.id.txtTitle);
        if (getIntentStringExtra("status").equalsIgnoreCase("TB")) {
            findViewById(R.id.lnrBayar).setVisibility(View.GONE);
            findViewById(R.id.lnrTB).setVisibility(View.VISIBLE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.GONE);
            setTitle("TARIK BARANG");
            title.setText("TARIK BARANG");
            TextView textView = findViewById(R.id.txtPriview);
            //textView.setText(getPrintTB(this, order) );
            textView.setText(getPrintTB_(ListViewDetails3FirstDesignActivity.this, order).append(getPrintCheckList(ListViewDetails3FirstDesignActivity.this, order)).toString());



            sbitmap2 = getIntentStringExtra("image2");
            sbitmap3 = getIntentStringExtra("image3");
            sbitmap4 = getIntentStringExtra("image4");
        }else if (getIntentStringExtra("status").equalsIgnoreCase("LAIN")) {
            findViewById(R.id.lnrTB).setVisibility(View.GONE);
            findViewById(R.id.lnrBayar).setVisibility(View.GONE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.VISIBLE);
            setTitle("BAYAR LAIN");
            title.setText("BAYAR LAIN");
        }else if (getIntentStringExtra("status").equalsIgnoreCase("JB")) {
            findViewById(R.id.lnrBayar).setVisibility(View.GONE);
            findViewById(R.id.lnrTB).setVisibility(View.VISIBLE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.GONE);
            setTitle("JB PRINT");
            title.setText("JB PRINT");
            TextView textView = findViewById(R.id.txtPriview);

            textView.setText(getPrintJB(ListViewDetails3FirstDesignActivity.this, order).append(  "").toString());

            if (Utility.getSetting(ListViewDetails3FirstDesignActivity.this, Utility.MD5("FB"),"").trim().equalsIgnoreCase("KK")){
                //boleh ngeprint
            }else{
                //findViewById(R.id.frmCetak).setVisibility(View.GONE);
                btnPrint.setVisibility(View.INVISIBLE);
                btnScan.setVisibility(View.INVISIBLE);
                findViewById(R.id.tblBHC).setVisibility(View.VISIBLE);
                findViewById(R.id.tblBHC).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            saveJB();
                    }
                });
            }
        }else{
            findViewById(R.id.lnrTB).setVisibility(View.GONE);
            findViewById(R.id.lnrBayar).setVisibility(View.VISIBLE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.GONE);
            setTitle("BAYAR ANGSURAN");
            title.setText("BAYAR ANGSURAN");
        }

        sbitmap = getIntentStringExtra("image");

        AppUtil.setSetting(ListViewDetails3FirstDesignActivity.this, "MAC", "");



        /*checklist =(CheckBox) findViewById(R.id.checklist);
        checklist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    TextView textView = findViewById(R.id.txtPriview);
                    textView.setText(getPrintTB_(ListViewDetails3FirstDesignActivity.this, order).append("--------------------\n").append(getPrintCheckList(ListViewDetails3FirstDesignActivity.this, order)).toString());
                }else{
                    TextView textView = findViewById(R.id.txtPriview);
                    textView.setText(getPrintTB(ListViewDetails3FirstDesignActivity.this, order) );
                }
            }
        });*/


        setGPS();
        setAllData();



        btnScan.setText("Scan");
        btnPrint.setText("Print");



        if( SelectedBDAddress.equalsIgnoreCase("")){
            btnPrint.setEnabled(false);
        }else{
            btnScan.setText("Paired to:"+SelectedBDAddress);
        }

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewDetails3FirstDesignActivity.this)){
                    Toast.makeText(getApplicationContext(),"Maaf, GPS anda belum aktif",Toast.LENGTH_LONG).show();
                     return;
                 }
                if (getIntentStringExtra("status").equalsIgnoreCase("TB")){
                }else if (getIntentStringExtra("status").equalsIgnoreCase("JB")){
                }else if (getIntentStringExtra("status").equalsIgnoreCase("LAIN")){
                }else{
                    if (AppUtil.getIntCut(order.getJmlAngsuran())>AppUtil.getIntCut(order.getJmlTotTerbayar())) {
                        return;
                    }
                }


                if(isBluetoothAvailable(getApplicationContext())){
                    Intent serverIntent = null;
                    serverIntent = new Intent(ListViewDetails3FirstDesignActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }else{
                    ShowMessageBox("Bluetooth anda harus diaktifkan terlebih dahulu!", "Perhatian");

                }
            }
        });
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewDetails3FirstDesignActivity.this)){
                    Toast.makeText(getApplicationContext(),"Maaf, GPS anda belum aktif",Toast.LENGTH_LONG).show();

                }else{
                    save();
                }


                }

        });



        filldata(order.getNoPsb(), "no_psb");
        filldata(order.getNama(),"nama");
        filldata(order.getNopol(),"nopol");
        filldata(order.getTelepon(),"telp");
        filldata(order.getAlamat1(),"alamat1");
        filldata(order.getAlamat2(),"alamat2");
        filldata(order.getKodepos(),"kodepos");
        filldata(order.getKota(),"kota");
        filldata(order.getArea(),"area");
        filldata(order.getKeAwal(),"ke_awal");
        filldata(order.getKeAkhir(),"ke_akhir");
        filldata(order.getTglJtempo1(),"tgl_jtempo1");
        filldata(order.getTglJtempo2(),"tgl_jtempo2");
        filldata(order.getKodeAo(),"kode_ao");
        filldata(order.getNamaAo(),"nama_ao");
        filldata(order.getJmlAngsuran(),"jml_angsuran");
        filldata(order.getJmlDenda(),"jml_denda");
        filldata(order.getJmlBayarTagih(),"jml_bayar_tagih");
        filldata(order.getJmlTotTerbayar(),"jml_tot_terbayar");
        filldata(order.getTglBayar(), "tgl_bayar");
        filldata(order.getJamBayar(), "jam_bayar");
        filldata(order.getJanjiBayar(), "janji_bayar");
        filldata(order.getJanjiBayar(), "status_janji_bayar");
        filldata(order.getLangitude(), "langitude");
        filldata(order.getLongitude(), "longitude");
        filldata(order.getDiscount(),"discount");
        filldata(order.getNoKwitansi(),"nokwitansi");

        filldata(order.getDendasisa(),"dendasisa");
        filldata(order.getJmlSisa(),"angsisa");
        filldata(order.getByrSisa(),"byrsisa");
        //filldata(rst, "print_status");
        status = order.getStatus();
        rprint = order.getPrintStatus();
        filldata(order.getJumlahSisa(),"sisa");

        filldata(order.getJmlSisa(), "jml_sisa");
        filldata(order.getJenis(),"jenis");
        filldata(order.getKecamatan(), "kecamatan");
        filldata(order.getAlasan(),"alasan");
        filldata(order.getQue(), "que");
        String jsisa = order.getJmlSisa();



























        /*fab = (FloatingActionButton) findViewById(R.id.fab);

        setAllData();
        fabToggle();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (appController.isAddedToFavorites(news)) {
                    appController.removeFromFavorites(news);
                    Snackbar.make(parentView, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    appController.addToFavorites(news);
                    Snackbar.make(parentView, "Added to favorites", Snackbar.LENGTH_SHORT).show();
                }
                fabToggle();
            }
        });*/

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            /*    if (scrollY < oldScrollY) {
                    animateFAB(false);
                }
                if (scrollY > oldScrollY) {
                    animateFAB(true);
                }*/
            }
        });
    }
    String txtAng="";
    private void setAllData() {
        if (order != null) {
            txtAng= "ANGS KE "+ order.getKeAwal() +" S/D "+ AppUtil.setZero(angsuranAkhir) ;
            ((TextView)findViewById(R.id.textView8)).setText( AppUtil.formatCurrency( order.getByrDenda() ) );
            ((TextView)findViewById(R.id.textView9)).setText( AppUtil.formatCurrency( order.getByrSisa()) );
            ((TextView)findViewById(R.id.TextView02)).setText( AppUtil.formatCurrency( order.getJmlTotTerbayar() ) );

            ((TextView)findViewById(R.id.textView2a)).setText(  "ANGS KE "+ order.getKeAwal() +" S/D "+ AppUtil.setZero(angsuranAkhir)  );
            ((TextView)findViewById(R.id.textView7a)).setText( AppUtil.formatCurrency( jumlahangsuran) );


            ((TextView)findViewById(R.id.txtName)).setText( order.getNama() );
            ((TextView)findViewById(R.id.txtDate)).setText( Now().substring(0,10) );
            ((TextView)findViewById(R.id.txtBayarName)).setText( order.getNama() );
            ((TextView)findViewById(R.id.txtBayarDate)).setText( Now().substring(0,10) );


            ((TextView)findViewById(R.id.txtDenda)).setText( AppUtil.formatCurrency( order.getByrDenda() ) );
            ((TextView)findViewById(R.id.txtKunjungan)).setText( AppUtil.formatCurrency( order.getByrSisa()) );
            ((TextView)findViewById(R.id.txtTagih)).setText( AppUtil.formatCurrency( order.getDiscount() ) );
            ((TextView)findViewById(R.id.txtTotalBayar)).setText( AppUtil.formatCurrency( order.getJmlTotTerbayar() ) );

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      /*  if(requestCode == AppController.REQUEST_DETAIL){
            if(resultCode == AppController.CLOSE_CODE){
                setResult(AppController.CLOSE_CODE);
                finish();
            }
        }*/

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // pastikan close terlebih dahulu


                    SelectedBDAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    SelectedDevices = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);

                    if(SelectedBDAddress!=null || !SelectedBDAddress.equalsIgnoreCase("")){

                        btnPrint.setEnabled(true);
                        btnScan.setText("Paired to:"+SelectedBDAddress);
                    }else{
                        btnPrint.setEnabled(false);
                        btnScan.setText("Scan");
                    }


                    return;
                }else{
                    btnPrint.setEnabled(false);
                    btnScan.setText("Scan");
                }



                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // connectDevice(data, false);
                }
                break;

            default :
                btnPrint.setEnabled(false);
                btnScan.setText("Scan");
                break;
        }





    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listview_first_design_details, menu);
        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorTextSecondary), PorterDuff.Mode.SRC_ATOP);
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_share) {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = getString(R.string.share_format,
                    order.getNama(),
                    order.getAlamat1());
            //    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        }else if (itemId == android.R.id.home ) {

           onBackPressed();
        }
        return true;
    }


    private void filldata(String rst, String name){
        try {
            buf.put(name, rst);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private String getdata(String name){
        String s = buf.get(name);
        return (s!=null?s:"");
    }

    boolean resultNya ;
    ProgressDialog progressDialog;
    public boolean save() {
        if (getIntent()!=null && String.valueOf(getIntent().getStringExtra("status")).equalsIgnoreCase("TB")) {
            return saveTB();
        }else if (getIntent()!=null && String.valueOf(getIntent().getStringExtra("status")).equalsIgnoreCase("LAIN")){
            return saveByrLain();
        }else if (getIntent()!=null && String.valueOf(getIntent().getStringExtra("status")).equalsIgnoreCase("JB")){
            return saveJB();
        }else{
            return saveByr();
        }
    }
    public boolean saveJB() {
        resultNya = false;

        order.setStatus("1");//2 bayar
        order.setPrintStatus("1");
        //add
        order.setText2(Utility.Now());
        order.setText3(Utility.getSetting(getApplicationContext(),"GPS", ""));

        AppController.currentOrder = order;
      /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();*/

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap));
        String now = NowX();
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+now+".jpg", requestFile);


        MultipartBody.Part bukti = JanjiBayarActivity.getBlankImagePart("photo_bukti","photo_bukti");


        progressDialog = new ProgressDialog(ListViewDetails3FirstDesignActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Call<UpdateOrder> updateService = apiInterface.updateData(
                toRequestBody(AppController.getImei()),
                toRequestBody(AppController.getToken()),
                toRequestBody(order.getId()),
                toRequestBody(AppUtil.replaceNull(order.getNoPsb()).trim()),
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
                //toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
                //toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),

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
                progressDialog.dismiss();
                if (response != null) {
                    Log.d("Test", response.message());

                    if (response.message().equalsIgnoreCase("Ok")) {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                        printITwithGPS(0);
                        //order.delete();
                        order.save();
                    }else{
                        notifPend();
                        JSONObject jObjError;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                            Log.d("error message" ,jObjError.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        order.setStatus("pending_janji");
                        File xFile = null;
                        try {
                            xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                            order.setStatus("pending_janji");
                            order.setPhotoBayar(xFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        printITwithGPS(0);
                        resultNya = false;
                        order.save();
                    }


                    resultNya = true;
                }else{
                    notifPend();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    order.setStatus("pending_janji");
                    File xFile = null;
                    try {
                        xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (xFile != null) {
                        order.setPhotoBayar(xFile.getAbsolutePath()+"");
                    }else{
                        order.setPhotoBayar("");
                    }


                    order.save();
                }


            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                notifPend();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_LONG).show();

                order.setStatus("pending_janji");
                File xFile = null;
                try {
                    xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (xFile != null) {
                    order.setPhotoBayar(xFile.getAbsolutePath()+"");



                    printITwithGPS(0);


                    resultNya = false;
                }else{
                    order.setPhotoBayar("");

                    printITwithGPS(0);

                    //  Toast.makeText(getApplicationContext(),"Gagal pembentukan file silahkan coba lagi",Toast.LENGTH_SHORT).show();
                }
                order.save();

            }
        });

        return resultNya;
    }
    public boolean saveTB() {
        resultNya = false;

        order.setStatus("1");//2 bayar
        order.setPrintStatus("1");
        //add
        order.setText2(Utility.Now());
        order.setText3(Utility.getSetting(getApplicationContext(),"GPS", ""));

        AppController.currentOrder = order;
      /*  ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray = stream.toByteArray();*/

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap));
        String now = NowX();
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+now+".jpg", requestFile);

        /*ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray2 = stream.toByteArray();*/
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap2));
        MultipartBody.Part bukti =
                MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+now+"tb2.jpg", requestFile2);

       /* ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
        bitmap3.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray3 = stream.toByteArray();*/
        RequestBody requestFile3 = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap3));
        MultipartBody.Part parttarik1 =
                MultipartBody.Part.createFormData("photo_tarik1", order.getNoPsb()+now+"tb3.jpg", requestFile3);

        /*ByteArrayOutputStream stream4 = new ByteArrayOutputStream();
        bitmap4.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] byteArray4 = stream.toByteArray();*/
        RequestBody requestFile4 = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap4));
        MultipartBody.Part parttarik2 =
                MultipartBody.Part.createFormData("photo_tarik2", order.getNoPsb()+now+"tb4.jpg", requestFile4);



      /*  MultipartBody.Part parttarik2 =JanjiBayarActivity.getBlankImagePart("","");
        MultipartBody.Part parttarik1 =JanjiBayarActivity.getBlankImagePart("","");
                MultipartBody.Part body =JanjiBayarActivity.getBlankImagePart("","");
                MultipartBody.Part bukti = JanjiBayarActivity.getBlankImagePart("","");
*/
        progressDialog = new ProgressDialog(ListViewDetails3FirstDesignActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Call<UpdateOrder> updateService = apiInterface.updateData(
                toRequestBody(AppController.getImei()),
                toRequestBody(AppController.getToken()),
                toRequestBody(order.getId()),
                toRequestBody(AppUtil.replaceNull(order.getNoPsb()).trim()),
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
                //toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
                //toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),

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
                parttarik1,
                parttarik2


        );


        updateService.enqueue(new Callback<UpdateOrder>() {
            @Override
            public void onResponse(Call<UpdateOrder> call, Response<UpdateOrder> response) {
                progressDialog.dismiss();
                if (response != null) {
                    Log.d("Test", response.message());

                    if (response.message().equalsIgnoreCase("Ok")) {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                        printITwithGPS(0);
                        //order.delete();
                        order.save();
                    }else{
                        notifPend();
                        JSONObject jObjError;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                            Log.d("error message" ,jObjError.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //DO



                        order.setStatus("pending_janji");
                        File xFile = null;
                        try {
                            xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                            order.setStatus("pending_janji");
                            order.setPhotoBayar(xFile.getAbsolutePath());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            AppUtil.savebitmapFile(sbitmap2, order.getNoPsb().toString()+"tb2");
                        } catch (IOException e) {}
                        try {
                            AppUtil.savebitmapFile(sbitmap3, order.getNoPsb().toString()+"tb3");
                        } catch (IOException e) {}
                        try {
                            AppUtil.savebitmapFile(sbitmap4,  order.getNoPsb().toString()+"tb4");
                        } catch (IOException e) {}

                        printITwithGPS(0);
                        resultNya = false;
                        order.save();
                    }


                    resultNya = true;
                }else{
                    notifPend();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    order.setStatus("pending_janji");
                    File xFile = null;
                    try {
                        xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (xFile != null) {
                        order.setPhotoBayar(xFile.getAbsolutePath()+"");
                    }else{
                        order.setPhotoBayar("");
                    }
                    try {
                        AppUtil.savebitmapFile(sbitmap2, order.getNoPsb().toString()+"tb2");
                    } catch (IOException e) {}
                    try {
                        AppUtil.savebitmapFile(sbitmap3, order.getNoPsb().toString()+"tb3");
                    } catch (IOException e) {}
                    try {
                        AppUtil.savebitmapFile(sbitmap4,  order.getNoPsb().toString()+"tb4");
                    } catch (IOException e) {}

                    order.save();
                }
                LoginActivity.autoLogout(ListViewDetails3FirstDesignActivity.this, response);
                //   Toast.makeText(getApplicationContext(), "Nothing happen", Toast.LENGTH_LONG).show();
                // resultNya = false;
                DB.save(getApplicationContext(), order);
            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                notifPend();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_LONG).show();

                order.setStatus("pending_janji");
                File xFile = null;
                try {
                    xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    AppUtil.savebitmapFile(sbitmap2, order.getNoPsb().toString()+"tb2");
                } catch (IOException e) {}
                try {
                    AppUtil.savebitmapFile(sbitmap3, order.getNoPsb().toString()+"tb3");
                } catch (IOException e) {}
                try {
                    AppUtil.savebitmapFile(sbitmap4,  order.getNoPsb().toString()+"tb4");
                } catch (IOException e) {}
                if (xFile != null) {
                    order.setPhotoBayar(xFile.getAbsolutePath()+"");



                    printITwithGPS(0);


                    resultNya = false;
                }else{
                    order.setPhotoBayar("");

                    printITwithGPS(0);

                    //  Toast.makeText(getApplicationContext(),"Gagal pembentukan file silahkan coba lagi",Toast.LENGTH_SHORT).show();
                }
                order.save();
                DB.save(getApplicationContext(), order);


            }
        });

        return resultNya;
    }

    /*private boolean checkImageExist(ImageView imageView){
        String file = String.valueOf(imageView.getTag());
        return new File(file).exists();
    }*/

    public boolean saveByr() {
        resultNya = false;

        order.setStatus("2");
        order.setPrintStatus("1");
        //add
        order.setText2(Utility.Now());
        order.setText3(Utility.getSetting(getApplicationContext(),"GPS", ""));

        AppController.currentOrder = order;
        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();*/

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
      //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap));

// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);

        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),new byte[0]);
        MultipartBody.Part bukti =
                MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);



        progressDialog = new ProgressDialog(ListViewDetails3FirstDesignActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Call<UpdateOrder> updateService = apiInterface.updateData( toRequestBody(AppController.getImei()),
                toRequestBody(AppController.getToken()),toRequestBody(order.getId()),
                toRequestBody(AppUtil.replaceNull(order.getNoPsb()).trim()),
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
                //toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
                //toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),

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
                progressDialog.dismiss();
                if (response != null) {
                    Log.d("Test", response.message());

                    if (response.message().equalsIgnoreCase("Ok")) {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                        printITwithGPS(0);
                        //order.delete();
                        order.save();


                    }else{
                        notifPend();
                        JSONObject jObjError;
                        try {
                             jObjError = new JSONObject(response.errorBody().string());
                            Log.d("error message" ,jObjError.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //DO



                        order.setStatus("pending_bayar");
                        File xFile = null;
                        try {
                            xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());

                            order.setStatus("pending_bayar");
                            order.setPhotoBayar(xFile.getAbsolutePath());

                        } catch (IOException e) {
                            e.printStackTrace();

                        }


                        printITwithGPS(0);
                        resultNya = false;
                        order.save();
                    }


                    resultNya = true;
                }else{
                    notifPend();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    order.setStatus("pending_bayar");
                    File xFile = null;
                    try {
                        xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (xFile != null) {
                        order.setPhotoBayar(xFile.getAbsolutePath()+"");
                    }else{
                        order.setPhotoBayar("");
                    }

                    order.save();

                }
                LoginActivity.autoLogout(ListViewDetails3FirstDesignActivity.this, response);
                //   Toast.makeText(getApplicationContext(), "Nothing happen", Toast.LENGTH_LONG).show();
                // resultNya = false;
                //DB.backup(getApplicationContext(), FlowManager.getContext().getDatabasePath(AppController.NAME).getAbsolutePath());
                DB.save(getApplicationContext(), order);
            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                notifPend();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_LONG).show();

                order.setStatus("pending_bayar");
                File xFile = null;
                try {
                    xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (xFile != null) {

                    order.setPhotoBayar(xFile.getAbsolutePath()+"");



                    printITwithGPS(0);


                    resultNya = false;
                }else{
                    order.setPhotoBayar("");

                    printITwithGPS(0);

                  //  Toast.makeText(getApplicationContext(),"Gagal pembentukan file silahkan coba lagi",Toast.LENGTH_SHORT).show();
                }
                order.save();
                //DB.backup(getApplicationContext(), FlowManager.getContext().getDatabasePath(AppController.NAME).getAbsolutePath());
                DB.save(getApplicationContext(), order);
            }
        });

        return resultNya;
    }

    void notifPend(){
        com.icollection.util.Utility.setSetting(ListViewDetails3FirstDesignActivity. this,  "pend","1");
    }
    public boolean saveByrLain() {
        resultNya = false;

        order.setStatus("2");//bayar
        order.setPrintStatus("1");
        //add
        order.setText2(Utility.Now());
        order.setText3(Utility.getSetting(getApplicationContext(),"GPS", ""));

        /*ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();*/

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),new File(sbitmap));
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);


        progressDialog = new ProgressDialog(ListViewDetails3FirstDesignActivity.this);
        progressDialog.setTitle("Loading");
        progressDialog.show();

        Call<UpdateOrder> updateService = apiInterface.updateData( toRequestBody(AppController.getImei()),
                toRequestBody(AppController.getToken()),
                toRequestBody(order.getId()),
                toRequestBody(AppUtil.replaceNull(order.getNoPsb()).trim()),
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
                //toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
                //toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getTypeKendaraan() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getByrDenda() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getByrSisa() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getTenor() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJumlahangsuran() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),

                toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),
                toRequestBody(AppUtil.replaceNull(order.getJml_angsuran_ke() ).trim()),

                toRequestBody(AppUtil.replaceNullMax(order.getAlasan(), 30 ).trim()),
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

                JanjiBayarActivity.getBlankImagePart("photo_bukti","photo_bukti"),
                JanjiBayarActivity.getBlankImagePart("photo_tarik1","photo_tarik1"),
                JanjiBayarActivity.getBlankImagePart("photo_tarik2","photo_tarik2")


        );

/*
bypass
        order.setStatus("pending_bayar");
        File xFile = null;
        try {
            xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (xFile != null) {

            order.setPhotoBayar(xFile.getAbsolutePath()+"");


            resultNya = false;
        }else{
            order.setPhotoBayar("");
        }
        order.save();
        int a =0;
        if (a == 0) {
            progressDialog.dismiss();
            return false;
        }*/

        updateService.enqueue(new Callback<UpdateOrder>() {
            @Override
            public void onResponse(Call<UpdateOrder> call, Response<UpdateOrder> response) {
                progressDialog.dismiss();
                if (response != null) {
                    Log.d("Test", response.message());

                    if (response.message().equalsIgnoreCase("Ok")) {
                        Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                        printITwithGPS(0);
                        //order.delete();
                        order.save();
                    }else{
                        notifPend();
                        JSONObject jObjError;
                        try {
                            jObjError = new JSONObject(response.errorBody().string());
                            Log.d("error message" ,jObjError.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //DO



                        order.setStatus("pending_bayar");
                        File xFile = null;
                        try {
                            xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());

                            order.setStatus("pending_bayar");
                            order.setPhotoBayar(xFile.getAbsolutePath());

                        } catch (IOException e) {
                            e.printStackTrace();

                        }


                        printITwithGPS(0);
                        resultNya = false;
                        order.save();
                    }


                    resultNya = true;
                }else{
                    notifPend();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    order.setStatus("pending_bayar");
                    File xFile = null;
                    try {
                        xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (xFile != null) {
                        order.setPhotoBayar(xFile.getAbsolutePath()+"");
                    }else{
                        order.setPhotoBayar("");
                    }

                    order.save();
                }
                LoginActivity.autoLogout(ListViewDetails3FirstDesignActivity.this, response);
                //   Toast.makeText(getApplicationContext(), "Nothing happen", Toast.LENGTH_LONG).show();
                // resultNya = false;
                DB.save(getApplicationContext(), order);
            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                notifPend();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_LONG).show();

                order.setStatus("pending_bayar");
                File xFile = null;
                try {
                    xFile =   AppUtil.savebitmapFile(sbitmap,order.getNoPsb().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (xFile != null) {

                    order.setPhotoBayar(xFile.getAbsolutePath()+"");



                    printITwithGPS(0);


                    resultNya = false;
                }else{
                    order.setPhotoBayar("");

                    printITwithGPS(0);

                    //  Toast.makeText(getApplicationContext(),"Gagal pembentukan file silahkan coba lagi",Toast.LENGTH_SHORT).show();
                }
                order.save();


                DB.save(getApplicationContext(), order);
            }
        });

        return resultNya;
    }
    private void printITwithGPS(int i){
        if (ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewDetails3FirstDesignActivity.this)){
            printIT(i);
        }else {
            Toast.makeText(ListViewDetails3FirstDesignActivity.this, "Maaf, GPS anda belum aktif", Toast.LENGTH_SHORT).show();
        }

    }
    private static String Isi(Vector<String>  strings, int i){
        if (strings.size()>i){
            return  strings.get(i).equalsIgnoreCase("1")?"v": (strings.get(i).equalsIgnoreCase("2")?".":".");
        }
        return  ".";
    }
    public static String getPrintCheckList(Activity activity, OrderItem order){
        if (order!=null){
            List<String> list = Utility.splitList(order.getData2()+"|||||||" , "|");////data2 merk0|warna1|type2|tahun3|nopol4|stnk_an5
            List<String> stnk = Utility.splitList(order.getQue2()+"|||" , "|");////data2 merk0|warna1|type2|tahun3|nopol4|stnk_an5


            final StringBuffer sbuff = new StringBuffer("").append("\n");
            sbuff.append("Tanggal   : ").append(fixLeft(order.getTglBayar(),20)).append("\n");
            sbuff.append("Motor     : ").append(fixLeft(list.get(2).trim(),20)).append("\n");
            sbuff.append("Nopol     : ").append(fixLeft(list.get(4).trim(),20)).append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("KELENGKAPAN KENDARAAN :         ").append("\n");
            sbuff.append("                                ").append("\n");


            int i = 0 ;
            Vector<String>  strV = com.naa.data.Utility.splitVector(CheckList.getChecklistQue1(order),",");


            //sbuff.append("STNK Kendaraan               [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Lampu Besar                  [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Lampu Stop (Belakang)        [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Lampu Sein Depan (R/L)       [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Lampu Sein Belakang (R/L)    [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Lampu Sein Belakang (R/L)    [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Cover Body Belakang          [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Stiker                       [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Sparkboard Depan             [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Sparkboard Belakang          [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Tutup Shock Depan (R/L)      [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Kunci Kontak                 [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Panel Starter/Lampu          [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Panel Klakson                [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Besi Belakang Jok            [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Pedal Verseneling            [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Accu                         [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Tool Kit                     [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Foot Step Depan (R/L)        [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Foot Step Belakang (R/L)     [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Spion                        [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Standard Tengah              [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Standard Samping             [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Kick Starter                 [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Karburator/CDI               [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Master Rem Cakram            [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Caliver (Jepitan Disk)       [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Disk Brake                   [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Tromol (Depan/Belakang)      [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Pedal Rem                    [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Speedometer                  [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Kabel Speedometer            [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Kabel RPM                    [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Shock Absorber Depan         [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Shock Absorber Belakang      [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Lengan Ayun/Arm              [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Velg Depan                   [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Velg Belakang                [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Ban Depan                    [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Ban Belakang                 [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Tanki BBM                    [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Tanki Oli Samping            [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Jok                          [").append(Isi(strV, i++ )).append("]").append("\n");
            sbuff.append("Setang                       [").append(Isi(strV, i++ )).append("]").append("\n");*/
            sbuff.append("Knalpot                      [").append(Isi(strV, i++ )).append("]").append("\n");
            /*sbuff.append("Tutup Rantai                 [").append(Isi(strV, i++ )).append("]").append("\n");*/
            //sbuff.append("Mesin Dapat Hidup            [").append(Isi(strV, i++ )).append("]").append("\n");

            sbuff.append("                                ").append("\n");
            sbuff.append("Diketahui,          Pemeriksa,  ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("(").append(fixCenter( CheckList.getQue1(order.getQue1()) .toUpperCase(),10)).append(")     (").append(fixCenter(Utility.getSetting(activity, Utility.MD5("NM"),""),13)).append(")").append("\n");
            //sbuff.append("(Nama kons)       (nama kolektor)").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("Note:                           ").append("\n");
            sbuff.append("v = Ada                         ").append("\n");
            sbuff.append(". = Tidak Ada                   ").append("\n");

            sbuff.append("\n").append("\n").append("\n");
            return  sbuff.toString();
        }
        return "";
    }
    public static String getPrintTB(Activity activity, OrderItem order){
        return getPrintTB_(activity, order).append("\n").append("\n").append("\n").toString();
    }
    public static StringBuffer getPrintTB_(Activity activity, OrderItem order){
        final StringBuffer sbuff = new StringBuffer("");
        if (order!=null){
            List<String> list = Utility.splitList(order.getData2()+"|||||||" , "|");////data2 merk0|warna1|type2|tahun3|nopol4|stnk_an5
            List<String> stnk = Utility.splitList(order.getQue2()+"|||" , "|");////data2 merk0|warna1|type2|tahun3|nopol4|stnk_an5


            sbuff.append("\n");
            sbuff.append("Bukti  Serah  terima   Kendaraan").append("\n");
            sbuff.append("     No ").append(leftsubstring(order.getNoPsb(),8)).append("/").append(fixLeft(order.getTglBayar(),10).replace("-","/")).append("          ").append("\n").append("\n");
            sbuff.append("Menunjuk Surat Perjanjian Hutang").append("\n");
            sbuff.append(leftsubstring(order.getNoPsb(),8)).append(" pada tanggal ").append(fixLeft(order.getTglBayar(),10)).append("\n");
            sbuff.append("telah  dilaksanakan   penyerahan").append("\n");
            sbuff.append("sepenuhnya dari ").append(fixLeft( CheckList.getQue1(order.getQue1()).toUpperCase(),16)).append("\n");

            //sbuff.append("yang   disertai  pemberian kuasa").append("\n");
            //sbuff.append("ke pada                         ").append("\n");
            sbuff.append("sesuai dengan kondisi pada saat ").append("\n");
            sbuff.append("diserahkan                      ").append("\n");
            sbuff.append("yang   disertai  pemberian kuasa").append("\n");
            sbuff.append("ke pada                         ").append("\n");


            sbuff.append("KOPERASI  ANUGRAH  MEGA  MANDIRI").append("\n");
            sbuff.append("untuk    mengalihkan   kendaraan").append("\n");
            sbuff.append("pada  pihak  lain,              ").append("\n");
            sbuff.append("apabila  dalam  waktu   7   hari").append("\n");
            sbuff.append("dari  hari  ini  tidak dilakukan").append("\n");
            //sbuff.append("pembayaran  oleh  konsumen  yang").append("\n");
            sbuff.append("penyelesaian oleh konsumen  yang").append("\n");

            sbuff.append("tertera  pada  perjanjian       ").append("\n");
            sbuff.append("Merk      : ").append(fixLeft(list.get(0),20)).append("\n");
            sbuff.append("Warna     : ").append(fixLeft(list.get(1),20)).append("\n");
            //sbuff.append("STNK      : ").append(fixLeft(list.get(5),20)).append("\n");
            sbuff.append("STNK      : ").append(fixLeft(stnk.get(0),20)).append("\n");
            if (stnk.get(0).equalsIgnoreCase("ADA")) {
                sbuff.append("Tgl Pajak : ").append(fixLeft(stnk.get(1), 20)).append("\n");
                sbuff.append("Tgl Plat  : ").append(fixLeft(stnk.get(2), 20)).append("\n");
            }
            sbuff.append("Type      : ").append(fixLeft(list.get(2),20)).append("\n");
            sbuff.append("No Polisi : ").append(fixLeft(list.get(4),20)).append("\n");
            sbuff.append("Kondisi Motor : ").append(fixLeft(order.getQue3(),16)).append("\n");
            sbuff.append("Yang menyerahkan   Yang menerima").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("(").append(fixCenter( CheckList.getQue1(order.getQue1()).toUpperCase(),10)).append(")     (").append(fixCenter(Utility.getSetting(activity, Utility.MD5("NM"),""),13)).append(")").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("Konsumen  mengalihkan  kendaraan").append("\n");
            sbuff.append("ini dalam keadaan sadar & tidak ").append("\n");
            sbuff.append("ada  unsur  tekanan  dari  pihak").append("\n");
            sbuff.append("manapun                         ").append("\n").append("\n");
            //sbuff.append("WA  Center  KAMM  0899 9731 1188").append("\n");
            sbuff.append("WA  Center  KAMM  0852 1717 1118").append("\n");
            sbuff.append("\n");
//Utility.setSetting(LoginActivity.this, Utility.MD5("US")



            return  sbuff;
        }
        return sbuff;
    }
    public static Nson getVA(Context context,String nopsb){
        Nson nson = Nson.newObject();
        Nson arr = Nson.readJson(UtilityAndroid.getSetting(context,"VA", ""));
        arr = arr.get("data");
        for (int i = 0; i < arr.size(); i++) {
            if (arr.get(i).get("NOMOR_PSB").asString().trim().equalsIgnoreCase(nopsb)){
                return arr.get(i);
            }
        }
        return nson;
    }

    public static StringBuffer getPrintJB(Activity activity, OrderItem order){
        final StringBuffer sbuff = new StringBuffer("");
        if (order!=null){


            sbuff.append("================================\n");
            sbuff.append("Tanggal : ").append(fixLeft((order.getText2()+Utility.Now()).substring(0, 10).trim(),21)).append("\n");
            sbuff.append("Jam     : ").append(fixLeft((order.getText2()+Utility.Now()).substring(10,16 ).trim(),21)).append("\n");
            sbuff.append("Petugas : ").append(fixLeft(Utility.getSetting(activity, Utility.MD5("NM"),""),21)).append("\n");
            sbuff.append("\n");

            String strInfo =  "";
            int i  = (AppUtil.getIntCut(order.getKeAkhir())-AppUtil.getIntCut(order.getKeAwal()));
            if (i == 0) {
                strInfo = "      Surat Pemberitahuan       ";
            }else if (i == 1){
                strInfo = "      Surat Peringatan 1        ";
            }else if (i == 2){
                strInfo = "      Surat Peringatan 2        ";
            }else{
                strInfo = "      Surat Peringatan 3        ";
            }

            sbuff.append(strInfo).append("\n");
            sbuff.append("\n");

            int total = (  (AppUtil.getIntCut(order.getKeAkhir())-AppUtil.getIntCut(order.getKeAwal())+1) * AppUtil.getIntCut(order.getJmlAngsuran()) ) ;
            sbuff.append("Bersama ini kami informasikan   ").append("\n");
            sbuff.append("angsuran anda telah jatuh tempo ").append("\n");
            sbuff.append("\n");
            sbuff.append("Per tgl        : ").append( fixLeft(order.getTglJtempo1().substring(0, 10),15)).append("\n");
            sbuff.append("No. PSB        : ").append( fixLeft(order.getNoPsb(),15)).append("\n");
            sbuff.append("Nama           : ").append( fixLeft(order.getNama(),15)).append("\n");


            sbuff.append("Angsuran ke    : ").append( fixLeft(order.getKeAwal()  + " - " +order.getKeAkhir(),15) ).append("\n");
            sbuff.append("Per angsuran   : ").append(rightCcurrency((AppUtil.formatCurrency(order.getJmlAngsuran())),15)).append("\n");
            sbuff.append("Total Tunggakan: ").append(rightCcurrency((AppUtil.formatCurrency( total  )),15)).append("\n");
            sbuff.append("tdk termasuk denda keterlambatan").append("\n");
            sbuff.append("\n");
            sbuff.append("Tgl janji bayar anda: ").append( fixLeft(order.getJanjiBayar(),10)).append("\n");
            sbuff.append("\n");
            sbuff.append("Silakan melakukan pembayaran di:").append("\n");
            Nson n = getVA(activity, order.getNoPsb().trim());
            sbuff.append("Alfa/indomart :").append(fixLeft (n.get("VA_ALFA").asString(),16)).append("\n");
            sbuff.append("Kode Perusahaan: KAMM          ").append("\n");
            sbuff.append("\n");
            sbuff.append("Transfer Virtual Bank           ").append("\n");
            sbuff.append("Nomor Virtual Account           ").append("\n");

            sbuff.append("BCA       : ").append(fixLeft (n.get("VA_BCA").asString(),20)).append("\n");
            sbuff.append("BRI       : ").append(fixLeft (n.get("VA_BRI").asString(),20)).append("\n");
            sbuff.append("BNI       : ").append(fixLeft (n.get("VA_BNI").asString(),20)).append("\n");
            sbuff.append("").append("\n");
            sbuff.append("================================").append("\n");
            sbuff.append("").append("\n");
            sbuff.append("Apabila ada kesulitan, silakan  ").append("\n");
            sbuff.append("wa ke nomor 085217171118        ").append("\n");
            sbuff.append("Dan informasikan ke kami apabila").append("\n");
            sbuff.append("ada perubahan Tlp atau Alamat   ").append("\n");
            sbuff.append("").append("\n");
            sbuff.append("").append("\n");
            sbuff.append("Penerima                        ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            sbuff.append("                                ").append("\n");
            //sbuff.append("(").append( fixLeft(order.getNama()+")",30) ).append("\n");
            sbuff.append("(                     )         ").append("\n");
            sbuff.append("\n");
            sbuff.append("\n");
            sbuff.append("\n");

//Utility.setSetting(LoginActivity.this, Utility.MD5("US")



            return  sbuff;
        }
        return sbuff;
    }
    private void printIT(int i){
        final StringBuffer sbuff = new StringBuffer("");




        if (getIntentStringExtra("status").equalsIgnoreCase("TB")) {
            sbuff.setLength(0);//clear



           /* if (checklist.isChecked()){
                sbuff.append(getPrintTB_(this, order));
                sbuff.append(getPrintCheckList(this, order));
            }else{
                sbuff.append(getPrintTB(this, order));
            }
            */
            sbuff.append(getPrintTB_(this, order));
            sbuff.append(getPrintCheckList(this, order));
        }else if (getIntentStringExtra("status").equalsIgnoreCase("JB")){
            sbuff.setLength(0);//clear
            if (Utility.getSetting(ListViewDetails3FirstDesignActivity.this, Utility.MD5("FB"),"").trim().equalsIgnoreCase("KK")){
                //boleh ngeprint
                sbuff.append(getPrintJB(this, order));
            }else{
                updateBHC();
                return;
            }
        }else{
            String tanggal ="";
            if (order.getTglBayar() != null ){
                if (!order.getTglBayar().equalsIgnoreCase("")){

                    //sbuff.append("Tgl ").append(order.getTglBayar().substring(0,10) ).append(" Jam Bayar ").append(order.getTglBayar().substring(11,16) ).append("\n");
                    String date = order.getTglBayar().trim();
                    if (AppUtil.isAlpa(date)){
                        date = AppUtil.ConvetToNow(date);
                    }
                    sbuff.append("Tgl ").append( date.substring(0,10) ).append(" Jam Bayar ").append( date.substring(11,16) ).append("\n");
                }else{
                    sbuff.append("Tgl ").append(Now().substring(0,10) ).append(" Jam Bayar ").append(Now().substring(11,16) ).append("\n");
                }
            }

            sbuff.append("Nama Collector   :").append(getdata("nama_ao")).append("\n");

            sbuff.append(getCode(getdata("nokwitansi"),  order.getNoPsb(), getdata("kodepos"),  AppUtil.getSetting(ListViewDetails3FirstDesignActivity.this, "USERNAME", "") ,order.getJmlTotTerbayar() )).append("\n");
            sbuff.append("").append("\n");
            sbuff.append("NO PSB   :").append(getdata("no_psb")).append("\n");
            sbuff.append("TGL JT   :").append(getdata("tgl_jtempo1").substring(0,10)).append("\n");
            sbuff.append("Nama     :").append(getdata("nama")).append("\n");// Utility.getSetting(Payment2.this, "REALNAME", "")
            sbuff.append("NO. Pol  :").append(getdata("nopol")).append("\n");
            sbuff.append("").append("\n");


            if (order.getStatusBayar().equalsIgnoreCase("LAIN")){
                sbuff.append("").append("\n");
                sbuff.append("Byr Denda   :").append(rightM(AppUtil.formatCurrency(order.getByrDenda()))).append("\n");
                sbuff.append("Byr Kunjungan").append(rightM((AppUtil.formatCurrency(order.getByrSisa())))).append("\n");
                sbuff.append("Titipan Angs:").append(rightM(AppUtil.formatCurrency( order.getDiscount()  ))).append("\n");
                sbuff.append("Total Bayar :").append(rightM(AppUtil.formatCurrency(  order.getJmlTotTerbayar()   ))).append("\n");
                sbuff.append("").append("\n");
            }else{
                sbuff.append("").append(( txtAng)).append("\n");
                sbuff.append("Nilai Ang   :").append(rightM((AppUtil.formatCurrency(order.getJumlahangsuran())))).append("\n");
                sbuff.append("Denda       :").append(rightM(AppUtil.formatCurrency(order.getByrDenda()))).append("\n");
                sbuff.append("By Tagih    :").append(rightM(AppUtil.formatCurrency(order.getByrSisa() ))).append("\n");
                sbuff.append("Total Bayar :").append(rightM(AppUtil.formatCurrency( order.getJmlTotTerbayar()) )).append("\n");
                sbuff.append("").append("\n");

                //hitungan denda sisa
                dendasisa=AppUtil.getInt(getdata("dendasisa"));
                angsisa  =AppUtil.getInt(getdata("angsisa"));


                sbuff.append("Denda Tersisa :").append(rightM(AppUtil.formatCurrency( order.getDendasisa() ),16)).append("\n");
                try {
                    //new
                    int angsuran_yang_tersisa =
                            Utility.getInt(order.getTenor())-
                                    (Utility.getInt(order.getKeAwal())+1)*Utility.getInt(order.getJmlAngsuran())-
                                    (
                                            Utility.getInt(   order.getJmlTotTerbayar())-
                                                    Utility.getInt(order.getByrSisa())-
                                                    Utility.getInt(  order.getByrDenda())
                                    );

                    //(CAST(tenor AS int)-CAST(ke_awal AS int)+1)*jml_angsuran)-(jml_tot_terbayar-Byr_sisa-byr_denda)) as sisa_ang_struk
                    //sbuff.append("Ang. Tersisa  :").append(rightM(String.valueOf(angsuran_yang_tersisa), 16)).append("\n");
                }catch (Exception e) {
                    if (order.getSisaAngsuran() != null) {
                        if (order.getSisaAngsuran().equalsIgnoreCase("0") || order.getSisaAngsuran().equalsIgnoreCase("")) {
                            sbuff.append("Ang. Tersisa  :").append(rightM("0", 16)).append("\n");
                        } else {
                            int angsuran_yang_tersisa = Utility.getInt(order.getSisaAngsuran()) - Utility.getInt(order.getJumlahangsuran());
                            if (angsuran_yang_tersisa >= 0) {
                                sbuff.append("Ang. Tersisa  :").append(rightM(String.valueOf(angsuran_yang_tersisa), 16)).append("\n");
                            } else {
                                sbuff.append("Ang. Tersisa  :").append(rightM("0", 16)).append("\n");
                            }
                        }
                    } else {
                        sbuff.append("Ang. Tersisa  :").append(rightM("0", 16)).append("\n");
                    }
                }

            }

            sbuff.append("").append("\n");
            sbuff.append("Nomor Virtual Account\n");
            Nson n = getVA(ListViewDetails3FirstDesignActivity.this,getdata("no_psb"));
            sbuff.append("Alfa/Indo : ").append(n.get("VA_ALFA").asString()).append("\n");
            sbuff.append("BCA       : ").append(n.get("VA_BCA").asString()).append("\n");
            sbuff.append("BRI       : ").append(n.get("VA_BRI").asString()).append("\n");
            sbuff.append("BNI       : ").append(n.get("VA_BNI").asString()).append("\n");
            sbuff.append("").append("\n");
            sbuff.append("").append("\n");
            // sbuff.append("Note: No telp anda ").append(getdata("telp")).append("\n");
            sbuff.append("SMS WA CENTER KAMM, ").append("\n");
           // sbuff.append("08999731188. Harap hubungi kami").append("\n");
            sbuff.append("085217171118 Harap hubungi kami").append("\n");

            sbuff.append("bila ada perubahan Nomor Telpon Anda").append("\n");
            sbuff.append("").append("\n");
            sbuff.append("Bila dikunjungi/ditagih maka").append("\n");
            sbuff.append("dibebankan Biaya Tagih ").append("\n");
            sbuff.append("Rp.10.000/Kwitansi  ").append("\n");
            //sbuff.append("(Dibayar di kantor atau saat  Pelunasan BPKB).").append("\n");
            //sbuff.append("Tidak Dibayar ke Kolektor KAMM.").append("\n");
            sbuff.append("").append("\n");
            sbuff.append("").append("\n");

            //sbuff.append(order.xPrint());
        }

        if (mService!=null  ){
            if (mSocket != null) {
                if (mSocket.isConnected()){
                }else{
                    newBT();
                }
            }else{
                newBT();
            }
        }else{
            newBT();
        }

        if (SelectedDevices.equals("") ) {
            printerName="sprt";
        }
        Messagebox.showProsesBar(ListViewDetails3FirstDesignActivity.this, new Messagebox.DoubleRunnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                }catch (Exception e){}
                if (printerName.equals("sprt")) {
                    printSprt("sprt", LogoAsset(ListViewDetails3FirstDesignActivity.this, imageName), sbuff.toString());
                }else{
                    printBT(SelectedBDAddress,  GetLogo(ListViewDetails3FirstDesignActivity.this, sbuff.toString()));
                }
            }

            @Override
            public void runUI() {
                if( closBT()) {
                    if (getIntentStringExtra("status").equalsIgnoreCase("TB")) {
                        if (iTB == 0) {
                            rePrintTB();
                        } else {
                            updateBHC();
                        }
                    }else if (getIntentStringExtra("status").equalsIgnoreCase("JB")){
                        if (iTB == 0){
                            rePrintTB();//rePrintJB
                        }else{
                            updateBHC();
                        }
                    }else{
                        updateBHC();
                    }
                    iTB++;
                    /*setResult(AppController.CLOSE_CODE);
                    finish();*/
                }
            }
        });


    }
    int iTB = 0;
    private void rePrintTB(){
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(this);
        dlg.setTitle("ISI REPRINT");
        dlg.setMessage("Apakah Ingin Reprint ?");
        dlg.setPositiveButton("IYA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                printIT(0);
            }
        });
        dlg.setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateBHC();
            }
        });

        final android.app.AlertDialog alertDialog = dlg.create();

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void updateBHC(){
        android.app.AlertDialog.Builder dlg = new android.app.AlertDialog.Builder(this);
        dlg.setTitle("ISI BHC");
        dlg.setMessage("Apakah Ingin mengisi BHC ?");
        dlg.setPositiveButton("IYA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("smode","BHC");
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        dlg.setNegativeButton("LAIN KALI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        final android.app.AlertDialog alertDialog = dlg.create();

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public String rightM(String s){
        return rightM(s, 18);
    }
    public String rightM(String s, int max){
        s="                     "+s;
        return s.substring(s.length()-max,s.length());
    }
    public String getCode(String kwit, String nopsb, String posko, String coll, String total){
		/*
		KODE KWITANSI By DIGIT
		TERSEDIA  32  DIGIT
		no kwit	no pp	kode posko  (tanpa "P")	kode Koll  (tanpa "CT")	Total Digit
		Q030000006993C	00012345	P21	CT.145
		Q030000006993C	00012345	21	145
		14	8	2	3	27
		*/
        kwit=kwit+"00000000000000";
        nopsb="000000000"+nopsb;
        posko="000"+posko;
        coll="0000"+coll;
        total="0000000000000000000000000000"+total;
        String rePrint = "";

            if (order.getPrintStatus()==null) {

            }else if (order.getPrintStatus().equals("1")||order.getPrintStatus().equals("2")) {
                rePrint="";
            }

        return kwit.substring(0,14)+nopsb.substring(nopsb.length()-8,nopsb.length())+posko.substring(posko.length()-2,posko.length())+coll.substring(coll.length()-3,coll.length())+rePrint;
    }
    public static boolean isBluetoothAvailable(Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
    }

    public void ShowMessageBox(final String message, final String title) {
        m_handler.post(new Runnable() {

            @Override
            public void run() {
                LayoutInflater inflater = LayoutInflater.from(ListViewDetails3FirstDesignActivity.this);
                View view = inflater.inflate(R.layout.messagebox, null);

                TextView textview=(TextView)view.findViewById(R.id.textmsg);

                textview.setText(message);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListViewDetails3FirstDesignActivity.this);
                builder.setTitle(title)
                        .setCancelable(false)
                        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                                Intent intentOpenBluetoothSettings = new Intent();
                                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                                startActivity(intentOpenBluetoothSettings);
                            }
                        });
                builder.setView(view);
                AlertDialog alert = builder.create();

                alert.show();

            }
        });
    }


    private String dataPrint="";
    private byte[] dataPrintb=null;
    public BluetoothService mService = null;
    public BluetoothSocket mSocket;
    public OutputStream mSocketOut =null;

    public void newBT( ){
        mService = new BluetoothService( this,  getHandler()){
            @Override
            public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
                mSocket=socket;

                super.connected(socket, device);
            }
        } ;
    }
    public boolean closBT( ){
        try {
            if (mSocket!=null) {
                mSocket.close();
                return true;
            }
        } catch (Exception e) {

            //order.setStatus("pending_bayar");
            //order.save();
            return true; }
        try {
            if (mSocketOut!=null) {
                //mSocketOut.close();
                //order.setStatus("pending_bayar");
                //order.save();
                return true;
            }
        } catch (Exception e) { return true; }
        return true;
    }
    public void printBT(String mac, String data ){
        dataPrint=data+"\n\n\n";
        try {
            if (mService.getState()==BluetoothService.STATE_CONNECTED) {
                if (!dataPrint.equals("")) {
                    mService.sendMessage(dataPrint, "GBK");
                }
                dataPrint="";
            }else{
                mService.connect(mService.getDevByMac(mac));
            }
        } catch (Exception e) { }
    }

    public void printBT(String mac, byte[] data ){
        dataPrintb=data;
        try {
            if (mService.getState()==BluetoothService.STATE_CONNECTED) {
                if (mSocketOut==null) {
                    mSocketOut = mSocket.getOutputStream();
                }
                mSocketOut.write(dataPrintb);
                mSocketOut.flush();
                dataPrintb=null;
            }else{
                mService.connect(mService.getDevByMac(mac));
            }
        } catch (Exception e) {
            //order.setStatus("pending_bayar");
            //order.save();
        }
    }

    private BluetoothPrinter mPrinter;
    public void printSprt(String name, byte[] data, String text ){
        try {
            if (mPrinter!=null) {
            }else{
                mPrinter = new BluetoothPrinter(name);
            }
            int ret = 0;
            if (mPrinter.isConnected()) {

            }else{
                mPrinter.setCurrentPrintType(BluetoothPrinter.PrinterType.TIII);
                ret = mPrinter.open();
            }

            if (ret == 0) {
                if (data!=null) {
                    mPrinter.send(data)	;
                    mPrinter.setPrinter(4);
                }


                if (text!=null) {
                    byte[] charLarge1 = { 0x1d, 0x21, 0 };
                    mPrinter.send(charLarge1);

                    mPrinter.send(text)	;
                    mPrinter.setPrinter(4);
                }

                //mPrinter.close();
            }else{

                //Toast.makeText(this, "Connect Failed", Toast.LENGTH_SHORT);
            }
        } catch (Exception e) {
            //order.setStatus("pending_bayar");
            //order.save();
        }
    }

    public Handler getHandler(){
        return mHandler;
    }
    public BluetoothService getBluetoothService(){
        return mService;
    }
    private final  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();

            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            Toast.makeText(getApplicationContext(), "Connect successful",  Toast.LENGTH_SHORT).show();
                            try {
                                new Thread(new Runnable() {
                                    public void run() {
                                        try {



                                            if (!dataPrint.equals("")) {
                                                mService.sendMessage(dataPrint, "GBK");
                                            }else if (dataPrintb!=null){
                                                mSocketOut = mSocket.getOutputStream();
                                                mSocketOut.write(dataPrintb);
                                                mSocketOut.flush();
                                            }
                                            dataPrintb=null;
                                            dataPrint="";
                                        } catch (Exception e) { }
                                    }
                                }).start();
                            } catch (Exception e) { }

                            break;
                        case BluetoothService.STATE_CONNECTING:

                            break;
                        case BluetoothService.STATE_LISTEN:

                        case BluetoothService.STATE_NONE:

                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    Toast.makeText(getApplicationContext(), "Device connection was lost", Toast.LENGTH_SHORT).show();
                    //onActivityResult(12345,  00001, intent );
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    Toast.makeText(getApplicationContext(), "Unable to connect device", Toast.LENGTH_SHORT).show();
                    //onActivityResult(12345,  00002, intent );
                    break;
            }
        }

    };



    public byte[] GetLogo(Activity form , String data) {
        String logo = "";

        try {
            InputStream is = form.getAssets().open("logo.jpg");//n.png
            Bitmap img0 = BitmapFactory.decodeStream(is);

            boolean[] dots = getBitmapMonoChromeWithScalePrinter(img0);


            ByteArrayOutputStream out= new ByteArrayOutputStream();



            int threshold=127;

            int multiplier = 570; // this depends on your printer model. for Beiyang you should use 1000
            double scale = (double)(multiplier/(int)img0.getWidth());
            int iheight = (int)(img0.getHeight() * scale);
            int iwidth = (int)(img0.getWidth() * scale);



            int integer = iwidth;
            byte[] width = new byte[4];
            for (int i = 0; i < 4; i++) {
                width[i] = (byte)(integer >>> (i * 8));
            }
            byte[] widthmsb=  new byte[] {
                    (byte)(integer >>> 24),
                    (byte)(integer >>> 16),
                    (byte)(integer >>> 8),
                    (byte)integer};


            int offset = 0;


            out.write((char)0x1B);
            out.write('3');
            out.write((byte)0);

            while (offset < iheight){


                //raster error
                //out.write((char)0x1D);
                //out.write((char)0x76);
                // out.write((char)0x30);

                //bit image
                out.write((char)0x1B);
                out.write('*');         // bit-image mode
                out.write((byte)33);    // 24-dot double-density
                out.write(width[0]);  // width low byte
                out.write(width[1]);  // width high byte


                for (int x = 0; x < iwidth; ++x)   {
                    for (int k = 0; k < 3; ++k)  {
                        byte slice = 0;

                        for (int b = 0; b < 8; ++b) {
                            int y = (((offset / 8) + k) * 8) + b;
                            // Calculate the location of the pixel we want in the bit array.
                            // It'll be at (y * width) + x.
                            int i = (y * iwidth) + x;

                            // If the image is shorter than 24 dots, pad with zero.
                            boolean v = false;
                            if (i < dots.length) {
                                v = dots[i];
                            }
                            slice |= (byte)((v ? 1 : 0) << (7 - b));
                        }

                        out.write(slice);
                    }
                }
                offset += 24;
                out.write((char)0x0A);
            }


            //fort 12x24
            out.write((char)0x1B);
            out.write((char)0x4D);
            out.write((byte)0);

            // Restore the line spacing to the default of 30 dots.
            out.write((char)0x1B);
            out.write('2');
            //out.write((byte)32);

            out.write(data.getBytes());;

            return out.toByteArray();
        } catch (Exception e) {
            order.setStatus("pending_bayar");//??????????????
            order.save();
        }
        return new byte[0];
    }


    public boolean[] getBitmapMonoChromeWithScalePrinter(Bitmap img0) {
        int height = img0.getHeight();
        int width = img0.getWidth();


        int threshold=127;

        int multiplier = 570; // this depends on your printer model. for Beiyang you should use 1000
        double scale = (double)(multiplier/(int)width);
        int xheight = (int)(height * scale);
        int xwidth = (int)(width * scale);
        int dimensions = xwidth * xheight;


        int index=0;
        boolean[] dots= new boolean[dimensions];

        for (int y = 0; y < xheight; y++) {
            for (int x = 0; x < xwidth; x++)  {
                int _x = (int)(x / scale);
                int _y = (int)(y / scale);
                int rgb = img0.getPixel(_x, _y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb & 0xFF);
                //int gray = (r + g + b) / 3;
                int luminance = (int)(r* 0.3 + g * 0.59 + b * 0.11);
                dots[index] =    (luminance < threshold);
                index++;
            }
        }
        return dots;
    }


    public static byte[] Logo (Activity form, String imageName){
        if (imageName.length()>=3) {
            try {
                new File("/sdcard/logo.jpg").delete();
            } catch (Exception e) {}
            try {
                InputStream is = form.getAssets().open("logo.jpg");
               // Utility.copyFile(is, "/sdcard/logo.jpg");
            } catch (Exception e) { }
        }
        PrintGraphics pg = new PrintGraphics();
        pg.initCanvas(540);
        pg.initPaint();
        pg.drawImage(0, 0, "/sdcard/logo.jpg");
        pg.printPng();

        return pg.printDraw();
    }
    private String printerName= "";
    private String imageName= "";

    private static String fixLeft(String s, int max){
        StringBuilder stringBuilder = new StringBuilder(String.valueOf(s));
        for (int i = 0; i < max+2; i++) {
            stringBuilder.append(" ");
        }
        s = stringBuilder.toString();
        if (s.length()>max) {
            return (s.substring(0, max)) ;
        }
        return "";
    }
    private static String fixCenter(String str, int max){
        /*StringBuilder stringBuilder = new StringBuilder(String.valueOf(s));
        for (int i = 0; i < max+2; i++) {
            stringBuilder.append(" ");
        }
        s = stringBuilder.toString();
        if (s.length()>max) {
            return (s.substring(0, max)) ;
        }
        return "";*/

        int l =  max;
        StringBuilder stringBuilder = new StringBuilder();
        str = str.substring(0, Math.min(l, str.length() ));
        int lc =  (l-str.length())/2;
        for (int i = 0; i < lc; i++) {
            stringBuilder.append(" ");
        }
        stringBuilder.append(str);
        for (int i = stringBuilder.length(); i < l; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();

    }
    private static String leftsubstring(String s, int max){

        if (s.length()>max) {
            return s.substring(0, max);
        }
        return s;
    }
    private static String rightCcurrency(String s, int max){
        s="                                          "+s;
        return s.substring(s.length()-max, s.length());
    }



    public static byte[] PrintImage (Activity form, String imageName){

        try {
            InputStream is = form.getAssets().open("lg.png", AssetManager.ACCESS_BUFFER);
            Bitmap btm = BitmapFactory.decodeStream(is);
            PrintImage(btm);
        }catch (Exception e) { }
        return PrintImage(null);
    }
    public static  byte[]  PrintImage(Bitmap img0) {
        try {

            int width = img0.getWidth()/8;
            int heigh = img0.getHeight();



            byte x[]=new byte[width*heigh+8];


            x[0]=(byte) 29;
            x[1]=(byte) 118;
            x[2]=(byte) 48;
            x[3]=(byte) 0;
            x[4]=(byte) ( width  );
            x[5]=(byte) 0;
            x[6]=(byte) heigh;
            x[7]=(byte) 0;

            int w = img0.getWidth();
            int hi = img0.getHeight();
            int bytesOfWidth = w / 8 + (w % 8 != 0 ? 1 : 0);

            x[4] = (byte) (bytesOfWidth % 256);
            x[5] = (byte) (bytesOfWidth / 256);
            x[6] = (byte) (hi % 256);
            x[7] = (byte) (hi / 256);



            int ix = 7;
            for (int h = 0; h < heigh; h++) {

                for (int k = 0; k < width ; k++)  {
                    ix++;
	    		/* 267 */         int c0 = img0.getPixel(k * 8 + 0, h);
	    		/*     */         int p0;
	    		/* 270 */         if (c0 == -1)
	    		/* 271 */           p0 = 0;
	    		/*     */         else {
	    		/* 273 */           p0 = 1;
	    		/*     */         }
	    		/* 275 */         int c1 = img0.getPixel(k * 8 + 1, h);
	    		/*     */         int p1;
	    		/* 276 */         if (c1 == -1)
	    		/* 277 */           p1 = 0;
	    		/*     */         else {
	    		/* 279 */           p1 = 1;
	    		/*     */         }
	    		/* 281 */         int c2 = img0.getPixel(k * 8 + 2, h);
	    		/*     */         int p2;
	    		/* 282 */         if (c2 == -1)
	    		/* 283 */           p2 = 0;
	    		/*     */         else {
	    		/* 285 */           p2 = 1;
	    		/*     */         }
	    		/* 287 */         int c3 = img0.getPixel(k * 8 + 3, h);
	    		/*     */         int p3;
	    		/* 288 */         if (c3 == -1)
	    		/* 289 */           p3 = 0;
	    		/*     */         else {
	    		/* 291 */           p3 = 1;
	    		/*     */         }
	    		/* 293 */         int c4 = img0.getPixel(k * 8 + 4, h);
	    		/*     */         int p4;
	    		/* 294 */         if (c4 == -1)
	    		/* 295 */           p4 = 0;
	    		/*     */         else {
	    		/* 297 */           p4 = 1;
	    		/*     */         }
	    		/* 299 */         int c5 = img0.getPixel(k * 8 + 5, h);
	    		/*     */         int p5;
	    		/* 300 */         if (c5 == -1)
	    		/* 301 */           p5 = 0;
	    		/*     */         else {
	    		/* 303 */           p5 = 1;
	    		/*     */         }
	    		/* 305 */         int c6 = img0.getPixel(k * 8 + 6,h);
	    		/*     */         int p6;
	    		/* 306 */         if (c6 == -1)
	    		/* 307 */           p6 = 0;
	    		/*     */         else {
	    		/* 309 */           p6 = 1;
	    		/*     */         }
	    		/* 311 */         int c7 = img0.getPixel(k * 8 + 7,h);
	    		/*     */         int p7;
	    		/* 312 */         if (c7 == -1)
	    		/* 313 */           p7 = 0;
	    		/*     */         else {
	    		/* 315 */           p7 = 1;
	    		/*     */         }
	    		/* 317 */         int value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 +
	    		/* 318 */           p5 * 4 + p6 * 2 + p7;
	    		/* 319 */          x[ix] = ((byte)value);
	    		/*     */       }




            }

            return x;
        } catch (Exception e) {return   ( "" ).getBytes();}


    }


    @Override
    protected void onDestroy() {
        closBT();

        if (mPrinter!=null) {
            try {
                mPrinter.close();
            } catch (Exception e) {
                //order.setStatus("pending_bayar");
                //order.save();
            }
        }
        super.onDestroy();
    }



    public static byte[] LogoAsset (Activity form, String imageName){
       PrintGraphics pg = new PrintGraphics();
        pg.initCanvas(380);
        pg.initPaint();
        try {
            InputStream is = form.getAssets().open("logo.jpg", AssetManager.ACCESS_BUFFER);
            pg.drawImage(0, 0, is);
        }catch (Exception e) { }
        return pg.printDraw();
    }

    public RequestBody toRequestBody(String value) {
        if (value==null){
            value="";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }


    public void setGPS(){
        final RxGps rxGps = new RxGps(this);
        String xGps = Utility.getSetting(ListViewDetails3FirstDesignActivity.this, "GPS","");
        String[] arrGps = Utility.split(xGps+",",",");
        order.setLongitude(arrGps[0]);
        order.setLangitude(arrGps[1]);


        rxGps.lastLocation()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(location -> {
                    exitIfMockLocationOn(location, getActivity());
                   order.setLongitude(String.valueOf(location.getLongitude()));
                   order.setLangitude(String.valueOf(location.getLatitude()));
                }, throwable -> {
                    if (throwable instanceof RxGps.PermissionException) {
                        displayError(throwable.getMessage());
                    } else if (throwable instanceof RxGps.PlayServicesNotAvailableException) {
                        displayError(throwable.getMessage());
                    }
                });
    }

    public void displayError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
