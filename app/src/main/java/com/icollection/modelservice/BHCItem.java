
package com.icollection.modelservice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.icollection.AppController;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;


public class BHCItem extends BaseModel implements Serializable, Parcelable
{

    @Column
    @PrimaryKey
    @SerializedName("Field_id")
    @Expose
    private String id;
    @Column
    @SerializedName("Field_id")
    @Expose
    private String field_id;
    @Column
    @SerializedName("Seq")
    @Expose
    private String seq;
    @Column
    @SerializedName("Name_id")
    @Expose
    private String name_id;
    @Column
    @SerializedName("Tanya_detail")
    @Expose
    private String tanya_detail;
    @Column
    @SerializedName("Type_Tanya")
    @Expose
    private String type_Tanya;
    @Column
    @SerializedName("Tanya")
    @Expose
    private String tanya;
    @Column
    @SerializedName("Terlihat")
    @Expose
    private String terlihat;
    @Column
    @SerializedName("Isian")
    @Expose
    private String isian;





    public final static Creator<BHCItem> CREATOR = new Creator<BHCItem>() {


        @SuppressWarnings({
                "unchecked"
        })
        public BHCItem createFromParcel(Parcel in) {
            return new BHCItem(in);
        }

        public BHCItem[] newArray(int size) {
            return (new BHCItem[size]);
        }

    }
            ;
    private final static long serialVersionUID = 4590390406821574552L;

    protected BHCItem(Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.field_id = ((String) in.readValue((String.class.getClassLoader())));
        this.seq = ((String) in.readValue((String.class.getClassLoader())));
        this.name_id = ((String) in.readValue((String.class.getClassLoader())));
        this.tanya_detail = ((String) in.readValue((String.class.getClassLoader())));
        this.type_Tanya = ((String) in.readValue((String.class.getClassLoader())));
        this.tanya = ((String) in.readValue((String.class.getClassLoader())));
        this.terlihat = ((String) in.readValue((String.class.getClassLoader())));
        this.isian = ((String) in.readValue((String.class.getClassLoader())));


    }

    /**
     * No args constructor for use in serialization
     *
     */
    public BHCItem() {
    }


    public BHCItem(String id, String field_id, String seq, String name_id, String tanya_detail, String type_Tanya, String tanya, String terlihat, String isian  ) {
        super();
        this.id = id;
        this.field_id = field_id;
        this.seq = seq;
        this.name_id = name_id;
        this.tanya_detail = tanya_detail;
        this.type_Tanya = type_Tanya;
        this.tanya = tanya;
        this.terlihat = terlihat;
        this.isian = isian;

    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BHCItem withId(String id) {
        this.id = id;
        return this;
    }

    public String getNoPsb() {
        return seq;
    }

    public void setNoPsb(String seq) {
        this.seq = seq;
    }

    public BHCItem withNoPsb(String seq) {
        this.seq = seq;
        return this;
    }

    public String getNameID() {
        return name_id;
    }

    public void setNameID(String nama) {
        this.name_id = name_id;
    }

    public BHCItem withNameID(String name_id) {
        this.name_id = name_id;
        return this;
    }
    public String getFieldID() {
        return field_id;
    }

    public void setFieldID(String nama) {
        this.field_id = field_id;
    }

    public BHCItem withFieldID(String field_id) {
        this.field_id = field_id;
        return this;
    }
    public String getTanyaDetail() {
        return tanya_detail;
    }

    public void setTanyaDetail(String tanya_detail) {
        this.tanya_detail = tanya_detail;
    }

    public BHCItem witTanyaDetail(String tanya_detail) {
        this.tanya_detail = tanya_detail;
        return this;
    }

    public String getTypeTanya() {
        return type_Tanya;
    }

    public void setTypeTanya(String type_Tanya) {
        this.type_Tanya = type_Tanya;
    }

    public BHCItem withTypeTanya(String type_Tanya) {
        this.type_Tanya = type_Tanya;
        return this;
    }

    public String getTanya() {
        return tanya;
    }

    public void setTanya(String tanya) {
        this.tanya = tanya;
    }

    public BHCItem withTanya(String tanya) {
        this.tanya = tanya;
        return this;
    }

    public String getTerlihat() {
        return terlihat;
    }

    public void setTerlihat(String terlihat) {
        this.terlihat = terlihat;
    }

    public BHCItem withTerlihat(String terlihat) {
        this.terlihat = terlihat;
        return this;
    }

    public String getIsian() {
        return isian;
    }

    public void setIsian(String isian) {
        this.isian = isian;
    }

    public BHCItem withIsian(String isian) {
        this.isian = isian;
        return this;
    }
    public boolean contains(String query){
        StringBuilder dest = new StringBuilder();
        dest.append(id);
        dest.append(field_id);
        dest.append(seq);
        dest.append(name_id);
        dest.append(tanya_detail);
        dest.append(type_Tanya);
        dest.append(tanya);
        dest.append(terlihat);
        dest.append(isian);

        return dest.toString().toLowerCase().contains(query);
    }
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(field_id);
        dest.writeValue(seq);
        dest.writeValue(name_id);
        dest.writeValue(tanya_detail);
        dest.writeValue(type_Tanya);
        dest.writeValue(tanya);
        dest.writeValue(terlihat);
        dest.writeValue(isian);



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
        field_id = trim(field_id);
        seq = trim(seq);
        name_id = trim(name_id);
        tanya_detail = trim(tanya_detail);
        type_Tanya = trim(type_Tanya);
        tanya  = trim(tanya);
        terlihat  = trim(terlihat);
        isian  = trim(isian);

    }
    public int describeContents() {
        return  0;
    }

}
