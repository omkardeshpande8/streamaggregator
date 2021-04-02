package com.mycompany.streamaggregator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.streamaggregator.bean.Data;
import com.mycompany.streamaggregator.bean.GroupingKey;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DriverTest {
//    private static final Logger LOGGER = LoggerFactory.getLogger(DriverTest.class);

    @Test
    public void main() throws IOException {
        String path = "src/test/resources/data.txt";
        ObjectMapper objectMapper = new ObjectMapper();
        List<Data> list = new LinkedList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            for(String line; (line = br.readLine()) != null; ) {
                list.add(objectMapper.readValue(line, Data.class));
            }
        }

        Aggregator aggregator = new Aggregator(list);
        Map<GroupingKey, Integer> aggregate = aggregator.aggregate();
        assertEquals(aggregate.get(new GroupingKey("xbox_one_x","ointb","USA")),7);
        assertEquals(list.size(), 3596);
    }
}
