/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
