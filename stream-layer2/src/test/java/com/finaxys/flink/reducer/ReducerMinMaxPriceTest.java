package com.finaxys.flink.reducer;

import com.finaxys.flink.function.reducer.ReduceMinMaxPrice;
import org.apache.flink.api.java.tuple.Tuple2;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReducerMinMaxPriceTest {

    @Test
    public void testSum() throws Exception {
        // instantiate your function
        ReduceMinMaxPrice reduceMinMaxPrice = new ReduceMinMaxPrice();
        Tuple2<Integer, Integer> bestBid1 = new Tuple2<>(12,15);
        Tuple2<Integer, Integer> bestBid2 = new Tuple2<>(1,10);
        Tuple2<String, Tuple2<Integer, Integer>> t1 = new Tuple2<>("DD",bestBid1);
        Tuple2<String, Tuple2<Integer, Integer>> t2 =  new Tuple2<>("DD",bestBid2);

        Tuple2<String, Tuple2<Integer, Integer>> result = reduceMinMaxPrice.reduce(t1,t2);
        Tuple2<Integer, Integer> resultBestBid = result.getField(1);


        // call the methods that you have implemented
        assertEquals("DD", result.getField(0));
        assertEquals(1, (int)resultBestBid.getField(0));
        assertEquals(15, (int)resultBestBid.getField(1));
    }
}
