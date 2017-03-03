package com.finaxys.flink.function;

import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.api.common.functions.MapFunction;

/**
 * @Author raphael on 01/03/2017.
 */
public class AtomOrderLogTransformer<T> implements MapFunction<TimestampedAtomLog, T> {

    private Class<T> classToCastInto;


    public AtomOrderLogTransformer() {}

    @Override
    public T map(TimestampedAtomLog timestampedAtomLog) throws Exception {
        return classToCastInto.cast(timestampedAtomLog);
    }
}
