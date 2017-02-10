package com.finaxys.flink.processor;

import com.finaxys.flink.sink.FileSink;
import configuration.DelaySimulationConfiguration;
import configuration.FileSinkConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.io.Serializable;


/**
 * @Author raphael on 26/01/2017.
 */
public abstract class DefaultStreamProcessor implements Serializable {



    protected static DelaySimulationConfiguration delayConf;
    protected static StreamingApplicationConfiguration appConf;
    protected static StreamExecutionEnvironment env;

    protected static DataStream source;
    protected static DataStream processedData;


    public DefaultStreamProcessor(DelaySimulationConfiguration delayConfig,
                                  StreamingApplicationConfiguration appConfig,
                                  StreamExecutionEnvironment environment) {
        delayConf = delayConfig;
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
//        processedData.print();
        if (appConf.getSinkType().equals(FileSink.FILE_SINK_KEY)) {
            FileSinkConfiguration fileConf = new FileSinkConfiguration(appConf.getAllProperties(), StreamingApplicationConfiguration.SINK_PREFIX);
            processedData.writeAsText(fileConf.getFullPath(), FileSystem.WriteMode.OVERWRITE);
        }
        else {
            processedData.addSink(createSink());
        }
    }

    protected abstract DataStream instantiateSource();

    protected abstract RichSinkFunction createSink();

    protected abstract DataStream processDataStream();
}
