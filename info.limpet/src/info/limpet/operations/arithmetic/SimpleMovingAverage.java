/*****************************************************************************
 *  Limpet - the Lightweight InforMation ProcEssing Toolkit
 *  http://limpet.info
 *
 *  (C) 2015-2016, Deep Blue C Technologies Ltd
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the Eclipse Public License v1.0
 *  (http://www.eclipse.org/legal/epl-v10.html)
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *****************************************************************************/
package info.limpet.operations.arithmetic;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Source: http://rosettacode.org/wiki/Averages/Simple_moving_average#Java
 */
public class SimpleMovingAverage
{

  private final Queue<Double> window = new LinkedList<Double>();
  private final int period;
  private double sum;

  public SimpleMovingAverage(final int period)
  {
    assert period > 0 : "Period must be a positive integer";
    this.period = period;
  }

  public double getAvg()
  {
    if (window.isEmpty())
    {
      return 0; // technically the average is undefined
    }
    else
    {
      return sum / window.size();
    }
  }

  public void newNum(final double num)
  {
    sum += num;
    window.add(num);
    if (window.size() > period)
    {
      sum -= window.remove();
    }
  }

}
