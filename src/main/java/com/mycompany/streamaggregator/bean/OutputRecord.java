package com.mycompany.streamaggregator.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OutputRecord extends GroupingKey{

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputRecord.class);

    private final int count;

    public OutputRecord(Map.Entry<GroupingKey,Integer> entry) {
        this(entry.getKey(), entry.getValue());
    }

    public OutputRecord(GroupingKey key, int count) {
        super(key.getDevice(), key.getTitle(), key.getCountry());
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return null;
    }
}
