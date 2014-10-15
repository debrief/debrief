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

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;


abstract class PointerDrawerBase implements PointerDrawer {
    public void paintPointer(GC gc, Point p) {
        paintPointer(gc, p.x, p.y, null);
    }

    public void paintPointer(GC gc, Point p, String optionalLabel) {
        paintPointer(gc, p.x, p.y, optionalLabel);
    }

}
