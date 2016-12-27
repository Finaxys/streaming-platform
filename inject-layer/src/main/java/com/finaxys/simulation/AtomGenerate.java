package com.finaxys.simulation;

import com.finaxys.loging.AtomLogger;
import com.finaxys.loging.AtomLoggerWithDelay;
import com.finaxys.loging.injectors.AtomDataInjector;
import com.finaxys.loging.injectors.FileInjector;
import com.finaxys.loging.injectors.KafkaInjector;
import com.finaxys.utils.AtomSimulationConfiguration;
import com.finaxys.utils.InjectLayerException;
import configuration.CommandLineArgumentsParser;
import configuration.KafkaConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.cli.*;
import v13.Day;
import v13.MonothreadedSimulation;
import v13.Simulation;
import v13.agents.ZIT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AtomGenerate {

	private static Logger LOGGER = LogManager.getLogger(AtomGenerate.class);

	// Command Line arguments names
	private static final String ATOM_CONF = "atomConf";
	private static final String KAFKA_CONF = "kafkaConf";

	// Configuration related attributes
	private static AtomSimulationConfiguration atomConf;
	private static KafkaConfiguration kafkaConf;
	private static CommandLine commandLine;

	// Simulation attributes
	private static  Simulation sim;
	private static List<String> orderBooks;
	private static List<String> agents;
	private static v13.Logger logger = null;


	// Main configuration for Atom
	public static void main(String args[]) throws IOException, ParseException {
		// Used to calculate how much time the simulation takes
		long startTime = System.currentTimeMillis();

		// Instantiate the simulation object
		LOGGER.debug("Instantiate the ATOM simulation");
		sim = new MonothreadedSimulation();

		// Loading arguments from command line
		commandLine = createCommandLine(args);

		// Loading properties from configuration files
		LOGGER.debug("Loading all configurations");
		getConfiguration();

		try {
			// Create the custom simulation logger with outputs defined in the configuration
			LOGGER.debug("Adding wanted injectors and creating custom logger");
			logger = instantiateLoggerWithInjectors();
			LOGGER.debug("Attaching custom logger to the simulation");
			sim.setLogger(logger);

			// Set Agents and Orderbooks
			setAgents();
			setOrderbooks();

			// Launch simulation
			launchSimulation();
		} finally {
			// Close simulation
			closeSimulation();
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		LOGGER.debug("Elapsed time: " + estimatedTime / 1000 + "s");
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

	private static v13.Logger instantiateLoggerWithInjectors() {
		List<AtomDataInjector> injectors = new ArrayList<>();

		if (atomConf.isOutKafka()) {
			injectors.add(new KafkaInjector(kafkaConf));
			LOGGER.debug("KafkaInjector added");
		}
		if (atomConf.isOutFile()) {
			injectors.add(new FileInjector(atomConf));
			LOGGER.debug("FileInjector added");
		}

		if (injectors.isEmpty())
			throw new InjectLayerException("No output define");

		if (atomConf.isOutOfOrderEnabled()) {
			LOGGER.debug("Creating out of order custom logger");
			return new AtomLoggerWithDelay(atomConf, injectors.toArray(new AtomDataInjector[injectors.size()]));
		}
		else {
			LOGGER.debug("Creating classic custom logger");
			return new AtomLogger(atomConf, injectors.toArray(new AtomDataInjector[injectors.size()]));
		}
	}


	private static void getConfiguration() {
		// ATOM Conf is required
		atomConf = new AtomSimulationConfiguration(commandLine.getOptionValue(ATOM_CONF));

		// KafkaConf is not required
		kafkaConf = atomConf.isOutKafka()
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
				.hasArg().required().build();
		Option kafkaConfPath = Option.builder()
				.argName(KAFKA_CONF).longOpt(KAFKA_CONF).desc("Path to the file containing the Kafka parameters")
				.hasArg().required(false).build();
		return CommandLineArgumentsParser.createCommandLine(args, atomConfPath, kafkaConfPath);
	}
}