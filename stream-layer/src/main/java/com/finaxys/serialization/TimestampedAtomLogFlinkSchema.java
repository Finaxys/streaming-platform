package com.finaxys.serialization;

import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import org.apache.flink.streaming.util.serialization.SerializationSchema;

import java.io.IOException;

/**
 * @Author raphael on 28/12/2016.
 */
public class TimestampedAtomLogFlinkSchema implements DeserializationSchema<TimestampedAtomLog>, SerializationSchema<TimestampedAtomLog> {

    private boolean withDateTime;
    public TimestampedAtomLogFlinkSchema(boolean withDateTime) {
        this.withDateTime = withDateTime;
    }

    @Override
    public TimestampedAtomLog deserialize(byte[] messsage) throws IOException {
        String stringMessage = new String(messsage);
        return new TimestampedAtomLog(stringMessage, withDateTime);
    }

    @Override
    public boolean isEndOfStream(TimestampedAtomLog timestampedAtomLog) {
        return false;
    }

    @Override
    public TypeInformation<TimestampedAtomLog> getProducedType() {
        return TypeInformation.of(TimestampedAtomLog.class);
    }

    @Override
    public byte[] serialize(TimestampedAtomLog timestampedAtomLog) {
        return timestampedAtomLog.toString().getBytes();
    }
}