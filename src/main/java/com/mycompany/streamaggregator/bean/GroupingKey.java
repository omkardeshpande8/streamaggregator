package com.mycompany.streamaggregator.bean;

import java.util.Objects;

public class GroupingKey {

    private final String device;
    private final String title;
    private final String country;

    public GroupingKey(String device, String title, String country) {
        this.device = device;
        this.title = title;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupingKey that = (GroupingKey) o;
        return device.equals(that.device) && title.equals(that.title) && country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, title, country);
    }

    public String getDevice() {
        return device;
    }

    public String getTitle() {
        return title;
    }

    public String getCountry() {
        return country;
    }
}
