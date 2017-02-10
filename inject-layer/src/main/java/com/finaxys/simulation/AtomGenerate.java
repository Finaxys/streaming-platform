package com.finaxys.simulation;

import com.finaxys.loging.AtomLogger;
import com.finaxys.loging.AtomLoggerWithDelay;
import com.finaxys.loging.injectors.AtomDataInjector;
import com.finaxys.loging.injectors.FileInjector;
import com.finaxys.loging.injectors.KafkaInjector;
import configuration.*;
import com.finaxys.utils.InjectLayerException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.cli.*;
import v13.*;
import v13.agents.ZIT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * USAGE:
 * Run this class with the following arguments:
 * 		--atomConf: [REQUIRED] file for ATOM simulation configuration
 * 	    --outputConf: [REQUIRED] file for Output configurations (file, kafka, others, ...)
 * 	    --delayConf: [OPTIONAL] file for delay configurations if there is delay added to the simulation
 * 		--kafkaConf: [OPTIONAL] file for KAFKA configuration if you are using Kafka as an output
 * 		--replayFromFile: [OPTIONAL] file from which the simulation will be replayed if you choose
 * 						   to replay an existing simulation
 */
public class AtomGenerate {

	private static Logger LOGGER = LogManager.getLogger(AtomGenerate.class);
	private static final String USAGE = "Run this class with the following arguments:\n" +
            "\t\t--atomConf: [REQUIRED] file for ATOM simulation configuration\n" +
			"\t\t--outputConf: [REQUIRED] file for Output configurations (file, kafka, others, ...)\n" +
			"\t\t--delayConf: [OPTIONAL] file for delay configurations if there is delay added to the simulation\n" +
			"\t\t--kafkaConf: [OPTIONAL] file for KAFKA configuration if you are using Kafka as an output\n" +
            "\t\t--replayFromFile: [OPTIONAL] file from which the simulation will be replayed if you choose to replay an existing simulation";

	// Command Line arguments names
	private static final String ATOM_CONF = "atomConf";
	private static final String KAFKA_CONF = "kafkaConf";
	private static final String DELAY_CONF = "delayConf";
	private static final String OUTPUT_CONF = "outputConf";
	private static final String REPLAY_FROM_FILE = "replayFromFile";

	// Configuration related attributes
	private static AtomSimulationConfiguration atomConf;
	private static KafkaConfiguration kafkaConf;
	private static OutputSimulationConfiguration outputConf;
	private static DelaySimulationConfiguration delayConf;
	private static CommandLine commandLine;

	// Simulation attributes
	private static Simulation sim;
	private static List<String> orderBooks;
	private static List<String> agents;
	private static v13.Logger logger = null;


	// Main configuration for Atom
	public static void main(String args[]) throws IOException {
		// Used to calculate how much time the simulation takes
		long startTime = System.currentTimeMillis();

		// Instantiate the simulation object
		LOGGER.debug("Instantiate the ATOM simulation");
		sim = new MonothreadedSimulation();

		// Loading arguments from command line
        LOGGER.debug("Loading command line arguments");
        try {
            commandLine = createCommandLine(args);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new InjectLayerException(USAGE);
        }


		// Loading properties from configuration files
		LOGGER.debug("Loading all configurations");
		setUpSimulationConfigurations();
		setUpOutputConfigurations();
		setUpDelayConfigurations();
		setUpKafkaConfigurations();

		try {
			setUpSimulationLogger();

			// Set Agents and Orderbooks
			setAgents();
			setOrderbooks();

			boolean isReplaySimulation = commandLine.hasOption(REPLAY_FROM_FILE);
			if (isReplaySimulation) {
				replaySimulation();
			}
			else {
				launchSimulation();
			}
		} finally {
			// Close simulation
			closeSimulation();
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		LOGGER.debug("Elapsed time: " + estimatedTime / 1000 + "s");
	}

	private static void replaySimulation() throws IOException {
		String replayFromFile = commandLine.getOptionValue(REPLAY_FROM_FILE);
		LOGGER.debug("Replaying simulation from " + replayFromFile);
		Replay replay = new AtomReplay(replayFromFile, atomConf);
		replay.sim = sim; // we use our custom logger within the replay simulation
		replay.go();
	}

	private static void closeSimulation() {
		LOGGER.debug("Closing simulation");
		sim.market.close();
		if (logger instanceof AtomLogger)
			((AtomLogger) logger).closeInjectors();
		LOGGER.debug("Simulation closed");
	}

	private static void launchSimulation() {
		LOGGER.debug("Launching simulation");
		sim.run(
				Day.createEuroNEXT(atomConf.getTickOpening(), atomConf.getTickContinuous(), atomConf.getTickClosing()),
				atomConf.getDays()
		);
	}

	private static void setUpSimulationLogger() {
		// Create the custom simulation logger with outputs defined in the configuration
		LOGGER.debug("Adding wanted injectors and creating custom logger");
		logger = instantiateLoggerWithInjectors();
		LOGGER.debug("Attaching custom logger to the simulation");
		sim.setLogger(logger);
	}

	private static v13.Logger instantiateLoggerWithInjectors() {
		List<AtomDataInjector> injectors = new ArrayList<>();

		if (outputConf.isOutKafka()) {
			injectors.add(new KafkaInjector(kafkaConf));
			LOGGER.debug("KafkaInjector added");
		}
		if (outputConf.isOutFile()) {
			injectors.add(new FileInjector(outputConf));
			LOGGER.debug("FileInjector added");
		}

		if (injectors.isEmpty())
			throw new InjectLayerException("No output define");

		if (commandLine.hasOption(DELAY_CONF)) {
			LOGGER.debug("Creating out of order custom logger");
			AtomLoggerWithDelay atomLoggerWithDelay = new AtomLoggerWithDelay(atomConf, injectors.toArray(new AtomDataInjector[injectors.size()]));
			atomLoggerWithDelay.setUpOutOfOrderDelay(delayConf);
			return atomLoggerWithDelay;
		}
		else {
			LOGGER.debug("Creating classic custom logger");
			return new AtomLogger(atomConf, injectors.toArray(new AtomDataInjector[injectors.size()]));
		}
	}


	private static void setUpSimulationConfigurations() {
		// ATOM Conf is required
		atomConf = new AtomSimulationConfiguration(commandLine.getOptionValue(ATOM_CONF));
	}

	private static void setUpOutputConfigurations() {
		// Output conf is required
		outputConf = new OutputSimulationConfiguration(commandLine.getOptionValue(OUTPUT_CONF));
	}

	private static void setUpDelayConfigurations() {
		// Output conf is required
		if (commandLine.hasOption(DELAY_CONF)) {
			LOGGER.debug("Creating Delay configuration");
			delayConf = new DelaySimulationConfiguration(commandLine.getOptionValue(DELAY_CONF));
		}
		else {
			LOGGER.debug("No delay is added to the simulation");
			delayConf = null;
		}
	}

	private static void setUpKafkaConfigurations() {
		// KafkaConf is not required
		if (outputConf == null)
			throw new InjectLayerException("Output configuration must be set");
		kafkaConf = outputConf.isOutKafka()
				? new KafkaConfiguration(commandLine.getOptionValue(KAFKA_CONF))
				: null;
	}

	private static void setOrderbooks() {
		orderBooks = atomConf.getOrderBooks();
		if (orderBooks.isEmpty()) {
			LOGGER.error("Orderbooks not set");
			throw new InjectLayerException("Orderbooks not set");
		}

		// Create Order book to MarketMaker depending properties
		boolean marketmaker = atomConf.isMarketMarker();
		int marketmakerQuantity = marketmaker ? atomConf.getMarketMakerQuantity() : 0;
		for (int i = 0; i < orderBooks.size(); i++) {
			if (marketmaker) {
				sim.addNewMarketMaker(orderBooks.get(i) + "" + ((i % marketmakerQuantity) + 1));
			}
			sim.addNewOrderBook(orderBooks.get(i));
		}
	}


	private static void setAgents() {
		agents = atomConf.getAgents();
		if (agents.isEmpty()) {
			LOGGER.error("Agents not set");
			throw new InjectLayerException("Agents not set");
		}
		for (String agent : agents) {
			sim.addNewAgent(new ZIT(agent, atomConf.getAgentCash(), atomConf
					.getAgentMinPrice(), atomConf.getAgentMaxPrice(), atomConf
					.getAgentMinQuantity(), atomConf.getAgentMaxQuantity()));
		}
	}
	private static CommandLine createCommandLine(String[] args) throws ParseException {
		Option atomConfPath = Option.builder()
				.argName(ATOM_CONF).longOpt(ATOM_CONF).desc("Path to the file containing the ATOM simulation parameters")
				.hasArg().required(true).build();
		Option outputConfPath = Option.builder()
				.argName(OUTPUT_CONF).longOpt(OUTPUT_CONF).desc("Path to the file containing the output parameters")
				.hasArg().required(true).build();
		Option delayConfPath = Option.builder()
				.argName(DELAY_CONF).longOpt(DELAY_CONF).desc("Path to the file containing the delay parameters")
				.hasArg().required(false).build();
		Option kafkaConfPath = Option.builder()
				.argName(KAFKA_CONF).longOpt(KAFKA_CONF).desc("Path to the file containing the Kafka parameters")
				.hasArg().required(false).build();
		Option replayFromFile = Option.builder()
				.argName(REPLAY_FROM_FILE).longOpt(REPLAY_FROM_FILE).desc("Path to the file containing the simulation to replay")
				.hasArg().required(false).build();
		return CommandLineArgumentsParser.createCommandLine(args, atomConfPath, outputConfPath, delayConfPath, kafkaConfPath, replayFromFile);
	}
}