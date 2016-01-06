package com.finaxys.atom;

import com.finaxys.kafka.KafkaInjector;
import com.finaxys.utils.AtomConfiguration;
import com.finaxys.utils.InjectLayerException;
import org.apache.log4j.Logger;
import v13.Day;
import v13.MonothreadedSimulation;
import v13.Simulation;
import v13.agents.ZIT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AtomGenerate {

	private static Logger LOGGER = Logger.getLogger(AtomGenerate.class);

	// Static informations
	static private List<String> orderBooks;
	static private List<String> agents;

	private static v13.Logger logger = null;
	private static AtomConfiguration atomConf;

	// Main configuration for Atom
	public static void main(String args[]) throws IOException {
		// Loading properties
		try {
			getConfiguration();
		} catch (InjectLayerException e) {
			LOGGER.error("Could not load properties", e);
			return;
		}

		List<String> parseArgs = Arrays.asList(args);

		// How long
		long startTime = System.currentTimeMillis();

		// Create simulator with custom logger

		List<AtomDataInjector> injectors = new ArrayList<AtomDataInjector>();
		try {
            if (parseArgs.contains("-kafka") || atomConf.isOutKafka()) {
                injectors.add(new KafkaInjector(atomConf));
            }

		} catch (Exception e) {
			LOGGER.error("Could not instantiate logger", e);
			return;
		}
		if (injectors.isEmpty()) {
			throw new InjectLayerException("No output define");
		}

		logger = new AtomLogger(atomConf,
				injectors.toArray(new AtomDataInjector[injectors.size()]));
		final Simulation sim = new MonothreadedSimulation();
		sim.setLogger(logger);

		// sim.setLogger(new FileLogger(System.getProperty("atom.output.file",
		// "dump")));

		LOGGER.info("Setting up agents and orderbooks");

		// Create Agents and Order book to MarketMaker depending properties
		final boolean marketmaker = atomConf.isMarketMarker();
		final int marketmakerQuantity = marketmaker ? atomConf
				.getMarketMakerQuantity() : 0;

		for (String agent : agents) {
			sim.addNewAgent(new ZIT(agent, atomConf.getAgentCash(), atomConf
					.getAgentMinPrice(), atomConf.getAgentMaxPrice(), atomConf
					.getAgentMinQuantity(), atomConf.getAgentMaxQuantity()));
		}
		for (int i = 0; i < orderBooks.size(); i++) {
			if (marketmaker) {
				sim.addNewMarketMaker(orderBooks.get(i) + ""
						+ ((i % marketmakerQuantity) + 1));
			}
			sim.addNewOrderBook(orderBooks.get(i));
		}

		LOGGER.info("Launching simulation");

		sim.run(Day.createEuroNEXT(atomConf.getTickOpening(),
                        atomConf.getTickContinuous(), atomConf.getTickClosing()),
				atomConf.getDays());

		LOGGER.info("Closing up");

		sim.market.close();

		if (logger instanceof AtomLogger) {
			try {
				((AtomLogger) logger).close();
			} catch (Exception e) {
				LOGGER.error("Could not close logger", e);
				return;
			}
		}

		long estimatedTime = System.currentTimeMillis() - startTime;
		LOGGER.info("Elapsed time: " + estimatedTime / 1000 + "s");
	}

	private static void getConfiguration() {

		atomConf = AtomConfiguration.getInstance();

		// Get agents & orderbooks
		agents = atomConf.getAgents();
		orderBooks = atomConf.getOrderBooks();

		if (agents.isEmpty() || orderBooks.isEmpty()) {
			LOGGER.error("Agents/Orderbooks not set");
			throw new InjectLayerException("agents or orderbooks not set");
		}
	}
}