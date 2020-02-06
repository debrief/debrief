/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package Debrief.GUI.Tote.Painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Vector;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.Wrappers.ShapeWrapper;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public class SnailPainter2 extends TotePainter
{

  /**
   * utility class, to help determine a semi-transparent color shade that fades with time
   *
   * @author ian
   *
   */
  public static class ColorFadeCalculator
  {
    /*
     * how long the trail is
     *
     */
    private final long _trail_lenMillis;

    /*
     * the time now
     *
     */
    private final long _datumTime;

    public ColorFadeCalculator(final long trail_lenMillis,
        final HiResDate datumTime)
    {
      _trail_lenMillis = trail_lenMillis;
      _datumTime = datumTime.getDate().getTime();
    }

    public float fadeAt(final HiResDate currentTime)
    {
      // how far back through the time period are we?
      long our_time = _datumTime - currentTime.getDate().getTime();

      // just double check that we have a positive time offset
      our_time = Math.max(0, our_time);

      float proportion = (((float) _trail_lenMillis - our_time)
          / _trail_lenMillis);
      proportion = Math.max(0, proportion);
      return proportion;
    }

    public Color fadeColorAt(final Color trkColor, final HiResDate currentTime)
    {
      final float thisFade = fadeAt(currentTime);
      return new Color(trkColor.getRed(), trkColor.getGreen(), trkColor
          .getBlue(), (int) (thisFade * 255f));
    }
  }

  public interface drawHighLight2
  {
    public boolean canPlot(Watchable wt);

    public java.awt.Rectangle drawMe(MWC.Algorithms.PlainProjection proj,
        Graphics dest, WatchableList list, Watchable watch, TotePainter parent,
        HiResDate dtg, ColorFadeCalculator fader);
  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    public final void testFade()
    {
      final ColorFadeCalculator fader = new ColorFadeCalculator(1000,
          new HiResDate(10000));
      assertEquals(0f, fader.fadeAt(new HiResDate(9000)));
      assertEquals(1f, fader.fadeAt(new HiResDate(10000)));
      assertEquals(0.5f, fader.fadeAt(new HiResDate(9500)));

      final Color base = Color.red;
      final Color faded = fader.fadeColorAt(base, new HiResDate(9500));
      assertEquals("half fade", 127, faded.getAlpha());
      assertEquals("right red", 255, faded.getRed());
      assertEquals("right greem", 0, faded.getGreen());
      assertEquals("right blue", 0, faded.getBlue());
    }

    public final void testMyParams()
    {
      Editable ed = new SnailPainter2(null, null, null);
      editableTesterSupport.testParams(ed, this);
      ed = null;
    }

  }

  public static final String SNAIL_NAME = "Snail";

  /**
   * the highlight plotters we know about
   */
  protected final Vector<drawHighLight2> _myHighlightPlotters;

  /**
   * the size to draw myself
   */
  protected final int _mySize = 5;

  /**
   * the list of painters previously used by the canvas
   */
  protected Vector<PaintListener> _oldPainters;

  /**
   * the snail track plotter to use
   */
  private final SnailDrawFix2 _mySnailPlotter;

  public SnailPainter2(final PlainChart theChart, final Layers theData,
      final AnalysisTote theTote)
  {
    super(theChart, theData, theTote);

    _mySnailPlotter = new SnailDrawFix2("Snail", theTote);

    _myHighlightPlotters = new Vector<drawHighLight2>();
    _myHighlightPlotters.addElement(_mySnailPlotter);
    _myHighlightPlotters.addElement(new SnailDrawBuoyPattern2());
    _myHighlightPlotters.addElement(new SnailDrawAnnotation2());
    _myHighlightPlotters.addElement(new SnailDrawSensorContact2(
        _mySnailPlotter));
    _myHighlightPlotters.addElement(new SnailDrawTMAContact2(_mySnailPlotter));

    _mySnailPlotter.setPointSize(new BoundedInteger(7, 0, 20));
    _mySnailPlotter.setVectorStretch(1);
  }

  /**
   * override the method provided by TotePainter. That method returns null, since it can rely on the
   * other painters to generate the current data area --> there aren't any other painters here
   * though, so we need to calculate it
   */
  @Override
  public final WorldArea getDataArea()
  {
    return this._theChart.getDataArea();
  }

  /**
   * NON-STANDARD implementation, we are returning the editor for our snail plotter object, not
   * ourself
   */
  @Override
  public final Editable.EditorType getInfo()
  {
    return _mySnailPlotter.getInfo();
  }

  @Override
  public final String getName()
  {
    return toString();
  }

  /**
   * whether to plot in the name of the vessel
   */
  public final boolean getPlotTrackName()
  {
    return _mySnailPlotter.getPlotTrackName();
  }

  /**
   * how much to stretch this vector
   *
   * @return the stretch to apply
   */
  public final double getVectorStretch()
  {
    return _mySnailPlotter.getVectorStretch();
  }

  @Override
  public final boolean hasEditor()
  {
    return true;
  }

  /**
   * method to highlight a watchable.
   */
  private void highlightIt(final PlainProjection proj, final Graphics dest,
      final WatchableList list, final Watchable watch, final HiResDate dtg,
      final ColorFadeCalculator fader)
  {
    // check that our graphics context is still valid -
    // we can't, so we will just have to trap any exceptions it raises
    try
    {
      // set the highlight colour
      dest.setColor(Color.white);
      // _theTote.getStepper()

      // see if our plotters can plot this type of watchable
      final Enumeration<drawHighLight2> iter = _myHighlightPlotters.elements();
      while (iter.hasMoreElements())
      {
        final drawHighLight2 plotter = iter.nextElement();

        if (plotter.canPlot(watch))
        {
          // does this list have a width?
          if (list instanceof Layer)
          {
            final Layer ly = (Layer) list;
            if (dest instanceof Graphics2D)
            {
              final Graphics2D g2 = (Graphics2D) dest;
              g2.setStroke(new BasicStroke(ly.getLineThickness()));
            }
          }
          else if (list instanceof ShapeWrapper)
          {
            final ShapeWrapper sw = (ShapeWrapper) list;
            if (dest instanceof Graphics2D)
            {
              final Graphics2D g2 = (Graphics2D) dest;
              g2.setStroke(new BasicStroke(sw.getShape().getLineWidth()));
            }
          }

          final Rectangle rec = plotter.drawMe(proj, dest, list, watch, this,
              dtg, fader);

          // just check if a rectangle got returned at all (there may not
          // have been any valid data
          if (rec != null)
          {
            if (_areaCovered == null)
              _areaCovered = rec;
            else
              _areaCovered.add(rec);
          }

          // and drop out of the loop
          break;
        }
      }

    }
    catch (final IllegalStateException e)
    {
      MWC.Utilities.Errors.Trace.trace(e);
    }
  }

  @Override
  public void newTime(final HiResDate oldDTG, final HiResDate newDTG,
      final MWC.GUI.CanvasType canvas)
  {

    // check if we have any data
    if (_theTote.getPrimary() == null)
      return;

    // check we have a valid new DTG
    if (newDTG == null)
    {
      return;
    }

    // sort out the fade function
    final long trailLength = _mySnailPlotter.getTrailLength().getMillis();
    final SnailPainter2.ColorFadeCalculator fader = new ColorFadeCalculator(
        trailLength, newDTG);

    // initialise the area covered
    _areaCovered = null;

    // prepare the chart
    MWC.GUI.CanvasType theCanvas = canvas;
    if (theCanvas == null)
      theCanvas = _theChart.getCanvas();

    final Graphics2D dest = (Graphics2D) theCanvas.getGraphicsTemp();

    // just drop out if we can't create any graphics though
    if (dest == null)
      return;

    final Vector<Plottable> nonWatches = SnailPainter.getNonWatchables(
        super.getLayers());
    final Enumeration<Plottable> iter = nonWatches.elements();
    while (iter.hasMoreElements())
    {
      final Plottable p = iter.nextElement();
      p.paint(new MWC.GUI.Canvas.CanvasAdaptor(theCanvas.getProjection(),
          dest));
    }

    // get the primary track
    final WatchableList _thePrimary = _theTote.getPrimary();

    // determine the new items
    final Vector<Plottable> theWatchableLists = getWatchables(
        super.getLayers());

    // sort out the line width of the primary
    if (_thePrimary instanceof Layer)
    {
      final Layer ly = (Layer) _thePrimary;
      if (dest instanceof Graphics2D)
      {
        final Graphics2D g2 = dest;
        g2.setStroke(new BasicStroke(ly.getLineThickness()));
      }
    }

    // show the current highlighter
    Watchable[] wList = _theTote.getPrimary().getNearestTo(newDTG);

    Watchable newPrimary = null;
    if (wList.length > 0)
      newPrimary = wList[0];
    if (newPrimary != null)
    {
      final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter thisHighlighter =
          getCurrentPrimaryHighlighter();
      if (thisHighlighter.getName().equals("Range Rings"))
      {
        thisHighlighter.highlightIt(theCanvas.getProjection(), dest, _theTote
            .getPrimary(), newPrimary, true);
      }
    }

    // got through to highlight the data
    final Enumeration<Plottable> watches = theWatchableLists.elements();
    while (watches.hasMoreElements())
    {
      final WatchableList list = (WatchableList) watches.nextElement();
      // is the primary an instance of layer (with it's own line thickness?)
      if (list instanceof Layer)
      {
        final Layer ly = (Layer) list;
        dest.setStroke(new BasicStroke(ly.getLineThickness()));
      }

      // ok, clear the nearest items
      wList = list.getNearestTo(newDTG);
      Watchable watch = null;
      if (wList.length > 0)
        watch = wList[0];

      if (watch != null)
      {
        // plot it
        highlightIt(theCanvas.getProjection(), dest, list, watch, newDTG,
            fader);
      }
    }

    // restore the painting setup
    dest.setPaintMode();
    dest.dispose();

    // we know we're finished with the first step now anyway
    _firstStep = false;
    _lastDTG = newDTG;

    // do a repaint, if we have to
    if (!_inRepaint)
    {

      // are any of the bits which changed visible?
      if (_areaCovered != null)
      {

        // force a repaint of the plot

        // grow the area covered by a shade,
        _areaCovered.grow(2, 2);

        // see if we are trying to plot in relative mode - in which
        // case we need a full repaint
        if (theCanvas.getProjection().getNonStandardPlotting())
        {
          _theChart.update();
        }
        else
        {
          // and ask for an instant update
          // NOTE: changed this to stop JDK1.3
          // plotting in a purple background _theChart.repaintNow(_areaCovered);
          _theChart.repaint();
        }
      }
    }
  }

  /**
   * whether to plot in the name of the vessel
   */
  public final void setPlotTrackName(final boolean val)
  {
    _mySnailPlotter.setPlotTrackName(val);
  }

  /**
   * the stretch to apply to the speed vector (pixels per knot)
   *
   * @param val
   *          the strech to use = 1 is 1 pixel per knot
   */
  public final void setVectorStretch(final double val)
  {
    _mySnailPlotter.setVectorStretch(val);
  }

  //
  @Override
  public void steppingModeChanged(final boolean on)
  {
    if (on)
    {
      // remove the current painters for the canvas
      final Enumeration<CanvasType.PaintListener> iter = _theChart.getCanvas()
          .getPainters();

      _oldPainters = new Vector<PaintListener>(0, 1);

      // take a copy of these painters
      while (iter.hasMoreElements())
      {
        _oldPainters.addElement(iter.nextElement());
      }

      // and remove the painters
      final Enumeration<CanvasType.PaintListener> oldies = _oldPainters
          .elements();
      while (oldies.hasMoreElements())
      {
        _theChart.getCanvas().removePainter(oldies.nextElement());
      }

      // add us as a painter
      _theChart.getCanvas().addPainter(this);

      // and redraw the chart
      _theChart.update();
    }
    else
    {
      // remove us as a painter
      _theChart.getCanvas().removePainter(this);

      // restore the painters
      final Enumeration<CanvasType.PaintListener> oldies = _oldPainters
          .elements();
      while (oldies.hasMoreElements())
      {
        _theChart.getCanvas().addPainter(oldies.nextElement());
      }
    }
  }

  @Override
  public String toString()
  {
    return SNAIL_NAME;
  }

}
