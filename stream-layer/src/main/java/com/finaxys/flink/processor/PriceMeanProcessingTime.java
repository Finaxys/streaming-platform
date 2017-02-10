package com.finaxys.flink.processor;

import com.finaxys.flink.sink.ElasticSink;
import com.finaxys.flink.source.KafkaSource;
import com.finaxys.flink.time.SimpleTimestampAndWatermarkExtractor;
import com.finaxys.serialization.TimestampedAtomLogFlinkSchema;
import com.finaxys.utils.StreamLayerException;
import configuration.*;
import model.atomlogs.AtomLog;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.price.PriceLog;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.RequestIndexer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author raphael on 26/01/2017.
 */
public class PriceMeanProcessingTime extends DefaultStreamProcessor {
    private static Logger LOGGER = LogManager.getLogger(PriceMeanProcessingTime.class);

    public PriceMeanProcessingTime(DelaySimulationConfiguration delayConfig,
                                   StreamingApplicationConfiguration appConfig,
                                   StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }

    @Override
    protected DataStream instantiateSource() {
        switch (appConf.getSourceType()) {

            case KafkaSource.KAFKA_SOURCE_KEY:
                KafkaConfiguration kafkaConf = new KafkaConfiguration(appConf.getAllProperties(), StreamingApplicationConfiguration.SOURCE_PREFIX);
                return env.addSource(new FlinkKafkaConsumer010<TimestampedAtomLog>(
                            kafkaConf.getKafkaTopic(),
                            new TimestampedAtomLogFlinkSchema(),
                            kafkaConf.getKafkaProperties()
                        )
                );
            default:
                String errorMessage = "Only Kafka source is allowed at the moment for this processor";
                LOGGER.error(errorMessage);
                throw new StreamLayerException(errorMessage);
        }
    }

    @Override
    protected RichSinkFunction createSink() {
        switch (appConf.getSinkType()) {
            case ElasticSink.ELASTICSEARCH_SINK_KEY:
                ElasticsearchConfiguration elasticConf = new ElasticsearchConfiguration(appConf.getAllProperties(), StreamingApplicationConfiguration.SINK_PREFIX);
                return new ElasticSink(elasticConf, createElasticSinkFunction(elasticConf));
            default:
                String errorMessage = "Only ElasticSearch sink is allowed at the moment for this processor";
                LOGGER.error(errorMessage);
                throw new StreamLayerException(errorMessage);
        }


    }

    @Override
    protected DataStream processDataStream() {

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        return ((DataStream<TimestampedAtomLog>)source)
                .filter(timestampedAtomLog -> timestampedAtomLog.getAtomLog().getLogType().equals(AtomLog.LogTypes.PRICE.getCode()))
                .assignTimestampsAndWatermarks(new SimpleTimestampAndWatermarkExtractor())
                .map(new MapFunction<TimestampedAtomLog, Tuple2<Long, Long>>() {
                    @Override
                    public Tuple2<Long, Long> map(TimestampedAtomLog in) throws Exception {
                        return new Tuple2<Long, Long>(in.getProcessingTimeTimeStamp(), PriceLog.class.cast(in.getAtomLog()).getPrice());
                    }
                })
                .keyBy(0)
                .timeWindow(Time.seconds(delayConf.getOutOfOrderMaxDelayInSeconds()/2))
                .fold(new Tuple3<Long, Double, Long>(0L, 0D, 0L), new FoldFunction<Tuple2<Long, Long>, Tuple3<Long, Double, Long>>() {
                    @Override
                    public Tuple3<Long, Double, Long> fold(Tuple3<Long, Double, Long> previousMean, Tuple2<Long, Long> currentValue) throws Exception {

                        Double mean = ( (previousMean.f1 * previousMean.f2) + currentValue.f1 ) / (previousMean.f2 + 1);

                        return new Tuple3<>(currentValue.f0, mean, previousMean.f2+1);
                    }
                });
    }




    private ElasticsearchSinkFunction<Tuple3<Long, Double, Long>> createElasticSinkFunction(ElasticsearchConfiguration elasticConfig) {
        return new ElasticsearchSinkFunction<Tuple3<Long, Double, Long>>() {

            IndexRequest createIndexRequest(Tuple3<Long, Double, Long> element) {
                Map<String, String> values = new HashMap<>();
                values.put("@timestamp", element.f0.toString());
                values.put("price", element.f1.toString());
                values.put("timeType", elasticConfig.getDiscriminant());

                return Requests
                        .indexRequest()
                        .index(elasticConfig.getIndex())
                        .type("price_log")
                        .source(values);
            }
            @Override
            public void process(Tuple3<Long, Double, Long> element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                requestIndexer.add(createIndexRequest(element));
            }
        };
    }

}
