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
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * ---------------------
 * PlotFitAlgorithm.java
 * ---------------------
 * (C) Copyright 2001, 2002, by Matthew Wright and Contributors.
 *
 * Original Author:  Matthew Wright;
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: PlotFitAlgorithm.java,v 1.1.1.1 2003/07/17 10:06:31 Ian.Mayo Exp $
 *
 * Changes (from 15-Oct-2001)
 * --------------------------
 * 15-Oct-2001 : Data source classes in new package com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 08-Nov-2001 : Removed redundant import statements (DG);
 */

package com.jrefinery.chart.data;

import com.jrefinery.data.XYDataset;

/**
 * an interface that any PlotFit needs to use to get the curve
 * for the plot fit.  The algorithm takes an XYDataset and
 * comes up with a plot fit formula.  Then, using this formula,
 * it must return a y for any x supplied.  The PlotFit class is
 * responsible for querying the PlotFitAlgorithm for the data points
 * in order to get the curve to display.
 *
 * @author MW
 */
public interface PlotFitAlgorithm {

    /**
     * Returns the name that you want to see in the legend.
     * <P>
     * This is prepended to the series name that generated
     * this plot i.e. for "Chicago Moving Average" , the name
     * would be, "Moving Average" and "Chicago" would be the
     * name of the series that generated the moving average.
     *
     * @return the name that you want to see in the legend.
     */
    public String getName();

    /**
     * This Algorithm might or might not need an XYDataset to be relevant.
     *
     * @param ds  the XYDataset for this PlotFit.
     */
    public void setXYDataset(XYDataset ds);

    /**
     * For a given x, must return a y.
     *
     * @param x  the x value.
     * @param i  the series.
     *
     * @return the y value
     */
    public Number getY(int i, Number x);

}
