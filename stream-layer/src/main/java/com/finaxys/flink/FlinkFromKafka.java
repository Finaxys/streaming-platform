package com.finaxys.flink;

import com.finaxys.utils.StreamConfiguration;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class FlinkFromKafka {

    /*

    private static Logger LOGGER = LogManager.getLogger(FlinkFromKafka.class);

    private static StreamConfiguration streamConfig = StreamConfiguration.getInstance();
    private static String kafkaTopic = streamConfig.getKafkaTopic();
    private static String localZookeeperHost = streamConfig.getKafkaQuorum();
    private static String localKafkaBroker = streamConfig.getKafkaBroker();
    private static String consumerGroup = "atomGroup";


    public static void main(String[] args) throws Exception {

        // get an ExecutionEnvironment
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // configure event-time processing
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        // setup Kafka configuration
        Properties properties = new Properties();
        properties.setProperty("zookeeper.connect", localZookeeperHost);  // Zookeeper host:port
        properties.setProperty("bootstrap.servers", localKafkaBroker);    // Broker host:port
        properties.setProperty("group.id", consumerGroup);                // Consumer group ID



        // ---------- Consume data from Kafak ----------

        //
         * Kafka Consumer
         * Flink’s Kafka consumer is called FlinkKafkaConsumer08 (or 09 for Kafka 0.9.0.x versions).
         * It provides access to one or more Kafka topics.
         *
         * The constructor accepts the following arguments:
         *      - The topic name / list of topic names
         *      - A DeserializationSchema / KeyedDeserializationSchema for deserializing the
         *        data from Kafka
         *      - Properties for the Kafka consumer. The following properties are required:
         *          - “bootstrap.servers” (comma separated list of Kafka brokers)
         *          - “zookeeper.connect” (comma separated list of Zookeeper servers)
         *              (only required for Kafka 0.8)
         *          - “group.id” the id of the consumer group


        // DataStream with trace type as a String
        DataStream<Tuple2<String, String>> simulLogs = env.addSource(
                // use of FlinkKafkaConsumer08 because of the flink-connector-kafka
                 * version (0.8)
                new FlinkKafkaConsumer09<Tuple2<String, String>>(
                        kafkaTopic,                 // Topic to read from
                        new SimpleTuple2Schema(),   // Deserializer
                        properties                  // Kafka configuration
                ));

        // DataStream with trace type as TraceType enum
        DataStream<Tuple2<TraceType, String>> simulLogsWithTraceEnum = env.addSource(
                new FlinkKafkaConsumer09<Tuple2<TraceType, String>>(
                        kafkaTopic,                             // Topic to read from
                        new Tuple2TraceTypeAndStringSchema(),   // Deserializer
                        properties                              // Kafka configuration
                ));



        // ---------- Simple data transformation on the DataStream ----------

        // count the number of traces depending on the trace type
        DataStream<Tuple2<TraceType,Integer>> countDifferentTraces =  simulLogsWithTraceEnum
                .flatMap(new FlatMapFunction<Tuple2<TraceType,String>, Tuple2<TraceType,Integer>>() {
                    @Override
                    public void flatMap(Tuple2<TraceType, String> in,
                                        Collector<Tuple2<TraceType, Integer>> out) throws Exception {
                        out.collect(new Tuple2<TraceType, Integer>(in.f0, 1));
                    }
                })
                .keyBy(0)
                .sum(1);

        // count all traces
        DataStream<Tuple1<Integer>> countAllTraces = simulLogsWithTraceEnum
                .flatMap(new FlatMapFunction<Tuple2<TraceType,String>, Tuple1<Integer>>() {
                    @Override
                    public void flatMap(Tuple2<TraceType, String> in,
                                        Collector<Tuple1<Integer>> out) throws Exception {
                        out.collect(new Tuple1<Integer>(1));
                    }
                })
                .keyBy(0)
                .sum(0);



        // Uncomment to see results on standart output (while AtomGenerate is running to have data into the kafka Topic)
        countDifferentTraces.print();
//        countAllTraces.print();

        env.execute();
    }



    public static class SimpleTuple2Schema
            implements DeserializationSchema<Tuple2<String, String>>,
            SerializationSchema<Tuple2<String, String>> {

        public SimpleTuple2Schema() {}

        @Override
        public Tuple2<String, String> deserialize(byte[] message) throws IOException {

            String stringMessage = new String(message);

            String typeOfMessage = stringMessage.substring(0, stringMessage.indexOf(";"));
            String messageContent = stringMessage.substring(stringMessage.indexOf(";")+1);

            return new Tuple2<>(typeOfMessage, messageContent);
        }

        @Override
        public byte[] serialize(Tuple2<String, String> element) {
            return (element.f0 + ";" + element.f1).getBytes();
        }

        @Override
        public boolean isEndOfStream(Tuple2<String, String> nextElement) {
            return false;
        }

        @Override
        public TypeInformation<Tuple2<String, String>> getProducedType() {
            return new TupleTypeInfo<>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO);
        }
    }



    public static class Tuple2TraceTypeAndStringSchema
            implements DeserializationSchema<Tuple2<TraceType, String>>,
            SerializationSchema<Tuple2<TraceType, String>> {

        public Tuple2TraceTypeAndStringSchema() {}


        @Override
        public Tuple2<TraceType, String> deserialize(byte[] messsage) throws IOException {
            String stringMessage = new String(messsage);

            TraceType traceType = TraceType.valueOf(stringMessage.substring(0, stringMessage.indexOf(";")));
            String messageContent = stringMessage.substring(stringMessage.indexOf(";")+1);

            return new Tuple2<>(traceType, messageContent);
        }

        @Override
        public boolean isEndOfStream(Tuple2<TraceType, String> stringStringTuple2) {
            return false;
        }

        @Override
        public TypeInformation<Tuple2<TraceType, String>> getProducedType() {
            return new TupleTypeInfo<>(TypeInformation.of(TraceType.class), BasicTypeInfo.STRING_TYPE_INFO);
        }

        @Override
        public byte[] serialize(Tuple2<TraceType, String> element) {
            return (element.f0 + ";" + element.f1).getBytes();
        }
    }

    */


}
