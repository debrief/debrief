/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
package org.mwc.debrief.track_shift.freq;
import java.util.ArrayList;

import junit.framework.TestCase;

public class Normaliser
{
  public static class TestNormaliser extends TestCase
  {
    public void testFlipped()
    {
      final ArrayList<Double> data = new ArrayList<Double>();
      data.add(12d);
      data.add(3d);
      data.add(5d);
      data.add(103d);
      final Normaliser norm = new Normaliser(data, true);

      assertEquals(3d, norm._min, 0d);
      assertEquals(103d, norm._max, 0d);
      assertEquals(100d, norm._range, 0d);

      assertEquals(1d, norm.normalise(3d), 0d);
      assertEquals(0d, norm.normalise(103d), 0d);
      assertEquals(0.5d, norm.normalise(53d), 0d);

      assertEquals(3d, norm.deNormalise(1d), 0d);
      assertEquals(103d, norm.deNormalise(0d), 0d);
      assertEquals(53d, norm.deNormalise(0.5d), 0d);
    }

    public void testNormal()
    {
      final ArrayList<Double> data = new ArrayList<Double>();
      data.add(12d);
      data.add(3d);
      data.add(5d);
      data.add(103d);
      final Normaliser norm = new Normaliser(data, false);

      assertEquals(3d, norm._min, 0d);
      assertEquals(103d, norm._max, 0d);
      assertEquals(100d, norm._range, 0d);

      assertEquals(0d, norm.normalise(3d), 0d);
      assertEquals(1d, norm.normalise(103d), 0d);
      assertEquals(0.5d, norm.normalise(53d), 0d);

      assertEquals(3d, norm.deNormalise(0d), 0d);
      assertEquals(103d, norm.deNormalise(1d), 0d);
      assertEquals(53d, norm.deNormalise(0.5d), 0d);
    }
  }

  private double _min;
  private double _max;
  private final double _range;

  /**
   * whether we should reverse the axis
   * 
   */
  private final boolean _flipAxis;

  /**
   * 
   * @param items
   *          data to normalise
   * @param flipAxis
   *          whether we should flip the axis (to return data in 1..0 domain)
   */
  public Normaliser(final ArrayList<Double> items, final boolean flipAxis)
  {
    _flipAxis = flipAxis;
    
    boolean first = true;
    for (final double num : items)
    {
      if (first)
      {
        _min = num;
        _max = num;
        first = false;
      }
      else
      {
        if (num < _min)
        {
          _min = num;
        }
        if (num > _max)
        {
          _max = num;
        }
      }
    }

    _range = _max - _min;
  }

  public double deNormalise(final double val)
  {
    if (_flipAxis)
    {
      return (((1 - val) * _range) + _min);
    }
    else
    {
      return (val * _range) + _min;
    }
  }

  public double normalise(final double val)
  {
    if (_flipAxis)
    {
      return 1 - ((val - _min) / _range);
    }
    else
    {
      return (val - _min) / _range;
    }
  }

}
