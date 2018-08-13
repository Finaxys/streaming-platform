package com.finaxys.streamintegrator.schema;

import com.finaxys.streamintegrator.model.OrderByDir;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class OrderByDirSchema implements DeserializationSchema<OrderByDir>, SerializationSchema<OrderByDir> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(OrderByDir orderByDir) {
        return orderByDir.toStringKafka().getBytes();
    }

    @Override
    public OrderByDir deserialize(byte[] message) throws IOException {
        return OrderByDir.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(OrderByDir nextElement) {
        return false;
    }

    @Override
    public TypeInformation<OrderByDir> getProducedType() {
        return TypeInformation.of(OrderByDir.class);
    }
}
