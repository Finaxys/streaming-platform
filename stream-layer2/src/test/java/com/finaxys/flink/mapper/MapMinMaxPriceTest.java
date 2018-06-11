package com.finaxys.flink.mapper;

import com.finaxys.flink.function.mapper.MapMinMaxPrice;
import com.finaxys.streamintegrator.model.Price;
import com.google.common.collect.Lists;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.util.StreamingMultipleProgramsTestBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class  MapMinMaxPriceTest extends StreamingMultipleProgramsTestBase {

    @Test
    public void testMultiply() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setParallelism(1);

        CollectSink.values.clear();

        env.fromElements(
                    new Price("AA", 1, 11,"A","Khalil-10381","Denise-10231",1,11),
                    new Price("BB", 8, 9,"B","Lucy-10381","Jerome-10231",2,22),
                    new Price("CC", 2, 22,"C","Yassine-10381","Chris-10231",3,33)
                )
                .map(new MapMinMaxPrice())
                .addSink(new CollectSink());

        env.execute();

        assertEquals(Lists.newArrayList(
                new Tuple2<String, Tuple2<Integer, Integer>>("AA",new Tuple2<Integer, Integer>(1, 11)),
                new Tuple2<String, Tuple2<Integer, Integer>>("BB",new Tuple2<Integer, Integer>(2, 22)),
                new Tuple2<String, Tuple2<Integer, Integer>>("CC",new Tuple2<Integer, Integer>(3, 33))
                ), CollectSink.values);
    }

    private static class CollectSink implements SinkFunction<Tuple2<String, Tuple2<Integer, Integer>>> {
        public static final List<Tuple2<String, Tuple2<Integer, Integer>>> values = new ArrayList<>();

        @Override
        public synchronized void invoke(Tuple2<String, Tuple2<Integer, Integer>> value) throws Exception {
            values.add(value);
        }
    }
}