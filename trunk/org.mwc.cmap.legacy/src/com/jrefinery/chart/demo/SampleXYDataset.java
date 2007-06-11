/* ===============
 * JFreeChart Demo
 * ===============
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors;
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
 * --------------------
 * SampleXYDataset.java
 * --------------------
 * (C) Copyright 2000-2002, by Simba Management Limited;
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: SampleXYDataset.java,v 1.1.1.1 2003/07/17 10:06:35 Ian.Mayo Exp $
 *
 * Changes (from 24-Aug-2001)
 * --------------------------
 * 24-Aug-2001 : Added standard source header. Fixed DOS encoding problem (DG);
 * 15-Oct-2001 : Parent class has changed package (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 *               Added translate factor, used for demonstrating dynamic chart (DG);
 * 07-Nov-2001 : Updated source header (DG);
 * 11-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.demo;

import com.jrefinery.data.XYDataset;
import com.jrefinery.data.AbstractSeriesDataset;
import com.jrefinery.data.DatasetChangeEvent;

/**
 * A dummy dataset for an XY plot.
 * <P>
 * Note that the aim of this class is to create a self-contained data source for demo purposes -
 * it is NOT intended to show how you should go about writing your own datasets.
 *
 * @author DG
 */
public class SampleXYDataset extends AbstractSeriesDataset implements XYDataset {

    /** Use the translate to change the data and demonstrate dynamic data changes. */
    private double translate;

    /**
     * Default constructor.
     */
    public SampleXYDataset() {
        this.translate = 0.0;
    }

    /**
     * Returns the translation factor.
     *
     * @return  the translation factor.
     */
    public double getTranslate() {
        return this.translate;
    }

    /**
     * Sets the translation constant for the x-axis.
     *
     * @param translate  the translation factor.
     */
    public void setTranslate(double translate) {
        this.translate = translate;
        this.notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Returns the x-value for the specified series and item.  Series are numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     *
     * @return the x-value for the specified series and item.
     */
    public Number getXValue(int series, int item) {
        return new Double(-10.0 + translate + (item / 10.0));
    }

    /**
     * Returns the y-value for the specified series and item.  Series are numbered 0, 1, ...
     *
     * @param series  the index (zero-based) of the series.
     * @param item  the index (zero-based) of the required item.
     *
     * @return the y-value for the specified series and item.
     */
    public Number getYValue(int series, int item) {
        if (series == 0) {
            return new Double(Math.cos(-10.0 + translate + (item / 10.0)));
        }
        else {
            return new Double(2 * (Math.sin(-10.0 + translate + (item / 10.0))));
        }
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return the number of series in the dataset.
     */
    public int getSeriesCount() {
        return 2;
    }

    /**
     * Returns the name of the series.
     *
     * @param series  the index (zero-based) of the series.
     *
     * @return the name of the series.
     */
    public String getSeriesName(int series) {
        if (series == 0) {
            return "y = cosine(x)";
        }
        else if (series == 1) {
            return "y = 2*sine(x)";
        }
        else {
            return "Error";
        }
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the index (zero-based) of the series.
     * @return the number of items in the specified series.
     *
     */
    public int getItemCount(int series) {
        return 200;
    }

}







