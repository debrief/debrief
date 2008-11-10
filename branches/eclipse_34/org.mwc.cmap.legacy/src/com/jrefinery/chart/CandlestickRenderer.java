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
 * ------------------------
 * CandlestickRenderer.java
 * ------------------------
 * (C) Copyright 2001, 2002, by Simba Management Limited.
 *
 * Original Authors:  David Gilbert (for Simba Management Limited);
 *                    Sylvain Vieujot;
 * Contributor(s):    Richard Atkinson;
 *
 * $Id: CandlestickRenderer.java,v 1.1.1.1 2003/07/17 10:06:20 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 13-Dec-2001 : Version 1.  Based on code in the CandlestickPlot class, written by Sylvain
 *               Vieujot, which now is redundant (DG);
 * 23-Jan-2002 : Added DrawInfo parameter to drawItem(...) method (DG);
 * 28-Mar-2002 : Added a property change listener mechanism so that renderers no longer need to be
 *               immutable.  Added properties for up and down colors (DG);
 * 04-Apr-2002 : Updated with new automatic width calculation and optional volume display,
 *               contributed by Sylvain Vieujot (DG);
 * 09-Apr-2002 : Removed translatedRangeZero from the drawItem(...) method, and changed the return
 *               type of the drawItem method to void, reflecting a change in the XYItemRenderer
 *               interface.  Added tooltip code to drawItem(...) method (DG);
 * 25-Jun-2002 : Removed redundant code (DG);
 * 05-Aug-2002 : Small modification to drawItem method to support URLs for HTML image maps (RA);
 * 19-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Paint;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.Composite;
import java.awt.AlphaComposite;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import com.jrefinery.data.XYDataset;
import com.jrefinery.data.HighLowDataset;
import com.jrefinery.chart.entity.EntityCollection;
import com.jrefinery.chart.entity.XYItemEntity;
import com.jrefinery.chart.tooltips.XYToolTipGenerator;
import com.jrefinery.chart.tooltips.HighLowToolTipGenerator;

/**
 * A renderer that draws candlesticks on an XY plot (requires a HighLowDataset).
 *
 * @author SV
 */
public class CandlestickRenderer extends AbstractXYItemRenderer implements XYItemRenderer {

    /** The candle width. */
    private double candleWidth;

    /** The paint used to fill the candle when the price moved up from open to close. */
    private Paint upPaint;

    /** The paint used to fill the candle when the price moved down from open to close. */
    private Paint downPaint;

    /** A flag controlling whether or not volume bars are drawn on the chart. */
    private boolean drawVolume;

    /**
     * Creates a new renderer for candlestick charts.
     */
    public CandlestickRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated automatically.
     *
     * @param candleWidth  The candle width.
     */
    public CandlestickRenderer(double candleWidth) {

        this(candleWidth, true, new HighLowToolTipGenerator());

    }

    /**
     * Creates a new renderer for candlestick charts.
     * <P>
     * Use -1 for the candle width if you prefer the width to be calculated automatically.
     *
     * @param candleWidth  The candle width.
     * @param drawVolume  A flag indicating whether or not volume bars should be drawn.
     * @param toolTipGenerator  The tool tip generator. <code>null</code> is none.
     */
    public CandlestickRenderer(double candleWidth, boolean drawVolume,
                               XYToolTipGenerator toolTipGenerator) {

        super(toolTipGenerator);
        this.candleWidth = candleWidth;
        this.drawVolume = drawVolume;
        this.upPaint = Color.green;
        this.downPaint = Color.red;

    }

    /**
     * Returns the width of each candle.
     *
     * @return The candle width.
     */
    public double getCandleWidth() {
        return this.candleWidth;
    }

    /**
     * Sets the candle width.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the candle width automatically based on the space available on the chart.
     *
     * @param width  The width.
     */
    public void setCandleWidth(double width) {

        if (width != this.candleWidth) {
            Double old = new Double(this.candleWidth);
            this.candleWidth = width;
            this.firePropertyChanged("CandleStickRenderer.candleWidth", old, new Double(width));
        }

    }

    /**
     * Returns the paint used to fill candles when the price moves up from open
     * to close.
     *
     * @return The paint.
     */
    public Paint getUpPaint() {
        return this.upPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves up from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.upPaint" property has changed.
     *
     * @param paint The paint.
     */
    public void setUpPaint(Paint paint) {

        Paint old = this.upPaint;
        this.upPaint = paint;
        this.firePropertyChanged("CandleStickRenderer.upPaint", old, paint);

    }

    /**
     * Returns the paint used to fill candles when the price moves down from
     * open to close.
     *
     * @return The paint.
     */
    public Paint getDownPaint() {
        return this.downPaint;
    }

    /**
     * Sets the paint used to fill candles when the price moves down from open
     * to close.
     * <P>
     * Registered property change listeners are notified that the
     * "CandleStickRenderer.downPaint" property has changed.
     *
     * @param paint  The paint.
     */
    public void setDownPaint(Paint paint) {

        Paint old = this.upPaint;
        this.downPaint = paint;
        this.firePropertyChanged("CandleStickRenderer.downPaint", old, paint);

    }

    /**
     * Returns a flag indicating whether or not volume bars are drawn on the
     * chart.
     *
     * @return <code>true</code> if volume bars are drawn on the chart.
     */
    public boolean drawVolume() {
        return this.drawVolume;
    }

    /**
     * Sets a flag that controls whether or not volume bars are drawn in the
     * background.
     *
     * @param flag The flag.
     */
    public void setDrawVolume(boolean flag) {

        if (this.drawVolume != flag) {
            this.drawVolume = flag;
            this.firePropertyChanged("CandlestickRenderer.drawVolume", null, new Boolean(flag));
        }

    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  The graphics device.
     * @param dataArea  The area within which the plot is being drawn.
     * @param info  Collects info about the drawing.
     * @param plot  The plot (can be used to obtain standard color information etc).
     * @param domainAxis  The domain axis.
     * @param rangeAxis  The range axis.
     * @param data  The dataset.
     * @param series  The series index.
     * @param item  The item index.
     * @param crosshairInfo  Information about crosshairs on a plot.
     */
    public void drawItem(Graphics2D g2, Rectangle2D dataArea,
                         ChartRenderingInfo info,
                         XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis,
                         XYDataset data, int series, int item,
                         CrosshairInfo crosshairInfo) {

        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getEntityCollection();
        }

        HighLowDataset highLowData = (HighLowDataset) data;

        Number x = highLowData.getXValue(series, item);
        Number yHigh = highLowData.getHighValue(series, item);
        Number yLow = highLowData.getLowValue(series, item);
        Number yOpen = highLowData.getOpenValue(series, item);
        Number yClose = highLowData.getCloseValue(series, item);

        double xx = domainAxis.translateValueToJava2D(x.doubleValue(), dataArea);
        double yyHigh = rangeAxis.translateValueToJava2D(yHigh.doubleValue(), dataArea);
        double yyLow = rangeAxis.translateValueToJava2D(yLow.doubleValue(), dataArea);
        double yyOpen = rangeAxis.translateValueToJava2D(yOpen.doubleValue(), dataArea);
        double yyClose = rangeAxis.translateValueToJava2D(yClose.doubleValue(), dataArea);

        double exactCandleWidth = candleWidth;
        double thisCandleWidth = candleWidth;
        if (candleWidth <= 0.0) {
            int itemCount = highLowData.getItemCount(series);
            exactCandleWidth = (dataArea.getMaxX() - dataArea.getMinX()) / itemCount * 4.5 / 7;
            if (exactCandleWidth < 1) {
                exactCandleWidth = 1;
            }
            thisCandleWidth = exactCandleWidth;
            if (thisCandleWidth < 3) {
                thisCandleWidth = 3;
            }
        }

        Paint p = plot.getSeriesPaint(series);
        Stroke s = plot.getSeriesStroke(series);

        g2.setStroke(s);

        if (drawVolume) {
            int volume = highLowData.getVolumeValue(series, item).intValue();
            int maxVolume = 1;
            int maxCount = highLowData.getItemCount(series);
            for (int i = 0; i < maxCount; i++) {
                int thisVolume = highLowData.getVolumeValue(series, i).intValue();
                if (thisVolume > maxVolume) {
                    maxVolume = thisVolume;
                }
            }
            double drawVolume = volume / (double) maxVolume;

            double minY = dataArea.getMinY();
            double maxY = dataArea.getMaxY();

            double yyVolume = drawVolume * (maxY - minY);

            g2.setPaint(Color.gray);
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

            g2.fill(new Rectangle2D.Double(xx - exactCandleWidth / 2,
                                           maxY - yyVolume, exactCandleWidth, yyVolume));

            g2.setComposite(originalComposite);
        }

        g2.setPaint(p);

        // draw the upper shadow
        if ((yyHigh < yyOpen) && (yyHigh < yyClose)) {
            g2.draw(new Line2D.Double(xx, yyHigh, xx,
                Math.min(yyOpen, yyClose)));
        }

        // draw the lower shadow
        if ((yyLow > yyOpen) && (yyLow > yyClose)) {
            g2.draw(new Line2D.Double(xx, yyLow, xx,
                Math.max(yyOpen, yyClose)));
        }


        // draw the body
        Shape body = null;
        if (yyOpen > yyClose) {
            body = new Rectangle2D.Double(xx - thisCandleWidth / 2, yyClose,
                                          thisCandleWidth, yyOpen - yyClose);
            if (upPaint != null) {
                g2.setPaint(upPaint);
                g2.fill(body);
            }
            g2.setPaint(p);
            g2.draw(body);
        }
        else {
            body = new Rectangle2D.Double(xx - thisCandleWidth / 2, yyOpen,
                                          thisCandleWidth, yyClose - yyOpen);
            if (downPaint != null) {
                g2.setPaint(downPaint);
            }
            g2.fill(body);
            g2.setPaint(p);
            g2.draw(body);
        }

        // add an entity for the item...
        if (entities != null) {
            String tip = "";
            if (getToolTipGenerator() != null) {
                tip = getToolTipGenerator().generateToolTip(data, series, item);
            }
            String url = null;
            if (getURLGenerator() != null) {
                url = getURLGenerator().generateURL(data, series, item);
            }
            XYItemEntity entity = new XYItemEntity(body, tip, url, series, item);
            entities.addEntity(entity);
        }

    }

}
