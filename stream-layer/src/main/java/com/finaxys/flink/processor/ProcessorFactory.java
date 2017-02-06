package com.finaxys.flink.processor;

import com.finaxys.utils.StreamLayerException;
import configuration.AtomSimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


/**
 * @Author raphael on 26/01/2017.
 */
public class ProcessorFactory {

    private static final String PRICE_MEAN_EVENT_TIME_PROCESSOR = "TestPriceMeanEventTime";

    public static DefaultStreamProcessor createProcessor(AtomSimulationConfiguration atomConf,
                                                         StreamingApplicationConfiguration appConf,
                                                         StreamExecutionEnvironment env) {

        switch (appConf.getProcessorName()) {
            case PRICE_MEAN_EVENT_TIME_PROCESSOR:
                return new TestPriceMeanEventTime(atomConf, appConf, env);
            default:
                throw new StreamLayerException("Unable to find appropriate processor for the given name '"+appConf.getProcessorName()+"'");
        }
    }

}
