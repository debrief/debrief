package com.borlander.rac353542.bislider.impl;


class ColoredSegment {
    private final ColorDescriptor myColorDescriptor;
    private final double myMaxValue;
    private final double myMinValue;

    public ColoredSegment(double minValue, double maxValue, ColorDescriptor colorDescriptor){
        myMinValue = minValue;
        myMaxValue = maxValue;
        myColorDescriptor = colorDescriptor;
    }
    
    public double getMaxValue() {
        return myMaxValue;
    }
    
    public double getMinValue() {
        return myMinValue;
    }
    
    public ColorDescriptor getColorDescriptor() {
        return myColorDescriptor;
    }
    
}
