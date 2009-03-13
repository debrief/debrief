/* =======================================
 * JFreeChart : a Java Chart Class Library
 * =======================================
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
 * ----------------
 * LegendTitle.java
 * ----------------
 * (C) Copyright 2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   -;
 *
 * $Id: LegendTitle.java,v 1.1.1.1 2003/07/17 10:06:25 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 07-Feb-2002 : Version 1. INCOMPLETE, PLEASE IGNORE. (DG);
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * A chart title that displays a legend for the data in the chart.
 * <P>
 * The title can be populated with legend items manually, or you can assign a
 * reference to the chart, in which case the legend items will be automatically
 * created to match the dataset.
 *
 * @author DG
 */
public abstract class LegendTitle extends AbstractTitle {

    /** A container for the legend items. */
    private LegendItemCollection items;

    /**
     * Constructs a new, empty LegendTitle.
     */
    public LegendTitle() {
        this(new StandardLegendItemLayout(0, 0.0));
    }

    /**
     * Creates a new legend title.
     *
     * @param layout  the layout.
     */
    public LegendTitle(LegendItemLayout layout) {
    }

    /**
     * Adds a legend item to the LegendTitle.
     *
     * @param item  the item to add.
     */
    public void addLegendItem(LegendItem item) {
        items.add(item);
    }

    /**
     * Draws the title on a Java 2D graphics device (such as the screen or a
     * printer). Currently it does nothing.
     *
     * @param g2  the graphics device.
     * @param area  the area for the chart and all its titles.
     */
    public void draw(Graphics2D g2, Rectangle2D area) {

        // if the position is TOP or BOTTOM then the constraint is on the width
        // so layout the items accordingly

        // if the position is LEFT or RIGHT then the constraint is on the height
        // so layout the items accordingly

        // get the height and width of the items, then add the space around the outside
        // work out where to start drawing...
        // and draw...
    }

}
