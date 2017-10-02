package org.mwc.debrief.track_shift.ambiguity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import Debrief.Wrappers.SensorContactWrapper;

public class LegOfCuts extends ArrayList<SensorContactWrapper>
{

  public static class TestLegs extends TestCase
  {

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

      List<WeightedObservedPoint> res = LegOfCuts.putObsInCorrectDomain(obs);
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

      res = LegOfCuts.putObsInCorrectDomain(obs);
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

      res = LegOfCuts.putObsInCorrectRange(obs);
      assertEquals("correct last score", 200d, res.get(0).getY(), 0.001);
      assertEquals("correct last score", 40d, res.get(res.size() - 1).getY(),
          0.001);
    }

  }

  /**
   * determine if we're using the main baaring, or the ambig bearing
   * 
   */
  public static enum WhichBearing
  {
    CORE, AMBIGUOUS
  }

  /**
   * determine which portion of the leg we're extracting data for
   * 
   */
  public static enum WhichPeriod
  {
    ALL, EARLY, LATE
  }

  /**
   * we use a weighting factor to reduce the influence of cuts in the first 1/4 of the leg - to
   * reduce the impact of the cuts while the array is steadying
   */
  private static final double EARLY_CUTS_WEIGHTING = 0.001d;

  /**
   * how many cuts to include in the min leg length
   * 
   */
  public static final int LEG_LENGTH = 8;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public static List<WeightedObservedPoint> putObsInCorrectDomain(
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

  public static List<WeightedObservedPoint> putObsInCorrectRange(
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

  /**
   * extract the requested portion of the data
   * 
   * @param period
   * @return
   */
  public List<SensorContactWrapper> extractPortion(final WhichPeriod period)
  {
    final List<SensorContactWrapper> cutsToUse;
    if (size() > LEG_LENGTH * 2)
    {
      switch (period)
      {
        case EARLY:
          cutsToUse = this.subList(0, LEG_LENGTH);
          break;
        case LATE:
          final int len = this.size();
          cutsToUse = this.subList(len - (LEG_LENGTH), len);
          break;
        case ALL:
        default:
          cutsToUse = this;
          break;
      }
    }
    else
    {
      cutsToUse = this;
    }
    return cutsToUse;
  }

  /**
   * fit a polynomial curve to the sensor cuts in this leg
   * 
   * @param period
   *          what portion of data to use
   * @param whichBearing
   *          whether we want the raw or ambiguous bearing
   * @return
   */
  public double[] getCurve(final WhichPeriod period,
      final WhichBearing whichBearing)
  {
    final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);

    // add my values
    final WeightedObservedPoints obs = new WeightedObservedPoints();
    Double lastBearing = null;

    // if we're a large dataset, just us a few at the end.
    // our curve fitter struggles with curves s-shaped curves,
    // it works best with c-shaped curves
    final List<SensorContactWrapper> cutsToUse = extractPortion(period);

    for (final SensorContactWrapper item : cutsToUse)
    {
      final long time = item.getDTG().getDate().getTime();
      double theBrg;
      if (whichBearing.equals(WhichBearing.AMBIGUOUS))
      {
        if (item.getHasAmbiguousBearing())
        {
          theBrg = item.getAmbiguousBearing();
        }
        else
        {
          theBrg = Double.NaN;
        }
      }
      else
      {
        theBrg = item.getBearing();
      }

      if (lastBearing != null)
      {
        // check if we've passed through zero
        final double delta = theBrg - lastBearing;
        if (Math.abs(delta) > 180)
        {
          if (delta > 0)
          {
            theBrg -= 360d;
          }
          else
          {
            theBrg += 360d;
          }
        }
      }

      lastBearing = theBrg;

      if (!Double.isNaN(theBrg))
      {
        // reduce the weighting of the first 1/4 of the cuts,
        // if we're looking at all or the early cuts.
        // This is to handle the occurrence where
        // the array still isn't stable
        final double weighting;
        weighting = getWeightingFor(period, obs);

        // and store the observation
        obs.add(weighting, time, theBrg);
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

  private double getWeightingFor(final WhichPeriod period,
      final WeightedObservedPoints obs)
  {
    // how many are in the first 1/4?
    final int firstQuarter = (int) Math.ceil(this.size() / 4d);

    final double weighting;
    if (period.equals(WhichPeriod.LATE))
    {
      // ok, we're doing a late leg. we don't
      // need to dumb down the first cuts
      weighting = 1d;
    }
    else
    {
      if (obs.toList().size() <= firstQuarter)
      {
        weighting = EARLY_CUTS_WEIGHTING;
      }
      else
      {
        weighting = 1d;
      }
    }
    return weighting;
  }

  public double getPeriodSecs()
  {
    final double res;
    if(!isEmpty())
    {
      double first = get(0).getDTG().getDate().getTime();
      double last = get(size()-1).getDTG().getDate().getTime();
      res = (last - first) / 1000;
    }
    else
    {
      res = 0d;
    }
    return res;
  }
}