package com.mycompany.streamaggregator;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.ReadyState;
import com.mycompany.streamaggregator.aggregate.BufferedEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Driver class that starts the event listener
 */
public class Driver {

    /**
     * Logger for the class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    /**
     * Entrypoint for the project
     * @param args command line args
     */
    public static void main(String[] args) {

        EventHandler eventHandler = new BufferedEventHandler();
        URI uri = URI.create("https://tweet-service.herokuapp.com/sps");
        EventSource.Builder builder = new EventSource.Builder(eventHandler, uri)
                .reconnectTime(Duration.ofMillis(3000));

        try (EventSource eventSource = builder.build()) {
            eventSource.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (!eventSource.getState().equals(ReadyState.CLOSED)) {
                    eventSource.close();
                }
            }));
            while (!eventSource.getState().equals(ReadyState.CLOSED)) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
          LOGGER.error(e.getLocalizedMessage());
        }
    }

}
