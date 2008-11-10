package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderContentsDataProvider;

public abstract class DataObjectContentsDataProvider implements BiSliderContentsDataProvider {

    private final DataObjectMapper myObjectMapper;

    public DataObjectContentsDataProvider(DataObjectMapper objectMapper) {
        myObjectMapper = objectMapper;
    }

    public double getNormalValueAt(double totalMin, double totalMax, double segmentMin, double segmentMax) {
        Object totalMinObject = myObjectMapper.double2object(totalMin);
        Object totalMaxObject = myObjectMapper.double2object(totalMax);
        Object segmentMinObject = myObjectMapper.double2object(segmentMin);
        Object segmentMaxObject = myObjectMapper.double2object(segmentMax);
        return getNormalValueAt(totalMinObject, totalMaxObject, segmentMinObject, segmentMaxObject);
    }

    public abstract double getNormalValueAt(Object totalMinObject, Object totalMaxObject, Object segmentMinObject, Object segmentMaxObject);
}
