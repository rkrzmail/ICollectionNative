package com.icollection.modelservice;

/**
 * Created by user on 1/10/2018.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangeResult {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;


    /**
     * No args constructor for use in serialization
     *
     */
    public ChangeResult() {
    }


    public ChangeResult(Boolean status, String message) {
        super();
        this.status = status;
        this.message = message;

    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ChangeResult withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChangeResult withMessage(String message) {
        this.message = message;
        return this;
    }




}
