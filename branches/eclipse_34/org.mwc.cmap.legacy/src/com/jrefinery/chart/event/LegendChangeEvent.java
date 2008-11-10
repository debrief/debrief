/* ======================================
 * JFreeChart : a free Java chart library
 * ======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * ----------------------
 * LegendChangeEvent.java
 * ----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendChangeEvent.java,v 1.1.1.1 2003/07/17 10:06:41 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated header (DG);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.event;

import com.jrefinery.chart.Legend;

/**
 * A change event that encapsulates information about a change to a chart legend.
 *
 * @author DG
 */
public class LegendChangeEvent extends ChartChangeEvent {

    /** The chart legend that generated the event. */
    private Legend legend;

    /**
     * Creates a new LegendChangeEvent.
     *
     * @param legend  the legend that generated the event.
     */
    public LegendChangeEvent(Legend legend) {
        super(legend);
        this.legend = legend;
    }

    /**
     * Returns the legend that generated the event.
     *
     * @return the legend that generated the event.
     */
    public Legend getLegend() {
        return this.legend;
    }

}
