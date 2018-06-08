package com.finaxys.flink.processor;

import com.finaxys.streamintegrator.model.Agent;
import com.finaxys.streamintegrator.model.CashByAgent;
import com.finaxys.streamintegrator.schema.AgentSchema;
import com.finaxys.streamintegrator.schema.CashByAgentSchema;
import com.finaxys.streamintegrator.Utils.KafkaUtils;
import com.finaxys.flink.function.filter.FilterCashByAgent;
import com.finaxys.flink.function.mapper.MapCashByAgent;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class AgentConsumer {

    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);
        DataStream<Agent> agents = env.addSource(new FlinkKafkaConsumer011<>(
                KafkaUtils.getTopicAtom(), new AgentSchema(), KafkaUtils.getProperties()));
        tableEnv.registerDataStream("cashTable", agents);
        Table orders = tableEnv.scan("cashTable");
        Table resultCash = orders.groupBy("name").select("name, cash.Sum as cashSum");
        DataStream<Tuple2<Boolean, Row>> resultSteam = tableEnv.toRetractStream(resultCash, Row.class);
        DataStream<CashByAgent> modelStream = resultSteam
                .filter(new FilterCashByAgent().getCashByAgent())
                .map(new MapCashByAgent());
        modelStream.addSink(new FlinkKafkaProducer011<CashByAgent>(KafkaUtils.getBrokerList(), "ResultCashByAgents", new CashByAgentSchema()));
        modelStream.print();
        env.execute();
    }


}
