/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package MWC.GUI.JFreeChart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;

import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.PlotState;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;

import MWC.GUI.CanvasType;
import MWC.GUI.StepperListener;
import MWC.GenericData.Duration;
import MWC.GenericData.HiResDate;

/**
 * ******************************************************************* embedded class which extends
 * free chart to give current DTG indication
 * *******************************************************************
 */
public class StepperXYPlot extends XYPlot implements StepperListener
{
  /**
   *
   */
  private static final long serialVersionUID = 1L;

  /**
   * the step control we monitor
   */
  private final StepperListener.StepperController _myStepper;

  /**
   * the current time we are looking at (or -1 for null) (micros)
   */
  protected HiResDate _currentTime = null;

  /**
   * whether to grow the axis with time
   *
   */
  protected boolean _growWithTime = false;

  /**
   * flag for if we need to reset the axes, after a grow setting
   *
   */
  private boolean _resetAxes = false;

  /**
   * whether to actually show the line
   *
   */
  private boolean _showLine = true;

  private Duration _fixedDuration;

  // ////////////////////////////////////////////////
  // constructor
  // ////////////////////////////////////////////////

  /**
   * Constructs an XYPlot with the specified axes (other attributes take default values).
   *
   * @param data
   *          The dataset.
   * @param domainAxis
   *          The domain axis.
   * @param rangeAxis
   *          The range axis.
   * @param theRenderer
   */
  public StepperXYPlot(final XYDataset data, final RelativeDateAxis domainAxis,
      final ValueAxis rangeAxis,
      final StepperListener.StepperController stepper,
      final XYItemRenderer theRenderer)
  {
    super(data, domainAxis, rangeAxis, theRenderer);
    this._myStepper = stepper;

    if (_myStepper != null)
    {
      _myStepper.addStepperListener(this);
    }
  }

  // ////////////////////////////////////////////////
  // over-ride painting support
  // ////////////////////////////////////////////////

  /**
   * Draws the XY plot on a Java 2D graphics device (such as the screen or a printer), together with
   * a current time marker
   * <P>
   * XYPlot relies on an XYItemRenderer to draw each item in the plot. This allows the visual
   * representation of the data to be changed easily.
   * <P>
   * The optional info argument collects information about the rendering of the plot (dimensions,
   * tooltip information etc). Just pass in null if you do not need this information.
   *
   * @param g2
   *          The graphics device.
   * @param plotArea
   *          The area within which the plot (including axis labels) should be drawn.
   * @param info
   *          Collects chart drawing information (null permitted).
   */
  @Override
  public final void draw(final Graphics2D g2, final Rectangle2D plotArea,
      final Point2D anchor, final PlotState state, final PlotRenderingInfo info)
  {
    super.draw(g2, plotArea, anchor, state, info);

    // do we want to view the line?
    if (!_showLine)
      return;

    // do we have a time?
    if (_currentTime != null)
    {
      // find the screen area for the dataset
      final Rectangle2D dataArea = info.getDataArea();

      // determine the time we are plotting the line at
      long theTime = _currentTime.getMicros();

      // hmmm, how do we format the date
      final CanBeRelativeToTimeStepper axis = (CanBeRelativeToTimeStepper) this
          .getDomainAxis();

      // are we working in relative time mode?
      if (axis.isRelativeTimes())
      {
        if (_myStepper != null)
        {
          // yes, we now need to offset the time
          theTime = theTime - _myStepper.getTimeZero().getMicros();
        }
      }

      // hmm, see if we are wroking with a date or number axis
      double linePosition = 0;
      if (axis instanceof DateAxis)
      {
        // ok, now scale the time to graph units
        final DateAxis dateAxis = (DateAxis) axis;

        // find the new x value
        linePosition = dateAxis.dateToJava2D(new Date(theTime / 1000), dataArea,
            this.getDomainAxisEdge());

        if (_resetAxes)
        {
          dateAxis.setAutoRange(true);
          _resetAxes = false;
        }

        if (isGrowWithTime())
        {
          final long endMillis = theTime / 1000;
          long startMillis;

          if (_fixedDuration != null)
          {
            startMillis = endMillis - _fixedDuration.getMillis();
          }
          else
          {
            startMillis = (long) dateAxis.getLowerBound();
          }

          final Date startDate = new Date(startMillis);
          final Date endDate = new Date(endMillis);

          dateAxis.setRange(startDate, endDate);
        }
        else
        {
        }

      }
      else
      {
        if (axis instanceof NumberAxis)
        {
          final NumberAxis numberAxis = (NumberAxis) axis;
          linePosition = numberAxis.valueToJava2D(theTime, dataArea, this
              .getDomainAxisEdge());

          if (isGrowWithTime())
            numberAxis.setRange(numberAxis.getRange().getLowerBound(), theTime);
          else
          {
            if (_resetAxes)
            {
              numberAxis.setAutoRange(true);
              _resetAxes = false;
            }
          }

        }
      }

      // ok, finally draw the line - if we're not showing the growing plot
      if (!isGrowWithTime())
        plotStepperLine(g2, linePosition, dataArea);

    }
  }

  public Duration getFixedDuration()
  {
    return _fixedDuration;
  }

  public boolean isGrowWithTime()
  {
    return _growWithTime;
  }

  /**
   * the current time has changed
   */
  @Override
  public final void newTime(final HiResDate oldDTG, final HiResDate newDTG,
      final CanvasType canvas)
  {
    _currentTime = newDTG;
  }

  /**
   * draw the new stepper line into the plot
   *
   * @param g2
   * @param linePosition
   * @param dataArea
   */
  protected void plotStepperLine(final Graphics2D g2, final double linePosition,
      final Rectangle2D dataArea)
  {
    // prepare to draw
    final Stroke oldStroke = g2.getStroke();
    g2.setXORMode(Color.darkGray);

    // thicken up the line
    g2.setStroke(new BasicStroke(3));

    if (this.getOrientation() == PlotOrientation.VERTICAL)
    {
      // draw the line
      g2.drawLine((int) linePosition - 1, (int) dataArea.getY() + 1,
          (int) linePosition - 1, (int) dataArea.getY() + (int) dataArea
              .getHeight() - 1);
    }
    else
    {
      // draw the line
      g2.drawLine((int) dataArea.getX() + 1, (int) linePosition - 1,
          (int) dataArea.getX() + (int) dataArea.getWidth() - 1,
          (int) linePosition - 1);

    }

    // and restore everything
    g2.setStroke(oldStroke);
    g2.setPaintMode();
  }

  @Override
  public void reset()
  {
    // don't worry about it, ignore
  }

  public void setFixedDuration(final Duration dur)
  {
    _fixedDuration = dur;

    // do we need to reset the axes?
    if (_fixedDuration == null)
      _resetAxes = true;
  }

  public void setGrowWithTime(final boolean growWithTime)
  {
    // do we need to reset the bounds?
    if (!growWithTime && isGrowWithTime())
    {
      _resetAxes = true;
    }

    this._growWithTime = growWithTime;

  }

  /**
   * @param line
   *          whether to actually show the line
   */
  public void setShowLine(final boolean line)
  {
    _showLine = line;
  }

  /**
   * the mode for stepping has changed
   */
  @Override
  public final void steppingModeChanged(final boolean on)
  {
  }

  @Override
  public void zoom(final double percent)
  {
    this.getDomainAxis().setAutoRange(true);
    this.getRangeAxis().setAutoRange(true);
  }

	public HiResDate getTimeZero()
	{
	  return _myStepper.getTimeZero();
	}
}
