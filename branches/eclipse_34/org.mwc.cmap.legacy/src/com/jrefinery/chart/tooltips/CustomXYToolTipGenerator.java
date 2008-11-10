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
 * -----------------------------
 * CustomXYToolTipGenerator.java
 * -----------------------------
 * (C) Copyright 2002, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson (richard_c_atkinson@ntlworld.com);
 * Contributor(s):   David Gilbert (for Simba Management Limited);
 *
 * $Id: CustomXYToolTipGenerator.java,v 1.1.1.1 2003/07/17 10:06:44 Ian.Mayo Exp $
 *
 * Changes:
 * --------
 * 05-Aug-2002 : Version 1, contributed by Richard Atkinson (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart.tooltips;

import java.util.List;
import java.util.ArrayList;
import com.jrefinery.data.XYDataset;

/**
 * A tool tip generator that stores custom tooltips in a List of Lists. The XYDataSet passed into
 * the generateToolTip method is not used.
 *
 * @author RA
 */
public class CustomXYToolTipGenerator implements XYToolTipGenerator {

    /** Storage for the tooltip lists. */
    private List toolTipSeries = new ArrayList();

    /**
     * Default constructor.
     */
    public CustomXYToolTipGenerator() {
        super();
    }

    /**
     * Adds a list of tooltips for a series.
     *
     * @param toolTips  the list of tool tips.
     */
    public void addToolTipSeries(List toolTips) {
        this.toolTipSeries.add(toolTips);
    }

    /**
     * Generates a tool tip text item for a particular item within a series.
     *
     * @param data  the dataset.
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return the tooltip text.
     */
    public String generateToolTip(XYDataset data, int series, int item) {

        String toolTip = "";
        List toolTips = (List) this.toolTipSeries.get(series);
        toolTip = (String) toolTips.get(item);
        return toolTip;

    }

}
