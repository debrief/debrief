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
 * ---------------------------
 * StandardXYURLGenerator.java
 * ---------------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Simba Management Limited);
 *
 * $Id: StandardXYURLGenerator.java,v 1.1.1.1 2003/07/17 10:06:48 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 29-Aug-2002 : New constructor and member variables to customise series and item parameter
 *               names (RA);
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package com.jrefinery.chart.urls;

import com.jrefinery.data.XYDataset;

/**
 * A URL generator.
 *
 * @author RA
 */
public class StandardXYURLGenerator implements XYURLGenerator {

    /** Prefix to the URL */
    private String prefix = "index.html";

    /** Series parameter name to go in each URL */
    private String seriesParameterName = "series";

    /** Item parameter name to go in each URL */
    private String itemParameterName = "item";

    /**
     * Blank constructor
     */
    public StandardXYURLGenerator() {
    }

    /**
     * Constructor that overrides default prefix to the URL.
     *
     * @param sPrefix  the prefix to the URL
     */
    public StandardXYURLGenerator(String sPrefix) {
        this.prefix = sPrefix;
    }

    /**
     * Constructor that overrides all the defaults
     *
     * @param prefix  the prefix to the URL.
     * @param seriesParameterName  the name of the series parameter to go in each URL.
     * @param itemParameterName  the name of the item parameter to go in each URL.
     */
    public StandardXYURLGenerator(String prefix,
                                  String seriesParameterName,
                                  String itemParameterName) {

        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.itemParameterName = itemParameterName;

    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param dataset  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return  the generated URL.
     */
    public String generateURL(XYDataset dataset, int series, int item) {
        String url = prefix;
        boolean firstParameter = url.indexOf("?") == -1;
        url += firstParameter ? "?" : "&";
        url += this.seriesParameterName + "=" + series
            + "&" + this.itemParameterName + "=" + item;
        return url;
    }

}
