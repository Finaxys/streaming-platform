package com.finaxys.streamintegrator.model;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MinMaxPriceTest {

    @Test
    public void testToStringKafka() throws Exception {
        String minMax = "DD;1;19";
        MinMaxPrice minMaxPrice = new MinMaxPrice("DD",1,19);
        assertEquals(minMax,minMaxPrice.toStringKafka());
    }
}
