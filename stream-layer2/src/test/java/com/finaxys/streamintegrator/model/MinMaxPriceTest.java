package com.finaxys.streamintegrator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinMaxPriceTest {

    @Test
    public void toStringKafkaTest() throws Exception {
        String minMax = "DD;1;19";
        MinMaxPrice minMaxPrice = new MinMaxPrice("DD",1,19);
        assertEquals(minMax,minMaxPrice.toStringKafka());
    }
}
