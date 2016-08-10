package com.finaxys.atom;

import com.finaxys.utils.AtomInjectConfiguration;
import com.finaxys.utils.TimeStampBuilder;
import v13.Order;
import v13.OrderBook;
import v13.PriceRecord;
import v13.Day;
import v13.agents.Agent;
import v13.Logger;


public class AtomLogger extends Logger {

    private static org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(AtomLogger.class);

    private AtomDataInjector injectors[];
    private TimeStampBuilder tsb;
    private AtomInjectConfiguration conf;

    public AtomLogger(AtomInjectConfiguration conf, AtomDataInjector... injectors) {
        super();
        this.conf = conf;
        this.injectors = injectors;
        init();
    }

    public void init() {
        LOGGER.info("Initializing AtomLogger");
        tsb = new TimeStampBuilder(
                conf.getTsbDateBegin(), conf.getTsbOpenHour(),
                conf.getTsbCloseHour(), conf.getTsbNbTickMax(),
                conf.getNbAgents(), conf.getNbOrderBooks()
        );

        for (AtomDataInjector injector : injectors) {
            injector.createOutput();
        }
    }


    @Override
    public void agent(Agent a, Order o, PriceRecord pr) {
        super.agent(a, o, pr);
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendAgent(ts, a, o, pr);
        }
    }


    @Override
    public void exec(Order o) {
        super.exec(o);
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendExec(ts, o);
        }
    }

    @Override
    public void order(Order o) {
        super.order(o);
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendOrder(ts, o);
        }
    }

    @Override
    public void price(PriceRecord pr, long bestAskPrice, long bestBidPrice) {
        super.price(pr, bestAskPrice, bestBidPrice);
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendPriceRecord(ts, pr, bestAskPrice, bestBidPrice);
        }
    }


    @Override
    public void day(int nbDays, java.util.Collection<OrderBook> orderbooks) {
        super.day(nbDays, orderbooks);

        tsb.setCurrentDay(nbDays);
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendDay(ts, nbDays, orderbooks);
        }
    }


    @Override
    public void tick(Day day, java.util.Collection<OrderBook> orderbooks) {
        super.tick(day, orderbooks);
        tsb.setCurrentTick(day.currentTick());
        tsb.setTimeStamp(tsb.computeTimestampForCurrentTick());
        long ts = tsb.nextTimeStamp();
        for (AtomDataInjector injector : injectors) {
            injector.sendTick(ts, day, orderbooks);
        }

    }

    public void close() throws Exception {
        for (AtomDataInjector injector : injectors) {
            injector.closeOutput();
        }
    }

}