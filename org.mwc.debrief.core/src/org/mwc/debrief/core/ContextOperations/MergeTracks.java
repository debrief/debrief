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

import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.CoreTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;

/**
 * @author ian.mayo
 */
public class MergeTracks implements RightClickContextItemGenerator
{

  /**
   * @param parent
   * @param theLayers
   * @param parentLayers
   * @param subjects
   */
  public void generate(final IMenuManager parent, final Layers theLayers,
      final Layer[] parentLayers, final Editable[] subjects)
  {
    int validItems = 0;
    String targetTrackName = null;

    // we're only going to work with two or more items
    if (subjects.length >= 1)
    {
      // are they tracks, or track segments
      for (int i = 0; i < subjects.length; i++)
      {
        boolean goForIt = false;
        final Editable thisE = subjects[i];
        if (thisE instanceof TrackWrapper)
        {
          goForIt = true;
          validItems++;
        }
        else if (thisE instanceof TrackSegment)
        {
          goForIt = true;
          validItems++;
        }
        else if (thisE instanceof SegmentList)
        {
          goForIt = true;

          SegmentList sl = (SegmentList) thisE;
          targetTrackName = sl.getWrapper().getName() + " (Merged)";
          validItems += sl.size();
        }

        if (goForIt)
        {
        }
        else
        {
          // may as well drop out - this item wasn't compliant
          continue;
        }
      }
    }

    // ok, is it worth going for?
    if (validItems >= 2)
    {
      // right,stick in a separator
      parent.add(new Separator());

      // get the first item
      final Editable editable = subjects[0];

      // just check it isn't a segment list
      if (!(editable instanceof SegmentList))
      {

        // ok, and let us do it in place
        final Action doMergeInPlace =
            new Action("Merge track segments - in place")
            {
              public void run()
              {
                final IUndoableOperation theAction =
                    new MergeTracksInPlaceOperation("Merge tracks in place",
                        editable, theLayers, parentLayers, subjects);

                CorePlugin.run(theAction);
              }
            };
        parent.add(doMergeInPlace);
      }

      if (targetTrackName == null)
      {
        targetTrackName = editable.getName() + " (Merged)";
      }

      // ok, check the name is safe
      final String safeTargetTrackName =
          theLayers.createUniqueLayerName(targetTrackName);

      final Color newTrackColor = DebriefColors.MAGENTA;
      final Color infillColor = DebriefColors.ORANGE;
      
      // see if we can get color prefs
      
      // ok, we need a title for the action
      final String titleSingle =
          "Merge track segments - into new track (single shade): "
              + safeTargetTrackName;

      // create this operation
      final Action doMergeSingleColor = new Action(titleSingle)
      {
        public void run()
        {
          final IUndoableOperation theAction =
              new MergeTracksOperation(titleSingle, null, safeTargetTrackName,
                  theLayers, parentLayers, subjects, newTrackColor, null);

          CorePlugin.run(theAction);
        }
      };
      parent.add(doMergeSingleColor);

      final String titleMulti =
          "Merge track segments - into new track (highlight infills): "
              + safeTargetTrackName;
      final Action doMergeMultiColor = new Action(titleMulti)
      {
        public void run()
        {
          final IUndoableOperation theAction =
              new MergeTracksOperation(titleMulti, null, safeTargetTrackName,
                  theLayers, parentLayers, subjects, newTrackColor, infillColor);

          CorePlugin.run(theAction);
        }
      };
      parent.add(doMergeMultiColor);

    }
    else
    {
      // aah, see if this a single-segment leg
      if (subjects.length == 1)
      {
        final Editable item = subjects[0];

        final CoreTMASegment seg;
        String segmentName = null;

        // is it a track?
        if (item instanceof TrackWrapper)
        {
          final TrackWrapper tw = (TrackWrapper) item;
          final SegmentList segs = tw.getSegments();
          if (segs.size() == 1)
          {
            final TrackSegment thisSeg = (TrackSegment) segs.first();

            // is it a TMA segment?
            seg =
                thisSeg instanceof CoreTMASegment ? (CoreTMASegment) thisSeg
                    : null;

            // only one segment, so use the track name
            segmentName = tw.getName();
          }
          else
          {
            seg = null;
          }
        }
        else if (item instanceof CoreTMASegment)
        {
          seg = (CoreTMASegment) item;
          segmentName = item.getName();
        }
        else
        {
          seg = null;
        }

        if (seg != null)
        {
          // right,stick in a separator
          parent.add(new Separator());

          final String title =
              "Convert " + segmentName + " into standalone track";
          final CoreTMASegment target = seg;
          // create this operation
          final Action doMerge = new Action(title)
          {
            public void run()
            {
              final IUndoableOperation theAction =
                  new ConvertTrackOperation(title, target, theLayers);

              CorePlugin.run(theAction);
            }
          };
          parent.add(doMerge);

        }

      }

    }
  }

  private static class MergeTracksOperation extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    protected final Layers _layers;
    protected final Layer[] _parents;
    protected final Editable[] _subjects;
    protected Editable _target;
    private final String _trackName;
    private final Color _infillShade;
    private final Color _newTrackColor;

    public MergeTracksOperation(final String title, final Editable target,
        final String trackName, final Layers theLayers,
        final Layer[] parentLayers, final Editable[] subjects, final Color newTrackColor,
        final Color singleShade)
    {
      super(title);
      _target = target;
      _trackName = trackName;
      _layers = theLayers;
      _parents = parentLayers;
      _subjects = subjects;
      _infillShade = singleShade;
      _newTrackColor = newTrackColor;
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      if (_target == null)
      {
        TrackWrapper target = new TrackWrapper();
        target.setName(_trackName);
        
        // set default color, if we have one
        if(_newTrackColor != null)
        {
          target.setColor(_newTrackColor);
        }

        _target = target;
      }

      final int res =
          TrackWrapper.mergeTracks((TrackWrapper) _target, _layers, _subjects,
              _infillShade);

      // ok, we can also hide the parent

      if (res == IStatus.OK)
      {
        // it worked, so switch off the composite track
        TrackWrapper oldParent = (TrackWrapper) _parents[0];
        oldParent.setVisible(false);

        // and talk about the UI update
        fireModified();
      }

      return Status.OK_STATUS;
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

    private void fireModified()
    {
      _layers.fireExtended();
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      CorePlugin.logError(Status.INFO,
          "Undo not permitted for merge operation", null);
      return null;
    }
  }

  private static class MergeTracksInPlaceOperation extends MergeTracksOperation
  {
    public MergeTracksInPlaceOperation(final String title,
        final Editable target, final Layers theLayers,
        final Layer[] parentLayers, final Editable[] subjects)
    {
      super(title, target, null, theLayers, parentLayers, subjects, null, null);
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {

      final int res =
          TrackWrapper
              .mergeTracksInPlace(_target, _layers, _parents, _subjects);

      // ok, we can also hide the parent

      if (res == IStatus.OK)
      {
        // and talk about the UI update
        fireModified();
      }

      return Status.OK_STATUS;
    }

    private void fireModified()
    {
      _layers.fireExtended(null, _parents[0]);
    }

  }

  private static class ConvertTrackOperation extends CMAPOperation
  {

    /**
     * the parent to update on completion
     */
    private final Layers _layers;
    private final CoreTMASegment _target;

    public ConvertTrackOperation(final String title,
        final CoreTMASegment segment, final Layers theLayers)
    {
      super(title);
      _target = segment;
      _layers = theLayers;
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {
      // create a non-TMA track
      final TrackSegment newSegment = new TrackSegment(_target);

      // now do some fancy footwork to remove the target from the wrapper,
      // and
      // replace it with our new segment
      newSegment.getWrapper().removeElement(_target);
      newSegment.getWrapper().add(newSegment);
      fireModified();

      return Status.OK_STATUS;
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

    private void fireModified()
    {
      _layers.fireExtended();
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      CorePlugin.logError(Status.INFO,
          "Undo not permitted for merge operation", null);
      return null;
    }
  }
}
