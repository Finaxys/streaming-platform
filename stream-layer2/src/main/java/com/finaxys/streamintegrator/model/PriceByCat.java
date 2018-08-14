package com.finaxys.streamintegrator.model;

public class PriceByCat {

    private String obname; // nom de l'order
    private Integer price;// le price de l'order
    private Integer executedQuty; // quantity executed

    public PriceByCat(String obname, Integer price, Integer executedQuty) {
        this.obname = obname;
        this.price = price;
        this.executedQuty = executedQuty;
    }

    public PriceByCat() {

    }

    public String getdir() {
        return obname;
    }

    public void setdir(String dir) {
        this.obname = dir;
    }

    public Integer getquty() {
        return price;
    }

    public void setquty(Integer quty) {
        this.price = quty;
    }


    public Integer getExecutedQuty() { return executedQuty; }

    public void setExecutedQuty(Integer executedQuty) { this.executedQuty = executedQuty; }

    @Override
    public String toString() {
        return "priceByObname{" +
                "obname='" + obname + '\'' +
                ", price=" + price +
                ", executedQuty="+ executedQuty +
                '}';
    }

    public static PriceByCat fromString(String string) {
        String[] split = string.split(";");
        return new PriceByCat(split[0],
                Math.abs(Integer.parseInt(split[1])),
                Math.abs(Integer.parseInt(split[2]))
        );
    }

    public String toStringKafka(){
        return this.obname +";"+this.price+";"+ this.executedQuty;

    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PriceByCat other = (PriceByCat) obj;
        return this.obname.equals(other.getdir()) && this.price.equals(other.getquty()) && this.executedQuty.equals(other.getExecutedQuty());
    }
}
