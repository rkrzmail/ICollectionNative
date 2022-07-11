package com.icollection;

import android.Manifest;
import android.app.Activity;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.icollection.modelservice.OrderItem;
import com.icollection.util.AppUtil;
import com.icollection.util.Messagebox;
import com.icollection.util.Utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import static com.icollection.AppController.MY_PERMISSIONS_REQUEST_CAMERA;


/**
 * Created by Mounzer on 8/22/2017.
 */

public class ListViewDetails2FirstDesignActivity extends AppCompatActivity {
    private static final String LOG_TAG = ListViewDetails2FirstDesignActivity.class.getSimpleName();
    Toolbar toolbar;


    Button btnLanjut, btnCapturePhoto;
    ImageView ImageViewHolder;

    Bitmap bitmap;
    Bitmap bitmap2;
    Bitmap bitmap3;
    Bitmap bitmap4;
    String sbitmap;
    private int CAMERA_REQUEST_LAIN = 101;
    private int CAMERA_REQUEST_DEPAN1 = 201;
    private int CAMERA_REQUEST_KANAN2 = 202;
    private int CAMERA_REQUEST_KIRI3 = 203;
    private int CAMERA_REQUEST_BELANAG4 = 204;

    public Uri fileUri;
    private int CAMERA_REQUEST = 100;
    private int Gallary_REQUEST = 101;


    OrderItem order;
    private View parentView;
    private FloatingActionButton fab;
    NestedScrollView nestedScrollView;
    boolean isFABHided = false;
    AppController appController;

    String jmlAng = "0";
    String jmlTotal = "0";
    String txtAng = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview_details_2_first_design);
        parentView = findViewById(android.R.id.content);
        appController = (AppController) getApplication();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ViewCompat.setTransitionName(findViewById(R.id.image), "order");
        order = getIntent().getParcelableExtra("order");

        findViewById(R.id.lnrSTNK).setVisibility(View.GONE);

        TextView title = findViewById(R.id.txtTitle);
        if (getIntentStringExtra("status").equalsIgnoreCase("TB")) {
            findViewById(R.id.lnrBayar).setVisibility(View.GONE);
            findViewById(R.id.lnrTB).setVisibility(View.VISIBLE);
            setTitle("TARIK BARANG");
            title.setText("TARIK BARANG");


            final Spinner spnKondisi = findViewById(R.id.spnKondisi);
            ArrayAdapter aaa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[]{"", "HIDUP BAGUS", "HIDUP KURANG", " MATI"});
            aaa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnKondisi.setAdapter(aaa);
            spnKondisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            final Spinner spnSTNK = findViewById(R.id.spnSTNK);
            ArrayAdapter stnk = new ArrayAdapter(this, android.R.layout.simple_spinner_item, new String[]{"", "ADA", "TIDAK ADA"});
            stnk.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSTNK.setAdapter(stnk);
            spnSTNK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (String.valueOf(spnSTNK.getSelectedItem()).equalsIgnoreCase("ADA")) {
                        findViewById(R.id.lnrSTNK).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.lnrSTNK).setVisibility(View.GONE);
                    }
                }

                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (order != null) {
                List<String> list = Utility.splitList(order.getData2() + "||||||", "|");
                find(R.id.txtTBnoPSB, TextView.class).setText(order.getNoPsb());
                find(R.id.txtTBNamaKonsumen, TextView.class).setText(order.getNama());
                find(R.id.txtTBNNoPol, TextView.class).setText(list.get(4));
                find(R.id.txtTBMerk, TextView.class).setText(list.get(0));//data2 merk0|warna1|type2|tahun3|nopol4|stnk_an5
                find(R.id.txtTBType, TextView.class).setText(list.get(2));
                find(R.id.txtTBTahun, TextView.class).setText(list.get(3));

                String kondisi = String.valueOf(order.getQue3()).trim();
                for (int i = 0; i < aaa.getCount(); i++) {
                    if (String.valueOf(aaa.getItem(i)).trim().equalsIgnoreCase(kondisi)) {
                        spnKondisi.setSelection(i);
                        break;
                    }
                }

            }

            findViewById(R.id.frmTbTB).setVisibility(View.VISIBLE);
        } else if (getIntentStringExtra("status").equalsIgnoreCase("LAIN")) {
            findViewById(R.id.lnrTB).setVisibility(View.GONE);
            findViewById(R.id.lnrBayar).setVisibility(View.VISIBLE);

            findViewById(R.id.lnrBayarInput).setVisibility(View.GONE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.VISIBLE);
            setTitle("BAYAR LAIN");
            title.setText("BAYAR LAIN");

            findViewById(R.id.frmTbLain).setVisibility(View.VISIBLE);//tblByrLanjut LAIN
        } else {
            findViewById(R.id.lnrTB).setVisibility(View.GONE);
            findViewById(R.id.lnrBayar).setVisibility(View.VISIBLE);

            findViewById(R.id.lnrBayarInput).setVisibility(View.VISIBLE);
            findViewById(R.id.lnrBayarLain).setVisibility(View.GONE);
            setTitle("BAYAR ANGSURAN");
            title.setText("BAYAR ANGSURAN");

            findViewById(R.id.frmTbByr).setVisibility(View.VISIBLE);
        }

        if (JanjiBayarActivity.needInputHP(order)) {
            findViewById(R.id.lnrNewHp).setVisibility(View.VISIBLE);
        }

        datePicker = ((DatePicker) findViewById(R.id.datePicker1));
        datePicker.setCalendarViewShown(false);
        if (!(order.getJanjiBayar() == null) && !order.getJanjiBayar().equalsIgnoreCase("")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = sdf.parse(order.getJanjiBayar());
            } catch (ParseException e) {
                date = new Date();
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            datePicker.updateDate(year, month, day);


        } else {


        }
        Calendar calendar = Calendar.getInstance();//get(order);// Calendar.getInstance();//
        datePicker.setMinDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, 0);//today
        datePicker.setMaxDate(calendar.getTimeInMillis());


        data.clear();
        if (order != null && order.getQue() != null) {
            que = order.getQue();
            if (que.length() <= 10) {
                Vector<String> v = AppUtil.spliter(AppUtil.getSetting(this, "QUE", ""), "|");
                for (int i = 0; i < v.size(); i++) {
                    data.addElement(new ModelData(v.elementAt(i).toUpperCase(), "?"));
                }
            } else {
                Vector<String> v = AppUtil.spliter(que, "|");
                for (int i = 0; i < v.size(); i++) {
                    int l = v.elementAt(i).indexOf("=");
                    if (l >= 0) {
                        data.addElement(new ModelData(v.elementAt(i).toUpperCase().substring(0, l), v.elementAt(i).toUpperCase().substring(l + 1)));
                    }
                }
            }
        }
        ((ListView) findViewById(R.id.listView1)).getAdapter();
        ((ListView) findViewById(R.id.listView1)).setAdapter(new ItemAdapter(ListViewDetails2FirstDesignActivity.this, R.layout.tidakbayaritem, data));
        ((ListView) findViewById(R.id.listView1)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                Messagebox.showDialogSingleChoiceItems(ListViewDetails2FirstDesignActivity.this, "", new String[]{"YA", "TIDAK"}, -1, new DialogInterface.OnClickListener() {
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

        btnLanjut = (Button) findViewById(R.id.btnLanjut);
        btnCapturePhoto = (Button) findViewById(R.id.btnPhotoBayar);
        ImageViewHolder = (ImageView) findViewById(R.id.imgFotoBayar);

        setAllData();


        ((EditText) findViewById(R.id.editText1)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                countNow(s.toString(), ((EditText) findViewById(R.id.editText2)).getText().toString(), ((EditText) findViewById(R.id.angsuranke)).getText().toString());
            }
        });
        ((EditText) findViewById(R.id.editText2)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                countNow(s.toString(), ((EditText) findViewById(R.id.editText1)).getText().toString(), ((EditText) findViewById(R.id.angsuranke)).getText().toString());
            }
        });

//tambahan
        ((EditText) findViewById(R.id.angsuranke)).addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                countNow(((EditText) findViewById(R.id.editText1)).getText().toString(), ((EditText) findViewById(R.id.editText2)).getText().toString(), s.toString());

            }
        });


        findViewById(R.id.tblLanjutTB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //ListViewDetails3FirstDesignActivity.navigateTB(ListViewDetails2FirstDesignActivity.this,order,bitmap, bitmap2,bitmap3,bitmap4);


                Calendar calendar = get(order);
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());

                String[] arrMonth = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
                int mYear = ((DatePicker) findViewById(R.id.datePicker1)).getYear();
                int mMonth = ((DatePicker) findViewById(R.id.datePicker1)).getMonth();
                int mDay = ((DatePicker) findViewById(R.id.datePicker1)).getDayOfMonth();

                String sdate = mYear + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2);
                Log.i("DATE", sdate);
                order.setTglBayar(sdate);
                order.setQue(((EditText) findViewById(R.id.txtTBKeterangan)).getText().toString());//keterangan
                order.setQue1(CheckList.setQue1(order, ((EditText) findViewById(R.id.txtTBTerimaNama)).getText().toString()));//terimadai
                order.setQue3(String.valueOf(find(R.id.spnKondisi, Spinner.class).getSelectedItem()));

                order.setJamBayar(Utility.Now());

                Spinner spnSTNK = findViewById(R.id.spnSTNK);
                mYear = ((DatePicker) findViewById(R.id.datePlatNopol)).getYear();
                mMonth = ((DatePicker) findViewById(R.id.datePlatNopol)).getMonth();
                mDay = ((DatePicker) findViewById(R.id.datePlatNopol)).getDayOfMonth();

                String sdatePlat = mYear + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2);
                mYear = ((DatePicker) findViewById(R.id.datePajak)).getYear();
                mMonth = ((DatePicker) findViewById(R.id.datePajak)).getMonth();
                mDay = ((DatePicker) findViewById(R.id.datePajak)).getDayOfMonth();
                String sdatePajak = mYear + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2);

                order.setQue2(String.valueOf(spnSTNK.getSelectedItem()) + "|" + sdatePajak + "|" + sdatePlat);

                if (checkImageNoExist((ImageView) findViewById(R.id.imgFotoDepan))) {
                    Toast.makeText(getApplicationContext(), "Foto harus Depan harus diisi", Toast.LENGTH_LONG).show();
                } else if (checkImageNoExist((ImageView) findViewById(R.id.imgFotoKanan))) {
                    Toast.makeText(getApplicationContext(), "Foto harus Kanan harus diisi", Toast.LENGTH_LONG).show();
                } else if (checkImageNoExist((ImageView) findViewById(R.id.imgFotoKiri))) {
                    Toast.makeText(getApplicationContext(), "Foto harus Kiri harus diisi", Toast.LENGTH_LONG).show();
                } else if (checkImageNoExist((ImageView) findViewById(R.id.imgFotoBelakang))) {
                    Toast.makeText(getApplicationContext(), "Foto harus Belakang harus diisi", Toast.LENGTH_LONG).show();
                } else if (((EditText) findViewById(R.id.txtTBTerimaNama)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Telah Terima harus diisi", Toast.LENGTH_LONG).show();
                } else if (String.valueOf(find(R.id.spnKondisi, Spinner.class).getSelectedItem()).equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Kondisi Motor harus diisi", Toast.LENGTH_LONG).show();
                } else if (((EditText) findViewById(R.id.txtTBKeterangan)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Keterangan harus diisi", Toast.LENGTH_LONG).show();
                } else if (String.valueOf(spnSTNK.getSelectedItem()).equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "STNK harus dipilih", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "No Hp Harus diisi", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().length() < 10) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Minimal 10 Digit", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && !((EditText) findViewById(R.id.txtNewHp)).getText().toString().startsWith("08")) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Harus start 08", Toast.LENGTH_LONG).show();
                } else {
                    showMessageTB("Apa data benar ?", "YA", "TIDAK", null, true, 0);
                }


            }
        });


        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (((EditText) findViewById(R.id.editText1)).getText().toString().equalsIgnoreCase("")) {
                    //((EditText)findViewById(R.id.editText1)).setText("0");
                }
                if (((EditText) findViewById(R.id.editText2)).getText().toString().equalsIgnoreCase("")) {
                    //((EditText)findViewById(R.id.editText2)).setText("0");
                }
                countNowA(((EditText) findViewById(R.id.editText1)).getText().toString(), ((EditText) findViewById(R.id.editText2)).getText().toString(), ((EditText) findViewById(R.id.angsuranke)).getText().toString());


                int keawal = AppUtil.getIntCut(order.getKeAwal());
                int keakhir = AppUtil.getIntCut(((EditText) findViewById(R.id.angsuranke)).getText().toString());

                int tenor = AppUtil.getIntCut(order.getTenor());


                Calendar calendar = get(order);
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());

                if (checkImageNoExist((ImageView) findViewById(R.id.imgFotoBayar))) {//bitmap ==null
                    Toast.makeText(getApplicationContext(), "Foto harus dicapture", Toast.LENGTH_LONG).show();
                } else if (keawal > keakhir) {
                    Toast.makeText(getApplicationContext(), "Angsuran akhir tidak boleh lebih kecil daripada angsuran awal", Toast.LENGTH_LONG).show();
                } else if (keakhir > tenor) {
                    Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Maaf nilai pembayaran angsuran ke melebihi tenor", Toast.LENGTH_LONG).show();
                } else if (Utility.getInt(((EditText) findViewById(R.id.editText1)).getText().toString()) < 0) {
                    Toast.makeText(getApplicationContext(), "Denda tidak boleh minus", Toast.LENGTH_LONG).show();
                } else if (Utility.getInt(((EditText) findViewById(R.id.editText2)).getText().toString()) < 0) {
                    Toast.makeText(getApplicationContext(), "Bayar sisa tidak boleh minus", Toast.LENGTH_LONG).show();
                /*}else  if (((EditText)findViewById(R.id.txtBertemuDg)).getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),"Bertemu dengan (Nama) harus diisi",Toast.LENGTH_LONG).show();

                }else  if (((EditText)findViewById(R.id.txtBertemuNo)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Bertemu dengan (No. Hp) harus diisi", Toast.LENGTH_LONG).show();
               */
                } else if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewDetails2FirstDesignActivity.this)) {
                    Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Maaf, GPS anda belum aktif", Toast.LENGTH_LONG).show();
                } else if (!cekPertanyaan()) {
                    Toast.makeText(getApplicationContext(), "Semua pertanyaan harus diisi", Toast.LENGTH_LONG).show();
                    //((ListView)findViewById(R.id.listView1)).requestFocus();
                } else if (now.getTimeInMillis() + 1 < calendar.getTimeInMillis()) {
                    Toast.makeText(getApplicationContext(), "Tanggal bayar/HP lebih kecil dari tanggal server/kirim, Sesuaikan tanggal anda", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "No Hp Harus diisi", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().length() < 10) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Minimal 10 Digit", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && !((EditText) findViewById(R.id.txtNewHp)).getText().toString().startsWith("08")) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Harus start 08", Toast.LENGTH_LONG).show();
                }else {
                    showMessage("Apa data benar ?", "YA", "TIDAK", null, true, 0);
                }


            }
        });

        findViewById(R.id.tblByrLanjut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = get(order);
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());


                long l = Utility.getInt(find(R.id.txtByrDenda, EditText.class).getText().toString()) +
                        Utility.getInt(find(R.id.txtByrKunjungan, EditText.class).getText().toString()) +
                        Utility.getInt(find(R.id.txtByrTitipanAng, EditText.class).getText().toString());

                if (checkImageNoExist((ImageView) findViewById(R.id.imgByrFoto))) {//bitmap ==null
                    Toast.makeText(getApplicationContext(), "Foto harus dicapture", Toast.LENGTH_LONG).show();
                } else if (((EditText) findViewById(R.id.txtByrMemo)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "Memo   tidak boleh  kosong", Toast.LENGTH_LONG).show();
                } else if (l <= 0) {
                    Toast.makeText(getApplicationContext(), "Bayar Denda/Kunjungan/Titip  tidak boleh Nol Semuanya ", Toast.LENGTH_LONG).show();
                } else if (!ListViewDetailsFirstDesignActivity.isGPSEnable(ListViewDetails2FirstDesignActivity.this)) {
                    Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Maaf, GPS anda belum aktif", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(getApplicationContext(), "No Hp Harus diisi", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && ((EditText) findViewById(R.id.txtNewHp)).getText().toString().length() < 10) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Minimal 10 Digit", Toast.LENGTH_LONG).show();
                } else if (findViewById(R.id.lnrNewHp).getVisibility() == (View.VISIBLE) && !((EditText) findViewById(R.id.txtNewHp)).getText().toString().startsWith("08")) { //bitmap2 ==null
                    Toast.makeText(getApplicationContext(), "No Hp Harus start 08", Toast.LENGTH_LONG).show();
                } else {
                    showMessageLain("Apa data benar ?", "YA", "TIDAK", null, true, 0);
                }


            }
        });
        btnCapturePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        findViewById(R.id.tblByrPhotoBayar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(CAMERA_REQUEST_LAIN);
            }
        });
        findViewById(R.id.btnFotoDepan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(CAMERA_REQUEST_DEPAN1);
            }
        });
        findViewById(R.id.btnFotoKanan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(CAMERA_REQUEST_KANAN2);
            }
        });
        findViewById(R.id.btnFotoKiri).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(CAMERA_REQUEST_KIRI3);
            }
        });
        findViewById(R.id.btnFotoBelakang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(CAMERA_REQUEST_BELANAG4);
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
    }

    public static void navigateLain(AppCompatActivity activity, OrderItem obj) {
        Intent intent = new Intent(activity, ListViewDetails2FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("status", "LAIN");
        ActivityCompat.startActivityForResult(activity, intent, AppController.REQUEST_DETAIL, null);

    }

    public static void navigate(AppCompatActivity activity, OrderItem obj) {
        Intent intent = new Intent(activity, ListViewDetails2FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("status", "BAYAR");
        ActivityCompat.startActivityForResult(activity, intent, AppController.REQUEST_DETAIL, null);

    }

    public static void navigateTB(AppCompatActivity activity, OrderItem obj) {
        Intent intent = new Intent(activity, ListViewDetails2FirstDesignActivity.class);
        intent.putExtra("order", (Parcelable) obj);
        intent.putExtra("status", "TB");
        ActivityCompat.startActivityForResult(activity, intent, AppController.REQUEST_DETAIL, null);
    }

    public String getIntentStringExtra(String key) {
        return getIntentStringExtra(getIntent(), key);
    }

    public String getIntentStringExtra(Intent intent, String key) {
        if (intent != null && intent.getStringExtra(key) != null) {
            return intent.getStringExtra(key);
        }
        return "";
    }

    public static void viewImage(ImageView img, String absolutePath, int wmax) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePath, options);
        int scale = options.outWidth / wmax;


        options = new BitmapFactory.Options();
        options.inSampleSize = scale;
        Bitmap bmp = BitmapFactory.decodeFile(absolutePath, options);

        img.setImageBitmap(bmp);
    }

    public static void onCompressImage(String file, int quality, int width, int maxpx) {
        String format = "jpg";
        width = width <= 10 ? 540 : width;
        maxpx = maxpx <= 10 ? 540 : maxpx;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, options);
            int scale = options.outWidth / width;
            if (maxpx > 1) {
                scale = Math.max(options.outWidth, options.outHeight) / maxpx;
            }

            options = new BitmapFactory.Options();
            options.inSampleSize = scale + 1;
            Bitmap bmp = BitmapFactory.decodeFile(file, options);

            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(format.equalsIgnoreCase("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG, quality, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
        }
    }

    public void getCamera(String name, Intent data, ImageView imageView) {
        File photo = com.naa.data.Utility.getFileCamera();
        String pfile = com.naa.data.Utility.getCacheDir(name + "_BYTB");
        com.naa.data.Utility.copyFile(photo.getAbsolutePath(), pfile);
        onCompressImage(pfile, 80, 1366, 1366);//540


        viewImage(imageView, pfile, 256);//photo.getAbsolutePath()
        imageView.setTag(pfile);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppController.REQUEST_DETAIL) {
            if (resultCode == AppController.CLOSE_CODE) {
                setResult(AppController.CLOSE_CODE);
                finish();
            }
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("smode", getIntentStringExtra(data, "smode"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            /*bitmap = (Bitmap) data.getExtras().get("data");
            ImageViewHolder.setImageBitmap(bitmap);*/
            getCamera("CAMERA_REQUEST", data, ImageViewHolder);

        } else if (requestCode == CAMERA_REQUEST_LAIN && resultCode == Activity.RESULT_OK) {
            getCamera("CAMERA_REQUEST_LAIN", data, (ImageView) findViewById(R.id.imgByrFoto));
        } else if (requestCode == CAMERA_REQUEST_DEPAN1 && resultCode == Activity.RESULT_OK) {
            /*bitmap = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView)findViewById(R.id.imgFotoDepan);
            imageView.setImageBitmap(bitmap);*/
            getCamera("CAMERA_REQUEST_DEPAN1", data, (ImageView) findViewById(R.id.imgFotoDepan));
        } else if (requestCode == CAMERA_REQUEST_KANAN2 && resultCode == Activity.RESULT_OK) {
           /* bitmap2 = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView)findViewById(R.id.imgFotoKanan);
            imageView.setImageBitmap(bitmap2);*/
            getCamera("CAMERA_REQUEST_KANAN2", data, (ImageView) findViewById(R.id.imgFotoKanan));
        } else if (requestCode == CAMERA_REQUEST_KIRI3 && resultCode == Activity.RESULT_OK) {
           /* bitmap3 = (Bitmap) data.getExtras().get("data");
            ImageView imageView = (ImageView)findViewById(R.id.imgFotoKiri);
            imageView.setImageBitmap(bitmap3);*/
            getCamera("CAMERA_REQUEST_KIRI3", data, (ImageView) findViewById(R.id.imgFotoKiri));
        } else if (requestCode == CAMERA_REQUEST_BELANAG4 && resultCode == Activity.RESULT_OK) {
            /* bitmap4 = (Bitmap) data.getExtras().get("data");
             *//*ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);*//*
            ImageView imageView = (ImageView)findViewById(R.id.imgFotoBelakang);
            imageView.setImageBitmap(bitmap4);*/
            getCamera("CAMERA_REQUEST_BELANAG4", data, (ImageView) findViewById(R.id.imgFotoBelakang));
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

        } else if (requestCode == 123 && resultCode == 123) {
            Log.i("checklist", data.getStringExtra("checklist"));
            //order.setText1(data.getStringExtra("checklist"));
            order.setQue1(CheckList.setChecklistQue1(order, data.getStringExtra("checklist")));
            ListViewDetails3FirstDesignActivity.navigateTBA(ListViewDetails2FirstDesignActivity.this, order,
                    String.valueOf(findViewById(R.id.imgFotoDepan).getTag()),
                    String.valueOf(findViewById(R.id.imgFotoKanan).getTag()),
                    String.valueOf(findViewById(R.id.imgFotoKiri).getTag()),
                    String.valueOf(findViewById(R.id.imgFotoBelakang).getTag()));
        }


    }

    private class ItemAdapter extends ArrayAdapter<Vector> {
        private Vector<ModelData> data;
        private int layout;
        private Context contex;

        public ItemAdapter(Context context, int layoutinflater, Vector dat) {
            super(context, layoutinflater, dat);
            data = dat;
            layout = layoutinflater;
            contex = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) contex.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(layout, parent, false);


            ((TextView) v.findViewById(R.id.textView1)).setText(data.elementAt(position).nama);
            ((TextView) v.findViewById(R.id.textView2)).setText(data.elementAt(position).ans);


            return v;
        }

    }

    private Vector<ModelData> data = new Vector<ModelData>();
    private String que = "";

    public class ModelData {
        public String id;
        public String ans;
        public String nama;

        public ModelData(String nama, String ans) {
            this.nama = nama;
            this.ans = ans;
        }
    }

    DatePicker datePicker;

    public <T extends View> T find(int id) {
        return (T) findViewById(id);
    }

    public <T extends View> T find(int id, Class<? super T> s) {
        return (T) findViewById(id);
    }

    public <T extends View> T findView(View v, int id, Class<? super T> s) {
        return (T) v.findViewById(id);
    }

    private boolean checkImageNoExist(ImageView imageView) {
        String file = String.valueOf(imageView.getTag());
        return !new File(file).exists();
    }

    private Calendar getNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private Calendar get(OrderItem orderItem) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(order.getData1());
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    int jumlahAngsuran = 0;

    private void countNow(String txt1, String txt2, String keakhir) {
        int keawal = AppUtil.getIntCut(order.getKeAwal());
        int ikeakhir = AppUtil.getIntCut(keakhir);


        int totalWaktu = (ikeakhir - keawal) + 1;
        jumlahAngsuran = AppUtil.getIntCut(jmlAng) * totalWaktu;

        jmlTotal = (AppUtil.getIntCut(txt1) + AppUtil.getIntCut(txt2) + jumlahAngsuran) + "";

        countNowB(jmlTotal, jumlahAngsuran);
    }

    public boolean cekPertanyaan() {

        for (int x = 0; x < data.size(); x++) {
            if (data.get(x).ans.trim().equalsIgnoreCase("")) {
                return true;
            }
        }

        return true;
    }

    private void countNowA(String txt1, String txt2, String keakhir) {
        int keawal = AppUtil.getIntCut(order.getKeAwal());
        int ikeakhir = AppUtil.getIntCut(keakhir);


        int totalWaktu = (ikeakhir - keawal) + 1;
        jumlahAngsuran = AppUtil.getIntCut(jmlAng) * totalWaktu;

        jmlTotal = (AppUtil.getIntCut(txt1) + AppUtil.getIntCut(txt2) + jumlahAngsuran) + "";

    }

    private void countNowB(String jmlTotal, int jumlahAngsuran) {
        ((TextView) findViewById(R.id.TextView02)).setText(AppUtil.formatCurrency(jmlTotal));
        ((TextView) findViewById(R.id.TextView04)).setText(AppUtil.formatCurrency(jumlahAngsuran));
    }

    private void setAllData() {
        if (order != null) {

            int keawal = AppUtil.getIntCut(order.getKeAwal());
            int ikeakhir = AppUtil.getIntCut(order.getKeAkhir());


            int totalWaktu = (ikeakhir - keawal) + 1;

            jmlAng = order.getJmlAngsuran();
            txtAng = "ANGS KE " + order.getKeAwal() + " S/D " + order.getKeAkhir();
            String txtAng2 = "ANGS KE " + order.getKeAwal() + " S/D ";
            ((TextView) findViewById(R.id.textView6)).setText(order.getTglJtempo1().substring(0, 10));
            ((TextView) findViewById(R.id.textView2)).setText(txtAng);
            ((TextView) findViewById(R.id.textView7)).setText(AppUtil.formatCurrency(jmlAng));
            ((TextView) findViewById(R.id.textView8)).setText(AppUtil.formatCurrency(order.getJmlDenda()));
            ((TextView) findViewById(R.id.textView9)).setText(AppUtil.formatCurrency(order.getJmlBayarTagih()));
            ((TextView) findViewById(R.id.textView10)).setText(AppUtil.formatCurrency(((AppUtil.getIntCut(order.getJmlAngsuran()))) + AppUtil.getIntCut(order.getJmlDenda()) + AppUtil.getIntCut(order.getJmlBayarTagih())));

            ((EditText) findViewById(R.id.angsuranke)).setText(order.getKeAkhir());
            ((TextView) findViewById(R.id.TextView02)).setText(AppUtil.formatCurrency(AppUtil.getIntCut(jmlAng) * totalWaktu));
            ((TextView) findViewById(R.id.TextView03)).setText(txtAng2);

            ((TextView) findViewById(R.id.txtName)).setText(order.getNama());
            ((TextView) findViewById(R.id.txtDate)).setText(AppUtil.Now().substring(0, 10));
            ((TextView) findViewById(R.id.TextView04)).setText(AppUtil.formatCurrency(AppUtil.getIntCut(jmlAng) * totalWaktu));


        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_listview_first_design_details, menu);
        for (int i = 0; i < menu.size(); i++) {
            Drawable drawable = menu.getItem(i).getIcon();
            if (drawable != null) {
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
        } else if (itemId == android.R.id.home) {

            onBackPressed();
        }
        return true;
    }

    public void showMessageTB(String message, final String PositiveButton, final String NegativeButton, final String CenterButton, boolean cancel, final int request) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("");
        if (NegativeButton != null) {
            if (!NegativeButton.equals("")) {
                dlg.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
        if (PositiveButton != null) {
            if (!PositiveButton.equals("")) {
                dlg.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (request == 0) {
                            if (JanjiBayarActivity.needInputHP(order)) {
                                order.setTelepon(((EditText) findViewById(R.id.txtNewHp)).getText().toString());
                            }

                            //input CheckList


                            Intent intent = new Intent(ListViewDetails2FirstDesignActivity.this,
                                    CheckList.class);
                            //Log.i("checklist-",order.getText1());
                            intent.putExtra("checklist", CheckList.getChecklistQue1(order));//overide
                            ActivityCompat.startActivityForResult(ListViewDetails2FirstDesignActivity.this, intent, 123, null);

                            /*ListViewDetails3FirstDesignActivity.navigateTBA(ListViewDetails2FirstDesignActivity.this,order,
                                    String.valueOf( findViewById(R.id.imgFotoDepan).getTag()),
                                    String.valueOf( findViewById(R.id.imgFotoKanan).getTag()),
                                    String.valueOf( findViewById(R.id.imgFotoKiri).getTag()),
                                    String.valueOf( findViewById(R.id.imgFotoBelakang).getTag()));*/

                        } else if (request == 1) {
                            //syn
                        }

                    }
                });
            }
        }
        if (CenterButton != null) {
            if (!CenterButton.equals("")) {
                dlg.setNeutralButton(CenterButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        }
        dlg.setCancelable(cancel);
        dlg.setMessage(message);
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
    }

    public void showMessage(String message, final String PositiveButton, final String NegativeButton, final String CenterButton, boolean cancel, final int request) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("");
        if (NegativeButton != null) {
            if (!NegativeButton.equals("")) {
                dlg.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
        if (PositiveButton != null) {
            if (!PositiveButton.equals("")) {
                dlg.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (request == 0) {
                            if (JanjiBayarActivity.needInputHP(order)) {
                                order.setTelepon(((EditText) findViewById(R.id.txtNewHp)).getText().toString());
                            }

                            int denda = Utility.getInt(((EditText) findViewById(R.id.editText1)).getText().toString());
                            int jmdenda = Utility.getInt(order.getJmlDenda());
                            if (denda > jmdenda && jmdenda > 0) {
                                Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Denda tidak boleh lebih dari Jumlah Denda", Toast.LENGTH_SHORT).show();
                                ;
                                return;
                            }

                            int tagih = Utility.getInt(((EditText) findViewById(R.id.editText2)).getText().toString());
                            int jmlbtagih = Utility.getInt(order.getJmlBayarTagih());
                            if (tagih > jmlbtagih && jmlbtagih > 0) {
                                Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Bayar tagih tidak boleh lebih dari Jumlah tagih", Toast.LENGTH_SHORT).show();
                                ;
                                return;
                            }


                            if (((EditText) findViewById(R.id.editText1)).getText().toString().trim().equalsIgnoreCase("")) {
                                order.setByrDenda("0");
                            } else {
                                order.setByrDenda(((EditText) findViewById(R.id.editText1)).getText().toString().trim());
                            }

                            if (((EditText) findViewById(R.id.editText2)).getText().toString().trim().equalsIgnoreCase("")) {
                                order.setByrSisa("0");
                            } else {
                                order.setByrSisa(((EditText) findViewById(R.id.editText2)).getText().toString().trim());
                            }


                            order.setJmlTotTerbayar(String.valueOf(AppUtil.getNumber(((TextView) findViewById(R.id.TextView02)).getText().toString())));
                            order.setJmlAngsuran(jmlAng);
                            order.setTglBayar(AppUtil.Now());
                            order.setJumlahangsuran(String.valueOf(jumlahAngsuran));
                            order.setJml_angsuran_ke(((EditText) findViewById(R.id.angsuranke)).getText().toString());

                            order.setAlamat2(":" + ((EditText) findViewById(R.id.txtBertemuNo)).getText().toString() + ":" + ((EditText) findViewById(R.id.txtBertemuDg)).getText().toString() + ":BAYAR");

                            int dendasisa = Utility.getInt(order.getJmlDenda()) - Utility.getInt(order.getByrDenda());
                            if (dendasisa <= 0) {
                                order.setDendasisa("0");
                            } else {
                                order.setDendasisa(String.valueOf(dendasisa));
                            }

                            int tagihsisa = Utility.getInt(order.getJmlBayarTagih()) - Utility.getInt(order.getByrSisa());

                            if (tagihsisa <= 0) {
                                order.setJmlSisa("0");
                            } else {
                                order.setJmlSisa(String.valueOf(tagihsisa));
                            }


                            StringBuffer sbBuffer = new StringBuffer("");
                            for (int i = 0; i < data.size(); i++) {
                                sbBuffer.append(data.elementAt(i).nama).append("=").append(data.elementAt(i).ans).append("|");
                            }
                            String s = sbBuffer.toString();
                            if (s.endsWith("|")) {
                                s = s.substring(0, s.length() - 1);
                            }
                            order.setQue(s.toString());
                            //add
                            order.setText2(Utility.Now());
                            order.setText3(Utility.getSetting(getApplicationContext(), "GPS", ""));

                            ListViewDetails3FirstDesignActivity.navigateA(ListViewDetails2FirstDesignActivity.this, order, String.valueOf(findViewById(R.id.imgFotoBayar).getTag()), ((EditText) findViewById(R.id.angsuranke)).getText().toString(), String.valueOf(jumlahAngsuran));

                        } else if (request == 1) {
                            //syn
                        }

                    }
                });
            }
        }
        if (CenterButton != null) {
            if (!CenterButton.equals("")) {
                dlg.setNeutralButton(CenterButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        }
        dlg.setCancelable(cancel);
        dlg.setMessage(message);
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
    }

    public void showMessageLain(String message, final String PositiveButton, final String NegativeButton, final String CenterButton, boolean cancel, final int request) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("");
        if (NegativeButton != null) {
            if (!NegativeButton.equals("")) {
                dlg.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
        if (PositiveButton != null) {
            if (!PositiveButton.equals("")) {
                dlg.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (request == 0) {
                            if (JanjiBayarActivity.needInputHP(order)) {
                                order.setTelepon(((EditText) findViewById(R.id.txtNewHp)).getText().toString());
                            }

                           /* int denda = Utility.getInt(((EditText)findViewById(R.id.editText1)).getText().toString());
                            int jmdenda = Utility.getInt(order.getJmlDenda());
                            if (denda>jmdenda && jmdenda >0){
                                Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Denda tidak boleh lebih dari Jumlah Denda", Toast.LENGTH_SHORT).show();;
                                return;
                            }

                            int tagih = Utility.getInt(((EditText)findViewById(R.id.editText2)).getText().toString());
                            int jmlbtagih = Utility.getInt(order.getJmlBayarTagih());
                            if (tagih>jmlbtagih && jmlbtagih >0){
                                Toast.makeText(ListViewDetails2FirstDesignActivity.this, "Bayar tagih tidak boleh lebih dari Jumlah tagih", Toast.LENGTH_SHORT).show();;
                                return;
                            }*/


                            /*if(((EditText)findViewById(R.id.editText1)).getText().toString().trim().equalsIgnoreCase("")){
                                order.setByrDenda("0");
                            }else{
                                order.setByrDenda(((EditText)findViewById(R.id.editText1)).getText().toString().trim());
                            }

                            if(((EditText)findViewById(R.id.editText2)).getText().toString().trim().equalsIgnoreCase("")){
                                order.setByrSisa("0");
                            }else{
                                order.setByrSisa(((EditText)findViewById(R.id.editText2)).getText().toString().trim());
                            }*/


                            //order.setJmlTotTerbayar(String.valueOf(AppUtil.getNumber(((TextView)findViewById(R.id.TextView02)).getText().toString())));
                            //order.setJmlAngsuran(jmlAng);
                            order.setTglBayar(AppUtil.Now());
                            //order.setJumlahangsuran(String.valueOf(jumlahAngsuran));
                            //order.setJml_angsuran_ke(((EditText)findViewById(R.id.angsuranke)).getText().toString());
                            //order.setAlamat2(":"+((EditText)findViewById(R.id.txtBertemuNo)).getText().toString() +":"+((EditText)findViewById(R.id.txtBertemuDg)).getText().toString()+":BAYAR");

                            /*int dendasisa = Utility.getInt(order.getJmlDenda()) - Utility.getInt(order.getByrDenda());
                            if (dendasisa <= 0){
                                order.setDendasisa("0");
                            }else{
                                order.setDendasisa(String.valueOf(dendasisa));
                            }*/

                            /*int tagihsisa = Utility.getInt( order.getJmlBayarTagih()) - Utility.getInt(order.getByrSisa());

                            if (tagihsisa <= 0){
                                order.setJmlSisa("0");
                            }else{
                                order.setJmlSisa(String.valueOf(tagihsisa));
                            }*/


                            order.setByrDenda(find(R.id.txtByrDenda, EditText.class).getText().toString());
                            order.setByrSisa(find(R.id.txtByrKunjungan, EditText.class).getText().toString());
                            order.setDiscount(find(R.id.txtByrTitipanAng, EditText.class).getText().toString());

                            long l = Utility.getInt(order.getByrDenda().trim()) +
                                    Utility.getInt(order.getByrSisa().trim()) +
                                    Utility.getInt(order.getDiscount().trim());
                            order.setJmlTotTerbayar(String.valueOf(l));

                            StringBuffer lainBayar = new StringBuffer("");
                            lainBayar.append("Byr Denda").append(":").append(find(R.id.txtByrDenda, EditText.class).getText().toString()).append(",");
                            lainBayar.append("Byr Kunjungan").append(":").append(find(R.id.txtByrKunjungan, EditText.class).getText().toString()).append(",");
                            lainBayar.append("Byr Titipan Angsuran").append(":").append(find(R.id.txtByrTitipanAng, EditText.class).getText().toString());
                            order.setDownloadPertanyaan(lainBayar.toString());
                            order.setAlasan(find(R.id.txtByrMemo, EditText.class).getText().toString());

                            StringBuffer sbBuffer = new StringBuffer("");
                            for (int i = 0; i < data.size(); i++) {
                                sbBuffer.append(data.elementAt(i).nama).append("=").append(data.elementAt(i).ans).append("|");
                            }
                            String s = sbBuffer.toString();
                            if (s.endsWith("|")) {
                                s = s.substring(0, s.length() - 1);
                            }
                            order.setQue(s.toString());
                            //add
                            order.setText2(Utility.Now());
                            order.setText3(Utility.getSetting(getApplicationContext(), "GPS", ""));

                            ListViewDetails3FirstDesignActivity.navigateL(ListViewDetails2FirstDesignActivity.this, order, String.valueOf(findViewById(R.id.imgByrFoto).getTag()), find(R.id.txtByrDenda, EditText.class).getText().toString(), find(R.id.txtByrKunjungan, EditText.class).getText().toString(), find(R.id.txtByrTitipanAng, EditText.class).getText().toString());

                        } else if (request == 1) {
                            //syn
                        }

                    }
                });
            }
        }
        if (CenterButton != null) {
            if (!CenterButton.equals("")) {
                dlg.setNeutralButton(CenterButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            }
        }
        dlg.setCancelable(cancel);
        dlg.setMessage(message);
        AlertDialog alertDialog = dlg.create();
        alertDialog.show();
    }

    private void Camerapermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(ListViewDetails2FirstDesignActivity.this
                ,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ListViewDetails2FirstDesignActivity.this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ListViewDetails2FirstDesignActivity.this,
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
        openCamera(CAMERA_REQUEST);
    }

    void openCamera(int request) {
        try {
           /* File directory = new File(  Environment.getExternalStorageDirectory(), "iCollector" + "/" + getPackageName());

            SimpleDateFormat sdfPic = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String currentDateandTime = sdfPic.format(new Date()).replace(" ", "");
            File imagesFolder = new File(directory.getAbsolutePath());
            imagesFolder.mkdirs();
*/


            /*String fname = "IMAGE_" + currentDateandTime + ".jpg";
            File file = new File(imagesFolder, fname);
            fileUri = Uri.fromFile(file);*/

            /*File tempFile = File.createTempFile("photo"+request, ".jpg", ListViewDetails2FirstDesignActivity.this.getCacheDir());
            tempFile.setWritable(true, false);*/

            File photo = com.naa.data.Utility.getFileCamera();
            photo.delete();
            photo.createNewFile();


            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempFile);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

            Uri photoURI = FileProvider.getUriForFile(this,
                    "com.icollection.fileprovider",  photo);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}