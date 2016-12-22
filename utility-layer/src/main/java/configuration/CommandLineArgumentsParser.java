package configuration;

import org.apache.commons.cli.*;

/**
 * @Author raphael on 21/12/2016.
 *
 * Class used to parse command line arguments
 */
public class CommandLineArgumentsParser {



    public static CommandLine createCommandLine(String[] args, Option ... opts) throws ParseException {
        Options options = new Options();
        for (Option opt : opts)
            options.addOption(opt);
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }
}
