/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
 *
 * Project Info:  http://www.object-refinery.com/jfreechart/index.html
 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);
 *
 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.

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
 * -------------------------------
 * PlotNotCompatibleException.java
 * -------------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: PlotNotCompatibleException.java,v 1.1.1.1 2003/07/17 10:06:26 Ian.Mayo Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 30-Nov-2001 : Now extends RuntimeException rather than Exception, as suggested by Joao Guilherme
 *               Del Valle (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.legacy.chart;

/**
 * An exception that is generated when assigning a plot to a chart *if* the
 * plot is not compatible with the chart's current data source.  For example,
 * a HorizontalBarPlot is not compatible with an XYDataset.
 *
 * @author DG
 */
public class PlotNotCompatibleException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		/**
     * Constructs a new exception.
     *
     * @param message  a message describing the exception.
     */
    public PlotNotCompatibleException(String message) {
        super(message);
    }

}
