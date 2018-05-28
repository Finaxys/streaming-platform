package com.finaxys.flink.flink;

import com.finaxys.flink.Utils.KafkaUtils;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;

import java.util.Properties;

public class KafkaConsumer {

    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> stream = env.addSource(new FlinkKafkaConsumer011<>(
                "atomTopic", new SimpleStringSchema(), KafkaUtils.getProperties()));
        DataStream<String> stream1 =
                stream.filter((FilterFunction<String>) value -> {
                    String[] ss = value.split(";");
                    return ss[0].equals("Agent");
                });
        stream1.print();
        env.execute();
    }
}