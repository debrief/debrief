package com.planetmayo.debrief.satc.model.states;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.SortedMap;

import com.planetmayo.debrief.satc.model.VehicleType;
import com.planetmayo.debrief.satc.model.states.BaseRange.IncompatibleStateException;
import com.planetmayo.debrief.satc.support.SupportServices;
import com.planetmayo.debrief.satc.util.ObjectUtils;

public class ProblemSpace
{
	public static final String VEHICLE_TYPE = "vType";

	/**
	 * this map of bounded states, stored by time
	 * 
	 */
	private final SortedMap<Date, BoundedState> _boundedStates;

	/**
	 * the performance characeristics of the subject vehicle
	 * 
	 */
	private volatile VehicleType _vType;

	public ProblemSpace()
	{
		_boundedStates = SupportServices.INSTANCE.getUtilsService().newConcurrentSortedMap();
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
			_boundedStates.get(_boundedStates.firstKey()).constrainTo(newState);
			_boundedStates.get(_boundedStates.lastKey()).constrainTo(newState);
		}
		else
		{
			if (getBoundedStateAt(newState.getTime()) != null)
			{
				throw new IllegalArgumentException("We already have bounded state at "
						+ newState.getTime());
			}
			_boundedStates.put(newState.getTime(), newState);
		}

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
		return _boundedStates.get(theTime);
	}

	/**
	 * return the bounded state at this time (or null)
	 * 
	 * @param theTime
	 *          the time we're searching for
	 * @return
	 */
	public Collection<BoundedState> getBoundedStatesBetween(Date startDate,
			Date finishDate)
	{
		if (_boundedStates.isEmpty())
		{
			return _boundedStates.values();
		}

		// just check that we have non-null dates
		if ((startDate != null) && (finishDate != null))
		{
			// we do have dates, now just check that they're correctly sequenced
			if (!startDate.before(finishDate))
			{
				// ok, start date invalid - just return empty list, so the algs don't
				// trip
				// over
				return new ArrayList<BoundedState>();
			}
		}

		startDate = ObjectUtils.safe(startDate, _boundedStates.firstKey());
		finishDate = ObjectUtils.safe(finishDate, _boundedStates.lastKey());
		// inclusive toKey - gwt compliant version
		finishDate = new Date(finishDate.getTime() + 1);

		return _boundedStates.subMap(startDate, finishDate).values();
	}

	public Date getFinishDate()
	{
		return _boundedStates.isEmpty() ? null : _boundedStates.lastKey();
	}

	public Date getStartDate()
	{
		return _boundedStates.isEmpty() ? null : _boundedStates.firstKey();
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
		return _boundedStates.values();
	}

	/**
	 * provide the date times
	 * 
	 * @return
	 */
	public Collection<Date> times()
	{
		return _boundedStates.keySet();
	}

	public Collection<BoundedState> statesAfter(BoundedState state)
	{
		return _boundedStates.tailMap(new Date(state.getTime().getTime() + 1))
				.values();
	}
}
