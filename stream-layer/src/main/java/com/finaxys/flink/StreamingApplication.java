package com.finaxys.flink;

import com.finaxys.flink.processor.ProcessorFactory;
import configuration.CommandLineArgumentsParser;
import configuration.DelaySimulationConfiguration;
import configuration.StreamingApplicationConfiguration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @Author raphael on 26/01/2017.
 */
public class StreamingApplication {

    private static final String DELAY_CONF = "delayConf";
    private static final String APP_CONF = "appConf";

    private static CommandLine commandLine;
    private static DelaySimulationConfiguration delayConf;
    private static StreamingApplicationConfiguration appConf;


    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);

        commandLine = createCommandLine(args);
        delayConf = new DelaySimulationConfiguration(commandLine.getOptionValue(DELAY_CONF));
        appConf= new StreamingApplicationConfiguration(commandLine.getOptionValue(APP_CONF));

        ProcessorFactory.createProcessor(delayConf, appConf, env)
                .getSource()
                .processData()
                .sendToSink();
        env.execute();
    }



    private static CommandLine createCommandLine(String[] args) throws ParseException {
        Option atomConfPath = Option.builder()
                .argName(DELAY_CONF).longOpt(DELAY_CONF).desc("Path to the file containing the ATOM simulation parameters")
                .hasArg().required(true).build();
        Option appConfPath = Option.builder()
                .argName(APP_CONF).longOpt(APP_CONF).desc("Path to the file containing the application parameters")
                .hasArg().required(true).build();
        return CommandLineArgumentsParser.createCommandLine(args, atomConfPath, appConfPath);
    }

}
