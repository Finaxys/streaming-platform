package com.finaxys.flink;

import com.finaxys.Utils.KafkaUtils;
import com.finaxys.model.MinMaxPrice;
import com.finaxys.model.Price;
import com.finaxys.schema.MinMaxPriceSchema;
import com.finaxys.schema.PriceSchema;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

public class PriceConsumer {

    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Price> prices = env.addSource(new FlinkKafkaConsumer011<>(
                "TopicSAAS", new PriceSchema(), KafkaUtils.getProperties()));
        DataStream<Tuple2<String, Tuple2<Integer, Integer>>> bestPrices = prices.map(new MapFunction<Price, Tuple2<String, Tuple2<Integer, Integer>>>() {
            @Override
            public Tuple2<String, Tuple2<Integer, Integer>> map(Price price) throws Exception {
                return new Tuple2<>(price.getObName(), new Tuple2<>(price.getBestAskPrice(), price.getBestBidPrice()));
            }
        }).keyBy(0).reduce(new ReduceFunction<Tuple2<String, Tuple2<Integer, Integer>>>() {
            @Override
            public Tuple2<String, Tuple2<Integer, Integer>> reduce(Tuple2<String, Tuple2<Integer, Integer>> t1, Tuple2<String, Tuple2<Integer, Integer>> t2) throws Exception {
                Tuple2<Integer, Integer> tuple1 = t1.getField(1);
                Tuple2<Integer, Integer> tuple2 = t2.getField(1);
                int min = Math.min(tuple1.getField(0), tuple2.getField(0));
                int max = Math.max(tuple1.getField(1), tuple2.getField(1));
                return new Tuple2<>(t1.getField(0), new Tuple2<>(min, max));
            }
        });

        DataStream<MinMaxPrice> minMaxPriceDs = bestPrices.map(new MapFunction<Tuple2<String, Tuple2<Integer, Integer>>, MinMaxPrice>() {
            @Override
            public MinMaxPrice map(Tuple2<String, Tuple2<Integer, Integer>> stringTuple2Tuple2) throws Exception {
                Tuple2<Integer, Integer> tuple1 = stringTuple2Tuple2.getField(1);
                return new MinMaxPrice(stringTuple2Tuple2.getField(0), tuple1.getField(0), tuple1.getField(1));
            }
        });
        minMaxPriceDs.addSink(new FlinkKafkaProducer011<MinMaxPrice>("localhost:9092","ResultMinMaxPrice", new MinMaxPriceSchema()));
        minMaxPriceDs.print();
        env.execute();
    }
}