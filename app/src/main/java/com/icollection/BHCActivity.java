package com.icollection;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icollection.modelservice.OrderItem;
import com.icollection.tracker.FileDB;
import com.icollection.tracker.GpsTrackerIntentService;
import com.icollection.tracker.GpsTrackerJobService;
import com.naa.data.Nson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.naa.utils.Expression;
import com.naa.utils.Messagebox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

public class BHCActivity extends AppCompatActivity {
    LinearLayout content ;
    public static boolean existBHC(Nson nbhc, OrderItem order){
        if (nbhc.get("data").containsKey( getTglKirimBHC(order.getData1())+ order.getNoPsb() + BHCActivity.getStatusBHC(order.getStatusBayar()))) {
            return true;
        }
        return false;
    }
    private String getDefault(Nson data, String s){
        if (s.startsWith("@")){
            s = s.substring(1);
            if (data.containsKey(s)){
                return data.get(s).asString();
            }
            return "";
        }
        return s;
    }
    public static String getStatusBHC(String statusbayar){
        /*if (statusbayar.equals("KUNJUNGAN")) {
            statusbayar =("Gagal_Bertemu");
        }else if (statusbayar.equals("BAYAR")) {
            statusbayar = ("Bayar");
        }else if (statusbayar.equals("BELUM")) {
            statusbayar =("JB");//Janji Bayar
        }else{
            //sama
        }*/
        //statusbayar =("Gagal_Bayar");//bypass
        return statusbayar;
    }
    public static String getTglKirimBHC(String tanggalkirim){
        /*if (statusbayar.equals("KUNJUNGAN")) {
            statusbayar =("Gagal_Bertemu");
        }else if (statusbayar.equals("BAYAR")) {
            statusbayar = ("Bayar");
        }else if (statusbayar.equals("BELUM")) {
            statusbayar =("JB");//Janji Bayar
        }else{
            //sama
        }*/
        //statusbayar =("Gagal_Bayar");//bypass
        if (tanggalkirim!=null){
            if (tanggalkirim.length()>=10){
                //yyyy-mm-dd
                return tanggalkirim.substring(0,10);
            }
        }else{
            return "0000-00-00";
        }
        return tanggalkirim;
    }
    private void reqNexFocus(View vc){
        int icurr = 0;
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            if (v == vc){
                icurr = i+1;
            }
        }
        for (int i = icurr; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());


            if (v.getVisibility()==View.VISIBLE && editText.isEnabled()){
                if (type.equalsIgnoreCase("combo")) {
                    v.findViewById(R.id.spnText).requestFocus();
                }else if (type.equalsIgnoreCase("date")){
                   v.findViewById(R.id.datText).requestFocus();
                }else if (type.equalsIgnoreCase("view")){
                    //none
                    continue;
                }else if (type.equalsIgnoreCase("title")){
                    //none
                    continue;
                }else if (type.equalsIgnoreCase("button")){
                    v.findViewById(R.id.tblText).requestFocus();
                }else{
                    editText.requestFocus();
                }
                break;
            }
        }
    }
    private boolean exNow = false;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bhcgenerator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Nson nson = Nson.newArray();
        try {
            /*nson = Nson.readNson(Utility.readFile(new FileInputStream(Environment.getExternalStorageDirectory()
                    + File.separator+"bhc"))) ;*/
            nson = Nson.readNson(Utility.readFile(new FileInputStream(new File(com.naa.data.Utility.getAppContext().getExternalFilesDir(null),   "bhc")))) ;
        }catch (Exception e){}
        String statusbayar = getStatusBHC(getIntentStringExtra("statusbayar").trim()) ;


        TextView textView = findViewById(R.id.txtPSB);
        textView.setText("NO. PSB : "+getIntentStringExtra("nopsb") + "  (" +statusbayar + ")");
        textView = findViewById(R.id.txtNama);
        textView.setText("NAMA : "+getIntentStringExtra("nama"));
        Nson dataa = Nson.readJson(getIntentStringExtra("order"));
        Log.i("Exporder",getIntentStringExtra("order"));


        content = findViewById(R.id.lnrContent);
        //Toast.makeText(this,nson.toJson(),Toast.LENGTH_SHORT).show();
        for (int i = 0; i < nson.size(); i++) {
            if (nson.get(i).get("Field_id").asString().trim().equalsIgnoreCase(statusbayar+getIntentStringExtra("index").trim())){
                String type =  nson.get(i).get("Type_Tanya").asString();
                String text =  getDefault (dataa, nson.get(i).get("Isian").asString());//default
                String combo = nson.get(i).get("Tanya").asString();
                String label = nson.get(i).get("Tanya_detail").asString();
                String eval =  nson.get(i).get("Terlihat").asString();
                String name =  nson.get(i).get("Name_id").asString();
                String expr =  nson.get(i).toJson();

                View v = null;
                if (type.equalsIgnoreCase("combo")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_combo);
                    Vector array = Utility.splitVector(combo, ",");
                    Spinner spinner =  v.findViewById(R.id.spnText);
                    if (text.trim().equalsIgnoreCase("")){
                        array.insertElementAt("",0);
                    }

                    ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array);
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(aa);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        private View v;
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            validateVisible();
                            if (exNow) {
                                reqNexFocus(v);
                            }
                        }
                        public void onNothingSelected(AdapterView<?> parent) {}
                        public AdapterView.OnItemSelectedListener get(View v ){
                            this.v=v;
                            return  this;
                        }
                    }.get(v));



                }else if (type.equalsIgnoreCase("text")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_text);
                    EditText editText = v.findViewById(R.id.txtText);
                    editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                }else if (type.equalsIgnoreCase("area")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_area);
                    EditText editText = v.findViewById(R.id.txtText);
                    editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                }else if (type.equalsIgnoreCase("phone")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_phone);
                }else if (type.equalsIgnoreCase("number")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_number);
                }else if (type.equalsIgnoreCase("view")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_view);
                }else if (type.equalsIgnoreCase("title")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_title);
                }else if (type.equalsIgnoreCase("button")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_button);
                    v.findViewById(R.id.tblText).setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String text = ((Button)v).getText().toString();
                            //save
                            if (text.equalsIgnoreCase("save")||text.equalsIgnoreCase("simpan")){
                                save();
                            }
                            //lanjut
                            if (text.equalsIgnoreCase("lanjut")||text.equalsIgnoreCase("next")){
                                lanjut();
                            }
                        }
                    });
                    ((Button)v.findViewById(R.id.tblText)).setText(combo);
                }else if (type.equalsIgnoreCase("date")){
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_date);
                    //file hire
                    DatePicker datePicker = ((DatePicker)v.findViewById(R.id.datText));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    Date date = null;
                    try {
                        date = sdf.parse(text);
                    } catch (Exception e) {
                        date = new Date();
                    }

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    datePicker.updateDate(year, month, day);

                    long now = System.currentTimeMillis() - 1000;
                    datePicker.setMinDate(now + (1000 * 60 * 60 * 24 * 0));
                    datePicker.setMaxDate(now + (1000 * 60 * 60 * 24 * 4));


                }else{
                    v = UtilityAndroid.inflate(this, R.layout.bhc_item_text);
                }

                textView = v.findViewById(R.id.lblLabel);
                textView.setText(label);
                if (label.equalsIgnoreCase("")){
                    textView.setVisibility(View.GONE);

                }
                textView.setTag(name);

                if ( v.findViewById(R.id.txtText)instanceof EditText){
                    EditText editText = v.findViewById(R.id.txtText);
                    editText.setText(text);
                    editText.setTag(type);
                }
                v.findViewById(R.id.frmExpression).setTag(expr);
                v.setTag(eval);
                content.addView(v);
            }
        }
         validateVisible();
        findViewById(R.id.tblSimpan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();*/
        findViewById(R.id.tblSimpan).postDelayed(new Runnable() {
            @Override
            public void run() {
                reqNexFocus(findViewById(R.id.tblSimpan));
            }
        },300);

    }

    @Override
    protected void onResume() {
        super.onResume();
        exNow = true;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==12345 && resultCode==RESULT_OK){
            finish();
        }
    }
    public static void resetBHC(Activity activity){
        Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(activity, "BHC","{}"));
        /*if (nbhc.isNsonObject()){
            if (nbhc.get("date").asString().equalsIgnoreCase(com.naa.data.Utility.Now().substring(0,10))){
            }else{
                //reset
                //**********************************************BHC***********************************
                nbhc.set("date", com.naa.data.Utility.Now().substring(0,10));
                nbhc.set("data", Nson.newObject());
                UtilityAndroid.setSetting(activity, "BHC",nbhc.toJson());
            }
        }*/
        Nson key = nbhc.get("data").getObjectKeys();

        if (nbhc.get("version").asInteger()<=1){
            //reset
            UtilityAndroid.setSetting(activity, "BHC","");
        }else{
            for (int i = 0; i < key.size(); i++) {
                if (key.get(i).asString().length()>=10){
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = sdf.parse(key.get(i).asString().substring(0,10));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.DAY_OF_MONTH, -5);

                        if (date.getTime()<=calendar.getTimeInMillis()){
                            //delete
                            nbhc.get("data").remove(key.get(i).asString());
                        }
                    } catch (ParseException e) {
                        //delete
                        nbhc.get("data").remove(key.get(i).asString());
                    }
                }//end date 10
            }//end if

            if (key.size()!=nbhc.get("data").getObjectKeys().size()){
                UtilityAndroid.setSetting(activity, "BHC",nbhc.toJson());
            }
        }
    }


    public static void startBHC(Activity activity,OrderItem myOrder, int req){
        Intent intent =  new Intent(activity, BHCActivity.class);
        intent.putExtra("nopsb", myOrder.getNoPsb());
        intent.putExtra("nama", myOrder.getNama());
        intent.putExtra("statusbayar", myOrder.getStatusBayar());
        intent.putExtra("order",new Gson().toJson(myOrder, OrderItem.class));
        intent.putExtra("kode_ao", myOrder.getKodeAo());
        intent.putExtra("ke_awal", myOrder.getKeAwal());
        intent.putExtra("ke_akhir", myOrder.getKeAkhir());
        intent.putExtra("posko", myOrder.getPosko());
        intent.putExtra("orderX", (Parcelable) myOrder);
        intent.putExtra("tgl_update", com.icollection.util.Utility.Now().substring(0,10));
        intent.putExtra("jam_update",  com.icollection.util.Utility.Now().substring(11).trim());
        intent.putExtra("tgl_kirim", myOrder.getData1());
        intent.putExtra("jam_kirim", "");
        intent.putExtra("id", myOrder.getId());

        OrderItem curOrder = AppController.currentOrder;
        if (curOrder != null){
            if (curOrder.getNoPsb().equalsIgnoreCase(myOrder.getNoPsb()) && curOrder.getStatusBayar().equalsIgnoreCase(myOrder.getStatusBayar())){
                myOrder = curOrder;
            }
        }

        Nson nson = Nson.newObject();
        nson.set("StatusBayar",String.valueOf(myOrder.getStatusBayar()));
        nson.set("Status",String.valueOf(myOrder.getStatus()));
        nson.set("JmlTotTerbayar",String.valueOf(myOrder.getJmlTotTerbayar()));
        nson.set("Jml_angsuran_ke",String.valueOf(myOrder.getJml_angsuran_ke()));
        nson.set("JmlAngsuran",String.valueOf(myOrder.getJmlAngsuran()));
        nson.set("Jumlahangsuran",String.valueOf(myOrder.getJumlahangsuran()));
        nson.set("JumlahSisa",String.valueOf(myOrder.getJumlahSisa()));
        nson.set("ByrDenda",String.valueOf(myOrder.getByrDenda()));
        nson.set("ByrSisa",String.valueOf(myOrder.getByrSisa()));
        nson.set("Discount",String.valueOf(myOrder.getDiscount()));
        nson.set("TglBayar",String.valueOf(myOrder.getTglBayar()));
        nson.set("Tgl_kirim2",String.valueOf(myOrder.getTgl_kirim2()));
        nson.set("Telepon",String.valueOf(myOrder.getTelepon()));
        nson.set("Alasan",String.valueOf(myOrder.getAlasan()));
        nson.set("NOW",String.valueOf(myOrder.getText2()));
        nson.set("GPS",String.valueOf(myOrder.getText3()));

        intent.putExtra("keterangan", nson.toJson());
        if (req == -1 ){
            activity.startActivity(intent);
        }else{
            activity.startActivityForResult(intent, req);
        }
    }
    private void lanjut(){
        if (validateMandatory()){
            Intent intent =  new Intent(BHCActivity.this, BHCActivity.class);
            intent.putExtra("id",           getIntentStringExtra("id"));
            intent.putExtra("nopsb",        getIntentStringExtra("nopsb"));
            intent.putExtra("nama",         getIntentStringExtra("nama"));
            intent.putExtra("statusbayar",  getIntentStringExtra("statusbayar"));
            intent.putExtra("order",        getIntentStringExtra("order"));
            intent.putExtra("kode_ao",      getIntentStringExtra("kode_ao"));
            intent.putExtra("ke_awal",      getIntentStringExtra("ke_awal"));
            intent.putExtra("ke_akhir",     getIntentStringExtra("ke_akhir"));
            intent.putExtra("posko",        getIntentStringExtra("posko"));
            if (getIntent()!=null && getIntent().getParcelableExtra("orderX") instanceof Parcelable ){
                intent.putExtra("orderX", (Parcelable) getIntent().getParcelableExtra("orderX"));
            }
            intent.putExtra("tgl_update",   getIntentStringExtra("tgl_update"));
            intent.putExtra("jam_update",   getIntentStringExtra("jam_update"));
            intent.putExtra("tgl_kirim",   getIntentStringExtra("tgl_kirim"));
            intent.putExtra("jam_kirim",   getIntentStringExtra("jam_kirim"));

            intent.putExtra("index", getIntentStringExtra("index") +"2");
            intent.putExtra("stream",       getData().toJson());
            intent.putExtra("keterangan", getIntentStringExtra("keterangan").trim());
            startActivityForResult(intent,12345);
        }else{
            showInfoDialog("Semua pertanyaan harus diisi", null);
        }

    }

    private void save(){
        String statusbayar = getStatusBHC(getIntentStringExtra("statusbayar").trim()) ;
        if (validateMandatory()){
            Nson hasil = getData();
            Nson g = Nson.readJson(getIntentStringExtra("stream"));
            if (g.isNsonArray() && hasil.isNsonArray()){
                hasil.asArray().addAll(g.asArray());
            }
            //save an send
            Nson data = Nson.newObject();
            data.set("hasil", hasil);
            data.set("id", getIntentStringExtra("id"));

            data.set("nopsb", getIntentStringExtra("nopsb"));
            data.set("nama", getIntentStringExtra("nama"));

            data.set("ke_akhir",getIntentStringExtra("ke_akhir"));
            data.set("ke_awal", getIntentStringExtra("ke_awal"));
            data.set("posko",   getIntentStringExtra("posko"));
            data.set("kode_ao", getIntentStringExtra("kode_ao"));

            data.set("status",    getIntentStringExtra("statusbayar").trim());
            data.set("tgl_update", getIntentStringExtra("tgl_update").trim());
            data.set("jam_update", getIntentStringExtra("jam_update").trim());

            data.set("tgl_kirim", getIntentStringExtra("tgl_kirim").trim());
            data.set("jam_kirim", getIntentStringExtra("jam_kirim").trim());
            data.set("keterangan", getIntentStringExtra("keterangan").trim());

            FileDB.get().add(data);
            com.icollection.util.Utility.setSetting(this,  "pendb","1");
            //**********************************************BHC***********************************
            //save buffer list
            resetBHC(this);

            Nson nbhc = Nson.readNson(UtilityAndroid.getSetting(BHCActivity.this, "BHC","{}"));
            /*if (nbhc.isNsonObject()){
                if (nbhc.get("date").asString().equalsIgnoreCase(Utility.Now().substring(0,10))){
                    if (nbhc.get("data").isNsonObject()){
                    }else{
                        nbhc.set("data", Nson.newObject());
                    }
                }else{
                    nbhc.set("date", Utility.Now().substring(0,10));
                    nbhc.set("data", Nson.newObject());
                }
            }else{
                nbhc = Nson.newObject();
                nbhc.set("date", Utility.Now().substring(0,10));
                nbhc.set("data", Nson.newObject());
            }
            nbhc.get("data").set(getIntentStringExtra("nopsb") + statusbayar,data);
            UtilityAndroid.setSetting(BHCActivity.this, "BHC",nbhc.toJson());*/

            if (nbhc.isNsonObject()){
                //none
            }else{
                nbhc = Nson.newObject();
                nbhc.set("date", Utility.Now().substring(0,10));
                nbhc.set("data", Nson.newObject());
            }
            nbhc.set("version", 2);
            nbhc.get("data").set(getTglKirimBHC(getIntentStringExtra("tgl_kirim")) + getIntentStringExtra("nopsb") + statusbayar,"");
            UtilityAndroid.setSetting(BHCActivity.this, "BHC",nbhc.toJson());

            if (getIntentStringExtra("statusbayar").trim().equalsIgnoreCase("TK BELUM SEMPAT")) {
                simpanBelumSempat();
                //wait
                com.icollection.util.Messagebox.showProsesBar(BHCActivity.this, new com.icollection.util.Messagebox.DoubleRunnable() {
                    @Override
                    public void run() {
                        GpsTrackerIntentService.sendBHC(BHCActivity.this);
                    }
                    @Override
                    public void runUI() {
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
            }else{
                //sync
                Intent intent = new Intent(BHCActivity.this, GpsTrackerIntentService.class);
                intent.putExtra("smode","bhc");//syn bhc only
                startService(intent);

                setResult(Activity.RESULT_OK);
                finish();
            }



        }else{
            showInfoDialog("Semua pertanyaan harus diisi", null);
        }
    }
    private void simpanBelumSempat(){
        if (getIntent().getParcelableExtra("orderX") instanceof OrderItem){
            OrderItem order = getIntent().getParcelableExtra("orderX");
            order.setStatus("pending_janji");


            //add
            order.setText2(com.icollection.util.Utility.Now());
            order.setText3(com.icollection.util.Utility.getSetting(getApplicationContext(),"GPS", ""));
            order.save();
        }
    }
    public void showInfoDialog(String message, DialogInterface.OnClickListener onClickListener){
        if (onClickListener == null){
            onClickListener = onClickListenerDismiss;
        }
        Messagebox.showDialog(this, "", message, "OK", "", onClickListener, null);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home ) {

            onBackPressed();
        }
        return true;
    }

    private final DialogInterface.OnClickListener onClickListenerDismiss = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };
    private void validateVisible(){
        Expression expression = new Expression();
        final Nson nson = Nson.newObject();
        //Nson data = Nson.readJson(getIntentStringExtra("order"));
        //prepare data
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            String kname = "$"+String.valueOf(textView.getTag());
            String value = "";


            if (type.equalsIgnoreCase("combo")) {
                Spinner spinner = v.findViewById(R.id.spnText);
                value =  String.valueOf(spinner.getSelectedItem()).trim();

            }else if (type.equalsIgnoreCase("date")){
                String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                value = mYear  + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2) ;
            }else{
                value = editText.getText().toString();
            }
            if (v.getVisibility()==View.VISIBLE){
                nson.set(kname.toLowerCase().trim(), value);
                Log.i("Exps",kname.toLowerCase().trim() +":"+value);
            }else{
                nson.set(kname.toLowerCase().trim(), value);
                Log.i("Expsx",kname.toLowerCase().trim() +":"+value);
            }


        }
        Log.i("ExpT",AppController.getToken());
        //evaluate
        for (int i = 0; i < content.getChildCount(); i++) {
            View v = content.getChildAt(i);
            String eval = String.valueOf(v.getTag());
            //evaluate expression
            Log.i("Exp",eval);
            boolean visible = false;
            if (eval.equalsIgnoreCase("")||eval.equalsIgnoreCase("Ya")||eval.equalsIgnoreCase("null")||eval.equalsIgnoreCase("true")){
                visible = true;
                Log.i("Exp0",String.valueOf(visible));
            }else{
                try {
                    Object out =  expression.evaluate(eval, new Expression.OnVariableListener() {
                        public Object get(String name) {
                            Log.i("Expg", name+":"+nson.get(name.toLowerCase().trim()));
                            return nson.get(name.toLowerCase().trim());
                        }
                        public void set(String name, Object value) {  }
                    });
                    Log.i("Exp1",String.valueOf(out));
                    visible = String.valueOf(out).equalsIgnoreCase("true");
                    Log.i("Exp2",String.valueOf(String.valueOf(out).equalsIgnoreCase("true")));
                }catch (Exception e){}
            }

            if (visible){
                v.setVisibility(View.VISIBLE);
            }else{
                v.setVisibility(View.GONE);//Gone
            }
        }
    }
    private boolean validateMandatory(){
        for (int i = 0; i < content.getChildCount(); i++) {
            Nson object = Nson.newObject();
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            if (v.getVisibility()==View.VISIBLE && editText.isEnabled()){
                if (type.equalsIgnoreCase("combo")){
                    Spinner spinner =  v.findViewById(R.id.spnText);
                    String text = String.valueOf(spinner.getSelectedItem()).trim() ;
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }else if (type.equalsIgnoreCase("date")){
                    String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                    int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                    int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                    int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                    String sdate = mYear  + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2) ;
                    String text = sdate;
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }else{
                    String text = editText.getText().toString();
                    if ( text.equalsIgnoreCase("")||text.equalsIgnoreCase("null")  ){
                        return false;
                    }
                }
            }

        }
        return true;
    }
    private Nson getData(){
        Nson nson = Nson.newArray();
        for (int i = 0; i < content.getChildCount(); i++) {
            Nson object = Nson.newObject();
            View v = content.getChildAt(i);
            TextView textView = v.findViewById(R.id.lblLabel);
            EditText editText = v.findViewById(R.id.txtText);
            String type =  String.valueOf(editText.getTag());
            Nson param = Nson.readJson(String.valueOf( v.findViewById(R.id.frmExpression).getTag()));
            if (param.isNsonObject()){
                object.asObject().putAll(param.asObject());
            }

            //String.valueOf(textView.getText())
            if (type.equalsIgnoreCase("combo")){
                Spinner spinner =  v.findViewById(R.id.spnText);
                object.set("isi", String.valueOf(spinner.getSelectedItem()).trim());
            }else if (type.equalsIgnoreCase("date")){
                String[] arrMonth = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
                int mYear =  ((DatePicker)v.findViewById(R.id.datText)).getYear();
                int mMonth = ((DatePicker)v.findViewById(R.id.datText)).getMonth();
                int mDay =   ((DatePicker)v.findViewById(R.id.datText)).getDayOfMonth();

                String sdate = mYear  + "-" + arrMonth[mMonth] + "-" + JanjiBayarActivity.LPad(mDay + "", "0", 2) ;
                object.set("isi", sdate);
            }else{
                object.set("isi", editText.getText().toString());
            }
            nson.add(object);
        }
        Log.i("Exp_da", nson.toJson());
        return nson;
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
}
