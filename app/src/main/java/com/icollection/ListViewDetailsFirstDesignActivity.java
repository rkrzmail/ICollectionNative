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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.icollection.modelservice.News;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.ImageUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;
import com.naa.data.Nson;
import com.sdk.bluetooth.android.BluetoothPrinter;
import com.zj.btsdk.BluetoothService;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icollection.util.AppUtil.Now;
import static com.icollection.util.AppUtil.NowX;


/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewDetailsFirstDesignActivity extends AppCompatActivity {
    private static final String LOG_TAG = ListViewDetailsFirstDesignActivity.class.getSimpleName();
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    Toolbar toolbar;
    ImageView image;

    TextView lblNo, lblName,lblBayar,lblAngsuran,lblAngsurand;

    Button btnScan,btnPrint, btnPrintCheckList;
    private Handler m_handler = new Handler(); // Main thread

    EditText txtNoKwintasi,txtJmlAngsuran,txtDenda,txtJmlByTagih,txtTotal,txtTerbayar,txtDiscount,txtSisa;
    OrderItem order;
    private View parentView;
    private FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    boolean isFABHided = false;
    AppController appController;


    private String SelectedBDAddress="";
    private String SelectedDevices="";
    private String curStatusMsg;

    private Hashtable<String, String> buf = new Hashtable<String, String>();
    private String status;
    private String rprint;
    int dendasisa=0;
    int angsisa  =0;

    public static void navigate(AppCompatActivity activity, OrderItem obj) {
        Intent intent = new Intent(activity, ListViewDetailsFirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);

        ActivityCompat.startActivityForResult(activity, intent,AppController.REQUEST_DETAIL, null);
    }
    public static void navigateAllX(AppCompatActivity activity, OrderItem obj, String id) {
        Intent intent = new Intent(activity, ListViewDetailsFirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("id", id);
        ActivityCompat.startActivityForResult(activity, intent,AppController.REQUEST_DETAIL, null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // pastikan close terlebih dahulu


                    SelectedBDAddress = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    SelectedDevices = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);

                    if(SelectedBDAddress!=null || !SelectedBDAddress.equalsIgnoreCase("")){

                        btnPrint.setEnabled(true);
                        btnPrintCheckList.setEnabled(true);
                        btnScan.setText("Paired to:"+SelectedBDAddress);
                    }else{
                        btnPrint.setEnabled(false);
                        btnPrintCheckList.setEnabled(false);
                        btnScan.setText("Scan");
                    }


                    return;
                }else{
                    btnPrint.setEnabled(false);
                    btnPrintCheckList.setEnabled(false);
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
                btnPrintCheckList.setEnabled(false);
                btnScan.setText("Scan");
                break;
        }
    }

    private void xAll(){
        String id = getIntent().getStringExtra("id");

        findViewById(R.id.response).setVisibility(View.GONE);
        if (id!=null){
            findViewById(R.id.allx).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.lblNoID)).setText(id);
            findViewById(R.id.response).setVisibility(View.VISIBLE);

            txtNoKwintasi.setEnabled(false);
            txtJmlAngsuran.setEnabled(false);
            txtDenda.setEnabled(false);
            txtJmlByTagih.setEnabled(false);
            txtTotal.setEnabled(false);
            txtTerbayar.setEnabled(false);
            txtDiscount.setEnabled(false);
            txtSisa.setEnabled(false);

            findViewById(R.id.alltbl).setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_details_first_design);
        parentView = findViewById(android.R.id.content);
        appController = (AppController) getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnScan =(Button)findViewById(R.id.btnScan);
        btnPrint =(Button)findViewById(R.id.tblPrint);


        ViewCompat.setTransitionName(findViewById(R.id.image), "order");
        btnPrintCheckList=(Button)findViewById(R.id.tblPrintCheckList);

        btnPrintCheckList.setVisibility(View.GONE);
        if (getIntent().getParcelableExtra("order") instanceof OrderItem){
            order = getIntent().getParcelableExtra("order");


            if (String.valueOf(order.getStatusBayar()).startsWith("TARIK BARANG")||String.valueOf(order.getStatusBayar()).startsWith("TB")){
                btnPrintCheckList.setVisibility(View.VISIBLE);
            }else   if (String.valueOf(order.getStatusBayar()).startsWith("JB")){
                //view
                TextView title = findViewById(R.id.txtPriview);
                findViewById(R.id.lnrPrev).setVisibility(View.VISIBLE);
                findViewById(R.id.lnrBayar).setVisibility(View.GONE);
                title.setText(ListViewDetails3FirstDesignActivity.getPrintJB(this, order));

                if (Utility.getSetting(ListViewDetailsFirstDesignActivity.this, Utility.MD5("FB"),"").trim().equalsIgnoreCase("KK")){
                    //boleh ngeprint
                }else{
                    findViewById(R.id.alltbl).setVisibility(View.GONE);
                    btnScan.setVisibility(View.INVISIBLE);
                    btnPrint.setVisibility(View.INVISIBLE);
                }
            }
        }else{
            finish();
            return;
        }


        //image = (ImageView) findViewById(R.id.image);


        txtNoKwintasi= (EditText) findViewById(R.id.txtNoKwintasi);
        txtJmlAngsuran= (EditText) findViewById(R.id.txtJmlAngsuran);
        txtDenda= (EditText) findViewById(R.id.txtDenda);
        txtJmlByTagih= (EditText) findViewById(R.id.txtJmlByTagih);
        txtTotal= (EditText) findViewById(R.id.txtTotal);
        txtTerbayar= (EditText) findViewById(R.id.txtTerbayar);
        txtDiscount= (EditText) findViewById(R.id.txtDiscount);
        txtSisa= (EditText) findViewById(R.id.txtSisa);



        btnScan.setText("Scan Bluetooth");
        btnPrint.setEnabled(false);
        btnPrintCheckList.setEnabled(false);

        setAllData();
        if( SelectedBDAddress.equalsIgnoreCase("")){
            btnPrint.setEnabled(false);
            btnPrintCheckList.setEnabled(false);
        }else{
            btnScan.setText("Paired to:"+SelectedBDAddress);
        }
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isBluetoothAvailable(getApplicationContext())){
                    Intent serverIntent = null;
                    serverIntent = new Intent(ListViewDetailsFirstDesignActivity.this, DeviceListActivity.class);
                    startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                }else{
                    ShowMessageBox("Bluetooth anda harus diaktifkan terlebih dahulu!", "Perhatian");

                }


                //setResult(AppController.CLOSE_CODE);
                //finish();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printITwithGPS(0);
            }

        });


        btnPrintCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printITwithGPS(9);
            }

        });


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



        xAll();
        lockAll();
    }
    String txtAng="";
    private void setAllData() {
        if (order != null) {
            txtAng= "ANGS KE "+ order.getKeAwal() +" S/D "+ AppUtil.setZero(order.getJml_angsuran_ke()) ;
/*
            txtNoKwintasi.setText(order.getNoKwitansi());
            txtJmlAngsuran.setText(order.getJmlAngsuran());
            txtDenda.setText(order.getJmlDenda());
            txtJmlByTagih.setText(order.getJmlBayarTagih());
            txtTotal.setText(order.getJmlTotTerbayar());
            txtTerbayar.setText(order.getJmlBayarTagih());
            txtDiscount.setText(order.getDiscount());
            txtSisa.setText(order.getSisaAngsuran());

*/
            filldatax(order.getNoPsb(),R.id.lblNo);
            filldatax(order.getNama(), R.id.lblName);

            filldatax(order.getKeAwal(), R.id.lblAngno);
            filldatax(order.getKeAkhir(), R.id.lblAngsd);

            filldatax(order.getJmlAngsuran(),  R.id.txtJmlAngsuran);
            filldatax(order.getJmlDenda(), R.id.txtDenda);
            filldatax(order.getJmlBayarTagih(),  R.id.txtJmlByTagih);
            filldatax(order.getJmlTotTerbayar(),  R.id.txtTerbayar);

            filldatax(order.getTglBayar(), R.id.lblBayar);

            filldatax(order.getDiscount(),  R.id.txtDiscount);
            filldatax(order.getNoKwitansi(),  R.id.txtNoKwintasi);




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
            filldata(order.getAngsisa(),"angsisa");
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




            filldatax(order.getJumlahSisa(),  R.id.txtSisa);


            ((TextView)findViewById(R.id.lblSisa)).setText("Sisa ("+(jsisa!=null?jsisa:"")+"Angsuran)");


            ((EditText)findViewById(R.id.response)).setText(Utility.getSetting(getApplicationContext(),order.getId() +"-"+ order.getNoPsb(), ""));

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



    private void fabToggle() {
        if (appController.isAddedToFavorites(order)) {
            fab.setImageResource(R.mipmap.ic_favorite_white_24dp);
        } else{
            fab.setImageResource(R.mipmap.ic_favorite_border_white_24dp);
        }
    }

    private void animateFAB(final boolean hide) {
        if (isFABHided && hide || !isFABHided && !hide) return;
        isFABHided = hide;
        int moveY = hide ? (2 * fab.getHeight()) : 0;
        fab.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void filldatax(String value, int res){
        View v = findViewById(res);
        if (v instanceof TextView) {
            ((TextView)v).setText(value);
        }else if (v instanceof EditText) {
            ((EditText)v).setText(value);
        }
    }


    boolean resultNya ;
    ProgressDialog progressDialog;
    public boolean save() {
        resultNya = false;

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),new byte[0]);

// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);
        RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),new byte[0]);
        MultipartBody.Part bukti =
                MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);



        progressDialog = new ProgressDialog(ListViewDetailsFirstDesignActivity.this);
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
                // toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
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

                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                    order.setPrintStatus("1");
                    btnPrint.setEnabled(true);
                    btnPrintCheckList.setEnabled(true);
                    resultNya = true;
                }else{
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
             //   Toast.makeText(getApplicationContext(), "Nothing happen", Toast.LENGTH_LONG).show();
               // resultNya = false;
                LoginActivity.autoLogout(ListViewDetailsFirstDesignActivity.this, response);
            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Koneksi bermasalah", Toast.LENGTH_LONG).show();
                resultNya = false;
            }
        });

        return resultNya;
    }

    @Override
    protected void onStart() {
        super.onStart();
       lockAll();
        try {
            if (order.getStatus().equalsIgnoreCase("0")) {
                ((Button)findViewById(R.id.tblPrint)).setText("Print");
                //((TextView)findViewById(R.id.txtTerbayar)).setEnabled(true);
                ((Button)findViewById(R.id.tblPrint)).setEnabled(true);
            }else if (order.getStatus().equals("1")) {
                ((Button)findViewById(R.id.tblPrint)).setText("Reprint");
            }else if (order.getStatus().equals("2")) {
                ((Button)findViewById(R.id.tblPrint)).setText("Reprint");
                ((Button)findViewById(R.id.tblPrint)).setEnabled(true);
            }else{
                ((Button)findViewById(R.id.tblPrint)).setText("Print");
               // ((TextView)findViewById(R.id.txtTerbayar)).setEnabled(true);
                ((Button)findViewById(R.id.tblPrint)).setEnabled(true);
            }
            if (order.getPrintStatus() == null || order.getPrintStatus().equals("0")) {
            }else if (order.getPrintStatus().equals("1")) {
                ((Button)findViewById(R.id.tblPrint)).setEnabled(true);
            }
        }catch (Exception e){}
    }

    private void lockAll(){
        try {
            ((Button)findViewById(R.id.tblPrint)).setText("Reprint");
            view.removeAllElements();
            getAllView(view, getWindow().getDecorView());
            for (int i = 0; i < view.size(); i++) {
                if (view.elementAt(i) instanceof EditText) {
                    ((EditText)view.elementAt(i)).setEnabled(false);
                }else if (view.elementAt(i) instanceof Button) {
                    //((Button)view.elementAt(i)).setEnabled(false);
                }
            }
        }catch (Exception e){}
    }

    private Vector<View> view = new Vector<View>();
    private void getAllView(Vector view, View child){
        //if (child.getContentDescription()!=null||child.getTag()!=null) {
        view.addElement(child);
        //}
        if (child instanceof ViewGroup ) {
            for (int j = 0; j < ((ViewGroup)child).getChildCount(); j++) {
                getAllView(view, ((ViewGroup)child).getChildAt(j));
            }
        }
    }

    public static boolean isGPSEnable(Context mContext) {
        boolean isGPSEnabled = false;
        LocationManager locationManager = (LocationManager) mContext
                .getSystemService(mContext.LOCATION_SERVICE);
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }
    private void printITwithGPS(int i){
        if (isGPSEnable(ListViewDetailsFirstDesignActivity.this)){
            printIT(i);
        }else {
            Toast.makeText(ListViewDetailsFirstDesignActivity.this, "Maaf, GPS anda belum aktif", Toast.LENGTH_SHORT).show();
        }

    }
    private void printIT(int i){
        final StringBuffer sbuff = new StringBuffer("");
        String stbayar = String.valueOf(order.getStatusBayar());
        if (stbayar.equalsIgnoreCase("TB")||stbayar.equalsIgnoreCase("TARIK BARANG")) {
            sbuff.setLength(0);//clear
            if (i == 9) {
                sbuff.append(ListViewDetails3FirstDesignActivity.getPrintCheckList(this, order));
            } else {
                sbuff.append(ListViewDetails3FirstDesignActivity.getPrintTB(this, order));
            }
        }else if (stbayar.equalsIgnoreCase("JB")||stbayar.equalsIgnoreCase("JANJI BAYAR")){
            sbuff.setLength(0);//clear
            sbuff.append(ListViewDetails3FirstDesignActivity.getPrintJB(this, order));
        }else {
            //  sbuff.append("  Koperasi Anugrah Mega Mandiri \n");
            sbuff.append("").append("\n");
            //  sbuff.append("Telp :            ").append("\n");

            String tanggal ="";

            if (order.getTglBayar() != null ){
                if (!order.getTglBayar().equalsIgnoreCase("")){
                    //sbuff.append("Tgl").append(order.getTglBayar().substring(0,11) ).append(" - Jam Bayar ").append(order.getTglBayar().substring(11,16) ).append("\n");
                    String date = order.getTglBayar().trim();
                    if (AppUtil.isAlpa(date)){
                        date = AppUtil.ConvetToNow(date);
                    }
                    sbuff.append("Tgl ").append( date.substring(0,10) ).append(" Jam Bayar ").append( date.substring(11,16) ).append("\n");

                }else{
                    sbuff.append("Tgl").append(Now().substring(0,11) ).append(" - Jam Bayar ").append(Now().substring(11,16) ).append("\n");
                }
            }

            sbuff.append("Nama Collector   :").append(getdata("nama_ao")).append("\n");


            sbuff.append("REPRINT").append("\n");
            sbuff.append("").append("\n");
            sbuff.append(getCode(getdata("nokwitansi"),  order.getNoPsb(), getdata("kodepos"),  AppUtil.getSetting(ListViewDetailsFirstDesignActivity.this, "USERNAME", "") ,order.getJmlTotTerbayar() )).append("\n");
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
                }catch (Exception e){
                    //old
                    if(order.getSisaAngsuran() !=null){
                        if (order.getSisaAngsuran().equalsIgnoreCase("0") || order.getSisaAngsuran().equalsIgnoreCase("") ){
                            sbuff.append("Ang. Tersisa  :").append(rightM("0",16)).append("\n");
                        }else{
                            int angsuran_yang_tersisa = Utility.getInt(order.getSisaAngsuran()) - Utility.getInt(order.getJumlahangsuran());
                            if (angsuran_yang_tersisa >=0) {
                                sbuff.append("Ang. Tersisa  :").append(rightM(String.valueOf(angsuran_yang_tersisa), 16)).append("\n");
                            }else{
                                sbuff.append("Ang. Tersisa  :").append(rightM("0",16)).append("\n");
                            }
                        }
                    }else {
                        sbuff.append("Ang. Tersisa  :").append(rightM("0",16)).append("\n");
                    }
                }

            }

            sbuff.append("").append("\n");
            sbuff.append("Nomor Virtual Account\n");
            Nson n = ListViewDetails3FirstDesignActivity.getVA(ListViewDetailsFirstDesignActivity.this,getdata("no_psb"));
            sbuff.append("Alfa/Indo : ").append(n.get("VA_ALFA").asString()).append("\n");
            sbuff.append("BCA       : ").append(n.get("VA_BCA").asString()).append("\n");
            sbuff.append("BRI       : ").append(n.get("VA_BRI").asString()).append("\n");
            sbuff.append("BNI       : ").append(n.get("VA_BNI").asString()).append("\n");
            sbuff.append("").append("\n");
            sbuff.append("").append("\n");
            // sbuff.append("Note: No telp anda ").append(getdata("telp")).append("\n");
            sbuff.append("SMS WA CENTER KAMM, ").append("\n");
            sbuff.append("08999731188. Harap hubungi kami").append("\n");
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
        Messagebox.showProsesBar(ListViewDetailsFirstDesignActivity.this, new Messagebox.DoubleRunnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                }catch (Exception e){}
                if (printerName.equals("sprt")) {
                    printSprt("sprt", LogoAsset(ListViewDetailsFirstDesignActivity.this, imageName), sbuff.toString());
                }else{

                    printBT(SelectedBDAddress,  GetLogo(ListViewDetailsFirstDesignActivity.this, sbuff.toString()));
                }
            }

            @Override
            public void runUI() {
                if( closBT()) {
                    setResult(AppController.CLOSE_CODE);
                    finish();
                }
            }
        });



    }
    public String rightM(String s){
        return rightM(s, 18);
    }
    public String rightM(String s, int max){
        s="                     "+s;
        return s.substring(s.length()-max,s.length());
    }
    private String getdata(String name){
        String s = buf.get(name);
        return (s!=null?s:"");
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


            rePrint="R";


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
                LayoutInflater inflater = LayoutInflater.from(ListViewDetailsFirstDesignActivity.this);
                View view = inflater.inflate(R.layout.messagebox, null);

                TextView textview=(TextView)view.findViewById(R.id.textmsg);

                textview.setText(message);
                AlertDialog.Builder builder = new AlertDialog.Builder(ListViewDetailsFirstDesignActivity.this);
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
    public BluetoothSocket mSocket = null;
    public OutputStream mSocketOut = null;

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
        } catch (Exception e) { return true; }
        try {
            if (mSocketOut!=null) {
                //mSocketOut.close();
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
        } catch (Exception e) { }
    }

    private BluetoothPrinter mPrinter = null ;
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
        } catch (Exception e) { }
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
        } catch (Exception e) {  }
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


    private String leftsubstring(String s, int max){
        if (s.length()>max) {
            return s.substring(0, max);
        }
        return s;
    }
    private String rightCcurrency(String s, int max){
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
        super.onDestroy();

        closBT();
        if (mPrinter!=null) {
            try {
                mPrinter.close();
            } catch (Exception e) { }
        }
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
    private void filldata(String rst, String name){
        try {
            buf.put(name, rst);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}
