package com.finaxys.flink.function.filter;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;

public class FilterCashByAgent {

     public static FilterFunction<Tuple2<Boolean, Row>> getCashByAgent() {
        return bool -> bool.getField(0).equals(true);
    }
}
