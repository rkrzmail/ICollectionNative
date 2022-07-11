package com.icollection.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.icollection.AppController;
import com.icollection.modelservice.OrderItem;
import com.naa.data.Nson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

public class DB {
    public static void delete(Context context, OrderItem dbFile){
        FileDB._deleteData(context, dbFile);

        /*Gson gson = new Gson();
        UtilityAndroid.delSetting(context, "-"+dbFile.getNoPsb());*/
        /*try {
            String fname = "dapp";
            Nson nson = Nson.readNson(Utility.readFile(FileDB.openInputStream(context, fname)));
            Nson keys = nson.getObjectKeys();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i).asString();
                if (key.equalsIgnoreCase(dbFile.getNoPsb())){
                    nson.asObject().remove(key);
                }
            }
            nson.toJson(FileDB.openOutputStream(context, fname));
        } catch (Exception e) {
            Log.d("test", e.toString());
        }*/
    }
    public static void save(Context context, OrderItem dbFile){
        FileDB._saveData(context, dbFile);

        /*Gson gson = new Gson();
        UtilityAndroid.setSetting(context, "-"+dbFile.getNoPsb(), gson.toJson(dbFile));*/

        /*String fname = "dapp";
        Gson gson = new Gson();

        try {
            Nson nson;
            try {
                nson = Nson.readNson(Utility.readFile(FileDB.openInputStream(context, fname)));
            }catch (Exception exception){nson = Nson.newObject();}
            if (!nson.isNsonObject()){
                nson = Nson.newObject();
            }

            nson.set(dbFile.getNoPsb(),  gson.toJson(dbFile) );
            nson.toJson(FileDB.openOutputStream(context, fname));
        } catch (Exception e) {
            Log.d("test", e.toString());
        }*/
    }
    public static void restore(Context context){
        FileDB._restoreData(context);

        /*Gson gson = new Gson();
        SharedPreferences settings = context.getApplicationContext().getSharedPreferences("SmartMobile", 0);
        Map<String, ?>  all = settings.getAll();
        Iterator iterator = all.keySet().iterator();

        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.startsWith("-")){
                try {
                    Nson nson = Nson.readNson( String.valueOf( all.get(key)  ) );
                    OrderItem orderItem= gson.fromJson(nson.get(key).asString(), OrderItem.class);
                    FlowManager.getModelAdapter(OrderItem.class).insert(orderItem);
                }catch (Exception exception){}
            }
        }*/

        /*Gson gson = new Gson();
        try {
            String fname = "dapp";

            Nson nson = Nson.readNson(Utility.readFile(FileDB.openInputStream(context, fname)));
            Nson keys = nson.getObjectKeys();
            for (int i = 0; i < keys.size(); i++) {
                String key = keys.get(i).asString();
                try {
                    OrderItem orderItem= gson.fromJson(nson.get(key).asString(), OrderItem.class);
                    FlowManager.getModelAdapter(OrderItem.class).insert(orderItem);
                }catch (Exception exception){}
            }
        } catch (Exception e) {
            Log.d("test", e.toString());
        }*/
    }
}
