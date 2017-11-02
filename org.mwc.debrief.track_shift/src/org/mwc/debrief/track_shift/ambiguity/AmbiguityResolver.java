package org.mwc.debrief.track_shift.ambiguity;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichBearing;
import org.mwc.debrief.track_shift.ambiguity.LegOfCuts.WhichPeriod;
import org.mwc.debrief.track_shift.controls.ZoneChart.Zone;
import org.mwc.debrief.track_shift.views.BearingResidualsView;

import Debrief.ReaderWriter.Replay.ImportReplay;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.GenericData.Watchable;

public class AmbiguityResolver
{
  private static final long OS_TURN_MIN_TIME_INTERVAL = 180l;
  private static final double OS_TURN_MIN_COURSE_CHANGE = 10d;

  private static class LegPermutation
  {
    final private double[] coreSlopeEarly;
    final private double[] ambigSlopeEarly;
    final private double[] coreSlopeLate;
    final private double[] ambigSlopeLate;

    final private LegOfCuts leg;

    public double coreBefore;
    public double ambigBefore;
    public double coreAfter;
    public double ambigAfter;

    public LegPermutation(final LegOfCuts leg, final double[] coreSlopeEarly,
        final double[] ambigSlopeEarly, final double[] coreSlopeLate,
        final double[] ambigSlopeLate)
    {
      this.leg = leg;
      this.coreSlopeEarly = coreSlopeEarly;
      this.ambigSlopeEarly = ambigSlopeEarly;
      this.coreSlopeLate = coreSlopeLate;
      this.ambigSlopeLate = ambigSlopeLate;
    }

  }

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

  private static class PermScore
  {

    final private WhichBearing lastB;
    final private WhichBearing thisB;
    final private double thisScore;
    private final LegOfCuts lastLeg;
    private final LegOfCuts thisLeg;

    public PermScore(final LegPermutation lastPerm,
        final LegPermutation thisPerm, final WhichBearing lastB,
        final WhichBearing thisB)
    {
      this.lastLeg = lastPerm.leg;
      this.thisLeg = thisPerm.leg;
      this.lastB = lastB;
      this.thisB = thisB;

      // retrieve the bearings
      final double beforeBearing;
      switch (lastB)
      {
        case CORE:
          beforeBearing = lastPerm.coreAfter;
          break;
        case AMBIGUOUS:
        default:
          beforeBearing = lastPerm.ambigAfter;
          break;
      }

      final double afterBearing;
      switch (thisB)
      {
        case CORE:
          afterBearing = thisPerm.coreBefore;
          break;
        case AMBIGUOUS:
        default:
          afterBearing = thisPerm.ambigBefore;
          break;
      }

      // ok, sort out the minimum angle between the two
      double score = afterBearing - beforeBearing;
      if (score > 180)
      {
        score -= 360;
      }
      else if (score < -180)
      {
        score += 360;
      }

      thisScore = Math.abs(score);
    }

    @Override
    public String toString()
    {
      return "( " + lastB + "-" + thisB + " :" + (int) thisScore + ")";
    }

  }

  public static class ResolvedLeg
  {
    public final WhichBearing bearing;
    public final LegOfCuts leg;

    public ResolvedLeg(final LegOfCuts leg, final WhichBearing bearing)
    {
      this.leg = leg;
      this.bearing = bearing;
    }
  }

  /**
   * helper class that cache's the total score for the list.
   * 
   * @author Ian
   * 
   */
  private static class ScoreList extends ArrayList<PermScore>
  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private double _score = 0;
    private boolean closed = false;

    public void finalise()
    {
      double total = 0;
      for (final PermScore item : this)
      {
        total += item.thisScore;
      }
      _score = total;

      closed = true;
    }

    /**
     * this method should only be called when no more items are to be added
     * 
     * @return
     */
    private double getScore()
    {
      return _score;
    }

    @Override
    public boolean add(PermScore e)
    {
      if (closed)
      {
        throw new IllegalArgumentException(
            "Cannot add more items once score is generated");
      }
      return super.add(e);
    }

    @Override
    public void add(int index, PermScore element)
    {
      if (closed)
      {
        throw new IllegalArgumentException(
            "Cannot add more items once score is generated");
      }
      super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends PermScore> c)
    {
      if (closed)
      {
        throw new IllegalArgumentException(
            "Cannot add more items once score is generated");
      }
      return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends PermScore> c)
    {
      if (closed)
      {
        throw new IllegalArgumentException(
            "Cannot add more items once score is generated");
      }
      return super.addAll(index, c);
    }

  }

  // ////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  // ////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class TestResolveAmbig extends junit.framework.TestCase
  {

    /**
     * algorithm to quickly generate large quantities of lists of boolean permutations. Taken from
     * here: https://stackoverflow.com/a/27994579/92441
     * 
     * @param n
     *          number of boolean perms
     * @return iterator delivers series of boolean arrays, each providing different permutation on
     *         the possibles
     */
    public static Iterator<Boolean[]> bool(final int n)
    {
      return new Iterator<Boolean[]>()
      {
        final long max = (long) Math.pow(2, n);
        long next = 0L;

        @Override
        public boolean hasNext()
        {
          return next < max;
        }

        @Override
        public Boolean[] next()
        {
          final Boolean[] b = new Boolean[n];
          for (int i = 0; i < n; i++)
          {
            b[i] = (next & (1 << i)) != 0;
          }
          next++;
          return b;
        }

      };
    }

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

    private void getPerms(final List<ArrayList<WhichBearing>> results,
        final int ctr, final List<WhichBearing> thisList,
        final WhichBearing newPerm)
    {
      final ArrayList<WhichBearing> newList = new ArrayList<WhichBearing>();

      if (thisList != null)
      {
        newList.addAll(thisList);
      }

      if (newPerm != null)
      {
        newList.add(newPerm);
      }

      if (ctr > 0)
      {
        // ok, add the two new perms
        final int thisCtr = ctr - 1;
        getPerms(results, thisCtr, newList, WhichBearing.CORE);
        getPerms(results, thisCtr, newList, WhichBearing.AMBIGUOUS);
      }
      else
      {
        // ok, finished. store the results
        results.add(newList);
      }
    }

    public void testGenPerms()
    {
      final List<ArrayList<WhichBearing>> results =
          new ArrayList<ArrayList<WhichBearing>>();
      final int ctr = 6;
      final List<WhichBearing> thisList = null;
      final WhichBearing newPerm = null;

      getPerms(results, ctr, thisList, newPerm);
      System.out.println(results.size());
      assertEquals("enough perms", (int) Math.pow(2, ctr), results.size());

      results.clear();

      final ArrayList<WhichBearing> starter = new ArrayList<WhichBearing>();
      starter.add(WhichBearing.CORE);
      starter.add(WhichBearing.AMBIGUOUS);

      results.add(starter);

      // try another way
      for (int i = 0; i < ctr; i++)
      {
        final ArrayList<ArrayList<WhichBearing>> newList =
            new ArrayList<ArrayList<WhichBearing>>();
        for (final ArrayList<WhichBearing> list : results)
        {
          final ArrayList<WhichBearing> permA = new ArrayList<WhichBearing>();
          permA.addAll(list);
          final ArrayList<WhichBearing> permB = new ArrayList<WhichBearing>();
          permB.addAll(list);
          permA.add(WhichBearing.CORE);
          permB.add(WhichBearing.AMBIGUOUS);

          newList.add(permA);
          newList.add(permB);
        }

        results.clear();
        results.addAll(newList);
      }

      System.out.println(results.size());
      assertEquals("enough perms", (int) Math.pow(2, ctr), results.size());

      final Iterator<Boolean[]> iter = bool(ctr);
      int numGenerated = 0;
      while (iter.hasNext())
      {
        @SuppressWarnings("unused")
        final Boolean[] perm = iter.next();
        numGenerated++;
      }

      assertEquals("enough perms", (int) Math.pow(2, ctr), numGenerated);

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
    
    public void testMaxLegsUnspecified() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks2.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensors", 1, track.getSensors().size());

      final Enumeration<Editable> sensorList = track.getSensors().elements();
      
      // make the sensor visible
      final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
      sensor.setVisible(true);
      TimePeriod timePeriod =
          new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor
              .getEndDTG());

      // check we have the correct sensor
      assertEquals("correct name", "TA", sensor.getName());

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      final Logger logger = Logger.getLogger("Logger");
      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
              null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
              timePeriod, null);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 12, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 22, zigs.size());
    }

    
    public void testMaxLegs() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks2.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensors", 1, track.getSensors().size());

      final Enumeration<Editable> sensorList = track.getSensors().elements();
      
      // make the sensor visible
      final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
      sensor.setVisible(true);
      TimePeriod timePeriod =
          new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor
              .getEndDTG());

      // check we have the correct sensor
      assertEquals("correct name", "TA", sensor.getName());

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      final Logger logger = Logger.getLogger("Logger");
      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
              null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
              timePeriod, 4);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 4, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 8, zigs.size());
      
      assertEquals("cuts present", 121, sensor.size());
      
      
      // ok, simulate moving along. Now delete the cuts.
      HiResDate firstLegStart = legs.get(0).getStartDTG();
      HiResDate lastLegEnd = legs.get(legs.size()-1).getEndDTG();
      TimePeriod period = new TimePeriod.BaseTimePeriod(firstLegStart, lastLegEnd);
      List<Zone> zones = new ArrayList<Zone>();
      for(LegOfCuts leg: legs)
      {
        long startT = leg.getStartDTG().getDate().getTime();
        long endT = leg.getEndDTG().getDate().getTime();
        Zone newZone = new Zone(startT, endT, null);
        zones.add(newZone);
      }
      LegOfCuts toDelete = BearingResidualsView.findCutsNotInZones(zones, period, track);
      
      // and delete them
      for(SensorContactWrapper cut: toDelete)
      {
        cut.getSensor().removeElement(cut);
      }
      
      // check the cuts got deleted Note - this isn't the same as cuts.size() - zigs.size(),
      // since I suspect we're constraining the cut period to the period of the 
      // identified legs, which may ignore a period of zig cuts are the
      // last leg
      assertEquals("cuts deleted", 115, sensor.size());
      
      // now we run the slicer gain
      
      
      
      
    }


    public void testHandleWiggle() throws FileNotFoundException
    {
      final TrackWrapper track = getData("Ambig_tracks_hover_north.rep");
      assertNotNull("found track", track);

      // has sensors
      assertEquals("has sensors", 2, track.getSensors().size());

      final Enumeration<Editable> sensorList = track.getSensors().elements();
      @SuppressWarnings("unused")
      final SensorWrapper hmSensor = (SensorWrapper) sensorList.nextElement();
      // make the sensor visible

      final SensorWrapper sensor = (SensorWrapper) sensorList.nextElement();
      sensor.setVisible(true);
      TimePeriod timePeriod =
          new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor
              .getEndDTG());

      // check we have the correct sensor
      assertEquals("correct name", "TA", sensor.getName());

      // ok, get resolving
      final AmbiguityResolver solver = new AmbiguityResolver();

      final Logger logger = Logger.getLogger("Logger");
      // try to get zones using ambiguity delta
      final LegsAndZigs res =
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, logger,
              null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
              timePeriod, null);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 3, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 10, zigs.size());
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
      TimePeriod timePeriod =
          new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor
              .getEndDTG());

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
          solver.sliceTrackIntoLegsUsingAmbiguity(track, 0.2, 0.2, 240, null,
              null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
              timePeriod, null);
      final List<LegOfCuts> legs = res.legs;
      final LegOfCuts zigs = res.zigCuts;

      assertNotNull("found zones", legs);
      assertEquals("found correct number of zones", 9, legs.size());

      assertNotNull("found zigs", zigs);
      assertEquals("found correct number of zig cuts", 16, zigs.size());
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
      // assertFalse("not ambig", leg4.get(0).getHasAmbiguousBearing());
      // assertFalse("not ambig", leg5.get(0).getHasAmbiguousBearing());
      // assertFalse("not ambig", leg6.get(0).getHasAmbiguousBearing());
      //
      // assertEquals("correct bearing", 180d, leg1.get(0).getBearing());
      // assertEquals("correct bearing", 182d, leg2.get(0).getBearing());
      // assertEquals("correct bearing", 200d, leg3.get(0).getBearing());
      // assertEquals("correct bearing", 260d, leg4.get(0).getBearing());
      // assertEquals("correct bearing", 41d, leg5.get(0).getBearing());
      // assertEquals("correct bearing", 350d, leg6.get(0).getBearing());

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
      TimePeriod timePeriod =
          new TimePeriod.BaseTimePeriod(sensor.getStartDTG(), sensor
              .getEndDTG());

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
          solver.sliceTrackIntoLegsUsingAmbiguity(host, 2.2, 0.2, 22, logger,
              null, OS_TURN_MIN_COURSE_CHANGE, OS_TURN_MIN_TIME_INTERVAL,
              timePeriod, null);

      assertNotNull("produced slices", sliced);
      assertEquals("correct legs", 4, sliced.legs.size());
      assertEquals("correct turning cuts", 13, sliced.zigCuts.size());
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

  private static void ditchBearingsForThisLeg(final LegOfCuts leg,
      final WhichBearing bearing)
  {
    for (final SensorContactWrapper cut : leg)
    {
      // cool, we have a course - we can go for it. remember the bearings
      final double bearing1 = cut.getBearing();
      final double bearing2 = cut.getAmbiguousBearing();

      switch (bearing)
      {
        case CORE:
          cut.setBearing(bearing1);
          cut.setAmbiguousBearing(bearing2);
          break;
        case AMBIGUOUS:
        default:
          cut.setBearing(bearing2);
          cut.setAmbiguousBearing(bearing1);
          break;
      }

      // remember we're morally ambiguous
      cut.setHasAmbiguousBearing(false);
    }
  }

  private static void doLog(final Logger logger, final String msg,
      final HiResDate time)
  {
    if (logger != null)
    {
      final String timeStr = time != null ? time.getDate().toString() : "";

      logger.log(Level.INFO, timeStr + " " + msg);
    }
  }

  private static Comparator<ScoreList> getPermComparator()
  {
    return new Comparator<ScoreList>()
    {

      @Override
      public int compare(final ScoreList d1, final ScoreList d2)
      {
        final int res;

        final double d1Total = d1.getScore();
        final double d2Total = d2.getScore();

        if (d1Total < d2Total)
        {
          res = -1;
        }
        else if (d1Total > d2Total)
        {
          res = 1;
        }
        else
        {
          res = 0;
        }

        return res;

      }

    };
  }

  private static long midTimeFor(final LegOfCuts lastLeg, final LegOfCuts leg)
  {
    final long startTime =
        lastLeg.get(lastLeg.size() - 1).getDTG().getDate().getTime();
    final long endTime = leg.get(0).getDTG().getDate().getTime();

    // and the mid-way value
    return startTime + (endTime - startTime) / 2;
  }

  private static boolean nearNorth(final double bearing)
  {
    return Math.abs(bearing) < 20 || Math.abs(bearing) > 340;
  }

  private static double shortAngle(final double brg1, final double brg2)
  {
    double res = brg1 - brg2;
    if (res > 180)
    {
      res -= 360;
    }
    if (res < -180)
    {
      res += 360;
    }

    return res;
  }

  /**
   * we've received another steady cut. Do we have enough cuts to treat it as a leg?
   * 
   * @param possLeg
   *          the cuts we've cached so far
   * @param thisTime
   *          the time of the new cut
   * @param minLength
   *          the min period required to treat it as a leg
   * @return yes/no
   */
  private static boolean stillCacheing(final LegOfCuts possLeg,
      final long thisTime, final long minLength)
  {
    final boolean res;
    if (possLeg.isEmpty())
    {
      res = true;
    }
    else
    {
      final long legStart = possLeg.get(0).getDTG().getDate().getTime();
      final long elapsed = (thisTime - legStart) / 1000L;
      res = elapsed < minLength;
    }

    return res;
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

  private static void walkScores(final List<LegPermutation> legs,
      final int curLeg, final ScoreList thisPermSoFar,
      final PermScore lastScore, final List<ScoreList> finishedPerms)
  {
    // take a deep copy of the clones, since we want independent copies
    final ScoreList newScores = new ScoreList();
    if (thisPermSoFar != null && !thisPermSoFar.isEmpty())
    {
      newScores.addAll(thisPermSoFar);
    }

    // and add a new score, if there is one
    if (lastScore != null)
    {
      newScores.add(lastScore);
    }

    // have we reached the end?
    if (curLeg < legs.size())
    {
      // ok, we can add some scores
      final LegPermutation lastPerm = legs.get(curLeg - 1);
      final LegPermutation thisPerm = legs.get(curLeg);

      // ok, increment the counter
      final int thisCount = curLeg + 1;

      // ok, what was the last leg?
      final WhichBearing lastBearing =
          lastScore == null ? null : lastScore.thisB;

      // ok, sort out the four permutations

      // if we had a previous leg, was if resolved as CORE?
      if (lastBearing == null || lastBearing == WhichBearing.CORE)
      {
        walkScores(legs, thisCount, newScores, new PermScore(lastPerm,
            thisPerm, WhichBearing.CORE, WhichBearing.CORE), finishedPerms);
        walkScores(legs, thisCount, newScores, new PermScore(lastPerm,
            thisPerm, WhichBearing.CORE, WhichBearing.AMBIGUOUS), finishedPerms);
      }

      // if we had a previous leg, was if resolved as AMBIGUOUS?
      if (lastBearing == null || lastBearing == WhichBearing.AMBIGUOUS)
      {
        walkScores(legs, thisCount, newScores, new PermScore(lastPerm,
            thisPerm, WhichBearing.AMBIGUOUS, WhichBearing.AMBIGUOUS),
            finishedPerms);
        walkScores(legs, thisCount, newScores, new PermScore(lastPerm,
            thisPerm, WhichBearing.AMBIGUOUS, WhichBearing.CORE), finishedPerms);
      }
    }
    else
    {
      // no more to be added, let the list sort out it's total
      newScores.finalise();

      // ok, we've reached the end. Store the score.
      finishedPerms.add(newScores);
    }
  }

  public List<ResolvedLeg> resolve(final List<LegOfCuts> legs)
  {
    final List<ResolvedLeg> res = new ArrayList<ResolvedLeg>();

    // get all the permutations for this set of legs
    final List<LegPermutation> listOfPermutations = getPermutations(legs);

    // ok, now work through the permutations
    final List<ScoreList> overallScores = new ArrayList<ScoreList>();

    // the recursive algorithm we use has a limit of 20 legs,
    // else we will run out of heap space.
    // so, limit it to 20
    if (listOfPermutations.size() > 20)
    {
      CorePlugin
          .showMessage(
              "Resolve Ambiguity",
              "Sorry, there is a limit of 20 legs. Please Filter to Time Period to reduce the number of legs being resolved");
      return null;
    }

    // ok, produce a list of permutations, with scores
    walkScores(listOfPermutations, 1, null, null, overallScores);

    // we now need to sort them into ascending order
    final Comparator<ScoreList> sorter = getPermComparator();
    Collections.sort(overallScores, sorter);

    // get the best performing one
    final ArrayList<PermScore> winner = overallScores.get(0);

    // ok, set the legs to the correct permutation
    boolean firstZig = true;
    for (final PermScore zig : winner)
    {
      if (firstZig)
      {
        // SPECIAL CASE:
        // for the first zig we handle the previous leg

        // ditch the side we don't want
        ditchBearingsForThisLeg(zig.lastLeg, zig.lastB);

        // remember the leg
        res.add(new ResolvedLeg(zig.lastLeg, zig.lastB));

        firstZig = false;
      }

      // for all legs we handle the following leg

      // ditch the side we don't want
      ditchBearingsForThisLeg(zig.thisLeg, zig.thisB);

      // remember the leg
      res.add(new ResolvedLeg(zig.thisLeg, zig.thisB));
    }

    return res;
  }

  private List<LegPermutation> getPermutations(final List<LegOfCuts> legs)
  {
    final List<LegPermutation> listOfPermutations =
        new ArrayList<LegPermutation>();

    // ok, loop through the legs
    LegOfCuts previousLeg = null;
    LegPermutation lastPerm = null;
    for (final LegOfCuts leg : legs)
    {

      // generate the curves
      final double[] coreSlopeEarly =
          leg.getCurve(WhichPeriod.EARLY, WhichBearing.CORE);
      final double[] ambigSlopeEarly =
          leg.getCurve(WhichPeriod.EARLY, WhichBearing.AMBIGUOUS);
      final double[] coreSlopeLate =
          leg.getCurve(WhichPeriod.LATE, WhichBearing.CORE);
      final double[] ambigSlopeLate =
          leg.getCurve(WhichPeriod.LATE, WhichBearing.AMBIGUOUS);

      // special handling. if this has already been resolved, we can skip it
      if (ambigSlopeEarly != null && ambigSlopeLate != null)
      {
        // now put them into a permutation
        final LegPermutation thisPerm =
            new LegPermutation(leg, coreSlopeEarly, ambigSlopeEarly,
                coreSlopeLate, ambigSlopeLate);

        // have we already processed a leg?
        if (previousLeg != null)
        {
          final long midTime = midTimeFor(previousLeg, leg);

          // sort out the after scores for the last leg
          lastPerm.coreAfter = valueAt(midTime, lastPerm.coreSlopeLate);
          lastPerm.ambigAfter = valueAt(midTime, lastPerm.ambigSlopeLate);

          // and the early scores for this leg
          thisPerm.coreBefore = valueAt(midTime, thisPerm.coreSlopeEarly);
          thisPerm.ambigBefore = valueAt(midTime, thisPerm.ambigSlopeEarly);
        }

        // store the leg permutation
        listOfPermutations.add(thisPerm);
        lastPerm = thisPerm;

        // special handling.
        previousLeg = leg;
      }
    }
    return listOfPermutations;
  }

  /**
   * During a turn the difference between the bearing and ambiguous bearing go crazy. Exploit this
   * behaviour to find periods when the array is staedy.
   * 
   * @param sensor
   *          the sensor to process
   * @param minZig
   *          above this value we treat it as a zig
   * @param minBoth
   *          below this value we treat it as a leg
   * @param minLegLength
   *          we need at least this period of cuts to interpret steady bearings as O/S leg
   * @param logger
   *          where to log the results
   * @param scores
   *          a time-series of scores, to be plotted in the zone chart
   * @param trackPeriod
   *          the extent of the parent track, since we don't want to plot outside this
   * @param osTurnMinCourseChange
   * @param osTurnMinTimeDelta
   * @return A collection of legs and zigs.
   */
  private LegsAndZigs sliceSensorIntoLegsUsingAmbiguity(
      final SensorWrapper sensor, final double minZig, final double minBoth,
      final double minLegLength, final Logger logger, final TimeSeries scores,
      final TimePeriod trackPeriod, final Double osTurnMinCourseChange,
      final Long osTurnMinTimeDelta, final Integer allowedLegs)
  {
    final List<LegOfCuts> legs = new ArrayList<LegOfCuts>();
    final LegOfCuts zigs = new LegOfCuts();

    if (scores != null)
    {
      scores.clear();
    }

    final Enumeration<Editable> enumer = sensor.elements();
    Double lastDelta = null;
    SensorContactWrapper lastCut = null;
    HiResDate lastTime = null;
    LegOfCuts thisLeg = null;
    LegOfCuts thisZig = null;
    Double lastCourse = null;
    SensorContactWrapper firstCut = null;
    final LegOfCuts possLeg = new LegOfCuts();

    while (enumer.hasMoreElements() && (allowedLegs == null || legs.size() <= allowedLegs))
    {
      final SensorContactWrapper cut =
          (SensorContactWrapper) enumer.nextElement();

      final boolean hasAmbig = !Double.isNaN(cut.getAmbiguousBearing());

      if (cut.getVisible() && hasAmbig && trackPeriod.contains(cut.getDTG()))
      {
        // ok, TA data
        double delta = cut.getAmbiguousBearing() - cut.getBearing();

        final HiResDate time = cut.getDTG();

        // if (time.getDate().toString().contains("12:32:00"))
        // {
        // System.out.println("here");
        // }

        // is this the first cut?
        if (lastDelta == null)
        {
          // store it. we'll add it to whatever type of data we build
          firstCut = cut;
        }
        else
        {
          // just check that we're not getting some jitter about zero
          if (nearNorth(cut.getAmbiguousBearing())
              || nearNorth(cut.getBearing()))
          {
            // ok, we may be near North, so we may be jittering
            // around zero. See if the has been a huge delta jump
            if (delta - lastDelta > 180)
            {
              // ok, we're now in 360 domain. Subtract 360 to
              // put us back in the old domain
              delta -= 360;
            }
            else if (delta - lastDelta < -180)
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

          final double brgDelta =
              shortAngle(cut.getBearing(), lastCut.getBearing());
          double ambigBrgDelta =
              shortAngle(cut.getAmbiguousBearing(), lastCut
                  .getAmbiguousBearing());
          final double sysDelta = ambigBrgDelta - brgDelta;
          if (sysDelta > 180)
          {
            ambigBrgDelta = -ambigBrgDelta;
          }

          final double brgRate = brgDelta / timeDeltaSecs;

          final boolean TRIP_ZIG =
              Math.signum(brgDelta) == Math.signum(ambigBrgDelta)
                  && Math.abs(brgRate) > minBoth;

          // introduce an extra test, for if there's a large period of missing data
          boolean MISSING_CUTS = false;

          if (osTurnMinCourseChange != null && osTurnMinTimeDelta != null)
          {
            Watchable[] nearest = sensor.getHost().getNearestTo(time);
            if (nearest != null && nearest.length > 0)
            {
              double course =
                  MWC.Algorithms.Conversions.Rads2Degs(nearest[0].getCourse());

              if (lastCourse != null)
              {
                double courseDelta = Math.abs(shortAngle(course, lastCourse));
                MISSING_CUTS =
                    courseDelta > osTurnMinCourseChange
                        && timeDeltaSecs > osTurnMinTimeDelta;
                if (MISSING_CUTS)
                {
                  // ok, we've had a large course change, and it's been a long
                  // time since we last had any data. Assume we're now on a new leg.
                  // we can't create a zig for the list, since we don't have
                  // any cuts to put into it.
                  thisLeg = null;
                }
              }

              // ok, remember it
              lastCourse = course;
            }
          }

          if (scores != null)
          {
            final FixedMillisecond sec =
                new FixedMillisecond(time.getDate().getTime());
            final TimeSeriesDataItem item =
                new TimeSeriesDataItem(sec, gapRate);
            scores.addOrUpdate(item);
          }

          if (logger != null)
          {
            final String stats =
                " brg:" + (int) cut.getBearing() + " ambig:"
                    + (int) cut.getAmbiguousBearing() + " step (secs)"
                    + (int) timeDeltaSecs + " gap delta rate:" + gapRate
                    + " lastBrg:" + (int) lastCut.getBearing() + " brg:"
                    + ((int) cut.getBearing()) + " brg delta:" + brgRate
                    + " gap rate:" + gapRate + " OS zig trip:" + TRIP_ZIG;
            doLog(logger, stats, time);
          }

          // note: we ignore the gap rate if we know we've got missing cuts.

          if (TRIP_ZIG || (gapRate > minZig && !MISSING_CUTS))
          {
            // ok, were we on a straight leg?
            if (thisLeg != null)
            {
              // close the leg
              thisLeg = null;
              doLog(logger, " End leg.", time);
            }

            // ok, were we're in a zig?
            if (thisZig == null)
            {
              // not in a zig. Put us in a zig
              thisZig = new LegOfCuts();
              doLog(logger, " New zig.", time);
            }

            // do we have any pending cuts?
            if (!possLeg.isEmpty())
            {
              doLog(logger,
                  " Did have poss straight cuts. Drop them, we're in a turn",
                  time);

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
              final long thisTime = cut.getDTG().getDate().getTime();

              if (stillCacheing(possLeg, thisTime, (long) minLegLength))
              {
                doLog(logger, " Poss straight leg. Cache it.", time);

                // ok, we'll add this to the list
                possLeg.add(cut);

                straightCutHandled = true;
              }
              else
              {
                // ok, we were in a turn, now we know we're on a straight leg.
                // finish the zig
                zigs.addAll(thisZig);

                doLog(logger, " Zig ended.", time);

                // close the leg
                thisZig = null;
              }
            }

            if (!straightCutHandled)
            {
              // ok, we're in a leg
              if (thisLeg == null)
              {
                doLog(logger, " New Leg.", time);

                thisLeg = new LegOfCuts();

                // right. We've allowed a couple of potential cuts
                // but, we've ended up on a straight leg. Add the stored
                // cuts to the leg
                if (!possLeg.isEmpty())
                {
                  doLog(logger, " Have poss straight leg cuts.", time);
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
        lastCut = cut;
      }

    }

    // ok, do some last minute tidying

    // are we still in a zig?
    if (thisZig != null)
    {
      doLog(logger, "Finishing zig.", null);

      // store the zig cuts
      zigs.addAll(thisZig);
      thisZig = null;
    }

    // do we have any possible straight leg cuts
    if (!possLeg.isEmpty())
    {
      doLog(logger, "Append trailing straight cuts.", null);
      thisLeg = new LegOfCuts();

      thisLeg.addAll(possLeg);
      possLeg.clear();

      legs.add(thisLeg);
    }
    
    // just check we haven't gone over our allowance. We don't mind doing this,
    // since it lets use ensure we've got all of the previous leg
    if(allowedLegs != null && legs.size() > allowedLegs)
    {
      legs.remove(legs.get(legs.size()-1));
    }

    return new LegsAndZigs(legs, zigs);
  }

  public LegsAndZigs sliceTrackIntoLegsUsingAmbiguity(final TrackWrapper track,
      final double minZig, final double minBoth, final double minLegLength,
      final Logger logger, final TimeSeries scores,
      final Double osTurnMinCourseChange, final Long osTurnMinTimeInterval,
      final TimePeriod visiblePeriod, Integer maxLegs)
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
        final Integer allowedLegs = maxLegs == null ? null : maxLegs - res.legs.size();
        
        final LegsAndZigs thisL =
            sliceSensorIntoLegsUsingAmbiguity(sensor, minZig, minBoth,
                minLegLength, logger, scores, visiblePeriod,
                osTurnMinCourseChange, osTurnMinTimeInterval, allowedLegs);
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
