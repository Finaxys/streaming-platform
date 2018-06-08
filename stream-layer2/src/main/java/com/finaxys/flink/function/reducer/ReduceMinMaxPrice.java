package com.finaxys.flink.function.reducer;

import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;

public class ReduceMinMaxPrice implements ReduceFunction<Tuple2<String, Tuple2<Integer, Integer>>> {

    public Tuple2<String, Tuple2<Integer, Integer>> reduce(Tuple2<String, Tuple2<Integer, Integer>> t1, Tuple2<String, Tuple2<Integer, Integer>> t2) throws Exception {
        Tuple2<Integer, Integer> tuple1 = t1.getField(1);
        Tuple2<Integer, Integer> tuple2 = t2.getField(1);
        int min = Math.min(tuple1.getField(0), tuple2.getField(0));
        int max = Math.max(tuple1.getField(1), tuple2.getField(1));
        return new Tuple2<>(t1.getField(0), new Tuple2<>(min, max));
    }
}
