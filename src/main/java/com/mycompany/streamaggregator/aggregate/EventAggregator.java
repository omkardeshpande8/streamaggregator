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

    /**
     * boolean indicating whether aggregation should lock the buffer
     */
    private final boolean synchronize;

    public EventAggregator(Queue<Event> buffer) {
        this(buffer, true);
    }

    public EventAggregator(Queue<Event> buffer, boolean synchronize) {
        this.buffer = buffer;
        this.synchronize = synchronize;
    }

    /**
     * Aggregates the buffer and prints the result
     */
    public void aggregateAndPrint() {
        Map<GroupingKey, Integer> map = synchronize ? aggregateSync() : aggregate();
        if(map != null){
            map.entrySet().stream()
                    .map(OutputRecord::new)
                    .map(OutputRecord::toString)
                    .forEach(LOGGER::info);
        }
    }

    /**
     * Aggregation function converts queue of event to desired output i.e. grouping key and count
     */
    private final Function<Queue<Event>, Map<GroupingKey, Integer>> aggregateFunction = events -> {
        int size = events.size();
        Map<GroupingKey, Integer> map = IntStream.range(0, size)
                .mapToObj(i -> events.poll())
                .filter(Objects::nonNull)
                .map(event -> new GroupingKey(event.getDevice(), event.getTitle(), event.getCountry()))
                .collect(Collectors.toMap(Function.identity(), groupingKey -> 1, Integer::sum));

        LOGGER.info("Processed events: {}, buffer size:{} ", size, events.size());
        return map;
    };

    /**
     * Aggregate buffer
     * @return map of key used for grouping and count
     */
    public Map<GroupingKey, Integer> aggregate() {
        return aggregateFunction.apply(buffer);
    }

    /**
     * Lock the buffer and aggregate buffer. No new elements will be added to the buffer.
     * @return map of key used for grouping and count
     */
    public Map<GroupingKey, Integer> aggregateSync() {
        synchronized (buffer) {
            return aggregateFunction.apply(buffer);
        }
    }

}
