/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2020, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------------
 * ChartMouseListener.java
 * -----------------------
 * (C) Copyright 2002-2020, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Alex Weber;
 *
 */

package org.jfree.chart;

import java.util.EventListener;

/**
 * The interface that must be implemented by classes that wish to receive
 * {@link ChartMouseEvent} notifications from a {@link ChartPanel}.
 *
 * @see ChartPanel#addChartMouseListener(ChartMouseListener)
 */
public interface ChartMouseListener extends EventListener {

    /**
     * Callback method for receiving notification of a mouse click on a chart.
     *
     * @param event  information about the event.
     */
    void chartMouseClicked(ChartMouseEvent event);

    /**
     * Callback method for receiving notification of a mouse movement on a
     * chart.
     *
     * @param event  information about the event.
     */
    void chartMouseMoved(ChartMouseEvent event);

}
