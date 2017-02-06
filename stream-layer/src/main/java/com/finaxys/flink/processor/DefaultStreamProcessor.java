package com.finaxys.flink.processor;

import configuration.AtomSimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.Serializable;


/**
 * @Author raphael on 26/01/2017.
 */
public abstract class DefaultStreamProcessor implements Serializable {



    protected static AtomSimulationConfiguration atomConf;
    protected static StreamingApplicationConfiguration appConf;
    protected static StreamExecutionEnvironment env;

    protected static DataStream source;
    protected static DataStream processedData;


    public DefaultStreamProcessor(AtomSimulationConfiguration atomConfig,
                                  StreamingApplicationConfiguration appConfig,
                                  StreamExecutionEnvironment environment) {
        atomConf = atomConfig;
        appConf = appConfig;
        env = environment;
    }



    public DefaultStreamProcessor getSource() {
        source = instantiateSource();
        return this;
    }

    public DefaultStreamProcessor processData() {
        processedData = processDataStream();
        return this;
    }

    public void sendToSink() {
        processedData.addSink(createSink());
    }

    protected abstract DataStream instantiateSource();

    protected abstract RichSinkFunction createSink();

    protected abstract DataStream processDataStream();
}
