package com.finaxys.flink.function.mapper;

import com.finaxys.streamintegrator.model.CashByAgent;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.types.Row;

public class MapCashByAgent implements MapFunction<Tuple2<Boolean, Row>, CashByAgent> {


    public CashByAgent map(Tuple2<Boolean, Row> booleanRowTuple2) throws Exception {
        Row row = booleanRowTuple2.getField(1);
        String name = row.getField(0).toString();
        Long cash = Long.parseLong(row.getField(1).toString());
        return new CashByAgent(name,cash);
    }

}
