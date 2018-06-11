package com.finaxys.flink.mapper;


import com.finaxys.flink.function.mapper.MapCashByAgent;
import com.finaxys.streamintegrator.model.Agent;
import com.finaxys.streamintegrator.model.CashByAgent;
import com.google.common.collect.Lists;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.util.StreamingMultipleProgramsTestBase;
import org.apache.flink.types.Row;
import org.junit.Test;


import javax.security.auth.callback.CallbackHandler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class MapCashByAgentTest extends StreamingMultipleProgramsTestBase {

    @Test
    public void testMultiply() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        CollectSink.values.clear();

        Row row1 = new Row(2);
        row1.setField(0,"Khalil");
        row1.setField(1,10);

        Row row2 = new Row(2);
        row2.setField(0,"Denise");
        row2.setField(1,20);

        Row row3 = new Row(2);
        row3.setField(0,"Atef");
        row3.setField(1,30);

        Tuple2<Boolean, Row> tuple1 = new Tuple2<>(true, row1);
        Tuple2<Boolean, Row> tuple2 = new Tuple2<>(true, row2);
        Tuple2<Boolean, Row> tuple3 = new Tuple2<>(true, row3);

        env.fromElements(tuple1, tuple2, tuple3)
                .map(new MapCashByAgent())
                .addSink(new CollectSink());

        env.execute();

        assertEquals(Lists.newArrayList(
                new CashByAgent("Khalil",new Long("10")),
                new CashByAgent("Denise",new Long("20")),
                new CashByAgent("Atef",new Long("30"))
                ), CollectSink.values);
    }

    private static class CollectSink implements SinkFunction<CashByAgent> {
        public static final List<CashByAgent> values = new ArrayList<>();

        @Override
        public synchronized void invoke(CashByAgent value) throws Exception {
            values.add(value);
        }
    }


}
