package com.borlander.rac353542.bislider;


public interface BiSliderContentsDataProvider {
    public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax);
    
    public static final BiSliderContentsDataProvider DUMMY = new BiSliderContentsDataProvider(){
        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            return 1.0;
        }
    };
    
    public static final BiSliderContentsDataProvider NORMAL_DISTRIBUTION = new BiSliderContentsDataProvider(){
        public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
            double arg = (segmentMin + segmentMax) / 2;
            double mean = (totalMin + totalMax) / 2;
            double delta = arg - mean;
            double totalDelta = (totalMax - totalMin) / 2;
            double divisor = totalDelta * totalDelta / (2 * 2) / Math.log(2.0);
            return Math.exp(-delta * delta / divisor);
        }
    };
}
