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
import java.util.Date;
import java.util.Enumeration;
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
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.LightweightTrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DebriefColors;
import MWC.GUI.Properties.LineStylePropertyEditor;
import MWC.GUI.Shapes.LineShape;
import MWC.GUI.Shapes.PlainShape;
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

      Editable[] selection = new Editable[4];
      
      for (int i = 0; i < 4; i++)
      {
        final LightweightTrackWrapper light = new LightweightTrackWrapper("track:" + i, true, true, Color.RED, LineStylePropertyEditor.DOTTED);
        Fix theFix = new Fix(new HiResDate(i), new WorldLocation(1,i,0),12d, 12d);
        light.addFix(new FixWrapper(theFix));
        holder.add(light);
        selection[i] = light;
      }

      
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

      // check the track got generated
      final TrackWrapper tw = (TrackWrapper) theLayers.findLayer("track:2");

      // did we find it?
      assertNotNull("track generated", tw);

      // check we've got the right number of fixes
      assertEquals("right num of fixes generated", tw.numFixes(), 1);

    }

    public testMe(final String val)
    {
      super(val);
    }
  }

  public static TrackWrapper generateTrackFor(final BaseLayer layer)
  {
    TrackWrapper res = new TrackWrapper();
    res.setName("T_" + layer.getName());

    Color trackColor = null;

    // ok, step through the points
    final Enumeration<Editable> numer = layer.elements();

    // remember the last line viewed, since we want to add both of it's points
    ShapeWrapper lastLine = null;

    while (numer.hasMoreElements())
    {
      final Plottable pl = (Plottable) numer.nextElement();
      if (pl instanceof LabelWrapper)
      {
        final LabelWrapper label = (LabelWrapper) pl;

        // just check we know the track color
        if (trackColor == null)
          trackColor = label.getColor();

        HiResDate dtg = label.getStartDTG();
        if (dtg == null)
          dtg = new HiResDate(new Date());

        final WorldLocation loc = label.getBounds().getCentre();
        final Fix newFix = new Fix(dtg, loc, 0, 0);
        final FixWrapper fw = new FixWrapper(newFix);

        if (label.getColor() != trackColor)
          fw.setColor(label.getColor());

        res.add(fw);
        fw.setTrackWrapper(res);

        // forget the last-line, clearly we've moved on to other things
        lastLine = null;

      }
      else if (pl instanceof ShapeWrapper)
      {
        final ShapeWrapper sw = (ShapeWrapper) pl;
        final PlainShape shape = sw.getShape();
        if (shape instanceof LineShape)
        {
          final LineShape line = (LineShape) shape;
          // just check we know the track color
          if (trackColor == null)
            trackColor = line.getColor();

          final HiResDate dtg = sw.getStartDTG();
          final WorldLocation loc = line.getLine_Start();
          final Fix newFix = new Fix(dtg, loc, 0, 0);
          final FixWrapper fw = new FixWrapper(newFix);

          if (line.getColor() != trackColor)
            fw.setColor(line.getColor());
          fw.setTrackWrapper(res);
          res.add(fw);

          // and remember this line
          lastLine = sw;

        }
      }
    }

    // did we have a trailing line item?
    if (lastLine != null)
    {
      final HiResDate dtg = lastLine.getEndDTG();
      final LineShape line = (LineShape) lastLine.getShape();
      final WorldLocation loc = line.getLineEnd();
      final Fix newFix = new Fix(dtg, loc, 0, 0);
      final FixWrapper fw = new FixWrapper(newFix);
      fw.setTrackWrapper(res);
      res.add(fw);
    }

    // update the track color
    res.setColor(trackColor);

    // did we find any?
    if (res.numFixes() == 0)
      res = null;

    return res;
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
