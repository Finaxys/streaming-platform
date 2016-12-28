package com.finaxys.flink;

import com.finaxys.serialization.TimestampedAtomLogSchema;
import configuration.AtomSimulationConfiguration;
import configuration.CommandLineArgumentsParser;
import configuration.KafkaConfiguration;
import model.atomlogs.TimestampedAtomLog;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author raphael on 28/12/2016.
 */
public class EventTimeProcessor {


    private static Logger LOGGER = LogManager.getLogger(EventTimeProcessor.class);

    private static final String ATOM_CONF = "atomConf";
    private static final String KAFKA_CONF = "kafkaConf";

    private static KafkaConfiguration kafkaConf;
    private static AtomSimulationConfiguration atomConf;
    private static CommandLine commandLine;


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        commandLine = createCommandLine(args);
        kafkaConf = new KafkaConfiguration(commandLine.getOptionValue(KAFKA_CONF));
        atomConf = new AtomSimulationConfiguration(commandLine.getOptionValue(ATOM_CONF));

        DataStream<TimestampedAtomLog> stream = env.addSource(
            new FlinkKafkaConsumer010<>(
                    kafkaConf.getKafkaTopic(),
                    new TimestampedAtomLogSchema(atomConf.isTimestampHumanReadableEnabled()),
                    kafkaConf.getKafkaPropertiesWithGroupID(EventTimeProcessor.class.getName())
            )
        );

        stream.print();

        env.execute();
    }



    private static CommandLine createCommandLine(String[] args) throws ParseException {
        Option atomConfPath = Option.builder()
                .argName(ATOM_CONF).longOpt(ATOM_CONF).desc("Path to the file containing the ATOM simulation parameters")
                .hasArg().required(true).build();
        Option kafkaConfPath = Option.builder()
                .argName(KAFKA_CONF).longOpt(KAFKA_CONF).desc("Path to the file containing the Kafka parameters")
                .hasArg().required(true).build();
        return CommandLineArgumentsParser.createCommandLine(args, kafkaConfPath, atomConfPath);
    }
}
