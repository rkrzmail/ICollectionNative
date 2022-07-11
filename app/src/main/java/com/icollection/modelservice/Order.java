
package com.icollection.modelservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {




    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private DataOrder dataOrder;
    @SerializedName("total")
    @Expose
    private Integer total;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Order() {
    }

    /**
     * 
     * @param total
     * @param message
     * @param status
     * @param dataOrder
     */
    public Order(Boolean status, String message, DataOrder dataOrder, Integer total) {
        super();
        this.status = status;
        this.message = message;
        this.dataOrder = dataOrder;
        this.total = total;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Order withStatus(Boolean status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Order withMessage(String message) {
        this.message = message;
        return this;
    }

    public DataOrder getDataOrder() {
        return dataOrder;
    }

    public void setDataOrder(DataOrder dataOrder) {
        this.dataOrder = dataOrder;
    }

    public Order withData(DataOrder dataOrder) {
        this.dataOrder = dataOrder;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Order withTotal(Integer total) {
        this.total = total;
        return this;
    }

}
