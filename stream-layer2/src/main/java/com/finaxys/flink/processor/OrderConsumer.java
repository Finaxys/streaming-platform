package com.finaxys.flink.processor;

import com.finaxys.flink.function.mapper.MapOrderByDir;
import com.finaxys.streamintegrator.Utils.KafkaUtils;
import com.finaxys.streamintegrator.model.Order;
import com.finaxys.streamintegrator.model.OrderByDir;
import com.finaxys.streamintegrator.schema.OrderByDirSchema;
import com.finaxys.streamintegrator.schema.OrderSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;

public class OrderConsumer {

    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);
        DataStream<Order> orders = env.addSource(new FlinkKafkaConsumer011<>(
                KafkaUtils.getTopicAtom(), new OrderSchema(), KafkaUtils.getProperties()));
        DataStream<OrderByDir> ordersByDir = orders
                .map(new MapOrderByDir());
        ordersByDir.addSink(new FlinkKafkaProducer011<OrderByDir>(KafkaUtils.getBrokerList(), "ResultOrderByDir", new OrderByDirSchema()));
        ordersByDir.print();
        env.execute();
    }
}
