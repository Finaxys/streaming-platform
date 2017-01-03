package com.finaxys.flink.time;

import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

/**
 * @Author raphael on 28/12/2016.
 */
public class BoundedTimestampAndWatermarkExtractor implements AssignerWithPeriodicWatermarks<TimestampedAtomLog> {

    private long maxOutOfOrderness;

    private long currentMaxTimestamp;

    public BoundedTimestampAndWatermarkExtractor(long maxOutOfOrderness) {
        this.maxOutOfOrderness = maxOutOfOrderness;
    }

    @Override
    public long extractTimestamp(TimestampedAtomLog element, long previousElementTimestamp) {
        long timestamp = element.getEventTimeTimeStamp();
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
        return timestamp;
    }

    @Override
    public Watermark getCurrentWatermark() {
        // return the watermark as current highest timestamp minus the out-of-orderness bound
        return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
    }
}