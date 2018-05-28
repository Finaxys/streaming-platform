package com.finaxys.flink.schema;

import com.finaxys.flink.model.Agent;
import com.finaxys.flink.model.CashByAgent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class CashByAgentSchema  implements DeserializationSchema<CashByAgent>, SerializationSchema<CashByAgent> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(CashByAgent cashByAgent) {
        return cashByAgent.toStringKafka().getBytes();
    }

    @Override
    public CashByAgent deserialize(byte[] message) throws IOException {
        return CashByAgent.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(CashByAgent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<CashByAgent> getProducedType() {
        return TypeInformation.of(CashByAgent.class);
    }
}
