
package com.icollection.modelservice;


import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataOrder {

    @SerializedName("tbl_order")
    @Expose
    private List<OrderItem> orderItem = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public DataOrder() {
    }

    /**
     *
     * @param orderItem
     */
    public DataOrder(List<OrderItem> orderItem) {
        super();
        this.orderItem = orderItem;
    }

    public List<OrderItem> getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
    }

    public DataOrder withTblOrder(List<OrderItem> orderItem) {
        this.orderItem = orderItem;
        return this;
    }

}
