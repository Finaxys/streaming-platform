package com.finaxys.flink;

import com.finaxys.Utils.KafkaUtils;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
/* import org.apache.flink.streaming.connectors.fs.bucketing.BucketingSink;
import org.apache.flink.streaming.connectors.fs.bucketing.DateTimeBucketer; */
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;

public class DataTransfer {





        public static void main(String[] args) throws Exception {


            // Flink environment setup
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.getConfig().disableSysoutLogging();
            env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(4, 10000));

            // Flink check/save point setting
            env.enableCheckpointing(30000);
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            env.getCheckpointConfig().setMinPauseBetweenCheckpoints(10000);
            env.getCheckpointConfig().setCheckpointTimeout(10000);
            env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);

            env.getCheckpointConfig().enableExternalizedCheckpoints(
                    CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION
            );

            // Init the stream
            DataStream<String> stream = env
                    .addSource(new FlinkKafkaConsumer010<String>(
                            "flink-test",
                            new SimpleStringSchema(),
                            KafkaUtils.getProperties()));

            // Path of the output
            String basePath = "file:///home/finaxys/StreamingAsAService/output"; // Here is you output path

            /* BucketingSink<String> hdfsSink = new BucketingSink<>(basePath);
            hdfsSink.setBucketer(new DateTimeBucketer<>("yyyy-MM-dd--HH-mm"));
            stream.print();
            stream.addSink(hdfsSink); */

            env.execute();
        }

}
