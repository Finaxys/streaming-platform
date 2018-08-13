package com.finaxys.flink.function.filter;

import com.finaxys.streamintegrator.model.Order;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;

public class FilterOrderByDir {

    public static FilterFunction<Order> getOrderByDirSell() {
        return order -> order.getDir().equals("A");
    }

    public static FilterFunction<Order> getOrderByDirBuy() {
        return order -> order.getDir().equals("B");
    }

}
