package org.mwc.debrief.track_shift.freq;
import java.util.ArrayList;

import net.finmath.optimizer.LevenbergMarquardt;
import net.finmath.optimizer.SolverException;

import org.apache.commons.math3.analysis.solvers.BisectionSolver;

public class DopplerCurveFinMath implements IDopplerCurve
{

  /**
   * time stamp at inflection point
   */
  private final long _inflectionTime;

  /**
   * frequency at inflection point
   */
  private final double _inflectionFreq;

  /**
   * double[4] -> [a,b,c,d] for the sigmoid model: d + (c/(1+e^(a*x+b)))
   */
  private double[] _modelParameters;

  private final Normaliser _timeNormaliser;
  private final Normaliser _freqNormaliser;

  public DopplerCurveFinMath(final ArrayList<Long> times,
      final ArrayList<Double> freqs)
  {
    // do some data testing
    if (times == null || freqs == null)
    {
      throw new IllegalArgumentException("The input datasets cannot be null");
    }

    if (times.size() == 0 || freqs.size() == 0)
    {
      throw new IllegalArgumentException("The input datasets cannot be empty");
    }

    final int sampleCount = times.size();

    // convert the times to doubles
    ArrayList<Double> dTimes = new ArrayList<Double>();
    for (Long t : times)
    {
      dTimes.add((double) t);
    }

    // create the normaliser for the two datasets
    _timeNormaliser = new Normaliser(dTimes, false);
    _freqNormaliser = new Normaliser(freqs, true);

    // ok, collate the data
    final double[] normalTimes = new double[sampleCount];
    final double[] normalFreqs = new double[sampleCount];
    final double[] weights = new double[sampleCount];

    for (int i = 0; i < sampleCount; i++)
    {
      double time = _timeNormaliser.normalise(dTimes.get(i));
      double freq = _freqNormaliser.normalise(freqs.get(i));

      normalTimes[i] = time;
      normalFreqs[i] = freq;
      weights[i] = 1d;
    }

    LevenbergMarquardt optimizer = new LevenbergMarquardt()
    {
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      // Override your objective function here
      public void setValues(final double[] params, final double[] values)
      {
        final double a = params[0];
        final double b = params[1];
        final double c = params[2];
        final double d = params[3];

        for (int i = 0; i < normalTimes.length; i++)
        {
          final double thisT = normalTimes[i];
          values[i] = ((a - d) / (1.0 + Math.pow(thisT / c, b))) + d;
        }
      }
    };

    // Set solver parameters
    optimizer.setInitialParameters(new double[]
    {1, 1, 1, 1});
    optimizer.setWeights(weights);
    optimizer.setMaxIteration(10000);
    optimizer.setTargetValues(normalFreqs);

    try
    {
      optimizer.run();
      _modelParameters = optimizer.getBestFitParameters();
    }
    catch (SolverException e)
    {
      e.printStackTrace();
    }

    FourParameterLogisticSecondDrivative derivativeFunc =
        new FourParameterLogisticSecondDrivative(_modelParameters);

    // use bisection solver to find the zero crossing point of derivative
    BisectionSolver bs = new BisectionSolver(1.0e-12, 1.0e-8);
    double root = bs.solve(1000000, derivativeFunc, 0.01, 1, 0.5);
    _inflectionTime = (long) _timeNormaliser.deNormalise(root);
    _inflectionFreq = valueAt(_inflectionTime);
  }

  public double inflectionFreq()
  {
    return _inflectionFreq;
  }

  public long inflectionTime()
  {
    return _inflectionTime;
  }

  /**
   * calculate the value on the curve at this time
   * 
   * @param t
   *          time
   * @return frequency at this time
   */
  public double valueAt(final long t)
  {
    double normalised = _timeNormaliser.normalise(t);
    double val =
        new FourParameterLogistic().value(normalised, _modelParameters);
    return _freqNormaliser.deNormalise(val);
  }

  @Override
  public void printCoords()
  {
    final double[] coords = _modelParameters;
    for (int i = 0; i < coords.length; i++)
    {
      System.out.print(coords[i] + " , ");
    }
    System.out.println();
  }
}