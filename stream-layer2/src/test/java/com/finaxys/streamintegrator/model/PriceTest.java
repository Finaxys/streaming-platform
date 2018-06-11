package com.finaxys.streamintegrator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceTest {



    @Test
    public void fromStringTest() throws Exception {
        String priceString = "Price;DD;8;9;A;Lucy-10381;Jerome-10231;3;10;1526725026492";
        Price price = new Price("ADD",8,9,"A","Lucy-10381","Jerome-10231",3,10);
        Price price2 = Price.fromString(priceString);
        assertEquals(price,price2);
    }
}
