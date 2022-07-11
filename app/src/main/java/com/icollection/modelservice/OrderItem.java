
package com.icollection.modelservice;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.icollection.AppController;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppController.class)
public class OrderItem extends BaseModel implements Serializable, Parcelable
{

    @Column
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private String id;
    @Column
    @SerializedName("no_psb")
    @Expose
    private String noPsb;
    @Column
    @SerializedName("nama")
    @Expose
    private String nama;
    @Column
    @SerializedName("alamat1")
    @Expose
    private String alamat1;
    @Column
    @SerializedName("alamat2")
    @Expose
    private String alamat2;
    @Column
    @SerializedName("kodepos")
    @Expose
    private String kodepos;
    @Column
    @SerializedName("kota")
    @Expose
    private String kota;
    @Column
    @SerializedName("area")
    @Expose
    private String area;
    @Column
    @SerializedName("ke_awal")
    @Expose
    private String keAwal;
    @Column
    @SerializedName("ke_akhir")
    @Expose
    private String keAkhir;
    @Column
    @SerializedName("tgl_jtempo1")
    @Expose
    private String tglJtempo1;
    @Column
    @SerializedName("tgl_jtempo2")
    @Expose
    private String tglJtempo2;
    @Column
    @SerializedName("kode_ao")
    @Expose
    private String kodeAo;
    @Column
    @SerializedName("nama_ao")
    @Expose
    private String namaAo;
    @Column
    @SerializedName("jml_angsuran")
    @Expose
    private String jmlAngsuran;
    @Column
    @SerializedName("jml_denda")
    @Expose
    private String jmlDenda;
    @Column
    @SerializedName("jml_bayar_tagih")
    @Expose
    private String jmlBayarTagih;
    @Column
    @SerializedName("jml_tot_terbayar")
    @Expose
    private String jmlTotTerbayar;
    @Column
    @SerializedName("tgl_bayar")
    @Expose
    private String tglBayar;
    @Column
    @SerializedName("jam_bayar")
    @Expose
    private String jamBayar;
    @Column
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @Column
    @SerializedName("langitude")
    @Expose
    private String langitude;
    @Column
    @SerializedName("discount")
    @Expose
    private String discount;
    @Column
    @SerializedName("no_kwitansi")
    @Expose
    private String noKwitansi;
    @Column
    @SerializedName("jenis")
    @Expose
    private String jenis;
    @Column
    @SerializedName("status")
    @Expose
    private String status;
    @Column
    @SerializedName("print_status")
    @Expose
    private String printStatus;
    @Column
    @SerializedName("reprint_status")
    @Expose
    private String reprintStatus;
    @Column
    @SerializedName("sisa_angsuran")
    @Expose
    private String sisaAngsuran;
    @Column
    @SerializedName("jumlah_sisa")
    @Expose
    private String jumlahSisa;
    @Column
    @SerializedName("kecamatan")
    @Expose
    private String kecamatan;
    @Column
    @SerializedName("kelurahan")
    @Expose
    private String kelurahan;
    @Column
    @SerializedName("jml_sisa")
    @Expose
    private String jmlSisa;
    @Column
    @SerializedName("que")
    @Expose
    private String que;
    @Column
    @SerializedName("janji_bayar")
    @Expose
    private String janjiBayar;
    @Column
    @SerializedName("alasan")
    @Expose
    private String alasan;
    @Column
    @SerializedName("up_data")
    @Expose
    private String upData;
    @Column
    @SerializedName("download_pertanyaan")
    @Expose
    private String downloadPertanyaan;
    @Column
    @SerializedName("status_bayar")
    @Expose
    private String statusBayar;
    @Column
    @SerializedName("nopol")
    @Expose
    private String nopol;
    @Column
    @SerializedName("telepon")
    @Expose
    private String telepon;
    @Column
    @SerializedName("posko")
    @Expose
    private String posko;
    @Column
    @SerializedName("dendasisa")
    @Expose
    private String dendasisa;
    @Column
    @SerializedName("angsisa")
    @Expose
    private String angsisa;
    @Column
    @SerializedName("photo_bayar")
    @Expose
    private String photoBayar;
    @Column
    @SerializedName("photo_janji_bayar")
    @Expose
    private String photoJanjiBayar;
    @Column
    @SerializedName("type_kendaraan")
    @Expose
    private String typeKendaraan;
    @Column
    @SerializedName("byr_denda")
    @Expose
    private String byrDenda;
    @Column
    @SerializedName("byr_sisa")
    @Expose
    private String byrSisa;
    @Column
    @SerializedName("Tenor")
    @Expose
    private String Tenor;
    @Column
    @SerializedName("jumlahangsuran")
    @Expose
    private String jumlahangsuran;
    @Column
    @SerializedName("jml_angsuran_ke")
    @Expose
    private String jml_angsuran_ke;
    @Column
    @SerializedName("closing")
    @Expose
    private String closing;
    @Column
    @SerializedName("alasan1")
    @Expose
    private String alasan1;
    @Column
    @SerializedName("isi_alasan1")
    @Expose
    private String isi_alasan1;
    @Column
    @SerializedName("informasi")
    @Expose
    private String informasi;

    @Column
    @SerializedName("text1")
    @Expose
    private String text1;
    @Column
    @SerializedName("text2")
    @Expose
    private String text2;
    @Column
    @SerializedName("text3")
    @Expose
    private String text3;
    @Column
    @SerializedName("data1")
    @Expose
    private String data1;
    @Column
    @SerializedName("data2")
    @Expose
    private String data2;
    @Column
    @SerializedName("tgl_kirim2")
    @Expose
    private String tgl_kirim2;

    public String getTgl_kirim2() {
        return tgl_kirim2;
    }

    public void setTgl_kirim2(String tgl_kirim2) {
        this.tgl_kirim2 = tgl_kirim2;
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public String getData1() {
        return data1;
    }

    public void setData1(String data1) {
        this.data1 = data1;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }

    public String getData3() {
        return data3;
    }

    public void setData3(String data3) {
        this.data3 = data3;
    }

    @Column
    @SerializedName("data3")
    @Expose
    private String data3;

    @Column
    @SerializedName("que1")
    @Expose
    private String que1;

    public String getQue1() {
        return que1;
    }

    public void setQue1(String que1) {
        this.que1 = que1;
    }

    public String getQue2() {
        return que2;
    }

    public void setQue2(String que2) {
        this.que2 = que2;
    }

    public String getQue3() {
        return que3;
    }

    public void setQue3(String que3) {
        this.que3 = que3;
    }

    @Column
    @SerializedName("que2")
    @Expose
    private String que2;
    @Column
    @SerializedName("que3")
    @Expose
    private String que3;

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }

    public String getAlasan1() {
        return alasan1;
    }

    public void setAlasan1(String alasan1) {
        this.alasan1 = alasan1;
    }

    public String getIsi_alasan1() {
        return isi_alasan1;
    }

    public void setIsi_alasan1(String isi_alasan1) {
        this.isi_alasan1 = isi_alasan1;
    }

    public String getInformasi() {
        return informasi;
    }

    public void setInformasi(String informasi) {
        this.informasi = informasi;
    }

    public String getIsi_informasi() {
        return isi_informasi;
    }

    public void setIsi_informasi(String isi_informasi) {
        this.isi_informasi = isi_informasi;
    }

    @Column
    @SerializedName("isi_informasi")
    @Expose
    private String isi_informasi;


    public String getByrDenda() {
        if (byrDenda == null){
            byrDenda="0";
        }
        return byrDenda;
    }

    public void setByrDenda(String byrDenda) {
        this.byrDenda = byrDenda;
    }

    public String getByrSisa() {
        if(byrSisa == null){
            byrSisa ="0";
        }

        return byrSisa;
    }

    public void setByrSisa(String byrSisa) {
        this.byrSisa = byrSisa;
    }



    public String getTenor() {
        return Tenor;
    }

    public void setTenor(String tenor) {
        Tenor = tenor;
    }

    public String getJumlahangsuran() {

        if(jumlahangsuran == null){
            jumlahangsuran ="0";
        }
        return jumlahangsuran;
    }

    public void setJumlahangsuran(String jumlahangsuran) {
        this.jumlahangsuran = jumlahangsuran;
    }

    public String getJml_angsuran_ke() {
        return jml_angsuran_ke;
    }

    public void setJml_angsuran_ke(String jml_angsuran_ke) {
        this.jml_angsuran_ke = jml_angsuran_ke;
    }




    public final static Parcelable.Creator<OrderItem> CREATOR = new Creator<OrderItem>() {


        @SuppressWarnings({
                "unchecked"
        })
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        public OrderItem[] newArray(int size) {
            return (new OrderItem[size]);
        }

    };

    private final static long serialVersionUID = 4590390406821574552L;

    protected OrderItem(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.noPsb = ((String) in.readValue((String.class.getClassLoader())));
        this.nama = ((String) in.readValue((String.class.getClassLoader())));
        this.alamat1 = ((String) in.readValue((String.class.getClassLoader())));
        this.alamat2 = ((String) in.readValue((String.class.getClassLoader())));
        this.kodepos = ((String) in.readValue((String.class.getClassLoader())));
        this.kota = ((String) in.readValue((String.class.getClassLoader())));
        this.area = ((String) in.readValue((String.class.getClassLoader())));
        this.keAwal = ((String) in.readValue((String.class.getClassLoader())));
        this.keAkhir = ((String) in.readValue((String.class.getClassLoader())));
        this.tglJtempo1 = ((String) in.readValue((String.class.getClassLoader())));
        this.tglJtempo2 = ((String) in.readValue((String.class.getClassLoader())));
        this.kodeAo = ((String) in.readValue((String.class.getClassLoader())));
        this.namaAo = ((String) in.readValue((String.class.getClassLoader())));
        this.jmlAngsuran = ((String) in.readValue((String.class.getClassLoader())));
        this.jmlDenda = ((String) in.readValue((String.class.getClassLoader())));
        this.jmlBayarTagih = ((String) in.readValue((String.class.getClassLoader())));
        this.jmlTotTerbayar = ((String) in.readValue((String.class.getClassLoader())));
        this.tglBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.jamBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.longitude = ((String) in.readValue((String.class.getClassLoader())));
        this.langitude = ((String) in.readValue((String.class.getClassLoader())));
        this.discount = ((String) in.readValue((String.class.getClassLoader())));
        this.noKwitansi = ((String) in.readValue((String.class.getClassLoader())));
        this.jenis = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.printStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.reprintStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.sisaAngsuran = ((String) in.readValue((String.class.getClassLoader())));
        this.jumlahSisa = ((String) in.readValue((String.class.getClassLoader())));
        this.kecamatan = ((String) in.readValue((String.class.getClassLoader())));
        this.kelurahan = ((String) in.readValue((String.class.getClassLoader())));
        this.jmlSisa = ((String) in.
                readValue((String.class.getClassLoader())));
        this.que = ((String) in.readValue((String.class.getClassLoader())));
        this.janjiBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.alasan = ((String) in.readValue((String.class.getClassLoader())));
        this.upData = ((String) in.readValue((String.class.getClassLoader())));
        this.downloadPertanyaan = ((String) in.readValue((String.class.getClassLoader())));
        this.statusBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.nopol = ((String) in.readValue((String.class.getClassLoader())));
        this.telepon = ((String) in.readValue((String.class.getClassLoader())));
        this.posko = ((String) in.readValue((String.class.getClassLoader())));
        this.dendasisa = ((String) in.readValue((String.class.getClassLoader())));
        this.angsisa = ((String) in.readValue((String.class.getClassLoader())));
        this.photoBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.photoJanjiBayar = ((String) in.readValue((String.class.getClassLoader())));
        this.typeKendaraan = ((String) in.readValue((String.class.getClassLoader())));
        this.byrDenda = ((String) in.readValue((String.class.getClassLoader())));
        this.byrSisa = ((String) in.readValue((String.class.getClassLoader())));
        this.Tenor = ((String) in.readValue((String.class.getClassLoader())));
        this.jumlahangsuran = ((String) in.readValue((String.class.getClassLoader())));;
        this.jml_angsuran_ke = ((String) in.readValue((String.class.getClassLoader())));

        this.closing = ((String) in.readValue((String.class.getClassLoader())));
        this.alasan1 = ((String) in.readValue((String.class.getClassLoader())));
        this.isi_alasan1 = ((String) in.readValue((String.class.getClassLoader())));
        this.informasi = ((String) in.readValue((String.class.getClassLoader())));
        this.isi_informasi = ((String) in.readValue((String.class.getClassLoader())));



        this.que1 = ((String) in.readValue((String.class.getClassLoader())));
        this.que2 = ((String) in.readValue((String.class.getClassLoader())));
        this.que3 = ((String) in.readValue((String.class.getClassLoader())));

        this.data1 = ((String) in.readValue((String.class.getClassLoader())));
        this.data2 = ((String) in.readValue((String.class.getClassLoader())));
        this.data3 = ((String) in.readValue((String.class.getClassLoader())));

        this.text1 = ((String) in.readValue((String.class.getClassLoader())));
        this.text2 = ((String) in.readValue((String.class.getClassLoader())));
        this.text3 = ((String) in.readValue((String.class.getClassLoader())));

        this.tgl_kirim2 = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public OrderItem() {
    }

    /**
     *
     * @param namaAo
     * @param dendasisa
     * @param printStatus
     * @param keAkhir
     * @param noPsb
     * @param angsisa
     * @param jmlSisa
     * @param kota
     * @param alasan
     * @param keAwal
     * @param id
     * @param tglBayar
     * @param langitude
     * @param area
     * @param kodepos
     * @param tglJtempo1
     * @param alamat1
     * @param jmlTotTerbayar
     * @param janjiBayar
     * @param reprintStatus
     * @param jmlAngsuran
     * @param que
     * @param tglJtempo2
     * @param alamat2
     * @param longitude
     * @param nama
     * @param downloadPertanyaan
     * @param jmlBayarTagih
     * @param status
     * @param jumlahSisa
     * @param noKwitansi
     * @param kodeAo
     * @param jmlDenda
     * @param upData
     * @param kelurahan
     * @param nopol
     * @param discount
     * @param kecamatan
     * @param telepon
     * @param sisaAngsuran
     * @param posko
     * @param photoBayar
     * @param jenis
     * @param jamBayar
     * @param photoJanjiBayar
     * @param statusBayar
     * @param typeKendaraan
     * @param byrDenda
     * @param byrSisa
     */
    public OrderItem(String id, String noPsb, String nama, String alamat1, String alamat2, String kodepos, String kota, String area, String keAwal, String keAkhir, String tglJtempo1, String tglJtempo2, String kodeAo, String namaAo, String jmlAngsuran, String jmlDenda, String jmlBayarTagih, String jmlTotTerbayar, String tglBayar, String jamBayar, String longitude, String langitude, String discount, String noKwitansi, String jenis, String status, String printStatus, String reprintStatus, String sisaAngsuran, String jumlahSisa, String kecamatan, String kelurahan, String jmlSisa, String que, String janjiBayar, String alasan, String upData, String downloadPertanyaan, String statusBayar, String nopol, String telepon, String posko, String dendasisa, String angsisa, String photoBayar, String photoJanjiBayar,String typeKendaraan,String byrDenda, String byrSisa, String Tenor, String jumlahangsuran, String jml_angsuran_ke
        ,String closing, String alasan1, String isi_alasan1, String informasi, String isi_informasi

            ,String que1, String que2, String que3
            ,String data1, String data2, String data3
            ,String text1, String text2, String text3
            ,String tgl_kirim2
    ) {
        super();
        this.id = id;
        this.noPsb = noPsb;
        this.nama = nama;
        this.alamat1 = alamat1;
        this.alamat2 = alamat2;
        this.kodepos = kodepos;
        this.kota = kota;
        this.area = area;
        this.keAwal = keAwal;
        this.keAkhir = keAkhir;
        this.tglJtempo1 = tglJtempo1;
        this.tglJtempo2 = tglJtempo2;
        this.kodeAo = kodeAo;
        this.namaAo = namaAo;
        this.jmlAngsuran = jmlAngsuran;
        this.jmlDenda = jmlDenda;
        this.jmlBayarTagih = jmlBayarTagih;
        this.jmlTotTerbayar = jmlTotTerbayar;
        this.tglBayar = tglBayar;
        this.jamBayar = jamBayar;
        this.longitude = longitude;
        this.langitude = langitude;
        this.discount = discount;
        this.noKwitansi = noKwitansi;
        this.jenis = jenis;
        this.status = status;
        this.printStatus = printStatus;
        this.reprintStatus = reprintStatus;
        this.sisaAngsuran = sisaAngsuran;
        this.jumlahSisa = jumlahSisa;
        this.kecamatan = kecamatan;
        this.kelurahan = kelurahan;
        this.jmlSisa = jmlSisa;
        this.que = que;
        this.janjiBayar = janjiBayar;
        this.alasan = alasan;
        this.upData = upData;
        this.downloadPertanyaan = downloadPertanyaan;
        this.statusBayar = statusBayar;
        this.nopol = nopol;
        this.telepon = telepon;
        this.posko = posko;
        this.dendasisa = dendasisa;
        this.angsisa = angsisa;
        this.photoBayar = photoBayar;
        this.photoJanjiBayar = photoJanjiBayar;
        this.typeKendaraan = typeKendaraan;
        this.byrDenda = byrDenda;
        this.byrSisa = byrSisa;
        this.Tenor = Tenor;
        this.jumlahangsuran = jumlahangsuran;
        this.jml_angsuran_ke = jml_angsuran_ke;

        this.closing = closing;
        this.alasan1 = alasan1;
        this.isi_alasan1 = isi_alasan1;
        this.informasi = informasi;
        this.isi_informasi = isi_informasi;


        this.que1 = que1;
        this.que2 = que2;
        this.que3 = que3;

        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;

        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;

        this.tgl_kirim2 = tgl_kirim2;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OrderItem withId(String id) {
        this.id = id;
        return this;
    }

    public String getNoPsb() {
        return noPsb;
    }

    public void setNoPsb(String noPsb) {
        this.noPsb = noPsb;
    }

    public OrderItem withNoPsb(String noPsb) {
        this.noPsb = noPsb;
        return this;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public OrderItem withNama(String nama) {
        this.nama = nama;
        return this;
    }

    public String getAlamat1() {
        return alamat1;
    }

    public void setAlamat1(String alamat1) {
        this.alamat1 = alamat1;
    }

    public OrderItem withAlamat1(String alamat1) {
        this.alamat1 = alamat1;
        return this;
    }

    public String getAlamat2() {
        return alamat2;
    }

    public void setAlamat2(String alamat2) {
        this.alamat2 = alamat2;
    }

    public OrderItem withAlamat2(String alamat2) {
        this.alamat2 = alamat2;
        return this;
    }

    public String getKodepos() {
        return kodepos;
    }

    public void setKodepos(String kodepos) {
        this.kodepos = kodepos;
    }

    public OrderItem withKodepos(String kodepos) {
        this.kodepos = kodepos;
        return this;
    }

    public String getKota() {
        return kota;
    }

    public void setKota(String kota) {
        this.kota = kota;
    }

    public OrderItem withKota(String kota) {
        this.kota = kota;
        return this;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public OrderItem withArea(String area) {
        this.area = area;
        return this;
    }

    public String getKeAwal() {
        return keAwal;
    }

    public void setKeAwal(String keAwal) {
        this.keAwal = keAwal;
    }

    public OrderItem withKeAwal(String keAwal) {
        this.keAwal = keAwal;
        return this;
    }

    public String getKeAkhir() {
        return keAkhir;
    }

    public void setKeAkhir(String keAkhir) {
        this.keAkhir = keAkhir;
    }

    public OrderItem withKeAkhir(String keAkhir) {
        this.keAkhir = keAkhir;
        return this;
    }

    public String getTglJtempo1() {
        return tglJtempo1;
    }

    public void setTglJtempo1(String tglJtempo1) {
        this.tglJtempo1 = tglJtempo1;
    }

    public OrderItem withTglJtempo1(String tglJtempo1) {
        this.tglJtempo1 = tglJtempo1;
        return this;
    }

    public String getTglJtempo2() {
        return tglJtempo2;
    }

    public void setTglJtempo2(String tglJtempo2) {
        this.tglJtempo2 = tglJtempo2;
    }

    public OrderItem withTglJtempo2(String tglJtempo2) {
        this.tglJtempo2 = tglJtempo2;
        return this;
    }

    public String getKodeAo() {
        return kodeAo;
    }

    public void setKodeAo(String kodeAo) {
        this.kodeAo = kodeAo;
    }

    public OrderItem withKodeAo(String kodeAo) {
        this.kodeAo = kodeAo;
        return this;
    }

    public String getNamaAo() {
        return namaAo;
    }

    public void setNamaAo(String namaAo) {
        this.namaAo = namaAo;
    }

    public OrderItem withNamaAo(String namaAo) {
        this.namaAo = namaAo;
        return this;
    }

    public String getJmlAngsuran() {
        return jmlAngsuran;
    }

    public void setJmlAngsuran(String jmlAngsuran) {
        this.jmlAngsuran = jmlAngsuran;
    }

    public OrderItem withJmlAngsuran(String jmlAngsuran) {
        this.jmlAngsuran = jmlAngsuran;
        return this;
    }

    public String getJmlDenda() {
        if(jmlDenda == null){
            jmlDenda ="0";
        }
        return jmlDenda;
    }

    public void setJmlDenda(String jmlDenda) {
        this.jmlDenda = jmlDenda;
    }

    public OrderItem withJmlDenda(String jmlDenda) {
        this.jmlDenda = jmlDenda;
        return this;
    }

    public String getJmlBayarTagih() {
        if(jmlBayarTagih == null){
            jmlBayarTagih ="0";
        }
        return jmlBayarTagih;
    }

    public void setJmlBayarTagih(String jmlBayarTagih) {
        this.jmlBayarTagih = jmlBayarTagih;
    }

    public OrderItem withJmlBayarTagih(String jmlBayarTagih) {
        this.jmlBayarTagih = jmlBayarTagih;
        return this;
    }

    public String getJmlTotTerbayar() {
        return jmlTotTerbayar;
    }

    public void setJmlTotTerbayar(String jmlTotTerbayar) {
        this.jmlTotTerbayar = jmlTotTerbayar;
    }

    public OrderItem withJmlTotTerbayar(String jmlTotTerbayar) {
        this.jmlTotTerbayar = jmlTotTerbayar;
        return this;
    }

    public String getTglBayar() {
        return tglBayar;
    }

    public void setTglBayar(String tglBayar) {
        this.tglBayar = tglBayar;
    }

    public OrderItem withTglBayar(String tglBayar) {
        this.tglBayar = tglBayar;
        return this;
    }

    public String getJamBayar() {
        return jamBayar;
    }

    public void setJamBayar(String jamBayar) {
        this.jamBayar = jamBayar;
    }

    public OrderItem withJamBayar(String jamBayar) {
        this.jamBayar = jamBayar;
        return this;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public OrderItem withLongitude(String longitude) {
        this.longitude = longitude;
        return this;
    }

    public String getLangitude() {
        return langitude;
    }

    public void setLangitude(String langitude) {
        this.langitude = langitude;
    }

    public OrderItem withLangitude(String langitude) {
        this.langitude = langitude;
        return this;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public OrderItem withDiscount(String discount) {
        this.discount = discount;
        return this;
    }

    public String getNoKwitansi() {
        return noKwitansi;
    }

    public void setNoKwitansi(String noKwitansi) {
        this.noKwitansi = noKwitansi;
    }

    public OrderItem withNoKwitansi(String noKwitansi) {
        this.noKwitansi = noKwitansi;
        return this;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public OrderItem withJenis(String jenis) {
        this.jenis = jenis;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderItem withStatus(String status) {
        this.status = status;
        return this;
    }

    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public OrderItem withPrintStatus(String printStatus) {
        this.printStatus = printStatus;
        return this;
    }

    public String getReprintStatus() {
        return reprintStatus;
    }

    public void setReprintStatus(String reprintStatus) {
        this.reprintStatus = reprintStatus;
    }

    public OrderItem withReprintStatus(String reprintStatus) {
        this.reprintStatus = reprintStatus;
        return this;
    }

    public String getSisaAngsuran() {
        if(sisaAngsuran == null){
            sisaAngsuran ="0";
        }
        return sisaAngsuran;
    }

    public void setSisaAngsuran(String sisaAngsuran) {
        this.sisaAngsuran = sisaAngsuran;
    }

    public OrderItem withSisaAngsuran(String sisaAngsuran) {
        this.sisaAngsuran = sisaAngsuran;
        return this;
    }

    public String getJumlahSisa() {
        if(jumlahSisa == null){
            jumlahSisa ="0";
        }
        return jumlahSisa;
    }

    public void setJumlahSisa(String jumlahSisa) {
        this.jumlahSisa = jumlahSisa;
    }

    public OrderItem withJumlahSisa(String jumlahSisa) {
        this.jumlahSisa = jumlahSisa;
        return this;
    }

    public String getKecamatan() {
        return kecamatan;
    }

    public void setKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
    }

    public OrderItem withKecamatan(String kecamatan) {
        this.kecamatan = kecamatan;
        return this;
    }

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }

    public OrderItem withKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
        return this;
    }

    public String getJmlSisa() {
        if(jmlSisa == null){
            jmlSisa ="0";
        }
        return jmlSisa;
    }

    public void setJmlSisa(String jmlSisa) {
        this.jmlSisa = jmlSisa;
    }

    public OrderItem withJmlSisa(String jmlSisa) {
        this.jmlSisa = jmlSisa;
        return this;
    }

    public String getQue() {
        return que;
    }

    public void setQue(String que) {
        this.que = que;
    }

    public OrderItem withQue(String que) {
        this.que = que;
        return this;
    }

    public String getJanjiBayar() {
        if (janjiBayar ==null){
            janjiBayar="";
        }
        return janjiBayar;
    }

    public void setJanjiBayar(String janjiBayar) {
        this.janjiBayar = janjiBayar;
    }

    public OrderItem withJanjiBayar(String janjiBayar) {
        this.janjiBayar = janjiBayar;
        return this;
    }

    public String getAlasan() {
        return alasan;
    }

    public void setAlasan(String alasan) {
        this.alasan = alasan;
    }

    public OrderItem withAlasan(String alasan) {
        this.alasan = alasan;
        return this;
    }

    public String getUpData() {
        return upData;
    }

    public void setUpData(String upData) {
        this.upData = upData;
    }

    public OrderItem withUpData(String upData) {
        this.upData = upData;
        return this;
    }

    public String getDownloadPertanyaan() {
        return downloadPertanyaan;
    }

    public void setDownloadPertanyaan(String downloadPertanyaan) {
        this.downloadPertanyaan = downloadPertanyaan;
    }

    public OrderItem withDownloadPertanyaan(String downloadPertanyaan) {
        this.downloadPertanyaan = downloadPertanyaan;
        return this;
    }

    public String getStatusBayar() {
        return statusBayar;
    }

    public void setStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
    }

    String statusBayarX = null;
    public String getStatusBayarX() {
        return statusBayarX==null?getStatusBayar():statusBayarX;
    }

    public void setStatusBayarX() {
        if (statusBayarX == null){
            this.statusBayarX = getStatusBayar();
        }
    }

    public OrderItem withStatusBayar(String statusBayar) {
        this.statusBayar = statusBayar;
        return this;
    }

    public String getNopol() {
        return nopol;
    }

    public void setNopol(String nopol) {
        this.nopol = nopol;
    }

    public OrderItem withNopol(String nopol) {
        this.nopol = nopol;
        return this;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public OrderItem withTelepon(String telepon) {
        this.telepon = telepon;
        return this;
    }

    public String getPosko() {
        return posko;
    }

    public void setPosko(String posko) {
        this.posko = posko;
    }

    public OrderItem withPosko(String posko) {
        this.posko = posko;
        return this;
    }

    public String getDendasisa() {
        if(dendasisa == null){
            dendasisa ="0";
        }

        return dendasisa;
    }

    public void setDendasisa(String dendasisa) {
        this.dendasisa = dendasisa;
    }

    public OrderItem withDendasisa(String dendasisa) {
        this.dendasisa = dendasisa;
        return this;
    }

    public String getAngsisa() {

        if(angsisa == null){
            angsisa ="0";
        }

        return angsisa;
    }

    public void setAngsisa(String angsisa) {
        this.angsisa = angsisa;
    }

    public OrderItem withAngsisa(String angsisa) {
        this.angsisa = angsisa;
        return this;
    }

    public String getPhotoBayar() {
        return photoBayar;
    }

    public void setPhotoBayar(String photoBayar) {
        this.photoBayar = photoBayar;
    }

    public OrderItem withPhotoBayar(String photoBayar) {
        this.photoBayar = photoBayar;
        return this;
    }
    public String getTypeKendaraan() {
        return typeKendaraan;
    }

    public void setTypeKendaraan(String typeKendaraan) {
        this.typeKendaraan = typeKendaraan;
    }

    public String getPhotoJanjiBayar() {
        return photoJanjiBayar;
    }

    public void setPhotoJanjiBayar(String photoJanjiBayar) {
        this.photoJanjiBayar = photoJanjiBayar;
    }

    public OrderItem withPhotoJanjiBayar(String photoJanjiBayar) {
        this.photoJanjiBayar = photoJanjiBayar;
        return this;
    }

    public OrderItem withTypeKendaraan(String typeKendaraan) {
        this.typeKendaraan = typeKendaraan;
        return this;
    }
    public boolean contains(String query){
        StringBuilder dest = new StringBuilder();
        dest.append(id);
        dest.append(noPsb);
        dest.append(nama);
        dest.append(alamat1);
        dest.append(alamat2);
        dest.append(kodepos);
        dest.append(kota);
        dest.append(area);
        dest.append(keAwal);
        dest.append(keAkhir);
        dest.append(tglJtempo1);
        dest.append(tglJtempo2);
        dest.append(kodeAo);
        dest.append(namaAo);
        dest.append(jmlAngsuran);
        dest.append(jmlDenda);
        dest.append(jmlBayarTagih);
        dest.append(jmlTotTerbayar);
        dest.append(tglBayar);
        dest.append(jamBayar);
        dest.append(longitude);
        dest.append(langitude);
        dest.append(discount);
        dest.append(noKwitansi);
        dest.append(jenis);
        dest.append(status);
        dest.append(printStatus);
        dest.append(reprintStatus);
        dest.append(sisaAngsuran);
        dest.append(jumlahSisa);
        dest.append(kecamatan);
        dest.append(kelurahan);
        dest.append(jmlSisa);
        dest.append(que);
        dest.append(janjiBayar);
        dest.append(alasan);
        dest.append(upData);
        dest.append(downloadPertanyaan);
        dest.append(statusBayar);
        dest.append(nopol);
        dest.append(telepon);
        dest.append(posko);
        dest.append(dendasisa);
        dest.append(angsisa);
        dest.append(photoBayar);
        dest.append(photoJanjiBayar);
        dest.append(typeKendaraan);
        dest.append(byrDenda);
        dest.append(byrSisa);
        dest.append(Tenor);
        dest.append(jumlahangsuran);
        dest.append(jml_angsuran_ke);


        return dest.toString().toLowerCase().contains(query);
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(noPsb);
        dest.writeValue(nama);
        dest.writeValue(alamat1);
        dest.writeValue(alamat2);
        dest.writeValue(kodepos);
        dest.writeValue(kota);
        dest.writeValue(area);
        dest.writeValue(keAwal);
        dest.writeValue(keAkhir);
        dest.writeValue(tglJtempo1);
        dest.writeValue(tglJtempo2);
        dest.writeValue(kodeAo);
        dest.writeValue(namaAo);
        dest.writeValue(jmlAngsuran);
        dest.writeValue(jmlDenda);
        dest.writeValue(jmlBayarTagih);
        dest.writeValue(jmlTotTerbayar);
        dest.writeValue(tglBayar);
        dest.writeValue(jamBayar);
        dest.writeValue(longitude);
        dest.writeValue(langitude);
        dest.writeValue(discount);
        dest.writeValue(noKwitansi);
        dest.writeValue(jenis);
        dest.writeValue(status);
        dest.writeValue(printStatus);
        dest.writeValue(reprintStatus);
        dest.writeValue(sisaAngsuran);
        dest.writeValue(jumlahSisa);
        dest.writeValue(kecamatan);
        dest.writeValue(kelurahan);
        dest.writeValue(jmlSisa);
        dest.writeValue(que);
        dest.writeValue(janjiBayar);
        dest.writeValue(alasan);
        dest.writeValue(upData);
        dest.writeValue(downloadPertanyaan);
        dest.writeValue(statusBayar);
        dest.writeValue(nopol);
        dest.writeValue(telepon);
        dest.writeValue(posko);
        dest.writeValue(dendasisa);
        dest.writeValue(angsisa);
        dest.writeValue(photoBayar);
        dest.writeValue(photoJanjiBayar);
        dest.writeValue(typeKendaraan);
        dest.writeValue(byrDenda);
        dest.writeValue(byrSisa);
        dest.writeValue(Tenor);
        dest.writeValue(jumlahangsuran);
        dest.writeValue(jml_angsuran_ke);

        dest.writeValue(closing);
        dest.writeValue(alasan1);
        dest.writeValue(isi_alasan1);
        dest.writeValue(informasi);
        dest.writeValue(isi_informasi);

        dest.writeValue(que1);
        dest.writeValue(que2);
        dest.writeValue(que3);

        dest.writeValue(data1);
        dest.writeValue(data2);
        dest.writeValue(data3);

        dest.writeValue(text1);
        dest.writeValue(text2);
        dest.writeValue(text3);

        dest.writeValue(tgl_kirim2);

    }

    public String xPrint(){
        StringBuilder dest = new StringBuilder();
        dest.append("\n").append("id: ").append(id);
        dest.append("\n").append("noPsb: ").append(noPsb);
        dest.append("\n").append("nama: ").append(nama);
        dest.append("\n").append("alamat1: ").append(alamat1);
        dest.append("\n").append("alamat2: ").append(alamat2);
        dest.append("\n").append("kodepos: ").append(kodepos);
        dest.append("\n").append("kota: ").append(kota);
        dest.append("\n").append("area: ").append(area);
        dest.append("\n").append("keAwal: ").append(keAwal);
        dest.append("\n").append("keAkhir: ").append(keAkhir);
        dest.append("\n").append("tglJtempo1: ").append(tglJtempo1);
        dest.append("\n").append("tglJtempo2: ").append(tglJtempo2);
        dest.append("\n").append("kodeAo: ").append(kodeAo);
        dest.append("\n").append("namaAo: ").append(namaAo);
        dest.append("\n").append("jmlAngsuran: ").append(jmlAngsuran);
        dest.append("\n").append("jmlDenda: ").append(jmlDenda);
        dest.append("\n").append("jmlBayarTagih: ").append(jmlBayarTagih);
        dest.append("\n").append("jmlTotTerbayar: ").append(jmlTotTerbayar);
        dest.append("\n").append("tglBayar: ").append(tglBayar);
        dest.append("\n").append("jamBayar: ").append(jamBayar);
        dest.append("\n").append("longitude: ").append(longitude);
        dest.append("\n").append("langitude: ").append(langitude);
        dest.append("\n").append("discount: ").append(discount);
        dest.append("\n").append("noKwitansi: ").append(noKwitansi);
        dest.append("\n").append("jenis: ").append(jenis);
        dest.append("\n").append("status: ").append(status);
        dest.append("\n").append("printStatus: ").append(printStatus);
        dest.append("\n").append("reprintStatus: ").append(reprintStatus);
        dest.append("\n").append("sisaAngsuran: ").append(sisaAngsuran);
        dest.append("\n").append("jumlahSisa: ").append(jumlahSisa);
        dest.append("\n").append("kecamatan: ").append(kecamatan);
        dest.append("\n").append("kelurahan: ").append(kelurahan);
        dest.append("\n").append("jmlSisa: ").append(jmlSisa);
        dest.append("\n").append("que: ").append(que);
        dest.append("\n").append("janjiBayar: ").append(janjiBayar);
        dest.append("\n").append("alasan: ").append(alasan);
        dest.append("\n").append("upData: ").append(upData);
        dest.append("\n").append("downloadPertanyaan: ").append(downloadPertanyaan);
        dest.append("\n").append("statusBayar: ").append(statusBayar);
        dest.append("\n").append("nopol: ").append(nopol);
        dest.append("\n").append("telepon: ").append(telepon);
        dest.append("\n").append("posko: ").append(posko);
        dest.append("\n").append("dendasisa: ").append(dendasisa);
        dest.append("\n").append("angsisa: ").append(angsisa);
        dest.append("\n").append("photoBayar: ").append(photoBayar);
        dest.append("\n").append("photoJanjiBayar: ").append(photoJanjiBayar);
        dest.append("\n").append("typeKendaraan: ").append(typeKendaraan);
        dest.append("\n").append("byrDenda: ").append(byrDenda);
        dest.append("\n").append("byrSisa: ").append(byrSisa);
        dest.append("\n").append("Tenor: ").append(Tenor);
        dest.append("\n").append("jumlahangsuran: ").append(jumlahangsuran);
        dest.append("\n").append("jml_angsuran_ke: ").append(jml_angsuran_ke);
        return dest.toString();
    }
    private String trim(String s){
        if (s!=null){
            return s.trim();
        }else{
            return "";
        }
    }
    public void trimx(){
        id = trim(id);
        noPsb = trim(noPsb);
        nama = trim(nama);
        alamat1 = trim(alamat1);
        alamat2 = trim(alamat2);
        kodepos  = trim(kodepos);
        kota  = trim(kota);
        area  = trim(area);
        keAwal = trim(keAwal);
        keAkhir = trim(keAkhir);
        tglJtempo1  = trim(tglJtempo1);
        tglJtempo2 = trim(tglJtempo2);
        kodeAo = trim(kodeAo);
        namaAo = trim(namaAo);
        jmlAngsuran  = trim(jmlAngsuran);
        jmlDenda = trim(jmlDenda);
        jmlBayarTagih = trim(jmlBayarTagih);
        jmlTotTerbayar = trim(jmlTotTerbayar);
        tglBayar  = trim(tglBayar);
        jamBayar = trim(jamBayar);
        longitude  = trim(longitude);
        langitude   = trim(langitude);
        discount = trim(discount);
        noKwitansi= trim(noKwitansi);
        jenis = trim(jenis);
        status = trim(status);
        printStatus= trim(printStatus);
        reprintStatus= trim(reprintStatus);
        sisaAngsuran = trim(sisaAngsuran);
        jumlahSisa= trim(jumlahSisa);
        kecamatan = trim(kecamatan);
        kelurahan= trim(kelurahan);
        jmlSisa= trim(jmlSisa);
        que= trim(que);
        janjiBayar= trim(janjiBayar);
        alasan= trim(alasan);
        upData= trim(upData);
        downloadPertanyaan= trim(downloadPertanyaan);
        statusBayar = trim(statusBayar);
        nopol = trim(nopol);
        telepon = trim(telepon);
        posko = trim(posko);
        dendasisa = trim(dendasisa);
        angsisa= trim(angsisa);
        photoBayar = trim(photoBayar);
        photoJanjiBayar = trim(photoJanjiBayar);
        typeKendaraan = trim(typeKendaraan);
        byrDenda= trim(byrDenda);
        byrSisa= trim(byrSisa);
        Tenor= trim(Tenor);
        jumlahangsuran= trim(jumlahangsuran);
        jml_angsuran_ke= trim(jml_angsuran_ke);

        closing= trim(closing);
        alasan1= trim(alasan1);
        isi_alasan1= trim(isi_alasan1);
        informasi= trim(informasi);
        isi_informasi= trim(isi_informasi);


        text1= trim(text1);
        text2= trim(text2);
        text3= trim(text3);

        data1= trim(data1);
        data2= trim(data2);
        data3= trim(data3);


        que1= trim(que1);
        que2= trim(que2);
        que3= trim(que3);

        tgl_kirim2 = trim(tgl_kirim2);
    }
    public int describeContents() {
        return  0;
    }

}
