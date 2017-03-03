package com.finaxys.flink.processor.meanprice;

import com.finaxys.flink.function.AtomLogFilter;
import com.finaxys.flink.processor.DefaultKafkaToElasticProcessor;
import com.finaxys.flink.time.BoundedTimestampAndWatermarkExtractor;
import configuration.DelaySimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import model.atomlogs.AtomLog;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.price.PriceLog;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @Author raphael on 26/01/2017.
 */
public class PriceMeanEventTime extends DefaultMeanPriceProcessor {
    private static Logger LOGGER = LogManager.getLogger(PriceMeanEventTime.class);

    public PriceMeanEventTime(DelaySimulationConfiguration delayConfig,
                              StreamingApplicationConfiguration appConfig,
                              StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }

    @Override
    protected DataStream processDataStream() {

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        return ((DataStream<TimestampedAtomLog>)source)
                .filter(new AtomLogFilter(AtomLog.LogTypes.PRICE.getCode()))
                .assignTimestampsAndWatermarks(new BoundedTimestampAndWatermarkExtractor(delayConf.getOutOfOrderMaxDelayInMillies()))
                .map(PriceMeanReference.ExtractTimeStampAndPrice.evenTime())
                .keyBy(0)
                .timeWindow(Time.seconds(delayConf.getOutOfOrderMaxDelayInSeconds()))
//                .allowedLateness(Time.seconds(delayConf.getOutOfOrderMaxDelayInSeconds())) // ne change rien
                .fold(new Tuple3<Long, Double, Long>(0L, 0D, 0L),
                        PriceMeanReference.CalculatePriceMean.fold());
    }


    @Override
    protected void setType() {
        this.type = "price_log";
    }
}
