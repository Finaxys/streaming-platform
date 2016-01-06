package com.finaxys.atom;

import com.finaxys.utils.InjectLayerException;
import v13.Day;
import v13.Order;
import v13.OrderBook;
import v13.PriceRecord;
import v13.agents.Agent;

import java.util.Collection;

public interface AtomDataInjector {

    void closeOutput() throws InjectLayerException;

    void createOutput() throws InjectLayerException;

    void sendAgent(long ts, Agent a, Order o, PriceRecord pr)
            throws InjectLayerException;

    void sendPriceRecord(long ts, PriceRecord pr, long bestAskPrice,
                                long bestBidPrice) throws InjectLayerException;

    void sendOrder(long ts, Order o) throws InjectLayerException;

    void sendTick(long ts, Day day, Collection<OrderBook> orderbooks)
            throws InjectLayerException;

    void sendDay(long ts, int nbDays, Collection<OrderBook> orderbooks)
            throws InjectLayerException;

    void sendExec(long ts, Order o) throws InjectLayerException;

}
