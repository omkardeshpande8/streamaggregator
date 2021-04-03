package com.mycompany.streamaggregator;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.mycompany.streamaggregator.handler.BufferedEventHandler;
import com.mycompany.streamaggregator.aggregate.EventAggregator;
import com.mycompany.streamaggregator.bean.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Driver class that starts the event listener
 */
public class Driver {

    /**
     * Driver for the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);
    /**
     * Constant used to read properties Interval in seconds
     */
    public static final String INTERVAL_SECONDS = "interval.seconds";
    /**
     * Address to read server-sent events from
     */
    public static final String SERVICE_ADDRESS = "https://tweet-service.herokuapp.com/sps";

    /**
     * Read properties from config.properties file
     *
     * @return Properties
     */
    private static Properties readProperties() {
        Properties properties = new Properties();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            LOGGER.error("config.properties file not found. Using default values");
        }
        properties.putIfAbsent(INTERVAL_SECONDS, "1");
        return properties;
    }

    /**
     * Entrypoint for the project
     *
     * @param args command line args
     */
    public static void main(String[] args) throws InterruptedException {

        Properties properties = readProperties();
        int interval = Integer.parseInt(properties.getProperty(INTERVAL_SECONDS));

        Queue<Event> buffer = new LinkedList<>();
        EventAggregator eventAggregator = new EventAggregator(buffer);
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(2);


        //Create event source
        EventHandler eventHandler = new BufferedEventHandler(buffer);
        URI uri = URI.create(SERVICE_ADDRESS);
        EventSource.Builder builder = new EventSource.Builder(eventHandler, uri)
                .reconnectTime(Duration.ofMillis(3000));

        //Start event source
        EventSource eventSource = builder.build();
        Thread sourceThread = new Thread(eventSource::start);
        sourceThread.setName("Event-Source-Thread");
        sourceThread.start();

        // Schedule consumer threads at fixed interval
        scheduledExecutorService.scheduleAtFixedRate(eventAggregator::aggregateAndPrint, interval, interval, TimeUnit.SECONDS);
        scheduledExecutorService.scheduleAtFixedRate(eventAggregator::printBufferStats, interval, interval, TimeUnit.SECONDS);

        // Add shutdown hook to clean up source and consumer
        Runtime.getRuntime().addShutdownHook(new Cleanup(scheduledExecutorService, eventAggregator, eventSource));

        // wait for source thread to complete
        sourceThread.join();
    }
}

