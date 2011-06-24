/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 2:09:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Element;

import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;

public final class XMLRange implements XMLOperation
{
  /**
   * the min value
   */
  double _min;

  /**
   * the max value
   */
  double _max;

  /**
   * the step to use
   */
  private Double _step;

  /**
   * how many random permutations to use
   */
  Integer _numPerms;

  /** an (optional) list of random values to use when the user has
   * specified how many permutations are required
   *
   */
  Vector<Double> _myPerms;

  /**
   * the text format to use
   */
  private java.text.DecimalFormat _format;

  /**
   * the current value of this operation
   */
  double _currentValue;

  /** the random generator we're going to use
   *
   */
  int _model;

  /**
   * ************************************************************
   * constructor
   * *************************************************************
   */
  public XMLRange(final Element element)
  {
    // get the data items
    double min = Double.parseDouble(element.getAttribute("min"));
    double max = Double.parseDouble(element.getAttribute("max"));

    // see if the step size has been set
    final String stepStr = element.getAttribute("step");
    Double step;
    if(stepStr.length() > 0)
    {
      step = new Double(stepStr);
    }
    else
      step = null;

    // now the number format
    final String formatStr = element.getAttribute("format");
    DecimalFormat format;
    if (formatStr.length() > 0)
      format = new java.text.DecimalFormat(formatStr);
    else
      format = null;

    // see if the user has specified num perms
    final String perms = element.getAttribute("number_permutations");
    Integer numPerms;
    if (perms.length() > 0)
    {
      numPerms = new Integer(perms);
    }
    else
      numPerms = null;

    // and see if there is a random model
    final String modelStr = element.getAttribute("RandomModel");
    int model;
    if(modelStr.length() > 0)
    {
      model = XMLVariance.getModelFromString(modelStr);
    }
    else
     model = RandomGenerator.UNIFORM;

    assign(max, min, model, numPerms, step, format);


  }

  /**
   * default constructor, used in cloning operation
   */
  private XMLRange(final XMLRange other)
  {
    assign(other._max, other._min, other._model, other._numPerms, other._step, other._format);
  }

  /** testing constructor
   *
   */
  XMLRange()
  {
    // have to set the values using the assign method
  }


  /***************************************************************
   *  member methods
   ***************************************************************/

  /** store the values in this object - used for refactoring & to help testing
   *
   */
  void assign(double max, double min, int model, Integer numPerms, Double step, DecimalFormat format)
  {
    _max = max;
    _min = min;
    _numPerms = numPerms;
    _step = step;
    _format = format;
    _model = model;

    // and generate a new random permutation
    newPermutation();
  }




  /**
   * clone this object
   */
  public final Object clone()
  {
    final XMLRange res = new XMLRange(this);

    // but put the current value back in
    res._currentValue = this._currentValue;

    return res;
  }


  /**
   * produce a new value for this operation
   */
  public final void newPermutation()
  {
    // does the user want a restricted number of permutations?
    if(_numPerms != null)
    {
      // yes.  Has the list of perms been defined?
      if(_myPerms == null)
      {
        _myPerms = new Vector<Double>(_numPerms.intValue(), 1);

        // nope, better create it
        for(int i=0;i<_numPerms.intValue();i++)
        {
          double newVal = getNextRandom();
          _myPerms.add(new Double(newVal));
        }
      }

      // ok, we've got our list now.
      final int index = (int)(ASSET.Util.RandomGenerator.nextRandom() * _myPerms.size());
      Double thisVal = (Double)_myPerms.get(index);
      _currentValue = thisVal.doubleValue();

    }
    else
    {
      // nope, just pull one out of the ether...
      _currentValue = getNextRandom();
    }
  }

  /** helper method which generates a random number according to the user-settings
   *
   * @return a new random number appropriate to the current model, step size, format
   */
  private double getNextRandom()
  {
    double res;

    // has a step size been defined?
    if(_step != null)
    {
      // yes, create a random number using the supplied step size
      final int num_steps = (int) ((_max - _min) / _step.doubleValue());
      final int rnd = (int) (ASSET.Util.RandomGenerator.nextRandom() * num_steps);
      final double newVal = _min + rnd * _step.doubleValue();
      res = newVal;
    }
    else
    {
      // nope, just get a completely random one
      res = ASSET.Util.RandomGenerator.generateRandomNumber(_min, _max, _model);
    }
    return res;
  }

  /**
   * return the current value of this permutation
   */
  public final String getValue()
  {
    final String res;
    if (_format == null)
    {
      res = "" + _currentValue;
    }
    else
    {
      res = _format.format(_currentValue);
    }

    return res;
  }


  /**
   * merge ourselves with the supplied operation
   */
  public final void merge(final XMLOperation other)
  {
    final XMLRange xr = (XMLRange) other;
    _currentValue = xr._currentValue;

    // add a little random variance
    final double variance = 0.05 * (_max - _min);
    final double var_2 = 2 * variance;
    final double rand = ASSET.Util.RandomGenerator.nextRandom();
    _currentValue += (var_2) * rand - (var_2);

    // check we're still in range
    _currentValue = Math.max(_currentValue, _min);
    _currentValue = Math.min(_currentValue, _max);

    //    double sep = xr._currentValue - _currentValue;
    //    double newVal = _currentValue + sep / 2;
    //    _currentValue = newVal;
  }

  //////////////////////////////////////////////////////////////////////
  // getter/setter methods
  //////////////////////////////////////////////////////////////////////
  public final double getMin()
  {
    return _min;
  }

  public final double getMax()
  {
    return _max;
  }

  public final Double getStep()
  {
    return _step;
  }

  public final String getFormat()
  {
    return _format.toPattern();
  }


  //////////////////////////////////////////////////
  // property testing
  //////////////////////////////////////////////////
  public static class RangeTest extends SupportTesting
  {
    public RangeTest(String s)
    {
      super(s);
    }

    public void testNumPerms()
    {
      XMLRange newR = new XMLRange();
      newR.assign(140, 60, RandomGenerator.UNIFORM, new Integer(12), null, null);

      // ok, check we haven't got the perms yet
      assertEquals("wrong min", 60, newR._min, 0);
      assertEquals("wrong max", 140, newR._max, 0);
      assertEquals("wrong num perms", 12, newR._numPerms.intValue());
      assertEquals("wrong model", RandomGenerator.UNIFORM, newR._model);

      assertEquals("wrong num perms generated", 12, newR._myPerms.size(), 0);
      for (int i = 0; i < newR._myPerms.size(); i++)
      {
        Double aDouble = (Double) newR._myPerms.elementAt(i);
        assertTrue("out of range", aDouble.doubleValue() > 60);
        assertTrue("out of range", aDouble.doubleValue() < 140);
      }

      // and check that we only get the right values
      HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
      for(int i=0;i<5000;i++)
      {
        newR.newPermutation();
        Double thisVal = new Double(newR._currentValue);
        assertTrue("not one of ours found", newR._myPerms.contains(thisVal));
        int index = newR._myPerms.indexOf(thisVal);
        Integer res = (Integer) scores.get(new Integer(index));
        if(res == null)
          res = new Integer(0);

        res = new Integer(res.intValue() + 1);
        scores.put(new Integer(index), res);
      }

//      Iterator<Integer> freq = scores.keySet().iterator();
//      while (freq.hasNext())
//      {
//        Integer index = (Integer) freq.next();
//        Integer score = (Integer) scores.get(index);
//      }

    }

    public void testGaussianPerms()
    {
      XMLRange newR = new XMLRange();
      newR.assign(140, 60, RandomGenerator.NORMAL, new Integer(120), null, null);

      // ok, check we haven't got the perms yet
      assertEquals("wrong min", 60, newR._min, 0);
      assertEquals("wrong max", 140, newR._max, 0);
      assertEquals("wrong num perms", 120, newR._numPerms.intValue());
      assertEquals("wrong model", RandomGenerator.NORMAL, newR._model);

      assertEquals("wrong num perms generated", 120, newR._myPerms.size(), 0);
      for (int i = 0; i < newR._myPerms.size(); i++)
      {
        Double aDouble = (Double) newR._myPerms.elementAt(i);
        assertTrue("out of range", aDouble.doubleValue() > 60);
        assertTrue("out of range", aDouble.doubleValue() < 140);
      }

      // and check that we only get the right values
      HashMap<Integer, Integer> scores = new HashMap<Integer, Integer>();
      for(int i=0;i<5000;i++)
      {
        newR.newPermutation();
        Double thisVal = new Double(newR._currentValue);
        assertTrue("not one of ours found", newR._myPerms.contains(thisVal));
        int index = newR._myPerms.indexOf(thisVal);
        Integer res = (Integer) scores.get(new Integer(index));
        if(res == null)
          res = new Integer(0);

        res = new Integer(res.intValue() + 1);
        scores.put(new Integer(index), res);
      }

    }
  }

}
