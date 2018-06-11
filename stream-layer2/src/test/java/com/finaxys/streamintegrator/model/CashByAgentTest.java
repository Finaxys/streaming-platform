package com.finaxys.streamintegrator.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashByAgentTest {


    @Test
    public void testToStringKafka() throws Exception {
        String string = "Khalil;20";
        CashByAgent cashByAgent = new CashByAgent("Khalil",new Long("20"));
        assertEquals(string,cashByAgent.toStringKafka());
    }
}
