package org.mwc.debrief.track_shift.ambiguity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichBearing;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichPeriod;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;

public class AmbiguityResolver
{
  public static class LegsAndZigs
  {
    private final List<LegOfCuts> legs;
    private final LegOfCuts zigCuts;

    public LegsAndZigs(final List<LegOfCuts> legs, final LegOfCuts zigCuts)
    {
      this.legs = legs;
      this.zigCuts = zigCuts;
    }

    public List<LegOfCuts> getLegs()
    {
      return legs;
    }

    public LegOfCuts getZigs()
    {
      return zigCuts;
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

  public static class ResolvedLeg
  {
    public final boolean keepFirst;
    public final LegOfCuts leg;

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

    private TrackWrapper getData(final String name)
        throws FileNotFoundException
    {
      // get our sample data-file
      final ImportReplay importer = new ImportReplay();
      final Layers theLayers = new Layers();
      final String fName =
          "../org.mwc.cmap.combined.feature/root_installs/sample_data/S2R/"
              + name;
      final File inFile = new File(fName);
      assertTrue("input file exists", inFile.exists());
      final FileInputStream is = new FileInputStream(fName);
      importer.importThis(fName, is, theLayers);

      // sort out the sensors
      importer.storePendingSensors();

      // get the sensor track
      final TrackWrapper track = (TrackWrapper) theLayers.findLayer("SENSOR");
      
      assertNotNull("found sensor track", track);

      return track;
    }

    public void testTrim()
    {
      assertEquals("trim as normal", 5d, trim(365, null));
      assertEquals("trim as normal", -5d, trim(-365, null));
      assertEquals("trim as normal", 65d, trim(65, null));
      assertEquals("trim as normal", -65d, trim(-65, null));
      assertEquals("trim as normal", -10d, trim(-370, null));

      // ok, give it a target value, and check we're in the correct domain
      assertEquals("trim as normal", 365d, trim(365, 340d));
      assertEquals("trim as normal", -300d, trim(-300, -340d));

      // ooh, what if we're looking for a high value,
      // but only receive a low value
      assertEquals("trim as normal", 365d, trim(5, 340d));
      assertEquals("trim as normal", -370d, trim(-10, -340d));
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
      assertEquals("correct value (since we can't fit good curve)", 119.20,
          valueAt(80, curve), 0.01);
      assertEquals("correct value (since we can't fit good curve)", 78.79,
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

    public void testOnlyDitchVisible() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks2.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensor", 1, track.getSensors().size());

      // make the sensor visible
      final SensorWrapper sensor =
          (SensorWrapper) track.getSensors().elements().nextElement();
      sensor.setVisible(true);

      // set some cuts to hidden
      int ctr = 0;
      final Enumeration<Editable> numer = sensor.elements();
      while (numer.hasMoreElements())
      {
        final SensorContactWrapper scw =
            (SensorContactWrapper) numer.nextElement();
        if (ctr > 20 && ctr < 50)
        {
          scw.setVisible(false);
        }

        ctr++;
      }

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, null, null);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 8, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 17, zigs.size());
    }
    
    public void testHandleWiggle() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks_hover_north.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensors", 2, track.getSensors().size());

      final Enumeration<Editable> sensorList = track.getSensors().elements();
      @SuppressWarnings("unused")
      final SensorWrapper hmSensor =
          (SensorWrapper) sensorList.nextElement();
      // make the sensor visible
      
      final SensorWrapper sensor =
          (SensorWrapper) sensorList.nextElement();
      sensor.setVisible(true);
      
      // check we have the correct sensor
      assertEquals("correct name", "TA", sensor.getName());

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      Logger logger = Logger.getLogger("Logger");
      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, logger, null);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 3, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 10, zigs.size());
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

    /**
     * check that we allow a couple of apparently steady cuts during a turn
     * 
     * @throws FileNotFoundException
     */
    public void testSteadyInTurn() throws FileNotFoundException
    {
      final SensorWrapper sensor = new SensorWrapper("name");
      sensor.add(wrapMe(sensor, 100000, 180d, 270d));
      sensor.add(wrapMe(sensor, 110000, 170d, 280d));
      sensor.add(wrapMe(sensor, 120000, 160d, 290d));
      sensor.add(wrapMe(sensor, 130000, 150d, 300d));
      sensor.add(wrapMe(sensor, 140000, 140d, 310d));
      sensor.add(wrapMe(sensor, 150000, 130d, 310d));
      sensor.add(wrapMe(sensor, 160000, 122d, 220d));
      sensor.add(wrapMe(sensor, 170000, 113d, 221d));
      sensor.add(wrapMe(sensor, 180000, 104d, 222d));
      sensor.add(wrapMe(sensor, 190000, 095d, 223d));
      sensor.add(wrapMe(sensor, 200000, 086d, 224d));
      sensor.add(wrapMe(sensor, 210000, 076d, 224d));
      sensor.add(wrapMe(sensor, 220000, 62d, 200d));
      sensor.add(wrapMe(sensor, 230000, 53d, 210d));
      sensor.add(wrapMe(sensor, 240000, 44d, 220d));
      sensor.add(wrapMe(sensor, 250000, 35d, 230d));
      sensor.add(wrapMe(sensor, 260000, 26d, 240d));
      sensor.add(wrapMe(sensor, 270000, 36d, 240d));
      sensor.add(wrapMe(sensor, 280000, 42d, 260d));
      sensor.add(wrapMe(sensor, 290000, 53d, 280d));
      sensor.add(wrapMe(sensor, 300000, 64d, 300d));
      sensor.add(wrapMe(sensor, 310000, 75d, 320d));
      sensor.add(wrapMe(sensor, 320000, 66d, 340d));
      sensor.add(wrapMe(sensor, 330000, 56d, 0d));
      sensor.add(wrapMe(sensor, 340000, 45d, 20d));
      sensor.add(wrapMe(sensor, 350000, 35d, 30d));
      sensor.add(wrapMe(sensor, 360000, 35d, 30d));
      sensor.add(wrapMe(sensor, 370000, 15d, 70d));
      sensor.add(wrapMe(sensor, 380000, 355d, 20d));
      sensor.add(wrapMe(sensor, 390000, 355d, 20d));
      sensor.add(wrapMe(sensor, 400000, 345d, 20d));
      // sensor.add(wrapMe(sensor, 410000, 345d, 20d));

      sensor.setVisible(true);

      final TrackWrapper host = new TrackWrapper();
      host.setName("Host");
      host.add(sensor);

      final AmbiguityResolver solver = new AmbiguityResolver();

      final Logger logger = Logger.getLogger("Test output");
      logger.setUseParentHandlers(false);
      logger.addHandler(new ConsoleHandler()
      {

        @Override
        public void publish(final LogRecord record)
        {
          System.out.println(record.getMessage());
        }
      });

      final LegsAndZigs sliced =
          solver.sliceTrackIntoLegsUsingAmbiguity(host, 2.2, 0.2, logger, null);

      // for(LegOfCuts leg: sliced.legs)
      // {
      // System.out.println(leg.get(0).getDTG().getDate().getTime() + " - " +
      // leg.get(leg.size()-1).getDTG().getDate().getTime());
      // }
      //
      // System.out.println("===");
      // for(SensorContactWrapper cut: sliced.zigCuts)
      // {
      // System.out.println(cut.getDTG().getDate().getTime());
      // }

      assertNotNull("produced slices", sliced);
      assertEquals("correct legs", 3, sliced.legs.size());
      assertEquals("correct turning cuts", 19, sliced.zigCuts.size());
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

  private static void doLog(final Logger logger, final String msg)
  {
    if (logger != null)
    {
      logger.log(Level.INFO, msg);
    }
  }

  /**
   * trim the supplied value to the 0..360 domain
   * 
   * @param val
   *          value to trim
   * @param target
   *          value we're comparing against - so we keep the subject value fairly clsoe to this one
   * 
   * @return
   */
  private static double trim(final double val, final Double target)
  {
    double res = val;
    
    // ok, get trimming
    if ((target == null || target > -270) && (res < -360d))
    {
      res += 360d;
    }
    if ((target == null || target < 270) && (res >= 360d))
    {
      res -= 360d;
    }

    // ok, special cases. We're looking for a value near 360, but we've only got
    // the value near 050
    if ((target != null && target > 270) && (res <= 270d))
    {
      res += 360d;
    }
    if ((target != null && target < -270) && (res >= -270d))
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

  private long midTimeFor(final LegOfCuts lastLeg, final LegOfCuts leg)
  {
    final long startTime =
        lastLeg.get(lastLeg.size() - 1).getDTG().getDate().getTime();
    final long endTime = leg.get(0).getDTG().getDate().getTime();

    // and the mid-way value
    return startTime + (endTime - startTime) / 2;
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

      System.out.println(midTime + ", "
          + trim(valueAt(midTime, slopeOne), null) + ", " + trim(legTwo, null));
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

      System.out
          .println(thisTime + ", " + trim(valueAt(thisTime, slopeOne), null)
              + ", " + trim(legTwo, null));
    }

    if (firstLeg)
    {
      // ok, output the mid-point
      final double legTwo = twoLegs ? valueAt(midTime, slopeTwo) : Double.NaN;
      System.out.println(midTime + ", "
          + trim(valueAt(midTime, slopeOne), null) + ", " + trim(legTwo, null));
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

  public List<ResolvedLeg> resolve(final List<LegOfCuts> legs)
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

        // get the slope scores we know we need. Note: when we generate the
        // value for the second leg, we use our knowledge of the end of
        // the first leg to ensure we keep the resulting value in
        // roughly the correct domain
        final double lastSlopeValOne =
            trim(valueAt(midTime, lastSlopeOne), null);
        final double nextSlopeValOne =
            trim(valueAt(midTime, thisSlopeOne), lastSlopeValOne);
        final double nextSlopeValTwo =
            trim(valueAt(midTime, thisSlopeTwo), lastSlopeValOne);

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
          final double lastSlopeValTwo =
              trim(valueAt(midTime, lastSlopeTwo), null);

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
  
  private static boolean nearNorth(double bearing)
  {
    return Math.abs(bearing) < 20 || Math.abs(bearing) > 340;
  }

  private LegsAndZigs sliceSensorIntoLegsUsingAmbiguity(
      final SensorWrapper sensor, final double minZig, final double maxSteady,
      final Logger logger, final TimeSeries scores)
  {
    final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
    final LegOfCuts zigs = new LegOfCuts();

    if (scores != null)
    {
      scores.clear();
    }

    final Enumeration<Editable> enumer = sensor.elements();
    Double lastDelta = null;
    Double lastBearing = null;
    HiResDate lastTime = null;
    LegOfCuts thisLeg = null;
    LegOfCuts thisZig = null;
    SensorContactWrapper firstCut = null;
    final LegOfCuts possLeg = new LegOfCuts();
    final int possLegAllowance = 2;

    while (enumer.hasMoreElements())
    {
      final SensorContactWrapper cut =
          (SensorContactWrapper) enumer.nextElement();

      if (cut.getVisible() && cut.getHasAmbiguousBearing())
      {
        // ok, TA data
        double delta = cut.getAmbiguousBearing() - cut.getBearing();

        final HiResDate time = cut.getDTG();

        // is this the first cut?
        if (lastDelta == null)
        {
          // store it. we'll add it to whatever type of data we build
          firstCut = cut;
        }
        else
        {
          // just check that we're not getting some jitter about zero
          if(nearNorth(cut.getAmbiguousBearing()) || nearNorth(cut.getBearing()))
          {
            // ok, we may be near North, so we may be jittering
            // around zero. See if the has been a huge delta jump
            if(delta - lastDelta > 180)
            {
              // ok, we're now in 360 domain.  Subtract 360 to
              // put us back in the old domain
              delta -= 360;
            }
            else if(delta - lastDelta < -180)
            {
              // we've moved to 0 domain. Add 360 to get back in
              // the 360 domain
              delta += 360;
            }
          }
          
          double valueDelta = delta - lastDelta;

          // if we're not already in a turn, then any
          // monster delta will prob be related to domain
          if (thisLeg != null)
          {
            if (valueDelta < -180)
            {
              valueDelta += 360d;
            }
            else if (valueDelta > 180)
            {
              valueDelta -= 180d;
            }
          }

          // ok, work out the change rate
          final long timeDeltaMillis =
              time.getDate().getTime() - lastTime.getDate().getTime();
          final long timeDeltaSecs = timeDeltaMillis / 1000L;

          final double gapRate = Math.abs(valueDelta / timeDeltaSecs);

          // and the delta bearing rate
          double brgDelta = Math.abs(cut.getBearing() - lastBearing);
          if (brgDelta > 180)
          {
            brgDelta = 360 - brgDelta;
          }
          double brgRate = brgDelta / timeDeltaSecs;

          // ok, combine the two rates
          final double combinedRate = gapRate + brgRate;

          if (scores != null)
          {
            final FixedMillisecond sec =
                new FixedMillisecond(time.getDate().getTime());
            final TimeSeriesDataItem item =
                new TimeSeriesDataItem(sec, combinedRate);
            scores.add(item);
          }

          final String timeStr = time.getDate().toString();
          final String stats =
              timeStr + " brg:" + (int) cut.getBearing() + " ambig:"
                  + (int) cut.getAmbiguousBearing() + " step (secs)"
                  + (int) timeDeltaSecs + " gap delta rate:" + gapRate
                  + " lastBrg:" + lastBearing.intValue() + " brg:"
                  + ((int) cut.getBearing()) + " brg delta:" + brgRate
                  + " combined:" + combinedRate;
          doLog(logger, stats);

          // if(time.getDate().getTime() == 260000)
          // {
          // System.out.println("here");
          // }

          if (combinedRate > minZig)
          {
            // ok, we were on a straight leg
            if (thisLeg != null)
            {
              // close the leg
              thisLeg = null;
              doLog(logger, timeStr + " End leg.");
            }

            // ok, we're in a leg
            if (thisZig == null)
            {
              thisZig = new LegOfCuts();
              doLog(logger, timeStr + " New zig.");
            }

            // do we have any pending cuts
            if (!possLeg.isEmpty())
            {
              doLog(logger, timeStr
                  + " Did have poss straight cuts. Drop them, we're in a turn");

              // ok, we have a couple of cuts that look like they're straight.
              // well, they're not. they're actually in a turn
              thisZig.addAll(possLeg);

              // and clear the list
              possLeg.clear();
            }

            // if we have a pending first cut,
            // we should store it
            if (firstCut != null)
            {
              thisZig.add(firstCut);
              firstCut = null;
            }

            thisZig.add(cut);
          }
          else
          {
            boolean straightCutHandled = false;

            if (thisZig != null)
            {
              // hmm, we were in a turn, and now things are straight.
              // but, we want to allow a number of low-rate-change
              // entries, just in cases there's a coincidental
              // couple of steady cuts during the turn.
              if (possLeg.size() < possLegAllowance)
              {
                doLog(logger, timeStr + " Poss straight leg. Cache it.");

                // ok, we'll add this to the list
                possLeg.add(cut);

                straightCutHandled = true;
              }
              else
              {
                // ok, we were in a turn. End it
                zigs.addAll(thisZig);

                doLog(logger, timeStr + " Zig ended.");

                // close the leg
                thisZig = null;
              }
            }

            if (!straightCutHandled)
            {
              // ok, we're in a leg
              if (thisLeg == null)
              {
                doLog(logger, timeStr + " New Leg.");

                thisLeg = new LegOfCuts();

                // right. We've allowed a couple of potential cuts
                // but, we've ended up on a straight leg. Add the stored
                // cuts to the leg
                if (!possLeg.isEmpty())
                {
                  doLog(logger, timeStr + " Have poss straight leg cuts.");
                  thisLeg.addAll(possLeg);
                  possLeg.clear();
                }

                legs.add(thisLeg);
              }

              // if we have a pending first cut,
              // we should store it
              if (firstCut != null)
              {
                thisLeg.add(firstCut);
                firstCut = null;
              }

              thisLeg.add(cut);
            }
          }
        }
        lastDelta = delta;
        lastTime = time;
        lastBearing = cut.getBearing();
      }

    }

    // ok, do some last minute tidying

    // are we still in a zig?
    if (thisZig != null)
    {
      doLog(logger, "Finishing zig.");

      // store the zig cuts
      zigs.addAll(thisZig);
      thisZig = null;
    }

    // do we have any possible straight leg cuts
    if (!possLeg.isEmpty())
    {
      doLog(logger, "Append trailing straight cuts.");
      thisLeg = new LegOfCuts();

      thisLeg.addAll(possLeg);
      possLeg.clear();

      legs.add(thisLeg);
    }

    return new LegsAndZigs(legs, zigs);
  }

  public LegsAndZigs sliceTrackIntoLegsUsingAmbiguity(final TrackWrapper track,
      final double minZig, final double maxSteady, final Logger logger,
      final TimeSeries scores)
  {
    final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
    final LegOfCuts zigCuts = new LegOfCuts();
    final LegsAndZigs res = new LegsAndZigs(legs, zigCuts);

    // ok, go for it
    final BaseLayer sensors = track.getSensors();
    final Enumeration<Editable> numer = sensors.elements();
    while (numer.hasMoreElements())
    {
      final SensorWrapper sensor = (SensorWrapper) numer.nextElement();
      if (sensor.getVisible())
      {
        final LegsAndZigs thisL =
            sliceSensorIntoLegsUsingAmbiguity(sensor, minZig, maxSteady,
                logger, scores);
        if (thisL.legs.size() > 0)
        {
          res.legs.addAll(thisL.legs);
        }
        if (thisL.zigCuts.size() > 0)
        {
          res.zigCuts.addAll(thisL.zigCuts);
        }
      }
    }
    return res;
  }

}
