package com.finaxys.flink.processor;

import com.finaxys.flink.function.mapper.MapPriceByCat;
import com.finaxys.flink.function.mapper.MapTuple2ToMinMax;
import com.finaxys.streamintegrator.model.Price;
import com.finaxys.streamintegrator.model.PriceByCat;
import com.finaxys.streamintegrator.schema.MinMaxPriceSchema;
import com.finaxys.streamintegrator.schema.PriceByCatSchema;
import com.finaxys.streamintegrator.schema.PriceSchema;
import com.finaxys.streamintegrator.Utils.KafkaUtils;
import com.finaxys.flink.function.mapper.MapMinMaxPrice;
import com.finaxys.streamintegrator.model.MinMaxPrice;
import com.finaxys.flink.function.reducer.ReduceMinMaxPrice;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

public class PriceConsumer {

    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<Price> prices = env.addSource(new FlinkKafkaConsumer011<>(
                KafkaUtils.getTopicAtom(), new PriceSchema(), KafkaUtils.getProperties()));
        DataStream<Tuple2<String, Tuple2<Integer, Integer>>> bestPrices = prices
                .map(new MapMinMaxPrice())
                .keyBy(0).reduce(new ReduceMinMaxPrice());
        DataStream<MinMaxPrice> minMaxPriceDs = bestPrices.map(new MapTuple2ToMinMax());
        minMaxPriceDs.addSink(new FlinkKafkaProducer011<MinMaxPrice>(KafkaUtils.getBrokerList(), "ResultMinMaxPrice", new MinMaxPriceSchema()));
        DataStream<PriceByCat> pricesByCat = prices
                .map(new MapPriceByCat());
        pricesByCat.addSink(new FlinkKafkaProducer011<PriceByCat>(KafkaUtils.getBrokerList(), "ResultPrice", new PriceByCatSchema()));
        env.execute();
    }
}