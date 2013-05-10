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
