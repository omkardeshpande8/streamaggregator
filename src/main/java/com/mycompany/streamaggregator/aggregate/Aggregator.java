package com.mycompany.streamaggregator.aggregate;

import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import com.mycompany.streamaggregator.bean.OutputRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Aggregator {
    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Aggregator.class);

    /**
     * List of events/data
     */
    private final List<Event> list;

    /**
     * Constructor
     * @param list of events/data
     */
    public Aggregator(List<Event> list) {
        this.list = list;
    }

    /**
     * Aggregates the events in the list by grouping key - device, title, country
     * after filtering out the events with status failed
     * @return map of grouping key and count as value
     */
    public Map<GroupingKey, Integer> aggregate(){
        return list
                .stream()
                .filter(e -> e.getSev().equals(Event.Sev.SUCCESS))
                .collect(Collectors.groupingBy(event ->
                                new GroupingKey(event.getDevice(), event.getTitle(), event.getCountry()),
                        Collectors.summingInt(d -> 1)));
    }

    /**
     * Aggregates the events in the list by grouping key - device, title, country parallely
     * after filtering out the events with status failed
     * @return map of grouping key and count as value
     */
    public Map<GroupingKey, Integer> aggregateParallel(){
        Map<GroupingKey, Integer> success = list
                .parallelStream()
                .filter(e -> e.getSev().equals(Event.Sev.SUCCESS))
                .collect(Collectors.groupingByConcurrent(event ->
                                new GroupingKey(event.getDevice(), event.getTitle(), event.getCountry()),
                        Collectors.summingInt(d -> 1)));
        return success;
    }

    /**
     * Prints the results in specified output format
     * @param success map of grouping key and count as value
     */
    public void print(Map<GroupingKey, Integer> success) {
        LOGGER.info(Timestamp.from(Instant.now()).toString());
        success.entrySet().stream().map(OutputRecord::new).map(OutputRecord::toString).forEach(LOGGER::info);
    }

}
