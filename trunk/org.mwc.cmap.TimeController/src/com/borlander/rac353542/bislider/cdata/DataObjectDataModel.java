package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.DefaultBiSliderDataModel;

public class DataObjectDataModel extends DefaultBiSliderDataModel {
    private final DataObjectMapper myMapper;

    public DataObjectDataModel(DataObjectMapper mapper){
        super(mapper.getPrecision());
        myMapper = mapper;
    }
    
    public DataObjectMapper getMapper() {
        return myMapper;
    }
    
    protected Object getTotalMinimumObject(){
        return myMapper.double2object(this.getTotalMinimum());
    }
    
    protected Object getTotalMaximumObject(){
        return myMapper.double2object(this.getTotalMaximum());
    }
    
    protected Object getUserMinimumObject(){
        return myMapper.double2object(this.getUserMinimum());
    }
    
    protected Object getUserMaximumObject(){
        return myMapper.double2object(this.getUserMaximum());
    }
    
    protected void setUserMaximumObject(Object dataObject){
        this.setUserMaximum(myMapper.object2double(dataObject));
    }
    
    protected void setUserMinimumObject(Object dataObject){
        this.setUserMinimum(myMapper.object2double(dataObject));
    }
    
    protected void setTotalObjectRange(Object minimumDataObject, Object maximumDataObject){
        this.setTotalRange(myMapper.object2double(minimumDataObject), myMapper.object2double(maximumDataObject));
    }
    
}
