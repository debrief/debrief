package com.borlander.rac353542.bislider;

import java.text.NumberFormat;


public interface BiSliderLabelProvider {
    public String getLabel(double value);
    
    public static final BiSliderLabelProvider TO_STRING = new BiSliderLabelProvider(){
        public final NumberFormat myFormat = NumberFormat.getNumberInstance();
        
        public String getLabel(double value) {
            return myFormat.format(value);
        }
    };
    
    public static final BiSliderLabelProvider DUMMY = new BiSliderLabelProvider(){
        public String getLabel(double value) {
            return "";
        }
    };
    
}
