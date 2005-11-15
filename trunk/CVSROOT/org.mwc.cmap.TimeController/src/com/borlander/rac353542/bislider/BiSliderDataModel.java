package com.borlander.rac353542.bislider;


public interface BiSliderDataModel {
    public double getTotalMinimum();
    public double getTotalMaximum();
    public double getTotalDelta();
    
    public double getUserMinimum();
    public double getUserMaximum();
    public double getUserDelta();
    
    public double getSegmentLength();
    public int getSegmentsCount();
    
    public void addListener(Listener listener);
    public void removeListener(Listener listener);
    
    public static interface Writable extends BiSliderDataModel {
        public void setUserMinimum(double userMinimum);
        public void setUserMaximum(double userMaximum);
    }
    
    public static interface Listener {
        public void dataModelChanged(BiSliderDataModel dataModel);
    }
}
