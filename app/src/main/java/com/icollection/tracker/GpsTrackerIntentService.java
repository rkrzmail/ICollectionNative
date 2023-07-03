package com.icollection.tracker;

import static com.icollection.util.AppUtil.NowX;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.icollection.modelservice.OrderItem_Table;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.icollection.APIInterfacesRest;
import com.icollection.AppController;
import com.icollection.JanjiBayarActivity;
import com.icollection.modelservice.LocationModel;
import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.util.APIClient;
import com.icollection.util.AppUtil;
import com.icollection.util.EasySSLSocketFactory;
import com.icollection.util.SharedPreferencesUtil;
import com.icollection.util.Utility;
import com.naa.data.Nson;
import com.naa.utils.InternetX;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Este intent service es usado por el JobScheduler directamente y por el AlarmManager
 * mediante la clase {@link GpsTrackerWakefulService}.
 * Created by jmarkstar on 24/05/2017.
 */
public class GpsTrackerIntentService extends IntentService implements LocationListener {

    private static final String TAG = "GpsTrackerIntentService";

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private GoogleApiClient mGoogleApiClient;

    public GpsTrackerIntentService() {
        super("GpsTrackerIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v(TAG, "GpsTrackerIntentService ran!");
        startGoogleApiClient();

        sendBHC();
        sendDataPending();

        /*if (getIntentStringExtra(intent, "smode").equalsIgnoreCase("bhc")) {
            sendDataPending();
            sendBHC();
            sendDataPending();
            Utility.setSetting(this,  "auto","");
        }else if (getIntentStringExtra(intent, "smode").equalsIgnoreCase("auto")){
            if (Utility.getSetting(this,  "auto","").equalsIgnoreCase("run")) {
            }else{
                Utility.setSetting(this,  "auto","run");
                sendDataPending();
                sendBHC();
                Utility.setSetting(this,  "auto","");
            }
        }else{
            sendDataPending();
            sendBHC();
            Utility.setSetting(this,  "auto","");
        }*/
    }
    public String getIntentStringExtra(Intent intent, String key) {
        if (intent!=null && intent.getStringExtra(key)!=null){
            return intent.getStringExtra(key);
        }
        return "";
    }
    private synchronized void startGoogleApiClient() {
        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){

                        @Override
                        public void onConnected(@Nullable Bundle bundle) throws SecurityException {
                            Log.i(TAG, "Connection connected");
                            registerRequestUpdate();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            Log.i(TAG, "Connection suspended");
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
                        }
                    })
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }else{
            mGoogleApiClient.connect();
        }
    }

    private void registerRequestUpdate() throws SecurityException {
        LocationRequest mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            sendNewLocation(location);
        } else {
            Log.e(TAG, "Location no detected.");
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Log.v(TAG, "GoogleApiClient was disconnect.");
            mGoogleApiClient.disconnect();
        }
    }

    private SharedPreferencesUtil sharedPreferencesUtil;
    LocationModel locationModel;
    private void sendNewLocation(Location location){
        locationModel = new LocationModel();
        locationModel.setLatitude(location.getLatitude());
        locationModel.setLongitude(location.getLongitude());
        locationModel.setDate(new Date());


        sharedPreferencesUtil = new SharedPreferencesUtil(this);

        saveNewLocation(locationModel);

   /*     Intent intent = new Intent(LocationsActivity.GET_NEW_LOCATION_BROADCAST_RECEIVER);
        intent.putExtra(LocationsActivity.GET_NEW_LOCATION_PARAM, locationModel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
    }

    /** Saves new location on the database.
     * */
    private void saveNewLocation(LocationModel locationModel){
        new SimpleTask().execute();
    }

    private class SimpleTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(String... urls) {
            String result = "";
            try {


                HttpPost post = new HttpPost(AppUtil.BASE_URL+"apicoll/gps_tracking/");



                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("username",
                        sharedPreferencesUtil.getUsername()));
                nameValuePairs.add(new BasicNameValuePair("longitude",
                        String.valueOf(locationModel.getLongitude())));
                nameValuePairs.add(new BasicNameValuePair("latitude",
                        String.valueOf(locationModel.getLatitude())));
                nameValuePairs.add(new BasicNameValuePair("date",
                        locationModel.getDate().toString()));
                nameValuePairs.add(new BasicNameValuePair("key",
                        AppController.getToken()));
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                post.addHeader("X-Api-Key", AppUtil.API_KEY);

                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
                HttpParams params = new BasicHttpParams();
                params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
                params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
                params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
                ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);

                HttpClient client =  new DefaultHttpClient(cm, params);//new DefaultHttpClient();

                HttpResponse response = client.execute(post);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200 || statusCode == 406) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;
        }

        protected void onPostExecute(String jsonString) {
            // Dismiss ProgressBar
            if (jsonString != null && jsonString != "") {
                Log.d("Location send", jsonString);
            }
        }

    }


    public void savePending(OrderItem order) {
        String filename ="";

        if(order.getStatus().equalsIgnoreCase("pending_janji")){
            filename = order.getPhotoBayar();
            order.setStatus("1");
        }else  if(order.getStatus().equalsIgnoreCase("pending_bayar")){
            filename = order.getPhotoBayar();
            order.setStatus("2");
        }

        Bitmap myBitmap =null;
        try {

             /* if(imgFile.exists()){
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
        }

        byte[] byteArray = new byte[0];
        if (myBitmap !=null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();
        }
          RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);

        */

            MultipartBody.Part partbody   = JanjiBayarActivity.getBlankImagePart("photo_bayar",order.getNoPsb()+NowX()+".jpg");
            MultipartBody.Part partbukti  = JanjiBayarActivity.getBlankImagePart("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg");
            MultipartBody.Part parttarik1 = JanjiBayarActivity.getBlankImagePart("photo_tarik1","");
            MultipartBody.Part parttarik2 = JanjiBayarActivity.getBlankImagePart("photo_tarik2","");

            if (filename.equalsIgnoreCase("")){
                //none
            }else if(new  File(filename).exists()){
                String now = NowX();
                File imgFile = new  File(filename);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                partbody =  MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+now+".jpg", requestFile);
                //ada foto
                if(new  File(filename+"tb2").exists()){
                    imgFile = new  File(filename);
                    requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                    partbukti =  MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+now+"tb2.jpg", requestFile);
                }
                if(new  File(filename+"tb3").exists()){
                    imgFile = new  File(filename);
                    requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                    parttarik1 =  MultipartBody.Part.createFormData("photo_tarik1", order.getNoPsb()+now+"tb3.jpg", requestFile);
                }
                if(new  File(filename+"tb4").exists()){
                    imgFile = new  File(filename);
                    requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                    parttarik2 =  MultipartBody.Part.createFormData("photo_tarik2", order.getNoPsb()+now+"tb4.jpg", requestFile);
                }
            }else{
                //none
            }

            if (order.getQue3().equalsIgnoreCase("")){
                //none
            }else if(new  File(order.getQue3()).exists()){
                File imgFile = new  File(order.getQue3());
                RequestBody requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
                partbukti = MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);
            }else{
                //none
            }

            /*RequestBody requestFile;
            RequestBody requestFile2;
            if (filename.equalsIgnoreCase("")){
                byte[] byteArray = new byte[0];
                requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);
            }else if(new  File(filename).exists()){
                File imgFile = new  File(filename);
                requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);

                //ada foto

            }else{
                byte[] byteArray = new byte[0];
                requestFile = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);
            }


            //  RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), byteArray);


            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("photo_bayar", order.getNoPsb()+NowX()+".jpg", requestFile);





            if (order.getQue3().equalsIgnoreCase("")){
                byte[] byteArray = new byte[0];
                requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);
            }else if(new  File(order.getQue3()).exists()){
                File imgFile = new  File(order.getQue3());
                requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"), imgFile);
            }else{
                byte[] byteArray = new byte[0];
                requestFile2 = RequestBody.create(MediaType.parse("image/jpeg"),byteArray);
            }
            MultipartBody.Part bukti =
                    MultipartBody.Part.createFormData("photo_bukti", order.getNoPsb()+NowX()+"b2.jpg", requestFile2);
            */



            APIInterfacesRest apiInterface = APIClient.getClient().create(APIInterfacesRest.class);
            Call<UpdateOrder> updateService = apiInterface.updateData( toRequestBody(AppController.getImei()),
                    toRequestBody(AppController.getToken()),
                    toRequestBody(order.getId()),
                    toRequestBody(AppUtil.replaceNull(order.getNoPsb()  ).trim()),
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
                    toRequestBody(AppUtil.replaceNullMax(order.getAlasan(), 30 ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getUpData() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getDownloadPertanyaan() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getStatusBayar() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getNopol() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getTelepon() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getPosko() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getDendasisa() ).trim()),
                    toRequestBody(AppUtil.replaceNull(order.getAngsisa() ).trim()),
                    partbody,
                    //toRequestBody(AppUtil.replaceNull(order.getPhotoBayar() ).trim()),
                    // toRequestBody(AppUtil.replaceNull(order.getPhotoJanjiBayar() ).trim()),


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
                    partbukti,
                    parttarik1,
                    parttarik2
            );
            updateService.enqueue(new Callback<UpdateOrder>() {
                @Override
                public void onResponse(Call<UpdateOrder> call, Response<UpdateOrder> response) {

                    if (response != null) {

                        if (response.message().equalsIgnoreCase("Ok")) {
                            Log.d("Test", response.message());
                            //  order.setStatus("99");
                            //  order.save();
                            DB.delete(getApplicationContext() , order  );

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


                }

                @Override
                public void onFailure(Call<UpdateOrder> call, Throwable t) {
                    Log.d("Failure", String.valueOf(t.getMessage()));

                }
            });

        }catch (Exception e){}
    }

    public RequestBody toRequestBody(String value) {
        if (value==null){
            value="";
        }
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }


    public void sendDataPending(){
        List<OrderItem> orderLists = new ArrayList<OrderItem>();
        orderLists = (ArrayList) SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in("pending_janji","pending_bayar"))
                .queryList();


        for(int x = 0 ; x < orderLists.size();x++){
            savePending(orderLists.get(x));
        }
        if (orderLists.size()>=1){
            Intent intent = new Intent();
            intent.setAction("com.icollection.reload");
            sendBroadcast(intent);
        }else{
            Utility.setSetting(this,  "pend","");
        }
    }

    public static void sendBHC(Context context){
        Intent intent = new Intent(context, GpsTrackerIntentService.class);
        intent.putExtra("smode","bhc");
        context.startService(intent);
    }
    public  void sendBHC(){
        Utility.setSetting(this,  "pendb","");

        Nson nson = FileDB.get().readAll();
        Nson keys = nson.getObjectKeys();
        for (int i = 0; i < keys.size(); i++) {
            //update

            Map<String, String> args = new HashMap<>();
            args.put("hasil",  nson.get(keys.get(i).asString()).get("hasil").toJson() );
            args.put("no_psb",  nson.get(keys.get(i).asString()).get("nopsb").asString() );
            args.put("nama",    nson.get(keys.get(i).asString()).get("nama").asString() );
            args.put("ke_awal", nson.get(keys.get(i).asString()).get("ke_awal").asString() );
            args.put("ke_akhir",nson.get(keys.get(i).asString()).get("ke_akhir").asString() );
            args.put("kode_ao", nson.get(keys.get(i).asString()).get("kode_ao").asString() );
            args.put("posko",   nson.get(keys.get(i).asString()).get("posko").asString() );
            args.put("status",      nson.get(keys.get(i).asString()).get("status").asString() );
            args.put("tgl_update",  nson.get(keys.get(i).asString()).get("tgl_update").asString() );
            args.put("jam_update",  nson.get(keys.get(i).asString()).get("jam_update").asString() );
            args.put("tgl_kirim",  nson.get(keys.get(i).asString()).get("tgl_kirim").asString() );
            args.put("jam_kirim",  nson.get(keys.get(i).asString()).get("jam_kirim").asString() );
            args.put("id",  nson.get(keys.get(i).asString()).get("id").asString() );
            args.put("keterangan",  nson.get(keys.get(i).asString()).get("keterangan").asString() );

            String res = (InternetX.postHttpConnection( AppUtil.BASE_URL + "apicoll/bhc/?key="+AppController.getToken(), args));
            Nson out = Nson.readNson(res);
            if (out.get("status").asString().equalsIgnoreCase("true")){
                //ifsucses delete
                FileDB.get().delete(keys.get(i).asString());
            }else{
               // FileDB.get().delete(keys.get(i).asString());
                Utility.setSetting(this,  "pendb","1");
            }
        }
    }
    public static void startService(Activity activity){
        //sync
        Intent intent = new Intent(activity, GpsTrackerIntentService.class);
        activity.startService(intent);
    }
    public static void startServiceBHC(Activity activity){
        //sync
        Intent intent = new Intent(activity, GpsTrackerIntentService.class);
        intent.putExtra("smode","bhc");
        activity.startService(intent);
    }
}
