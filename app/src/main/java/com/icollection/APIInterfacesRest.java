package com.icollection;

/**
 * Created by user on 1/10/2018.
 */

import com.icollection.modelservice.Authentication;
import com.icollection.modelservice.BHC;
import com.icollection.modelservice.ChangeResult;
import com.icollection.modelservice.Order;
import com.icollection.modelservice.UpdateOrder;
import com.icollection.modelservice.VA;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by anupamchugh on 09/01/17.
 */

 public interface APIInterfacesRest {

    @FormUrlEncoded
    @POST("apicoll/cls9/")
    Call getClosing(@Field("imei") String imei, @Field("key") String key, @Field("username") String username);

    /*@Headers("User-Agent : Mozilla/5.0 (Android 4.4; Mobile; rv:41.0; Nikita V3) Gecko/41.0 Firefox/41.0"
    )*/
    @FormUrlEncoded
    @POST("apicoll/login/")
    Call<Authentication> getAuthentication(@Field("imei") String imei, @Field("username") String username, @Field("password") String password, @Field("versi") String versi);

    @FormUrlEncoded
    @POST("apicoll/ubahpas/")
    Call<ChangeResult> getChangePassword(@Field("imei") String imei, @Field("key") String key, @Field("username") String username, @Field("password1") String password, @Field("password2") String password2);

    @FormUrlEncoded
    @POST("apicoll/nova/")
    Call<String>  getVA(@Field("imei") String imei, @Field("key") String key, @Field("ao_tagih") String ao_tagih );


    @FormUrlEncoded
    @POST("apicoll/novas/")
    Call<String>  getVAs(@Field("imei") String imei, @Field("key") String key, @Field("nopsb") String nopsb );



    @GET("apicoll/getorder/")
    Call<Order> getOrder(@Query("imei") String imei, @Query("key") String key, @Query("status") String filter, @Query("field") String field, @Query("start") int start, @Query("limit") String limit,@Query("user") String user,@Query("nopsb") String nopsb, @Query("versi") String versi);

   @FormUrlEncoded
    @POST("apicoll/bhc_para/")
    Call<BHC> getBHC(@Field("imei") String imei, @Query("key") String key);

    @Multipart
   @POST("apicoll/updateb/")
   Call<UpdateOrder> updateDataOld(
            @Part("imei") RequestBody imei ,
           @Part("key") RequestBody key ,
           @Part("id") RequestBody id ,
           @Part("no_psb") RequestBody no_psb ,
           @Part("nama") RequestBody nama ,
           @Part("alamat1") RequestBody alamat1 ,
           @Part("alamat2") RequestBody alamat2 ,
           @Part("kodepos") RequestBody kodepos ,
           @Part("kota") RequestBody kota ,
           @Part("area") RequestBody area ,
           @Part("ke_awal") RequestBody ke_awal ,
           @Part("ke_akhir") RequestBody ke_akhir ,
           @Part("tgl_jtempo1") RequestBody tgl_jtempo1 ,
           @Part("tgl_jtempo2") RequestBody tgl_jtempo2 ,
           @Part("kode_ao") RequestBody kode_ao ,
           @Part("nama_ao") RequestBody nama_ao ,
           @Part("jml_angsuran") RequestBody jml_angsuran ,
           @Part("jml_denda") RequestBody jml_denda ,
           @Part("jml_bayar_tagih") RequestBody jml_bayar_tagih ,
           @Part("jml_tot_terbayar") RequestBody jml_tot_terbayar ,
           @Part("tgl_bayar") RequestBody tgl_bayar ,
           @Part("jam_bayar") RequestBody jam_bayar ,
           @Part("longitude") RequestBody longitude ,
           @Part("langitude") RequestBody langitude ,
           @Part("discount") RequestBody discount ,
           @Part("no_kwitansi") RequestBody no_kwitansi ,
           @Part("jenis") RequestBody jenis ,
           @Part("status") RequestBody status ,
           @Part("print_status") RequestBody print_status ,
           @Part("reprint_status") RequestBody reprint_status ,
           @Part("sisa_angsuran") RequestBody sisa_angsuran ,
           @Part("jumlah_sisa") RequestBody jumlah_sisa ,
           @Part("kecamatan") RequestBody kecamatan ,
           @Part("kelurahan") RequestBody kelurahan ,
           @Part("jml_sisa") RequestBody jml_sisa ,
           @Part("que") RequestBody que ,
           @Part("janji_bayar") RequestBody janji_bayar ,
           @Part("alasan") RequestBody alasan ,
           @Part("up_data") RequestBody up_data ,
           @Part("download_pertanyaan") RequestBody download_pertanyaan ,
           @Part("status_bayar") RequestBody status_bayar ,
           @Part("nopol") RequestBody nopol ,
           @Part("telepon") RequestBody telepon ,
           @Part("posko") RequestBody posko ,
           @Part("dendasisa") RequestBody dendasisa ,
           @Part("angsisa") RequestBody angsisa,
       //    @Part("photo_bayar\"; filename=\"image.jpeg\"") RequestBody photo_bayar,
           @Part MultipartBody.Part photo_bayar,
           @Part("type_kendaraan") RequestBody typeKendaraan,
           @Part("byr_denda") RequestBody byrDenda,
           @Part("byr_sisa") RequestBody byrSisa,
           @Part("Tenor") RequestBody Tenor,
           @Part("jumlahangsuran") RequestBody jumlahangsuran,
           @Part("jml_angsuran_ke") RequestBody jml_angsuran_ke,

           @Part("photo_janji_bayar") RequestBody photo_janji_bayar,
           @Part("photo1") RequestBody photo1,
           @Part("photo2") RequestBody photo2,

           @Part("alasan1") RequestBody alasan1,
           @Part("isi_alasan1") RequestBody isi_alasan1,
           @Part("informasi") RequestBody informasi,
           @Part("isi_informasi") RequestBody isi_informasi,


           @Part("que1") RequestBody que1,
           @Part("que2") RequestBody que2,
           @Part("que3") RequestBody que3,

           @Part("data1") RequestBody data1,
           @Part("data2") RequestBody data2,
           @Part("data3") RequestBody data3,

           @Part("text1") RequestBody text1,
           @Part("text2") RequestBody text2,
           @Part("text3") RequestBody text3,
           @Part MultipartBody.Part photo_bukti
           );



    @Multipart
    @POST("apicoll/update4/")
    Call<UpdateOrder> updateData(
            @Part("imei") RequestBody imei ,
            @Part("key") RequestBody key ,
            @Part("id") RequestBody id ,
            @Part("no_psb") RequestBody no_psb ,
            @Part("nama") RequestBody nama ,
            @Part("alamat1") RequestBody alamat1 ,
            @Part("alamat2") RequestBody alamat2 ,
            @Part("kodepos") RequestBody kodepos ,
            @Part("kota") RequestBody kota ,
            @Part("area") RequestBody area ,
            @Part("ke_awal") RequestBody ke_awal ,
            @Part("ke_akhir") RequestBody ke_akhir ,
            @Part("tgl_jtempo1") RequestBody tgl_jtempo1 ,
            @Part("tgl_jtempo2") RequestBody tgl_jtempo2 ,
            @Part("kode_ao") RequestBody kode_ao ,
            @Part("nama_ao") RequestBody nama_ao ,
            @Part("jml_angsuran") RequestBody jml_angsuran ,
            @Part("jml_denda") RequestBody jml_denda ,
            @Part("jml_bayar_tagih") RequestBody jml_bayar_tagih ,
            @Part("jml_tot_terbayar") RequestBody jml_tot_terbayar ,
            @Part("tgl_bayar") RequestBody tgl_bayar ,
            @Part("jam_bayar") RequestBody jam_bayar ,
            @Part("longitude") RequestBody longitude ,
            @Part("langitude") RequestBody langitude ,
            @Part("discount") RequestBody discount ,
            @Part("no_kwitansi") RequestBody no_kwitansi ,
            @Part("jenis") RequestBody jenis ,
            @Part("status") RequestBody status ,
            @Part("print_status") RequestBody print_status ,
            @Part("reprint_status") RequestBody reprint_status ,
            @Part("sisa_angsuran") RequestBody sisa_angsuran ,
            @Part("jumlah_sisa") RequestBody jumlah_sisa ,
            @Part("kecamatan") RequestBody kecamatan ,
            @Part("kelurahan") RequestBody kelurahan ,
            @Part("jml_sisa") RequestBody jml_sisa ,
            @Part("que") RequestBody que ,
            @Part("janji_bayar") RequestBody janji_bayar ,
            @Part("alasan") RequestBody alasan ,
            @Part("up_data") RequestBody up_data ,
            @Part("download_pertanyaan") RequestBody download_pertanyaan ,
            @Part("status_bayar") RequestBody status_bayar ,
            @Part("nopol") RequestBody nopol ,
            @Part("telepon") RequestBody telepon ,
            @Part("posko") RequestBody posko ,
            @Part("dendasisa") RequestBody dendasisa ,
            @Part("angsisa") RequestBody angsisa,
            //    @Part("photo_bayar\"; filename=\"image.jpeg\"") RequestBody photo_bayar,
            @Part MultipartBody.Part photo_bayar,
            @Part("type_kendaraan") RequestBody typeKendaraan,
            @Part("byr_denda") RequestBody byrDenda,
            @Part("byr_sisa") RequestBody byrSisa,
            @Part("Tenor") RequestBody Tenor,
            @Part("jumlahangsuran") RequestBody jumlahangsuran,
            @Part("jml_angsuran_ke") RequestBody jml_angsuran_ke,

            @Part("photo_janji_bayar") RequestBody photo_janji_bayar,
            @Part("photo1") RequestBody photo1,
            @Part("photo2") RequestBody photo2,

            @Part("alasan1") RequestBody alasan1,
            @Part("isi_alasan1") RequestBody isi_alasan1,
            @Part("informasi") RequestBody informasi,
            @Part("isi_informasi") RequestBody isi_informasi,


            @Part("que1") RequestBody que1,
            @Part("que2") RequestBody que2,
            @Part("que3") RequestBody que3,

            @Part("data1") RequestBody data1,
            @Part("data2") RequestBody data2,
            @Part("data3") RequestBody data3,

            @Part("text1") RequestBody text1,
            @Part("text2") RequestBody text2,
            @Part("text3") RequestBody text3,
            @Part MultipartBody.Part photo_bukti,
            @Part MultipartBody.Part photo_tarik1,
            @Part MultipartBody.Part photo_tarik2
    );
}

