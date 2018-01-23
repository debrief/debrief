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
import java.util.*;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.CMAPOperation;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.ContextOperations.GenerateInfillSegment.GenerateInfillOperation;

import Debrief.Wrappers.*;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.GUI.*;
import MWC.GUI.Shapes.*;
import MWC.GenericData.*;
import MWC.TacticalData.*;

/**
 * @author ian.mayo
 * 
 */
public class CopyBearingsToClipboard implements RightClickContextItemGenerator
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
    // ok, we only allow a single selection
    if (subjects.length == 1)
    {
      // ok, good start

      Editable first = subjects[0];
      if (first instanceof TrackWrapper)
      {
        final TrackWrapper track = (TrackWrapper) first;

        // ok, it's a track. Is it made from relative TMA segments?
        SegmentList segments = track.getSegments();
        Enumeration<Editable> ele = segments.elements();

        TrackWrapper host = null;

        while (ele.hasMoreElements())
        {
          TrackSegment seg = (TrackSegment) ele.nextElement();

          if (seg instanceof RelativeTMASegment)
          {
            RelativeTMASegment rel = (RelativeTMASegment) seg;
            WatchableList refTrack = rel.getReferenceTrack();
            if (refTrack == null)
            {
              // ok, show error
              CorePlugin.logError(Status.ERROR,
                  "Host track for TMA leg can't be determined", null);
              break;
            }
            else if (refTrack instanceof TrackWrapper)
            {
              host = (TrackWrapper) refTrack;
              break;
            }
            else
            {
              CorePlugin.logError(Status.ERROR,
                  "Host track for TMA leg isn't a TrackWrapper", null);
              break;
            }
          }
        }

        if (host != null)
        {
          // yes, create the action
          final String title =
              "Copy to clipboard as offsets from " + host.getName();
          final Action convertToTrack = new Action(title)
          {
            public void run()
            {
              // ok, go for it.
              // sort it out as an operation
              final IUndoableOperation convertToTrack1 =
                  new CopyBearingData(title, theLayers, track);

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

  private static class CopyBearingData extends CMAPOperation
  {

    private final Layers _layers;
    @SuppressWarnings("unused")
    private final TrackWrapper _subject;

    private Vector<TrackWrapper> _newTracks;

    public CopyBearingData(final String title, final Layers layers,
        final TrackWrapper subject)
    {
      super(title);
      _layers = layers;
      _subject = subject;
    }

    public IStatus
        execute(final IProgressMonitor monitor, final IAdaptable info)
            throws ExecutionException
    {

      return Status.OK_STATUS;
    }

    public IStatus undo(final IProgressMonitor monitor, final IAdaptable info)
        throws ExecutionException
    {
      // forget about the new tracks
      for (final Iterator<TrackWrapper> iter = _newTracks.iterator(); iter
          .hasNext();)
      {
        final TrackWrapper trk = (TrackWrapper) iter.next();
        _layers.removeThisLayer(trk);
      }

      // and clear the new tracks item
      _newTracks.removeAllElements();
      _newTracks = null;

      return Status.OK_STATUS;
    }

  }

  /**
   * find out if this item is suitable for use as a track item
   * 
   * @param thisP
   * @return
   */
  static boolean isSuitableAsTrackPoint(final Plottable thisP)
  {
    boolean res = false;

    // ok - is it a label? Converting that to a track point is quite easy
    if (thisP instanceof LabelWrapper)
    {
      res = true;
    }

    // next, see if it's a line, because the pretend track could have been
    // drawn up as a series of lines
    if (thisP instanceof ShapeWrapper)
    {
      final ShapeWrapper sw = (ShapeWrapper) thisP;
      final PlainShape shp = sw.getShape();
      if (shp instanceof LineShape)
        res = true;
    }
    return res;
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

    @SuppressWarnings("deprecation")
    public void testBearings() throws ExecutionException
    {
      Layers layers = new Layers();
      TrackWrapper host = new TrackWrapper();
      host.setName("Host");
      SensorWrapper sensor = new SensorWrapper("Sensor");
      host.add(sensor);
      layers.addThisLayer(host);

      for (int i = 0; i < 60; i++)
      {
        Date newDate = new Date(2018, 01, 01, 02, i * 2, 0);
        WorldLocation loc = new WorldLocation(12, 12 + i / 60, 0d);
        Fix newF = new Fix(new HiResDate(newDate.getTime()), loc, 0, 12);
        FixWrapper fix = new FixWrapper(newF);
        host.add(fix);

        Date newDate2 = new Date(2018, 01, 01, 02, i * 2, 10);

        // public SensorContactWrapper(final String trackName, final HiResDate dtg,
        // final WorldDistance range, final Double bearingDegs,
        // final WorldLocation origin, final java.awt.Color color,
        // final String label, final int style, final String sensorName)
        if (i % 9 != 0)
        {
          SensorContactWrapper contact =
              new SensorContactWrapper(host.getName(), new HiResDate(newDate2
                  .getTime()), null, 12d, null, Color.red, "Some label", 1,
                  "Sensor");
          sensor.add(contact);
        }
      }

      TrackWrapper subject = new TrackWrapper();
      subject.setName("subject");
      layers.addThisLayer(subject);

      SensorContactWrapper[] obs = new SensorContactWrapper[sensor.size()];
      Enumeration<Editable> ele = sensor.elements();
      int ctr = 0;
      while (ele.hasMoreElements())
      {
        SensorContactWrapper cut = (SensorContactWrapper) ele.nextElement();
        obs[ctr++] = cut;
      }

      WorldVector offset = new WorldVector(12, 0.002, 0.0d);
      RelativeTMASegment seg =
          new RelativeTMASegment(obs, offset,
              new WorldSpeed(12, WorldSpeed.Kts), 12d, layers, Color.green);
      subject.add(seg);

      assertEquals("have valid segment", 53, seg.size());

      FixWrapper split1 = (FixWrapper) seg.getData().toArray()[12];
      FixWrapper split2 = (FixWrapper) seg.getData().toArray()[21];

      subject.splitTrack(split1, true);

      assertEquals("have legs", 2, subject.getSegments().size());

      subject.splitTrack(split2, true);

      assertEquals("have legs", 3, subject.getSegments().size());

      Editable[] subjects = new Editable[3];
      int ctr2 = 0;
      ele = subject.getSegments().elements();
      while (ele.hasMoreElements())
      {
        subjects[ctr2++] = ele.nextElement();
      }

      GenerateInfillOperation operation =
          new GenerateInfillOperation("title", subjects, layers, subject,
              getMyLogger(), true);

      assertEquals("before infills", 3, subject.getSegments().size());

      operation.execute(null, null);

      assertEquals("has infills", 5, subject.getSegments().size());
      
      // ok, now we can run the get bearings
      
      // check bearings are on clipboard.   
      
      // also test the paste
    }

    private ErrorLogger getMyLogger()
    {
      return new ErrorLogger()
      {

        @Override
        public void logStack(int status, String text)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void logError(int status, String text, Exception e,
            boolean revealLog)
        {
          // TODO Auto-generated method stub

        }

        @Override
        public void logError(int status, String text, Exception e)
        {
          // TODO Auto-generated method stub

        }
      };
    }

  }
}
