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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.ArrayOffsetHelper.LegacyArrayOffsetModes;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;

/**
 * @author ian.mayo
 */
public class GenerateInfillSegment implements RightClickContextItemGenerator
{

  private static final String DELETE_SUFFIX =
      " (delete from 2nd track if necessary)";

  public static class TestGenInfill extends TestCase
  {
    final private ArrayList<String> messages = new ArrayList<String>();
    final private GenerateInfillSegment gener = new GenerateInfillSegment()
    {

      @Override
      protected ErrorLogger getLogger()
      {
        return getMyLogger();
      }
    };

    protected ErrorLogger getMyLogger()
    {
      return new ErrorLogger()
      {

        @Override
        public void logError(int status, String text, Exception e)
        {
          messages.add(text);
        }

        @Override
        public void logError(int status, String text, Exception e,
            boolean revealLog)
        {
          logError(status, text, null);
        }

        @Override
        public void logStack(int status, String text)
        {
          logError(status, text, null);
        }
      };

    }

    @Override
    protected void setUp() throws Exception
    {
      super.setUp();

      // clear any messages
      messages.clear();
    }

    @SuppressWarnings("deprecation")
    public void testTooSmallSecondTooShort() throws ExecutionException
    {
      Layers theLayers = new Layers();
      TrackWrapper track = new TrackWrapper();

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 12, 2, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, theLayers, track,
              getMyLogger(), true);

      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 1, messages.size());
      assertEquals("correct message", GenerateInfillOperation.CANT_DELETE,
          messages.get(0));

      // check how many entries get deleted
      assertEquals("correct after len", 2, legTwo.getData().size());
    }

    @SuppressWarnings("deprecation")
    public void testTooSmall() throws ExecutionException
    {
      Layers theLayers = new Layers();
      IMenuManager parent = new MenuManager();
      TrackWrapper track = new TrackWrapper();
      Layer[] parentLayers = new Layer[]
      {track, track};

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};
      gener.generate(parent, theLayers, parentLayers, subjects);
      IContributionItem[] newItems = parent.getItems();

      assertEquals("newItems", 3, newItems.length);

      ActionContributionItem first = (ActionContributionItem) newItems[1];
      assertEquals("right name", "Generate infill segment", first.getAction()
          .getText());
      first.getAction().run();

      assertEquals("got error message", 1, messages.size());
      assertEquals("correct error message",
          GenerateInfillOperation.INSUFFICIENT_TIME, messages.get(0));

      assertEquals("correct before len", 60, legTwo.getData().size());

      messages.clear();
      first = (ActionContributionItem) newItems[2];
      assertEquals("right name",
          "Generate infill segment" + DELETE_SUFFIX, first
              .getAction().getText());
      first.getAction().run();

      assertEquals("got no error message", 0, messages.size());

      // check how many entries get deleted
      assertEquals("correct after len", 57, legTwo.getData().size());
    }

    @SuppressWarnings("deprecation")
    public void testUndoRelative2() throws ExecutionException
    {
      Layers theLayers = new Layers();
      TrackWrapper tmaTrack = new TrackWrapper();
      TrackWrapper hostTrack = new TrackWrapper();

      WorldLocation origin = new WorldLocation(44, 44, 44);
      WorldVector _offset = new WorldVector(12, 0.1, 0);

      SensorWrapper sensor1 = new SensorWrapper("sensor 1");
      sensor1.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
      hostTrack.add(sensor1);
      SensorContactWrapper[] cuts1 = new SensorContactWrapper[10];
      for (int i = 0; i < 10; i++)
      {
        HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 1 + i, 0));
        SensorContactWrapper contact = new SensorContactWrapper();
        contact.setDTG(date);
        contact.setBearing(i);
        contact.setOrigin(origin);
        sensor1.add(contact);
        cuts1[i] = contact;
      }

      SensorWrapper sensor2 = new SensorWrapper("sensor 2");
      sensor2.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
      hostTrack.add(sensor2);
      SensorContactWrapper[] cuts2 = new SensorContactWrapper[10];
      for (int i = 0; i < 10; i++)
      {
        HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 10 + i, 30));
        SensorContactWrapper contact = new SensorContactWrapper();
        contact.setDTG(date);
        contact.setOrigin(origin);
        contact.setBearing(i);
        sensor2.add(contact);
        cuts2[i] = contact;
      }

      CoreTMASegment legOne =
          new RelativeTMASegment(cuts1, _offset, new WorldSpeed(13,
              WorldSpeed.Kts), 12, theLayers, null);

      CoreTMASegment legTwo =
          new RelativeTMASegment(cuts2, _offset, new WorldSpeed(13,
              WorldSpeed.Kts), 12, theLayers, null);

      tmaTrack.add(legOne);
      tmaTrack.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, theLayers, tmaTrack,
              getMyLogger(), true);

      assertEquals("correct before len", 10, legTwo.getData().size());
      assertEquals("correct legs", 2, tmaTrack.getSegments().size());
      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 0, messages.size());
      assertEquals("correct legs", 3, tmaTrack.getSegments().size());

      // check how many entries get deleted
      assertEquals("correct after len", 7, legTwo.getData().size());

      // TODO - test undo processing, check second leg same as original length
      operation.undo(null, null);
      assertEquals("correct after len", 10, legTwo.getData().size());
      assertEquals("correct legs", 2, tmaTrack.getSegments().size());
    }

    @SuppressWarnings("deprecation")
    public void testUndoRelative() throws ExecutionException
    {
      Layers theLayers = new Layers();
      TrackWrapper tmaTrack = new TrackWrapper();
      TrackWrapper hostTrack = new TrackWrapper();

      WorldLocation origin = new WorldLocation(44, 44, 44);
      WorldVector _offset = new WorldVector(12, 0.1, 0);

      SensorWrapper sensor1 = new SensorWrapper("sensor 1");
      sensor1.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
      hostTrack.add(sensor1);
      SensorContactWrapper[] cuts1 = new SensorContactWrapper[10];
      for (int i = 0; i < 10; i++)
      {
        HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 1 + i, 0));
        SensorContactWrapper contact = new SensorContactWrapper();
        contact.setDTG(date);
        contact.setBearing(i);
        contact.setOrigin(origin);
        sensor1.add(contact);
        cuts1[i] = contact;
      }

      SensorWrapper sensor2 = new SensorWrapper("sensor 2");
      sensor2.setArrayCentreMode(LegacyArrayOffsetModes.PLAIN);
      hostTrack.add(sensor2);
      SensorContactWrapper[] cuts2 = new SensorContactWrapper[10];
      for (int i = 0; i < 10; i++)
      {
        HiResDate date = new HiResDate(new Date(2012, 1, 1, 12, 21 + i, 0));
        SensorContactWrapper contact = new SensorContactWrapper();
        contact.setDTG(date);
        contact.setOrigin(origin);
        contact.setBearing(i);
        sensor2.add(contact);
        cuts2[i] = contact;
      }

      CoreTMASegment legOne =
          new RelativeTMASegment(cuts1, _offset, new WorldSpeed(13,
              WorldSpeed.Kts), 12, theLayers, null);

      CoreTMASegment legTwo =
          new RelativeTMASegment(cuts2, _offset, new WorldSpeed(13,
              WorldSpeed.Kts), 12, theLayers, null);

      tmaTrack.add(legOne);
      tmaTrack.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, theLayers, tmaTrack,
              getMyLogger(), true);

      assertEquals("correct before len", 10, legTwo.getData().size());
      assertEquals("correct legs", 2, tmaTrack.getSegments().size());
      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 0, messages.size());
      assertEquals("correct legs", 3, tmaTrack.getSegments().size());

      // check how many entries get deleted
      assertEquals("correct after len", 10, legTwo.getData().size());

      // TODO - test undo processing, check second leg same as original length
      operation.undo(null, null);
      assertEquals("correct after len", 10, legTwo.getData().size());
      assertEquals("correct legs", 2, tmaTrack.getSegments().size());
    }

    @SuppressWarnings("deprecation")
    public void testRepeat() throws ExecutionException
    {
      Layers theLayers = new Layers();
      TrackWrapper track = new TrackWrapper();

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, theLayers, track,
              getMyLogger(), true);

      assertEquals("correct before len", 60, legTwo.getData().size());
      assertEquals("correct legs", 2, track.getSegments().size());
      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 0, messages.size());
      assertEquals("correct legs", 3, track.getSegments().size());

      // ok, now try to do it again
      subjects = new Editable[3];
      int ctr = 0;
      Iterator<Editable> iter = track.getSegments().getData().iterator();
      while (iter.hasNext())
      {
        subjects[ctr++] = iter.next();
      }
      operation =
          new GenerateInfillOperation("title", subjects, theLayers, track,
              getMyLogger(), true);

      assertEquals("correct before len", 57, legTwo.getData().size());
      assertEquals("correct legs", 3, track.getSegments().size());
      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 0, messages.size());
      assertEquals("correct legs", 3, track.getSegments().size());

    }

    @SuppressWarnings("deprecation")
    public void testUndo() throws ExecutionException
    {
      Layers theLayers = new Layers();
      TrackWrapper track = new TrackWrapper();

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 0, 1));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, theLayers, track,
              getMyLogger(), true);

      assertEquals("correct before len", 60, legTwo.getData().size());
      assertEquals("correct legs", 2, track.getSegments().size());
      messages.clear();

      operation.execute(null, null);

      assertEquals("got no error message", 0, messages.size());
      assertEquals("correct legs", 3, track.getSegments().size());

      // check how many entries get deleted
      assertEquals("correct after len", 57, legTwo.getData().size());

      // test undo processing, check second leg same as original length
      operation.undo(null, null);
      assertEquals("correct after len", 60, legTwo.getData().size());
      assertEquals("correct legs", 2, track.getSegments().size());
    }

    @SuppressWarnings("deprecation")
    public void testOverlap()
    {
      Layers theLayers = new Layers();
      IMenuManager parent = new MenuManager();
      TrackWrapper track = new TrackWrapper();
      Layer[] parentLayers = new Layer[]
      {track, track};

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 11, 55, 0));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};
      gener.generate(parent, theLayers, parentLayers, subjects);
      IContributionItem[] newItems = parent.getItems();

      assertEquals("newItems", 3, newItems.length);

      ActionContributionItem first = (ActionContributionItem) newItems[1];
      assertEquals("right name", "Generate infill segment", first.getAction()
          .getText());
      first.getAction().run();

      assertEquals("got error message", 1, messages.size());
      assertEquals("correct error message",
          GenerateInfillOperation.OVERLAPPING, messages.get(0));
    }

    @SuppressWarnings("deprecation")
    public void testValid()
    {
      Layers theLayers = new Layers();
      IMenuManager parent = new MenuManager();
      TrackWrapper track = new TrackWrapper();
      Layer[] parentLayers = new Layer[]
      {track, track};

      HiResDate l1_start = new HiResDate(new Date(2012, 1, 1, 11, 0, 0));
      HiResDate l1_end = new HiResDate(new Date(2012, 1, 1, 12, 0, 0));

      HiResDate l2_start = new HiResDate(new Date(2012, 1, 1, 12, 15, 0));
      HiResDate l2_end = new HiResDate(new Date(2012, 1, 1, 13, 0, 0));

      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l1_start, l1_end);
      AbsoluteTMASegment legTwo =
          new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts),
              origin, l2_start, l2_end);

      track.add(legOne);
      track.add(legTwo);

      Editable[] subjects = new Editable[]
      {legOne, legTwo};
      gener.generate(parent, theLayers, parentLayers, subjects);
      IContributionItem[] newItems = parent.getItems();

      assertEquals("newItems", 3, newItems.length);

      ActionContributionItem first = (ActionContributionItem) newItems[1];
      assertEquals("right name", "Generate infill segment", first.getAction()
          .getText());

      // check the before len
      assertEquals("Correct before len", 46, legTwo.getData().size());
      final int beforeLen = legTwo.getData().size();

      first.getAction().run();

      assertEquals("got error message", 0, messages.size());
      assertEquals("correct legs", 3, track.getSegments().size());

      // check the len still valid
      assertEquals("Correct before len", beforeLen, legTwo.getData().size());

      // ok, check that if we split the second, we don't lose the infill

      // get a point in the second leg
      FixWrapper target = null;
      Collection<Editable> secondCuts = legTwo.getData();
      Iterator<Editable> iter = secondCuts.iterator();
      for (int i = 0; i < 6; i++)
      {
        target = (FixWrapper) iter.next();
      }

      assertNotNull("found it", target);

      track.splitTrack(target, false);

      // check we now have 4 legs
      assertEquals("correct legs", 4, track.getSegments().size());
    }
  }

  public static class GenerateInfillOperation extends CMAPOperation
  {

    public static final String INSUFFICIENT_TIME =
        "Insufficient time to insert data. Please try deleting some points.";
    public static final String OVERLAPPING =
        "Sorry, this operation cannot be performed for overlapping track sections\nPlease delete overlapping data points and try again";
    public static final String CANT_DELETE =
        "We can't delete points from the following track, there are too few.\nYou must manally delete them from the previous track";

    /**
     * the parent to update on completion
     */
    private final Layers _layers;
    private final Layer _parentTrack;
    private final Vector<TrackSegment> _infills;
    private final Editable[] _segments;

    int segCtr = 1;
    private final ErrorLogger _logger;
    private final boolean _canDelete;
    private HashMap<TrackSegment, ArrayList<FixWrapper>> _deletedFixes = null;

    /**
     * 
     * @param title
     *          What to call this operation
     * @param segments
     *          the list of segments to join
     * @param theLayers
     *          the parent layers object
     * @param parentTrack
     *          the parent track
     * @param logger
     *          error logger
     * @param canDelete
     *          whether we allow the alg to delete points
     */
    public GenerateInfillOperation(final String title,
        final Editable[] segments, final Layers theLayers,
        final Layer parentTrack, final ErrorLogger logger,
        final boolean canDelete)
    {
      super(title);
      _segments = segments;
      _layers = theLayers;
      _parentTrack = parentTrack;
      _infills = new Vector<TrackSegment>();
      _logger = logger;
      _canDelete = canDelete;
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
    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      IStatus res = null;

      // ok, loop through the segments
      for (int i = 0; i < _segments.length - 1; i++)
      {
        // get the juicy pair we're looking at
        final TrackSegment trackOne = (TrackSegment) _segments[i];
        final TrackSegment trackTwo = (TrackSegment) _segments[i + 1];

        // check they're not infills
        if (trackOne instanceof DynamicInfillSegment
            || trackTwo instanceof DynamicInfillSegment)
        {
          // hold on - there's already an infill here. Move along
          continue;
        }

        // join them
        res = fillSegments(trackOne, trackTwo);

        // did it work?
        if (res != null)
        {
          // ok, see what the problem was
          if (_canDelete && INSUFFICIENT_TIME.equals(res.getMessage()))
          {
            while (res != null && INSUFFICIENT_TIME.equals(res.getMessage()))
            {
              // hmm, check the second track has enough points
              if (trackTwo.size() <= 2)
              {
                // ok, drop out. we can't delete any more

                // can we restore deleted fixes?
                if (_deletedFixes != null)
                {
                  ArrayList<FixWrapper> thesefixes =
                      _deletedFixes.get(trackTwo);
                  if (thesefixes != null)
                  {
                    for (FixWrapper t : thesefixes)
                    {
                      trackTwo.add(t);
                    }

                    // ok, remove this from the undo operation
                    _deletedFixes.remove(thesefixes);
                  }
                }

                // ok, sort out failure state
                res =
                    new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME,
                        CANT_DELETE, null);

                // output the message
                _logger.logError(res.getSeverity(), res.getMessage(), null);

              }
              else
              {
                // ok, delete a point from the second leg
                FixWrapper firstPoint =
                    (FixWrapper) trackTwo.getData().iterator().next();

                if (_deletedFixes == null)
                {
                  _deletedFixes =
                      new HashMap<TrackSegment, ArrayList<FixWrapper>>();
                }

                // remember this item
                ArrayList<FixWrapper> fixes = _deletedFixes.get(trackTwo);

                if (fixes == null)
                {
                  fixes = new ArrayList<FixWrapper>();
                  _deletedFixes.put(trackTwo, fixes);
                }

                fixes.add(firstPoint);

                trackTwo.removeElement(firstPoint);

                res = fillSegments(trackOne, trackTwo);
              }
            }
          }
          else
          {
            // output the message
            _logger.logError(res.getSeverity(), res.getMessage(), null);

            // stop processing further infills
            break;
          }
        }
      }

      _layers.fireExtended(null, _parentTrack);

      if (res == null)
      {
        res =
            new Status(IStatus.OK, DebriefPlugin.PLUGIN_NAME,
                "generate infill successful", null);
      }
      return res;
    }

    /**
     * create a joining infill segment for these two sections
     * 
     * @param trackOne
     * @param trackTwo
     * @return null for ok, status message for fail
     */
    private IStatus fillSegments(final TrackSegment trackOne,
        final TrackSegment trackTwo)
    {
      IStatus res = null;
      // now do the more detailed checks
      if (trackOne.endDTG().greaterThan(trackTwo.startDTG()))
      {
        // fail, they overlap
        res =
            new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME, OVERLAPPING,
                null);
      }
      else
      {
        // cool, go for it
        // generate the new track segment
        final TrackSegment newSeg =
            new DynamicInfillSegment(trackOne, trackTwo);

        // aah, but, no but, are there points in the segment
        if (!newSeg.getData().isEmpty())
        {
          storeSegment(newSeg);
        }
        else
        {
          res =
              new Status(IStatus.ERROR, DebriefPlugin.PLUGIN_NAME,
                  INSUFFICIENT_TIME, null);

        }
      }

      return res;
    }

    private void storeSegment(final TrackSegment newSeg)
    {
      // correct the name
      newSeg.setName(newSeg.getName() + "_" + segCtr++);

      // add the track segment to the parent track
      _parentTrack.add(newSeg);

      // and remember it
      _infills.add(newSeg);
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      final Iterator<TrackSegment> iter = _infills.iterator();
      while (iter.hasNext())
      {
        final TrackSegment thisSeg = iter.next();

        // right, just delete our new track segments
        _parentTrack.removeElement(thisSeg);
      }

      // and clera the infills
      _infills.clear();

      // and reinstore deleted fixes
      if (_deletedFixes != null)
      {
        for (TrackSegment t : _deletedFixes.keySet())
        {
          ArrayList<FixWrapper> fixes = _deletedFixes.get(t);
          for (FixWrapper f : fixes)
          {
            t.add(f);
          }
        }
      }

      // cool, tell everyone
      _layers.fireExtended(null, _parentTrack);

      // register success
      return new Status(IStatus.OK, DebriefPlugin.PLUGIN_NAME,
          "ditch infill successful", null);
    }
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
    // we're only going to work with two or more items
    if (subjects.length > 1)
    {
      // track the parents
      final Layer firstParent = parentLayers[0];

      // is it a track?
      if (firstParent instanceof TrackWrapper)
      {
        final TrackWrapper parentTrack = (TrackWrapper) firstParent;

        // do they have the same parent layer?
        if (parentLayers[1] == parentLayers[0])
        {
          // check what's selected, see if they are suitable
          boolean canDo = true;
          for (int i = 0; i < subjects.length; i++)
          {
            final Editable editable = subjects[i];
            if (!(editable instanceof TrackSegment))
            {
              canDo = false;
            }
          }

          // ok, is it worth going for?
          if (canDo)
          {
            String title;

            // see if there are more than one segment to be generated
            if (subjects.length > 2)
            {
              title = "Generate infill segments";
            }
            else
            {
              title = "Generate infill segment";
            }

            final String finalTitle = title;

            final ErrorLogger logger = getLogger();

            // create this operation
            final Action doMerge = new Action(title)
            {
              @Override
              public void run()
              {
                final IUndoableOperation theAction =
                    new GenerateInfillOperation(finalTitle, subjects,
                        theLayers, parentTrack, logger, false);

                CorePlugin.run(theAction);
              }
            };
            doMerge.setImageDescriptor(CorePlugin
                .getImageDescriptor("icons\\16\\generate_infill.png"));
            final String secondTitle = finalTitle + DELETE_SUFFIX;
            final Action doMergeAllowDelete = new Action(secondTitle)
            {
              @Override
              public void run()
              {
                final IUndoableOperation theAction =
                    new GenerateInfillOperation(secondTitle, subjects,
                        theLayers, parentTrack, logger, true);

                CorePlugin.run(theAction);
              }
            };
            doMergeAllowDelete.setImageDescriptor(CorePlugin
                .getImageDescriptor("icons\\16\\generate_infill_delete.png"));
            parent.add(new Separator());
            parent.add(doMerge);
            parent.add(doMergeAllowDelete);
          }
        }
      }
    }

  }

  /**
   * provide an error logger
   * 
   * @return
   */
  protected ErrorLogger getLogger()
  {
    return new ErrorLogger()
    {

      @Override
      public void logError(final int status, final String text,
          final Exception e)
      {
        CorePlugin.showMessage("Generate infill", text);
      }

      @Override
      public void logError(final int status, final String text,
          final Exception e, final boolean revealLog)
      {
        logError(status, text, e);
      }

      @Override
      public void logStack(final int status, final String text)
      {
        logError(status, text, null);
      }
    };
  }
}
