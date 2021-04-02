package com.mycompany.streamaggregator;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.ReadyState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Driver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    public static void main(String[] args) {

        EventHandler eventHandler = new SimpleEventHandler();
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
