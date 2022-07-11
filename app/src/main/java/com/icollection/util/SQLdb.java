package com.icollection.util;

import com.icollection.modelservice.OrderItem;
import com.icollection.modelservice.OrderItem_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.ArrayList;

public class SQLdb {
    public static synchronized ArrayList getOrder(String status, String...status2){
        return (ArrayList) SQLite.select().from(OrderItem.class)
                .where(OrderItem_Table.status.in(status,status))
                .queryList();
    }
}
