package com.finaxys.flink.model;



import java.io.Serializable;

public class Price implements  Serializable{


    //Price;CSCO;15;55;p;noname;noname;-1;15;1442214015810

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String obName ; //MSFT;
    private Integer price ; // the price generated by the match of the two orders  /	16;
    private Integer executedQuty  ; // quantity exchanged  	10;
    private String dir ; // direction generated by the price  B;
    private String askOrderID ; //: ID of the ask order (=agentSenderName-orderID)
    private String bidOrderI ; //  ID of the bid order (=agentSenderName-orderID)
    private Integer bestAskPrice; // : best current buy price
    private  Integer bestBidPrice ;  // : best current sell price

    public Price(String obName, Integer price, Integer executedQuty, String dir, String askOrderID,
                     String bidOrderI, Integer bestAskPrice, Integer bestBidPrice) {
        this.obName = obName;
        this.price = price;
        this.executedQuty = executedQuty;
        this.dir = dir;
        this.askOrderID = askOrderID;
        this.bidOrderI = bidOrderI;
        this.bestAskPrice = bestAskPrice;
        this.bestBidPrice = bestBidPrice;
    }

    public Price() {
        //empty
    }

    public String getObName() {
        return obName;
    }
    public void setObName(String obName) {
        this.obName = obName;
    }
    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }
    public Integer getExecutedQuty() {
        return executedQuty;
    }
    public void setExecutedQuty(Integer executedQuty) {
        this.executedQuty = executedQuty;
    }
    public String getDir() {
        return dir;
    }
    public void setDir(String dir) {
        this.dir = dir;
    }
    public String getAskOrderID() {
        return askOrderID;
    }
    public void setAskOrderID(String askOrderID) {
        this.askOrderID = askOrderID;
    }
    public String getBidOrderI() {
        return bidOrderI;
    }
    public void setBidOrderI(String bidOrderI) {
        this.bidOrderI = bidOrderI;
    }
    public Integer getBestAskPrice() {
        return bestAskPrice;
    }
    public void setBestAskPrice(Integer bestAskPrice) {
        this.bestAskPrice = bestAskPrice;
    }
    public Integer getBestBidPrice() {
        return bestBidPrice;
    }
    public void setBestBidPrice(Integer bestBidPrice) {
        this.bestBidPrice = bestBidPrice;
    }

    @Override
    public String toString() {
        return "AtomPrice [obName=" + obName + ", price=" + price + ", executedQuty="
                + executedQuty + ", dir=" + dir + ", askOrderID=" + askOrderID + ", bidOrderI=" + bidOrderI
                + ", bestAskPrice=" + bestAskPrice + ", bestBidPrice=" + bestBidPrice + "]";
    }

    public static Price fromString(String string) {
        String[] split = string.split(";");
        if (split[0].equals("Price"))
            return new Price(
                    split[1],
                    Math.abs(Integer.parseInt(split[2])),
                    Math.abs(Integer.parseInt(split[3])),
                    split[4],
                    split[5],
                    split[6],
                    Math.abs(Integer.parseInt(split[7])),
                    Math.abs(Integer.parseInt(split[2]))
            );
        else return null ;
    }

}