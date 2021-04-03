package com.mycompany.streamaggregator.handler;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.mycompany.streamaggregator.bean.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Event handler implementation that reads events from text/eventstream and adds it to the buffer
 */
public class BufferedEventHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedEventHandler.class);

    /**
     * Data/Event buffer
     */
    private final BlockingQueue<Event> buffer;
    /**
     * Backpressure enabled
     */
    private final boolean isBackpressureEnabled;

    /**
     * Constructor
     */
    public BufferedEventHandler(BlockingQueue<Event> buffer, boolean isBackpressureEnabled) {
        this.buffer = buffer;
        this.isBackpressureEnabled = isBackpressureEnabled;
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
            if(!isBackpressureEnabled) {
                addDataToBuffer(data);
            } else {
                synchronized (buffer){
                    addDataToBuffer(data);
                }
            }
        }
    }

    /**
     * Tries to add the event to blocking queue. Keeps waiting till the data can be added to the queue
     * @param data event
     */
    private void addDataToBuffer(Event data){
        try {
            while (!buffer.offer(data, 10, TimeUnit.MILLISECONDS)) {
                LOGGER.warn("Waiting to the event to buffer");
            }
        }catch (InterruptedException iex){
            LOGGER.error("interrupted while waiting to add data to buffer. Could not add data to the buffer:{}", data);
            LOGGER.error(iex.getLocalizedMessage());
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
