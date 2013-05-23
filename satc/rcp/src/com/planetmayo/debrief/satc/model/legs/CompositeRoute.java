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
