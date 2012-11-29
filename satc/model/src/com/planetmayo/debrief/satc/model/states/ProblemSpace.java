package com.planetmayo.debrief.satc.model.states;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;

public class ProblemSpace
{
	/**
	 * this set of bounded states
	 * 
	 */
	private TreeSet<BoundedState> _boundedStates;

	public static final String VEHICLE_TYPE = "vType";
	
	/**
	 * the performance characeristics of the subject vehicle
	 * 
	 */
	private VehicleType _vType;

	public ProblemSpace()
	{
		_boundedStates = new TreeSet<BoundedState>();
	}

	/**
	 * set the subject vehicle type
	 * 
	 * @param vType
	 */
	public void setVehicleType(VehicleType vType)
	{
		_vType = vType;
	}

	/**
	 * get the subject vehicle type
	 * 
	 * @return
	 */
	public VehicleType getVehicleType()
	{
		return _vType;
	}

	/**
	 * add a new bounded state
	 * 
	 * @param newState
	 */
	public void add(BoundedState newState) throws IncompatibleStateException
	{

		// check if this has a date - if it doesn't we'll give it our start/end
		// times
		if (newState.getTime() == null)
		{
			if (size() == 0)
				throw new RuntimeException(
						"we can't accept a null time state, since we don't know our period yet");

			// ok, we'll just apply this state to our start and end times
			_boundedStates.first().constrainTo(newState);
			_boundedStates.last().constrainTo(newState);
		}
		else
			_boundedStates.add(newState);

		// ok, constrain the new state to our vehicle performance, if we have one
		if (_vType != null)
		{
			SpeedRange sr = new SpeedRange(_vType.getMinSpeed(), _vType.getMaxSpeed());
			newState.constrainTo(sr);
		}
	}

	/**
	 * forget our set of bounded states
	 * 
	 */
	public void clear()
	{
		_boundedStates.clear();
	}

	/**
	 * return the bounded state at this time (or null)
	 * 
	 * @param theTime
	 *          the time we're searching for
	 * @return
	 */
	public BoundedState getBoundedStateAt(Date theTime)
	{
		BoundedState res = null;
		Iterator<BoundedState> iter = _boundedStates.iterator();
		while (iter.hasNext())
		{
			BoundedState boundedState = iter.next();
			if (boundedState.getTime().equals(theTime))
			{
				res = boundedState;
				break;
			}
		}

		return res;
	}

	protected Date getFinishDate()
	{
		Date res = null;
		if (size() > 0)
		{
			res = _boundedStates.last().getTime();
		}

		return res;
	}

	protected Date getStartDate()
	{
		Date res = null;
		if (size() > 0)
		{
			res = _boundedStates.first().getTime();
		}

		return res;
	}

	public int size()
	{
		return _boundedStates.size();
	}

	/**
	 * iterator through the set of bounded states
	 * 
	 * @return
	 */
	public Collection<BoundedState> states()
	{
		return _boundedStates;
	}

}
