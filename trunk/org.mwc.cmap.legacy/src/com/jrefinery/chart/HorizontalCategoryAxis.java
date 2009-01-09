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
 * ---------------------------
 * HorizontalCategoryAxis.java
 * ---------------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Contributor(s):   Jean-Luc SCHWAB;
 *                   Jon Iles;
 *                   Rich Unger;
 *
 * $Id: HorizontalCategoryAxis.java,v 1.2 2007/01/04 16:33:15 ian.mayo Exp $
 *
 * Changes (from 23-Jun-2001)
 * --------------------------
 * 23-Jun-2001 : Modified to work with null data source (DG);
 * 18-Sep-2001 : Updated header and fixed DOS encoding problem (DG);
 * 16-Oct-2001 : Moved data source classes to com.jrefinery.data.* (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 07-Nov-2001 : Updated configure() method (DG);
 * 23-Jan-2002 : Fixed bugs causing null pointer exceptions when axis label is null (DG);
 * 20-Feb-2002 : Adjusted x-coordinate for vertical category labels (DG);
 * 06-Mar-2002 : Updated import statements (DG);
 * 19-Apr-2002 : Added facility to set axis visibility on or off.  Also drawVerticalString(...) is
 *               now drawRotatedString(...) in RefineryUtilities (DG);
 * 30-Apr-2002 : Category labels now wrap to multiple lines if necessary, thanks to
 *               Jean-Luc SCHWAB (DG);
 * 12-Jul-2002 : Added code to (optionally) hide some category labels to avoid overlapping.
 *               Submitted by Jon Iles (DG)
 * 05-Sep-2002 : Updated constructor for changes in Axis class (DG);
 * 25-Sep-2002 : Fixed vertical category labels to observe skipping, as suggested by Rich
 *               Unger, and fixed errors reported by Checkstyle (DG);
 * 04-Oct-2002 : Added setVerticalTickLabels(boolean) method (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import com.jrefinery.data.CategoryDataset;
import com.jrefinery.ui.RefineryUtilities;
import com.jrefinery.chart.event.AxisChangeEvent;

/**
 * A horizontal axis that displays categories.  Used for bar charts and line charts.
 * <P>
 * Note: the axis needs to rely on the plot for assistance with the placement
 * of category labels, since the plot controls how the categories are distributed.
 *
 * @author DG
 */
public class HorizontalCategoryAxis extends CategoryAxis implements HorizontalAxis {

    /** A flag that indicates whether or not the category labels should be drawn vertically.
     */
    private boolean verticalCategoryLabels;

    /**
     * A flag that indicates whether or not some category labels should be
     * omitted in order to avoid overlapping.
     */
    private boolean skipCategoryLabelsToFit;

    /** Tick height */
    private int tickHeight;

    /**
     * Constructs a HorizontalCategoryAxis, using default values where necessary.
     */
    public HorizontalCategoryAxis() {

        this(null, // label
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // category labels visible
             false, // vertical category labels
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT);

    }

    /**
     * Constructs a HorizontalCategoryAxis, using default values where necessary.
     *
     * @param label  the axis label (null permitted).
     */
    public HorizontalCategoryAxis(String label) {

        this(label,
             Axis.DEFAULT_AXIS_LABEL_FONT,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // category labels visible
             false, // vertical category labels
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT);

    }

    /**
     * Constructs a HorizontalCategoryAxis.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param categoryLabelsVisible  a flag indicating whether or not category labels are visible.
     * @param verticalCategoryLabels  a flag indicating whether or not the category labels are
     *                                drawn vertically.
     * @param categoryLabelFont  the font used to display category labels.
     * @param categoryLabelPaint  the paint used to draw category labels.
     * @param categoryLabelInsets  determines the blank space around each category label.
     * @param tickMarksVisible  a flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the paint used to draw tick marks (if visible).
     */
    public HorizontalCategoryAxis(String label,
                                  Font labelFont, Paint labelPaint, Insets labelInsets,
                                  boolean categoryLabelsVisible,
                                  boolean verticalCategoryLabels,
                                  Font categoryLabelFont, Paint categoryLabelPaint,
                                  Insets categoryLabelInsets,
                                  boolean tickMarksVisible,
                                  Stroke tickMarkStroke, Paint tickMarkPaint) {

        super(label, labelFont, labelPaint, labelInsets,
              categoryLabelsVisible, categoryLabelFont, categoryLabelPaint,
              categoryLabelInsets,
              tickMarksVisible, tickMarkStroke, tickMarkPaint);

        this.verticalCategoryLabels = verticalCategoryLabels;

    }

    /**
     * Returns a flag indicating whether the category labels are drawn 'vertically'.
     *
     * @return the flag.
     */
    public boolean getVerticalCategoryLabels() {
        return this.verticalCategoryLabels;
    }

    /**
     * Sets the flag that determines whether the category labels are drawn 'vertically'.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalCategoryLabels(boolean flag) {

        if (this.verticalCategoryLabels != flag) {
            this.verticalCategoryLabels = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Sets the flag that determines whether the category labels are drawn 'vertically'.
     * <P>
     * You should use the setVerticalCategoryLabels method - this method just passed over to
     * it anyway.
     *
     * @param flag  the new value of the flag.
     */
    public void setVerticalTickLabels(boolean flag) {
        setVerticalCategoryLabels(flag);
    }

    /**
     * Returns the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @return The flag.
     */
    public boolean getSkipCategoryLabelsToFit() {
        return this.skipCategoryLabelsToFit;
    }

    /**
     * Sets the flag that determines whether the category labels are to be
     * skipped to avoid overlapping.
     *
     * @param flag  the new value of the flag.
     */
    public void setSkipCategoryLabelsToFit(boolean flag) {

        if (this.skipCategoryLabelsToFit != flag) {
            this.skipCategoryLabelsToFit = flag;
            notifyListeners(new AxisChangeEvent(this));
        }

    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a printer).
     *
     * @param g2  the graphics device.
     * @param drawArea  the area within which the axis should be drawn.
     * @param plotArea  the area within which the plot is being drawn.
     */
    public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        if (!visible) {
            return;
        }

        // draw the axis label...
        if (label != null) {
            g2.setFont(labelFont);
            g2.setPaint(labelPaint);
            FontRenderContext frc = g2.getFontRenderContext();
            Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
            LineMetrics lm = labelFont.getLineMetrics(label, frc);
            float labelx = (float) (plotArea.getX() + plotArea.getWidth() / 2
                                                    - labelBounds.getWidth() / 2);
            float labely = (float) (drawArea.getMaxY() - labelInsets.bottom
                                                       - lm.getDescent()
                                                       - lm.getLeading());
            g2.drawString(label, labelx, labely);
        }

        // draw the category labels
        if (this.tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            g2.setPaint(tickLabelPaint);
            this.refreshTicks(g2, drawArea, plotArea);
            Iterator<Tick> iterator = ticks.iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                if (obj instanceof Tick) {
                    Tick tick = (Tick) obj;
                    if (this.verticalCategoryLabels) {
                        RefineryUtilities.drawRotatedString(tick.getText(), g2,
                                                            tick.getX(), tick.getY(),
                                                            -Math.PI / 2);
                    }
                    else {
                        g2.drawString(tick.getText(), tick.getX(), tick.getY());
                    }
                }
                else {
                    Tick[] ts = (Tick[]) obj;
                    for (int i = 0; i < ts.length; i++) {
                        g2.drawString(ts[i].getText(), ts[i].getX(), ts[i].getY());
                    }
                }
            }
        }

    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param plotArea  the area where the plot and axes will be drawn.
     * @param dataArea  the area inside the axes.
     */
    @SuppressWarnings("unchecked")
		public void refreshTicks(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea) {

        this.tickHeight = 1;
        this.ticks.clear();
        CategoryPlot categoryPlot = (CategoryPlot) plot;
        CategoryDataset data = categoryPlot.getCategoryDataset();
        if (data != null) {
            FontRenderContext frc = g2.getFontRenderContext();
            Font font = this.getTickLabelFont();
            g2.setFont(font);
            int categorySkip = 0;
            int categoryIndex = 0;
            float maxWidth = (float) ((dataArea.getWidth() + dataArea.getX())
                                       / data.getCategoryCount()) * 0.9f;
            float xx = 0.0f;
            float yy = 0.0f;
            Iterator iterator = data.getCategories().iterator();
            while (iterator.hasNext()) {
                Object category = iterator.next();

                if (categorySkip != 0) {
                    ++categoryIndex;
                    --categorySkip;
                    continue;
                }

                String label1 = category.toString();
                Rectangle2D labelBounds = font.getStringBounds(label1, frc);
                LineMetrics metrics = font.getLineMetrics(label1, frc);
                float catX = (float) categoryPlot.getCategoryCoordinate(categoryIndex, dataArea);
                if (this.verticalCategoryLabels) {
                    xx = (float) (catX + labelBounds.getHeight() / 2 - metrics.getDescent());
                    yy = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                     + labelBounds.getWidth());
                    ticks.add(new Tick(category, label1, xx, yy));
                    if (this.skipCategoryLabelsToFit) {
                        categorySkip = (int) ((labelBounds.getHeight() - maxWidth / 2)
                                             / maxWidth) + 1;
                    }
                }
                else if (labelBounds.getWidth() > maxWidth) {
                    if (this.skipCategoryLabelsToFit) {
                        xx = (float) (catX - maxWidth / 2);
                        yy = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                         + metrics.getHeight()
                                                         - metrics.getDescent());
                        ticks.add(new Tick(category, label1, xx, yy));
                        categorySkip = (int) ((labelBounds.getWidth() - maxWidth / 2)
                                             / maxWidth) + 1;
                    }
                    else {
                        String[] labels = breakLine(label1, (int) maxWidth, frc);
                        Tick[] ts = new Tick[labels.length];
                        for (int i = 0; i < labels.length; i++) {
                            labelBounds = font.getStringBounds(labels[i], frc);
                            xx = (float) (catX - labelBounds.getWidth() / 2);
                            yy = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                             + (i + 1) * (metrics.getHeight()
                                                             - metrics.getDescent()));
                            ts[i] = new Tick(category, labels[i], xx, yy);
                        }
                        if (labels.length > tickHeight) {
                            tickHeight = labels.length;
                        }
                        for (int i = 0; i < ts.length; i++)
												{
													Tick tick = ts[i];
	                        ticks.add(tick);
												}
                    }
                }
                else {
                    xx = (float) (catX - labelBounds.getWidth() / 2);
                    yy = (float) (dataArea.getMaxY() + tickLabelInsets.top
                                                     + metrics.getHeight()
                                                     - metrics.getDescent());
                    ticks.add(new Tick(category, label1, xx, yy));
                }
                categoryIndex = categoryIndex + 1;
            }
        }

    }

    /**
     * Estimates the height required for the axis, given a specific drawing
     * area, without any information about the width of the vertical axis.
     * <P>
     * Supports the HorizontalAxis interface.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param plot1  the plot that the axis belongs to.
     * @param drawArea  the area within which the axis should be drawn.
     *
     * @return the estimated height required for the axis.
     */
    public double reserveHeight(Graphics2D g2, Plot plot1, Rectangle2D drawArea) {

        if (!visible) {
            return 0.0;
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top + labelInsets.bottom + labelBounds.getHeight();
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = 0.0;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
        }
        return labelHeight + tickLabelHeight;

    }

    /**
     * Returns the area required to draw the axis in the specified draw area.
     *
     * @param g2  the graphics device.
     * @param plot1  the plot that the axis belongs to.
     * @param drawArea  the area within which the plot should be drawn.
     * @param reservedWidth  the width reserved by the vertical axis.
     *
     * @return the area required to draw the axis in the specified draw area.
     */
    public Rectangle2D reserveAxisArea(Graphics2D g2, Plot plot1,
                                       Rectangle2D drawArea, double reservedWidth) {

        if (!visible) {
            return new Rectangle2D.Double(drawArea.getX(),
                                          drawArea.getMaxY(),
                                          drawArea.getWidth() - reservedWidth, 0.0);
        }

        // calculate the height of the axis label...
        double labelHeight = 0.0;
        if (label != null) {
            Rectangle2D labelBounds = labelFont.getStringBounds(label, g2.getFontRenderContext());
            labelHeight = this.labelInsets.top + labelInsets.bottom + labelBounds.getHeight();
        }

        // calculate the height required for the tick labels (if visible);
        double tickLabelHeight = 0.0;
        if (tickLabelsVisible) {
            g2.setFont(tickLabelFont);
            this.refreshTicks(g2, drawArea, drawArea);
            tickLabelHeight = tickLabelInsets.top + tickLabelInsets.bottom
                              + getMaxTickLabelHeight(g2, drawArea, this.verticalCategoryLabels);
        }
        return new Rectangle2D.Double(drawArea.getX(),
                                      drawArea.getMaxY() - labelHeight - tickLabelHeight,
                                      drawArea.getWidth() - reservedWidth,
                                      labelHeight + tickLabelHeight);

    }

    /**
     * A utility method for determining the height of the tallest tick label.
     *
     * @param g2  the graphics device.
     * @param drawArea  the drawing area.
     * @param vertical  a flag indicating whether the tick labels are drawn vertically.
     *
     * @return The maximum tick label height.
     */
    private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
        Font font = getTickLabelFont();
        g2.setFont(font);
        FontRenderContext frc = g2.getFontRenderContext();
        double maxHeight = 0.0;
        if (vertical) {
            Iterator<Tick> iterator = this.ticks.iterator();
            while (iterator.hasNext()) {
                Tick tick = (Tick) iterator.next();
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxHeight) {
                    maxHeight = labelBounds.getWidth();
                }
            }
        }
        else {
            LineMetrics metrics = font.getLineMetrics("Sample", frc);
            maxHeight = (metrics.getHeight() * tickHeight)
                        - (metrics.getDescent() * (tickHeight - 1));
        }
        return maxHeight;
    }

    /**
     * Returns the maximum width of the ticks in the working list (that is set
     * up by refreshTicks()).
     *
     * @param g2  the graphics device.
     * @param plotArea  the area within which the plot is to be drawn.
     *
     * @return the maximum width of the ticks in the working list.
     */
    protected double getMaxTickLabelWidth(Graphics2D g2, Rectangle2D plotArea) {

        double maxWidth = 0.0;
        Font font = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();

        Iterator<Tick> iterator = this.ticks.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof Tick) {
                Tick tick = (Tick) obj;
                Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
                if (labelBounds.getWidth() > maxWidth) {
                    maxWidth = labelBounds.getWidth();
                }
            }
            else {
                Tick[] ts = (Tick[]) obj;
                for (int i = 0; i < ts.length; i++) {
                    Rectangle2D labelBounds = font.getStringBounds(ts[i].getText(), frc);
                    if (labelBounds.getWidth() > maxWidth) {
                        maxWidth = labelBounds.getWidth();
                    }
                }

            }
        }
        return maxWidth;

    }

    /**
     * Returns true if the specified plot is compatible with the axis.
     *
     * @param plot1 The plot.
     *
     * @return <code>true</code> if the specified plot is compatible with the axis.
     */
    protected boolean isCompatiblePlot(Plot plot1) {

        if (plot1 instanceof VerticalCategoryPlot) {
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

    /**
     * Breaks a line
     *
     * @param text  string at break
     * @param areaWidth  width of tick area
     * @param frc  current Font Renderer Context
     *
     * @return array of breaked strings
     */
    private String[] breakLine(String text, int areaWidth, FontRenderContext frc) {

        ArrayList<String> textList = new ArrayList<String>(5);

        int currWidth = areaWidth;
        AttributedString as = new AttributedString(text, getTickLabelFont().getAttributes());
        AttributedCharacterIterator aci = as.getIterator();
        AffineTransform affine = new AffineTransform();
        for (; ;) {
            LineBreakMeasurer measurer = new LineBreakMeasurer(aci, frc);
            int maxWidth = 0, offset = 0;
            TextLayout layout = measurer.nextLayout(currWidth);
            while (layout != null) {
                textList.add(text.substring(offset, offset + layout.getCharacterCount()));
                int width = layout.getOutline(affine).getBounds().width;
                if (maxWidth < width) {
                    maxWidth = width;
                }
                offset += layout.getCharacterCount();
                layout = measurer.nextLayout(currWidth);
            }
            if (maxWidth > areaWidth) {
                currWidth -= maxWidth - currWidth;
                if (currWidth > 0) {
                    textList.clear();
                    continue;
                }
            }
            break;
        }

        String[] texts = new String[textList.size()];
        return (String[]) textList.toArray(texts);

    }
}
