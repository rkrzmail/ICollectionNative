
package com.icollection.modelservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VA {


    @SerializedName("status")
    @Expose
    private Boolean status;

    @SerializedName("data")
    @Expose
    private VAdata data;

    /**
     * No args constructor for use in serialization
     *
     */
    public VA() {
    }


    public VA(Boolean status, String message, VAdata data) {
        super();
        this.status = status;

        this.data = data;

    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public VA withStatus(Boolean status) {
        this.status = status;
        return this;
    }


    public VAdata getDataOrder() {
        return data;
    }

    public void setDataOrder(VAdata data) {
        this.data = data;
    }

    public VA withData(VAdata data) {
        this.data = data;
        return this;
    }



}
