package com.finaxys.streamintegrator.model;

public class Agent {

   private String name ; // nom de l'Agent
    private Long cash ;// le cash de l'agent
   private String obName ; // l'OrderBook qui vient d'être modifié (log Agent = match de deux ordres ask-bid)
    private Long nbInvest ; // quantité de l'order book donné possédée par l'Agent
    private Long lastFixedPrice ;  // dernier prix fixé pour l'order book

    public Agent(String name, Long cash, String obName, Long nbInvest, Long lastFixedPrice) {
        this.name = name;
        this.cash = cash;
        this.obName = obName;
        this.nbInvest = nbInvest;
        this.lastFixedPrice = lastFixedPrice;
    }

    public Agent() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCash() {
        return cash;
    }

    public void setCash(Long cash) {
        this.cash = cash;
    }

    public String getObName() {
        return obName;
    }

    public void setObName(String obName) {
        this.obName = obName;
    }

    public Long getNbInvest() {
        return nbInvest;
    }

    public void setNbInvest(Long nbInvest) {
        this.nbInvest = nbInvest;
    }

    public Long getLastFixedPrice() {
        return lastFixedPrice;
    }

    public void setLastFixedPrice(Long lastFixedPrice) {
        this.lastFixedPrice = lastFixedPrice;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                ", cash=" + cash +
                ", obName='" + obName + '\'' +
                ", nbInvest=" + nbInvest +
                ", lastFixedPrice=" + lastFixedPrice +
                '}';
    }


    public static Agent fromString(String string){
        String[] splitted = string.split(";");
        if(splitted[0].equals("Agent"))
            return new Agent(
                    splitted[1],
                    Math.abs(Long.parseLong(splitted[2])),
                    splitted[3],
                    Math.abs(Long.parseLong(splitted[4])),
                    Math.abs(Long.parseLong(splitted[5]))
            );
        else return null ;
    }
}
