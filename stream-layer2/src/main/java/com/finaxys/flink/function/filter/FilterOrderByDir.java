package com.finaxys.flink.function.filter;

import com.finaxys.streamintegrator.model.Order;
import org.apache.flink.api.common.functions.FilterFunction;

import java.util.List;

public class FilterOrderByDir implements FilterFunction<Order> {

    private List<String> dirs;

    public FilterOrderByDir(List<String> dirs) {

        this.dirs = dirs;
    }

    @Override
    public boolean filter(Order order) throws Exception {
        return dirs.contains(order.getDir());
    }
}
