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
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 * 
 */
public class ConvertAbsoluteTmaToRelative implements
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

    @SuppressWarnings(
    {"deprecation", "unused"})
    private TrackWrapper getLongerTrack()
    {
      final TrackWrapper tw = new TrackWrapper();

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
        tw.addFix(fw);

        lastLoc = new WorldLocation(fw.getLocation());
      }

      final SensorWrapper swa = new SensorWrapper("title one");
      tw.add(swa);
      swa.setSensorOffset(new ArrayLength(-400));

      for (int i = 0; i < 50; i += 3)
      {
        long thisTime = new Date(2016, 1, 14, 12, i, 30).getTime();
        final SensorContactWrapper scwa1 =
            new SensorContactWrapper("aaa", new HiResDate(thisTime), null,
                null, null, null, null, 0, null);
        swa.add(scwa1);
      }

      return tw;
    }

    public void testSplitWithOffset() throws ExecutionException
    {
      // TrackWrapper tw = getLongerTrack();
      //
      // assertNotNull(tw);
      //
      // // get the sensor data
      // SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();
      //
      // assertNotNull(sw);
      //
      // // create a list of cuts (to simulate the selection)
      // SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
      // Enumeration<Editable> numer = sw.elements();
      // int ctr=0;
      // while (numer.hasMoreElements())
      // {
      // SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
      // items[ctr++] = cut;
      // }
      //
      // Layers theLayers = new Layers();
      // WorldVector worldOffset= new WorldVector(Math.PI, 0.002, 0);
      // double tgtCourse = 0;
      // WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);
      //
      // // ok, generate the target track
      // CMAPOperation op = new ConvertToRelative(items, theLayers, worldOffset, tgtCourse,
      // tgtSpeed);
      //
      // // and run it
      // op.execute(null, null);
      //
      // assertEquals("has new data", 1, theLayers.size());
      //
      // TrackWrapper sol = (TrackWrapper) theLayers.elementAt(0);
      // assertNotNull("new layer not found", sol);
      //
      // // ok, now try to split it
      // assertEquals("only has one segment", 1, sol.getSegments().size());
      //
      // RelativeTMASegment seg = (RelativeTMASegment) sol.getSegments().elements().nextElement();
      //
      // assertNotNull("new seg not found", seg);
      //
      // // ok, and we split it.
      // int ctr2 = 0;
      // FixWrapper beforeF = null;
      // FixWrapper afterF = null;
      // Enumeration<Editable> eF = seg.elements();
      // while (eF.hasMoreElements())
      // {
      // FixWrapper fix = (FixWrapper) eF.nextElement();
      // ctr2++;
      // if(ctr2 > seg.size() / 2)
      // {
      // if(beforeF == null)
      // {
      // beforeF = fix;
      // }
      // else
      // {
      // afterF = fix;
      // break;
      // }
      // }
      // }
      //
      // assertNotNull("fix not found", beforeF);
      //
      // // ok, what's the time offset
      // WorldLocation afterBeforeSplit = afterF.getLocation();
      //
      // // ok, time to split
      // SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
      // SubjectAction doSplit = actions[1];
      // doSplit.execute(beforeF);
      //
      // // ok, have another look
      // assertEquals("now has two segments", 2, sol.getSegments().size());
      // Enumeration<Editable> aNum = sol.getSegments().elements();
      // aNum.nextElement();
      // TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
      // WorldLocation locAfterSplit = afterSeg.getTrackStart();
      //
      // assertEquals("origin remains valid", afterBeforeSplit, locAfterSplit);
      //
      // // hey, try the undo
      // doSplit.undo(beforeF);
      //
      // assertEquals("now has one segment again", 1, sol.getSegments().size());
      //
      // // hey, try the undo
      // doSplit.execute(beforeF);
      // assertEquals("now has two segments", 2, sol.getSegments().size());
      //
      // aNum = sol.getSegments().elements();
      // aNum.nextElement();
      // afterSeg = (TrackSegment) aNum.nextElement();
      // locAfterSplit = afterSeg.getTrackStart();
      // assertEquals("origin remains valid, after undo/redo", afterBeforeSplit, locAfterSplit);
      //

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

  private static class ConvertToRelative extends CMAPOperation
  {

    private final Layers _layers;
    @SuppressWarnings("unused")
    private final List<SuitableSegment> _segments;
    @SuppressWarnings("unused")
    private List<RelativeTMASegment> _replacements;

    public ConvertToRelative(Layers theLayers,
        List<SuitableSegment> suitableSegments)
    {
      super("Convert absolute segment(s) to relative");
      _layers = theLayers;
      _segments = suitableSegments;
    }

    @Override
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {

      // create the relative segment

      // remember the relative segment

      // remove the absolute segment

      // detach the absolute segment?

      // add the relative segment

      // fire updated / extended

      // // create it, then
      // final TrackSegment seg = new RelativeTMASegment(_items, _offset, _speed,
      // _courseDegs, _layers);
      //
      // // now wrap it
      // _newTrack = new TrackWrapper();
      // _newTrack.setColor(Color.red);
      // _newTrack.add(seg);
      // final String tNow = TrackSegment.TMA_LEADER
      // + FormatRNDateTime.toString(_newTrack.getStartDTG().getDate()
      // .getTime());
      // _newTrack.setName(tNow);
      //
      // _layers.addThisLayerAllowDuplication(_newTrack);

      // sorted, do the update
      // _layers.fireExtended();

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // remove the relative segment

      // detach the relative segment?

      // add the absolute segment

      // fire updated / extended

      // forget about the new tracks
      // _layers.removeThisLayer(_newTrack);
      _layers.fireExtended();

      return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
        throws ExecutionException
    {
      // _layers.addThisLayerAllowDuplication(_newTrack);

      // sorted, do the update
      _layers.fireExtended();

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
    public SuitableSegment(final AbsoluteTMASegment thisA,
        final SensorWrapper sensor)
    {

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
            System.err.println("Segments must be from same track");

            // TODO: popup dialog
            return;
          }

        }
        else
        {
          // ok, we only work on a collection of abs segments
          System.err.println("All segments must be absolute");

          // TODO: popup dialog
          return;
        }
      }

      if (commonParent == null)
      {
        // ok, we didn't find any relative segments
        System.err.println("No relative segments found");
        return;
      }

      // ok, loop through segments
      for (int i = 0; i < subjects.length; i++)
      {
        AbsoluteTMASegment thisA = (AbsoluteTMASegment) subjects[i];

        // now check for peer relative segments
        RelativeTMASegment before = null;
        RelativeTMASegment after = null;
        Enumeration<Editable> segs = commonParent.getPositions();
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
              if (thisSeg.startDTG().lessThan(thisA.getDTG_End()))
              {
                after = thisRel;
              }
            }
          }
        }

        if (before != null || after != null)
        {
          SensorWrapper beforeS = before.getReferenceSensor();
          SensorWrapper afterS = after.getReferenceSensor();
          if (beforeS != null && afterS != null && beforeS != afterS)
          {
            System.err.println("They're not from the same host");

            // TODO: popup dialog
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
        final ConvertToRelative action =
            new ConvertToRelative(theLayers, suitableSegments);

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
