package com.planetmayo.debrief.satc.zigdetector.TimeWindow.average;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by deft on 09/05/2015.
 */
public class DataPoint implements Comparable<DataPoint> {

    private final Calendar timestamp;

    private final Double value;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public DataPoint(Calendar timestamp, Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataPoint{" + "timestamp=" + sdf.format(timestamp.getTime()) + ", value=" + value + '}';
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public Double getValue() {
        return value;
    }

    @Override
    public int compareTo(DataPoint o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
