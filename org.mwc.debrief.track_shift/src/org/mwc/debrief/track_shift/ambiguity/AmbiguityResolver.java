package org.mwc.debrief.track_shift.ambiguity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichBearing;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichPeriod;
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

  public static class ResolvedLeg
  {
    final boolean keepFirst;
    final LegOfCuts leg;

    public ResolvedLeg(final LegOfCuts leg, final boolean keepFirst)
    {
      this.leg = leg;
      this.keepFirst = keepFirst;
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

      final double[] curve =
          leg4.getCurve(WhichPeriod.ALL, WhichBearing.AMBIGUOUS);
      assertNotNull("produced curve", curve);
      assertEquals("curve correct length", 3, curve.length);
      assertEquals("correct offset", 100d, curve[0], 0.001);

      leg4.set(5, wrapMe(sensor, 130, 42d, 0d));
      leg4.set(6, wrapMe(sensor, 140, 32d, 20d));

      final double[] curve2Ambig =
          leg4.getCurve(WhichPeriod.ALL, WhichBearing.AMBIGUOUS);
      final double[] curve2 = leg4.getCurve(WhichPeriod.ALL, WhichBearing.CORE);
      assertNotNull("produced curve", curve2Ambig);
      assertEquals("curve correct length", 3, curve2Ambig.length);
      assertEquals("correct offset", 100d, curve2Ambig[0], 0.001);

      double beforeValue = valueAt(60, curve2Ambig);
      assertEquals("correct next value", beforeValue, 220d, 0.001);

      double afterValue = valueAt(150, curve2Ambig);
      assertEquals("correct next value", afterValue, 400d, 0.001);

      beforeValue = valueAt(60, curve2);
      assertEquals("correct next value", beforeValue, 112d, 0.001);

      afterValue = valueAt(150, curve2);
      assertEquals("correct next value", afterValue, 22d, 0.001);
    }

    public void testGetCurveSector() throws FileNotFoundException
    {
      final LegOfCuts leg4 = new LegOfCuts();
      final SensorWrapper sensor = new SensorWrapper("name");
      leg4.add(wrapMe(sensor, 80, 92d, 260d));
      leg4.add(wrapMe(sensor, 90, 82d, 280d));
      leg4.add(wrapMe(sensor, 100, 72d, 300d));
      leg4.add(wrapMe(sensor, 110, 62d, 320d));
      leg4.add(wrapMe(sensor, 120, 52d, 340d));

      // start off with it too short
      List<SensorContactWrapper> leg = leg4.extractPortion(WhichPeriod.EARLY);
      assertNotNull("leg retrieved", leg);
      assertEquals("correct length", 5, leg.size());

      // ok, add more data, so that we need to trim the data
      leg4.add(wrapMe(sensor, 130, 42d, 0d));
      leg4.add(wrapMe(sensor, 140, 32d, 20d));
      leg4.add(wrapMe(sensor, 150, 22d, 40d));
      leg4.add(wrapMe(sensor, 160, 12d, 60d));
      leg4.add(wrapMe(sensor, 170, 2d, 80d));
      leg4.add(wrapMe(sensor, 180, 12d, 60d));
      leg4.add(wrapMe(sensor, 190, 22d, 40d));
      leg4.add(wrapMe(sensor, 200, 32d, 20d));
      leg4.add(wrapMe(sensor, 210, 42d, 0d));
      leg4.add(wrapMe(sensor, 220, 52d, 340d));
      leg4.add(wrapMe(sensor, 230, 62d, 320d));
      leg4.add(wrapMe(sensor, 240, 72d, 300d));

      // check we retrieve the expected data
      leg = leg4.extractPortion(WhichPeriod.ALL);
      assertNotNull("leg retrieved", leg);
      assertEquals("correct length", 17, leg.size());
      assertEquals("correct start", 80, leg.get(0).getDTG().getDate().getTime());
      assertEquals("correct end", 240, leg.get(leg.size() - 1).getDTG()
          .getDate().getTime());

      leg = leg4.extractPortion(WhichPeriod.EARLY);
      assertNotNull("leg retrieved", leg);
      assertEquals("correct length", 8, LegOfCuts.LEG_LENGTH);
      assertEquals("correct start", 80, leg.get(0).getDTG().getDate().getTime());
      assertEquals("correct end", 150, leg.get(leg.size() - 1).getDTG()
          .getDate().getTime());

      leg = leg4.extractPortion(WhichPeriod.LATE);
      assertNotNull("leg retrieved", leg);
      assertEquals("correct length", 8, LegOfCuts.LEG_LENGTH);
      assertEquals("correct start", 170, leg.get(0).getDTG().getDate()
          .getTime());
      assertEquals("correct end", 240, leg.get(leg.size() - 1).getDTG()
          .getDate().getTime());

      // try the calculated values
      double[] curve = leg4.getCurve(WhichPeriod.ALL, WhichBearing.CORE);
      assertEquals("correct value (since we can't fit good curve)", 102.40,
          valueAt(80, curve), 0.01);
      assertEquals("correct value (since we can't fit good curve)", 74.17,
          valueAt(240, curve), 0.01);

      curve = leg4.getCurve(WhichPeriod.EARLY, WhichBearing.CORE);
      assertEquals("correct value", 92, valueAt(80, curve), 0.01);

      curve = leg4.getCurve(WhichPeriod.EARLY, WhichBearing.AMBIGUOUS);
      assertEquals("correct value", 260, valueAt(80, curve), 0.01);

      curve = leg4.getCurve(WhichPeriod.LATE, WhichBearing.CORE);
      assertEquals("correct value", 72, valueAt(240, curve), 0.01);

      curve = leg4.getCurve(WhichPeriod.LATE, WhichBearing.AMBIGUOUS);
      assertEquals("correct value", -60, valueAt(240, curve), 0.01);
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
      res.findCutsToDropInTurn(track, zones, null);

      // now get the legs
      final List<LegOfCuts> legs = res.sliceIntoLegs(track, zones);
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

      // put the good cut in the wrong domain
      final LegOfCuts leg5 = new LegOfCuts();
      leg5.add(wrapMe(sensor, 360, 41d, 260d));
      leg5.add(wrapMe(sensor, 370, 43d, 240d));
      leg5.add(wrapMe(sensor, 380, 45d, 220d));
      leg5.add(wrapMe(sensor, 390, 47d, 200d));
      leg5.add(wrapMe(sensor, 400, 49d, 180d));
      leg5.add(wrapMe(sensor, 410, 51d, 160d));
      leg5.add(wrapMe(sensor, 420, 53d, 140d));
      legs.add(leg5);

      // make the first cuts very wonky
      final LegOfCuts leg6 = new LegOfCuts();
      leg6.add(wrapMe(sensor, 440, 141d, 350d));
      leg6.add(wrapMe(sensor, 450, 143d, 20d));
      leg6.add(wrapMe(sensor, 460, 145d, 70d));
      leg6.add(wrapMe(sensor, 470, 147d, 80d));
      leg6.add(wrapMe(sensor, 480, 149d, 90d));
      leg6.add(wrapMe(sensor, 490, 151d, 100d));
      leg6.add(wrapMe(sensor, 500, 153d, 110d));
      legs.add(leg6);

      final AmbiguityResolver solver = new AmbiguityResolver();
      final List<ResolvedLeg> resolvedLegs = solver.resolve(legs);

      assertNotNull("have list of resolved", resolvedLegs);
      assertEquals("correct num legs", 6, resolvedLegs.size());

      // ok, check the legs
      assertFalse("not ambig", leg1.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg2.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg3.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg4.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg5.get(0).getHasAmbiguousBearing());
      assertFalse("not ambig", leg6.get(0).getHasAmbiguousBearing());

      assertEquals("correct bearing", 180d, leg1.get(0).getBearing());
      assertEquals("correct bearing", 182d, leg2.get(0).getBearing());
      assertEquals("correct bearing", 200d, leg3.get(0).getBearing());
      assertEquals("correct bearing", 260d, leg4.get(0).getBearing());
      assertEquals("correct bearing", 41d, leg5.get(0).getBearing());
      assertEquals("correct bearing", 350d, leg6.get(0).getBearing());

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
      final List<SensorContactWrapper> toDel =
          res.findCutsToDropInTurn(track, zones, null);
      assertEquals("have cuts to delete", 133, toDel.size());

      @SuppressWarnings("unused")
      final List<LegOfCuts> legs = res.sliceIntoLegs(track, zones);

      // ok, check the data
    }

    public void testWeighting()
    {
      final SensorWrapper sensor = new SensorWrapper("name");
      final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();

      final LegOfCuts leg1 = new LegOfCuts();
      leg1.add(wrapMe(sensor, 280, 92d, 260d));
      leg1.add(wrapMe(sensor, 290, 73d, 280d));
      leg1.add(wrapMe(sensor, 300, 54d, 300d));
      leg1.add(wrapMe(sensor, 310, 35d, 320d));
      leg1.add(wrapMe(sensor, 320, 16d, 340d));
      leg1.add(wrapMe(sensor, 330, 9d, 0d));
      leg1.add(wrapMe(sensor, 340, 355d, 20d));
      legs.add(leg1);

      // put the good cut in the wrong domain
      final LegOfCuts leg2 = new LegOfCuts();
      leg2.add(wrapMe(sensor, 360, 42d, 260d));
      leg2.add(wrapMe(sensor, 370, 43d, 240d));
      leg2.add(wrapMe(sensor, 380, 45d, 220d));
      leg2.add(wrapMe(sensor, 390, 47d, 200d));
      leg2.add(wrapMe(sensor, 400, 49d, 180d));
      leg2.add(wrapMe(sensor, 410, 51d, 160d));
      leg2.add(wrapMe(sensor, 420, 53d, 140d));
      legs.add(leg2);

      // make the first cuts very wonky
      final LegOfCuts leg3 = new LegOfCuts();
      leg3.add(wrapMe(sensor, 440, 141d, 350d));
      leg3.add(wrapMe(sensor, 450, 143d, 20d));
      leg3.add(wrapMe(sensor, 460, 145d, 70d));
      leg3.add(wrapMe(sensor, 470, 147d, 80d));
      leg3.add(wrapMe(sensor, 480, 149d, 90d));
      leg3.add(wrapMe(sensor, 490, 151d, 100d));
      leg3.add(wrapMe(sensor, 500, 153d, 110d));
      legs.add(leg3);

      final AmbiguityResolver resolver = new AmbiguityResolver();
      final List<ResolvedLeg> resolvedLegs = resolver.resolve(legs);

      assertNotNull("have legs", resolvedLegs);
      assertEquals("correct bearing", 260d, leg1.get(0).getBearing());
      assertEquals("correct bearing", 42d, leg2.get(0).getBearing());
      assertEquals("correct bearing", 350d, leg3.get(0).getBearing());
    }

    private SensorContactWrapper wrapMe(final SensorWrapper sensor,
        final long dtg, final double bearing1, final double bearing2)
    {
      return new SensorContactWrapper("track", new HiResDate(dtg), null,
          bearing1, bearing2, null, null, Color.RED, "label", 0, sensor
              .getName());
    }
  }

  static List<WeightedObservedPoint> putObsInCorrectDomain(
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

  static List<WeightedObservedPoint> putObsInCorrectRange(
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

  private static double trim(final double val)
  {
    double res = val;
    while (res < -360d)
    {
      res += 360d;
    }
    while (res >= 360d)
    {
      res -= 360d;
    }
    return res;
  }

  private static double valueAt(final long time, final double[] slope)
  {
    return slope[0] + slope[1] * time + slope[2] * Math.pow(time, 2);
  }

  private double calcDelta(final double one, final double two)
  {
    double res = Math.abs(one - two);
    while (res > 360d)
    {
      res -= 360d;
    }
    while (res <= -360d)
    {
      res += 360d;
    }
    return res;
  }

  public void ditchBearings(final List<ResolvedLeg> legs)
  {
    for (final ResolvedLeg leg : legs)
    {
      ditchBearingsForThisLeg(leg.leg, leg.keepFirst);
    }
  }

  private void ditchBearingsForThisLeg(final LegOfCuts leg,
      final boolean keepFirst)
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

  public List<SensorContactWrapper> findCutsToDropInTurn(
      final TrackWrapper track, final Zone[] zones, final TimePeriod period)
  {
    final List<SensorContactWrapper> toDelete =
        new ArrayList<SensorContactWrapper>();
    if (zones != null && zones.length > 0)
    {
      // ok, go for it
      final BaseLayer sensors = track.getSensors();
      final Enumeration<Editable> numer = sensors.elements();
      while (numer.hasMoreElements())
      {
        final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
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
      }
    }
    return toDelete;
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

  @SuppressWarnings("unused")
  private void outputCurve(final String title, final long midTime,
      final LegOfCuts leg, final double[] slopeOne, final double[] slopeTwo)
  {
    System.out.println(title);
    final long firstTime = leg.get(0).getDTG().getDate().getTime();
    final boolean firstLeg = firstTime < midTime;
    final boolean twoLegs = slopeTwo != null;

    if (!firstLeg)
    {
      // ok, output the mid-point
      final double legTwo = twoLegs ? valueAt(midTime, slopeTwo) : Double.NaN;

      System.out.println(midTime + ", " + trim(valueAt(midTime, slopeOne))
          + ", " + trim(legTwo));
    }

    // now loop through
    for (final SensorContactWrapper cut : leg)
    {
      final long thisTime = cut.getDTG().getDate().getTime();
      double legTwo = twoLegs ? valueAt(thisTime, slopeTwo) : Double.NaN;
      if (legTwo > 360d)
      {
        legTwo -= 360d;
      }

      System.out.println(thisTime + ", " + trim(valueAt(thisTime, slopeOne))
          + ", " + trim(legTwo));
    }

    if (firstLeg)
    {
      // ok, output the mid-point
      final double legTwo = twoLegs ? valueAt(midTime, slopeTwo) : Double.NaN;
      System.out.println(midTime + ", " + trim(valueAt(midTime, slopeOne))
          + ", " + trim(legTwo));
    }

  }

  @SuppressWarnings("unused")
  private void outputLeg(final String title, final LegOfCuts lastLeg)
  {
    System.out.println(title);
    for (final SensorContactWrapper cut : lastLeg)
    {
      System.out.println(cut.getDTG().getDate().getTime() + ", "
          + cut.getBearing() + ", " + cut.getAmbiguousBearing());
    }
  }

  private List<ResolvedLeg> resolve(final List<LegOfCuts> legs)
  {
    final List<ResolvedLeg> res = new ArrayList<ResolvedLeg>();

    // ok, loop through the legs
    LegOfCuts lastLeg = null;
    for (final LegOfCuts leg : legs)
    {
      if (lastLeg != null)
      {
        // find the time 1/2 way between the legs
        final long midTime = midTimeFor(lastLeg, leg);

        // ok, retrieve slopes
        final double[] lastSlopeOne =
            lastLeg.getCurve(WhichPeriod.LATE, WhichBearing.CORE);
        final double[] lastSlopeTwo =
            lastLeg.getCurve(WhichPeriod.LATE, WhichBearing.AMBIGUOUS);

        // and generate the slope for this leg
        final double[] thisSlopeOne =
            leg.getCurve(WhichPeriod.EARLY, WhichBearing.CORE);
        final double[] thisSlopeTwo =
            leg.getCurve(WhichPeriod.EARLY, WhichBearing.AMBIGUOUS);

        // hmm, see if this has already been resolved
        if (thisSlopeTwo == null)
        {
          continue;
        }

        // get the slope scores we know we need
        final double lastSlopeValOne = trim(valueAt(midTime, lastSlopeOne));
        final double nextSlopeValOne = trim(valueAt(midTime, thisSlopeOne));
        final double nextSlopeValTwo = trim(valueAt(midTime, thisSlopeTwo));

        // ok, is the first track resolved?
        if (lastSlopeTwo == null)
        {
          // ok, the previous leg has been sorted. just sort this leg
          final double oneone = calcDelta(lastSlopeValOne, nextSlopeValOne);
          final double onetwo = calcDelta(lastSlopeValOne, nextSlopeValTwo);

          final List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));

          Collections.sort(items);

          // check that the two solutions aren't too similar. If they are,
          // then it would be better to move onto the next leg.
          final Perm closest = items.get(0);
          final Perm nextClosest = items.get(1);
          final double firstTwoDiff =
              Math.abs(nextClosest.score - closest.score);
          final double cutOff = 10d;
          if (firstTwoDiff > cutOff)
          {
            ditchBearingsForThisLeg(leg, closest.secondOne);
            res.add(new ResolvedLeg(leg, closest.secondOne));
          }
        }
        else
        {
          // ok, we've got to compare both of them
          final double lastSlopeValTwo = trim(valueAt(midTime, lastSlopeTwo));

          // find the difference in the legs
          final double oneone = calcDelta(lastSlopeValOne, nextSlopeValOne);
          final double onetwo = calcDelta(lastSlopeValOne, nextSlopeValTwo);
          final double twoone = calcDelta(lastSlopeValTwo, nextSlopeValOne);
          final double twotwo = calcDelta(lastSlopeValTwo, nextSlopeValTwo);

          // store the permutations
          final List<Perm> items = new ArrayList<>();
          items.add(new Perm(oneone, true, true));
          items.add(new Perm(onetwo, true, false));
          items.add(new Perm(twoone, false, true));
          items.add(new Perm(twotwo, false, false));

          // sort the permutations, so we can easily get the best
          Collections.sort(items);
          final Perm closest = items.get(0);

          // ditch the unnecessary bearing
          ditchBearingsForThisLeg(lastLeg, closest.firstOne);
          ditchBearingsForThisLeg(leg, closest.secondOne);

          // remember what we've done.
          res.add(new ResolvedLeg(lastLeg, closest.firstOne));
          res.add(new ResolvedLeg(leg, closest.secondOne));
        }
      }
      lastLeg = leg;
    }
    return res;
  }

  public List<ResolvedLeg> resolve(final TrackWrapper primaryTrack,
      final Zone[] zones)
  {
    final List<LegOfCuts> legs = sliceIntoLegs(primaryTrack, zones);
    return resolve(legs);
  }

  private List<LegOfCuts> sliceIntoLegs(final TrackWrapper track,
      final Zone[] zones)
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

  public void undoDitchBearings(final List<ResolvedLeg> legs)
  {
    for (final ResolvedLeg leg : legs)
    {
      for (final SensorContactWrapper cut : leg.leg)
      {
        // cool, we have a course - we can go for it. remember the bearings
        final double bearing1 = cut.getBearing();
        final double bearing2 = cut.getAmbiguousBearing();

        if (leg.keepFirst)
        {
          cut.setBearing(bearing2);
          cut.setAmbiguousBearing(bearing1);
        }
        else
        {
          cut.setBearing(bearing1);
          cut.setAmbiguousBearing(bearing2);
        }

        // remember we're morally ambiguous
        cut.setHasAmbiguousBearing(true);
      }
    }
  }
}
