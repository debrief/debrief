package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderUIModel;
import com.borlander.rac353542.bislider.DefaultBiSliderUIModel;

/**
 * Single pack of objects required to instantiate BiSlider working with custom
 * object data model of <code>java.lang.Long</code> values.
 */
public class LongDataSuite {

    public LongDataModel createDataModel(long totalMin, long totalMax, long userMin, long userMax){
        LongDataModel result = new LongDataModel();
        result.setTotalRange(new Long(totalMin), new Long(totalMax));
        result.setUserMinimum(new Long(userMin));
        result.setUserMaximum(new Long(userMax));
        return result;
    }
    
    public BiSliderUIModel createUIModel(){
        DefaultBiSliderUIModel result = new DefaultBiSliderUIModel();
        result.setLabelProvider(createLabelProvider());
        //assuming that long values are big enough
        result.setVerticalLabels(true);
        return result;
    }
    
    public DataObjectLabelProvider createLabelProvider() {
        return new DataObjectLabelProvider(MAPPER_LONG){
            public String getLabel(Object dataObject) {
                Long longValue = (Long)dataObject;
                return longValue.toString();
            }
        };
    }
    
    public static class LongDataModel extends DataObjectDataModel {
        public LongDataModel(){
            super(MAPPER_LONG);
        }
        
        public Long getTotalMinimumLong(){
            return (Long)getTotalMinimumObject();
        }
        
        public Long getTotalMaximumLong(){
            return (Long)getTotalMaximumObject();
        }
        
        public Long getUserMinimumLong(){
            return (Long)getUserMinimumObject();
        }
        
        public Long getUserMaximumLong(){
            return (Long)getUserMaximumObject();
        }
        
        public void setUserMaximum(Long longMaximum){
            setUserMaximumObject(longMaximum);
        }
        
        public void setUserMinimum(Long longMinimum){
            setUserMinimumObject(longMinimum);
        }
        
        public void setTotalRange(Long minimum, Long maximum){
            setTotalObjectRange(minimum, maximum);
        }
        
    }
    
    public static DataObjectMapper MAPPER_LONG = new DataObjectMapper() {
        public Object double2object(double value) {
            long longValue = Math.round(value);
            return new Long(longValue);
        }

        public double object2double(Object object) {
            Long longValue = (Long) object;
            return longValue.doubleValue();
        }
        
        public double getPrecision() {
            return 1;
        }
    };
    
}
