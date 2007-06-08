package com.borlander.rac353542.bislider.cdata;

import com.borlander.rac353542.bislider.BiSliderLabelProvider;

/**
 * Implementation of the <code>BiSliderLabelProvider</code> suited for working
 * woth singular set of model dats objects.
 */
public abstract class DataObjectLabelProvider implements BiSliderLabelProvider {

    private final DataObjectMapper myMapper;

    public DataObjectLabelProvider(DataObjectMapper mapper) {
        myMapper = mapper;
    }

    public String getLabel(double value) {
        Object dataObject = myMapper.double2object(value);
        if (dataObject == null) {
            // contract of DatatObjectMapper is broken but we do not want to
            // fail.
            return "";
        }
        return getLabel(dataObject);
    }

    public abstract String getLabel(Object dataObject);
}
