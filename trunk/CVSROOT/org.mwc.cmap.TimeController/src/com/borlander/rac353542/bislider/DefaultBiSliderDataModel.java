package com.borlander.rac353542.bislider;

import java.util.LinkedList;

public class DefaultBiSliderDataModel implements BiSliderDataModel.Writable {
    private static final int DEFAULT_SEGMENTS_COUNT = 25;
    private double myTotalMinimum;
    private double myTotalMaximum;
    private double myUserMinimum = Double.NEGATIVE_INFINITY;
    private double myUserMaximum = Double.POSITIVE_INFINITY;
    private int mySegmentCount;
    private final LinkedList myListeners;
    private Listener[] myListenersArray;

    public DefaultBiSliderDataModel() {
        this(0, 100);
    }

    public DefaultBiSliderDataModel(double totalMin, double totalMax) {
        myListeners = new LinkedList();
        setTotalRange(totalMin, totalMax);
        setSegmentCount(DEFAULT_SEGMENTS_COUNT);
    }
    
    public void addListener(Listener listener) {
        if (listener != null){
            myListeners.add(listener);
        }
    }
    
    public void removeListener(Listener listener) {
        if (listener != null){
            myListeners.remove(listener);
        }
    }

    public double getTotalDelta() {
        return myTotalMaximum - myTotalMinimum;
    }

    public double getTotalMaximum() {
        return myTotalMaximum;
    }

    public double getTotalMinimum() {
        return myTotalMinimum;
    }

    public double getUserMaximum() {
        return myUserMaximum;
    }

    public double getUserMinimum() {
        return myUserMinimum;
    }

    public double getUserDelta() {
        return myUserMaximum - myUserMinimum;
    }

    public double getSegmentLength() {
        return getTotalDelta() / getSegmentsCount();
    }
    
    public int getSegmentsCount() {
        return mySegmentCount;
    }

    public void setUserMinimum(double userMinimum) {
        userMinimum = Math.min(userMinimum, myUserMaximum);
        userMinimum = Math.max(userMinimum, myTotalMinimum);
        if (userMinimum != myUserMinimum){
            myUserMinimum = userMinimum;
            fireChanged();
        }
    }

    public void setUserMaximum(double userMaximum) {
        userMaximum = Math.max(userMaximum, myUserMinimum);
        userMaximum = Math.min(userMaximum, myTotalMaximum);
        if (userMaximum != myUserMaximum){
            myUserMaximum = userMaximum;
            fireChanged();
        }
    }

    public void setTotalRange(double minValue, double maxValue) {
        if (minValue == maxValue) {
            throw new IllegalArgumentException("Range is too small: (" + minValue + ", " + maxValue + ")");
        }
        if (minValue > maxValue) {
            double temp = minValue;
            minValue = maxValue;
            maxValue = temp;
        }
        if (myTotalMaximum != maxValue || myTotalMinimum != minValue){
            myTotalMinimum = minValue;
            myTotalMaximum = maxValue;
            myUserMinimum = Math.max(myUserMinimum, myTotalMinimum);
            myUserMaximum = Math.min(myUserMaximum, myTotalMaximum);
            fireChanged();
        }
    }

    public void setSegmentCount(int segmentsCount) {
        if (segmentsCount < 1){
            segmentsCount = 1;
        }
        if (mySegmentCount != segmentsCount){
            mySegmentCount = segmentsCount;
            fireChanged();
        }
    }

    private void fireChanged() {
        if (!myListeners.isEmpty()) {
            Listener[] listenersCopy = copyListeners();
            for (int i = 0; i < listenersCopy.length; i++){
                Listener next = listenersCopy[i];
                if (next == null){
                    break;
                }
                next.dataModelChanged(this);
            }
        }
    }

    /**
     * Creates separate copy of listeners. It allows listeners to be
     * unregistered during notification.
     * <p>
     * An array instance is cached to avoid unnecessary creation.
     */
    private Listener[] copyListeners() {
        if (myListenersArray == null) {
            myListenersArray = new Listener[myListeners.size()];
        }
        myListenersArray = (Listener[]) myListeners.toArray(myListenersArray);
        return myListenersArray;
    }


}
