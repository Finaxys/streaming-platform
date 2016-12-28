package com.finaxys.loging;

import org.apache.logging.log4j.*;
import v13.*;
import v13.agents.Agent;


/**
 * @Author raphael on 20/12/2016.
 *
 * Class that construct logs similar to the logs ATOM build by default
 *
 * Differences between ATOM logs and AtomBasicLogBuilder logs
 *      - Exec log
 *          - ATOM version      "Exec";agentSenderName-orderExtId
 *          - Modified version  "Exec";agentSenderName;orderExtId
 *      - Day log (";" at the end in ATOM version)
 *          - ATOM version      "Day";numDay;obName;firstFixedPrice;lowestPrice;highestPrice;lastFixedPrice;nbTotalFixedPrice;
 *          - Modified version  "Day";numDay;obName;firstFixedPrice;lowestPrice;highestPrice;lastFixedPrice;nbTotalFixedPrice
 */
public class AtomBasicLogBuilder {

    private static org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(AtomBasicLogBuilder.class);


    /**
     * Constructs an Order log
     *
     * Order is an Abstract class and there is different types of Order
     *      - General Order     The abstract class that gives general structure
     *      - CancelOrder       Order to cancel another order
     *      - IcebergOrder      Large order in terms of quantity hidden in small parts (small orders).
     *                          See http://www.investopedia.com/terms/i/icebergorder.asp
     *      - LimitOrder        Buy or sell at a specified price or better
     *      - MarketOrder       Buy or sell immediately at the best available current price
     *      - UpdateOrder       Allows to update the quantity of an existing order
     *
     * Semantic of Orders logs:
     *      - General Order     "Order";obName;senderName;extId;type
     *      - CancelOrder       "Order";obName;senderName;extId;type;extIdToKill
     *      - IcebergOrder      "Order";obName;senderName;extId;type;dir;price;part;initQuty;valid
     *      - LimitOrder        "Order";obName;senderName;extId;type;dir;price;quty;valid
     *      - MarketOrder       "Order";obName;senderName;extId;type;dir;quty;valid
     *      - UpdateOrder       "Order";obName;senderName;extId;type;extIdToUpdate
     *
     * Log fields meaning
     *      - Order: The log type (here "Order")
     *      - obName: Order book name (=symbol)
     *      - senderName: The name of the Agent that send the order, "UNKNOWN" by default
     *      - extId: Order ID (used to cancel or updated it, for instance)
     *      - extIdToKill: ID of the order to kill for a CancelOrder
     *      - extIdToUpdate: ID of the order to update for an UpdateOrder
     *      - type: Order type (L=LimitOrder, C=CancelOrder, I=IcebergOrder, M=MarketOrder, U=UpdateOrder)
     *      - dir: Order direction (A=ASK (selling) and B=BUY (buying))
     *      - part: For IcebergOrder, the quantity of smaller orders to send to complete the all IcebergOrder
     *      - initQuty: For IcebergOrder, the initial quantity of the order
     *      - quty: Quantity to sell/buy
     *      - valid: Order validity
     * @param o the Order to log
     * @return the Order log
     */
    public String order(Order o) {
        return o.toString();
    }


    /**
     * Constructs an Exec log
     *
     * Semantic of Exec log:
     *      - "Exec";agentSenderName;orderExtId
     *
     * Log fields meaning:
     *      - Exec: The log type (here "Exec")
     *      - agentSenderName: name of the Agent that send the order
     *      - orderExtId: Order ID
     * @param o the Order specific to the Exec
     * @return the Exec lof
     */
    public String exec(Order o) {
        return new StringBuilder()
                .append("Exec;")
                .append(o.sender.name).append(";")
                .append(o.extId)
                .toString();
    }


    /**
     * Constructs an Agent log.
     * An agent log occurs when an order is issued and match with another order.
     *
     * Semantic of Agent log:
     *      - "Agent";name;cash;obName;nbInvest;lastFixedPrice
     *
     * Log fields meaning:
     *      - Agent: The log type (here "Agent")
     *      - name: Name of the Agent
     *      - cash: Amount of the agent cash
     *      - obName: OrderBook name
     *      - nbInvest: Quantity of the OrderBook owned by the Agent
     *      - lastFixedPrice: Last fixed price for the OrderBook
     * @param a The agent
     * @param o The Order
     * @param pr The PriceRecord
     * @return The Agent log
     */
    public String agent(Agent a, Order o, PriceRecord pr) {
        return new StringBuilder().append("Agent;").append(a.name).append(";")
                        .append(a.cash).append(";")
                        .append(o.obName).append(";")
                        .append(a.getInvest(o.obName)).append(";")
                        .append((pr != null ? Long.valueOf(pr.price): "none"))
                        .toString();
    }

    /**
     * Constructs a Price log.
     * A price log occurs when an order is issued and match with another order.
     *
     * Semantic of Price log:
     *      - "Price";obName;price;executedQuty;dir;askOrderID;bidOrderID;bestAskPrice;bestBidPrice
     *
     * Log fields meaning:
     *      - Price: The log type (here "Price")
     *      - obName: OrderBook name
     *      - price: the price generated by the match of the two orders
     *      - executedQuty: quantity exchanged
     *      - dir: direction generated by the price
     *      - askOrderID: ID of the ask order (=agentSenderName-orderID)
     *      - bidOrderID: ID of the bid order (=agentSenderName-orderID)
     *      - bestAskPrice: best current buy price
     *      - bestBidPrice: best current sell price
     *
     * @param pr the PriceRecord
     * @param bestAskPrice the best current buy price
     * @param bestBidPrice the best current sell price
     * @return the Price log
     */
    public String price(PriceRecord pr, long bestAskPrice, long bestBidPrice) {
        return new StringBuilder().append("Price;").append(pr).append(";")
                        .append(bestAskPrice).append(";")
                        .append(bestBidPrice)
                        .toString();
    }

    /**
     * Constructs a Tick log.
     *
     * Semantic of Tick log:
     *      - "Tick";numTick;obName;bestAskPrice;bestBidPrice;lastPrice
     *
     * Log fields meaning:
     *      - Tick: The log type (here "Tick")
     *      - numTick: the tick number in the current period of the day
     *      - obName: the OrderBook name
     *      - bestAskPrice: best buy price for the OrderBook
     *      - bestBidPrice: best sell price for the OrderBook
     *      - lastPrice: last fixed price for the OrderBook
     *
     * @param day the Day in which the Tick is happening
     * @param ob an OrderBook
     * @return the Tick log
     */
    public String tick(Day day, OrderBook ob) {
        return new StringBuilder()
                .append("Tick;").append(day.currentPeriod().currentTick()).append(";")
                .append(ob.obName).append(";" + (ob.ask.size() > 0?Long.valueOf(((LimitOrder)ob.ask.first()).price):"0"))
                .append(";").append(ob.bid.size() > 0?Long.valueOf(((LimitOrder)ob.bid.first()).price):"0")
                .append(";").append(ob.lastFixedPrice != null?Long.valueOf(ob.lastFixedPrice.price):"0").append(";")
                .toString();
    }


    /**
     * Constructs a Day log.
     *
     * Semantic of Day log:
     *      - "Day";numDay;obName;firstFixedPrice;lowestPrice;highestPrice;lastFixedPrice;nbTotalFixedPrice
     *
     * Log fields meaning:
     *      - Day: The log type (here "Day")
     *      - numDay: the current day number in the simulation
     *      - obName: the OrderBook name
     *      - firstFixedPrice: the first price fixed during the day
     *      - lowestPrice: the lowest price of the day
     *      - highestPrice: the highest price of the day
     *      - lastFixedPrice: the last price fixed during the day
     *      - nbTotalFixedPrice: the total number of prices fixed
     *
     * @param numOfDay the current day number in the simulation
     * @param ob an OrderBook
     * @return the Day log
     */
    public String day(int numOfDay, OrderBook ob) {
        return new StringBuilder()
                .append("Day").append(";")
                .append(numOfDay).append(";")
                .append(ob.obName).append(";")
                .append(ob.firstPriceOfDay).append(";")
                .append(ob.lowestPriceOfDay).append(";")
                .append(ob.highestPriceOfDay).append(";")
                .append(ob.lastPriceOfDay).append(";")
                .append(ob.numberOfPricesFixed)
                .toString();
    }
}
