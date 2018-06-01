package com.finaxys.flink.model;

import org.apache.flink.table.expressions.In;

public class MinMaxPrice {
    private String obName ; //MSFT;
    private Integer bestAskPrice; // : best current buy price
    private  Integer bestBidPrice ;  // : best current sell price

    public MinMaxPrice(String obName, Integer bestAskPrice, Integer bestBidPrice) {
        this.obName = obName;
        this.bestAskPrice = bestAskPrice;
        this.bestBidPrice = bestBidPrice;
    }

    public MinMaxPrice() {

    }

    public String getObName() {
        return obName;
    }

    public Integer getBestAskPrice() {
        return bestAskPrice;
    }

    public Integer getBestBidPrice() {
        return bestBidPrice;
    }

    public void setObName(String obName) {
        this.obName = obName;
    }

    @Override
    public String toString() {
        return "MinMaxPrice{" +
                "obName='" + obName + '\'' +
                ", bestAskPrice=" + bestAskPrice +
                ", bestBidPrice=" + bestBidPrice +
                '}';
    }

    public String toStringKafka(){
        return this.obName+";"+this.bestAskPrice+";"+this.bestBidPrice;

    }

    public static MinMaxPrice fromString(String string) {
        String[] split = string.split(";");
        return new MinMaxPrice(split[0],
                Math.abs(Integer.parseInt(split[1])),
                Math.abs(Integer.parseInt(split[2]))
        );
    }
}
