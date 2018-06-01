package com.finaxys.schema;

import com.finaxys.model.Agent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class AgentSchema implements DeserializationSchema<Agent>, SerializationSchema<Agent> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(Agent agent) {
        return agent.toString().getBytes();
    }

    @Override
    public Agent deserialize(byte[] message) throws IOException {
        return Agent.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(Agent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Agent> getProducedType() {
        return TypeInformation.of(Agent.class);
    }
}
