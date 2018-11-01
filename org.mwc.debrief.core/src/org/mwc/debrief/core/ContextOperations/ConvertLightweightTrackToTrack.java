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

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldLocation;
import MWC.TacticalData.Fix;

/**
 * @author ian.mayo
 *
 */
public class ConvertLightweightTrackToTrack implements
    RightClickContextItemGenerator
{

  private static class ConvertIt extends CMAPOperation
  {

    private final Layers _layers;
    private final Editable[] _subjects;

    private Vector<TrackWrapper> _newTracks;
    private Vector<LightweightTrackWrapper> _oldLightweights;

    public ConvertIt(final String title, final Layers layers,
        final Editable[] subjects)
    {
      super(title);
      _layers = layers;
      _subjects = subjects;
    }

    @Override
    public IStatus execute(final IProgressMonitor monitor,
        final IAdaptable info) throws ExecutionException
    {
      _newTracks = new Vector<TrackWrapper>();
      _oldLightweights = new Vector<LightweightTrackWrapper>();

      // we don't want to fire updates as each track gets updated
      _layers.suspendFiringExtended(true);

      try
      {
        // right, get going through the track
        for (int i = 0; i < _subjects.length; i++)
        {
          final Editable thisE = _subjects[i];
          if (thisE instanceof LightweightTrackWrapper
              && !(thisE instanceof TrackWrapper))
          {
            final LightweightTrackWrapper oldLightweight =
                (LightweightTrackWrapper) thisE;

            // switch off the layer
            oldLightweight.setVisible(false);

            final TrackWrapper newTrack = new TrackWrapper();

            _newTracks.add(newTrack);
            _oldLightweights.add(oldLightweight);
            _layers.addThisLayer(newTrack);

            newTrack.setName(oldLightweight.getName());
            final Color hisColor = oldLightweight.getCustomColor();
            if (hisColor != null)
            {
              newTrack.setColor(hisColor);
            }
            else
            {
              newTrack.setColor(DebriefColors.GOLD);
            }

            final Iterator<FixWrapper> numer = oldLightweight.iterator();
            while (numer.hasNext())
            {
              final FixWrapper fix = numer.next();
              newTrack.add(fix);
            }
          }
        }
      }
      finally
      {
        // allow updates to be fired
        _layers.suspendFiringExtended(false);

        // sorted, force an update
        _layers.fireExtended();
      }

      return Status.OK_STATUS;
    }

    @Override
    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // forget about the new tracks
      for (final Iterator<TrackWrapper> iter = _newTracks.iterator(); iter
          .hasNext();)
      {
        final TrackWrapper trk = iter.next();
        _layers.removeThisLayer(trk);
      }

      for (final LightweightTrackWrapper t : _oldLightweights)
      {
        t.setVisible(true);
      }

      // and clear the new tracks item
      _newTracks.removeAllElements();
      _newTracks = null;

      _oldLightweights.removeAllElements();
      _oldLightweights = null;

      return Status.OK_STATUS;
    }

  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public final static void testIWork()
    {
      final Layers theLayers = new Layers();
      final BaseLayer holder = new BaseLayer();
      holder.setName("Trk");
      theLayers.addThisLayer(holder);

      final int NUM_TRACKS = 4;
      final int NUM_POSITIONS = 5;
      Editable[] selection = new Editable[NUM_TRACKS];
      
      LightweightTrackWrapper firstTrack = null;
      
      for (int j = 0; j < NUM_TRACKS; j++)
      {
        final LightweightTrackWrapper light = new LightweightTrackWrapper("track:" + j, true, true, Color.RED, LineStylePropertyEditor.DOTTED);
        
        for (int i = 0; i < NUM_POSITIONS; i++)
        {
          Fix theFix = new Fix(new HiResDate(i * 1000), new WorldLocation(1,i,0),12d, 12d);
          light.addFix(new FixWrapper(theFix));
        }
        
        holder.add(light);
        selection[j] = light;
        
        if(firstTrack == null)
        {
          firstTrack = light;
        }
        
      }

      assertEquals("have single layer before operation",  1, theLayers.size());

      // ok, now do the interpolation
      final ConvertIt ct = new ConvertIt("convert it", theLayers, selection);

      try
      {
        ct.execute(null, null);
      }
      catch (final ExecutionException e)
      {
        fail("Exception thrown");
      }
      
      assertEquals("have new layers",  5, theLayers.size());

      // check the track got generated
      final TrackWrapper tw = (TrackWrapper) theLayers.findLayer("track:0");

      // did we find it?
      assertNotNull("track generated", tw);

      // check we've got the right number of fixes
      
      assertEquals("correct name", "track:0", tw.getName());
      assertEquals("correct size", firstTrack.numFixes(), tw.numFixes());
      assertEquals("correct size", NUM_POSITIONS, tw.numFixes());
      assertEquals("correct color", firstTrack.getColor(), tw.getColor());
      assertEquals("correct name", firstTrack.getName(), tw.getName());
      

    }

    public testMe(final String val)
    {
      super(val);
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
    int layersValidForConvertToTrack = 0;

    // right, work through the subjects
    for (int i = 0; i < subjects.length; i++)
    {
      final Editable thisE = subjects[i];
      if (thisE instanceof LightweightTrackWrapper
          && !(thisE instanceof TrackWrapper))
      {
        // ok, we've started...
        layersValidForConvertToTrack++;
      }
      else
      {
        return;
      }
    }

    // ok, is it worth going for?
    if (layersValidForConvertToTrack > 0)
    {
      final String title;
      if (layersValidForConvertToTrack > 1)
        title = "Convert lightweight tracks to tracks";
      else
        title = "Convert lightweight track to track";

      // yes, create the action
      final Action convertToTrack = new Action(title)
      {
        @Override
        public void run()
        {
          // ok, go for it.
          // sort it out as an operation
          final IUndoableOperation convertToTrack1 = new ConvertIt(title,
              theLayers, subjects);

          // ok, stick it on the buffer
          runIt(convertToTrack1);
        }
      };

      // right,stick in a separator
      parent.add(new Separator());

      // ok - flash up the menu item
      parent.add(convertToTrack);
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
