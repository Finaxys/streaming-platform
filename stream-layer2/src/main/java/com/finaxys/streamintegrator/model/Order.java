package com.finaxys.streamintegrator.model;

public class Order {


    String obName; // Order book name (=symbol)
    String senderName; // The name of the Agent that send the order, "UNKNOWN" by default
    Integer extId; //Order ID (used to cancel or updated it, for instance)
    String type; // Order type (L=LimitOrder, C=CancelOrder, I=IcebergOrder, M=MarketOrder, U=UpdateOrder)
    String dir;  //Order direction (A=ASK (selling) and B=BUY (buying))
    Long part; // For IcebergOrder, the quantity of smaller orders to send to complete the all IcebergOrder
    Integer quty; // Quantity to sell/buy
    Integer initQuty; // For IcebergOrder, the initial quantity of the order
    Long valid; // Order validity


    public Order(String obName, String senderName, Integer extId, String type, String dir, Long part, Integer quty, Integer initQuty, Long valid) {
        this.obName = obName;
        this.senderName = senderName;
        this.extId = extId;
        this.type = type;
        this.dir = dir;
        this.part = part;
        this.quty = quty;
        this.initQuty = initQuty;
        this.valid = valid;
    }

    public Order() {

    }

    public String getObName() {
        return obName;
    }

    public void setObName(String obName) {
        this.obName = obName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Integer getExtId() {
        return extId;
    }

    public void setExtId(Integer extId) {
        this.extId = extId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public Long getPart() {
        return part;
    }

    public void setPart(Long part) {
        this.part = part;
    }

    public Integer getInitQuty() {
        return initQuty;
    }

    public void setInitQuty(Integer initQuty) {
        this.initQuty = initQuty;
    }

    public Integer getQuty() {
        return quty;
    }

    public void setQuty(Integer quty) {
        quty = quty;
    }

    public Long getValid() {
        return valid;
    }

    public void setValid(Long valid) {
        this.valid = valid;
    }


    @Override
    public String toString() {
        return "Order{" +
                "obName='" + obName + '\'' +
                ", senderName='" + senderName + '\'' +
                ", extId=" + extId +
                ", type='" + type + '\'' +
                ", dir='" + dir + '\'' +
                ", part=" + part +
                ", quty=" + quty +
                ", initQuty=" + initQuty +
                ", valid=" + valid +
                '}';
    }

    public static Order fromString(String string) {
        String[] splitted = string.split(";");
        if (splitted[0].equals("Order"))
            return new Order(
                    splitted[1],
                    splitted[2],
                    Math.abs(Integer.parseInt(splitted[3])),
                    splitted[4],
                    splitted[5],
                    Math.abs(Long.parseLong(splitted[6])),
                    Math.abs(Integer.parseInt(splitted[7])),
                    Math.abs(Integer.parseInt(splitted[8])),
                    Math.abs(Long.parseLong(splitted[9]))
            );
        else return null;
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
        Order other = (Order) obj;
        return this.obName.equals(other.getObName()) &&
                this.senderName.equals(other.getSenderName()) &&
                this.extId.equals(other.getExtId()) &&
                this.type.equals(other.getType()) &&
                this.dir.equals(other.getDir()) &&
                this.part.equals(other.getPart()) &&
                this.initQuty.equals(other.getInitQuty()) &&
                this.quty.equals(other.quty) &&
                this.valid.equals(other.valid)
                ;
    }
}
