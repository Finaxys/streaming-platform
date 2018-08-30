package com.finaxys.flink.function.mapper;

import com.finaxys.streamintegrator.model.Order;
import com.finaxys.streamintegrator.model.OrderByDir;
import org.apache.flink.api.common.functions.MapFunction;

public class MapOrderByDir implements MapFunction<Order, OrderByDir> {

    public OrderByDir map(Order order) throws Exception {
        if(order.getDir().equals("A"))
            return new OrderByDir("Ask",order.getQuty(),order.getPart());
        else
            return  new OrderByDir("Buy",order.getQuty(),order.getPart());
    }
}
