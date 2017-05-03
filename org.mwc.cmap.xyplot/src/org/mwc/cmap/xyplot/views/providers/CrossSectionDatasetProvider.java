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
package org.mwc.cmap.xyplot.views.providers;

import java.awt.Color;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.mwc.cmap.xyplot.views.CrossSectionViewer.SnailRenderer;
import org.mwc.cmap.xyplot.views.ILocationCalculator;
import org.mwc.cmap.xyplot.views.LocationCalculator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Shapes.LineShape;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

public class CrossSectionDatasetProvider implements
    ICrossSectionDatasetProvider
{
  private ILocationCalculator _calc;
  private Map<Integer, Color> _seriesColors = new HashMap<Integer, Color>();

  public CrossSectionDatasetProvider()
  {
    this(WorldDistance.KM);
  }

  public CrossSectionDatasetProvider(final int units)
  {
    _calc = new LocationCalculator(units);
  }

  private XYSeries getSeries(final LineShape line, String seriesName,
      Watchable[] wbs, SnailRenderer renderer,
      final HiResDate startT, final HiResDate endT, int colCounter)
  {
    final XYSeries series = new XYSeries(seriesName, false, true);
    int rowCounter = 0;
    for (final Watchable wb : wbs)
    {
      final Double x_coord = new Double(_calc.getDistance(line, wb));
      final Double y_coord = new Double(-1.0 * wb.getDepth());
      series.add(x_coord, y_coord);
      
      // do we have a renderer?
      if(renderer != null)
      {
        // ok, work out how far back we are
        long elapsed = wb.getTime().getMicros() - startT.getMicros();
        double proportion = (double)elapsed / (endT.getMicros() - startT.getMicros());
        renderer.setRowColProportion(colCounter, rowCounter, 1d - proportion);
      }
    }
    return series;
  }

  XYSeries getSeries(final LineShape line, final TrackWrapper wlist,
      final HiResDate startT, final HiResDate endT, final SnailRenderer renderer, final int colCounter)
  {
    final Collection<Editable> editables = wlist.getItemsBetween(startT, endT);
    String theName = wlist.getName();
    if (editables == null)
      return new XYSeries(theName, false, true);
    Watchable[] wbs =
        (Watchable[]) editables.toArray(new Watchable[editables.size()]);
    return getSeries(line, theName, wbs, renderer, startT, endT, colCounter);
  }

  XYSeries getSeries(final LineShape line, final TrackWrapper wlist,
      final HiResDate timeT)
  {
    final Watchable[] wbs = wlist.getNearestTo(timeT);
    return getSeries(line, wlist.getName(), wbs, null, null, null, 0);
  }

  @Override
  public XYSeriesCollection getDataset(LineShape line, Layers layers,
      HiResDate startT, HiResDate endT, SnailRenderer renderer)
  {
    return walk(layers, line, startT, endT, renderer);
  }

  @Override
  public XYSeriesCollection getDataset(LineShape line, Layers layers,
      HiResDate timeT)
  {
    return walk(layers, line, timeT, null, null);
  }

  private XYSeriesCollection
      walk(final Layers layers, final LineShape line, final HiResDate startT,
          final HiResDate endT, final SnailRenderer renderer)
  {
    final XYSeriesCollection dataset = new XYSeriesCollection();
    final Enumeration<Editable> numer = layers.elements();

    // ok, reset the renderer
    if(renderer != null)
    {
      renderer.reset();
    }
    int colCounter = 0;

    while (numer.hasMoreElements())
    {
      final Editable next = numer.nextElement();
      if (next instanceof WatchableList)
      {
        final WatchableList wlist = (WatchableList) next;
        if (wlist.getVisible())
        {
          if (wlist instanceof TrackWrapper)
          {
            TrackWrapper track = (TrackWrapper) wlist;

            if (endT != null)
            {
              final XYSeries series =
                  getSeries(line, track, startT, endT, renderer, colCounter++);
              dataset.addSeries(series);
            }
            else
            {
              final XYSeries series = getSeries(line, track, startT);
              dataset.addSeries(series);
            }
            _seriesColors.put(new Integer(dataset.getSeriesCount() - 1), wlist
                .getColor());
          }
        }
      }
    }
    return dataset;
  }

  @Override
  public Map<Integer, Color> getSeriesColors()
  {
    return _seriesColors;
  }

  static public final class CrossSectionDatasetProviderTest extends
      junit.framework.TestCase
  {
    DateFormat _dateFormat = new SimpleDateFormat("dd-mm-yyyy HH:mm");
    HiResDate[] _times;
    int TIME_ARRAY_SIZE = 7;

    TrackWrapper _track;
    LineShape _line;

    CrossSectionDatasetProvider _testable = new CrossSectionDatasetProvider();

    public void setUp() throws ParseException
    {
      WorldLocation start = new WorldLocation(0, 0, 0);
      WorldLocation end = new WorldLocation(0, 1, 0);
      _line = new LineShape(start, end);

      _times = new HiResDate[TIME_ARRAY_SIZE];
      for (int i = 0; i < TIME_ARRAY_SIZE; i++)
      {
        final Date dateVal = _dateFormat.parse("09-09-2013 10:" + i * 10);
        _times[i] = new HiResDate(dateVal);
      }

      _track = new TrackWrapper();
      int j = 0;
      for (double i = 0.0; i <= 1.2; i += 0.2)
      {
        final WorldLocation loc = new WorldLocation(0, i, 0);
        final Fix fix = new Fix(_times[j], loc, 2, 2);
        _track.addFix(new FixWrapper(fix));
        j++;
      }
    }

    public void testDiscreteSeries() throws ParseException
    {
      for (int i = 0; i < TIME_ARRAY_SIZE; i++)
      {
        final XYSeries series = _testable.getSeries(_line, _track, _times[i]);
        assertEquals(1, series.getItemCount());
        final XYDataItem item = series.getDataItem(0);
        assertNotNull(item.getXValue());
        assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
      }
    }

    public void testSnailSeries2() throws ParseException
    {
      for (int i = 0; i < TIME_ARRAY_SIZE - 1; i++)
      {
        final XYSeries series =
            _testable.getSeries(_line, _track, _times[i], _times[i + 1], null, 0);
        assertEquals(2, series.getItemCount());
        for (int j = 0; j < 2; j++)
        {
          final XYDataItem item = series.getDataItem(j);
          assertNotNull(item.getXValue());
          assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
        }
      }
    }

    public void testSnailSeries() throws ParseException
    {
      for (int i = 0; i < TIME_ARRAY_SIZE - 2; i++)
      {
        final XYSeries series =
            _testable.getSeries(_line, _track, _times[i], _times[i + 1], null, 0);
        assertEquals(2, series.getItemCount());
        for (int j = 0; j < 2; j++)
        {
          final XYDataItem item = series.getDataItem(j);
          assertNotNull(item.getXValue());
          assertEquals(0.0, item.getYValue(), 0.001); // depth is 0
        }
      }
    }

    public void testSnailShading() throws ParseException
    {
      SnailRenderer renderer = new SnailRenderer();
      final XYSeries series =
          _testable.getSeries(_line, _track, _times[0], _times[6], renderer, 0);
      assertEquals(7, series.getItemCount());
      for (int j = 0; j < 7; j++)
      {
        final XYDataItem item = series.getDataItem(j);
        assertNotNull(item.getXValue());
        assertEquals(0.0, item.getYValue(), 0.001); // depth is 0

        assertEquals("has proportion",(6d-j)/6d, renderer.getProportionFor(0, j),0.1);
       
        System.out.println("prop:" + renderer.getProportionFor(0, j));
        
      }
      
      // and the renderer
      assertNotNull("has a color", renderer.getProportionFor(0, 2));
      
    }

  }

}
