package com.mycompany.streamaggregator.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO class to hold event object
 */
public class Event {
    /**
     * Enum Sev
     */
    public enum Sev{
        @JsonProperty("success")
        SUCCESS,
        @JsonProperty("error")
        ERROR
    };
    /**
     * Device
     */
    public String device;

    /**
     * Sev
     */
    public Sev sev;

    /**
     * Title
     */
    public String title;

    /**
     * Country
     */
    public String country;

    /**
     * Timestamp
     */
    public long time;

    /**
     * Getter for device
     * @return string device
     */
    public String getDevice() {
        return device;
    }

    /**
     * Getter for sev
     * @return sev sev
     */
    public Sev getSev() {
        return sev;
    }

    /**
     * Getter for title
     * @return string title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for country
     * @return string country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Getter for time
     * @return long time
     */
    public long getTime() {
        return time;
    }
}

