package org.mwc.debrief.core.ContextOperations.tests;

import java.awt.Color;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import org.eclipse.core.commands.ExecutionException;

import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.RelativeTMASegment;
import Debrief.Wrappers.Track.TrackSegment;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Tools.SubjectAction;
import MWC.GenericData.HiResDate;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistance.ArrayLength;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldSpeed;
import MWC.GenericData.WorldVector;
import MWC.TacticalData.Fix;
import MWC.Utilities.TextFormatting.FormatRNDateTime;

public final class TMASegment_Test extends junit.framework.TestCase
{
  static public final String TEST_ALL_TEST_TYPE = "UNIT";

  @SuppressWarnings("deprecation")
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
              .add(getVector(25, 0)), MWC.Algorithms.Conversions.Degs2Rads(0),
              110));
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
          new SensorContactWrapper("aaa", new HiResDate(thisTime), null, null,
              null, null, null, 0, null);
      swa.add(scwa1);
    }

    return tw;
  }

  public void testDecimate() throws ExecutionException
  {
    TrackWrapper tw = getLongerTrack();

    assertNotNull(tw);

    // get the sensor data
    SensorWrapper sw = (SensorWrapper) tw.getSensors().elements().nextElement();

    assertNotNull(sw);

    // create a list of cuts (to simulate the selection)
    SensorContactWrapper[] items = new SensorContactWrapper[sw.size()];
    Enumeration<Editable> numer = sw.elements();
    int ctr = 0;
    while (numer.hasMoreElements())
    {
      SensorContactWrapper cut = (SensorContactWrapper) numer.nextElement();
      items[ctr++] = cut;
    }

    Layers theLayers = new Layers();
    WorldVector worldOffset = new WorldVector(Math.PI, 0.002, 0);
    double tgtCourse = 0;
    WorldSpeed tgtSpeed = new WorldSpeed(3, WorldSpeed.Kts);
    
    
    // create it, then
    final TrackSegment seg =
        new RelativeTMASegment(items, worldOffset, tgtSpeed, tgtCourse, theLayers,
            Color.RED);

    // now wrap it
    TrackWrapper track = new TrackWrapper();
    track.setColor(Color.red);
    track.add(seg);
    final String tNow =
        TrackSegment.TMA_LEADER
            + FormatRNDateTime.toString(track.getStartDTG().getDate()
                .getTime());
    track.setName(tNow);

    // check track size
    Collection<Editable> pts = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
    assertEquals("has correct points", 17, pts.size());
    
    track.setResampleDataAt(new HiResDate(300000));

    pts = track.getItemsBetween(track.getStartDTG(), track.getEndDTG());
    assertEquals("has more points", 9, pts.size());

//    RelativeTMASegment seg =
//        (RelativeTMASegment) sol.getSegments().elements().nextElement();

    assertNotNull("new seg not found", seg);

    // ok, and we split it.
    int ctr2 = 0;
    FixWrapper beforeF = null;
    FixWrapper afterF = null;
    Enumeration<Editable> eF = seg.elements();
    while (eF.hasMoreElements())
    {
      FixWrapper fix = (FixWrapper) eF.nextElement();
      ctr2++;
      if (ctr2 > seg.size() / 2)
      {
        if (beforeF == null)
        {
          beforeF = fix;
        }
        else
        {
          afterF = fix;
          break;
        }
      }
    }

    assertNotNull("fix not found", beforeF);

    // ok, what's the time offset
    WorldLocation afterBeforeSplit = afterF.getLocation();

    // ok, time to split
    SubjectAction[] actions = beforeF.getInfo().getUndoableActions();
    SubjectAction doSplit = actions[1];
    doSplit.execute(beforeF);

    // ok, have another look
    assertEquals("now has two segments", 2, track.getSegments().size());
    Enumeration<Editable> aNum = track.getSegments().elements();
    aNum.nextElement();
    TrackSegment afterSeg = (TrackSegment) aNum.nextElement();
    WorldLocation locAfterSplit = afterSeg.getTrackStart();

    assertEquals("origin remains valid", afterBeforeSplit, locAfterSplit);

    // hey, try the undo
    doSplit.undo(beforeF);

    assertEquals("now has one segment again", 1, track.getSegments().size());

    // hey, try the undo
    doSplit.execute(beforeF);
    assertEquals("now has two segments", 2, track.getSegments().size());

    aNum = track.getSegments().elements();
    aNum.nextElement();
    afterSeg = (TrackSegment) aNum.nextElement();
    locAfterSplit = afterSeg.getTrackStart();
    assertEquals("origin remains valid, after undo/redo", afterBeforeSplit,
        locAfterSplit);

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
