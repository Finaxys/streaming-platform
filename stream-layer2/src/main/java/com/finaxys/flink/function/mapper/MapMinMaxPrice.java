package com.finaxys.flink.function.mapper;

import com.finaxys.streamintegrator.model.Price;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class MapMinMaxPrice implements MapFunction<Price, Tuple2<String, Tuple2<Integer, Integer>>> {

    public Tuple2<String, Tuple2<Integer, Integer>> map(Price price) throws Exception {
        return new Tuple2<>(price.getObName(), new Tuple2<>(price.getBestAskPrice(), price.getBestBidPrice()));
    }
}
