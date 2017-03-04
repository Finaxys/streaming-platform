package com.finaxys.flink.processor;

import com.finaxys.flink.processor.agentposition.AgentPositionEventTime;
import com.finaxys.flink.processor.agentposition.AgentPositionProcessingTime;
import com.finaxys.flink.processor.agentposition.AgentPositionReference;
import com.finaxys.flink.processor.meanprice.PriceMeanEventTime;
import com.finaxys.flink.processor.meanprice.PriceMeanProcessingTime;
import com.finaxys.flink.processor.meanprice.PriceMeanReference;
import com.finaxys.utils.StreamLayerException;
import configuration.DelaySimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * @Author raphael on 26/01/2017.
 */
public class ProcessorFactory {

    private static final String PRICE_MEAN_REFERENCE = "PriceMeanReference";
    private static final String PRICE_MEAN_EVENT_TIME = "PriceMeanEventTime";
    private static final String PRICE_MEAN_PROCESSING_TIME = "PriceMeanProcessingTime";

    private static final String AGENT_POSITION_REFERENCE = "AgentPositionReference";
    private static final String AGENT_POSITION_EVENT_TIME = "AgentPositionEventTime";
    private static final String AGENT_POSITION_PROCESSING_TIME = "AgentPositionProcessingTime";

    public static DefaultStreamProcessor createProcessor(DelaySimulationConfiguration delayConf,
                                                         StreamingApplicationConfiguration appConf,
                                                         StreamExecutionEnvironment env) {
        switch (appConf.getProcessorName()) {
            case PRICE_MEAN_REFERENCE:
                return new PriceMeanReference(delayConf, appConf, env);
            case PRICE_MEAN_EVENT_TIME:
                return new PriceMeanEventTime(delayConf, appConf, env);
            case PRICE_MEAN_PROCESSING_TIME:
                return new PriceMeanProcessingTime(delayConf, appConf, env);
            case AGENT_POSITION_REFERENCE:
                return new AgentPositionReference(delayConf, appConf, env);
            case AGENT_POSITION_EVENT_TIME:
                return new AgentPositionEventTime(delayConf, appConf, env);
            case AGENT_POSITION_PROCESSING_TIME:
                return new AgentPositionProcessingTime(delayConf, appConf, env);
            default:
                throw new StreamLayerException("Unable to find appropriate processor for the given name '"+appConf.getProcessorName()+"'");
        }
    }

}
