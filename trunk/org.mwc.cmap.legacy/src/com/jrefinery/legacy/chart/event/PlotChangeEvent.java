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
 * --------------------
 * PlotChangeEvent.java
 * --------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotChangeEvent.java,v 1.1.1.1 2003/07/17 10:06:41 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 07-Nov-2001 : Updated header (DG);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart.event;

import com.jrefinery.legacy.chart.Plot;

/**
 * A change event that encapsulates information about a change to a plot.
 *
 * @author DG
 */
public class PlotChangeEvent extends ChartChangeEvent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		/** The plot that generated the event. */
    private Plot plot;

    /**
     * Creates a new PlotChangeEvent.
     *
     * @param plot  the plot that generated the event.
     */
    public PlotChangeEvent(Plot plot) {
        super(plot);
        this.plot = plot;
    }

    /**
     * Returns the plot that generated the event.
     *
     * @return the plot that generated the event.
     */
    public Plot getPlot() {
        return this.plot;
    }

}
