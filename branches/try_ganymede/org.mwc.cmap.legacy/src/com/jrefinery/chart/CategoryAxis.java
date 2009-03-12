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
 * -----------------
 * CategoryAxis.java
 * -----------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * $Id: CategoryAxis.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes (from 21-Aug-2001)
 * --------------------------
 * 21-Aug-2001 : Added standard header. Fixed DOS encoding problem (DG);
 * 18-Sep-2001 : Updated header (DG);
 * 04-Dec-2001 : Changed constructors to protected, and tidied up default values (DG);
 * 19-Apr-2002 : Updated import statements (DG);
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Paint;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Stroke;

/**
 * An axis that displays categories.
 * <P>
 * The axis needs to rely on the plot for placement of labels, since the plot
 * controls how the categories are distributed.
 *
 * @author DG
 */
public abstract class CategoryAxis extends Axis {

    /**
     * Constructs a category axis.
     *
     * @param label  the axis label.
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param categoryLabelsVisible  a flag indicating whether or not category labels are visible.
     * @param categoryLabelFont  the font used to display category (tick) labels.
     * @param categoryLabelPaint  the paint used to draw category (tick) labels.
     * @param categoryLabelInsets  the insets for the category labels.
     * @param tickMarksVisible  a flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     */
    protected CategoryAxis(String label,
                           Font labelFont, Paint labelPaint, Insets labelInsets,
                           boolean categoryLabelsVisible,
                           Font categoryLabelFont, Paint categoryLabelPaint,
                           Insets categoryLabelInsets,
                           boolean tickMarksVisible,
                           Stroke tickMarkStroke, Paint tickMarkPaint) {

        super(label,
              labelFont, labelPaint, labelInsets,
              categoryLabelsVisible,
              categoryLabelFont, categoryLabelPaint, categoryLabelInsets,
              tickMarksVisible,
              tickMarkStroke, tickMarkPaint);

    }

    /**
     * Constructs a category axis, using default values where necessary.
     *
     * @param label  the axis label.
     */
    protected CategoryAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // category labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick marks visible (not supported anyway)
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT);

    }

}
