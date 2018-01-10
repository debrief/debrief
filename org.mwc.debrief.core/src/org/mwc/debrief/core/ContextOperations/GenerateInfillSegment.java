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

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.AbsoluteTMASegment;
import Debrief.Wrappers.Track.DynamicInfillSegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.ErrorLogger;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;

/**
 * @author ian.mayo
 */
public class GenerateInfillSegment implements RightClickContextItemGenerator
{

  public static class TestGenInfill extends TestCase
  {
    public void testOverlap()
    {
      final ArrayList<String> messages = new ArrayList<String>();
      GenerateInfillSegment gener = new GenerateInfillSegment(){

        @Override
        protected ErrorLogger getLogger()
        {
          return new ErrorLogger(){

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
            }};
        }};
      Layers theLayers = new Layers();
      
      IMenuManager parent = new MenuManager();
      TrackWrapper track = new TrackWrapper();
      Layer[] parentLayers = new Layer[]{track, track};
      WorldLocation origin = new WorldLocation(44, 44, 44);
      AbsoluteTMASegment legOne = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin, new HiResDate(100), new HiResDate(100000));
      AbsoluteTMASegment legTwo = new AbsoluteTMASegment(12, new WorldSpeed(13, WorldSpeed.Kts), origin, new HiResDate(90000), new HiResDate(200000));
      Editable[] subjects = new Editable[]{legOne, legTwo};
      gener.generate(parent, theLayers, parentLayers, subjects);
      IContributionItem[] newItems = parent.getItems();
      
      assertEquals("newItems", 2, newItems.length);
      
      ActionContributionItem first = (ActionContributionItem) newItems[1];
      assertEquals("right name", "Generate infill segment", first.getAction().getText());
      first.getAction().run();
      
      
      assertEquals("got error message", 1, messages.size());
    }
  }
  
  private static class GenerateInfillOperation extends CMAPOperation
  {

    public static final String INSUFFICIENT_TIME =
        "Insufficient time to insert data. Please try deleting some points.";
    public static final String OVERLAPPING =
        "Sorry, this operation cannot be performed for overlapping track sections\nPlease delete overlapping data points and try again";
    /**
     * the parent to update on completion
     */
    private final Layers _layers;
    private final Layer _parentTrack;
    private final Vector<TrackSegment> _infills;
    private final Editable[] _segments;

    int segCtr = 1;
    private final ErrorLogger _logger;

    public GenerateInfillOperation(final String title,
        final Editable[] segments, final Layers theLayers,
        final Layer parentTrack, final ErrorLogger logger)
    {
      super(title);
      _segments = segments;
      _layers = theLayers;
      _parentTrack = parentTrack;
      _infills = new Vector<TrackSegment>();
      _logger = logger;
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

        // join them
        res = fillSegments(trackOne, trackTwo);

        // did it work?
        if (res != null)
        {
          // output the message
          _logger.logError(res.getSeverity(), res.getMessage(), null);

          // stop processing further infills
          break;
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
                        theLayers, parentTrack, logger);

                CorePlugin.run(theAction);
              }
            };
            parent.add(new Separator());
            parent.add(doMerge);
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
