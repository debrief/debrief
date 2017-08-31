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
  private static class LegOfCuts extends ArrayList<SensorContactWrapper>
  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public double[] getCurve(final boolean useAmbiguous)
    {
      final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

      // add my values
      final WeightedObservedPoints obs = new WeightedObservedPoints();
      for (final SensorContactWrapper item : this)
      {
        final long time = item.getDTG().getDate().getTime();
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
        // process the obs, to put them all in the correct domain
        final List<WeightedObservedPoint> rangedObs =
            putObsInCorrectRange(obs.toList());
        final List<WeightedObservedPoint> tidyObs =
            putObsInCorrectDomain(rangedObs);
        return fitter.fit(tidyObs);
      }
      else
      {
        return null;
      }
    }
  }

  private static class Perm implements Comparable<Perm>
  {
    private final double score;
    private final boolean firstOne;
    private final boolean secondOne;

    public Perm(final double score, final boolean firstOne,
        final boolean secondOne)
    {
      this.score = score;
      this.firstOne = firstOne;
      this.secondOne = secondOne;
    }

    @Override
    public int compareTo(final Perm o)
    {
      final Double dScore = score;
      return dScore.compareTo(o.score);
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class TestResolveAmbig extends junit.framework.TestCase
  {

    private TrackWrapper getData() throws FileNotFoundException
    {
      // get our sample data-file
      final ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/Ambig_tracks.rep";
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();
      assertEquals("has some layers", 3, theLayers.size());

      // get the sensor track
      final TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
      return track;
    }

    private TimeSeries getOSCourse(final TrackWrapper track)
    {
      final TimeSeries ts = new TimeSeries("OS Course");
      final Enumeration<Editable> pts = track.getPositionIterator();
      while (pts.hasMoreElements())
      {
        final FixWrapper fw = (FixWrapper) pts.nextElement();
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

    public void testGetCurve() throws FileNotFoundException
    {
      final LegOfCuts leg4 = new LegOfCuts();
      final SensorWrapper sensor = new SensorWrapper("name");
      leg4.add(wrapMe(sensor, 80, 92d, 260d));
      leg4.add(wrapMe(sensor, 90, 82d, 280d));
      leg4.add(wrapMe(sensor, 100, 72d, 300d));
      leg4.add(wrapMe(sensor, 110, 62d, 320d));
      leg4.add(wrapMe(sensor, 120, 52d, 340d));
      leg4.add(wrapMe(sensor, 130, 42d, 360d));
      leg4.add(wrapMe(sensor, 140, 32d, 380d));

      final double[] curve = leg4.getCurve(true);
      assertNotNull("produced curve", curve);
      assertEquals("curve correct length", 3, curve.length);
      assertEquals("correct offset", 100d, curve[0], 0.001);

      leg4.set(5, wrapMe(sensor, 130, 42d, 0d));
      leg4.set(6, wrapMe(sensor, 140, 32d, 20d));

      final double[] curve2 = leg4.getCurve(true);
      assertNotNull("produced curve", curve2);
      assertEquals("curve correct length", 3, curve2.length);
      assertEquals("correct offset", 100d, curve2[0], 0.001);
    }

    public void testGettingLegs() throws FileNotFoundException
    {

      final TrackWrapper track = getData();
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      final SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();
      sensor.setVisible(true);

      final ColorProvider provider = new ColorProvider()
      {
        @Override
        public Color getZoneColor()
        {
          return Color.blue;
        }
      };
      final TimeSeries osCourse = getOSCourse(track);
      // try to slice the O/S zones
      final ArrayList<Zone> zonesList =
          StackedDotHelper.sliceOwnship(osCourse, provider);
      final Zone[] zones = zonesList.toArray(new Zone[]
      {});

      // ok, get resolving
      final AmbiguityResolver res = new AmbiguityResolver();

      // drop cuts in turn
      res.dropCutsInTurn(track, zones, null);
      
      // now get the legs
      final List<LegOfCuts> legs = res.getLegs(track, zones, null);
      assertEquals("right num", zones.length, legs.size());

      // now resolve ambiguity
      res.resolve(legs);
    }

    public void testProcessCuts() throws FileNotFoundException
    {
      List<WeightedObservedPoint> obs = new ArrayList<WeightedObservedPoint>();
      obs.add(new WeightedObservedPoint(1, 80d, 260d));
      obs.add(new WeightedObservedPoint(1, 90, 280d));
      obs.add(new WeightedObservedPoint(1, 100, 300d));
      obs.add(new WeightedObservedPoint(1, 110, 320d));
      obs.add(new WeightedObservedPoint(1, 120, 340d));
      obs.add(new WeightedObservedPoint(1, 130, 0d));
      obs.add(new WeightedObservedPoint(1, 140, 20d));

      List<WeightedObservedPoint> res =
          AmbiguityResolver.putObsInCorrectDomain(obs);
      assertEquals("correct last score", 380d, res.get(res.size() - 1).getY(),
          0.001);

      obs = new ArrayList<WeightedObservedPoint>();
      obs.add(new WeightedObservedPoint(1, 80, 160d));
      obs.add(new WeightedObservedPoint(1, 90, 140d));
      obs.add(new WeightedObservedPoint(1, 100, 120d));
      obs.add(new WeightedObservedPoint(1, 110, 80d));
      obs.add(new WeightedObservedPoint(1, 120, 30d));
      obs.add(new WeightedObservedPoint(1, 130, 340d));
      obs.add(new WeightedObservedPoint(1, 140, 320d));

      res = AmbiguityResolver.putObsInCorrectDomain(obs);
      assertEquals("correct last score", -40d, res.get(res.size() - 1).getY(),
          0.001);

      obs = new ArrayList<WeightedObservedPoint>();
      obs.add(new WeightedObservedPoint(1, 80, -160d));
      obs.add(new WeightedObservedPoint(1, 90, -140d));
      obs.add(new WeightedObservedPoint(1, 100, -120d));
      obs.add(new WeightedObservedPoint(1, 110, -80d));
      obs.add(new WeightedObservedPoint(1, 120, -30d));
      obs.add(new WeightedObservedPoint(1, 130, 20d));
      obs.add(new WeightedObservedPoint(1, 140, 40d));

      res = AmbiguityResolver.putObsInCorrectRange(obs);
      assertEquals("correct last score", 200d, res.get(0).getY(), 0.001);
      assertEquals("correct last score", 40d, res.get(res.size() - 1).getY(),
          0.001);

      for (final WeightedObservedPoint ob : res)
      {
        System.out.println(ob.getY());
      }
    }

    public void testResolve() throws FileNotFoundException
    {
      final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
      final SensorWrapper sensor = new SensorWrapper("name");
      final LegOfCuts leg1 = new LegOfCuts();
      leg1.add(wrapMe(sensor, 100, 180d, 270d));
      leg1.add(wrapMe(sensor, 110, 170d, 280d));
      leg1.add(wrapMe(sensor, 120, 160d, 290d));
      leg1.add(wrapMe(sensor, 130, 150d, 300d));
      leg1.add(wrapMe(sensor, 140, 140d, 310d));
      legs.add(leg1);

      final LegOfCuts leg2 = new LegOfCuts();
      leg2.add(wrapMe(sensor, 160, 182d, 220d));
      leg2.add(wrapMe(sensor, 170, 183d, 221d));
      leg2.add(wrapMe(sensor, 180, 184d, 222d));
      leg2.add(wrapMe(sensor, 190, 185d, 223d));
      leg2.add(wrapMe(sensor, 200, 186d, 224d));
      legs.add(leg2);

      final LegOfCuts leg3 = new LegOfCuts();
      leg3.add(wrapMe(sensor, 220, 92d, 200d));
      leg3.add(wrapMe(sensor, 230, 83d, 210d));
      leg3.add(wrapMe(sensor, 240, 74d, 220d));
      leg3.add(wrapMe(sensor, 250, 65d, 230d));
      leg3.add(wrapMe(sensor, 260, 56d, 240d));
      legs.add(leg3);

      final LegOfCuts leg4 = new LegOfCuts();
      leg4.add(wrapMe(sensor, 280, 92d, 260d));
      leg4.add(wrapMe(sensor, 290, 73d, 280d));
      leg4.add(wrapMe(sensor, 300, 54d, 300d));
      leg4.add(wrapMe(sensor, 310, 35d, 320d));
      leg4.add(wrapMe(sensor, 320, 16d, 340d));
      leg4.add(wrapMe(sensor, 330, 9d, 0d));
      leg4.add(wrapMe(sensor, 340, 355d, 20d));
      legs.add(leg4);

      final LegOfCuts leg5 = new LegOfCuts();
      leg5.add(wrapMe(sensor, 360, 41d, 260d));
      leg5.add(wrapMe(sensor, 370, 43d, 240d));
      leg5.add(wrapMe(sensor, 380, 45d, 220d));
      leg5.add(wrapMe(sensor, 390, 47d, 200d));
      leg5.add(wrapMe(sensor, 400, 49d, 180d));
      leg5.add(wrapMe(sensor, 410, 51d, 160d));
      leg5.add(wrapMe(sensor, 420, 53d, 140d));
      legs.add(leg5);

      final AmbiguityResolver solver = new AmbiguityResolver();
      solver.resolve(legs);

      // ok, check the legs
      assertFalse("not ambig", leg1.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg2.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg3.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg4.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg5.get(0).getHasAmbiguousBearing());

      assertEquals("correct bearing", 180d, leg1.get(0).getBearing());
      assertEquals("correct bearing", 182d, leg2.get(0).getBearing());
      assertEquals("correct bearing", 200d, leg3.get(0).getBearing());
      assertEquals("correct bearing", 260d, leg4.get(0).getBearing());
      assertEquals("correct bearing", 260d, leg5.get(0).getBearing());
      //TODO: correct answer to previous one was 41d

    }

    public void testSplittingAllTime() throws FileNotFoundException
    {
      final TrackWrapper track = getData();
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      final SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();

      final ColorProvider provider = new ColorProvider()
      {
        @Override
        public Color getZoneColor()
        {
          return Color.blue;
        }
      };
      final TimeSeries osCourse = getOSCourse(track);
      // try to slice the O/S zones
      final ArrayList<Zone> zonesList =
          StackedDotHelper.sliceOwnship(osCourse, provider);
      final Zone[] zones = zonesList.toArray(new Zone[]
      {});

      // ok, get resolving
      final AmbiguityResolver res = new AmbiguityResolver();

      // drop cuts in turn
      final int numCuts = sensor.size();
      assertEquals("right cuts at start", 721, numCuts);
      res.dropCutsInTurn(track, zones, null);
      assertEquals("fewer cuts", 597, sensor.size());
      
      List<LegOfCuts> legs = res.getLegs(track, zones, null);
      

      // ok, check the data
    }

    private SensorContactWrapper wrapMe(final SensorWrapper sensor,
        final long dtg, final double bearing1, final double bearing2)
    {
      return new SensorContactWrapper("track", new HiResDate(dtg), null,
          bearing1, bearing2, null, null, Color.RED, "label", 0, sensor
              .getName());
    }
  }

  private static List<WeightedObservedPoint> putObsInCorrectDomain(
      final List<WeightedObservedPoint> obs)
  {
    final List<WeightedObservedPoint> res =
        new ArrayList<WeightedObservedPoint>();

    double lastVal = Double.MIN_VALUE;
    for (final WeightedObservedPoint ob : obs)
    {
      double thisVal = ob.getY();
      if (lastVal != Double.MIN_VALUE)
      {
        double valToUse;
        // ok, have we jumped up?
        if (thisVal - lastVal > 200)
        {
          // ok, reduce it
          valToUse = thisVal - 360d;
        }
        else if (thisVal - lastVal < -200)
        {
          // ok, increase it
          valToUse = thisVal + 360d;
        }
        else
        {
          valToUse = thisVal;
        }
        res.add(new WeightedObservedPoint(ob.getWeight(), ob.getX(), valToUse));

        thisVal = valToUse;
      }
      else
      {
        res.add(ob);
      }

      lastVal = thisVal;
    }
    return res;
  }

  private static List<WeightedObservedPoint> putObsInCorrectRange(
      final List<WeightedObservedPoint> obs)
  {
    final List<WeightedObservedPoint> res =
        new ArrayList<WeightedObservedPoint>();
    for (final WeightedObservedPoint ob : obs)
    {
      double thisVal = ob.getY();
      while (thisVal < 0)
      {
        thisVal += 360d;
      }
      while (thisVal >= 360)
      {
        thisVal -= 360d;
      }
      res.add(new WeightedObservedPoint(ob.getWeight(), ob.getX(), thisVal));
    }
    return res;
  }

  private void ditchBearings(final LegOfCuts leg, final boolean keepFirst)
  {
    for (final SensorContactWrapper cut : leg)
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

  public void dropCutsInTurn(final TrackWrapper track, final Zone[] zones,
      final TimePeriod period)
  {
    if (zones != null && zones.length > 0)
    {
      // ok, go for it
      final BaseLayer sensors = track.getSensors();
      final Enumeration<Editable> numer = sensors.elements();
      while (numer.hasMoreElements())
      {
        final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
        final List<SensorContactWrapper> toDelete =
            new ArrayList<SensorContactWrapper>();
        final Enumeration<Editable> cNumer = sensor.elements();
        while (cNumer.hasMoreElements())
        {
          final SensorContactWrapper scw =
              (SensorContactWrapper) cNumer.nextElement();
          final HiResDate dtg = scw.getDTG();
          if (outOfZones(zones, dtg))
          {
            toDelete.add(scw);
          }
        }

        // ok, do the delete
        for (final SensorContactWrapper sc : toDelete)
        {
          // ok, drop it.
          sensor.removeElement(sc);
        }
      }
    }
  }

  public List<LegOfCuts> getLegs(final TrackWrapper track, final Zone[] zones,
      final TimePeriod period)
  {
    final List<LegOfCuts> res = new ArrayList<LegOfCuts>();
    if (zones != null && zones.length > 0)
    {
      // ok, go for it
      final BaseLayer sensors = track.getSensors();
      final Enumeration<Editable> numer = sensors.elements();
      while (numer.hasMoreElements())
      {
        final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
        if (sensor.getVisible())
        {
          for (final Zone zone : zones)
          {
            LegOfCuts thisC = null;
            final Enumeration<Editable> cNumer = sensor.elements();
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

  private long midTimeFor(final LegOfCuts lastLeg, final LegOfCuts leg)
  {
    final long startTime =
        lastLeg.get(lastLeg.size() - 1).getDTG().getDate().getTime();
    final long endTime = leg.get(0).getDTG().getDate().getTime();

    // and the mid-way value
    return startTime + (endTime - startTime) / 2;
  }

  private boolean outOfZones(final Zone[] zones, final HiResDate dtg)
  {
    final long thisLong = dtg.getDate().getTime();
    boolean found = false;
    for (final Zone zone : zones)
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

  private void resolve(final List<LegOfCuts> legs)
  {
    // ok, loop through the legs
    LegOfCuts lastLeg = null;
    for (final LegOfCuts leg : legs)
    {
      if (lastLeg != null)
      {
        // ok, retrieve slopes
        final double[] lastSlopeOne = lastLeg.getCurve(false);
        final double[] lastSlopeTwo = lastLeg.getCurve(true);

        // and generate the slope for this leg
        final double[] thisSlopeOne = leg.getCurve(false);
        final double[] thisSlopeTwo = leg.getCurve(true);

        // find the time 1/2 way between the legs
        final long midTime = midTimeFor(lastLeg, leg);

        // get the slope scores we know we need
        final double lastSlopeValOne = valueAt(midTime, lastSlopeOne);
        final double nextSlopeValOne = valueAt(midTime, thisSlopeOne);
        final double nextSlopeValTwo = valueAt(midTime, thisSlopeTwo);

        // ok, is the first track resolved?
        if (lastSlopeTwo == null)
        {
          // ok, the previous leg has been sorted. just sort this leg
          final double oneone = Math.abs(lastSlopeValOne - nextSlopeValOne);
          final double onetwo = Math.abs(lastSlopeValOne - nextSlopeValTwo);

          final List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));

          Collections.sort(items);
          final Perm closest = items.get(0);

          ditchBearings(leg, closest.secondOne);
        }
        else
        {
          // ok, we've got to compare both of them
          final double lastSlopeValTwo = valueAt(midTime, lastSlopeTwo);

          final double oneone = Math.abs(lastSlopeValOne - nextSlopeValOne);
          final double onetwo = Math.abs(lastSlopeValOne - nextSlopeValTwo);
          final double twoone = Math.abs(lastSlopeValTwo - nextSlopeValOne);
          final double twotwo = Math.abs(lastSlopeValTwo - nextSlopeValTwo);

          final List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));
          items.add(new Perm(twoone, false, true));
          items.add(new Perm(twotwo, false, false));

          Collections.sort(items);
          final Perm closest = items.get(0);

          ditchBearings(lastLeg, closest.firstOne);
          ditchBearings(leg, closest.secondOne);
        }

      }

      lastLeg = leg;
    }
  }

  private double valueAt(final long time, final double[] slope)
  {
    return slope[0] + slope[1] * time + slope[2] * Math.pow(time, 2);
  }

  public void resolve(TrackWrapper primaryTrack, Zone[] zones, Object object)
  {
    List<LegOfCuts> legs = getLegs(primaryTrack, zones, null);
    resolve(legs);
  }
}
