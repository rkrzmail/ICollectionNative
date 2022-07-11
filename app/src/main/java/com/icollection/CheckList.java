package com.icollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.icollection.modelservice.OrderItem;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class CheckList extends AppCompatActivity {
    Map<String, String> list = new HashMap();
    Map<String, String> vals = new HashMap();
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklist);
        LinearLayout linearLayout = findViewById(R.id.content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        if (getIntent()!=null){
            String str = String.valueOf(getIntent().getStringExtra("checklist"));
            Vector<String>  strV = Utility.splitVector(str,",");
            for (int i = 0; i < strV.size(); i++) {
                vals.put(String.valueOf(vals.size()),String.valueOf(strV.get(i)).equalsIgnoreCase("1")?"1":"0");
            }
        }

        final StringBuffer sbuff = new StringBuffer(" ").append("\n");
        //sbuff.append("STNK Kendaraan               [ ]").append("\n");
        sbuff.append("Lampu Besar                  [ ]").append("\n");
        /*sbuff.append("Lampu Stop (Belakang)        [ ]").append("\n");
        sbuff.append("Lampu Sein Depan (R/L)       [ ]").append("\n");
        sbuff.append("Lampu Sein Belakang (R/L)    [ ]").append("\n");
        sbuff.append("Lampu Sein Belakang (R/L)    [ ]").append("\n");
        sbuff.append("Cover Body Belakang          [ ]").append("\n");
        sbuff.append("Stiker                       [ ]").append("\n");
        sbuff.append("Sparkboard Depan             [ ]").append("\n");
        sbuff.append("Sparkboard Belakang          [ ]").append("\n");
        sbuff.append("Tutup Shock Depan (R/L)      [ ]").append("\n");*/
        sbuff.append("Kunci Kontak                 [ ]").append("\n");
        /*sbuff.append("Panel Starter/Lampu          [ ]").append("\n");
        sbuff.append("Panel Klakson                [ ]").append("\n");
        sbuff.append("Besi Belakang Jok            [ ]").append("\n");
        sbuff.append("Pedal Verseneling            [ ]").append("\n");*/
        sbuff.append("Accu                         [ ]").append("\n");
        sbuff.append("Tool Kit                     [ ]").append("\n");
        /*sbuff.append("Foot Step Depan (R/L)        [ ]").append("\n");
        sbuff.append("Foot Step Belakang (R/L)     [ ]").append("\n");
        sbuff.append("Spion                        [ ]").append("\n");*/
        sbuff.append("Standard Tengah              [ ]").append("\n");
        /*sbuff.append("Standard Samping             [ ]").append("\n");
        sbuff.append("Kick Starter                 [ ]").append("\n");*/
        sbuff.append("Karburator/CDI               [ ]").append("\n");
        /*sbuff.append("Master Rem Cakram            [ ]").append("\n");
        sbuff.append("Caliver (Jepitan Disk)       [ ]").append("\n");
        sbuff.append("Disk Brake                   [ ]").append("\n");
        sbuff.append("Tromol (Depan/Belakang)      [ ]").append("\n");
        sbuff.append("Pedal Rem                    [ ]").append("\n");*/
        sbuff.append("Speedometer                  [ ]").append("\n");
        /*sbuff.append("Kabel Speedometer            [ ]").append("\n");
        sbuff.append("Kabel RPM                    [ ]").append("\n");
        sbuff.append("Shock Absorber Depan         [ ]").append("\n");
        sbuff.append("Shock Absorber Belakang      [ ]").append("\n");
        sbuff.append("Lengan Ayun/Arm              [ ]").append("\n");
        sbuff.append("Velg Depan                   [ ]").append("\n");
        sbuff.append("Velg Belakang                [ ]").append("\n");
        sbuff.append("Ban Depan                    [ ]").append("\n");
        sbuff.append("Ban Belakang                 [ ]").append("\n");
        sbuff.append("Tanki BBM                    [ ]").append("\n");
        sbuff.append("Tanki Oli Samping            [ ]").append("\n");
        sbuff.append("Jok                          [ ]").append("\n");
        sbuff.append("Setang                       [ ]").append("\n");*/
        sbuff.append("Knalpot                      [ ]").append("\n");
        /*sbuff.append("Tutup Rantai                 [ ]").append("\n");*/
        //sbuff.append("Mesin Dapat Hidup            [ ]").append("\n");

        Vector<String>  stringVector = Utility.splitVector(Utility.replace(sbuff.toString(),"[ ]",""),"\n");
        for (int i = 0; i < stringVector.size(); i++) {
            String item = stringVector.get(i).trim();
            if (!item.equalsIgnoreCase("")){
                String val = String.valueOf( vals.get(String.valueOf(list.size()))).equalsIgnoreCase("1")?"1":"0";
                list.put(String.valueOf(list.size()),val);

                View v = UtilityAndroid.inflate(this, R.layout.checklist_item);
                /*CheckBox checkBox = v.findViewById(R.id.checkBox);
                checkBox.setText(item);
                checkBox.setChecked(val.equalsIgnoreCase("1"));
                checkBox.setTag(String.valueOf(list.size()-1));
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String i = String.valueOf(buttonView.getTag());
                        list.put(i, isChecked?"1":"0");
                    }
                });*/
                TextView textView = v.findViewById(R.id.textSpin);
                textView.setText(item);
                Spinner spinner = v.findViewById(R.id.spinner);
                spinner.setSelection(val.equalsIgnoreCase("1")?1:(val.equalsIgnoreCase("2")?2:0));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                        String i = String.valueOf(this.spn.getTag());
                        list.put(i, String.valueOf(position));
                    }
                    Spinner spn;
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                    public AdapterView.OnItemSelectedListener get (Spinner spn){
                        this.spn = spn;
                        return this;
                    }
                }.get(spinner));
                spinner.setTag(String.valueOf(list.size()-1));

                linearLayout.addView(v);
            }
        }

        findViewById(R.id.lanjut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessage("Apa data benar ?", "YA", "TIDAK", null, true, 0);
            }
        });
    }
    private static final String CHECK_SPARATOR = "#|#" ;
    public static String getChecklistQue1(OrderItem item){
        if (String.valueOf(item.getQue1()).contains(CHECK_SPARATOR)) {
            return item.getQue1().substring(item.getQue1().indexOf(CHECK_SPARATOR)+3);
        }
        return  "";
    }
    public static String setChecklistQue1(OrderItem item, String check){
        if (String.valueOf(item.getQue1()).contains(CHECK_SPARATOR)) {
            return getQue1(item.getQue1()) + CHECK_SPARATOR + check;
        }
        return item.getQue1() + CHECK_SPARATOR + check;
    }
    public static String getQue1(String que1){
        if (String.valueOf(que1).contains(CHECK_SPARATOR)) {
            return que1.substring(0, que1.indexOf(CHECK_SPARATOR));
        }
        return  que1;
    }
    public static String setQue1(OrderItem item, String que1){
        if (String.valueOf(item.getQue1()).contains(CHECK_SPARATOR)) {
            return que1 + CHECK_SPARATOR + getChecklistQue1(item);
        }
        return  que1;
    }

    public void showMessage(String message,final String PositiveButton,final String NegativeButton, final String CenterButton, boolean cancel,final int request) {

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("");
        if (NegativeButton!=null) {
            if (!NegativeButton.equals("")) {
                dlg.setNegativeButton(NegativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
        if (PositiveButton!=null) {
            if (!PositiveButton.equals("")) {
                dlg.setPositiveButton(PositiveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (request==0) {
                            //input CheckList
                            boolean mand = false;
                            Intent intent = new Intent();

                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < list.size(); i++) {
                                if (i>=1) stringBuilder.append(",");
                                String value = list.get(String.valueOf(i));
                                stringBuilder.append(value);
                                if (value.equalsIgnoreCase("0")||value.equalsIgnoreCase("")){
                                    mand = true;
                                    break;
                                }
                            }

                            if (mand){
                                Toast.makeText(CheckList.this, "Semua Harus diisi", Toast.LENGTH_LONG).show();
                                return;
                            }
                            intent.putExtra("checklist", stringBuilder.toString());
                            setResult(123, intent);
                            finish();

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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home ) {

            onBackPressed();
        }
        return true;
    }
}
