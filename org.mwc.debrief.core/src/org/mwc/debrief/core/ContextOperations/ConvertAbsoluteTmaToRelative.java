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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 * 
 */
public class ConvertAbsoluteTmaToRelative implements
    RightClickContextItemGenerator
{

  private static class ShowMessage extends Action
  {
    private String _title;
    private String _message;

    public ShowMessage(String title, String message)
    {
      super("Convert segment(s) from absolute to relative");
      _title = title;
      _message = message;
    }
    
    @Override
    public void run()
    {
      CorePlugin.errorDialog(_title,
          "Sorry can't convert segment: " + 
          _message);
    }
  }

  private static class ConvertToRelativeOperation extends CMAPOperation
  {

    private final Layers _layers;
    private final List<SuitableSegment> _segments;
    private List<RelativeTMASegment> _replacements;
    private TrackWrapper _commonParent;

    public ConvertToRelativeOperation(Layers theLayers,
        List<SuitableSegment> suitableSegments, TrackWrapper commonParent)
    {
      super("Convert absolute segment(s) to relative");
      _layers = theLayers;
      _segments = suitableSegments;
      _commonParent = commonParent;
    }

    protected static RelativeTMASegment createSegment(Layers layers,
        SuitableSegment seg, TrackWrapper track, AbsoluteTMASegment absSegment,
        SensorWrapper sensor)
    {
      // sort out the offset
      final WorldLocation tOrigin =
          track.getBacktraceTo(absSegment.getDTG_Start(),
              sensor.getSensorOffset(), sensor.getWormInHole()).getLocation();

      final WorldLocation sOrigin = absSegment.getTrackStart();

      final WorldVector offset = sOrigin.subtract(tOrigin);
      
      // create the relative segment
      final HiResDate startTime = absSegment.getDTG_Start();
      final HiResDate endTime = absSegment.getDTG_End();
      final Collection<Editable> dataPoints = absSegment.getData();
      RelativeTMASegment rSeg =
          new RelativeTMASegment(sensor, offset, absSegment.getSpeed(),
              absSegment.getCourse(), dataPoints, layers, track, startTime, endTime);

      return rSeg;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {

      // loop through the segmenets
      Iterator<SuitableSegment> iter = _segments.iterator();
      while (iter.hasNext())
      {
        ConvertAbsoluteTmaToRelative.SuitableSegment seg =
            (ConvertAbsoluteTmaToRelative.SuitableSegment) iter.next();

        // calculate the data for the relative segment
        final AbsoluteTMASegment absSegment = seg._thisA;
        final SensorWrapper sensor = seg._sensor;
        final TrackWrapper track = sensor.getHost();

        RelativeTMASegment rSeg =
            createSegment(_layers, seg, track, absSegment, sensor);
        rSeg.setName(absSegment.getName());

        // remember the relative segment
        if(_replacements == null)
        {
          _replacements = new ArrayList<RelativeTMASegment>();
        }
        _replacements.add(rSeg);

        // remove the absolute segment
        _commonParent.removeElement(absSegment);
        
        // add the relative segment
        _commonParent.add(rSeg);

      }
      // fire updated / extended
      _layers.fireExtended(null, _commonParent);

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {

      Iterator<RelativeTMASegment> rIter = _replacements.iterator();
      while (rIter.hasNext())
      {
        RelativeTMASegment relativeTMASegment =
            (RelativeTMASegment) rIter.next();

        _commonParent.removeElement(relativeTMASegment);
      }

      // loop through the segmenets
      Iterator<SuitableSegment> iter = _segments.iterator();
      while (iter.hasNext())
      {
        ConvertAbsoluteTmaToRelative.SuitableSegment seg =
            (ConvertAbsoluteTmaToRelative.SuitableSegment) iter.next();

        _commonParent.add(seg._thisA);
      }

      // fire updated / extended
      _layers.fireExtended(null, _commonParent);

      return Status.OK_STATUS;

    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {

      // loop through the segmenets
      Iterator<SuitableSegment> iter = _segments.iterator();
      while (iter.hasNext())
      {
        ConvertAbsoluteTmaToRelative.SuitableSegment seg =
            (ConvertAbsoluteTmaToRelative.SuitableSegment) iter.next();

        _commonParent.removeElement(seg._thisA);
      }

      Iterator<RelativeTMASegment> rIter = _replacements.iterator();
      while (rIter.hasNext())
      {
        RelativeTMASegment relativeTMASegment =
            (RelativeTMASegment) rIter.next();

        _commonParent.add(relativeTMASegment);
      }

      // fire updated / extended
      _layers.fireExtended(null, _commonParent);

      return Status.OK_STATUS;
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

  }

  private static class SuitableSegment
  {
    private AbsoluteTMASegment _thisA;
    private SensorWrapper _sensor;

    public SuitableSegment(final AbsoluteTMASegment thisA,
        final SensorWrapper sensor)
    {
      _thisA = thisA;
      _sensor = sensor;
    }
  }

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    // so, see if it's something we can do business with
    if (subjects.length > 0)
    {
      TrackWrapper commonParent = null;
      List<SuitableSegment> suitableSegments = null;

      for (int i = 0; i < subjects.length; i++)
      {
        Editable editable = subjects[i];
        if (editable instanceof CoreTMASegment)
        {
          if (editable instanceof AbsoluteTMASegment)
          {
            // cool, go for it.
            AbsoluteTMASegment abs = (AbsoluteTMASegment) editable;

            // have a look at the parent
            TrackWrapper thisParent = abs.getWrapper();

            // does it match?
            if (commonParent == null || commonParent == thisParent)
            {
              // cool, go for it
              commonParent = thisParent;
            }
            else
            {
              // don't bother, we didn't find anything useful
              return;
            }
          }
          else
          {
            // ok, we only work on a collection of abs segments
            return;
          }
        }
      }

      if (commonParent == null)
      {
        // ok, we didn't find any relative segments
        return;
      }

      // ok, loop through segments
      for (int i = 0; i < subjects.length; i++)
      {
        AbsoluteTMASegment thisA = (AbsoluteTMASegment) subjects[i];

        // now check for peer relative segments
        RelativeTMASegment before = null;
        RelativeTMASegment after = null;
        Enumeration<Editable> segs = commonParent.getSegments().elements();
        while (segs.hasMoreElements())
        {
          TrackSegment thisSeg = (TrackSegment) segs.nextElement();

          // is this one relative?
          if (thisSeg instanceof RelativeTMASegment)
          {
            RelativeTMASegment thisRel = (RelativeTMASegment) thisSeg;
            // ok, is this one before us?
            if (thisSeg.endDTG().lessThan(thisA.getDTG_Start()))
            {
              before = thisRel;
            }

            // ready to look for after?
            if (before != null)
            {
              if (thisSeg.startDTG().greaterThan(thisA.getDTG_End()))
              {
                after = thisRel;
              }
            }
          }
        }

        if (before == null && after == null)
        {
          return;
        }
        else if (before != null || after != null)
        {
          SensorWrapper beforeS = null;
          SensorWrapper afterS = null;
          if (before != null)
            beforeS = before.getReferenceSensor();
          if (after != null)
            afterS = after.getReferenceSensor();

          if (beforeS == null && afterS == null)
          {
            return;
          }

          else if (beforeS != null && afterS != null && beforeS != afterS)
          {
            parent.add(new ShowMessage("Can't convert track", "Adjacent TMA Segments must be relative to same sensor"));
            return;
          }

          final SensorWrapper sensor;
          if (beforeS != null)
            sensor = beforeS;
          else
            sensor = afterS;

          // we must be ok, generate action
          SuitableSegment suitableSegment = new SuitableSegment(thisA, sensor);

          if (suitableSegments == null)
          {
            suitableSegments = new ArrayList<SuitableSegment>();
          }
          suitableSegments.add(suitableSegment);
        }
      }

      if (suitableSegments != null)
      {
        final String phrase;
        if (suitableSegments.size() > 1)
        {
          phrase = "segments";
        }
        else
        {
          phrase = "segment";
        }

        // ok, generate it
        final IUndoableOperation action =
            getOperation(theLayers, suitableSegments, commonParent);

        Action doIt =
            new Action("Convert " + phrase + " from absolute to relative")
            {
              @Override
              public void run()
              {
                runIt(action);
              }
            };

        // ok, go for it
        parent.add(doIt);
      }
    }

  }

  /**
   * move the operation generation to a method, so it can be overwritten (in testing)
   * 
   * 
   * @param theLayers
   * @param suitableSegments
   * @param commonParent
   * @return
   */
  protected IUndoableOperation getOperation(Layers theLayers,
      List<SuitableSegment> suitableSegments, TrackWrapper commonParent)
  {
    return new ConvertToRelativeOperation(theLayers, suitableSegments, commonParent);
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

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    @SuppressWarnings(
    {"deprecation"})
    private TrackWrapper getLongerTrack()
    {
      final TrackWrapper tw = new TrackWrapper();
      tw.setName("Some Name");

      TrackSegment ts = new TrackSegment(false);

      final WorldLocation loc_1 = new WorldLocation(0.00000001, 0.000000001, 0);
      WorldLocation lastLoc = loc_1;

      for (int i = 0; i < 50; i++)
      {

        long thisTime = new Date(2016, 1, 14, 12, i, 0).getTime();
        final FixWrapper fw =
            new FixWrapper(new Fix(new HiResDate(thisTime), lastLoc
                .add(getVector(25, 0)),
                MWC.Algorithms.Conversions.Degs2Rads(0), 110));
        fw.setLabel("fw1");
        ts.addFix(fw);

        lastLoc = new WorldLocation(fw.getLocation());
      }

      tw.add(ts);

      final SensorWrapper swa = new SensorWrapper("title one");
      tw.add(swa);
      swa.setSensorOffset(new ArrayLength(-400));

      for (int i = 0; i < 500; i += 3)
      {
        long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
        final SensorContactWrapper scwa1 =
            new SensorContactWrapper("aaa", new HiResDate(thisTime), null,
                null, null, null, null, 0, null);
        swa.add(scwa1);
      }

      return tw;
    }

    private static class TestOperation extends AbstractOperation
    {

      @SuppressWarnings("unused")
      final private Layers _theLayers;
      @SuppressWarnings("unused")
      final private List<SuitableSegment> _suitableSements;
      @SuppressWarnings("unused")
      private TrackWrapper _parent;

      public TestOperation(String label, Layers theLayers,
          List<SuitableSegment> suitableSegments, TrackWrapper parent)
      {
        super(label);
        _theLayers = theLayers;
        _suitableSements = suitableSegments;
        _parent = parent;
      }

      @Override
      public IStatus execute(IProgressMonitor monitor, IAdaptable info)
          throws ExecutionException
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IStatus redo(IProgressMonitor monitor, IAdaptable info)
          throws ExecutionException
      {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public IStatus undo(IProgressMonitor monitor, IAdaptable info)
          throws ExecutionException
      {
        // TODO Auto-generated method stub
        return null;
      }

    }

    List<IAction> actions = new ArrayList<IAction>();

    @SuppressWarnings("deprecation")
    public void testApplicable() throws ExecutionException
    {
      actions.clear();

      TrackWrapper tw = getLongerTrack();
      final SensorWrapper sw = new SensorWrapper("name");
      final SensorWrapper sw2 = new SensorWrapper("un-name");
      tw.add(sw);
      tw.add(sw2);

      ConvertAbsoluteTmaToRelative op = new ConvertAbsoluteTmaToRelative()
      {

        @Override
        protected IUndoableOperation getOperation(Layers theLayers,
            List<SuitableSegment> suitableSegments, TrackWrapper parent)
        {
          return new TestOperation("Label", theLayers, suitableSegments, parent);
        }
      };

      final MenuManager menu = new MenuManager("some name")
      {

        @Override
        public void add(IAction action)
        {
          super.add(action);
          actions.add(action);
        }
      };

      Layers theLayers = new Layers();
      theLayers.addThisLayer(tw);
      Editable[] subjects = new Editable[]
      {tw, sw};
      op.generate(menu, theLayers, null, subjects);

      assertEquals("no items added", 0, actions.size());

      WorldSpeed a1Speed = new WorldSpeed(12, WorldSpeed.Kts);

      WorldLocation a1Origin = new WorldLocation(12, 12, 0);
      HiResDate a1Start =
          new HiResDate(new Date(2016, 1, 14, 12, 30, 0).getTime());
      HiResDate a1end =
          new HiResDate(new Date(2016, 1, 14, 12, 40, 0).getTime());
      AbsoluteTMASegment a1 =
          new AbsoluteTMASegment(12, a1Speed, a1Origin, a1Start, a1end);
      a1.setWrapper(tw);

      subjects = new Editable[]
      {a1};

      actions.clear();

      op.generate(menu, theLayers, null, subjects);

      assertEquals("no items added", 0, actions.size());

      // ok, remove the track segment
      tw.removeElement(tw.getSegments().first());

      // check it worked
      assertEquals("empty segs", 0, tw.getSegments().size());

      WorldVector theOffset = new WorldVector(12, 0.002, 0);
      // ok, add a relative segment before it
      final RelativeTMASegment r1 =
          new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw
              .getName(), "other sensor");

      long thisTime = new Date(2016, 1, 14, 12, 22, 0).getTime();
      FixWrapper fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(25, 50)), MWC.Algorithms.Conversions.Degs2Rads(0),
              110));
      fw.setLabel("fw1");
      r1.addFix(fw);

      thisTime = new Date(2016, 1, 14, 12, 24, 0).getTime();
      fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(23, 200)),
              MWC.Algorithms.Conversions.Degs2Rads(0), 110));
      fw.setLabel("fw2");
      r1.addFix(fw);
      r1.setLayers(theLayers);

      tw.add(r1);

      actions.clear();
      op.generate(menu, theLayers, null, subjects);
      assertEquals("no items added", 0, actions.size());

      // now correct the sensor name
      final RelativeTMASegment r2 =
          new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw
              .getName(), sw.getName());
      Enumeration<Editable> enumer = r1.elements();
      while (enumer.hasMoreElements())
      {
        Editable editable = (Editable) enumer.nextElement();
        r2.add(editable);
      }
      r2.setLayers(theLayers);

      tw.removeElement(r1);
      tw.add(r2);

      System.out.println("r1 ends at:" + r2.getDTG_End() + " a1 starts at:"
          + a1.getDTG_Start());

      actions.clear();
      op.generate(menu, theLayers, null, subjects);
      assertEquals("generated an action items added", 1, actions.size());

      // - add later track
      // ok, add a relative segment before it
      final RelativeTMASegment r3 =
          new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw
              .getName(), sw2.getName());

      thisTime = new Date(2016, 1, 14, 12, 50, 0).getTime();
      fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(25, 50)), MWC.Algorithms.Conversions.Degs2Rads(0),
              110));
      fw.setLabel("fw1");
      r3.addFix(fw);

      thisTime = new Date(2016, 1, 14, 12, 59, 0).getTime();
      fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(23, 200)),
              MWC.Algorithms.Conversions.Degs2Rads(0), 110));
      fw.setLabel("fw2");
      r3.addFix(fw);
      r3.setLayers(theLayers);

      tw.add(r3);

      actions.clear();
      op.generate(menu, theLayers, null, subjects);
      assertEquals("no items added", 0, actions.size());

      tw.removeElement(r3);

      // - add later track
      // ok, add a relative segment before it
      final RelativeTMASegment r4 =
          new RelativeTMASegment(13, a1Speed, theOffset, theLayers, tw
              .getName(), sw.getName());

      thisTime = new Date(2016, 1, 14, 12, 50, 0).getTime();
      fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(25, 50)), MWC.Algorithms.Conversions.Degs2Rads(0),
              110));
      fw.setLabel("fw1");
      r4.addFix(fw);

      thisTime = new Date(2016, 1, 14, 12, 59, 0).getTime();
      fw =
          new FixWrapper(new Fix(new HiResDate(thisTime), a1Origin
              .add(getVector(23, 200)),
              MWC.Algorithms.Conversions.Degs2Rads(0), 110));
      fw.setLabel("fw2");
      r4.addFix(fw);
      r3.setLayers(theLayers);
      tw.add(r4);

      actions.clear();
      op.generate(menu, theLayers, null, subjects);
      assertEquals("items added", 1, actions.size());
    }

    /**
     * @return
     */
    private WorldVector getVector(final double courseDegs, final double distM)
    {
      return new WorldVector(MWC.Algorithms.Conversions.Degs2Rads(courseDegs),
          new WorldDistance(distM, WorldDistance.METRES), null);
    }
  }
}
