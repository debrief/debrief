package org.mwc.debrief.track_shift.zig_detector.moving_average;

import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

/** windowed moving average
 * 
 * @author Ian
 *
 */
public class TimeRestrictedMovingAverage
{
  final List<Long> _times;
  final List<Double> _values;
  final long _millis;
  
  /** whether we've got enough entries to fill the window
   * 
   */
  boolean _populated = false;

  public static class TestMe extends TestCase
  {
    public void testAverage()
    {
      TimeRestrictedMovingAverage avg = new TimeRestrictedMovingAverage(1000L);
      
      avg.add(100, 10);
      assertEquals("correct average", 10d, avg.getAverage(), 0.0001);

      avg.add(200, 20);
      assertEquals("correct average", 15d, avg.getAverage(), 0.0001);

      avg.add(500, 30);
      assertEquals("correct average", 20d, avg.getAverage(), 0.0001);
      
      // check we're storing all items
      assertEquals("holding all items", 3, avg.size());
      
      assertEquals("correctly calculating period", 400, avg.period());

      avg.add(800, 60);
      assertEquals("correct average", 30d, avg.getAverage(), 0.0001);

      avg.add(1000, 10);
      assertEquals("correct average", 26d, avg.getAverage(), 0.0001);

      // check we're storing all items
      assertEquals("holding all items", 5, avg.size());

      assertEquals("correctly calculating period", 900, avg.period());

      avg.add(1100, 20);
      assertEquals("holding all items", 6, avg.size());
      assertEquals("correct average", 25d, avg.getAverage(), 0.0001);

      assertEquals("correctly calculating period", 1000, avg.period());

      avg.add(1200, 40);
      assertEquals("holding all items", 6, avg.size());
      assertEquals("correct average", 34d, avg.getAverage(), 0.0001);

      assertEquals("correctly calculating period", 1000, avg.period());
    }
    
    public void testReverse()
    {
      TimeRestrictedMovingAverage avg = new TimeRestrictedMovingAverage(300L);
      
      avg.add(1000, 10);
      assertEquals("correct average", 10d, avg.getAverage(), 0.0001);

      avg.add(900, 20);
      assertEquals("correct average", 15d, avg.getAverage(), 0.0001);

      avg.add(800, 30);
      assertEquals("correct average", 20d, avg.getAverage(), 0.0001);
      assertFalse(avg.isPopulated());

      avg.add(700, 40);
      assertFalse(avg.isPopulated());
      assertEquals("correct average", 25d, avg.getAverage(), 0.0001);

      avg.add(600, 50);
      assertEquals("correct average", 35d, avg.getAverage(), 0.0001);
      assertTrue(avg.isPopulated());
    }
  }
  
  public TimeRestrictedMovingAverage(long millis)
  {
    _millis = millis;
    _times = new Vector<Long>();
    _values = new Vector<Double>();
  }
  
  public boolean isPopulated()
  {
    return _populated;
  }

  public void add(long time, double value)
  {
    // ok, we've moved forwards. Do some deleting
    List<Long> removeTimes = new Vector<Long>();
    List<Double> removeValues = new Vector<Double>();

    for (int i = 0; i < _times.size(); i++)
    {
      Long thisTime = _times.get(i);
      // note: we're using ABS here, since the series
      // of values may be running backwards in time
      if (Math.abs(time - thisTime) > _millis)
      {
        removeTimes.add(thisTime);
        removeValues.add(_values.get(i));
      }
    }

    // and remove them
    if (!removeTimes.isEmpty())
    {
      if(!_populated)
      {
        _populated = true;
      }
      
      _times.removeAll(removeTimes);
      _values.removeAll(removeValues);
    }

    // ok, now add the new items
    _times.add(time);
    _values.add(value);
    
  }
  
  public int size()
  {
    return _times.size();
  }
  
  public long period()
  {
    if(_times.isEmpty())
    {
      return 0;
    }
    else
    {
      return _times.get(_times.size()-1) - _times.get(0);
    }
  }
  
  public boolean isEmpty()
  {
    return _values.isEmpty();
  }

  public double lastValue()
  {
    return _values.get(_values.size()-1);
  }
  
  public double getVariance()
  {
    if (_values.size() > 0)
    {
      double mean = getAverage();
      
      double sum = 0;
      for (Double value : _values)
      {
        sum += Math.pow(mean - value, 2);
      }
      double variance = sum / _values.size();
      return Math.sqrt(variance);
    }
    else
    {
      return 0d;
    }
  }

  public double getAverage()
  {
    if (_values.size() > 0)
    {
      double sum = 0;
      for (Double value : _values)
      {
        sum += value;
      }
      return sum / _values.size();
    }
    else
    {
      return Double.NaN;
    }
  }

  public void clear()
  {
    _times.clear();
    _values.clear();
    _populated = false;
  }
}
