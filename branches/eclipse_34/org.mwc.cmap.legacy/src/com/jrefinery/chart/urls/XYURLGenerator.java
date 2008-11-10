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
 * -------------------
 * XYURLGenerator.java
 * -------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributors:     David Gilbert (for Simba Management Limited);
 *
 * $Id: XYURLGenerator.java,v 1.1.1.1 2003/07/17 10:06:49 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson;
 * 09-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */
package com.jrefinery.chart.urls;

import com.jrefinery.data.XYDataset;

/**
 * Interface for a URL generator for plots that uses data from an XYDataset.
 *
 * @author RA
 */
public interface XYURLGenerator extends URLGenerator {

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series number (zero-based index).
     * @param item  the item number (zero-based index).
     *
     * @return a string containing the generated URL.
     */
    public String generateURL(XYDataset data, int series, int item);

}
