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

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackWrapper_Support;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

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

    public SplitTracksOperation(final String title, final Layers theLayers,
        final List<TrackWrapper> tracks, final Long period)
    {
      super(title);
      _layers = theLayers;
      _tracks = tracks;
      _period = period;
    }

    @Override
    public boolean canRedo()
    {
      return false;
    }

    @Override
    public boolean canUndo()
    {
      return false;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      boolean modified = false;

      // loop through the tracks
      for (final TrackWrapper track : _tracks)
      {
        final boolean didSplit = TrackWrapper_Support.splitTrackAtJumps(track,
            _period);
        modified = modified || didSplit;
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
      CorePlugin.logError(IStatus.INFO,
          "Undo not permitted for merge operation", null);
      return null;
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

      final String fullMsg = "Split " + msg + " into segment on gaps over...";

      final MenuManager listing = new MenuManager(fullMsg);

      final HashMap<Long, String> choices = new HashMap<Long, String>();
      choices.put(60 * 1000L, "1 Minute");
      choices.put(60 * 60 * 1000L, "1 Hour");
      choices.put(24 * 60 * 60 * 1000L, "1 Day");
      choices.put(7 * 24 * 60 * 60 * 1000L, "1 Week");

      for (final Long period : choices.keySet())
      {
        // get the time period
        String label = choices.get(period);

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
