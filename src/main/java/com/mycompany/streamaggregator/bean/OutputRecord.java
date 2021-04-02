package com.mycompany.streamaggregator.bean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A helper class to print the output in specified format
 */
public class OutputRecord extends GroupingKey{

    private static final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OutputRecord.class);

    /**
     * int count
     */
    private final int count;

    /**
     * Public constructor
     * @param entry entry of grouping key and integer count
     */
    public OutputRecord(Map.Entry<GroupingKey,Integer> entry) {
        this(entry.getKey(), entry.getValue());
    }

    private OutputRecord(GroupingKey key, int count) {
        super(key.getDevice(), key.getTitle(), key.getCountry());
        this.count = count;
    }

    /**
     * Getter for count
     * @return count
     */
    public int getCount() {
        return count;
    }

    /**
     * Json representation of the object
     * @return json string
     */
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
