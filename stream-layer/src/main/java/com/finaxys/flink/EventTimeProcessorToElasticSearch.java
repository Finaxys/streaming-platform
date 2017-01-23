package com.finaxys.flink;

import com.finaxys.flink.time.BoundedTimestampAndWatermarkExtractor;
import com.finaxys.serialization.TimestampedAtomLogSchema;
import configuration.AtomSimulationConfiguration;
import configuration.CommandLineArgumentsParser;
import configuration.KafkaConfiguration;
import model.atomlogs.AtomLog;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.price.PriceLog;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSink;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.RequestIndexer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author raphael on 28/12/2016.
 */
public class EventTimeProcessorToElasticSearch {


    private static Logger LOGGER = LogManager.getLogger(EventTimeProcessorToElasticSearch.class);

    private static final String ATOM_CONF = "atomConf";
    private static final String KAFKA_CONF = "kafkaConf";

    private static KafkaConfiguration kafkaConf;
    private static AtomSimulationConfiguration atomConf;
    private static CommandLine commandLine;


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        commandLine = createCommandLine(args);
        kafkaConf = new KafkaConfiguration(commandLine.getOptionValue(KAFKA_CONF));
        atomConf = new AtomSimulationConfiguration(commandLine.getOptionValue(ATOM_CONF));


        Map<String, String> elasticConfigs = new HashMap<>();
        elasticConfigs.put("bulk.flush.max.actions", "1");
        elasticConfigs.put("cluster.name", "my-elastic-cluster");
//        elasticConfigs.put("path.home", "/usr/local/Cellar/elasticsearch/2.3.5/data");
        // Add elasticsearch hosts on startup
        List<InetSocketAddress> transports = new ArrayList<>();
        transports.add(new InetSocketAddress("127.0.0.1", 9300));

        DataStream<TimestampedAtomLog> stream = env.addSource(
            new FlinkKafkaConsumer010<>(
                    kafkaConf.getKafkaTopic(),
                    new TimestampedAtomLogSchema(atomConf.isTimestampHumanReadableEnabled()),
                    kafkaConf.getKafkaProperties()
            )
        );

        String right = "/Users/raphael/Desktop/atom/atom-simul-right-save.out";
        String wrong = "/Users/raphael/Desktop/atom/atom-simul-wrong.out";

        DataStream<TimestampedAtomLog> streamFile = env.readTextFile(wrong)
                .flatMap(new FlatMapFunction<String, TimestampedAtomLog>() {
                    @Override
                    public void flatMap(String s, Collector<TimestampedAtomLog> collector) throws Exception {
                        collector.collect(new TimestampedAtomLog(s, atomConf.isTimestampHumanReadableEnabled()));
                    }
                });


        streamFile
                .filter(timestampedAtomLog -> timestampedAtomLog.getAtomLog().getLogType().equals(AtomLog.LogTypes.PRICE.getCode()))
                .assignTimestampsAndWatermarks(new BoundedTimestampAndWatermarkExtractor(atomConf.getOutOfOrderMaxDelayInMillies()))
                .map(new MapFunction<TimestampedAtomLog, Tuple2<Long, Long>>() {
                    @Override
                    public Tuple2<Long, Long> map(TimestampedAtomLog in) throws Exception {
                        return new Tuple2<Long, Long>(in.getEventTimeTimeStamp(), PriceLog.class.cast(in.getAtomLog()).getPrice());
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(atomConf.getOutOfOrderMaxDelayInSeconds()))
                .allowedLateness(Time.seconds(atomConf.getOutOfOrderMaxDelayInSeconds()*2))
                .fold(new Tuple3<Long, Double, Long>(0L, 0D, 0L), new FoldFunction<Tuple2<Long, Long>, Tuple3<Long, Double, Long>>() {
                    @Override
                    public Tuple3<Long, Double, Long> fold(Tuple3<Long, Double, Long> previousMean, Tuple2<Long, Long> currentValue) throws Exception {

                        Double mean = ( (previousMean.f1 * previousMean.f2) + currentValue.f1 ) / (previousMean.f2 + 1);

                        return new Tuple3<>(currentValue.f0, mean, previousMean.f2+1);
                    }
                })
                .addSink(new ElasticsearchSink<>(elasticConfigs, transports, new ElasticsearchSinkFunction<Tuple3<Long, Double, Long>>() {

                    public IndexRequest createIndexRequest(Tuple3<Long, Double, Long> element) {
                        Map<String, String> esJson = new HashMap<>();
                        esJson.put("@timestamp", element.f0.toString());
                        esJson.put("price_mean", element.f1.toString());

                        return Requests
                                .indexRequest()
                                .index("test-on-prices")
                                .source(esJson);
                    }

                    @Override
                    public void process(Tuple3<Long, Double, Long> element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                        requestIndexer.add(createIndexRequest(element));
                    }
                }));

        env.execute();
    }



    private static CommandLine createCommandLine(String[] args) throws ParseException {
        Option atomConfPath = Option.builder()
                .argName(ATOM_CONF).longOpt(ATOM_CONF).desc("Path to the file containing the ATOM simulation parameters")
                .hasArg().required(true).build();
        Option kafkaConfPath = Option.builder()
                .argName(KAFKA_CONF).longOpt(KAFKA_CONF).desc("Path to the file containing the Kafka parameters")
                .hasArg().required(true).build();
        return CommandLineArgumentsParser.createCommandLine(args, kafkaConfPath, atomConfPath);
    }
}
