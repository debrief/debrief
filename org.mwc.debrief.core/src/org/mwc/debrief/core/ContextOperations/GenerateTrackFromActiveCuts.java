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
package org.mwc.debrief.core.ContextOperations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 *
 */
public class GenerateTrackFromActiveCuts implements
    RightClickContextItemGenerator
{

  private static class DummyMenu extends MenuManager
  {

    private final Vector<Object> items = new Vector<>();

    @Override
    public void add(final IAction action)
    {
      items.add(action);
    }

    @Override
    public void add(final IContributionItem item)
    {
      items.add(item);
    }
  }

  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public testMe(final String val)
    {
      super(val);
    }

    public final void testIWork()
    {
      final TrackWrapper track = new TrackWrapper();

      // and add the fixes
      final java.util.Calendar cal = new java.util.GregorianCalendar(2001, 10,
          4, 4, 4, 0);

      cal.set(2001, 10, 4, 4, 4, 0);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.0, 2.0, 0.0), 12, 12)));

      cal.set(2001, 10, 4, 4, 4, 01);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.0, 2.25, 0.0), 12,
          12)));

      cal.set(2001, 10, 4, 4, 4, 02);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.0, 2.5, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 05);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.0, 2.75, 0.0), 12,
          12)));
      cal.set(2001, 10, 4, 4, 4, 23);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.25, 2.0, 0.0), 12,
          12)));
      cal.set(2001, 10, 4, 4, 4, 25);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.5, 2.0, 0.0), 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 28);
      final WorldLocation theLoc = new WorldLocation(2.75d, 2.0, 0.0);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), theLoc, 12, 12)));
      cal.set(2001, 10, 4, 4, 4, 55);
      track.addFix(new FixWrapper(new Fix(new HiResDate(cal
          .getTime().getTime(), 0), new WorldLocation(2.25, 2.25, 0.0), 12,
          12)));

      // and some sensor data, which goes past the end of O/S track
      final SensorWrapper sensor = new SensorWrapper("SensorName");

      cal.set(2001, 10, 4, 4, 4, 25);
      sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime()
          .getTime()), new WorldDistance(1000, WorldDistance.YARDS), 23.3d,
          null, Color.RED, "Some lable", 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 27);
      sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime()
          .getTime()), new WorldDistance(1000, WorldDistance.YARDS), 23.3d,
          null, Color.RED, "Some lable", 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 4, 55);
      sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime()
          .getTime()), new WorldDistance(1000, WorldDistance.YARDS), 23.3d,
          null, Color.RED, "Some lable", 1, sensor.getName()));

      cal.set(2001, 10, 4, 4, 15, 05);
      sensor.add(new SensorContactWrapper("track", new HiResDate(cal.getTime()
          .getTime()), new WorldDistance(1000, WorldDistance.YARDS), 23.3d,
          null, Color.RED, "Some lable", 1, sensor.getName()));

      track.add(sensor);
      final Layers layers = new Layers();
      layers.addThisLayer(track);

      final AtomicInteger ctr = new AtomicInteger(0);

      final ErrorLogger logger = new ErrorLogger()
      {

        @Override
        public void logError(final int status, final String text,
            final Exception e)
        {
          ctr.getAndIncrement();
        }

        @Override
        public void logError(final int status, final String text,
            final Exception e, final boolean revealLog)
        {
          ctr.getAndIncrement();
        }

        @Override
        public void logStack(final int status, final String text)
        {
          ctr.getAndIncrement();
        }
      };
      // now try to offer cuts
      final GenerateTrackFromActiveCuts genny = new GenerateTrackFromActiveCuts(
          logger);
      final Layer[] parentLayers = new Layer[]
      {track};
      final Editable[] subjects = new Editable[]
      {sensor};
      final DummyMenu parent = new DummyMenu();
      genny.generate(parent, layers, parentLayers, subjects);

      assertEquals("items added", 1, parent.items.size());

      final Action item = (Action) parent.items.firstElement();
      item.run();

      assertEquals("Errors reported", 1, ctr.get());
      assertEquals("now has new track", 2, layers.size());

    }
  }

  private static class TrackfromSensorCuts extends TrackfromSensorData
  {
    private final SensorContactWrapper[] _items;

    public TrackfromSensorCuts(final SensorContactWrapper[] items,
        final Layers theLayers, ErrorLogger logger)
    {
      super("Create Track from Active cuts", theLayers, logger);
      _items = items;
    }

    @Override
    protected SensorContactWrapper[] getCuts()
    {
      return _items;
    }
  }

  private static abstract class TrackfromSensorData extends CMAPOperation
  {

    private final Layers _layers;
    private TrackWrapper _newTrack;
    private final ErrorLogger logger;

    public TrackfromSensorData(final String title, final Layers theLayers, final ErrorLogger logger)
    {
      super(title);
      _layers = theLayers;
      this.logger = logger;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {

      final SensorContactWrapper[] cuts = getCuts();

      _newTrack = new TrackWrapper();
      _newTrack.setName("Track_from_" + cuts[0].getSensorName());
      _newTrack.setColor(cuts[0].getColor());

      boolean fixesAdded = false;

      // ok, now loop through and assign the cuts
      for (int cnt = 0; cnt < cuts.length; cnt++)
      {
        final SensorContactWrapper cut = cuts[cnt];

        // double-check this is suitable
        if (cut.getHasBearing() && cut.getRange() != null && !cut
            .getHasAmbiguousBearing())
        {
          // represent rng/brg as a vector
          final WorldVector vec = new WorldVector(Math.toRadians(cut
              .getBearing()), cut.getRange().getValueIn(WorldDistance.DEGS), 0);

          // check we have a location
          final WorldLocation origin = cut.getLocation();
          if (origin != null)
          {
            // also do the far end
            final WorldLocation loc = origin.add(vec);

            final Fix fix = new Fix(cut.getDTG(), loc, 0d, 0d);
            final FixWrapper fw = new FixWrapper(fix);

            // give it a sensible label
            fw.resetName();

            _newTrack.add(fw);

            fixesAdded = true;
          }
          else
          {

            logger.logError(IStatus.WARNING,
                "Ownship location not found for sensor at:" + cut.getDTG()
                    .getDate(), null);
          }
        }
      }

      if (fixesAdded)
      {
        // and auto-generate courses and speeds
        _newTrack.calcCourseSpeed();

        // done, store it!
        _layers.addThisLayerAllowDuplication(_newTrack);

        // sorted, do the update
        _layers.fireExtended();
      }
      else
      {
        CorePlugin.logError(IStatus.WARNING,
            "Failed to find fixes with range and bearing - "
                + "track not generated", null);
      }

      return Status.OK_STATUS;
    }

    abstract protected SensorContactWrapper[] getCuts();

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // forget about the new tracks
      _layers.removeThisLayer(_newTrack);
      _layers.fireExtended();

      return Status.OK_STATUS;
    }

  }

  private static class TrackfromSensorWrappers extends TrackfromSensorData
  {
    private final SensorWrapper _wrapper;

    public TrackfromSensorWrappers(final SensorWrapper wrapper,
        final Layers theLayers, ErrorLogger logger)
    {
      super("Create Track from Active cuts", theLayers, logger);
      _wrapper = wrapper;
    }

    @Override
    protected SensorContactWrapper[] getCuts()
    {

      final Vector<SensorContactWrapper> wraps =
          new Vector<SensorContactWrapper>();

      if (_wrapper.size() > 0)
      {
        // ok, now check for range
        final Editable first = _wrapper.elements().nextElement();
        final SensorContactWrapper scw = (SensorContactWrapper) first;
        if (scw.getHasBearing() && scw.getRange() != null)
        {
          // ok, it's a goer.
          final Enumeration<Editable> numer = _wrapper.elements();
          while (numer.hasMoreElements())
          {
            wraps.add((SensorContactWrapper) numer.nextElement());
          }
        }
      }

      final SensorContactWrapper[] _items;
      if (wraps.size() > 0)
      {
        final SensorContactWrapper[] sample = new SensorContactWrapper[wraps
            .size()];
        _items = wraps.toArray(sample);
      }
      else
      {
        _items = null;
      }

      return _items;
    }
  }

  private final ErrorLogger _logger;

  public GenerateTrackFromActiveCuts()
  {
    this(new ErrorLogger()
    {

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {
        CorePlugin.logError(status, text, e);
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        throw new IllegalArgumentException(
            "Not implemented in GenerateTrackFromActiveCuts");
      }

      @Override
      public void logStack(final int status, final String text)
      {
        throw new IllegalArgumentException(
            "Not implemented in GenerateTrackFromActiveCuts");
      }
    });
  }

  public GenerateTrackFromActiveCuts(final ErrorLogger logger)
  {
    _logger = logger;
  }

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    Action _myAction = null;

    // so, see if it's something we can do business with
    if (subjects.length == 1)
    {
      // ok, do I know how to create a TMA segment from this?
      final Editable onlyOne = subjects[0];
      if (onlyOne instanceof SensorWrapper)
      {
        final SensorWrapper sw = (SensorWrapper) onlyOne;

        if (sw.size() > 0)
        {
          // ok, now check for range
          final Editable first = sw.elements().nextElement();
          final SensorContactWrapper scw = (SensorContactWrapper) first;

          if (scw.getHasBearing() && scw.getRange() != null && !scw
              .getHasAmbiguousBearing())
          {

            // cool wrap it in an action.
            _myAction = new Action("Generate Track from Active Sensor Data")
            {
              @Override
              public void run()
              {
                // ok, go for it.
                // sort it out as an operation
                final IUndoableOperation convertToTrack1 =
                    new TrackfromSensorWrappers(sw, theLayers, _logger);

                // ok, stick it on the buffer
                runIt(convertToTrack1);
              }
            };
          }
          else
          {
            _logger.logError(Status.WARNING,
                "Missing range or bearing data (or is ambiguous) for sensor:"
                    + sw.getName(), null);
          }
        }
      }
    }
    else if (subjects.length > 1)
    {
      // more than one item = maybe it's a series of sensor cuts

      //
      SensorContactWrapper[] sonarCuts = null;

      // see if it's a collection of cuts
      // so, it's a number of items, Are they all sensor contact wrappers
      boolean allGood = true;
      sonarCuts = new SensorContactWrapper[subjects.length];
      for (int i = 0; i < subjects.length; i++)
      {
        final Editable editable = subjects[i];
        if (editable instanceof SensorContactWrapper)
        {
          final SensorContactWrapper scw = (SensorContactWrapper) editable;
          if (scw.getHasBearing() && scw.getRange() != null && !scw
              .getHasAmbiguousBearing())
          {
            // cool, stick with it
            sonarCuts[i] = (SensorContactWrapper) editable;
          }
          else
          {
            allGood = false;
          }
        }
        else
        {
          allGood = false;
          break;
        }

      }
      // are we good to go?
      if (!allGood)
      {
        // nope, clear the items list
        sonarCuts = null;
        

        // ONE LAST CHANCE.  What if it's a series of sensors?
        final List<SensorWrapper> sensors = new ArrayList<SensorWrapper>();
        
        for (int i = 0; i < subjects.length; i++)
        {
          final Editable editable = subjects[i];
          if (editable instanceof SensorWrapper)
          {
            SensorWrapper sensor = (SensorWrapper) editable;
            // have a look at the first cut
            if (sensor.size() > 0)
            {
              SensorContactWrapper scw = (SensorContactWrapper) sensor
                  .elements().nextElement();
              if (!scw.getHasBearing())
              {
                _logger.logError(Status.WARNING,
                    "Missing bearing data from sensor:" + sensor.getName(),
                    null);
                return;
              }
              else if (scw.getRange() == null)
              {
                _logger.logError(Status.WARNING,
                    "Missing range data from sensor:" + sensor.getName(),
                    null);
                return;
              }
              else if (scw.getHasAmbiguousBearing())
              {
                _logger.logError(Status.WARNING,
                    "Cannot produce track for ambiguous data:" + sensor
                        .getName(), null);
                return;
              }
              else
              {
                // must be ok, go for it.
                sensors.add(sensor);
              }
            }
          }
        }
        if(!sensors.isEmpty())
        {
          // ok, create composite operation.
          _myAction = new Action("Generate Tracks from multiple Active Sensors")
          {
            @Override
            public void run()
            {
              // ok, go for it.
              // sort it out as an operation
              final IUndoableOperation convertToTrack1 = new BulkTrackGenerate(
                  sensors, theLayers, _logger);

              // ok, stick it on the buffer
              runIt(convertToTrack1);
            }
          };
        }
      }
      else
      {
        // cool, go for it
        final SensorContactWrapper[] finalItems = sonarCuts;

        // cool wrap it in an action.
        _myAction = new Action("Generate Track from Active Sensor Data")
        {
          @Override
          public void run()
          {
            // ok, go for it.
            // sort it out as an operation
            final IUndoableOperation convertToTrack1 = new TrackfromSensorCuts(
                finalItems, theLayers, _logger);

            // ok, stick it on the buffer
            runIt(convertToTrack1);
          }
        };
      }
    }

    if (_myAction != null)
    {
      parent.add(_myAction);
    }
  }
  

  private class BulkTrackGenerate extends CMAPOperation
  {
    private List<TrackfromSensorWrappers> list = new ArrayList<TrackfromSensorWrappers>();

    BulkTrackGenerate(final List<SensorWrapper> sensors, final Layers layers, ErrorLogger logger)
    {
      super("Generate multiple tracks from active sensors");
      
      for(SensorWrapper sensor: sensors)
      {
        TrackfromSensorWrappers genny = new TrackfromSensorWrappers(sensor, layers, logger);
        list.add(genny);
      }
    }

    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      for(final TrackfromSensorWrappers t: list)
      {
        t.execute(monitor, info);
      }
      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      for(final TrackfromSensorWrappers t: list)
      {
        t.undo(monitor, info);
      }
      return Status.OK_STATUS;
    }
  }
  
  /**
   * put the operation firer onto the undo history. We've refactored this into a separate method so
   * testing classes don't have to simulate the CorePlugin
   * 
   * @param operation
   */
  protected void runIt(final IUndoableOperation operation)
  {
    CorePlugin.run(operation);
  }
}
