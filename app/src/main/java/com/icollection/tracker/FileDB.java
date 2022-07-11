package com.icollection.tracker;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import com.google.gson.Gson;
import com.icollection.modelservice.OrderItem;
import com.naa.data.Nson;
import com.naa.data.Utility;
import com.naa.data.UtilityAndroid;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class FileDB  {
    public static  void clear(Context context, String file){
         try {
             new File(context.getExternalFilesDir(null),  file).delete();
         }catch (Exception e){}
    }
    public static File getPathFile(Context context, String file){
         return new File(context.getExternalFilesDir(null),  file);
    }

    public static InputStream openInputStream(Context context, String file) throws FileNotFoundException {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.openInputStream(findUriFirst(context, file));
        } else {
            return new FileInputStream(new File(context.getExternalFilesDir(null),  file));
        }*/
        ContentResolver resolver = context.getContentResolver();
        return resolver.openInputStream(findUriFirst(context, file));
    }
    public static OutputStream openOutputStream(Context context, String file) throws FileNotFoundException {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = context.getContentResolver();
            return resolver.openOutputStream(findUriFirst(context, file));
        } else {
            return new FileOutputStream(new File(context.getExternalFilesDir(null),  file));
        }*/
        ContentResolver resolver = context.getContentResolver();
        return resolver.openOutputStream(findUriFirst(context, file));
    }

    public static void _saveData(Context context, OrderItem dbFile){
        /*Gson gson = new Gson();
        Uri uri  = _findUriFirst(context, BACKUP_NAME);
        if (uri!=null){
            Nson nson = Nson.newObject();
            ContentResolver resolver = context.getContentResolver();
            try {
                nson = Nson.readNson(Utility.readFile(resolver.openInputStream(uri)) ) ;
            }catch (Exception exception){
                exception.printStackTrace();
            };
            if (!nson.isNsonObject()){
                nson = Nson.newObject();
            }
            nson.set(dbFile.getNoPsb(), gson.toJson(dbFile));
            try {
                nson.toJson(resolver.openOutputStream(uri,"w"));
            }catch (Exception exception){};
        }*/
    }
    public static void _deleteData(Context context, OrderItem dbFile){
        /*Gson gson = new Gson();
        Uri uri  = _findUriFirst(context, BACKUP_NAME);
        if (uri!=null){
            Nson nson = Nson.newObject();
            ContentResolver resolver = context.getContentResolver();
            try {
                nson = Nson.readNson(Utility.readFile(resolver.openInputStream(uri)) ) ;
            }catch (Exception exception){};
            if (!nson.isNsonObject()){
                nson = Nson.newObject();
            }
            nson.remove(dbFile.getNoPsb());
            try {
                nson.toJson(resolver.openOutputStream(uri,"w"));
            }catch (Exception exception){};
        }*/
    }
    public static void _restoreData(Context context){
        /*Gson gson = new Gson();
        Uri uri  = _findUriFirst(context, BACKUP_NAME);
        if (uri!=null){
            Nson nson = Nson.newObject();
            ContentResolver resolver = context.getContentResolver();
            try {
                nson = Nson.readNson(Utility.readFile(resolver.openInputStream(uri)) ) ;
            }catch (Exception exception){
                exception.printStackTrace();
            }
            if (!nson.isNsonObject()){
                nson = Nson.newObject();
            }
            Nson keys = nson.getObjectKeys();
            for (int i = 0; i < keys.size(); i++) {
                try {
                    OrderItem orderItem= gson.fromJson(nson.get(keys.get(i).asString()).asString(), OrderItem.class);
                    FlowManager.getModelAdapter(OrderItem.class).insert(orderItem);
                }catch (Exception exception){};
            }

        }*/
    }
    public static Uri getUri(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }else{
            return Uri.parse("content://downloads/public_downloads");
        }
    }
    public static void A(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //String s = openInputStream(Utility.getAppContext(),"a");
               // openOutputStream(Utility.getAppContext(),"a").write( (s+"assss").getBytes());
            }
        }catch (Exception exception){}

    }
    private static Uri _findUriFirst(Context context, String fileName) {
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        Uri collection =  getUri();
        String selection = MediaStore.MediaColumns.DISPLAY_NAME +    " = ?";
        String[] selectionArgs = new String[] { fileName };
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int nameColumn =  cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            Uri contentUri = ContentUris.withAppendedId( getUri(), id);
            return contentUri;//first
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        //contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        Uri uri = context.getContentResolver().insert(getUri(), contentValues);
        return  uri;
    }

    private static Uri findUriFirst(Context context, String fileName) {
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        Uri collection =  getUri();
        String selection = MediaStore.MediaColumns.DISPLAY_NAME +    " = ?";
        String[] selectionArgs = new String[] { fileName };
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int nameColumn =  cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            Uri contentUri = ContentUris.withAppendedId( getUri(), id);
            return contentUri;//first
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        Uri uri = context.getContentResolver().insert(getUri(), contentValues);
        return  uri;
    }


    static final String BACKUP_NAME =  "fc31mdxunikitamdcon65";
    static final String BACKUP_MIME =  ".txt";

    @RequiresApi(Build.VERSION_CODES.Q)
    private static void saveFileUsingMediaStore(Context context, String fileName, String data) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, BACKUP_MIME);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try {
               // resolver.delete(uri, null, null);
            }catch (Exception a){}
            try {
                OutputStream outputStream = resolver.openOutputStream(uri);
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
            }catch (Exception a){
                a.printStackTrace();
            }
        }


        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.MediaColumns.DISPLAY_NAME +
                " = ?";
        String[] selectionArgs = new String[] { "a" };
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int nameColumn =  cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
            try {
                OutputStream outputStream = resolver.openOutputStream(contentUri);
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
            }catch (Exception a){
                a.printStackTrace();
            }
            break;
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private static String   readFileUsingMediaStore(Context context, String fileName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, ".txt");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            //resolver.openOutputStream(uri);
            try {
                InputStream inputStream = resolver.openInputStream(uri);
                String s = Utility.readFile(inputStream);
                inputStream.close();
                //return s;
            }catch (Exception a){
                a.printStackTrace();
            }
        }

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE
        };
        Uri collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.MediaColumns.DISPLAY_NAME +
                " = ?";
        String[] selectionArgs = new String[] { "a" };
        String sortOrder = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";

        Cursor cursor = context.getContentResolver().query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
        int nameColumn =  cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id);
            try {
                InputStream inputStream = resolver.openInputStream(contentUri);
                String s = Utility.readFile(inputStream);
                inputStream.close();
                return s;
            }catch (Exception a){
                a.printStackTrace();
            }
        }
        return "";
    }
    private final static FileDB fileDB = new FileDB();
    public static FileDB get (){
        return fileDB;
    }
    public  void writeAll(Nson data){
        try {
            //FileOutputStream fileOutputStream = new FileOutputStream( Environment.getExternalStorageDirectory()+ File.separator+"dbhc");
            FileOutputStream fileOutputStream = new FileOutputStream(  getPathFile (com.naa.data.Utility.getAppContext(),   "dbhc"));

            data.toJson(fileOutputStream);
            fileOutputStream.close();
        }catch (Exception e){}
    }
    public Nson readAll(){
        Nson nson = Nson.newObject();
        try {
            //String s = Utility.readFile(new FileInputStream( Environment.getExternalStorageDirectory() + File.separator+"dbhc"));
            String s = Utility.readFile(new FileInputStream( getPathFile(com.naa.data.Utility.getAppContext(),   "dbhc")));
            nson = Nson.readNson(s);
        }catch (Exception e){}
        if (nson.isNsonObject()){
        }else{
            nson = Nson.newObject();
        }
        return nson;
    }
    public  void delete(String ID){
            Nson readAll = readAll();
            readAll.remove(ID);
            writeAll(readAll);
    }
    public  void add(Nson data){

            String ID = String.valueOf(System.currentTimeMillis());
            Nson readAll = readAll();
            readAll.set(ID, data);
            writeAll(readAll);

    }
    public  void set(String ID, Nson data){
            Nson readAll = readAll();
            readAll.set(ID, data);
            writeAll(readAll);
    }
}
