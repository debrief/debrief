package org.mwc.debrief.track_shift.freq;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.math3.analysis.solvers.BisectionSolver;
import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.linear.DiagonalMatrix;

public class DopplerCurve
{

  /**
   * our function definition for a doppler curve
   * 
   * @author Ian
   * 
   */
  private static class DopplerCurveFitter extends AbstractCurveFitter
  {

    @Override
    protected LeastSquaresProblem getProblem(
        final Collection<WeightedObservedPoint> points)
    {
      final int len = points.size();
      final double[] target = new double[len];
      final double[] weights = new double[len];

      int i = 0;
      for (final WeightedObservedPoint point : points)
      {
        target[i] = point.getY();
        weights[i] = point.getWeight();
        i += 1;
      }

      final AbstractCurveFitter.TheoreticalValuesFunction model =
          new AbstractCurveFitter.TheoreticalValuesFunction(
              new ScalableSigmoid(), points);

      final LeastSquaresBuilder lsb = new LeastSquaresBuilder();
      lsb.maxEvaluations(100000);
      lsb.maxIterations(100000);
      lsb.start(new double[]
      {1.0, 1.0, 1.0, 1.0});
      lsb.target(target);
      lsb.weight(new DiagonalMatrix(weights));
      lsb.model(model.getModelFunction(), model.getModelFunctionJacobian());
      return lsb.build();
    }

  }

  /**
   * times of samples
   * 
   */
  private final ArrayList<Long> _times;

  /**
   * measured frequencies
   * 
   */
  private final ArrayList<Double> _freqs;

  /**
   * use the first time value as an offset
   * 
   */
  private final long _startTime;

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
  private final double[] _modelParameters;

  /**
   * scaling constant to prevent
   * "LevenbergMarquardtOptimizer unable to perform Q.R decomposition on the ...x... jacobian matrix"
   * error due to very large numbers on time stamps.
   * See:https://stackoverflow.com/questions/19116987
   * /levenbergmarquardtoptimizer-unable-to-perform-q-r-decomposition-on-the-107x2-jac
   */
  private static final double scaler = 1e5;

  public DopplerCurve(final ArrayList<Long> times, final ArrayList<Double> freqs)
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

    _times = times;
    _freqs = freqs;
    final int sampleCount = times.size();

    // change the times, so they start at zero (to keep time parameters small)
    _startTime = _times.get(0);

    // ok, collate the data
    final WeightedObservedPoints obs = new WeightedObservedPoints();
    for (int i = 0; i < sampleCount; i++)
    {
      
      obs.add((_times.get(i) - _startTime) / scaler, _freqs.get(i));
      
      long thisT = _times.get(i);
      Date newD = new Date(thisT);
      DateFormat df = new SimpleDateFormat("HH:mm:ss");
      
      
      System.out.println(df.format(thisT) + ", " +  _freqs.get(i));
    }

    // now Instantiate a parametric sigmoid fitter.
    final AbstractCurveFitter fitter = new DopplerCurveFitter();

    // Retrieve fitted parameters (a,b,c,d) for the sigmoid model: d + (c/(1+e^(a*x+b)))
    final double[] coeff = fitter.fit(obs.toList());

    // construct the second order derivative of the sigmoid with this parameters
    final SigmoidSecondDerivative derivativeFunc =
        new SigmoidSecondDerivative();
    derivativeFunc.coeff = coeff;

    // use bisection solver to find the zero crossing point of derivative
    final BisectionSolver bs = new BisectionSolver(1.0e-12, 1.0e-8);
    final double root =
        bs.solve(1000, derivativeFunc, 0, (_times.get(sampleCount - 1) - _startTime) / scaler,
            (_times.get(sampleCount / 2)- _startTime) / scaler);

    // and store the equation parameters
    _modelParameters = coeff;

    _inflectionTime = (long) (root * scaler) + _startTime;
    _inflectionFreq = valueAt(_inflectionTime);
  }

  public double inflectionFreq()
  {
    return _inflectionFreq;
  }

  public long inflectionTime()
  {
    return _inflectionTime ;
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
    return new ScalableSigmoid().value( (t - _startTime) / scaler, _modelParameters);
  }
}
