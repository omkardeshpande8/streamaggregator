package com.mycompany.streamaggregator.aggregate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.mycompany.streamaggregator.bean.Event;
import com.mycompany.streamaggregator.bean.GroupingKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * Event handler implementation that reads events from text/eventstream and calls aggregator at 1 second interval
 */
public class BufferedEventHandler implements EventHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedEventHandler.class);

    /**
     * Data/Event buffer
     */
    private final List<Event> buffer;

    /**
     * Lock used for making a copy of the buffer
     */
    private final StampedLock stampedLock;

    /**
     * Constructor
     */
    public BufferedEventHandler() {
        buffer = new LinkedList<>();
        stampedLock = new StampedLock();
    }

    /**
     * Gets invoked when strem is opened
     * Schedules a task at 1 second interval
     */
    public void onOpen() {
        LOGGER.info("onOpen called. Scheduling the thread");
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(this::run, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    public void onClosed() {
        LOGGER.info("onClosed called");
    }

    /**
     * Gets invoked on message event. The event is added to the buffer
     * @param event event
     * @param messageEvent messageEvent
     */
    public void onMessage(String event, MessageEvent messageEvent) {
        long stamp = stampedLock.readLock();
        try {
            Event root = objectMapper.readValue(messageEvent.getData(), Event.class);
            buffer.add(root);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        stampedLock.unlockRead(stamp);
    }

    /**
     * {@inheritDoc}
     */
    public void onComment(String comment) {
        LOGGER.info("onComment called");
    }

    /**
     * {@inheritDoc}
     */
    public void onError(Throwable t) {
        LOGGER.info("onError called");
    }

    /**
     * Create a copy of the buffer and call aggregate function on it
     */
    private void run() {
        long stamp = stampedLock.writeLock();
        LinkedList<Event> data = new LinkedList<>(buffer);
        buffer.clear();
        stampedLock.unlockWrite(stamp);
        Aggregator aggregator = new Aggregator(data);
        Map<GroupingKey, Integer> aggregate = aggregator.aggregate();
        aggregator.print(aggregate);
    }
}