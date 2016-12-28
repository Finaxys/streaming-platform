package com.finaxys.utils;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.Map;

public class Converter implements Serializable {

    /**
     *
     */
    private final static Logger LOGGER = LogManager.getLogger(Converter.class);
    private static final long serialVersionUID = 3142828016264704546L;
    private  Map<byte[], byte[]> cfmap;
    private static  HBaseDataTypeEncoder encoder = new HBaseDataTypeEncoder() ;
    public static byte[] colFamily ;

    public static final byte[] Q_TRACE_TYPE = Bytes.toBytes("Trace");
    public static final byte[] Q_NUM_DAY = Bytes.toBytes("NumDay");
    public static final byte[] Q_NUM_TICK = Bytes.toBytes("NumTick");
    public static final byte[] Q_BEST_BID = Bytes.toBytes("BestBid");
    public static final byte[] Q_BEST_ASK = Bytes.toBytes("BestAsk");
    public static final byte[] Q_ORDER2 = Bytes.toBytes("Order2");
    public static final byte[] Q_ORDER1 = Bytes.toBytes("Order1");
    public static final byte[] Q_DIR = Bytes.toBytes("Dir");
    public static final byte[] Q_OB_NAME = Bytes.toBytes("ObName");
    public static final byte[] Q_VALIDITY = Bytes.toBytes("Validity");
    public static final byte[] Q_QUANTITY = Bytes.toBytes("Quantity");
    public static final byte[] Q_ID = Bytes.toBytes("Id");
    public static final byte[] Q_TYPE = Bytes.toBytes("Type");
    public static final byte[] Q_EXT_ID = Bytes.toBytes("ExtId");
    public static final byte[] Q_SENDER = Bytes.toBytes("Sender");
    public static final byte[] Q_NB_PRICES_FIXED = Bytes.toBytes("NbPricesFixed");
    public static final byte[] Q_LAST_FIXED_PRICE = Bytes.toBytes("LastFixedPrice");
    public static final byte[] Q_HIGHEST_PRICE = Bytes.toBytes("HighestPrice");
    public static final byte[] Q_LOWEST_PRICE = Bytes.toBytes("LowestPrice");
    public static final byte[] Q_FIRST_FIXED_PRICE = Bytes.toBytes("FirstFixedPrice");
    public static final byte[] EXT_NUM_DAY = Bytes.toBytes("NumDay");
    public static final byte[] Q_EXT_ORDER_ID = Bytes.toBytes("OrderExtId");
    public static final byte[] Q_TIMESTAMP = Bytes.toBytes("Timestamp");
    public static final byte[] Q_DIRECTION = Bytes.toBytes("Direction");
    public static final byte[] Q_PRICE = Bytes.toBytes("Price");
    public static final byte[] Q_EXECUTED_QUANTITY = Bytes.toBytes("Executed");
    public static final byte[] Q_CASH = Bytes.toBytes("Cash");
    public static final byte[] Q_AGENT_NAME = Bytes.toBytes("AgentName");
    public static final byte[] Q_INVEST = Bytes.toBytes("Invest");

    public Converter(byte[] colFamily) {
        this.colFamily = colFamily ;
    }


    public Put convertStringToPut(String key,String value){
        String[] data = value.split(";");
        TraceType type = TraceType.valueOf(data[0]);
        Put p =new Put(Bytes.toBytes("row-"+key));
        switch (type) {
            case Agent:
                mkPutAgent(data, p);
                break;
            case Day:
                mkPutDay(data, p);
                break;
            case Order:
                mkPutOrder(data, p);
                break;
            case Exec:
                mkPutExec(data, p);
                break;
            case Price:
                mkPutPrice(data, p);
                break;
            case Tick:
                mkPutTick(data, p);
                break;
        }

        return p ;
    }

    private void mkPutAgent(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_AGENT_NAME,encoder.encodeString(data[1])) ;
        put.addColumn(colFamily,Q_CASH,encoder.encodeLong(Long.parseLong(data[2]))) ;
        put.addColumn(colFamily,Q_OB_NAME,encoder.encodeString(data[3])) ;
        put.addColumn(colFamily,Q_INVEST,encoder.encodeString(data[4])) ;
        put.addColumn(colFamily,Q_PRICE,encoder.encodeLong(Long.parseLong(data[5]))) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[6]))) ;
    }

    private void mkPutOrder(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_OB_NAME,encoder.encodeString(data[1])) ;
        put.addColumn(colFamily,Q_SENDER,encoder.encodeString(data[2])) ;
        put.addColumn(colFamily,Q_EXT_ID,encoder.encodeString(data[3])) ;
        put.addColumn(colFamily,Q_TYPE,encoder.encodeChar(data[4].charAt(0))) ;
        put.addColumn(colFamily,Q_DIRECTION,encoder.encodeChar(data[5].charAt(0))) ;
        put.addColumn(colFamily,Q_PRICE,encoder.encodeLong(Long.parseLong(data[6]))) ;
        put.addColumn(colFamily,Q_QUANTITY,encoder.encodeInt(Integer.parseInt(data[7]))) ;
        put.addColumn(colFamily,Q_PRICE,encoder.encodeLong(Long.parseLong(data[8]))) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[9]))) ;
    }

    private void mkPutDay(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_NUM_DAY,encoder.encodeInt(Integer.parseInt(data[1]))) ;
        put.addColumn(colFamily,Q_OB_NAME,encoder.encodeString(data[2])) ;
        put.addColumn(colFamily,Q_FIRST_FIXED_PRICE,encoder.encodeLong(Long.parseLong(data[3]))) ;
        put.addColumn(colFamily,Q_LOWEST_PRICE,encoder.encodeLong(Long.parseLong(data[4]))) ;
        put.addColumn(colFamily,Q_HIGHEST_PRICE,encoder.encodeLong(Long.parseLong(data[5]))) ;
        put.addColumn(colFamily,Q_LAST_FIXED_PRICE,encoder.encodeLong(Long.parseLong(data[6]))) ;
        put.addColumn(colFamily,Q_NB_PRICES_FIXED,encoder.encodeLong(Long.parseLong(data[7]))) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[8]))) ;
    }

    private void mkPutExec(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_SENDER,encoder.encodeString(data[1].split("-")[0])) ;
        put.addColumn(colFamily,Q_EXT_ID,encoder.encodeString(data[1].split("-")[1])) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[2]))) ;
    }

    private void mkPutPrice(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_OB_NAME,encoder.encodeString(data[1])) ;
        put.addColumn(colFamily,Q_PRICE,encoder.encodeLong(Long.parseLong(data[2])));
        put.addColumn(colFamily,Q_QUANTITY,encoder.encodeInt(Integer.parseInt(data[3]))) ;
        put.addColumn(colFamily,Q_DIR,encoder.encodeChar((data[4].charAt(0)))) ;
        put.addColumn(colFamily,Q_ORDER1,encoder.encodeString(data[5]));
        put.addColumn(colFamily,Q_ORDER2,encoder.encodeString(data[6])) ;
        put.addColumn(colFamily,Q_BEST_ASK,encoder.encodeLong(Long.parseLong(data[7]))) ;
        put.addColumn(colFamily,Q_BEST_BID,encoder.encodeLong(Long.parseLong(data[8]))) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[9]))) ;
    }

    private void mkPutTick(String[] data,Put put){
        put.addColumn(colFamily,Q_TRACE_TYPE,encoder.encodeString(data[0])) ;
        put.addColumn(colFamily,Q_NUM_TICK,encoder.encodeInt(Integer.parseInt(data[1]))) ;
        put.addColumn(colFamily,Q_OB_NAME,encoder.encodeString(data[2])) ;
        put.addColumn(colFamily,Q_BEST_ASK,encoder.encodeLong(Long.parseLong(data[3]))) ;
        put.addColumn(colFamily,Q_BEST_BID,encoder.encodeLong(Long.parseLong(data[4]))) ;
        put.addColumn(colFamily,Q_LAST_FIXED_PRICE,encoder.encodeLong(Long.parseLong(data[5]))) ;
        put.addColumn(colFamily,Q_TIMESTAMP,encoder.encodeLong(Long.parseLong(data[6]))) ;
    }

    private String getString(String col) {
        try {
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return encoder.decodeString(bArr);
        }
        catch (Exception e){
            return null ;
        }
    }

    private Number getDouble(String col) {
        try {
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return (bArr != null) ? encoder.decodeDouble(bArr) : null;
        }
        catch(Exception e){
            return 0 ;
        }
    }

    private Integer getInteger(String col) {
        try{
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return (bArr != null) ? encoder.decodeInt(bArr): null;
        }
        catch(Exception e){
            return null ;
        }
    }

    private Long getLong(String col) {
        try {
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return (bArr != null) ? encoder.decodeLong(bArr) : null;
        }
        catch (Exception e){
            return null ;
        }
    }

    private String getChar(String col) {
        try {
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return (bArr != null) ? encoder.decodeChar(bArr)+"": null;
        }
        catch(Exception e){
            return null ;
        }
    }

    private Boolean getBoolean(String col) {
        try{
            byte[] bArr = cfmap.get(Bytes.toBytes(col));
            return (bArr != null) ? encoder.decodeBoolean(bArr): null;
        }
        catch (Exception e){
            return null ;
        }
    }
}
