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
package info.limpet.impl;


public class Range
{

	private Number _min;
	private Number _max;

	public Range(Number min,
			Number max)
	{
		_min = min;
		_max = max;
	}

	public Number getMinimum()
	{
		return _min;
	}

	public Number getMaximum()
	{
		return _max;
	}

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof Range)
    {
      Range other = (Range) obj;
      return (_min.equals(other._min) && _max.equals(other._max));
    }
    else
    {
      return false;
    }
  }


}
