package com.finaxys.streamintegrator.schema;

import com.finaxys.streamintegrator.model.OrderByDir;
import com.finaxys.streamintegrator.model.PriceByCat;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class PriceByCatSchema implements DeserializationSchema<PriceByCat>, SerializationSchema<PriceByCat> {

    private static final long serialVersionUID = 6154188370181669758L;

    @Override
    public byte[] serialize(PriceByCat priceByCat) {
        return priceByCat.toStringKafka().getBytes();
    }

    @Override
    public PriceByCat deserialize(byte[] message) throws IOException {
        return PriceByCat.fromString(new String(message));
    }

    @Override
    public boolean isEndOfStream(PriceByCat nextElement) {
        return false;
    }

    @Override
    public TypeInformation<PriceByCat> getProducedType() {
        return TypeInformation.of(PriceByCat.class);
    }
}