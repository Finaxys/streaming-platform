package com.finaxys.flink.function.mapper;

import com.finaxys.streamintegrator.model.MinMaxPrice;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class MapTuple2ToMinMax implements MapFunction<Tuple2<String, Tuple2<Integer, Integer>>, MinMaxPrice> {

    public MinMaxPrice map(Tuple2<String, Tuple2<Integer, Integer>> stringTuple2Tuple2) throws Exception {
        Tuple2<Integer, Integer> tuple1 = stringTuple2Tuple2.getField(1);
        return new MinMaxPrice(stringTuple2Tuple2.getField(0), tuple1.getField(0), tuple1.getField(1));
    }
}
