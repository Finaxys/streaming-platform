package com.finaxys.flink.function.mapper;

import com.finaxys.streamintegrator.model.Price;
import com.finaxys.streamintegrator.model.PriceByCat;
import org.apache.flink.api.common.functions.MapFunction;

public class MapPriceByCat implements MapFunction<Price, PriceByCat> {

    public PriceByCat map(Price price) throws Exception {
        return new PriceByCat(price.getObName(),price.getPrice(), price.getExecutedQuty());
    }
}
