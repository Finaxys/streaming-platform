package com.finaxys.flink;

import configuration.CommandLineArgumentsParser;
import configuration.KafkaConfiguration;
import model.atomlogs.TimestampedAtomLog;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * @Author raphael on 28/12/2016.
 */
public class EventTimeProcessor {


    private static Logger LOGGER = LogManager.getLogger(EventTimeProcessor.class);

    private static final String KAFKA_CONF = "kafkaConf";

    private static KafkaConfiguration kafkaConf;
    private static CommandLine commandLine;


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        commandLine = createCommandLine(args);
        kafkaConf = new KafkaConfiguration(commandLine.getOptionValue(KAFKA_CONF));

        DataStream<TimestampedAtomLog> stream = env.addSource(
            new FlinkKafkaConsumer010<>(
                    kafkaConf.getKafkaTopic(),
                    new TimestampedAtomLogSchema(),
                    kafkaConf.getKafkaPropertiesWithGroupID(EventTimeProcessor.class.getName())
            )
        );

        stream.print();

        env.execute();
    }



    private static CommandLine createCommandLine(String[] args) throws ParseException {
        Option kafkaConfPath = Option.builder()
                .argName(KAFKA_CONF).longOpt(KAFKA_CONF).desc("Path to the file containing the Kafka parameters")
                .hasArg().required(false).build();
        return CommandLineArgumentsParser.createCommandLine(args, kafkaConfPath);
    }



    public static class TimestampedAtomLogSchema implements DeserializationSchema<TimestampedAtomLog>, SerializationSchema<TimestampedAtomLog> {

        public TimestampedAtomLogSchema() {}

        @Override
        public TimestampedAtomLog deserialize(byte[] messsage) throws IOException {
            String stringMessage = new String(messsage);
            return new TimestampedAtomLog(stringMessage, true);
        }

        @Override
        public boolean isEndOfStream(TimestampedAtomLog timestampedAtomLog) {
            return false;
        }

        @Override
        public TypeInformation<TimestampedAtomLog> getProducedType() {
            return TypeInformation.of(TimestampedAtomLog.class);
        }

        @Override
        public byte[] serialize(TimestampedAtomLog timestampedAtomLog) {
            return Bytes.toBytes(timestampedAtomLog.toString());
        }
    }

}
