package com.finaxys.flink.time;

import model.atomlogs.TimestampedAtomLog;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

/**
 * @Author raphael on 28/12/2016.
 */
public class ProcessingTimeWatermarkExtractor implements AssignerWithPeriodicWatermarks<TimestampedAtomLog> {


    private long currentMaxTimestamp;

    public ProcessingTimeWatermarkExtractor() {}

    @Override
    public long extractTimestamp(TimestampedAtomLog element, long previousElementTimestamp) {
        long timestamp = element.getProcessingTimeTimeStamp();
        currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
        return timestamp;
    }

    @Override
    public Watermark getCurrentWatermark() {
        // return the watermark as current highest timestamp, no out off order allowed
        return new Watermark(currentMaxTimestamp);
    }
}