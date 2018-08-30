package com.finaxys.flink.function.filter;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;

public class FilterCashByAgent implements FilterFunction<Tuple2<Boolean, Row>> {



    @Override
    public boolean filter(Tuple2<Boolean, Row> bool) throws Exception {
        return bool.getField(0).equals(true);
    }
}
