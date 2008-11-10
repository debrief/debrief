/* =======================================
* JFreeChart : a Java Chart Class Library
* =======================================
*
* Project Info:  http://www.jrefinery.com/jfreechart
* Project Lead:  David Gilbert (david.gilbert@jrefinery.com);
*
* This file...
* $Id: MWCDateAxis.java,v 1.2 2004/05/25 15:36:04 Ian.Mayo Exp $
*
* Original Author:  David Gilbert;
* Contributor(s):   -;
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
* 27-Nov-2001 : Changed constructors from public to protected, updated Javadoc comments (DG);
*
*/

package MWC.GUI.ptplot.jfreeChart;

import MWC.GUI.ptplot.PlotBox;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.*;
import java.util.*;
import javax.swing.*;

/**
 * The base class for axes that display java.util.Date values.
 * @see MWCHorizontalDateAxis
 */
public class MWCDateAxis/*  extends ValueAxis*/ {

  /** the label for this axis
   *
   */
  protected String label = null;

  /** the list of ticks we plot
   *
   */
  public Vector ticks = new Vector(0,1);

  /** The earliest date showing on the axis. */
  protected Date minimumDate;

  /** The latest date showing on the axis. */
  protected Date maximumDate;

  /** The current tick unit. */
  protected DateUnit tickUnit;

  /** A formatter for the tick labels. */
  protected SimpleDateFormat tickLabelFormatter = new SimpleDateFormat("dd");

  /** the plot we are plotting into
   *
   */
  protected PlotBox plot;

  /** the tick index we are plotting
   *
   */
  protected int autoTickIndex = -1;


  /**
   * Constructs a date axis, using default values where necessary.
   * @param label The axis label.
   */
  public MWCDateAxis(String label) {
    this.label = label;
  }

  public MWCDateAxis()
  {
    this("Blank");
  }


  /** reset the axis values
   *
   */
  public void resetLimits()
  {
    minimumDate = null;
    maximumDate = null;
  }

  /**
   * Returns the earliest date visible on the axis.
   * @return The earliest date visible on the axis.
   */
  public Date getMinimumDate() {
    return this.minimumDate;
  }

  /**
   * Returns the latest date visible on the axis.
   * @return The latest date visible on the axis.
   */
  public Date getMaximumDate() {
    return this.maximumDate;
  }


  /** set the date range limits
   *
   */
  public void addValue(Date newVal)
  {
    long dVal = newVal.getTime();

    if(maximumDate == null)
    {
      maximumDate = newVal;
      minimumDate = newVal;
    }

    if(dVal > maximumDate.getTime())
      maximumDate = newVal;
    if(dVal < minimumDate.getTime())
      minimumDate = newVal;

  }


  /**
   * Returns the formatter for the tick labels.
   * @return The formatter for the tick labels.
   */
  public SimpleDateFormat getTickLabelFormatter() {
    return tickLabelFormatter;
  }

  /**
   * Calculates the value of the lowest visible tick on the axis.
   * @return The value of the lowest visible tick on the axis.
   */
  public Date calculateLowestVisibleTickValue(DateUnit unit) {
    return this.nextStandardDate(minimumDate, unit.getField(), unit.getCount());
  }

  /**
   * Calculates the value of the highest visible tick on the axis.
   * @return The value of the highest visible tick on the axis.
   */
  public Date calculateHighestVisibleTickValue(DateUnit unit) {
    return this.previousStandardDate(maximumDate, unit.getField(), unit.getCount());
  }

  /**
   * Returns the previous "standard" date (based on the specified field and units).
   */
  protected Date previousStandardDate(Date date, int field, int units) {
    int milliseconds;
    int seconds;
    int minutes;
    int hours;
    int days;
    int months;
    int years;
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int current = calendar.get(field);
    int value = units*(current/units);
    switch (field) {

      case(Calendar.MILLISECOND) : {
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DATE);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        seconds = calendar.get(Calendar.SECOND);
        calendar.set(years, months, days, hours, minutes, seconds);
        calendar.set(Calendar.MILLISECOND, value);
        return calendar.getTime();
      }

      case(Calendar.SECOND)      : {
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DATE);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(years, months, days, hours, minutes, value);
        return calendar.getTime();
      }

      case(Calendar.MINUTE)      : {
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DATE);
        hours = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(years, months, days, hours, value, 0);
        return calendar.getTime();
      }

      case(Calendar.HOUR_OF_DAY)        : {
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        days = calendar.get(Calendar.DATE);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(years, months, days, value, 0, 0);
        return calendar.getTime();
      }

      case(Calendar.DATE)        : {
        years = calendar.get(Calendar.YEAR);
        months = calendar.get(Calendar.MONTH);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(years, months, value, 0, 0, 0);
        return calendar.getTime();
      }

      case(Calendar.MONTH)       : {
        years = calendar.get(Calendar.YEAR);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(years, value, 1, 0, 0, 0);
        return calendar.getTime();
      }

      case(Calendar.YEAR)        : {
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(value, 0, 1, 0, 0, 0);
        return calendar.getTime();
      }

      default: return null;

    }
  }

  /**
   * Returns the first "standard" date (based on the specified field and units).
   */
  protected Date nextStandardDate(Date date, int field, int units) {
    Date previous = previousStandardDate(date, field, units);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(previous);
    calendar.add(field, units);
    return calendar.getTime();
  }

  /**
   * Returns the index of the largest tick unit that will fit within the axis range.
   */
  protected int findAxisMagnitudeIndex() {
    long axisMagnitude = this.maximumDate.getTime()-this.minimumDate.getTime();
    int index = 0;
    while(index<standardTickUnitMagnitudes.length-1) {
      if (axisMagnitude<standardTickUnitMagnitudes[index]) break;
      index++;
    }
    return Math.max(0, index-1);
  }


  /**
   * A utility method for drawing text vertically.
   * @param text The text.
   * @param g2 The graphics device.
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   */
  protected void drawVerticalString(String text, Graphics2D g2, float x, float y)
  {
    AffineTransform saved = g2.getTransform();

    // apply a 90 degree rotation
    AffineTransform rotate = AffineTransform.getRotateInstance(-Math.PI/2, x, y);
    g2.transform(rotate);
    g2.drawString(text, x, y);

    g2.setTransform(saved);

  }

  /**
   * The approximate magnitude of each of the standard tick units.  This array is used to find
   * an index into the standardTickUnits array.
   */
  protected static long[] standardTickUnitMagnitudes = {
    1L, 5L, 10L, 50L, 100L, 500L,
    1000L, 5*1000L, 10*1000L, 30*1000L,
    60*1000L, 5*60*1000L, 10*60*1000L, 30*60*1000L,
    60*60*1000L, 6*60*60*1000L, 12*60*60*1000L,
    24*60*60*1000L, 7*24*60*60*1000L,
    30*24*60*60*1000L, 90*24*60*60*1000L, 180*24*60*60*1000L,
    365*24*60*60*1000L, 5*365*24*60*60*1000L, 10*365*24*60*60*1000L,
    25*365*24*60*60*1000L, 50*365*24*60*60*1000L, 100*365*24*60*60*1000L
  };

  /**
   * An array of Calendar fields that will be used for automatic tick generation.
   */
  protected static int[][] standardTickUnits = {
    { Calendar.MILLISECOND, 1 },
    { Calendar.MILLISECOND, 5 },
    { Calendar.MILLISECOND, 10 },
    { Calendar.MILLISECOND, 50 },
    { Calendar.MILLISECOND, 100 },
    { Calendar.MILLISECOND, 500 },
    { Calendar.SECOND, 1 },
    { Calendar.SECOND, 5 },
    { Calendar.SECOND, 10 },
    { Calendar.SECOND, 30 },
    { Calendar.MINUTE, 1 },
    { Calendar.MINUTE, 5 },
    { Calendar.MINUTE, 10 },
    { Calendar.MINUTE, 30 },
    { Calendar.HOUR_OF_DAY, 1 },
    { Calendar.HOUR_OF_DAY, 6 },
    { Calendar.HOUR_OF_DAY, 12 },
    { Calendar.DATE, 1 },
    { Calendar.DATE, 7 },
    { Calendar.MONTH, 1 },
    { Calendar.MONTH, 3 },
    { Calendar.MONTH, 6 },
    { Calendar.YEAR, 1 },
    { Calendar.YEAR, 5 },
    { Calendar.YEAR, 10 },
    { Calendar.YEAR, 25 },
    { Calendar.YEAR, 50 },
    { Calendar.YEAR, 100 }
  };

  /**
   * An array of strings, corresponding to the tickValues array, and used to create a
   * DateFormat object for displaying tick values.
   */
  protected static String[] standardTickFormats = {
    "HH:mm:ss.SSS",
"HHmm:ss.SSS",//    "HH:mm:ss.SSS",
"HHmm:ss.SSS",//    "HH:mm:ss.SSS",
"HHmm:ss.SSS",//    "HH:mm:ss.SSS",
"HHmm:ss.SSS",//    "HH:mm:ss.SSS",
"HHmm:ss.SSS",//     "HH:mm:ss.SSS",
"ddHHmm:ss",//     "HH:mm:ss",
"ddHHmm:ss",//     "HH:mm:ss",
"ddHHmm:ss",//     "HH:mm:ss",
"ddHHmm:ss",//     "HH:mm:ss",
"ddHHmm",//     "HH:mm",
"ddHHmm",//     "HH:mm",
"ddHHmm",//     "HH:mm",
"ddHHmm",//     "HH:mm",
"ddHHmm",//    "HH:mm",
"ddHHmm MMM",//   "d-MMM, H:mm",
"ddHHmm MMM",//   "d-MMM, H:mm",
"ddHHmm MMM",//    "d-MMM-yyyy",
"ddHHmm MMM",//    "d-MMM-yyyy",
    "MMM-yyyy",
    "MMM-yyyy",
    "MMM-yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
  };
  /**
   * the original  array of strings, corresponding to the tickValues array, and used to create a
   * DateFormat object for displaying tick values.
   */
  protected static String[] OLDstandardTickFormats = {
    "HH:mm:ss.SSS",
    "HH:mm:ss.SSS",
    "HH:mm:ss.SSS",
    "HH:mm:ss.SSS",
    "HH:mm:ss.SSS",
    "HH:mm:ss.SSS",
    "HH:mm:ss",
    "HH:mm:ss",
    "HH:mm:ss",
    "HH:mm:ss",
    "HH:mm",
    "HH:mm",
    "HH:mm",
    "HH:mm",
    "HH:mm",
    "d-MMM, H:mm",
    "d-MMM, H:mm",
    "d-MMM-yyyy",
    "d-MMM-yyyy",
    "MMM-yyyy",
    "MMM-yyyy",
    "MMM-yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
    "yyyy",
  };

}
