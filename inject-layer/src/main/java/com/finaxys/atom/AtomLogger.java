package com.finaxys.atom;

import com.finaxys.utils.AtomConfiguration;
import com.finaxys.utils.TimeStampBuilder;
import v13.*;
import v13.agents.Agent;

public class AtomLogger extends Logger {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AtomLogger.class);

    private AtomDataInjector injectors[];
    private TimeStampBuilder tsb;
    private AtomConfiguration conf;

    public AtomLogger(AtomConfiguration conf, AtomDataInjector... injectors) {
        super();
        this.conf = conf;
        this.injectors = injectors;
        init();
    }

    public void init() {
        LOGGER.info("Initializing AtomLogger");
        tsb = new TimeStampBuilder(conf.getTsbDateBegin(), conf.getTsbOpenHour(), conf.getTsbCloseHour(), conf.getTsbNbTickMax(), conf.getNbAgents(), conf.getNbOrderBooks());
        tsb.init();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].createOutput();
        }
    }


    @Override
    public void agent(Agent a, Order o, PriceRecord pr) {
        super.agent(a, o, pr);
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendAgent(ts, a, o, pr);
        }
    }


    @Override
    public void exec(Order o) {
        super.exec(o);
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendExec(ts, o);
        }
    }

    @Override
    public void order(Order o) {
        super.order(o);
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendOrder(ts, o);
        }
    }

    @Override
    public void price(PriceRecord pr, long bestAskPrice, long bestBidPrice) {
        super.price(pr, bestAskPrice, bestBidPrice);
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendPriceRecord(ts, pr, bestAskPrice, bestBidPrice);
        }
    }


    @Override
    public void day(int nbDays, java.util.Collection<OrderBook> orderbooks) {
        super.day(nbDays, orderbooks);

        tsb.setCurrentDay(nbDays);
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendDay(ts, nbDays, orderbooks);
        }
    }


    @Override
    public void tick(Day day, java.util.Collection<OrderBook> orderbooks) {
        super.tick(day, orderbooks);

        tsb.setCurrentTick(day.currentTick());
        tsb.setTimeStamp(tsb.baseTimeStampForCurrentTick());
        long ts = tsb.nextTimeStamp();
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].sendTick(ts, day, orderbooks);
        }

    }

    public void close() throws Exception {
        for (int i = 0; i < injectors.length; i++) {
            injectors[i].closeOutput();
        }
    }

}