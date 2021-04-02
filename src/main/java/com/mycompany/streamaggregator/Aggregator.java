package com.mycompany.streamaggregator;

import com.mycompany.streamaggregator.bean.Data;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Aggregator.class);

    private final List<Data> list;

    public Aggregator(List<Data> list) {
        this.list = list;
    }

    public Map<GroupingKey, Integer> aggregate(){
        Map<GroupingKey, Integer> success = list
                .parallelStream()
                .filter(e -> e.getSev().equals(Data.Sev.SUCCESS))
                .collect(Collectors.groupingByConcurrent(data ->
                                new GroupingKey(data.getDevice(), data.getTitle(), data.getCountry()),
                        Collectors.summingInt(d -> 1)));
        return success;
    }

    public void print(Map<GroupingKey, Integer> success) {
        LOGGER.info(Timestamp.from(Instant.now()).toString());
        success.entrySet().stream().map(OutputRecord::new).map(OutputRecord::toString).forEach(LOGGER::info);
    }

}
