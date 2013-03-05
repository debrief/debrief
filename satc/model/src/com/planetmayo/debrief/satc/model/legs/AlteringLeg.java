package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.vividsolutions.jts.geom.Point;

public class AlteringLeg extends CoreLeg
{
	/*
	 * the route permutations through the leg This array will always be
	 * rectangular
	 */
	AlteringRoute[][] myRoutes;

	/**
	 * create an altering leg.
	 * 
	 * @param name
	 *          what to call the leg
	 * @param states
	 *          the set of bounded states that comprise the leg
	 */
	public AlteringLeg(String name, ArrayList<BoundedState> states)
	{
		super(name, states);

	}

//	@Override
//	public void generateRoutes(int gridNum)
//	{
//		// TODO: generate the routes through the leg?
//		// produce the grid of cells
//		ArrayList<Point> startP = MakeGrid.ST_Tile(getFirst().getLocation()
//				.getGeometry(), gridNum, 6);
//		ArrayList<Point> endP = MakeGrid.ST_Tile(getLast().getLocation()
//				.getGeometry(), gridNum, 6);
//
//		// ok, now generate the array of routes
//		_startLen = startP.size();
//		_endLen = endP.size();
//
//		// now populate it
//		int ctr = 1;
//		for (int i = 0; i < _startLen; i++)
//		{
//			for (int j = 0; j < _endLen; j++)
//			{
//				String thisName = _name + "_" + ctr++;
//			}
//		}
//	}

	/**
	 * use a simple speed/time decision to decide if it's possible to navigate a
	 * route
	 */
	public void decideAchievableRoutes()
	{
		// can we get there in the time available?
		applyToRoutes(new PointAchievable(_states.get(0).getSpeed()));

		// TODO: also do a turning-related test
	}

	static class PointAchievable implements RouteOperator
	{

		private final SpeedRange _speedR;

		public PointAchievable(SpeedRange theSpeed)
		{
			_speedR = theSpeed;
		}

		@Override
		public void process(AlteringRoute theRoute)
		{
			double distance = theRoute.getDirectDistance();
			double elapsed = theRoute.getElapsedTime();
			double speed = distance / elapsed;

			if (!_speedR.allows(speed))
			{
				theRoute.setImpossible();
			}
		}
	}

	/**
	 * apply the operator to all my routes
	 * 
	 * @param operator
	 */
	public void applyToRoutes(AlteringLeg.RouteOperator operator)
	{
		for (int i = 0; i < _startLen; i++)
		{
			for (int j = 0; j < _endLen; j++)
			{
				AlteringRoute thisR = myRoutes[i][j];
				operator.process(thisR);
			}
		}
	}

	/**
	 * s find out how many achievable routes there are through the area
	 * 
	 * @return how many
	 */
	public int getNumAchievable()
	{

		AlteringLeg.CountPossible isPossible = new CountPossible();
		applyToRoutes(isPossible);

		return isPossible.getCount();
	}

	/**
	 * interface for a generic operation that acts on a route
	 * 
	 * @author Ian
	 * 
	 */
	public static interface RouteOperator
	{
		/**
		 * apply the processing to this route
		 * 
		 * @param theRoute
		 */
		public void process(AlteringRoute theRoute);
	}

	@Override
	public CoreRoute[][] getRoutes()
	{
		return myRoutes;
	}

	/**
	 * utility class to count how many routes are possible
	 * 
	 * @author Ian
	 * 
	 */
	private static class CountPossible implements AlteringLeg.RouteOperator
	{
		int res = 0;

		@Override
		public void process(AlteringRoute theRoute)
		{
			if (theRoute.isPossible())
				res++;
		}

		public int getCount()
		{
			return res;
		}
	}

	@Override
	public LegType getType()
	{
		return LegType.ALTERING;
	}

	/**
	 * run through all the route permutation, and find the one with the highest
	 * score(s)
	 * 
	 */
	public void calculateOptimum()
	{
		// TODO calculate an optimal solution through this manoeuvre

	}

	@Override
	protected void createRouteStructure(int startLen, int endLen)
	{
		// ok, create the array
		myRoutes = new AlteringRoute[startLen][endLen];
	}

	@Override
	protected void createAndStoreLeg(ArrayList<Point> startP,
			ArrayList<Point> endP, int i, int j, String thisName)
	{
		// create the appropriate route
		AlteringRoute newRoute = new AlteringRoute(thisName, startP.get(i),
				getFirst().getTime(), endP.get(j), getLast().getTime());

		// and store the route
		myRoutes[i][j] = newRoute;
	}

}