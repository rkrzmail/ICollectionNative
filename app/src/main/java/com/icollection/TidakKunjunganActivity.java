package com.icollection;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.florent37.rxgps.RxGps;
import com.icollection.fonts.MerriweatherTextView;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.icollection.AppController.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.icollection.util.AppUtil.NowX;

public class TidakKunjunganActivity extends AppActivity {


    private static final String LOG_TAG = TidakKunjunganActivity.class.getSimpleName();
    Toolbar toolbar;
    private Vector<TidakKunjunganActivity.ModelData> data = new Vector<TidakKunjunganActivity.ModelData>();


    Bitmap bitmap;
    Button btnLanjut,btnUpdate,btnCapture;
    ImageView ImageViewHolder;

    private EditText txtAlasan;

    public Uri fileUri;
    private int CAMERA_REQUEST = 100;
    private int Gallary_REQUEST = 101;

    DatePicker datePicker;


    OrderItem order;
    private View parentView;
    private FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    boolean isFABHided = false;
    AppController appController;

    String jmlAng="0";String jmlTotal="0";
    String txtAng="";
    String tipeView="";

    private Calendar get(OrderItem orderItem){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(order.getData1());
            if (date.getTime()<new Date().getTime()){
                //date = new Date();
            }
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
    public static void navigateTidakKunjungan(AppCompatActivity activity, OrderItem obj, String tipeView) {
        Intent intent = new Intent(activity, TidakKunjunganActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("tipeView", tipeView);
        intent.putExtra("newstatus", "TIDAK KUNJUNGAN");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppController.REQUEST_DETAIL) {
            if (resultCode == AppController.CLOSE_CODE) {
                setResult(AppController.CLOSE_CODE);
                finish();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            /*bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            ImageViewHolder.setImageBitmap(bitmap);*/

            getCamera("CAMERA_REQUEST_TK",data,ImageViewHolder);

        } else if (requestCode == Gallary_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            bitmap = BitmapFactory.decodeFile(picturePath);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(picturePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateImage(bitmap, 180);
                    break;
                // etc.
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);

            ImageViewHolder.setImageBitmap(bitmap);

        }

    }
    public void getCamera(String name, Intent data, ImageView imageView){
        File photo = com.naa.data.Utility.getFileCamera();

        String pfile = com.naa.data.Utility.getCacheDir(name+"_BYTB");
        com.naa.data.Utility.copyFile( photo.getAbsolutePath(), pfile );
        onCompressImage( pfile , 80,1366,1366 );//540


        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);

    }
    public static void viewImage(ImageView img, String absolutePath, int wmax){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(absolutePath, options);
        int scale=options.outWidth/wmax;


        options = new BitmapFactory.Options();
        options.inSampleSize=scale;
        Bitmap bmp = BitmapFactory.decodeFile(absolutePath,  options);

        img.setImageBitmap(bmp);
    }

    public static void onCompressImage(String file, int quality, int width, int maxpx){
        String format = "jpg" ;
        width=width<=10?540:width;
        maxpx=maxpx<=10?540:maxpx;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds=true;
            BitmapFactory.decodeFile(file, options);
            int scale= options.outWidth / width ;
            if ( maxpx > 1) {
                scale=Math.max(options.outWidth, options.outHeight) / maxpx;
            }

            options = new BitmapFactory.Options();
            options.inSampleSize=scale+1;
            Bitmap bmp = BitmapFactory.decodeFile(file,  options);

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(format.equalsIgnoreCase("jpg")?Bitmap.CompressFormat.JPEG:Bitmap.CompressFormat.PNG, quality, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) { }
    }
    String[] alasan =new String[]{"", "BY PHONE", "BELUM SEMPAT"};
    String[] info = new String[]{"BAYAR ALFAMART/INDOMARET", "BAYAR ATM/VIRTUAL", "JANAJI BAYAR"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tidakkunjungan);
        parentView = findViewById(android.R.id.content);
        appController = (AppController) getApplication();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        AppUtil.verifyStoragePermissions(TidakKunjunganActivity.this);
        ViewCompat.setTransitionName(findViewById(R.id.image), "order");
        if (getIntent().getParcelableExtra("order") instanceof OrderItem){
            order = getIntent().getParcelableExtra("order");
        }else{
            finish();
            return;
        }


        tipeView = getIntent().getStringExtra("tipeView");

        txtAlasan = ((EditText)findViewById(R.id.txtAlasan));
        datePicker = ((DatePicker)findViewById(R.id.datePicker1));

        ((ListView)findViewById(R.id.listView1)).setFocusable(true);
        txtAlasan.setFocusable(true);
        txtAlasan.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        datePicker.setCalendarViewShown(false);







        if (order.getAlasan1()!=null){
            String x = String.valueOf(order.getAlasan1());
            alasan = Utility.split(x,"|");
        }

        if (order.getInformasi()!=null){
            String y = String.valueOf(order.getInformasi());
            info = Utility.split(y,"|");
        }

        Spinner spAlasan = findViewById(R.id.spAlasan);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, alasan);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAlasan.setAdapter(aa);


        Spinner spinInfo = findViewById(R.id.spInformasi);
        ArrayAdapter bb = new ArrayAdapter(this, android.R.layout.simple_spinner_item, info);
        bb.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinInfo.setAdapter(bb);


       /* spAlasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String val =String.valueOf(spAlasan.getSelectedItem());
                if (val.trim().equalsIgnoreCase("BLM SEMPAT")){
                    ((EditText)findViewById(R.id.txtBertemuDg)).setText("0");
                    ((EditText)findViewById(R.id.txtBertemuNo)).setText("0");

                    ((EditText)findViewById(R.id.txtBertemuDg)).setEnabled(false);
                    ((EditText)findViewById(R.id.txtBertemuNo)).setEnabled(false);

                    datePicker.setMinDate(new Date().getTime());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 1);

                    //datePicker.setMinDate(calendar.getTimeInMillis()-1000);
                    datePicker.setMaxDate(calendar.getTimeInMillis());
                   // datePicker.setMinDate(calendar.getTimeInMillis());
                   // datePicker.setMaxDate(calendar.getTimeInMillis()- 1000);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);

                    datePicker.updateDate(year, month, day);
                    datePicker.setEnabled(false);

                    Spinner spinInfo = findViewById(R.id.spInformasi);
                    spinInfo.setSelection(0);
                    spinInfo.setEnabled(false);
                }else{
                    datePicker.setEnabled(true);
                    ((EditText)findViewById(R.id.txtBertemuDg)).setEnabled(true);
                    ((EditText)findViewById(R.id.txtBertemuNo)).setEnabled(true);

                    ((EditText)findViewById(R.id.txtBertemuDg)).setText("");
                    ((EditText)findViewById(R.id.txtBertemuNo)).setText("");

                    //datePicker1.setMinDate(new Date().getTime());
                    //datePicker1.setMaxDate(new Date().getTime());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 4);
                    datePicker.setMinDate(new Date().getTime());
                    datePicker.setMaxDate(calendar.getTimeInMillis());
                    //datePicker.setMinDate(calendar.getTimeInMillis());
                    //datePicker.setMaxDate(calendar.getTimeInMillis()- 1000);

                    Spinner spinInfo = findViewById(R.id.spInformasi);
                    spinInfo.setEnabled(true);

                }
                if (isLocked()){
                    findViewById(R.id.spInformasi).setEnabled(false);
                    datePicker.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/


        if (!(order.getJanjiBayar()==null) && !order.getJanjiBayar().equalsIgnoreCase("") ) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date date = null;
            try {
                date = sdf.parse(order.getJanjiBayar());
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            datePicker.updateDate(year, month, day);

            long now = System.currentTimeMillis() - 1000;
            /*datePicker.setMinDate(now + (1000 * 60 * 60 * 24 * 0));
            datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 4)); //Af*/

        }else{
            long now = System.currentTimeMillis() - 1000;
            /*datePicker.setMinDate(now + (1000 * 60 * 60 * 24 * 0));
            datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 4)); //Af*/
        }

        ((MerriweatherTextView)findViewById(R.id.title)).setText(getIntentString("newstatus"));
        findViewById(R.id.frmSumber).setVisibility(View.GONE);
        findViewById(R.id.frmAlasanX).setVisibility(View.GONE);
        findViewById(R.id.frmMelalui).setVisibility(View.GONE);
        findViewById(R.id.frmMelaluiSpin).setVisibility(View.GONE);
        if (getIntentString("newstatus").equalsIgnoreCase("TK JB")){
            //janji bayar
            /*long now = System.currentTimeMillis() - 1000;
            datePicker.setMinDate(now + (1000 * 60 * 60 * 24 * 0));
            datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 4)); //Af*/
            Calendar calendar =get(order);// Calendar.getInstance();
            datePicker.setMinDate(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, 4);
            datePicker.setMaxDate(calendar.getTimeInMillis());

            findViewById(R.id.frmSumber).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtjanjinBayar)).setText("Tanggal Janji Bayar");
            findViewById(R.id.frmMelalui).setVisibility(View.VISIBLE);
            findViewById(R.id.frmMelaluiSpin).setVisibility(View.VISIBLE);
        }else if (getIntentString("newstatus").equalsIgnoreCase("TK SUDAH BAYAR")){
            findViewById(R.id.frmSumber).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.txtjanjinBayar)).setText("Tanggal Bayar");
            findViewById(R.id.frmMelalui).setVisibility(View.VISIBLE);
            findViewById(R.id.frmMelaluiSpin).setVisibility(View.VISIBLE);

           /* long now = System.currentTimeMillis() - 1000;
            datePicker.setMinDate(now - (1000 * 60 * 60 * 24 * 90));
            datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 0));*/
            Calendar calendar = get(order);//Calendar.getInstance();
            datePicker.setMaxDate(calendar.getTimeInMillis());
            calendar.add(Calendar.DAY_OF_MONTH, -90);
            datePicker.setMinDate(calendar.getTimeInMillis());
        }else if (getIntentString("newstatus").equalsIgnoreCase("TK SEMPAT")){
            findViewById(R.id.frmAlasanX).setVisibility(View.VISIBLE);
            findViewById(R.id.datePicker1).setVisibility(View.GONE);
            findViewById(R.id.txtjanjinBayar).setVisibility(View.GONE);

        }else{

        }

        final Spinner spinnerMemalui = findViewById(R.id.spnMelalui);
        ArrayAdapter aaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[] {"","ALFA MART","INDOMART"," ATM BCA","ATM BNI","ATM MANDIRI","ATM BRI","BAYAR KANTOR","KOLEKTOR KAMM"});
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMemalui.setAdapter(aaa);
        spinnerMemalui.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (String.valueOf(spinnerMemalui.getSelectedItem()).equalsIgnoreCase("KOLEKTOR KAMM")){
                    findViewById(R.id.frmMelalui).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.frmMelalui).setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Spinner spnSumber = findViewById(R.id.spnSumber);
        aaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[] {"","MELALUI WA/TLP/SMS","MELALUI VERBAL"});
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSumber.setAdapter(aaa);

        Spinner spnAlasanX = findViewById(R.id.spnAlasanX);
        aaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[] {"","SALAH AREA TAGIH","SALAH TANGGAL","SALAH TYPE BAYAR","LAINNYA"});
        aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAlasanX.setAdapter(aaa);


                btnLanjut =(Button)findViewById(R.id.btnLanjut);
        btnCapture =(Button)findViewById(R.id.btnPhotoJanji);
        ImageViewHolder = (ImageView)findViewById(R.id.imgFoto);


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        setGPS();
        setAllData();


        if ( isLocked() ){
            lockAll();
        }else{
            findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(verifikasi()) {
                        showMessage("Apa data benar ?", "YA", "TIDAK", null, true, 0);
                    }


                }
            });
        }




    }

    public String getIntentString(String key){
        if (getIntent()!=null && getIntent().getStringExtra(key)!=null){
            return getIntent().getStringExtra(key);
        }else{
            return "";
        }
    }
    private void setAllData() {
        if (order != null) {

         setupBrahmaMode();



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
        } else if (itemId == android.R.id.home ) {

            onBackPressed();
        }
        return true;
    }

    private String sStatus = "";
    private String que="";
    public void setupBrahmaMode(){

        sStatus=order.getStatusBayar();

        if (getIntent().getStringExtra("READ")!=null) {


            ((TextView)findViewById(R.id.txtHasil)).setText(sStatus );
            ((TextView)findViewById(R.id.txtName)).setText( order.getNama());
            ((EditText)findViewById(R.id.txtAlasan)).setText( order.getAlasan());
            que= order.getQue();
            ((TextView)findViewById(R.id.txtjanjinBayar)).setText( "Tidak Kunjungan "+order.getJanjiBayar() );
            findViewById(R.id.datePicker1).setVisibility(View.GONE);
            findViewById(R.id.button1).setVisibility(View.GONE);


            /*Vector<String> v= AppUtil.spliter(que, "|");
            for (int i = 0; i < v.size(); i++) {
                int l = v.elementAt(i).indexOf("=");
                if (l>=0) {
                    data.addElement(new TidakKunjunganActivity.ModelData(v.elementAt(i).toUpperCase().substring(0,l), v.elementAt(i).toUpperCase().substring(l+1)));
                }

            }*/

        }else if (getIntent().getStringExtra("REPORT")!=null) {


            ((TextView)findViewById(R.id.txtHasil)).setText(sStatus);
            ((TextView)findViewById(R.id.txtName)).setText( order.getNama());
            ((EditText)findViewById(R.id.txtAlasan)).setText( order.getAlasan() );
            que= order.getQue();
            que=(que==null)?"":que;
            ((TextView)findViewById(R.id.txtjanjinBayar)).setText( "TIDAK KUNJUNGAN "+order.getJanjiBayar() );




            /*if (que.length()<=10) {
                Vector<String> v= AppUtil.spliter(AppUtil.getSetting(this, "QUE", ""), "|");
                for (int i = 0; i < v.size(); i++) {
                    data.addElement(new TidakKunjunganActivity.ModelData(v.elementAt(i).toUpperCase(), "?"));
                }
            }else{
                Vector<String> v= AppUtil.spliter(que, "|");
                for (int i = 0; i < v.size(); i++) {
                    int l = v.elementAt(i).indexOf("=");
                    if (l>=0) {
                        data.addElement(new TidakKunjunganActivity.ModelData(v.elementAt(i).toUpperCase().substring(0,l), v.elementAt(i).toUpperCase().substring(l+1)));
                    }

                }
            }*/





        }else{
            String que="";



            Spinner spAlasan = findViewById(R.id.spAlasan);
            for (int i = 0; i < alasan.length; i++) {
                if (alasan[i].equalsIgnoreCase(order.getIsi_alasan1()) ){
                    spAlasan.setSelection(i);
                    break;
                }
            }

            Spinner spinInfo = findViewById(R.id.spInformasi);
            for (int i = 0; i < info.length; i++) {
                if (info[i].equalsIgnoreCase(order.getIsi_informasi())){
                    spinInfo.setSelection(i);
                    break;
                }
            }

            ((TextView)findViewById(R.id.txtHasil)).setText(sStatus);//rst.getString( rst.getColumnIndex("status_janji_bayar"))
            ((TextView)findViewById(R.id.txtName)).setText(order.getNama() );
            ((EditText)findViewById(R.id.txtAlasan)).setText( order.getAlasan());

            que=  order.getQue();
            que=(que==null)?"":que;
            //((DatePicker)findViewById(R.id.datePicker1)).updateDate(year, month, dayOfMonth)


//			Vector<String> v= Utility.spliter(Utility.getSetting(this, "QUE", ""), "|");
//			for (int i = 0; i < v.size(); i++) {
//				data.addElement(new ModelData(v.elementAt(i).toUpperCase(), "?"));
//			}

            /*if (que.length()<=10) {
                Vector<String> v= AppUtil.spliter(AppUtil.getSetting(this, "QUE", ""), "|");
                for (int i = 0; i < v.size(); i++) {
                    data.addElement(new TidakKunjunganActivity.ModelData(v.elementAt(i).toUpperCase(), "?"));
                }
            }else{
                Vector<String> v= AppUtil.spliter(que, "|");
                for (int i = 0; i < v.size(); i++) {
                    int l = v.elementAt(i).indexOf("=");
                    if (l>=0) {
                        data.addElement(new TidakKunjunganActivity.ModelData(v.elementAt(i).toUpperCase().substring(0,l), v.elementAt(i).toUpperCase().substring(l+1)));
                    }

                }
            }*/
        }
        sStatus = getIntentString("newstatus");

        ((ListView)findViewById(R.id.listView1)).setAdapter(new TidakKunjunganActivity.ItemAdapter(TidakKunjunganActivity.this, R.layout.tidakbayaritem, data));
        ((ListView)findViewById(R.id.listView1)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1,final int arg2, long arg3) {
                Messagebox.showDialogSingleChoiceItems(TidakKunjunganActivity.this, "", new String[]{"YA", "TIDAK"}, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (which == 0) {
                            data.elementAt(arg2).ans = "YA";
                        } else if (which == 1) {
                            data.elementAt(arg2).ans = "TIDAK";
                        }
                        ((ArrayAdapter) ((ListView) findViewById(R.id.listView1)).getAdapter()).notifyDataSetChanged();
                    }
                }, new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        return false;
                    }
                });



            }
        });



        ((TextView)findViewById(R.id.txtDate)).setText( AppUtil.Now().substring(0,10) );


    }

    public boolean verifikasi(){
        if (!ListViewDetailsFirstDesignActivity.isGPSEnable(TidakKunjunganActivity.this)){
            Toast.makeText(getApplicationContext(),"Maaf, GPS anda belum aktif",Toast.LENGTH_LONG).show();
            ((ListView)findViewById(R.id.listView1)).requestFocus();

            return false;
        }
        if(!cekPertanyaan()){
            Toast.makeText(getApplicationContext(),"Semua pertanyaan harus diisi",Toast.LENGTH_LONG).show();
            ((ListView)findViewById(R.id.listView1)).requestFocus();

            return false;
        }



        Spinner spAlasan = findViewById(R.id.spAlasan);
        Spinner spinInfo = findViewById(R.id.spInformasi);

      /*  if (String.valueOf(spAlasan.getSelectedItem()).equalsIgnoreCase("")||String.valueOf(spAlasan.getSelectedItem()).equalsIgnoreCase("null")){
            Toast.makeText(getApplicationContext(),"Alasan harus diisi",Toast.LENGTH_LONG).show();
            spAlasan.requestFocus();
            return false;
        }
        if (!spinInfo.isEnabled()) {
        }else if (String.valueOf(spinInfo.getSelectedItem()).equalsIgnoreCase("")||String.valueOf(spinInfo.getSelectedItem()).equalsIgnoreCase("null")){
            Toast.makeText(getApplicationContext(),"Informasi harus diisi",Toast.LENGTH_LONG).show();
            spinInfo.requestFocus();
            return false;
        }
*/
        /*if (bitmap ==null){
            Toast.makeText(getApplicationContext(),"Foto harus dicapture",Toast.LENGTH_LONG).show();
            return  false;
        }*/

        if (txtAlasan.getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Informasi Lainnya harus diisi",Toast.LENGTH_LONG).show();
            txtAlasan.requestFocus();
            return false;
        }

        /*if (((EditText)findViewById(R.id.txtBertemuDg)).getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Nama harus diisi",Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.txtBertemuDg)).requestFocus();
            return false;
        }

        if (((EditText)findViewById(R.id.txtBertemuNo)).getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"No. Hp harus diisi",Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.txtBertemuNo)).requestFocus();
            return false;
        }
*/

        if (findViewById(R.id.frmMelalui).getVisibility() == (View.VISIBLE) && ((EditText)findViewById(R.id.txtMelalui)).getText().toString().equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Nama Kolektor harus diisi",Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.txtMelalui)).requestFocus();
            return false;
        }

        if (findViewById(R.id.frmSumber).getVisibility() == (View.VISIBLE) && String.valueOf(((Spinner)findViewById(R.id.spnSumber)).getSelectedItem()).equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Sumber harus diisi",Toast.LENGTH_LONG).show();
            ((Spinner)findViewById(R.id.spnSumber)).requestFocus();
            return false;
        }
        if (findViewById(R.id.frmAlasanX).getVisibility() == (View.VISIBLE) && String.valueOf(((Spinner)findViewById(R.id.spnAlasanX)).getSelectedItem()).equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Alasan harus diisi",Toast.LENGTH_LONG).show();
            ((Spinner)findViewById(R.id.spnAlasanX)).requestFocus();
            return false;
        }
        if (findViewById(R.id.frmMelaluiSpin).getVisibility() == (View.VISIBLE) && String.valueOf(((Spinner)findViewById(R.id.spnMelalui)).getSelectedItem()).equalsIgnoreCase("")){
            Toast.makeText(getApplicationContext(),"Melalui harus diisi",Toast.LENGTH_LONG).show();
            ((Spinner)findViewById(R.id.spnMelalui)).requestFocus();
            return false;
        }
        Calendar calendar =  get(order);
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        if(now.getTimeInMillis()+1<calendar.getTimeInMillis()){
            Toast.makeText(getApplicationContext(),"Tanggal bayar/HP lebih kecil dari tanggal server/kirim, Sesuaikan tanggal anda",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void preSave(){
        StringBuffer sbBuffer = new StringBuffer("");
        /*for (int i = 0; i < data.size(); i++) {
            sbBuffer.append(data.elementAt(i).nama).append("=").append(data.elementAt(i).ans).append("|");
        }*/
        String s = sbBuffer.toString();
        if (s.endsWith("|")) {
            s=s.substring(0, s.length()-1);
        }
        String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int mYear =((DatePicker)findViewById(R.id.datePicker1)).getYear();
        int mMonth = ((DatePicker)findViewById(R.id.datePicker1)).getMonth();
        int mDay = ((DatePicker)findViewById(R.id.datePicker1)).getDayOfMonth();

        String sdate = mYear  + "-" + arrMonth[mMonth] + "-" + LPad(mDay + "", "0", 2) ;

        Log.i("DATE", sdate);
        updateQue("",s, sdate ,((EditText)findViewById(R.id.txtAlasan)).getText().toString(), getIntent().getStringExtra("PSB"));

    }
    public class ModelData{
        public String id;
        public String ans;
        public String nama;

        public ModelData(String nama, String ans){
            this.nama=nama;
            this.ans=ans;
        }
    }

    private class ItemAdapter extends ArrayAdapter<Vector> {
        private Vector<TidakKunjunganActivity.ModelData> data;
        private int layout;
        private Context contex;

        public ItemAdapter(Context context, int layoutinflater, Vector dat) {
            super(context, layoutinflater, dat);
            data = dat;
            layout = layoutinflater;
            contex = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) contex .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(layout, parent, false);


            ((TextView)v.findViewById(R.id.textView1)).setText(   data.elementAt(position).nama);
            ((TextView)v.findViewById(R.id.textView2)).setText(  data.elementAt(position).ans);


            return v;
        }

    }

    public void updateQue(String up_data,String que, String janji_bayar, String alasan, String psb){
       /* try {
            Connection.openConnection(TidakBayar.this);
            ContentValues contentValues = new ContentValues();
            contentValues.put("up_data",  up_data);

            contentValues.put("alasan",  alasan);
            contentValues.put("janji_bayar",  janji_bayar);
            contentValues.put("status_janji_bayar",  sStatus);
            contentValues.put("que",  que);
            if (getIntent().getStringExtra("REPORT")!=null) {
            }else{
                contentValues.put("status",  "1");
            }
            contentValues.put("status1",  "1");
            Connection.Update("orderdata", contentValues, "no_psb=?", new String[] { psb });

        } catch (Exception e) {Log.i("up_data", "up_data : "+e.getMessage()); }
        */
        order.setUpData(up_data);
        if (alasan.startsWith(" ")){
            order.setAlasan(alasan);
        }else{
            order.setAlasan(" "+ alasan);
        }
        order.setJanjiBayar(janji_bayar);
        order.setStatusBayar(sStatus);
        //order.setQue(que);
        order.setStatus("1");

        String sMode = "TIDAK KUNJUNGAN";
        sMode = sStatus;
        order.setAlamat2(":"+((EditText)findViewById(R.id.txtBertemuNo)).getText().toString() +":"+((EditText)findViewById(R.id.txtBertemuDg)).getText().toString() +":"+sMode);
       //melalui:kollid:sumber:alasn

        String xa = ":"+
        String.valueOf(((Spinner)findViewById(R.id.spnMelalui)).getSelectedItem()) + ":"+
        ((EditText)findViewById(R.id.txtMelalui)).getText() + ":"+
        String.valueOf(((Spinner)findViewById(R.id.spnSumber)).getSelectedItem()) + ":"+
        String.valueOf(((Spinner)findViewById(R.id.spnAlasanX)).getSelectedItem());
        order.setAlamat2(xa);

        Spinner spAlasan = findViewById(R.id.spAlasan);
        Spinner spinInfo = findViewById(R.id.spInformasi);

        //order.setInformasi("");
        order.setIsi_informasi(String.valueOf(spinInfo.getSelectedItem()));
        //order.setAlasan1("");
        order.setIsi_alasan1(String.valueOf(spAlasan.getSelectedItem()));

        save();
    }
    private static String LPad(String schar, String spad, int len) {
        String sret = schar;
        for (int i = sret.length(); i < len; i++) {
            sret = spad + sret;
        }
        return new String(sret);
    }
    void notifPend(){
        com.icollection.util.Utility.setSetting(TidakKunjunganActivity. this,  "pend","1");
    }
    boolean resultNya;
    ProgressDialog progressDialog;
    public boolean save() {
        resultNya = false;

        String xGPS = order.getPhotoJanjiBayar()+":"+Utility.getSetting(TidakKunjunganActivity.this,"GPS","");
        order.setPhotoJanjiBayar(xGPS);
        order.setStatusBayar(sStatus);
        //add
        order.setText2(Utility.Now());
        order.setText3(Utility.getSetting(getApplicationContext(),"GPS", ""));

       /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();*/
        byte[] byteArray = new byte[0];

        APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
        //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);

        //RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);

// MultipartBody.Part is used to send also the actual file name
        //MultipartBody.Part body = MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);

        //RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),new byte[0]);
        //MultipartBody.Part bukti =  MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);
        order.setQue3("");

        MultipartBody.Part bukti =  JanjiBayarActivity.getBlankImagePart("photo_bukti","photo_bukti");
        MultipartBody.Part body =  JanjiBayarActivity.getBlankImagePart("photo_bayar","photo_bayar");



        progressDialog = new ProgressDialog(TidakKunjunganActivity.this);
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
                    if(response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d("Test", response.message());

                            Toast.makeText(TidakKunjunganActivity.this, response.message(), Toast.LENGTH_LONG).show();

                            order.save();
                            //finish();

                            resultNya = true;
                        }else{
                            notifPend();
                            order.setStatus("pending_janji");//pending
                            order.save();
                        }
                    }else{
                        notifPend();
                        order.setStatus("pending_janji");//pending
                        order.save();
                    }
                }else{
                    notifPend();
                    order.setStatus("pending_janji");//pending
                    order.save();
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(TidakKunjunganActivity.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(TidakKunjunganActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
                LoginActivity.autoLogout(TidakKunjunganActivity.this, response);
                /*setResult(Activity.RESULT_OK);
                finish();*/
                updateBHC();
                //   Toast.makeText(getApplicationContext(), "Nothing happen", Toast.LENGTH_LONG).show();
                // resultNya = false;
            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {
                notifPend();
                Toast.makeText(TidakKunjunganActivity.this, "Koneksi bermasalah", Toast.LENGTH_SHORT).show();
                resultNya = false;
                progressDialog.dismiss();
                order.setStatus("pending_janji");
               /* File xFile = null;
                try {
                    xFile =   AppUtil.savebitmap(bitmap,order.getNoPsb().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                order.setPhotoBayar(xFile.getAbsolutePath());*/
                order.setPhotoBayar("");

                order.save();
                /*setResult(Activity.RESULT_OK);
                finish();*/
                updateBHC();
            }
        });

        return resultNya;
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


        alertDialog.show();
    }
    public RequestBody toRequestBody(String value) {
        if (value==null){
            value="";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
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
    private  boolean isLocked(){
        if (tipeView.equalsIgnoreCase("1")){
            return true;
        }else{
            return false;
        }
    }
    private void lockAll(){
        ((Button)findViewById(R.id.button1)).setEnabled(false);
        btnCapture.setEnabled(false);
        view.removeAllElements();
        getAllView(view, getWindow().getDecorView());
        for (int i = 0; i < view.size(); i++) {
            if (view.elementAt(i) instanceof EditText) {
                ((EditText)view.elementAt(i)).setEnabled(false);
            }else if (view.elementAt(i) instanceof Button) {
                //((Button)view.elementAt(i)).setEnabled(false);
            }
        }



        Spinner spAlasan = findViewById(R.id.spAlasan);
        spAlasan.setEnabled(false);
        Spinner spinInfo = findViewById(R.id.spInformasi);
        spinInfo.setEnabled(false);

        datePicker.setEnabled(false);
    }

    private void Camerapermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(TidakKunjunganActivity.this
                ,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TidakKunjunganActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(TidakKunjunganActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }
    void openCamera() {
        //File directory = new File(    Environment.getExternalStorageDirectory(), "iCollector" + "/" + getPackageName());
        try {


            /*if ( ActivityCompat.checkSelfPermission(JanjiBayarActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
               // ActivityCompat.requestPermissions(JanjiBayarActivity.this, new String[]{Manifest.permission.CAMERA}, 111);
                //return;
            } else {
                *//*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*//*
            }*/

           /* SimpleDateFormat sdfPic = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateandTime = sdfPic.format(new Date()).replace(" ", "");
            File imagesFolder = new File(directory.getAbsolutePath());
            imagesFolder.mkdirs();

            String fname = "IMAGE_" + currentDateandTime + ".jpg";
            File file = new File(imagesFolder, fname);
            fileUri = Uri.fromFile(file);

            //File tempFile = File.createTempFile("photo", ".jpg", JanjiBayarActivity.this.getCacheDir());
            //tempFile.setWritable(true, false);

            Intent cameraIntent = new Intent(  android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
          cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);*/



            File photo = com.naa.data.Utility.getFileCamera();
            photo.delete();
            photo.createNewFile();


            Intent cameraIntent = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            Uri photoURI = FileProvider.getUriForFile(this, "com.icollection.fileprovider", photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /*void openCameraA() {
        File directory = new File(
                Environment.getExternalStorageDirectory(), "iCollector" + "/" + getPackageName());
        try {
            SimpleDateFormat sdfPic = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateandTime = sdfPic.format(new Date()).replace(" ", "");
            File imagesFolder = new File(directory.getAbsolutePath());
            imagesFolder.mkdirs();

            File tempFile = File.createTempFile("photo", ".jpg", TidakKunjunganActivity.this.getCacheDir());
            tempFile.setWritable(true, false);

            String fname = "IMAGE_" + currentDateandTime + ".jpg";
            File file = new File(imagesFolder, fname);
            fileUri = Uri.fromFile(file);
            Intent cameraIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
            Uri photoURI = FileProvider.getUriForFile(this, "com.icollection.fileprovider", photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/
    public void setGPS(){
        final RxGps rxGps = new RxGps(this);
        String xGps = Utility.getSetting(TidakKunjunganActivity.this, "GPS","");
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
                    Utility.setSetting(TidakKunjunganActivity.this, "GPS",location.getLatitude()+","+location.getLongitude());
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




    public void showMessage(String message,final String PositiveButton,final String NegativeButton, final String CenterButton, boolean cancel,final int request) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("");
        if (NegativeButton!=null) {
            if (!NegativeButton.equals("")) {
                dlg.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        }
        if (PositiveButton!=null) {
            if (!PositiveButton.equals("")) {
                dlg.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (request==0) {

                            preSave();
                        }else if (request==1) {
                            //syn
                        }

                    }
                });
            }
        }
        if (CenterButton!=null) {
            if (!CenterButton.equals("")) {
                dlg.setNeutralButton(CenterButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        }
        dlg.setCancelable(cancel);
        dlg.setMessage(message);
        AlertDialog alertDialog=dlg.create();
        alertDialog.show();
    }


    public boolean cekPertanyaan(){

        for(int x = 0 ; x < data.size();x++){
            if(data.get(x).ans.trim().equalsIgnoreCase("")){
                return true;
            }
        }

        return true;
    }


}
