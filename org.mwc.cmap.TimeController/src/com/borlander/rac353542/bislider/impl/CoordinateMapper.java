/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package com.borlander.rac353542.bislider.impl;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import com.borlander.rac353542.bislider.BiSliderDataModel;

interface CoordinateMapper {

    public void setContext(BiSliderDataModel dataModel, Rectangle drawArea, Rectangle fullBounds);

    public double pixel2value(Point pixel);

    public double pixel2value(int pixelX, int pixelY);

    public Point value2pixel(double value, boolean anchorAtMinimumEdge);

    public Rectangle getDrawArea();
    
    public Rectangle getFullBounds();

    public Axis getAxis();

    /**
     * @param shrink
     *            if <code>true</code>, rsult rectangle should be shrinked
     *            for 1 pixel in the normal axis. It is workaround for filling
     *            algorithm in GC which otehrwise extends the outline.
     */
    public Rectangle segment2rectangle(double min, double max, double normalRate, boolean shrink);

    /**
     * @return the delta between the maximum and minimun values which are mapped
     *         into the same pixel as the given value.
     */
    public double getOnePixelValueDelta(double value);
}
