package com.finaxys.streamintegrator.model;

public class OrderByDir {


    private String dir ; // nom de l'Agent
    private Integer quty ;// le quty de l'agent

    public OrderByDir(String dir, Integer quty) {
        this.dir = dir;
        this.quty = quty;
    }

    public OrderByDir() {

    }

    public String getdir() {
        return dir;
    }

    public void setdir(String dir) {
        this.dir = dir;
    }

    public Integer getquty() {
        return quty;
    }

    public void setquty(Integer quty) {
        this.quty = quty;
    }

    @Override
    public String toString() {
        return "qutyByAgent{" +
                "dir='" + dir + '\'' +
                ", quty=" + quty +
                '}';
    }

    public static OrderByDir fromString(String string) {
        String[] split = string.split(";");
        return new OrderByDir(split[0],
                Math.abs(Integer.parseInt(split[1]))
        );
    }

    public String toStringKafka(){
        return this.dir+";"+this.quty;

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
        OrderByDir other = (OrderByDir) obj;
        return this.dir.equals(other.getdir()) && this.quty.equals(other.getquty()) ;
    }
}
