package com.finaxys.schema;

import com.finaxys.model.Price;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class PriceSchema implements DeserializationSchema<Price>, SerializationSchema<Price> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(Price price) {
        return price.toString().getBytes();
    }

    @Override
    public Price deserialize(byte[] message) throws IOException {
        return Price.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(Price nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Price> getProducedType() {
        return TypeInformation.of(Price.class);
    }

}
