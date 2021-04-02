package com.mycompany.streamaggregator;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.ReadyState;
import com.mycompany.streamaggregator.aggregate.BufferedEventHandler;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Driver class that starts the event listener
 */
public class Driver {

    /**
     * Entrypoint for the project
     * @param args command line args
     */
    public static void main(String[] args) throws InterruptedException {

        EventHandler eventHandler = new BufferedEventHandler();
        URI uri = URI.create("https://tweet-service.herokuapp.com/sps");
        EventSource.Builder builder = new EventSource.Builder(eventHandler, uri)
                .reconnectTime(Duration.ofMillis(3000));

        try (EventSource eventSource = builder.build()) {
            eventSource.start();

            // Close the eventsource if it's not closed already
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (!eventSource.getState().equals(ReadyState.CLOSED)) {
                    eventSource.close();
                }
            }));

            // Keep the main thread alive
            while (!eventSource.getState().equals(ReadyState.CLOSED)) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }

}
