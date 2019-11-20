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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.Algorithms.Conversions;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;
import junit.framework.TestCase;

/**
 * @author ian.mayo
 */
public class SplitTracksIntoLegs implements RightClickContextItemGenerator
{

  private static class SplitTracksOperation extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    private final Layers _layers;
    private final List<TrackWrapper> _tracks;
    private final Long _period;
    private final HashMap<TrackWrapper, List<TrackSegment>> _trackChanges;

    public SplitTracksOperation(final String title, final Layers theLayers,
        final List<TrackWrapper> tracks, final Long period)
    {
      super(title);
      _layers = theLayers;
      _tracks = tracks;
      _period = period;
      _trackChanges = new HashMap<TrackWrapper, List<TrackSegment>>();
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

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      boolean modified = false;
      _trackChanges.clear();

      // loop through the tracks
      for (final TrackWrapper track : _tracks)
      {
        final List<TrackSegment> newSegments = TrackWrapper_Support
            .splitTrackAtJumps(track, _period);
        modified = modified || !newSegments.isEmpty();
        _trackChanges.put(track, newSegments);
      }

      // did anything get changed
      if (modified)
      {
        fireModified();
      }
      return Status.OK_STATUS;
    }

    private void fireModified()
    {
      _layers.fireExtended();
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      int numChanges = 0;

      // ok, merge the segments
      for (final TrackWrapper track : _trackChanges.keySet())
      {
        final List<TrackSegment> splits = _trackChanges.get(track);

        final TrackSegment target = splits.get(0);

        int ctr = 0;
        for (final TrackSegment segment : splits)
        {
          if (segment != target)
          {
            // remove the segment
            track.removeElement(segment);

            final Enumeration<Editable> fixes = segment.elements();
            while (fixes.hasMoreElements())
            {
              final FixWrapper fix = (FixWrapper) fixes.nextElement();
              target.addFix(fix);
            }
            ctr++;
          }
        }

        numChanges += ctr;
      }

      final boolean modified = numChanges > 0;

      // did anything get changed
      if (modified)
      {
        fireModified();
      }
      return Status.OK_STATUS;
    }
  }

  public static class TestSplittingTracks extends TestCase
  {
    private static FixWrapper getFix(final long dtg, final double course,
        final double speed)
    {
      final Fix theFix = new Fix(new HiResDate(dtg), new WorldLocation(2, 2, 2),
          course, Conversions.Kts2Yps(speed));
      final FixWrapper res = new FixWrapper(theFix);
      return res;
    }

    private static TrackWrapper getOne()
    {
      final TrackWrapper tOne = new TrackWrapper();
      tOne.setName("t-1");
      tOne.addFix(getFix(1000, 22, 33));
      tOne.addFix(getFix(2000, 22, 33));
      tOne.addFix(getFix(2100, 22, 33));
      tOne.addFix(getFix(4000, 22, 33));
      tOne.addFix(getFix(5000, 22, 33));
      tOne.addFix(getFix(6000, 22, 33));
      tOne.addFix(getFix(7000, 22, 33));
      tOne.addFix(getFix(8000, 22, 33));
      tOne.addFix(getFix(9000, 22, 33));
      tOne.addFix(getFix(10000, 22, 33));
      tOne.addFix(getFix(11100, 22, 33));
      tOne.addFix(getFix(12000, 22, 33));
      tOne.addFix(getFix(13000, 22, 33));
      tOne.addFix(getFix(14000, 22, 33));
      return tOne;
    }

    private static TrackWrapper getTwo()
    {
      final TrackWrapper tTwo = new TrackWrapper();
      tTwo.setName("t-2");
      tTwo.addFix(getFix(1000, 22, 33));
      tTwo.addFix(getFix(2000, 22, 33));
      tTwo.addFix(getFix(2100, 22, 33));
      tTwo.addFix(getFix(4000, 22, 33));
      tTwo.addFix(getFix(5000, 22, 33));
      tTwo.addFix(getFix(8000, 22, 33));
      tTwo.addFix(getFix(9000, 22, 33));
      tTwo.addFix(getFix(10000, 22, 33));
      tTwo.addFix(getFix(11100, 22, 33));
      tTwo.addFix(getFix(12000, 22, 33));
      tTwo.addFix(getFix(13000, 22, 33));
      tTwo.addFix(getFix(14000, 22, 33));
      return tTwo;
    }

    public void testSplitOperation() throws ExecutionException
    {

      final TrackWrapper tOne = getOne();

      final TrackWrapper tTwo = getTwo();

      final Layers layers = new Layers();
      layers.addThisLayer(tOne);
      layers.addThisLayer(tTwo);

      final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();
      tracks.add(tOne);
      tracks.add(tTwo);
      final SplitTracksOperation oper = new SplitTracksOperation("Split tracks",
          layers, tracks, 1000L);

      assertEquals("just one leg", 1, tOne.getSegments().size());
      assertEquals("just one leg", 1, tTwo.getSegments().size());
      assertEquals("correct positions", 14, tOne.numFixes());
      assertEquals("correct positions", 12, tTwo.numFixes());

      oper.execute(null, null);

      // check the contents of the operation
      final HashMap<TrackWrapper, List<TrackSegment>> map = oper._trackChanges;
      assertEquals("two tracks", 2, map.keySet().size());

      assertEquals("more leg", 3, tOne.getSegments().size());
      assertEquals("more legs", 4, tTwo.getSegments().size());
      assertEquals("correct positions", 14, tOne.numFixes());
      assertEquals("correct positions", 12, tTwo.numFixes());

      oper.undo(null, null);

      assertEquals("just one leg", 1, tOne.getSegments().size());
      assertEquals("just one leg", 1, tTwo.getSegments().size());
      assertEquals("correct positions", 14, tOne.numFixes());
      assertEquals("correct positions", 12, tTwo.numFixes());
    }
  }

  @Override
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    boolean goForIt = true;

    final List<TrackWrapper> tracks = new ArrayList<TrackWrapper>();

    // we're only going to work with one or more items
    if (subjects.length > 0)
    {
      // are they tracks?
      for (int i = 0; i < subjects.length; i++)
      {
        final Editable thisE = subjects[i];
        if (thisE instanceof TrackWrapper)
        {
          goForIt = true;
          tracks.add((TrackWrapper) thisE);
        }
        else
        {
          goForIt = false;
        }
      }
    }

    // check we got more than one to group
    if (tracks.size() < 1)
      goForIt = false;

    // ok, is it worth going for?
    if (goForIt)
    {
      // right,stick in a separator
      parent.add(new Separator());

      final String msg = tracks.size() > 1 ? "tracks" : "track";

      final String fullMsg = "Split " + msg + " into segments on gaps over...";

      final MenuManager listing = new MenuManager(fullMsg);

      final HashMap<Long, String> choices = new HashMap<Long, String>();
      choices.put(60 * 1000L, "1 Minute");
      choices.put(60 * 60 * 1000L, "1 Hour");
      choices.put(24 * 60 * 60 * 1000L, "1 Day");
      choices.put(7 * 24 * 60 * 60 * 1000L, "1 Week");

      for (final Long period : choices.keySet())
      {
        // get the time period
        final String label = choices.get(period);

        // create this operation
        final Action doMerge = new Action(label)
        {
          @Override
          public void run()
          {
            final IUndoableOperation theAction = new SplitTracksOperation(
                "Split tracks at jumps over " + label, theLayers, tracks,
                period);
            CorePlugin.run(theAction);
          }
        };
        listing.add(doMerge);
      }
      parent.add(listing);
    }
  }
}
