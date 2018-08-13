package com.finaxys.streamintegrator.schema;

import com.finaxys.streamintegrator.model.Order;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class OrderSchema implements DeserializationSchema<Order>, SerializationSchema<Order> {

    @Override
    public byte[] serialize(Order order) {
        return order.toString().getBytes();
    }

    @Override
    public Order deserialize(byte[] message) throws IOException {
        return Order.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(Order nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Order> getProducedType() {
        return TypeInformation.of(Order.class);
    }
}
