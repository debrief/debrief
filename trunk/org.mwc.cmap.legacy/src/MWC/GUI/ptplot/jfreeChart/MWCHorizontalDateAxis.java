/* =======================================
* JFreeChart : a Java Chart Class Library
* =======================================
*
* Project Info:  http://www.jrefinery.com/jfreechart
* Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
*
* This file...
* $Id: MWCHorizontalDateAxis.java,v 1.2 2004/05/25 15:36:05 Ian.Mayo Exp $
*
* Original Author:  David Gilbert;
* Contributor(s):   David Li;
*
* (C) Copyright 2000, 2001 by Simba Management Limited;
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
* Changes (from 23-Jun-2001)
* --------------------------
* 23-Jun-2001 : Modified to work with null data source (DG);
* 18-Sep-2001 : Updated e-mail address (DG);
* 07-Nov-2001 : Updated configure() method (DG);
* 30-Nov-2001 : Cleaned up default values in constructor (DG);
*
*/

package MWC.GUI.ptplot.jfreeChart;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;

import MWC.GUI.ptplot.*;

/**
 * A horizontal axis that displays date values.  Used in XY plots.
 * @see com.jrefinery.chart.XYPlot
 */
public class MWCHorizontalDateAxis extends MWCDateAxis implements HorizontalAxis {

  /** A flag indicating whether or not tick labels are drawn vertically. */
  protected boolean verticalTickLabels;

  /**
   * Constructs a horizontal date axis, using default values where necessary.
   * @param label The axis label;
   */
  public MWCHorizontalDateAxis(String label) {
  }

  /**
   * Returns a flag indicating whether the tick labels are drawn 'vertically'.
   */
  public boolean getVerticalTickLabels() {
    return this.verticalTickLabels;
  }

  /**
   * Sets the flag that determines whether the tick labels are drawn 'vertically'.
   * @param flag The new value of the flag;
   */
  public void setVerticalTickLabels(boolean flag) {
    this.verticalTickLabels = flag;
  }

  /**
   * Configures the axis to work with the specified plot.  If the axis has auto-scaling, then sets
   * the maximum and minimum values.
   */
  public void configure() {
    this.autoAdjustRange();
  }

  /**
   * Translates the data value to the display coordinates (Java 2D User Space) of the chart.
   * @param dataValue The value to be plotted;
   * @param plotArea The plot area in Java 2D User Space.
   */
  public double translatedValue(Number dataValue, Rectangle2D plotArea) {
    double value = dataValue.doubleValue();
    double axisMin = (double)minimumDate.getTime();
    double axisMax = (double)maximumDate.getTime();
    double plotX = plotArea.getX();
    double plotMaxX = plotArea.getMaxX();
    return plotX + ((value - axisMin)/(axisMax - axisMin)) * (plotMaxX - plotX);
  }

  /**
   * Translates the data value to the display coordinates (Java 2D User Space) of the chart.
   * @param date The date to be plotted;
   * @param plotArea The plot area in Java 2D User Space.
   */
  public double translatedValue(Date date, Rectangle2D plotArea, Rectangle2D drawArea) {
    double value = (double)date.getTime();
    double axisMin = (double)minimumDate.getTime();
    double axisMax = (double)maximumDate.getTime();
    double plotX = drawArea.getX();
    double plotMaxX = drawArea.getMaxX();
    return plotX + ((value - axisMin)/(axisMax - axisMin)) * (plotMaxX - plotX);
  }

  /**
   * Rescales the axis to ensure that all data is visible.
   */
  public void autoAdjustRange() {
    if (plot!=null) {

//      if (plot instanceof HorizontalValuePlot) {
      //       HorizontalValuePlot hvp = (HorizontalValuePlot)plot;

      long upper = (long)plot.getXRange()[1];
//      Number u = hvp.getMaximumHorizontalDataValue();
/*      long upper = new Date().getTime()+24L*60L*60L*1000L;
      if (u!=null) {
        upper = u.longValue();
      }*/

//      Number l = hvp.getMinimumHorizontalDataValue();
      long lower = (long)plot.getXRange()[0];
/*      long lower = new Date().getTime();
      if (l!=null) {
        lower = l.longValue();
      }*/

      long range = upper-lower;
      upper = upper+(range/20);
      lower = lower-(range/20);
      this.minimumDate=new Date(lower);
      this.maximumDate=new Date(upper);
      //     }
    }
  }

  /**
   * Recalculates the ticks for the date axis.
   */
  public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {
    this.ticks.clear();
    Font tickLabelFont = g2.getFont();

    calculateAutoTickUnits(g2, drawArea, plotArea);

    Date tickDate = this.calculateLowestVisibleTickValue(tickUnit);
    while (tickDate.before(this.maximumDate)) {
      // work out the value, label and position
      double xx = this.translatedValue(tickDate, plotArea, drawArea);
      String tickLabel = this.tickLabelFormatter.format(tickDate);
      Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(tickLabel,
                                                                  g2.getFontRenderContext());
      float x = 0.0f;
      float y = 0.0f;
      if (this.verticalTickLabels) {
        x = (float)(xx+tickLabelBounds.getHeight()/2);
        y = (float)(plotArea.getMaxY()+tickLabelBounds.getWidth());
//        y = (float)(plotArea.getMaxY()+tickLabelInsets.top+tickLabelBounds.getWidth());
      }
      else {
        x = (float)(xx-tickLabelBounds.getWidth()/2);
        y = (float)(plotArea.getMaxY()+tickLabelBounds.getHeight());
//        y = (float)(plotArea.getMaxY()+tickLabelInsets.top+tickLabelBounds.getHeight());
      }
      Tick tick = new Tick(tickDate, tickLabel, x, y);
      ticks.add(tick);
      tickDate = this.tickUnit.addToDate(tickDate);
    }

  }

  /**
   * Draws the plot on a Java 2D graphics device (such as the screen or a printer).
   * @param g2 The graphics device.
   * @param drawArea The area within which the chart should be drawn.
   * @param plotArea The area within which the plot should be drawn (a subset of the drawArea).
   */
  public void draw(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    // draw the axis label
    if (this.label!=null) {
      Font labelFont = g2.getFont();
      FontRenderContext frc = g2.getFontRenderContext();
      Rectangle2D labelBounds = labelFont.getStringBounds(label, frc);
      LineMetrics lm = labelFont.getLineMetrics(label, frc);
      float labelx = (float)(plotArea.getX()+plotArea.getWidth()/2-labelBounds.getWidth()/2);
      float labely = (float)(drawArea.getMaxY()
              -lm.getDescent()-lm.getLeading());
      g2.drawString(label, labelx, labely);
    }

    // draw the tick labels and marks
    this.refreshTicks(g2, drawArea, plotArea);
    float maxY = (float)plotArea.getMaxY();


    Iterator<Tick> iterator = ticks.iterator();
    while (iterator.hasNext())
    {
      Tick tick = (Tick)iterator.next();
      float xx = (float)this.translatedValue(tick.getNumericalValue(), plotArea);

      if (this.verticalTickLabels) {
        drawVerticalString(tick.getText(), g2, tick.getX(), tick.getY());
      }
      else {
        g2.drawString(tick.getText(), tick.getX(), tick.getY());
      }

      Line2D mark = new Line2D.Float(xx, maxY-2, xx, maxY+2);
      g2.draw(mark);

    }

  }

  /**
   * Returns the height required to draw the axis in the specified draw area.
   * @param g2 The graphics device;
   * @param plot1 The plot that the axis belongs to;
   * @param drawArea The area within which the plot should be drawn.
   */
  public double reserveHeight(Graphics2D g2, PlotBox plot1, Rectangle2D drawArea) {
//
//    // calculate the height of the axis label...
//    double labelHeight = 0.0;
//    if (label!=null) {
//      LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
//      labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;
//    }
//
//    // calculate the height required for the tick labels (if visible);
//    double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
//    if (tickLabelsVisible) {
//      g2.setFont(tickLabelFont);
//      this.refreshTicks(g2, drawArea, drawArea);
//      tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
//                                                              this.verticalTickLabels);
//    }
//    return labelHeight+tickLabelHeight;
//
    return -1;
  }

  /**
   * Returns area in which the axis will be displayed.
   */
  public Rectangle2D reserveAxisArea(Graphics2D g2, PlotBox plot1, Rectangle2D drawArea,
                                     double reservedWidth) {
//
//    // calculate the height of the axis label...
//    LineMetrics metrics = labelFont.getLineMetrics(label, g2.getFontRenderContext());
//    double labelHeight = this.labelInsets.top+metrics.getHeight()+this.labelInsets.bottom;
//
//    // calculate the height required for the tick labels (if visible);
//    double tickLabelHeight = tickLabelInsets.top+tickLabelInsets.bottom;
//    if (tickLabelsVisible) {
//      g2.setFont(tickLabelFont);
//      this.refreshTicks(g2, drawArea, drawArea);
//      tickLabelHeight = tickLabelHeight+getMaxTickLabelHeight(g2, drawArea,
//                                                              this.verticalTickLabels);
//    }
//
//    return new Rectangle2D.Double(drawArea.getX(), drawArea.getMaxY(),
//                                  drawArea.getWidth()-reservedWidth,
//                                  labelHeight+tickLabelHeight);
//
    return null;
  }

  /**
   * Determines an appropriate tick value for the axis...
   */
  private void calculateAutoTickUnits(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    // find the index of the largest standard tick magnitude that fits into the axis range
    int index = this.findAxisMagnitudeIndex();
    boolean labelsFit = true;
    while (labelsFit && index>0) {
      index=index-1;
      labelsFit = tickLabelsFit(index, g2, drawArea, plotArea);
    }

    if (labelsFit) {
      this.autoTickIndex=index;
    }
    else {
      this.autoTickIndex=Math.min(index+1, MWCDateAxis.standardTickUnitMagnitudes.length);
    }

    this.tickLabelFormatter.applyPattern(MWCDateAxis.standardTickFormats[autoTickIndex]);
    this.tickUnit = new DateUnit(MWCDateAxis.standardTickUnits[autoTickIndex][0],
                                 MWCDateAxis.standardTickUnits[autoTickIndex][1]);

    // there are two special cases to handle
    // (1) the highest index doesn't fit, but there is no "next one up" to use;
    // (2) the lowest index DOES fit, so we should use it rather than the next one up
    // otherwise, step up one index and use it
  }

  /**
   * Determines whether or not the tick labels fit given the available space.
   */
  private boolean tickLabelsFit(int index,
                                Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

    // generate one label at a time until all are done OR there is an overlap (so fit==FALSE)
    SimpleDateFormat dateFormatter = new SimpleDateFormat(standardTickFormats[index]);
    DateUnit units = new DateUnit(MWCDateAxis.standardTickUnits[index][0],
                                  MWCDateAxis.standardTickUnits[index][1]);
    double lastLabelExtent = Double.NEGATIVE_INFINITY;
    double labelExtent;
    boolean labelsFit = true;
    Date tickDate = this.calculateLowestVisibleTickValue(units);

    int ct = 0;


    while (tickDate.before(this.maximumDate) && labelsFit) {
      double xx = this.translatedValue(tickDate, plotArea, drawArea);
      String tickLabel = dateFormatter.format(tickDate);
      Rectangle2D tickLabelBounds = g2.getFont().getStringBounds(tickLabel,
                                                                  g2.getFontRenderContext());
      if (this.verticalTickLabels) {
        labelExtent = xx-(tickLabelBounds.getHeight()/2);
        if (labelExtent<lastLabelExtent) labelsFit = false;
        lastLabelExtent = xx+(tickLabelBounds.getHeight()/2);
      }
      else {
        labelExtent = xx-(tickLabelBounds.getWidth()/2);
        if (labelExtent<lastLabelExtent) labelsFit = false;
        lastLabelExtent = xx+(tickLabelBounds.getWidth()/2);
      }
      tickDate = units.addToDate(tickDate);

      ct++;
      if(ct > 1000)
        labelsFit = false;
    }

    return labelsFit;

  }

  /**
   * A utility method for determining the height of the tallest tick label.
   */
//  private double getMaxTickLabelHeight(Graphics2D g2, Rectangle2D drawArea, boolean vertical) {
//    Font font = getTickLabelFont();
//    g2.setFont(font);
//    FontRenderContext frc = g2.getFontRenderContext();
//    double maxHeight = 0.0;
//    if (vertical) {
//      Iterator iterator = this.ticks.iterator();
//      while (iterator.hasNext()) {
//        Tick tick = (Tick)iterator.next();
//        Rectangle2D labelBounds = font.getStringBounds(tick.getText(), frc);
//        if (labelBounds.getWidth()>maxHeight) {
//          maxHeight = labelBounds.getWidth();
//        }
//      }
//    }
//    else {
//      LineMetrics metrics = font.getLineMetrics("Sample", frc);
//      maxHeight = metrics.getHeight();
//    }
//    return maxHeight;
//  }

}
