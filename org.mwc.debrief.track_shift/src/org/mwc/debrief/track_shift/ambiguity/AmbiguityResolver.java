package org.mwc.debrief.track_shift.ambiguity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.track_shift.controls.ZoneChart.ColorProvider;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.views.StackedDotHelper;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.JFreeChart.ColouredDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;

public class AmbiguityResolver
{
  public void resolve(TrackWrapper track, Zone[] zones, TimePeriod period)
  {
    BaseLayer sensors = track.getSensors();

    // find the O/S legs

    // ok, loop through the sensors
    Enumeration<Editable> numer = sensors.elements();
    while (numer.hasMoreElements())
    {
      SensorWrapper sensor = (SensorWrapper) numer.nextElement();
      processSensor(sensor);
    }
  }

  public List<LegOfCuts> getLegs(TrackWrapper track, Zone[] zones,
      TimePeriod period)
  {
    List<LegOfCuts> res = new ArrayList<LegOfCuts>();
    if (zones != null && zones.length > 0)
    {
      // ok, go for it
      BaseLayer sensors = track.getSensors();
      Enumeration<Editable> numer = sensors.elements();
      while (numer.hasMoreElements())
      {
        SensorWrapper sensor = (SensorWrapper) numer.nextElement();
        if (sensor.getVisible())
        {
          for (Zone zone : zones)
          {
            LegOfCuts thisC = null;
            Enumeration<Editable> cNumer = sensor.elements();
            while (cNumer.hasMoreElements())
            {
              final SensorContactWrapper scw =
                  (SensorContactWrapper) cNumer.nextElement();
              final long dtg = scw.getDTG().getDate().getTime();
              if (zone.getStart() <= dtg && zone.getEnd() >= dtg)
              {
                // ok, this cut is in this zone
                if (thisC == null)
                {
                  thisC = new LegOfCuts();
                }
                thisC.add(scw);
              }
              else if (zone.getEnd() < dtg)
              {
                // ok, we've passed the end of this zone
                continue;
              }
            }

            if (thisC != null)
            {
              res.add(thisC);
            }
          }
        }
      }
    }
    return res;
  }

  public void
      dropCutsInTurn(TrackWrapper track, Zone[] zones, TimePeriod period)
  {
    if (zones != null && zones.length > 0)
    {
      // ok, go for it
      BaseLayer sensors = track.getSensors();
      Enumeration<Editable> numer = sensors.elements();
      while (numer.hasMoreElements())
      {
        SensorWrapper sensor = (SensorWrapper) numer.nextElement();
        final List<SensorContactWrapper> toDelete =
            new ArrayList<SensorContactWrapper>();
        Enumeration<Editable> cNumer = sensor.elements();
        while (cNumer.hasMoreElements())
        {
          SensorContactWrapper scw =
              (SensorContactWrapper) cNumer.nextElement();
          final HiResDate dtg = scw.getDTG();
          if (outOfZones(zones, dtg))
          {
            toDelete.add(scw);
          }
        }

        // ok, do the delete
        for (SensorContactWrapper sc : toDelete)
        {
          // ok, drop it.
          sensor.removeElement(sc);
        }
      }
    }
  }

  private boolean outOfZones(Zone[] zones, HiResDate dtg)
  {
    final long thisLong = dtg.getDate().getTime();
    boolean found = false;
    for (Zone zone : zones)
    {
      if (zone.getStart() <= thisLong && zone.getEnd() >= thisLong)
      {
        // ok, valid.
        found = true;
        break;
      }
    }
    return !found;
  }

  private void processSensor(SensorWrapper sensor)
  {
    // work through this sensor
    @SuppressWarnings("unused")
    SensorWrapper tmpSensor = sensor;
  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class TestResolveAmbig extends junit.framework.TestCase
  {

    public void testResolve() throws FileNotFoundException
    {
      List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
      SensorWrapper sensor = new SensorWrapper("name");
      LegOfCuts leg1 = new LegOfCuts();
      leg1.add(wrapMe(sensor, 100, 180d, 270d));
      leg1.add(wrapMe(sensor, 110, 170d, 280d));
      leg1.add(wrapMe(sensor, 120, 160d, 290d));
      leg1.add(wrapMe(sensor, 130, 150d, 300d));
      leg1.add(wrapMe(sensor, 140, 140d, 310d));
      legs.add(leg1);

      LegOfCuts leg2 = new LegOfCuts();
      leg2.add(wrapMe(sensor, 160, 182d, 220d));
      leg2.add(wrapMe(sensor, 170, 183d, 221d));
      leg2.add(wrapMe(sensor, 180, 184d, 222d));
      leg2.add(wrapMe(sensor, 190, 185d, 223d));
      leg2.add(wrapMe(sensor, 200, 186d, 224d));
      legs.add(leg2);

      LegOfCuts leg3 = new LegOfCuts();
      leg3.add(wrapMe(sensor, 220, 92d, 200d));
      leg3.add(wrapMe(sensor, 230, 83d, 210d));
      leg3.add(wrapMe(sensor, 240, 74d, 220d));
      leg3.add(wrapMe(sensor, 250, 65d, 230d));
      leg3.add(wrapMe(sensor, 260, 56d, 240d));
      legs.add(leg3);

      AmbiguityResolver solver = new AmbiguityResolver();
      solver.resolve(legs);

      // ok, check the legs
      assertFalse("not ambig", leg1.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg2.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg3.get(0).getHasAmbiguousBearing());

      assertEquals("correct bearing", 180d, leg1.get(0).getBearing());
      assertEquals("correct bearing", 182d, leg2.get(0).getBearing());
      assertEquals("correct bearing", 200d, leg3.get(0).getBearing());

    }

    private SensorContactWrapper wrapMe(SensorWrapper sensor, long dtg,
        double bearing1, double bearing2)
    {
      return new SensorContactWrapper("track", new HiResDate(dtg), null,
          bearing1, bearing2, null, null, Color.RED, "label", 0, sensor
              .getName());
    }

    public void testGettingLegs() throws FileNotFoundException
    {

      TrackWrapper track = getData();
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();
      sensor.setVisible(true);

      ColorProvider provider = new ColorProvider()
      {
        @Override
        public Color getZoneColor()
        {
          return Color.blue;
        }
      };
      TimeSeries osCourse = getOSCourse(track);
      // try to slice the O/S zones
      ArrayList<Zone> zonesList =
          StackedDotHelper.sliceOwnship(osCourse, provider);
      Zone[] zones = zonesList.toArray(new Zone[]
      {});

      // ok, get resolving
      AmbiguityResolver res = new AmbiguityResolver();

      // drop cuts in turn
      List<LegOfCuts> legs = res.getLegs(track, zones, null);
      assertEquals("right num", zones.length, legs.size());

      // ok, now work through the legs
      res.resolve(legs);

    }

    public void testSplittingAllTime() throws FileNotFoundException
    {
      TrackWrapper track = getData();
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();

      ColorProvider provider = new ColorProvider()
      {
        @Override
        public Color getZoneColor()
        {
          return Color.blue;
        }
      };
      TimeSeries osCourse = getOSCourse(track);
      // try to slice the O/S zones
      ArrayList<Zone> zonesList =
          StackedDotHelper.sliceOwnship(osCourse, provider);
      Zone[] zones = zonesList.toArray(new Zone[]
      {});

      // ok, get resolving
      AmbiguityResolver res = new AmbiguityResolver();

      // drop cuts in turn
      int numCuts = sensor.size();
      assertEquals("right cuts at start", 721, numCuts);
      res.dropCutsInTurn(track, zones, null);
      assertEquals("fewer cuts", 597, sensor.size());

      res.resolve(track, zones, null);

      // ok, check the data
    }

    private TimeSeries getOSCourse(TrackWrapper track)
    {
      TimeSeries ts = new TimeSeries("OS Course");
      Enumeration<Editable> pts = track.getPositionIterator();
      while (pts.hasMoreElements())
      {
        FixWrapper fw = (FixWrapper) pts.nextElement();
        final double course = fw.getCourseDegs();

        final FixedMillisecond thisMilli =
            new FixedMillisecond(fw.getDateTimeGroup().getDate().getTime());
        final ColouredDataItem crseBearing =
            new ColouredDataItem(thisMilli, course, fw.getColor(), true, null,
                true, true);

        ts.add(crseBearing);
      }
      return ts;
    }

    private TrackWrapper getData() throws FileNotFoundException
    {
      // get our sample data-file
      ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/Ambig_tracks.rep";
      File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();
      assertEquals("has some layers", 3, theLayers.size());

      // get the sensor track
      TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
      return track;
    }
  }

  public void resolve(List<LegOfCuts> rawLegs)
  {
    // ok, loop through the legs

    // well, trim them first
    final List<LegOfCuts> legs = rawLegs;

    LegOfCuts lastLeg = null;

    for (final LegOfCuts leg : legs)
    {
      if (lastLeg != null)
      {
        // ok, retrieve slopes
        double[] lastSlopeOne = lastLeg.getCurve(false);
        double[] lastSlopeTwo = lastLeg.getCurve(true);

        // and generate the slope for this leg
        double[] thisSlopeOne = leg.getCurve(false);
        double[] thisSlopeTwo = leg.getCurve(true);

        // find the time 1/2 way between the legs
        final long midTime = midTimeFor(lastLeg, leg);

        // get the slope scores we know we need
        double lastSlopeValOne = valueAt(midTime, lastSlopeOne);
        double nextSlopeValOne = valueAt(midTime, thisSlopeOne);
        double nextSlopeValTwo = valueAt(midTime, thisSlopeTwo);

        // ok, is the first track resolved?
        if (lastSlopeTwo == null)
        {
          // ok, the previous leg has been sorted. just sort this leg
          double oneone = Math.abs(lastSlopeValOne - nextSlopeValOne);
          double onetwo = Math.abs(lastSlopeValOne - nextSlopeValTwo);

          List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));

          Collections.sort(items);
          Perm closest = items.get(0);

          ditchBearings(leg, closest.secondOne);
        }
        else
        {
          // ok, we've got to compare both of them
          double lastSlopeValTwo = valueAt(midTime, lastSlopeTwo);

          double oneone = Math.abs(lastSlopeValOne - nextSlopeValOne);
          double onetwo = Math.abs(lastSlopeValOne - nextSlopeValTwo);
          double twoone = Math.abs(lastSlopeValTwo - nextSlopeValOne);
          double twotwo = Math.abs(lastSlopeValTwo - nextSlopeValTwo);

          List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));
          items.add(new Perm(twoone, false, true));
          items.add(new Perm(twotwo, false, false));

          Collections.sort(items);
          Perm closest = items.get(0);

          ditchBearings(lastLeg, closest.firstOne);
          ditchBearings(leg, closest.secondOne);
        }

      }

      lastLeg = leg;
    }
  }

  private void ditchBearings(LegOfCuts leg, boolean keepFirst)
  {
    for (SensorContactWrapper cut : leg)
    {
      // cool, we have a course - we can go for it. remember the bearings
      final double bearing1 = cut.getBearing();
      final double bearing2 = cut.getAmbiguousBearing();

      if (keepFirst)
      {
        cut.setBearing(bearing1);
        cut.setAmbiguousBearing(bearing2);
      }
      else
      {
        cut.setBearing(bearing2);
        cut.setAmbiguousBearing(bearing1);
      }

      // remember we're morally ambiguous
      cut.setHasAmbiguousBearing(false);

    }
  }

  private static class Perm implements Comparable<Perm>
  {
    private final double score;
    private final boolean firstOne;
    private final boolean secondOne;

    public Perm(double score, boolean firstOne, boolean secondOne)
    {
      this.score = score;
      this.firstOne = firstOne;
      this.secondOne = secondOne;
    }

    @Override
    public int compareTo(Perm o)
    {
      Double dScore = score;
      return dScore.compareTo(o.score);
    }
  }

  private double valueAt(long time, double[] slope)
  {
    return slope[0] + slope[1] * time + slope[2] * Math.pow(time, 2);
  }

  private long midTimeFor(LegOfCuts lastLeg, LegOfCuts leg)
  {
    long startTime =
        lastLeg.get(lastLeg.size() - 1).getDTG().getDate().getTime();
    long endTime = leg.get(0).getDTG().getDate().getTime();

    // and the mid-way value
    return startTime + (endTime - startTime) / 2;
  }

  private static class LegOfCuts extends ArrayList<SensorContactWrapper>
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public double[] getCurve(boolean useAmbiguous)
    {
      PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

      // add my values
      final WeightedObservedPoints obs = new WeightedObservedPoints();
      for (SensorContactWrapper item : this)
      {
        long time = item.getDTG().getDate().getTime();
        final Double theBrg;
        if (useAmbiguous)
        {
          if (item.getHasAmbiguousBearing())
          {
            theBrg = item.getAmbiguousBearing();
          }
          else
          {
            theBrg = null;
          }
        }
        else
        {
          theBrg = item.getBearing();
          // System.out.println(time + ", " + theBrg + ", " + item.getAmbiguousBearing());
        }
        if (theBrg != null)
        {
          obs.add(time, theBrg);
        }
      }

      final List<WeightedObservedPoint> res = obs.toList();
      if (res.size() > 0)
      {
        return fitter.fit(obs.toList());
      }
      else
      {
        return null;
      }
    }
  }

}
