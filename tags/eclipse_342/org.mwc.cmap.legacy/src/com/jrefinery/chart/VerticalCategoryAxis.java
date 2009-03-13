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
 * -------------------------
 * VerticalCategoryAxis.java
 * -------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Richard Atkinson;
 *
 * $Id: VerticalCategoryAxis.java,v 1.1.1.1 2003/07/17 10:06:28 Ian.Mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 23-Jan-2002 : Changed the positioning of category labels to improve centering on bars (DG);
 *               Fixed bugs causing exceptions when axis label is null (DG);
 * 06-Mar-2002 : Added accessor methods for verticalLabel attribute. Updated import statements (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 06-Aug-2002 : Modified draw method to not draw axis label if label is empty String (RA);
 * 05-Sep-2002 : Updated constructor reflecting changes in the Axis class (DG);
 * 01-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import com.jrefinery.chart.event.AxisChangeEvent;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.RefineryUtilities;

/**
 * A vertical axis that displays categories, used for horizontal bar charts.
 * <P>
 * The axis needs to rely on the plot for placement of labels, since the plot
 * controls how the categories are distributed.
 *
 * @author DG
 */
public class VerticalCategoryAxis extends CategoryAxis implements VerticalAxis {

    /** The default setting for vertical axis label. */
    public static final boolean DEFAULT_VERTICAL_LABEL = true;

    /** A flag that indicates whether or not the axis label should be drawn vertically. */
    private boolean verticalLabel;

    /**
     * Constructs a new axis, using default attributes where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    public VerticalCategoryAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             VerticalCategoryAxis.DEFAULT_VERTICAL_LABEL,
             true, // category labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT);

    }

    /**
     * Constructs a new axis.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param verticalLabel  flag indicating whether or not the axis label is drawn vertically.
     * @param categoryLabelsVisible  a flag indicating whether or not category labels are visible.
     * @param categoryLabelFont  the font used to display category labels.
     * @param categoryLabelPaint  the paint used to draw category labels.
     * @param categoryLabelInsets  the insets for the category labels.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     */
    public VerticalCategoryAxis(String label,
                                Font labelFont, Paint labelPaint, Insets labelInsets,
                                boolean verticalLabel,
                                boolean categoryLabelsVisible,
                                Font categoryLabelFont, Paint categoryLabelPaint,
                                Insets categoryLabelInsets,
                                boolean tickMarksVisible,
                                Stroke tickMarkStroke, Paint tickMarkPaint) {

        super(label, labelFont, labelPaint, labelInsets,
              categoryLabelsVisible, categoryLabelFont, categoryLabelPaint,
              categoryLabelInsets,
              tickMarksVisible, tickMarkStroke, tickMarkPaint);

        this.verticalLabel = verticalLabel;

    }

    /**
     * Returns a flag indicating whether or not the axis label is drawn vertically.
     *
     * @return The flag.
     */
    public boolean isVerticalLabel() {
        return this.verticalLabel;
    }

    /**
     * Sets a flag indicating whether or not the axis label is drawn vertically.
     * If the setting is changed, registered listeners are notified that the
     * axis has changed.
     *
     * @param flag  the flag.
     */
    public void setVerticalLabel(boolean flag) {

        if (this.verticalLabel != flag) {
            this.verticalLabel = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the axis should be drawn.
     * @param dataArea  the area within which the plot is being drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea) {

        if (!visible) {
            return;
        }

        // draw the axis label
        if (this.label == null ? false : !this.label.equals("")) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics metrics = labelFont.getLineMetrics(label, frc);
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            if (verticalLabel) {
                double xx = drawArea.getX() + labelInsets.left
                                            + metrics.getHeight()
                                            - metrics.getDescent()
                                            - metrics.getLeading();
                double yy = dataArea.getY() + dataArea.getHeight() / 2
                                            + (labelBounds.getWidth() / 2);
                RefineryUtilities.drawRotatedString(label, g2,
                                                    (float) xx, (float) yy, -Math.PI / 2);
            }
            else {
                double xx = drawArea.getX() + labelInsets.left;
                double yy = drawArea.getY() + drawArea.getHeight() / 2
                                            - labelBounds.getHeight() / 2;
                g2.drawString(label, (float) xx, (float) yy);
            }
        }

        // draw the category labels
        if (this.tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            g2.setPaint(tickLabelPaint);
            this.refreshTicks(g2, drawArea, dataArea);
            Iterator<Tick> iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                g2.drawString(tick.getText(), tick.getX(), tick.getY());
            }
        }

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param drawArea  the area where the plot and axes will be drawn.
     * @param plotArea  the area inside the axes.
     */
    @SuppressWarnings("unchecked")
		public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        this.ticks.clear();
        CategoryPlot categoryPlot = (CategoryPlot) plot;
        CategoryDataset data = categoryPlot.getCategoryDataset();
        if (data != null) {
            Font font = getTickLabelFont();
            g2.setFont(font);
            FontRenderContext frc = g2.getFontRenderContext();
            int categoryIndex = 0;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {
                Object category = iterator.next();
                String label1 = category.toString();
                Rectangle2D labelBounds = font.getStringBounds(label1, frc);
                LineMetrics metrics = font.getLineMetrics(label1, frc);

                float xx = (float) (plotArea.getX() - tickLabelInsets.right
                                                    - labelBounds.getWidth());
                float yy = (float) (categoryPlot.getCategoryCoordinate(categoryIndex, plotArea)
                                    - metrics.getStrikethroughOffset() + 0.5f);
                Tick tick = new Tick(category, label1, xx, yy);
                ticks.add(tick);
                categoryIndex = categoryIndex + 1;
            }
        }
    }

    /**
     * Estimates the height required for the axis, given a specific drawing
     * area, without any information about the width of the vertical axis.
     * <P>
     * Supports the VerticalAxis interface.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param drawArea  the area within which the axis should be drawn.
     * @param plot1  the plot that the axis belongs to.
     *
     * @return the estimated height required for the axis.
     */
    public double reserveWidth(Graphics2D g2, Plot plot1, Rectangle2D drawArea) {

        if (!visible) {
            return 0.0;
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelWidth = this.labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible);
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (tickLabelsVisible) {
            refreshTicks(g2, drawArea, drawArea);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }
        return labelWidth + tickLabelWidth;

    }

    /**
     * Returns the area required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot1  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param reservedHeight  the height reserved by the horizontal axis.
     *
     * @return  the area to reserve for the axis.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot1,
                                       Rectangle2D drawArea, double reservedHeight) {

        if (!visible) {
            return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
                                          0.0,
                                          drawArea.getHeight() - reservedHeight);
        }

        // calculate the width of the axis label...
        double labelWidth = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelWidth = this.labelInsets.left + labelInsets.right;
            if (this.verticalLabel) {
                // assume width == height before rotation
                labelWidth = labelWidth + labelBounds.getHeight();
            }
            else {
                labelWidth = labelWidth + labelBounds.getWidth();
            }
        }

        // calculate the width required for the tick labels (if visible);
        double tickLabelWidth = tickLabelInsets.left + tickLabelInsets.right;
        if (tickLabelsVisible) {
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelWidth = tickLabelWidth + getMaxTickLabelWidth(g2, drawArea);
        }

        return new Rectangle2D.Double(drawArea.getX(), drawArea.getY(),
            labelWidth + tickLabelWidth, drawArea.getHeight() - reservedHeight);

    }

    /**
     * Returns true if the specified plot is compatible with the axis, and
     * false otherwise.
     *
     * @param plot1  the plot.
     *
     * @return A boolean indicating whether or not the axis considers the plot is compatible.
     */
    protected boolean isCompatiblePlot(Plot plot1) {

        if (plot1 instanceof HorizontalCategoryPlot) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * Configures the axis against the current plot.  Nothing required in this class.
     */
    public void configure() {
    }

}
