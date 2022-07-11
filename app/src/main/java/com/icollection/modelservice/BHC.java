
package com.icollection.modelservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BHC {




    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private BHCItem dataOrder;

    /**
     * No args constructor for use in serialization
     *
     */
    public BHC() {
    }

    /**
     *
     *
     * @param message
     * @param status
     * @param dataOrder
     */
    public BHC(Boolean status, String message, BHCItem dataOrder ) {
        super();
        this.status = status;
        this.message = message;
        this.dataOrder = dataOrder;

    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public BHC withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BHC withMessage(String message) {
        this.message = message;
        return this;
    }

    public BHCItem getDataOrder() {
        return dataOrder;
    }

    public void setDataOrder(BHCItem dataOrder) {
        this.dataOrder = dataOrder;
    }

    public BHC withData(BHCItem dataOrder) {
        this.dataOrder = dataOrder;
        return this;
    }



}
