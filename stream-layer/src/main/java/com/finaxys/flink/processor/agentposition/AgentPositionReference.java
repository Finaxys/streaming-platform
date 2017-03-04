package com.finaxys.flink.processor.agentposition;

import com.finaxys.flink.function.AtomLogFilter;
import com.finaxys.flink.function.AtomOrderLogFilter;
import com.finaxys.flink.time.SimpleTimestampAndWatermarkExtractor;
import configuration.DelaySimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import model.atomlogs.AtomLog;
import model.atomlogs.TimestampedAtomLog;
import model.atomlogs.orders.LimitOrderLog;
import model.atomlogs.orders.OrderLog;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author raphael on 26/01/2017.
 *
 * Agent position : his position if everyone of his orders was executed
 */
public class AgentPositionReference extends DefaultAgentPositionProcessor {
    private static Logger LOGGER = LogManager.getLogger(AgentPositionReference.class);

    public AgentPositionReference(DelaySimulationConfiguration delayConfig,
                                  StreamingApplicationConfiguration appConfig,
                                  StreamExecutionEnvironment environment) {
        super(delayConfig, appConfig, environment);
    }

    @Override
    protected DataStream processDataStream() {

        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        DataStream<TimestampedAtomLog> orderLogs = ((DataStream<TimestampedAtomLog>) source)
                .assignTimestampsAndWatermarks(new SimpleTimestampAndWatermarkExtractor())
                .filter(new AtomLogFilter(AtomLog.LogTypes.ORDER.getCode()))
                .filter(new AtomOrderLogFilter(OrderLog.OrderTypes.LIMIT_ORDER.getCode()));

        KeyedStream<TimestampedAtomLog, Tuple3<Long, String, String>> agentAndOrderBookKeyedLogs = orderLogs
                .keyBy(CustomKeySelector.keyByEventTimeAgentAndOrderbook());

        WindowedStream<TimestampedAtomLog, Tuple3<Long, String, String>, TimeWindow> timedAndKeyedOrderLogs = agentAndOrderBookKeyedLogs
                .timeWindow(Time.seconds(delayConf.getOutOfOrderMaxDelayInSeconds()));

        SingleOutputStreamOperator<Tuple4<Long, String, String, Double>> agentPosition = timedAndKeyedOrderLogs
                .fold(
                        new Tuple4<Long, String, String, Double>(0L, "", "", 0d),
                        new CalculateAgentPosition()
                );

        return agentPosition;
    }


    @Override
    protected void setType() {
        this.type = "agent_position";
    }

    protected static class CustomKeySelector {
        public static KeySelector<TimestampedAtomLog, Tuple3<Long, String, String>> keyByEventTimeAgentAndOrderbook() {
            return new KeySelector<TimestampedAtomLog, Tuple3<Long, String, String>>() {
                @Override
                public Tuple3<Long, String, String> getKey(TimestampedAtomLog log) throws Exception {
                    LimitOrderLog limitOrderLog = LimitOrderLog.class.cast(log.getAtomLog());
                    return new Tuple3<>(log.getEventTimeTimeStamp(), limitOrderLog.getAgentSenderName(), limitOrderLog.getOrderBookName());
                }
            };
        }
        public static KeySelector<TimestampedAtomLog, Tuple3<Long, String, String>> keyByProcessingTimeAgentAndOrderbook() {
            return new KeySelector<TimestampedAtomLog, Tuple3<Long, String, String>>() {
                @Override
                public Tuple3<Long, String, String> getKey(TimestampedAtomLog log) throws Exception {
                    LimitOrderLog limitOrderLog = LimitOrderLog.class.cast(log.getAtomLog());
                    return new Tuple3<>(log.getProcessingTimeTimeStamp(), limitOrderLog.getAgentSenderName(), limitOrderLog.getOrderBookName());
                }
            };
        }
    }

    protected static class CalculateAgentPosition implements FoldFunction<TimestampedAtomLog, Tuple4<Long, String, String, Double>> {
        @Override
        public Tuple4<Long, String, String, Double> fold(Tuple4<Long, String, String, Double> acc, TimestampedAtomLog orderLog) throws Exception {
            LimitOrderLog limitOrderLog = LimitOrderLog.class.cast(orderLog.getAtomLog());

            double nominalPosition = limitOrderLog.getQuantity() * limitOrderLog.getPrice();
            if (limitOrderLog.getDirection().equals(OrderLog.OrderDirections.SELLING.getCode()))
                nominalPosition = -nominalPosition;

            return new Tuple4<>(orderLog.getEventTimeTimeStamp(),
                    limitOrderLog.getAgentSenderName(),
                    limitOrderLog.getOrderBookName(),
                    acc.f3 + nominalPosition
            );
        }
    }
}
