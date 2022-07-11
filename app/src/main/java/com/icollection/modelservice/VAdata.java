
package com.icollection.modelservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VAdata {




    @SerializedName("VA_ALFA")
    @Expose
    private Boolean status;

    @SerializedName("data")
    @Expose
    private DataOrder dataOrder;

    /**
     * No args constructor for use in serialization
     *
     */
    public VAdata() {
    }

}
