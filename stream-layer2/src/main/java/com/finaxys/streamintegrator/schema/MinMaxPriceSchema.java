package com.finaxys.streamintegrator.schema;

import com.finaxys.streamintegrator.model.MinMaxPrice;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class MinMaxPriceSchema  implements DeserializationSchema<MinMaxPrice>, SerializationSchema<MinMaxPrice> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(MinMaxPrice minMaxPrice) {
        return minMaxPrice.toStringKafka().getBytes();
    }

    @Override
    public MinMaxPrice deserialize(byte[] message) throws IOException {
        return MinMaxPrice.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(MinMaxPrice nextElement) {
        return false;
    }

    @Override
    public TypeInformation<MinMaxPrice> getProducedType() {
        return TypeInformation.of(MinMaxPrice.class);
    }
}
