package com.finaxys.flink.flink;

import com.finaxys.flink.Utils.KafkaUtils;
import com.finaxys.flink.model.Agent;
import com.finaxys.flink.model.CashByAgent;
import com.finaxys.flink.model.Price;
import com.finaxys.flink.schema.AgentSchema;
import com.finaxys.flink.schema.CashByAgentSchema;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import javax.xml.crypto.Data;

public class AgentConsumer {


    public static void main(String args[]) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);
        DataStream<Agent> agents = env.addSource(new FlinkKafkaConsumer011<>(
                "TopicSAAS", new AgentSchema(), KafkaUtils.getProperties()));
        tableEnv.registerDataStream("cashTable",agents);
        Table orders = tableEnv.scan("cashTable");
        Table  resultCash = orders.groupBy("name").select("name, cash.Sum as cashSum");
        DataStream<Tuple2<Boolean,Row>> resultSteam = tableEnv.toRetractStream(resultCash,Row.class);
        DataStream<CashByAgent> modelStream = resultSteam.filter(bool -> bool.getField(0).equals(true))
                .map(new MapFunction<Tuple2<Boolean, Row>, CashByAgent>() {
                    @Override
                    public CashByAgent map(Tuple2<Boolean, Row> booleanRowTuple2) throws Exception {
                        Row row = booleanRowTuple2.getField(1);
                        String name = row.getField(0).toString();
                        Long cash = Long.parseLong(row.getField(1).toString());
                        return new CashByAgent(name,cash);
                    }
                });
        modelStream.addSink(new FlinkKafkaProducer011<CashByAgent>("localhost:9092","ResultCashByAgents", new CashByAgentSchema()));
        modelStream.print();
        env.execute();
    }
}
