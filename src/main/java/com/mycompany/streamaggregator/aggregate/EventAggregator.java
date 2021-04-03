package com.mycompany.streamaggregator.aggregate;

import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import com.mycompany.streamaggregator.bean.OutputRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The aggregator class that consumes events from the buffer and aggregates them.
 */
public class EventAggregator {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventAggregator.class);

    /**
     * Buffer to hold the events
     */
    private final BlockingQueue<Event> buffer;
    /**
     * Is back pressure enabled
     */
    private final boolean isBackpressureEnabled;

    public EventAggregator(BlockingQueue<Event> buffer, boolean isBackpressureEnabled) {
        this.buffer = buffer;
        this.isBackpressureEnabled = isBackpressureEnabled;
    }

    /**
     * Aggregates the buffer and prints the result
     */
    public void aggregateAndPrint() {
        Map<GroupingKey, Integer> map = aggregate();
        if (map != null) {
            map.entrySet().stream()
                    .map(OutputRecord::new)
                    .map(OutputRecord::toString)
                    .forEach(LOGGER::info);
        }
    }

    /**
     * Aggregate buffer
     *
     * @return map of key used for grouping and count
     */
    public Map<GroupingKey, Integer> aggregate() {
        List<Event> tempBuffer;

        if (isBackpressureEnabled) {
            synchronized (buffer) {
                tempBuffer = getTempBuffer();
            }
        } else {
            tempBuffer = getTempBuffer();
        }
        Map<GroupingKey, Integer> map = getCounts(tempBuffer);
        printBufferStats(tempBuffer.size());
        return map;
    }


    /**
     * Drains the buffer to a temp list
     * @return list temporary data structure for calculating aggregates
     */
    private List<Event> getTempBuffer(){
        int size = buffer.size();
        List<Event> list = new ArrayList<>(size);
        buffer.drainTo(list, size);
        return list;
    }

    /**
     * Generates counts by the grouping key for the events in the buffer
     * @param events list of events
     * @return Map<GroupingKey, Integer> grouping key and count is value
     */
    private Map<GroupingKey, Integer> getCounts(List<Event> events) {
        return events.stream()
                .filter(Objects::nonNull)
                .map(event -> new GroupingKey(event.getDevice(), event.getTitle(), event.getCountry()))
                .collect(Collectors.toMap(Function.identity(), groupingKey -> 1, Integer::sum));
    }

    /**
     * Print size of buffer
     */
    private void printBufferStats(int processedSize) {
        LOGGER.info("Processed events: {}, buffer size:{} ", processedSize, buffer.size());
    }
}
