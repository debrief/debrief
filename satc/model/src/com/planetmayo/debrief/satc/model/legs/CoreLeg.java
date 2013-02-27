package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.states.BoundedState;

public abstract class CoreLeg
{
	/**
	 * the route permutations through the leg. This array will always be
	 * rectangular
	 */
	protected StraightRoute[][] myRoutes;

	/**
	 * how many points there are in the start polygon
	 * 
	 */
	protected int _startLen;

	/**
	 * how many points there are in the end polygon
	 * 
	 */
	protected int _endLen;

	/**
	 * a name for the leg
	 * 
	 */
	protected final String _name;

	/**
	 * the set of bounded states
	 * 
	 */
	protected final ArrayList<BoundedState> _states;

	protected CoreLeg(String name, ArrayList<BoundedState> states)
	{
		_states = states;
		_name = name;

	}

	/**
	 * add this bounded state
	 * 
	 * @param thisS
	 */
	public void add(BoundedState thisS)
	{
		_states.add(thisS);
		if (myRoutes != null)
			throw new IllegalArgumentException("Cannot add new state once gridded");
	}

	protected BoundedState getFirst()
	{
		return _states.get(0);
	}

	protected BoundedState getLast()
	{
		return _states.get(_states.size() - 1);
	}

	abstract public void decideAchievableRoutes();

	/** produce the set of constituent routes for this leg
	 * 
	 * @param gridNum
	 *          how many grid cells to dissect the area into
	 */
	abstract public void generateRoutes(int gridNum);
}
