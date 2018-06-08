package com.finaxys.streamintegrator.model;

public class CashByAgent {

    private String name ; // nom de l'Agent
    private Long cash ;// le cash de l'agent

    public CashByAgent(String name, Long cash) {
        this.name = name;
        this.cash = cash;
    }

    public CashByAgent() {

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

    @Override
    public String toString() {
        return "CashByAgent{" +
                "name='" + name + '\'' +
                ", cash=" + cash +
                '}';
    }

    public static CashByAgent fromString(String string) {
        String[] split = string.split(";");
        return new CashByAgent(split[0],
                Math.abs(Long.parseLong(split[1]))
        );
    }

    public String toStringKafka(){
        return this.name+";"+this.cash;

    }
}
