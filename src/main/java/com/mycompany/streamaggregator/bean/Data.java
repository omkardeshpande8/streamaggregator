package com.mycompany.streamaggregator.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Data {
    public enum Sev{
        @JsonProperty("success")
        SUCCESS,
        @JsonProperty("error")
        ERROR
    };
    public String device;
    public Sev sev;
    public String title;
    public String country;
    public long time;

    public String getDevice() {
        return device;
    }

    public Sev getSev() {
        return sev;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }

    public long getTime() {
        return time;
    }
}

