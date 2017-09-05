package org.mwc.debrief.track_shift.ambiguity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import Debrief.Wrappers.SensorContactWrapper;

class LegOfCuts extends ArrayList<SensorContactWrapper>
{

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
  static final int LEG_LENGTH = 8;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  /** extract the requested portion of the data
   * 
   * @param period
   * @return
   */
  List<SensorContactWrapper> extractPortion(final WhichPeriod period)
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

  /** fit a polynomial curve to the sensor cuts in this leg
   * 
   * @param period what portion of data to use
   * @param whichBearing whether we want the raw or ambiguous bearing
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
          AmbiguityResolver.putObsInCorrectRange(obs.toList());
      final List<WeightedObservedPoint> tidyObs =
          AmbiguityResolver.putObsInCorrectDomain(rangedObs);
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
}