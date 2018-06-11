package com.finaxys.flink.mapper;

import com.finaxys.flink.function.mapper.MapCashByAgent;
import com.finaxys.flink.function.mapper.MapTuple2ToMinMax;
import com.finaxys.streamintegrator.model.CashByAgent;
import com.finaxys.streamintegrator.model.MinMaxPrice;
import com.google.common.collect.Lists;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.util.StreamingMultipleProgramsTestBase;
import org.apache.flink.types.Row;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MapTuple2ToMinMaxTest extends StreamingMultipleProgramsTestBase {


    @Test
    public void testMultiply() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        CollectSink.values.clear();

        Tuple2<String, Tuple2<Integer, Integer>> tuple1 = new Tuple2<String, Tuple2<Integer, Integer>>("DD",new Tuple2<>(10,15));
        Tuple2<String, Tuple2<Integer, Integer>> tuple2 = new Tuple2<String, Tuple2<Integer, Integer>>("COM",new Tuple2<>(4,27));
        Tuple2<String, Tuple2<Integer, Integer>> tuple3 = new Tuple2<String, Tuple2<Integer, Integer>>("XD",new Tuple2<>(1,19));

        env.fromElements(tuple1, tuple2, tuple3)
                .map(new MapTuple2ToMinMax())
                .addSink(new CollectSink());

        env.execute();

        assertEquals(Lists.newArrayList(
                new MinMaxPrice ("DD",10,15),
                new MinMaxPrice ("COM",4,27),
                new MinMaxPrice ("XD",1,19)
        ), CollectSink.values);
    }

    private static class CollectSink implements SinkFunction<MinMaxPrice> {
        public static final List<MinMaxPrice> values = new ArrayList<>();

        @Override
        public synchronized void invoke(MinMaxPrice value) throws Exception {
            values.add(value);
        }
    }
}
