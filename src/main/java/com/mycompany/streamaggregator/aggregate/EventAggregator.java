package com.mycompany.streamaggregator.aggregate;

import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import com.mycompany.streamaggregator.bean.OutputRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
    private final Queue<Event> buffer;

    public EventAggregator(Queue<Event> buffer) {
        this.buffer = buffer;
    }

    /**
     * Aggregates the buffer and prints the result
     */
    public void aggregateAndPrint() {
        Map<GroupingKey, Integer> map = aggregate();
        map.entrySet().stream()
                .map(OutputRecord::new)
                .map(OutputRecord::toString)
                .forEach(LOGGER::info);
    }

    /**
     * Aggregate buffer
     * @return map of key used for grouping and count
     */
    public Map<GroupingKey, Integer> aggregate() {
        Map<GroupingKey, Integer> map = new HashMap<>();
        int size = buffer.size();
        for (int i = 0; i < size; i++) {
            Event poll = buffer.poll();
            GroupingKey gk = new GroupingKey(poll.getDevice(), poll.getTitle(), poll.getCountry());
            map.put(gk, map.getOrDefault(gk, 0) + 1);
        }
        printBufferStats(size);
        return map;
    }

    /**
     * Print size of buffer
     */
    private void printBufferStats(int processedSize) {
        LOGGER.info("Processed events: {}, buffer size:{} ", processedSize, buffer.size());
    }
}
