package com.icollection.modelservice;

/**
 * Created by user on 1/10/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Authentication {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private AuthenticationData data;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("nama")
    @Expose
    private String nama;

    public String getChangepassword() {
        return changepassword;
    }

    public void setChangepassword(String changepassword) {
        this.changepassword = changepassword;
    }

    @SerializedName("changepassword")
    @Expose
    private String changepassword;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getFlag_beban() {
        return flag_beban;
    }

    public void setFlag_beban(String flag_beban) {
        this.flag_beban = flag_beban;
    }

    @SerializedName("flag_beban")
    @Expose
    private String flag_beban;
    /**
     * No args constructor for use in serialization
     *
     */
    public Authentication() {
    }

    /**
     *
     * @param message
     * @param token
     * @param status
     * @param data
     */
    public Authentication(Boolean status, String message, AuthenticationData data, String token, String changepassword) {
        super();
        this.status = status;
        this.message = message;
        this.data = data;
        this.token = token;
        this.changepassword = changepassword;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Authentication withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Authentication withMessage(String message) {
        this.message = message;
        return this;
    }

    public AuthenticationData getData() {
        return data;
    }

    public void setData(AuthenticationData data) {
        this.data = data;
    }

    public Authentication withData(AuthenticationData data) {
        this.data = data;
        return this;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Authentication withToken(String token) {
        this.token = token;
        return this;
    }

}
