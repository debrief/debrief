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
