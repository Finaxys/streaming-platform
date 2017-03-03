package com.finaxys.flink.processor.agentposition;

import com.finaxys.flink.processor.DefaultKafkaToElasticProcessor;
import configuration.DelaySimulationConfiguration;
import configuration.ElasticsearchConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.tuple.Tuple4;
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
public abstract class DefaultAgentPositionProcessor extends DefaultKafkaToElasticProcessor {
    private static Logger LOGGER = LogManager.getLogger(DefaultAgentPositionProcessor.class);
    protected String type;

    public DefaultAgentPositionProcessor(DelaySimulationConfiguration delayConfig,
                                         StreamingApplicationConfiguration appConfig,
                                         StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }


    abstract protected DataStream processDataStream();
    abstract protected void setType();

    @Override
    protected ElasticsearchSinkFunction<Tuple4<Long, String, String, Double>> createElasticSinkFunction(ElasticsearchConfiguration elasticConfig) {
        this.setType();

        return new ElasticsearchSinkFunction<Tuple4<Long, String, String, Double>>() {
            IndexRequest createIndexRequest(Tuple4<Long, String, String, Double> element) {
                Map<String, String> values = new HashMap<>();
                values.put("@timestamp", element.f0.toString());
                values.put("agent_and_orderbook", element.f1 + "_" + element.f2);
                values.put("agentPosition", element.f3.toString());
                values.put("timeType", elasticConfig.getDiscriminant());

                return Requests
                        .indexRequest()
                        .index(elasticConfig.getIndex())
                        .type(type)
                        .source(values);
            }
            @Override
            public void process(Tuple4<Long, String, String, Double> element, RuntimeContext runtimeContext, RequestIndexer requestIndexer) {
                requestIndexer.add(createIndexRequest(element));
            }
        };
    }

}
