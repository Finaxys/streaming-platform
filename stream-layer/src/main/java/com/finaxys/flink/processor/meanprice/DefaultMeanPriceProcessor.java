package com.finaxys.flink.processor.meanprice;

import com.finaxys.flink.processor.DefaultKafkaToElasticProcessor;
import configuration.DelaySimulationConfiguration;
import configuration.ElasticsearchConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.elasticsearch2.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch2.RequestIndexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author raphael on 26/01/2017.
 */
public abstract class DefaultMeanPriceProcessor extends DefaultKafkaToElasticProcessor {
    private static Logger LOGGER = LogManager.getLogger(DefaultMeanPriceProcessor.class);
    protected String type;

    public DefaultMeanPriceProcessor(DelaySimulationConfiguration delayConfig,
                                     StreamingApplicationConfiguration appConfig,
                                     StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }


    abstract protected DataStream processDataStream();
    abstract protected void setType();




    protected ElasticsearchSinkFunction<Tuple3<Long, Double, Long>> createElasticSinkFunction(ElasticsearchConfiguration elasticConfig) {
        this.setType();

        return new ElasticsearchSinkFunction<Tuple3<Long, Double, Long>>() {
            IndexRequest createIndexRequest(Tuple3<Long, Double, Long> element) {
                Map<String, String> values = new HashMap<>();
                values.put("@timestamp", element.f0.toString());
                values.put("price", element.f1.toString());
                values.put("timeType", elasticConfig.getDiscriminant());

                return Requests
                        .indexRequest()
                        .index(elasticConfig.getIndex())
                        .type(type)
                        .source(values);
            }
            @Override
            public void process(Tuple3<Long, Double, Long> element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                requestIndexer.add(createIndexRequest(element));
            }
        };
    }

}
