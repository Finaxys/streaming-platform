package com.finaxys.flink.processor.meanprice;

import com.finaxys.flink.function.AtomLogFilter;
import com.finaxys.flink.time.SimpleTimestampAndWatermarkExtractor;
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
public class PriceMeanReference extends DefaultMeanPriceProcessor {
    private static Logger LOGGER = LogManager.getLogger(PriceMeanReference.class);

    public PriceMeanReference(DelaySimulationConfiguration delayConfig,
                              StreamingApplicationConfiguration appConfig,
                              StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }

    @Override
    protected DataStream processDataStream() {

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        return ((DataStream<TimestampedAtomLog>)source)
                .filter(new AtomLogFilter(AtomLog.LogTypes.PRICE.getCode()))
                .assignTimestampsAndWatermarks(new SimpleTimestampAndWatermarkExtractor())
                .map(ExtractTimeStampAndPrice.evenTime())
                .keyBy(0)
                .timeWindow(Time.seconds(delayConf.getOutOfOrderMaxDelayInSeconds()))
                .fold(new Tuple3<Long, Double, Long>(0L, 0D, 0L),
                        CalculatePriceMean.fold());
    }


    @Override
    protected void setType() {
        this.type = "price_log";
    }


    protected static class ExtractTimeStampAndPrice {
        public static MapFunction<TimestampedAtomLog, Tuple2<Long, Long>> evenTime() {
            return new MapFunction<TimestampedAtomLog, Tuple2<Long, Long>>() {
                @Override
                public Tuple2<Long, Long> map(TimestampedAtomLog in) throws Exception {
                    return new Tuple2<Long, Long>(in.getEventTimeTimeStamp(), PriceLog.class.cast(in.getAtomLog()).getPrice());
                }
            };
        }

        public static MapFunction<TimestampedAtomLog, Tuple2<Long, Long>> processingTime() {
            return new MapFunction<TimestampedAtomLog, Tuple2<Long, Long>>() {
                @Override
                public Tuple2<Long, Long> map(TimestampedAtomLog in) throws Exception {
                    return new Tuple2<Long, Long>(in.getProcessingTimeTimeStamp(), PriceLog.class.cast(in.getAtomLog()).getPrice());
                }
            };
        }
    }

    protected static class CalculatePriceMean {
        public static FoldFunction<Tuple2<Long, Long>, Tuple3<Long, Double, Long>> fold() {
            return new FoldFunction<Tuple2<Long, Long>, Tuple3<Long, Double, Long>>() {
                @Override
                public Tuple3<Long, Double, Long> fold(Tuple3<Long, Double, Long> previousMean, Tuple2<Long, Long> currentValue) throws Exception {

                    Double mean = ( (previousMean.f1 * previousMean.f2) + currentValue.f1 ) / (previousMean.f2 + 1);

                    return new Tuple3<>(currentValue.f0, mean, previousMean.f2+1);
                }
            };
        }
    }
}
