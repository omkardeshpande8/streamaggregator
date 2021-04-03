package com.mycompany.streamaggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.streamaggregator.aggregate.EventAggregator;
import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverTest {

    private static Queue<Event> buffer;

    /**
     * Read the events from the file
     * @throws IOException if file is not present at src/test/resources/data.txt
     */
    @BeforeEach
    public static void setUp() throws IOException {
        String path = "src/test/resources/data.txt";
        ObjectMapper objectMapper = new ObjectMapper();
        buffer = new LinkedList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            for (String line; (line = br.readLine()) != null; ) {
                buffer.add(objectMapper.readValue(line, Event.class));
            }
        }
    }

    /**
     * Basic correctness test
     */
    @Test
    public void TestCorrectNess() {
        assertEquals(buffer.size(), 3596);
        EventAggregator aggregator = new EventAggregator(buffer);
        Map<GroupingKey, Integer> aggregate = aggregator.aggregate();
        assertEquals(aggregate.get(new GroupingKey("xbox_one_x", "ointb", "USA")), 7);
        assertEquals(buffer.size(), 0);

    }

}