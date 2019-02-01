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
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IContributionManagerOverrides;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorPart;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.DataTypes.TrackData.TrackManager;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.cmap.core.wizards.RangeBearingPage;
import org.mwc.cmap.core.wizards.SelectColorPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage;
import org.mwc.debrief.core.wizards.EnterSolutionPage.SolutionDataItem;
import org.mwc.debrief.core.wizards.s2r.TMAFromSensorWizard;

import Debrief.GUI.Frames.Application;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

/**
 * @author ian.mayo
 *
 */
public class GenerateTMASegmentFromCuts implements
    RightClickContextItemGenerator
{

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

    public void testGenerateMultiSensor()
    {
      TrackWrapper host = new TrackWrapper();
      host.setName("host");

      SensorWrapper sensor1 = new SensorWrapper("sensor1");
      host.add(sensor1);
      SensorWrapper sensor2 = new SensorWrapper("sensor2");
      host.add(sensor2);

      host.addFix(new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(2000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(3000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(4000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(5000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(6000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(7000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(8000), new WorldLocation(
          0, 0, 0), 10, 20)));

      sensor1.add(new SensorContactWrapper("host", new HiResDate(800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(1800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(2800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(5800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(7800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(8800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor1.add(new SensorContactWrapper("host", new HiResDate(9800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor2.add(new SensorContactWrapper("host", new HiResDate(800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor2.add(new SensorContactWrapper("host", new HiResDate(1800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor2.add(new SensorContactWrapper("host", new HiResDate(2800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor2.add(new SensorContactWrapper("host", new HiResDate(5800), null,
          100d, null, Color.RED, "label", 12, "sensor"));

      GenerateTMASegmentFromCuts genny = new GenerateTMASegmentFromCuts();
      Editable[] sensors = new SensorWrapper[]
      {sensor1, sensor2};
      Layers theLayers = new Layers();

      DummyMenuManager parent = new DummyMenuManager();
      genny.generate(parent, theLayers, null, sensors);
      assertEquals("has action", parent.actions.size(), 1);
      TMAAction theAction = (TMAAction) parent.actions.get(0);
      assertEquals("correct cuts", 11, theAction._items.length);

    }

    private static class DummyMenuManager implements IMenuManager
    {
      final private List<IAction> actions = new ArrayList<IAction>();

      @Override
      public void add(IAction action)
      {
        actions.add(action);
      }

      @Override
      public void add(IContributionItem item)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void appendToGroup(String groupName, IAction action)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void appendToGroup(String groupName, IContributionItem item)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public IContributionItem find(String id)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IContributionItem[] getItems()
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IContributionManagerOverrides getOverrides()
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void insertAfter(String id, IAction action)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void insertAfter(String id, IContributionItem item)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void insertBefore(String id, IAction action)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void insertBefore(String id, IContributionItem item)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public boolean isDirty()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isEmpty()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public void markDirty()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void prependToGroup(String groupName, IAction action)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void prependToGroup(String groupName, IContributionItem item)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public IContributionItem remove(String id)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IContributionItem remove(IContributionItem item)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public void removeAll()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void update(boolean force)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void dispose()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void fill(Composite parent)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void fill(Menu parent, int index)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void fill(ToolBar parent, int index)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void fill(CoolBar parent, int index)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public String getId()
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean isDynamic()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isGroupMarker()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isSeparator()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isVisible()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public void saveWidgetState()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void setParent(IContributionManager parent)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void setVisible(boolean visible)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void update()
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void update(String id)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void addMenuListener(IMenuListener listener)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public IMenuManager findMenuUsingPath(String path)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IContributionItem findUsingPath(String path)
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public boolean getRemoveAllWhenShown()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public boolean isEnabled()
      {
        // TODO Auto-generated method stub
        return false;
      }

      @Override
      public void removeMenuListener(IMenuListener listener)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void setRemoveAllWhenShown(boolean removeAll)
      {
        // TODO Auto-generated method stub

      }

      @Override
      public void updateAll(boolean force)
      {
        // TODO Auto-generated method stub

      }

    }

    public void testTrimmingTrack()
    {
      TrackWrapper host = new TrackWrapper();
      host.setName("host");

      SensorWrapper sensor = new SensorWrapper("sensor");
      host.add(sensor);

      host.addFix(new FixWrapper(new Fix(new HiResDate(1000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(2000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(3000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(4000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(5000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(6000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(7000), new WorldLocation(
          0, 0, 0), 10, 20)));
      host.addFix(new FixWrapper(new Fix(new HiResDate(8000), new WorldLocation(
          0, 0, 0), 10, 20)));

      sensor.add(new SensorContactWrapper("host", new HiResDate(800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(1800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(2800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(5800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(7800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(8800), null,
          100d, null, Color.RED, "label", 12, "sensor"));
      sensor.add(new SensorContactWrapper("host", new HiResDate(9800), null,
          100d, null, Color.RED, "label", 12, "sensor"));

      Enumeration<Editable> cuts = sensor.elements();
      SensorContactWrapper[] cutArr = new SensorContactWrapper[sensor.size()];
      int ctr = 0;
      while (cuts.hasMoreElements())
      {
        SensorContactWrapper cut = (SensorContactWrapper) cuts.nextElement();
        cutArr[ctr++] = cut;
      }

      assertEquals("expected number of cuts", 7, cutArr.length);

      SensorContactWrapper[] trimmed = TMAfromCuts.trimToHost(cutArr);
      assertEquals("expected number of cuts", 4, trimmed.length);
    }

    @SuppressWarnings("deprecation")
    private TrackWrapper getLongerTrack()
    {
      final TrackWrapper tw = new TrackWrapper();

      final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
      WorldLocation lastLoc = loc_1;

      for (int i = 0; i < 50; i++)
      {
        final long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
        final FixWrapper fw = new FixWrapper(new Fix(new HiResDate(thisTime),
            lastLoc.add(getVector(25, 0)), MWC.Algorithms.Conversions.Degs2Rads(
                0), 110));
        fw.setLabel("fw1");
        tw.addFix(fw);

        lastLoc = new WorldLocation(fw.getLocation());
      }

      final SensorWrapper swa = new SensorWrapper("title one");
      tw.add(swa);
      swa.setSensorOffset(new ArrayLength(-400));

      for (int i = 0; i < 50; i += 3)
      {
        final long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
        final SensorContactWrapper scwa1 = new SensorContactWrapper("aaa",
            new HiResDate(thisTime), null, null, null, null, null, 0, null);
        swa.add(scwa1);
      }

      return tw;
    }

    /**
     * @return
     */
    private WorldVector getVector(final double courseDegs, final double distM)
    {
      return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
          new WorldDistance(distM, WorldDistance.METRES), null);
    }

    public void testSplitWithOffset() throws ExecutionException
    {
      final TrackWrapper tw = getLongerTrack();

      assertNotNull(tw);

      // get the sensor data
      final SensorWrapper sw = (SensorWrapper) tw.getSensors().elements()
          .nextElement();

      assertNotNull(sw);

      // create a list of cuts (to simulate the selection)
      final SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
      final Enumeration<Editable> numer = sw.elements();
      int ctr = 0;
      while (numer.hasMoreElements())
      {
        final SensorContactWrapper cut = (SensorContactWrapper) numer
            .nextElement();
        items[ctr++] = cut;
      }

      final Layers theLayers = new Layers();
      final WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
      final double tgtCourse = 0;
      final WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);

      final Color newColor = Color.GREEN;

      // check we haven't got the new color
      final Color oldColor = items[0].getColor();
      assertEquals("correct original color", Color.YELLOW, oldColor);

      // ok, generate the target track
      final CMAPOperation op = new TMAfromCuts(items, theLayers, worldOffset,
          tgtCourse, tgtSpeed, newColor);

      // and run it
      op.execute(null, null);

      assertEquals("has new data", 1, theLayers.size());

      final TrackWrapper sol = (TrackWrapper) theLayers.elementAt(0);
      assertNotNull("new layer not found", sol);

      // ok, now try to split it
      assertEquals("only has one segment", 1, sol.getSegments().size());

      final RelativeTMASegment seg = (RelativeTMASegment) sol.getSegments()
          .elements().nextElement();

      assertNotNull("new seg not found", seg);

      // check the color
      final SensorContactWrapper first = items[0];
      assertEquals("new color:", newColor, first.getColor());

      // ok, and we split it.
      int ctr2 = 0;
      FixWrapper beforeF = null;
      FixWrapper afterF = null;
      final Enumeration<Editable> eF = seg.elements();
      while (eF.hasMoreElements())
      {
        final FixWrapper fix = (FixWrapper) eF.nextElement();
        ctr2++;
        if (ctr2 > seg.size() / 2)
        {
          if (beforeF == null)
          {
            beforeF = fix;
          }
          else
          {
            afterF = fix;
            break;
          }
        }
      }

      assertNotNull("fix not found", beforeF);

      // ok, what's the time offset
      final WorldLocation afterBeforeSplit = afterF.getLocation();

      // ok, time to split
      final SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
      final SubjectAction doSplit = actions[1];
      doSplit.execute(beforeF);

      // ok, have another look
      assertEquals("now has two segments", 2, sol.getSegments().size());
      Enumeration<Editable> aNum = sol.getSegments().elements();
      aNum.nextElement();
      TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
      WorldLocation locAfterSplit = afterSeg.getTrackStart();

      assertEquals("origin remains valid", afterBeforeSplit, locAfterSplit);

      // hey, try the undo
      doSplit.undo(beforeF);

      assertEquals("now has one segment again", 1, sol.getSegments().size());

      // hey, try the undo
      doSplit.execute(beforeF);
      assertEquals("now has two segments", 2, sol.getSegments().size());

      aNum = sol.getSegments().elements();
      aNum.nextElement();
      afterSeg = (TrackSegment) aNum.nextElement();
      locAfterSplit = afterSeg.getTrackStart();
      assertEquals("origin remains valid, after undo/redo", afterBeforeSplit,
          locAfterSplit);

    }

    public void testUndo() throws ExecutionException
    {
      final TrackWrapper tw = getLongerTrack();

      assertNotNull(tw);

      // get the sensor data
      final SensorWrapper sw = (SensorWrapper) tw.getSensors().elements()
          .nextElement();

      assertNotNull(sw);

      // create a list of cuts (to simulate the selection)
      final SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
      final Enumeration<Editable> numer = sw.elements();
      int ctr = 0;
      while (numer.hasMoreElements())
      {
        final SensorContactWrapper cut = (SensorContactWrapper) numer
            .nextElement();
        items[ctr++] = cut;
      }

      final Layers theLayers = new Layers();
      final WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
      final double tgtCourse = 0;
      final WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);

      final Color newColor = Color.GREEN;

      theLayers.addThisLayer(tw);

      // check we haven't got the new color
      final Color oldColor = items[0].getColor();
      assertEquals("correct original color", Color.YELLOW, oldColor);

      // ok, generate the target track
      final CMAPOperation op = new TMAfromCuts(items, theLayers, worldOffset,
          tgtCourse, tgtSpeed, newColor);

      // and run it
      op.execute(null, null);

      assertEquals("has new data", 2, theLayers.size());

      final TrackWrapper sol = (TrackWrapper) theLayers.elementAt(1);
      assertNotNull("new layer not found", sol);

      // ok, now undo the operation
      op.undo(null, null);

      assertEquals("new track didn't get deleted", 1, theLayers.size());
    }

  }

  public static class TMAfromCuts extends CMAPOperation
  {

    protected static SensorContactWrapper[] trimToHost(
        final SensorContactWrapper[] cuts)
    {
      final SensorContactWrapper[] res;
      if (cuts.length > 0)
      {
        // get the host
        final TrackWrapper host = cuts[0].getSensor().getHost();
        final TimePeriod hostPeriod = new TimePeriod.BaseTimePeriod(host
            .getStartDTG(), host.getEndDTG());

        final List<SensorContactWrapper> matches =
            new ArrayList<SensorContactWrapper>();
        for (final SensorContactWrapper cut : cuts)
        {
          if (hostPeriod.contains(cut.getDTG()))
          {
            matches.add(cut);
          }
        }
        res = matches.toArray(new SensorContactWrapper[]
        {});
      }
      else
      {
        res = null;
      }

      return res;
    }

    private final Layers _layers;
    private final SensorContactWrapper[] _items;
    private TrackWrapper _newTrack;
    private final double _courseDegs;
    private final WorldSpeed _speed;
    private final WorldVector _offset;

    private final Color _newColor;

    public TMAfromCuts(final SensorContactWrapper[] items,
        final Layers theLayers, final WorldVector offset,
        final double courseDegs, final WorldSpeed speed, final Color newColor)
    {
      super("Create TMA solution from sensor cuts");
      _items = trimToHost(items);
      _layers = theLayers;
      _courseDegs = courseDegs;
      _speed = speed;
      _offset = offset;
      _newColor = newColor;
    }

    @Override
    public boolean canExecute()
    {
      return true;
    }

    @Override
    public boolean canRedo()
    {
      return true;
    }

    @Override
    public boolean canUndo()
    {
      return true;
    }

    public String getTrackNameFor(TrackWrapper newTrack)
    {
      return TrackSegment.TMA_LEADER + FormatRNDateTime.toString(newTrack
          .getStartDTG().getDate().getTime());
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {

      // sort out the color of the segment
      final Color colorOverride;

      // have a looka the property
      String useCutColorStr = Application.getThisProperty(USE_CUT_COLOR);
      if (useCutColorStr == null)
      {
        useCutColorStr = "TRUE";
      }

      final boolean useCutColor = Boolean.valueOf(useCutColorStr);
      if (useCutColor)
      {
        colorOverride = null;
      }
      else
      {
        colorOverride = Color.red;
      }

      // create it, then
      final TrackSegment seg = new RelativeTMASegment(_items, _offset, _speed,
          _courseDegs, _layers, colorOverride);

      // now wrap it
      _newTrack = new TrackWrapper();
      _newTrack.setColor(Color.red);
      _newTrack.add(seg);
      final String tNow = getTrackNameFor(_newTrack);
      _newTrack.setName(tNow);

      _layers.addThisLayerAllowDuplication(_newTrack);

      // shade the sensor cuts
      shadeCuts();

      // also set it as a secondary track
      if (isRunning())
      {
        final IEditorPart editor = CorePlugin.getActivePage().getActiveEditor();
        if (editor != null)
        {
          // get the track manager
          final TrackManager mgr = (TrackManager) editor.getAdapter(
              TrackManager.class);
          if (mgr != null)
          {
            // and assign the new secondary
            mgr.setSecondary(_newTrack);
          }
        }
      }

      // sorted, do the update
      _layers.fireExtended();

      return Status.OK_STATUS;

    }

    public boolean isRunning()
    {
      return Platform.isRunning();
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      _layers.addThisLayerAllowDuplication(_newTrack);

      // sorted, do the update
      _layers.fireExtended();

      // re-shade the cuts
      shadeCuts();

      return Status.OK_STATUS;
    }

    private void shadeCuts()
    {
      // and re-shade the cuts
      if (_newColor != null)
      {
        for (final SensorContactWrapper cut : _items)
        {
          cut.setColor(_newColor);
        }
      }
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // forget about the new tracks
      _layers.removeThisLayer(_newTrack);
      _layers.fireExtended();

      // did we use a color?
      if (_newColor != null)
      {
        for (final SensorContactWrapper cut : _items)
        {
          cut.resetColor();
        }
      }

      return Status.OK_STATUS;
    }

  }

  private static final WorldSpeed DEFAULT_TARGET_SPEED = new WorldSpeed(12,
      WorldSpeed.Kts);

  private static final double DEFAULT_TARGET_COURSE = 120d;

  public static final String USE_CUT_COLOR = "USE_CUT_COLOR";

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
    //
    Action _myAction = null;

    if (allSensors(subjects))
    {
      final Vector<SensorContactWrapper> wraps =
          new Vector<SensorContactWrapper>();
      for (Editable thisE : subjects)
      {
        SensorWrapper sw = (SensorWrapper) thisE;
        // cool, go for it
        final Enumeration<Editable> numer = sw.elements();
        while (numer.hasMoreElements())
        {
          wraps.add((SensorContactWrapper) numer.nextElement());
        }
      }
      if (!wraps.isEmpty())
      {
        final SensorContactWrapper[] items = new SensorContactWrapper[wraps
            .size()];
        final SensorContactWrapper[] finalItems = wraps.toArray(items);
        final SensorContactWrapper firstContact = finalItems[0];
        
        final Color firstColor = firstContact.getColor() != null ? firstContact
            .getColor() : firstContact.getSensor().getColor();

        // cool wrap it in an action.
        _myAction = new TMAAction("Generate TMA solution from all cuts",
            finalItems)
        {
          @Override
          public void run()
          {
            // get ready for the supporting data (using selected sensor data,
            // if
            // we can)

            // just check we have some kind of range
            WorldDistance theDist = firstContact.getRange();
            if (theDist == null)
              theDist = new WorldDistance(6, WorldDistance.NM);

            // get the supporting data
            final TMAFromSensorWizard wizard = new TMAFromSensorWizard(
                firstContact.getBearing(), theDist, DEFAULT_TARGET_COURSE,
                DEFAULT_TARGET_SPEED, firstColor);
            final WizardDialog dialog = new WizardDialog(Display.getCurrent()
                .getActiveShell(), wizard);
            dialog.create();
            dialog.open();

            // did it work?
            if (dialog.getReturnCode() == Window.OK)
            {
              final UserChoice answers = getUserChoice(wizard, firstColor);

              // ok, go for it.
              // sort it out as an operation
              final IUndoableOperation convertToTrack1 = new TMAfromCuts(
                  getItems(), theLayers, answers.res, answers.courseDegs,
                  answers.speed, answers.newColor);

              // ok, stick it on the buffer
              runIt(convertToTrack1);

            }
            else
              System.err.println("user cancelled");
          }
        };
      } // whether there are any cuts for this sensor

    }
    else if (allSensorCuts(subjects))
    {
      // so, it's a number of items, Are they all sensor contact wrappers
      final SensorContactWrapper[] items =
          cutsToArray(subjects);
      if(items.length > 0)
      {
        final Editable editable = subjects[0];
        // cool, stick with it
        final SensorContactWrapper firstContact =
            (SensorContactWrapper) editable;
        
        final Color firstColor = firstContact.getColor() != null ? firstContact
            .getColor() : firstContact.getSensor().getColor();

        // cool wrap it in an action.
        _myAction = new TMAAction("Generate TMA solution from selected cuts",
            items)
        {
          @Override
          public void run()
          {
            // get the supporting data
            final TMAFromSensorWizard wizard = new TMAFromSensorWizard(
                firstContact.getBearing(), firstContact.getRange(),
                DEFAULT_TARGET_COURSE, DEFAULT_TARGET_SPEED, firstColor);
            final WizardDialog dialog = new WizardDialog(Display.getCurrent()
                .getActiveShell(), wizard);
            dialog.create();
            dialog.open();

            // did it work?
            if (dialog.getReturnCode() == Window.OK)
            {
              final UserChoice answers = getUserChoice(wizard, firstColor);

              // ok, go for it.
              // sort it out as an operation
              final IUndoableOperation convertToTrack1 = new TMAfromCuts(
                  getItems(), theLayers, answers.res, answers.courseDegs,
                  answers.speed, answers.newColor);

              // ok, stick it on the buffer
              runIt(convertToTrack1);
            }
            else
            {
              System.err.println("user cancelled");
            }
          }
        };
      }
    }

    // go for it, or not...
    if (_myAction != null)
      parent.add(_myAction);

  }

  private SensorContactWrapper[] cutsToArray(Editable[] subjects)
  {
    SensorContactWrapper[] res = new SensorContactWrapper[subjects.length];
    for(int i=0;i<subjects.length;i++)
    {
      res[i] = (SensorContactWrapper) subjects[i];
    }
    return res;
  }

  private static abstract class TMAAction extends Action
  {
    private final SensorContactWrapper[] _items;

    public TMAAction(String title, SensorContactWrapper[] items)
    {
      super(title);
      _items = items;
    }

    public SensorContactWrapper[] getItems()
    {
      return _items;
    }

  }

  private boolean allSensors(Editable[] subjects)
  {
    for (final Editable e : subjects)
    {
      if (!(e instanceof SensorWrapper))
      {
        return false;
      }
    }
    return true;
  }

  private boolean allSensorCuts(Editable[] subjects)
  {
    for (final Editable e : subjects)
    {
      if (!(e instanceof SensorContactWrapper))
      {
        return false;
      }
    }
    return true;
  }

  protected UserChoice getUserChoice(final TMAFromSensorWizard wizard,
      final Color firstColor)
  {
    UserChoice choice = new UserChoice();
    choice.res = new WorldVector(0, new WorldDistance(5, WorldDistance.NM),
        null);
    choice.courseDegs = 0d;
    choice.speed = new WorldSpeed(5, WorldSpeed.Kts);

    final RangeBearingPage offsetPage = (RangeBearingPage) wizard.getPage(
        RangeBearingPage.NAME);
    if (offsetPage != null)
    {
      if (offsetPage.isPageComplete())
      {
        choice.res = new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(
            offsetPage.getBearingDegs()), offsetPage.getRange(), null);
      }
    }

    final EnterSolutionPage solutionPage = (EnterSolutionPage) wizard.getPage(
        EnterSolutionPage.NAME);
    if (solutionPage != null)
    {
      if (solutionPage.isPageComplete())
      {
        final EnterSolutionPage.SolutionDataItem item =
            (SolutionDataItem) solutionPage.getEditable();
        choice.courseDegs = item.getCourse();
        choice.speed = item.getSpeed();
      }
    }

    final SelectColorPage colorPage = (SelectColorPage) wizard.getPage(
        SelectColorPage.NAME);
    if (colorPage != null && colorPage.isPageComplete())
    {
      final Color color = colorPage.getColor();
      if (!color.equals(firstColor))
      {
        choice.newColor = color;
      }
      else
      {
        choice.newColor = null;
      }
    }
    else
    {
      choice.newColor = null;
    }

    return choice;
  }

  private static class UserChoice
  {

    public Color newColor;
    public WorldSpeed speed;
    public double courseDegs;
    public WorldVector res;

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
