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
 * ------------------------------
 * HorizontalLogarithmicAxis.java
 * ------------------------------
 * (C) Copyright 2002, by Eric Thomas and Contributors.
 *
 * Original Author:  Eric Thomas;
 * Contributor(s):   Michael Duffy;
 *                   David Gilbert (for Simba Management Limited);
 *
 * $Id: HorizontalLogarithmicAxis.java,v 1.1.1.1 2003/07/17 10:06:23 Ian.Mayo Exp $
 *
 * Changes
 * -------
 * 16-May-2002 : Version 1, based on existing HorizontalNumberAxis and VerticalLogarithmicAxis
 *               classes (ET).
 * 10-Jun-2002 : Improved to handle plots containing single data point (ET).
 * 16-Jul-2002 : Fixed for situations where ticks could appear beyond
 *               upper bounds of graph; added 'get/setAllowNegativesFlag()'
 *               methods; fixed up 'computeLogCeil/Floor()' methods (ET).
 * 25-Jul-2002 : Changed order of parameters in ValueAxis constructor (DG);
 * 05-Sep-2002 : Updated constructor for changes in the Axis class (DG);
 *
 */

package com.jrefinery.chart;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.Paint;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import com.jrefinery.data.Range;

/**
 * A logarithmic value axis, for values displayed horizontally.  Display
 * of positive values arbitrarily close to zero is supported, as well as
 * negative values (if 'allowNegativesFlag' flag set).
 *
 * @author ET
 */
public class HorizontalLogarithmicAxis extends HorizontalNumberAxis  {

    /** Useful constant for log(10). */
    public static final double LOG10_VALUE = Math.log(10);

    /** Smallest arbitrarily-close-to-zero value allowed. */
    public static final double SMALL_LOG_VALUE = 1e-25;

    /** Flag set true to allow negative values in data. */
    private boolean allowNegativesFlag;

    /** Helper flag for log axis processing. */
    private boolean smallLogFlag = false;

    /** Number formatter for generating numeric strings. */
    private final DecimalFormat numberFormatterObj = new DecimalFormat("0.00000");

    /**
     * Constructs a horizontal logarithmic axis, using default attribute values where necessary.
     */
    public HorizontalLogarithmicAxis() {

        this(null);

    }

    /**
     * Constructs a horizontal logarithmic axis, using default attribute values where necessary.
     *
     * @param label The axis label (null permitted).
     */
    public HorizontalLogarithmicAxis(String label) {

        // set the default min/max axis values for a logarithmic scale.
        this(label, Axis.DEFAULT_AXIS_LABEL_FONT, 1, 10);
        setAutoRangeAttribute(true);

    }

    /**
     * Constructs a new horizontal logarithmic axis, using default attribute values where necessary.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     */
    public HorizontalLogarithmicAxis(String label,
                                     Font labelFont,
                                     double lowerBound,
                                     double upperBound) {

        this(label,
             labelFont,
             Axis.DEFAULT_AXIS_LABEL_PAINT,
             Axis.DEFAULT_AXIS_LABEL_INSETS,
             true, // tick labels visible
             Axis.DEFAULT_TICK_LABEL_FONT,
             Axis.DEFAULT_TICK_LABEL_PAINT,
             Axis.DEFAULT_TICK_LABEL_INSETS,
             false,  // tick labels drawn vertically
             true, // tick marks visible
             Axis.DEFAULT_TICK_STROKE,
             Axis.DEFAULT_TICK_PAINT,
             false, // no auto range selection, since the caller specified bounds
             ValueAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE,
             NumberAxis.DEFAULT_AUTO_RANGE_INCLUDES_ZERO,
             NumberAxis.DEFAULT_AUTO_RANGE_STICKY_ZERO,
             lowerBound,
             upperBound,
             false, // inverted
             true, // auto tick unit selection
             NumberAxis.DEFAULT_TICK_UNIT,
             true, // grid lines visible
             ValueAxis.DEFAULT_GRID_LINE_STROKE,
             ValueAxis.DEFAULT_GRID_LINE_PAINT,
             0.0,  // anchor value
             ValueAxis.DEFAULT_CROSSHAIR_VISIBLE,
             0.0,
             ValueAxis.DEFAULT_CROSSHAIR_STROKE,
             ValueAxis.DEFAULT_CROSSHAIR_PAINT,
             false);  //'allowNegativesFlag' set false for no values < 0
    }

    /**
     * Constructs a horizontal number axis.
     *
     * @param label  the axis label (null permitted).
     * @param labelFont  the font for displaying the axis label.
     * @param labelPaint  the paint used to draw the axis label.
     * @param labelInsets  determines the amount of blank space around the label.
     * @param tickLabelsVisible  flag indicating whether or not tick labels are visible.
     * @param tickLabelFont  the font used to display tick labels.
     * @param tickLabelPaint  the paint used to draw tick labels.
     * @param tickLabelInsets  determines the amount of blank space around tick labels.
     * @param verticalTickLabels  if <code>true</code> use vertical tick labels.
     * @param tickMarksVisible  flag indicating whether or not tick marks are visible.
     * @param tickMarkStroke  the stroke used to draw tick marks (if visible).
     * @param tickMarkPaint  the stroke used to draw tick marks (if visible).
     * @param autoRange  flag indicating whether the axis is automatically scaled to fit the data.
     * @param autoRangeMinimumSize  the smallest range allowed when the axis range is calculated to
     *                              fit the data.
     * @param autoRangeIncludesZero  a flag indicating whether or not zero *must* be displayed on
     *                               the axis.
     * @param autoRangeStickyZero  a flag controlling the axis margins around zero.
     * @param lowerBound  the lowest value shown on the axis.
     * @param upperBound  the highest value shown on the axis.
     * @param inverted  a flag indicating whether the axis is normal or inverted (inverted means
     *                  running from positive to negative).
     * @param autoTickUnitSelection  a flag indicating whether or not the tick units are selected
     *                               automatically.
     * @param tickUnit  the tick unit.
     * @param gridLinesVisible  flag indicating whether grid lines are visible for this axis.
     * @param gridStroke  the pen/brush used to display grid lines (if visible).
     * @param gridPaint  the color used to display grid lines (if visible).
     * @param anchorValue  the anchor value.
     * @param crosshairVisible  whether to show a crosshair.
     * @param crosshairValue  the value at which to draw an optional crosshair (null permitted).
     * @param crosshairStroke  the pen/brush used to draw the crosshair.
     * @param crosshairPaint  the color used to draw the crosshair.
     * @param allowNegativesFlag  <code>true<code> to allow plotting of negative values;
     *        <code>false</code> if all positive values, thus allowing values less than 1.0 and
     *        arbitrarily close to zero to be plotted correctly.
     */
    public HorizontalLogarithmicAxis(String label,
                                     Font labelFont, Paint labelPaint, Insets labelInsets,
                                     boolean tickLabelsVisible,
                                     Font tickLabelFont, Paint tickLabelPaint,
                                     Insets tickLabelInsets,
                                     boolean verticalTickLabels,
                                     boolean tickMarksVisible,
                                     Stroke tickMarkStroke, Paint tickMarkPaint,
                                     boolean autoRange,
                                     Number autoRangeMinimumSize,
                                     boolean autoRangeIncludesZero,
                                     boolean autoRangeStickyZero,
                                     double lowerBound, double upperBound,
                                     boolean inverted,
                                     boolean autoTickUnitSelection,
                                     NumberTickUnit tickUnit,
                                     boolean gridLinesVisible, Stroke gridStroke, Paint gridPaint,
                                     double anchorValue,
                                     boolean crosshairVisible, double crosshairValue,
                                     Stroke crosshairStroke, Paint crosshairPaint,
                                     boolean allowNegativesFlag) {

        super(label,
              labelFont, labelPaint, labelInsets,
              tickLabelsVisible,
              tickLabelFont, tickLabelPaint, tickLabelInsets,
              verticalTickLabels,
              tickMarksVisible,
              tickMarkStroke, tickMarkPaint,
              autoRange,
              autoRangeMinimumSize,
              autoRangeIncludesZero, autoRangeStickyZero,
              lowerBound, upperBound,
              inverted,
              autoTickUnitSelection, tickUnit,
              gridLinesVisible, gridStroke, gridPaint,
              anchorValue,
              crosshairVisible, crosshairValue, crosshairStroke, crosshairPaint);

        this.allowNegativesFlag = allowNegativesFlag;  //save flag
        if (!autoRange) {                              //if not auto-ranging then
            setupSmallLogFlag();                       //setup flag based on bounds values
        }
    }

    /**
     * Sets the 'allowNegativesFlag' flag; true to allow negative values
     * in data, false to be able to plot positive values arbitrarily close to zero.
     *
     * @param flgVal  the new value of the flag.
     */
    public void setAllowNegativesFlag(boolean flgVal) {
        allowNegativesFlag = flgVal;
    }

    /**
     * Returns the 'allowNegativesFlag' flag; true to allow negative values
     * in data, false to be able to plot positive values arbitrarily close
     * to zero.
     *
     * @return the flag.
     */
    public boolean getAllowNegativesFlag() {
        return allowNegativesFlag;
    }

    /**
     * Overridden version that calls original and then sets up flag for
     * log axis processing.
     *
     * @param range  the new range.
     */
    public void setRange(Range range) {
        super.setRange(range);      // call parent method
        setupSmallLogFlag();        // setup flag based on bounds values
    }

    /**
     * Sets up flag for log axis processing.
     */
    protected void setupSmallLogFlag() {
        // set flag true if negative values not allowed and the
        // lower bound is between 0 and 10:
        final double lowerVal = getRange().getLowerBound();
        smallLogFlag = (!allowNegativesFlag && lowerVal < 10.0 && lowerVal > 0.0);
    }


    /**
     * Converts a data value to a coordinate in Java2D space, assuming that
     * the axis runs along one edge of the specified plotArea.
     * Note that it is possible for the coordinate to fall outside the
     * plotArea.
     *
     * @param value the data value.
     * @param plotArea the area for plotting the data.
     *
     * @return The Java2D coordinate.
     */
    public double translateValueToJava2D(double value, Rectangle2D plotArea) {

        Range range = getRange();
        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());

        double maxX = plotArea.getMaxX();
        double minX = plotArea.getMinX();

        value = switchedLog10(value);

        if (isInverted()) {
            return maxX - (((value - axisMin) / (axisMax - axisMin)) * (maxX - minX));
        }
        else {
            return minX + (((value - axisMin) / (axisMax - axisMin)) * (maxX - minX));
        }

    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data
     * value, assuming that the axis runs along one edge of the specified plotArea.
     *
     * @param java2DValue the coordinate in Java2D space.
     * @param plotArea the area in which the data is plotted.
     *
     * @return The data value.
     */
    public double translateJava2DtoValue(float java2DValue, Rectangle2D plotArea) {

        Range range = getRange();
        double axisMin = switchedLog10(range.getLowerBound());
        double axisMax = switchedLog10(range.getUpperBound());

        double plotX = plotArea.getX();
        double plotMaxX = plotArea.getMaxX();

        if (isInverted()) {
          return axisMax
                 - Math.pow(10, ((java2DValue - plotX) / (plotMaxX - plotX)) * (axisMax - axisMin));
        }
        else {
          return axisMin
                 + Math.pow(10, ((java2DValue - plotX) / (plotMaxX - plotX)) * (axisMax - axisMin));
        }
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    public void autoAdjustRange() {

        if (plot instanceof HorizontalValuePlot) {
            HorizontalValuePlot hvp = (HorizontalValuePlot) plot;

            Range r = hvp.getHorizontalDataRange();
            if (r == null) {
              r = new Range(DEFAULT_LOWER_BOUND, DEFAULT_UPPER_BOUND);
            }

            double lower = computeLogFloor(r.getLowerBound());
            if (!allowNegativesFlag && lower >= 0.0 && lower < SMALL_LOG_VALUE) {
                //negatives not allowed and lower range bound is zero
                lower = r.getLowerBound();    //use data range bound instead
            }

            double upper = r.getUpperBound();

            if (!allowNegativesFlag && upper < 1.0 && upper > 0.0 && lower > 0.0) {
                  //negatives not allowed and upper bound between 0 & 1
                        //round up to nearest significant digit for bound:
                                                 //get negative exponent:
              double expVal = Math.log(upper) / LOG10_VALUE;
              expVal = Math.ceil(-expVal + 0.001); //get positive exponent
              expVal = Math.pow(10, expVal);      //create multiplier value
                        //multiply, round up, and divide for bound value:
              upper = (expVal > 0.0) ? Math.ceil(upper * expVal) / expVal : Math.ceil(upper);
            }
            else {  //negatives allowed or upper bound not between 0 & 1
              upper = Math.ceil(upper);     //use nearest integer value
            }
            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize().doubleValue();
            if (upper - lower < minRange) {
              upper = (upper + lower + minRange) / 2;
              lower = (upper + lower - minRange) / 2;
            }

            setRangeAttribute(new Range(lower, upper));

            setupSmallLogFlag();      //setup flag based on bounds values
        }
    }

    /**
     * Returns the smallest (closest to negative infinity) double value that is
     * not less than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param upper a double value above which a ceiling will be calcualted.
     *
     * @return 10<sup>N</sup> with N ... { 1 .. MAX_LONG }.
     */
    private double computeLogCeil(double upper) {

        double logCeil;
        if (upper > 10.0) {     //parameter value is > 10
          // The Math.log() function is based on e not 10.
          logCeil = Math.log(upper) / LOG10_VALUE;
          logCeil = Math.ceil(logCeil);
          logCeil = Math.pow(10, logCeil);
        }
        else {
          if (upper < -10.0) {     //parameter value is < -10
                   //calculate log using positive value:
            logCeil = Math.log(-upper) / LOG10_VALUE;
                     //calculate ceil using negative value:
            logCeil = Math.ceil(-logCeil);
                     //calculate power using positive value; then negate
            logCeil = -Math.pow(10, -logCeil);
          }
          else {
                 //parameter value is -10 > val < 10
            logCeil = Math.ceil(upper);       //use as-is
          }
        }
        return logCeil;
    }

    /**
     * Returns the largest (closest to positive infinity) double value that is
     * not greater than the argument, is equal to a mathematical integer and
     * satisfying the condition that log base 10 of the value is an integer
     * (i.e., the value returned will be a power of 10: 1, 10, 100, 1000, etc.).
     *
     * @param lower a double value below which a floor will be calcualted.
     *
     * @return 10<sup>N</sup> with N ... { 1 .. MAX_LONG }.
     */
    private double computeLogFloor(double lower) {

        double logFloor;
        if (lower > 10.0) {     //parameter value is > 10
          // The Math.log() function is based on e not 10.
          logFloor = Math.log(lower) / LOG10_VALUE;
          logFloor = Math.floor(logFloor);
          logFloor = Math.pow(10, logFloor);
        }
        else {
          if (lower < -10.0) {     //parameter value is < -10
                   //calculate log using positive value:
            logFloor = Math.log(-lower) / LOG10_VALUE;
                   //calculate floor using negative value:
            logFloor = Math.floor(-logFloor);
                   //calculate power using positive value; then negate
            logFloor = -Math.pow(10, -logFloor);
          }
          else {
                 //parameter value is -10 > val < 10
            logFloor = Math.floor(lower);     //use as-is
          }
        }
        return logFloor;          //return zero
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the results in the
     * tick label list (ready for drawing).
     *
     * @param g2 The graphics device.
     * @param drawArea The area in which the plot and the axes should be drawn.
     * @param plotArea The area in which the plot should be drawn.
     */
    public void refreshTicks(Graphics2D g2, Rectangle2D drawArea, Rectangle2D plotArea) {

        ticks.clear();

        Range range = getRange();
                                  //get lower bound value:
        double lowerBoundVal = range.getLowerBound();
              //if small log values and lower bound value too small
              // then set to a small value (don't allow <= 0):
        if (smallLogFlag && lowerBoundVal < SMALL_LOG_VALUE) {
          lowerBoundVal = SMALL_LOG_VALUE;
        }
                                  //get upper bound value
        final double upperBoundVal = range.getUpperBound();

              //get log10 version of lower bound and round to integer:
        final int iBegCount = (int) Math.rint(switchedLog10(lowerBoundVal));
              //get log10 version of upper bound and round to integer:
        final int iEndCount = (int) Math.rint(switchedLog10(upperBoundVal));

        double currentTickValue;
        String tickLabel;
        boolean zeroTickFlag = false;
        for (int i = iBegCount; i <= iEndCount; i++) {
          //for each power of 10 value; create ten ticks
          for (int j = 0; j < 10; ++j) {
            //for each tick to be displayed
            if (smallLogFlag) {
              //small log values in use; create numeric value for tick
              currentTickValue = Math.pow(10, i) + (Math.pow(10, i) * j);
              if (i < 0 && currentTickValue > 0.0 && currentTickValue < 1.0) {
                //negative exponent generating tick value between 0 & 1
                if (j == 0 || (i > -4 && j < 2)
                           || currentTickValue >= upperBoundVal) {
                    //first tick of series, or not too small a value and
                   // one of first 3 ticks, or last tick to be displayed
                        //set exact number of fractional digits to be shown:
                  numberFormatterObj.setMaximumFractionDigits(-i);
                                            //create tick label:
                  tickLabel = numberFormatterObj.format(currentTickValue);
                }
                else {    //no tick label to be shown
                  tickLabel = "";
                }
              }
              else {     //tick value not between 0 & 1; if one of first
                       // 5 ticks or last tick to be displayed then show
                tickLabel = (j < 5 || currentTickValue >= upperBoundVal)
                             ? getTickUnit().valueToString(currentTickValue) : "";
              }
            }
            else { //not small log values in use; allow for values <= 0
              if (zeroTickFlag) {   //if did zero tick last iter then
                --j;              //decrement to do 1.0 tick now
              }     //calculate power-of-ten value for tick:
              currentTickValue = (i >= 0)
                  ? Math.pow(10, i) + (Math.pow(10, i) * j)
                  : -(Math.pow(10, -i) - (Math.pow(10, -i - 1) * j));
              if (!zeroTickFlag) {    //did not do zero tick last iteration
                if (Math.abs(currentTickValue - 1.0) < 0.0001
                    && lowerBoundVal <= 0.0 && upperBoundVal >= 0.0) {
                    //tick value is 1.0 and 0.0 is within data range
                  currentTickValue = 0.0;     //set tick value to zero
                  zeroTickFlag = true;        //indicate zero tick
                }
              }
              else {     //did zero tick last iteration
                zeroTickFlag = false;         //clear flag
              }               //create tick label string:
              tickLabel = getTickUnit().valueToString(currentTickValue);
            }

            if (currentTickValue > upperBoundVal) {
              return;   //if past highest data value then exit method
            }

            double xx = translateValueToJava2D(currentTickValue, plotArea);
            Rectangle2D tickLabelBounds = tickLabelFont.getStringBounds(
                                          tickLabel, g2.getFontRenderContext());
            float x = 0.0f;
            float y = 0.0f;
            if (getVerticalTickLabels()) {
              x = (float) (xx + tickLabelBounds.getHeight() / 2);
              y = (float) (plotArea.getMaxY() + tickLabelInsets.top
                                              + tickLabelBounds.getWidth());
            }
            else {
              x = (float) (xx - tickLabelBounds.getWidth() / 2);
              y = (float) (plotArea.getMaxY() + tickLabelInsets.top
                                              + tickLabelBounds.getHeight());
            }
            Tick tick = new Tick(new Double(currentTickValue), tickLabel, x, y);
            ticks.add(tick);

            //if on last tick then exit method
            if (i >= iEndCount) {
              return;
            }
          }
        }

    }

    /**
     * Returns the log10 value, depending on if values between 0 and
     * 1 are being plotted.
     *
     * @param val   value for which log10 should be calculated.
     *
     * @return log<sub>10</sub>(val).
     */
    protected double switchedLog10(double val) {
      return smallLogFlag ? Math.log(val) / LOG10_VALUE : adjustedLog10(val);
    }

    /**
     * Returns an adjusted log10 value for graphing purposes.  The first
     * adjustment is that negative values are changed to positive during
     * the calculations, and then the answer is negated at the end.  The
     * second is that, for values less than 10, an increasingly large
     * (0 to 1) scaling factor is added such that at 0 the value is
     * adjusted to 1, resulting in a returned result of 0.
     *
     * @param val   value for which log10 should be calculated.
     *
     * @return an adjusted log<sub>10</sub>(val).
     */
    public double adjustedLog10(double val) {
      final boolean negFlag;
      if (negFlag = (val < 0.0)) {
        val = -val;          //if negative then set flag and make positive
      }
      if (val < 10.0) {                   //if < 10 then
        val += (10.0 - val) / 10;        //increase so 0 translates to 0
      }        //return value; negate if original value was negative:
      return negFlag
        ? -(Math.log(val) / LOG10_VALUE)
        : (Math.log(val) / LOG10_VALUE);
    }

}
