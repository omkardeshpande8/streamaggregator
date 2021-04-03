package com.mycompany.streamaggregator.aggregate;

import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import com.mycompany.streamaggregator.bean.OutputRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        int size = buffer.size();
        Map<GroupingKey, Integer> map = IntStream.range(0, size)
                .mapToObj(i -> buffer.poll())
                .filter(Objects::nonNull)
                .map(event -> new GroupingKey(event.getDevice(), event.getTitle(), event.getCountry()))
                .collect(Collectors.toMap(Function.identity(), groupingKey -> 1, Integer::sum));

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
