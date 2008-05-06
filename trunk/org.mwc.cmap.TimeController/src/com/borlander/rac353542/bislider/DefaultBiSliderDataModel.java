package com.borlander.rac353542.bislider;

import java.util.LinkedList;

public class DefaultBiSliderDataModel implements BiSliderDataModel.Writable {
    private static final int DEFAULT_SEGMENTS_COUNT = 25;
    private final double myPrecision;
    private double myTotalMinimum;
    private double myTotalMaximum;
    private double myUserMinimum = Double.NEGATIVE_INFINITY;
    private double myUserMaximum = Double.POSITIVE_INFINITY;
    private final LinkedList myListeners;
    private Listener[] myListenersArray;
    private int myCompositeUpdateCounter;
    
    private double mySegmentLength;
    private int mySegmentsCount;

    public DefaultBiSliderDataModel() {
        this(0);
    }
    
    public DefaultBiSliderDataModel(double precision) {
        this(0, 100, precision);
    }
    
    public DefaultBiSliderDataModel(double totalMin, double totalMax, double precision) {
        myListeners = new LinkedList();
        myPrecision = precision;
        setTotalRange(totalMin, totalMax);
        setSegmentCount(DEFAULT_SEGMENTS_COUNT);
    }
    
    public double getPrecision() {
        return myPrecision;
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
    	if (mySegmentLength <= 0){
    		if (mySegmentsCount <= 0){
    			throw new IllegalStateException();
    		}
    		return getTotalDelta() / mySegmentsCount;
    	} else {
    		return mySegmentLength;
    	}
    }
    
    public void setUserMinimum(double userMinimum) {
    // skip these checks, our estimate is better.
    //    userMinimum = Math.min(userMinimum, myUserMaximum);
    //    userMinimum = Math.max(userMinimum, myTotalMinimum);
        if (userMinimum != myUserMinimum){
            myUserMinimum = userMinimum;
            fireChanged();
        }
    }

    public void setUserMaximum(double userMaximum) {
      // skip these checks, our estimate is better.
			//        userMaximum = Math.max(userMaximum, myUserMinimum);
			//        userMaximum = Math.min(userMaximum, myTotalMaximum);
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
            // skip these checks, our estimate is better.		
		        //    myUserMinimum = Math.max(myUserMinimum, myTotalMinimum);
		        //    myUserMaximum = Math.min(myUserMaximum, myTotalMaximum);
            fireChanged();
        }
    }
    
    public void setSegmentLength(double segmentLength){
    	if (segmentLength > getTotalDelta()){
    		segmentLength = getTotalDelta();
    	}
    	if (mySegmentLength != segmentLength){
    		mySegmentLength = segmentLength;
    		mySegmentsCount = -1;
    		fireChanged();
    	}
    }

    public void setSegmentCount(int segmentsCount) {
        if (segmentsCount < 1){
            segmentsCount = 1;
        }
        if (mySegmentsCount != segmentsCount){
        	mySegmentsCount = segmentsCount;
        	mySegmentLength = -1;
        	fireChanged();
        }
    }
    
    public void setUserRange(double userMin, double userMax) {
        if (userMin > userMax){
            double temp = userMin;
            userMin = userMax;
            userMax = temp;
        }
        userMin = checkTotalRange(userMin);
        userMax = checkTotalRange(userMax);
        if (userMax != myUserMaximum || userMin != myUserMinimum){
            myUserMaximum = userMax;
            myUserMinimum = userMin;
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
                next.dataModelChanged(this, myCompositeUpdateCounter > 0);
            }
        }
    }
    
    public void startCompositeUpdate() {
        myCompositeUpdateCounter++;
    }
    
    public void finishCompositeUpdate() {
        if (myCompositeUpdateCounter <= 0){
            throw new IllegalStateException("Finish update without start update");
        }
        if (--myCompositeUpdateCounter == 0){
            //nothing changed since last notification. However, we have to 
            //send last notification with moreChangesExpected = false
            fireChanged();
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
    
    private double checkTotalRange(double value){
        value = Math.min(value, myTotalMaximum);
        value = Math.max(value, myTotalMinimum);
        return value;
    }

}
