/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2018, PlanetMayo Ltd
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.WorldLocation;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class SmoothTrackJumps implements RightClickContextItemGenerator
{
  private static class SmoothJumps extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    private final Layers _layers;

    /**
     * list of new fixes we're creating
     */
    private final Collection<Editable> _points;

    /**
     * the track we're interpolating
     */
    private final TrackWrapper _track;

    private HashMap<FixWrapper, WorldLocation> _newFixes;

    public SmoothJumps(final String title, final Layers theLayers,
        final TrackWrapper lTrack, final Collection<Editable> points)
    {
      super(title);
      _layers = theLayers;
      _track = lTrack;
      _points = points;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      // fina what needs fixing
      _newFixes = findFixesToFix(_points);

      // did we find any?
      if (_newFixes == null || _newFixes.size() == 0)
      {
        showMessage("Smooth jumps",
            "No back-tracking jumps were detected in the track segment\n\n"
                + "A jump is detected when n+2 doesn't loosely follow the direction\n"
                + "from n to n+1, but n+3 remains on that route.");

        // ok, return cancel - since this isn't an operation that we can undo
        return Status.CANCEL_STATUS;
      }
      else
      {
        applyFixes(_newFixes);

        // sorted, do the update
        if (_layers != null)
          _layers.fireModified(_track);
        return Status.OK_STATUS;
      }
    }

    @Override
    public IStatus redo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      applyFixes(_newFixes);
      if (_layers != null)
        _layers.fireModified(_track);

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      applyFixes(_newFixes);
      if (_layers != null)
        _layers.fireModified(_track);

      return Status.OK_STATUS;
    }
  }

  public static class TestMe extends TestCase
  {
    private static boolean isOnTrackDegs(final double existingDegs,
        final double newDegs)
    {
      return isOnTrack(MWC.Algorithms.Conversions.Degs2Rads(existingDegs),
          MWC.Algorithms.Conversions.Degs2Rads(newDegs));
    }

    public static void testOnTrack()
    {
      assertTrue("is on track", isOnTrackDegs(90, 90));
      assertTrue("is on track", isOnTrackDegs(90, 1));
      assertTrue("is on track", isOnTrackDegs(90, 179));

      assertFalse("not on track", isOnTrackDegs(90, 359));
      assertFalse("not on track", isOnTrackDegs(90, 270));
      assertFalse("not on track", isOnTrackDegs(90, 181));
      assertFalse("not on track", isOnTrackDegs(90, -90));
      assertFalse("not on track", isOnTrackDegs(90, -1));

      assertTrue("is on track", isOnTrackDegs(-90, -90));
      assertTrue("is on track", isOnTrackDegs(-90, 270));
      assertTrue("is on track", isOnTrackDegs(-90, -1));
      assertTrue("is on track", isOnTrackDegs(-90, -179));
      assertTrue("is on track", isOnTrackDegs(-90, 181));
      assertTrue("is on track", isOnTrackDegs(-90, 359));

      assertFalse("is on track", isOnTrackDegs(-90, 90));
      assertFalse("is on track", isOnTrackDegs(-90, -270));
      assertFalse("is on track", isOnTrackDegs(-90, 1));
      assertFalse("is on track", isOnTrackDegs(-90, 179));
      assertFalse("is on track", isOnTrackDegs(-90, -181));
      assertFalse("is on track", isOnTrackDegs(-90, -359));

      assertTrue("is on track", isOnTrackDegs(0, 89));
      assertTrue("is on track", isOnTrackDegs(0, 1));
      assertTrue("is on track", isOnTrackDegs(0, -89));
      assertTrue("is on track", isOnTrackDegs(0, -1));
      assertTrue("is on track", isOnTrackDegs(0, 0));

      assertFalse("not on track", isOnTrackDegs(0, 159));
      assertFalse("not on track", isOnTrackDegs(0, 260));
      assertFalse("not on track", isOnTrackDegs(0, 181));
      assertFalse("not on track", isOnTrackDegs(0, -91));
      assertFalse("not on track", isOnTrackDegs(0, -260));

      assertTrue("is on track", isOnTrackDegs(180, 159));
      assertTrue("is on track", isOnTrackDegs(180, 260));
      assertTrue("is on track", isOnTrackDegs(180, 181));
      assertTrue("is on track", isOnTrackDegs(180, -91));
      assertTrue("is on track", isOnTrackDegs(-180, 181));
      assertTrue("is on track", isOnTrackDegs(-180, -91));
      assertTrue("is on track", isOnTrackDegs(180, -260));

      assertFalse("not on track", isOnTrackDegs(180, 89));
      assertFalse("not on track", isOnTrackDegs(180, 1));
      assertFalse("not on track", isOnTrackDegs(180, -89));
      assertFalse("not on track", isOnTrackDegs(180, -1));
      assertFalse("not on track", isOnTrackDegs(180, 0));
    }

    public void testFindLegs() throws FileNotFoundException, ExecutionException
    {
      final ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/smooth_back_jumps/jumps.rep";
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();

      // get the sensor track
      final TrackWrapper track = (TrackWrapper) theLayers.findLayer("NONSUCH");

      final TrackSegment seg = (TrackSegment) track.getSegments().first();

      final int len = 826;

      assertEquals("correct num positions", len, seg.size());

      final SmoothJumps jumps = new SmoothJumps("smooth jumps", theLayers,
          track, seg.getData());

      // ok, apply operation
      jumps.execute(null, null);

      assertEquals("still correct num positions", len, seg.size());

      assertEquals("found correct num jumps", 7, jumps._newFixes.size());

      // ok, undo operation
      jumps.undo(null, null);

      assertEquals("still correct num positions", len, seg.size());

      assertEquals("found correct num jumps", 7, jumps._newFixes.size());

      // ok, redo operation
      jumps.undo(null, null);

      assertEquals("still correct num positions", len, seg.size());

      assertEquals("found correct num jumps", 7, jumps._newFixes.size());

    }
  }

  private static void applyFixes(
      final HashMap<FixWrapper, WorldLocation> newFixes)
  {
    for (final FixWrapper fix : newFixes.keySet())
    {
      // what's the new location?
      final WorldLocation newLoc = new WorldLocation(newFixes.get(fix));

      // what's the current location
      final WorldLocation oldLoc = new WorldLocation(fix.getLocation());

      // store the new location
      fix.setLocation(newLoc);

      // and remember the old value
      newFixes.put(fix, oldLoc);

    }
  }

  /**
   * find the legs in the block of data
   *
   * @param points
   * @return
   */
  private static HashMap<FixWrapper, WorldLocation> findFixesToFix(
      final Collection<Editable> points)
  {
    // prepare to store the legs
    final HashMap<FixWrapper, WorldLocation> res =
        new HashMap<FixWrapper, WorldLocation>();

    // get the points
    final Iterator<Editable> iter = points.iterator();

    // ok, here's the logic. We establish the direction of travel from n-3 to n-2.
    // if n-1 doesnt' follow that general trend, but n does, then
    // n-1 is replaced by it's interpolated equivalent.

    // remember the previous position (which we use to "plot on" the last point)
    FixWrapper n_minus_3 = null;
    FixWrapper n_minus_2 = null;
    FixWrapper n_minus_1 = null;

    while (iter.hasNext())
    {
      final FixWrapper subject = (FixWrapper) iter.next();

      // do we have all our positions?
      if (n_minus_3 != null)
      {
        // establish the direction for first two
        final double coreVector = n_minus_2.getLocation().bearingFrom(n_minus_3
            .getLocation());

        // and the direction for next two
        final double testVector = n_minus_1.getLocation().bearingFrom(n_minus_2
            .getLocation());

        // ok, does it appear to back-track?
        if (!isOnTrack(coreVector, testVector))
        {
          // ok, compare with the next leg
          final double nextVector = subject.getLocation().bearingFrom(n_minus_2
              .getLocation());

          // do the test for if it's inline
          final boolean inline = isOnTrack(coreVector, nextVector);

          if (inline)
          {
            // ok, we have to generate the interpolated fix location for the n_minus_1
            final FixWrapper newF = FixWrapper.interpolateFix(n_minus_2,
                subject, n_minus_1.getDateTimeGroup());

            res.put(n_minus_1, new WorldLocation(newF.getLocation()));
          }
        }

      }

      // ok, now move along the bed
      n_minus_3 = n_minus_2;
      n_minus_2 = n_minus_1;
      n_minus_1 = subject;
    }

    return res;
  }

  private static boolean isOnTrack(final double existingRads,
      final double newRads)
  {
    double delta = Math.abs(newRads - existingRads);

    // we're having troubles where large numbers aren't recognised as multiple circles.
    delta += 0.00001;

    while (delta > Math.PI)
    {
      delta -= Math.PI * 2;
    }

    return Math.abs(delta) <= Math.PI / 2;
  }

  protected static void showMessage(final String title, final String txt)
  {
    CorePlugin.showMessage(title, txt);
  }

  /**
   * @param parent
   *          menu
   * @param theLayers
   *          the whole layers
   * @param parentLayers
   *          the parent layers for the selected items
   * @param subjects
   *          the selected items
   */
  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    Collection<Editable> points = null;
    String tmpTitle = null;
    TrackWrapper track = null;

    // check something was selected
    if (subjects.length == 0)
      return;

    // how many items are selected?
    if (subjects.length == 1)
    {
      // is it a track?
      final Editable thisE = subjects[0];
      if (thisE instanceof TrackSegment)
      {
        final TrackSegment ts = (TrackSegment) thisE;
        points = ts.getData();
        tmpTitle = "Smooth back-tracking jumps for selected track";

        if (ts.getWrapper() instanceof TrackWrapper)
        {
          track = ts.getWrapper();
        }
      }
      else if (thisE instanceof TrackWrapper)
      {
        final TrackWrapper tw = (TrackWrapper) thisE;
        final SegmentList segs = tw.getSegments();
        if (segs.size() == 1)
        {
          final TrackSegment seg = (TrackSegment) segs.elements().nextElement();
          points = seg.getData();
          track = tw;
          tmpTitle = "Smooth back-tracking jumps for selected track";
        }
      }
    }
    else
    {
      // see if it's a series of points
      final Editable thisE = subjects[0];
      if (thisE instanceof FixWrapper)
      {
        // collate the points into a collection
        points = new Vector<Editable>();

        // loop through the data
        for (int i = 0; i < subjects.length; i++)
        {
          final Editable editable = subjects[i];

          if (track == null)
          {
            final FixWrapper fix = (FixWrapper) editable;
            if (fix.getTrackWrapper() instanceof TrackWrapper)
            {
              track = (TrackWrapper) fix.getTrackWrapper();
            }
          }

          points.add(editable);
        }

        tmpTitle = "Smooth back-tracking jumps in selected positions";

      }
    }

    // ok, is it worth going for?
    if (points != null && track != null)
    {
      final String title = tmpTitle;
      final Collection<Editable> permPoints = points;

      final TrackWrapper lTrack = track;

      // right,stick in a separator
      parent.add(new Separator());

      // and the new drop-down list of interpolation frequencies

      // yes, create the action
      final Action convertToTrack = new Action(title)
      {
        @Override
        public void run()
        {
          // ok, go for it.
          // sort it out as an operation
          final IUndoableOperation removeJumps = new SmoothJumps(title,
              theLayers, lTrack, permPoints);

          // ok, stick it on the buffer
          CorePlugin.run(removeJumps);
        }

      };
      parent.add(convertToTrack);
    }

  }

}
