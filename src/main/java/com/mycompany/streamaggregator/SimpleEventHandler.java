package com.mycompany.streamaggregator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.mycompany.streamaggregator.bean.Data;
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

public class SimpleEventHandler implements EventHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventHandler.class);

    private final List<Data> list;
    private final StampedLock stampedLock;

    public SimpleEventHandler() {
        list = new LinkedList<>();
        stampedLock = new StampedLock();
    }

    public void onOpen() {
        LOGGER.info("onOpen called. Scheduling the thread");
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(this::run, 1, 1, TimeUnit.SECONDS);
    }

    public void onClosed() {
        LOGGER.info("onClosed called");
    }

    public void onMessage(String event, MessageEvent messageEvent) {
        long stamp = stampedLock.readLock();
        try {
            Data root = objectMapper.readValue(messageEvent.getData(), Data.class);
            list.add(root);
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        stampedLock.unlockRead(stamp);
    }

    public void onComment(String comment) {
        LOGGER.info("onComment called");
    }

    public void onError(Throwable t) {
        LOGGER.info("onError called");
    }

    private void run() {
        long stamp = stampedLock.writeLock();
        LinkedList<Data> data = new LinkedList<>(list);
        list.clear();
        stampedLock.unlockWrite(stamp);
        Aggregator aggregator = new Aggregator(data);
        Map<GroupingKey, Integer> aggregate = aggregator.aggregate();
        aggregator.print(aggregate);
    }
}
