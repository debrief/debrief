/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;

/** a series of straight/altering legs
 * 
 * @author Ian
 *
 */
public class CompositeRoute
{
	private final ArrayList<CoreRoute> _legs;

	public CompositeRoute()
	{
		_legs = new ArrayList<CoreRoute>();
	}
	
	public CompositeRoute(Collection<CoreRoute> legs) 
	{
		_legs = new ArrayList<CoreRoute>();
		for (CoreRoute route : legs)
		{
			if (route != null)
			{
				_legs.add(route);
			}
		}
	}
	
	public double getScore() 
	{
		double sum = 0;
		for (CoreRoute route : _legs)
		{
			sum += route.getScore();
		}
		return sum;
	}
	
	/** add this leg to our data
	 * 
	 * @param topR
	 */
	public void add(CoreRoute topR)
	{
		_legs.add(topR);
	}
	
	/** get the legs that comprise this route
	 * 
	 */
	public Collection<CoreRoute> getLegs()
	{
		return _legs;
	}
}
