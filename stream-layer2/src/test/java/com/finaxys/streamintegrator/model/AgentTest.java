package com.finaxys.streamintegrator.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AgentTest {



    @Test
    public void testFromString() throws Exception {
        String agentString = "Agent;Khalil;100;CAT;-7;9;1526462070240";
        Agent agent = new Agent("Khalil",new Long("100"),"CAT",new Long("7"),new Long("9"));
        Agent agent2 = Agent.fromString(agentString);

        assertEquals(agent,agent2);
    }


}
