package com.finaxys.flink.processor;

import com.finaxys.flink.sink.ElasticSink;
import com.finaxys.flink.source.KafkaSource;
import com.finaxys.serialization.TimestampedAtomLogFlinkSchema;
import com.finaxys.utils.StreamLayerException;
import configuration.DelaySimulationConfiguration;
import configuration.ElasticsearchConfiguration;
import configuration.KafkaConfiguration;
import configuration.StreamingApplicationConfiguration;
import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @Author raphael on 26/01/2017.
 */
public abstract class DefaultKafkaToElasticProcessor extends DefaultStreamProcessor {
    private static Logger LOGGER = LogManager.getLogger(DefaultKafkaToElasticProcessor.class);
    protected String type;

    public DefaultKafkaToElasticProcessor(DelaySimulationConfiguration delayConfig,
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


    abstract protected DataStream processDataStream();
    abstract protected void setType();

    abstract protected ElasticsearchSinkFunction createElasticSinkFunction(ElasticsearchConfiguration elasticConfig);

}
