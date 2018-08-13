package com.finaxys.streamintegrator.model;

public class PriceByCat {

    private String obname; // nom de l'order
    private Integer price;// le price de l'order

    public PriceByCat(String obname, Integer price) {
        this.obname = obname;
        this.price = price;
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

    @Override
    public String toString() {
        return "priceByObname{" +
                "obname='" + obname + '\'' +
                ", price=" + price +
                '}';
    }

    public static PriceByCat fromString(String string) {
        String[] split = string.split(";");
        return new PriceByCat(split[0],
                Math.abs(Integer.parseInt(split[1]))
        );
    }

    public String toStringKafka(){
        return this.obname +";"+this.price;

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
        return this.obname.equals(other.getdir()) && this.price.equals(other.getquty()) ;
    }
}
