package com.mycompany.streamaggregator.handler;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.mycompany.streamaggregator.bean.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

/**
 * Event handler implementation that reads events from text/eventstream and adds it to the buffer
 */
public class BufferedEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedEventHandler.class);

    /**
     * Data/Event buffer
     */
    private final Queue<Event> buffer;

    /**
     * Constructor
     */
    public BufferedEventHandler(Queue<Event> buffer) {
        this.buffer = buffer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen() {
        LOGGER.info("onOpen called");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClosed() {
        LOGGER.info("onClosed called");
    }

    /**
     * Gets invoked on message event. The event is added to the buffer
     * @param event event
     * @param messageEvent messageEvent
     */
    @Override
    public void onMessage(String event, MessageEvent messageEvent) {
        Event data = Event.getEventFromJson(messageEvent.getData());
        if (data != null) {
            buffer.add(data);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onComment(String comment) {
        LOGGER.info("onComment called:{}", comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable t) {
        LOGGER.error("onError called:{}", t.getLocalizedMessage());
    }

}
