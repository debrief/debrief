/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
