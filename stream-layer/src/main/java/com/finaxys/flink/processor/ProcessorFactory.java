package com.finaxys.flink.processor;

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

    private static final String PRICE_MEAN_EVENT_TIME_PROCESSOR = "PriceMeanEventTime";
    private static final String PRICE_MEAN_PROCESSING_TIME_PROCESSOR = "PriceMeanProcessingTime";
    private static final String PRICE_MEAN_REFERENCE = "PriceMeanReference";

    public static DefaultStreamProcessor createProcessor(DelaySimulationConfiguration delayConf,
                                                         StreamingApplicationConfiguration appConf,
                                                         StreamExecutionEnvironment env) {

        switch (appConf.getProcessorName()) {
            case PRICE_MEAN_EVENT_TIME_PROCESSOR:
                return new PriceMeanEventTime(delayConf, appConf, env);
            case PRICE_MEAN_PROCESSING_TIME_PROCESSOR:
                return new PriceMeanProcessingTime(delayConf, appConf, env);
            case PRICE_MEAN_REFERENCE:
                return new PriceMeanReference(delayConf, appConf, env);
            default:
                throw new StreamLayerException("Unable to find appropriate processor for the given name '"+appConf.getProcessorName()+"'");
        }
    }

}
