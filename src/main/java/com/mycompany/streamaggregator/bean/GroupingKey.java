package com.mycompany.streamaggregator.bean;

import java.util.Objects;

/**
 * Grouping key class comprising of device, title and country
 */
public class GroupingKey {

    private final String device;
    private final String title;
    private final String country;

    public GroupingKey(String device, String title, String country) {
        this.device = device;
        this.title = title;
        this.country = country;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupingKey that = (GroupingKey) o;
        return device.equals(that.device) && title.equals(that.title) && country.equals(that.country);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(device, title, country);
    }


    /**
     * Getter for device
     * @return string device
     */
    public String getDevice() {
        return device;
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

}
