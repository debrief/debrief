package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import com.planetmayo.debrief.satc.model.contributions.BaseContribution;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.model.states.SpeedRange;
import com.planetmayo.debrief.satc.model.states.State;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.util.AffineTransformation;

public class StraightLeg extends CoreLeg
{
	/*
	 * the route permutations through the leg This array will always be
	 * rectangular
	 */
	StraightRoute[][] myRoutes;

	/**
	 * the routes, sorted according to their performance
	 * 
	 */
	SortedSet<CoreRoute> topRoutes;

	/**
	 * create a straight leg.
	 * 
	 * @param name
	 *          what to call the leg
	 * @param states
	 *          the set of bounded states that comprise the leg
	 */
	public StraightLeg(String name, ArrayList<BoundedState> states)
	{
		super(name, states);
	}

	@Override
	protected void createAndStoreLeg(ArrayList<Point> startP,
			ArrayList<Point> endP, int i, int j, String thisName)
	{
		StraightRoute newRoute = new StraightRoute(thisName, startP.get(i),
				getFirst().getTime(), endP.get(j), getLast().getTime());

		// tell the route to decimate itself
		newRoute.generateSegments(_states);

		// and store the route
		myRoutes[i][j] = newRoute;
	}

	/**
	 * work out what if the route is achievable, by scaling the locations to check
	 * that they overlap
	 * 
	 * @author Ian
	 * 
	 */
	public static class ScaledPolygons implements RouteOperator
	{

		private final ArrayList<BoundedState> _myStates;

		public ScaledPolygons(ArrayList<BoundedState> states)
		{
			_myStates = states;
		}

		@Override
		public void process(StraightRoute theRoute)
		{
			// do we already know this isn't possible?
			if (!theRoute.isPossible())
				return;

			// bugger, we'll have to get on with our hard sums then

			// sort out the origin.
			State startState = theRoute.getStates().get(0);
			Coordinate startCoord = startState.getLocation().getCoordinate();

			// also sort out the end state
			State endState = theRoute.getStates()
					.get(theRoute.getStates().size() - 1);
			Point endPt = endState.getLocation();

			// remeber the start time
			long tZero = startState.getTime().getTime();

			// how long is the total run?
			long elapsed = theRoute.getElapsedTime();

			// allow for multiple fidelity processing
			int ctr = 0;

			// how frequently shall we process the polygons?
			// calculating the scaled polygons is really expensive. this
			// give us most of the benefits, at a third of the speed (well,
			// with a freq of '3' it does)
			final int freq = 3;

			// loop through our states
			Iterator<BoundedState> iter = _myStates.iterator();
			while (iter.hasNext())
			{
				BoundedState thisB = iter.next();

				LocationRange thisL = thisB.getLocation();
				if (thisL != null)
				{
					// ok, what's the time difference
					long thisDelta = thisB.getTime().getTime() - tZero;

					// convert to secs
					long tDelta = thisDelta / 1000;

					// is this our first state
					if (tDelta > 0)
					{
						// ok, we've got a location - increment the counter
						ctr++;

						// is this one we're going to process?
						if (((ctr % freq) == 0) || ctr == _myStates.size() - 1)
						{
							double scale = (1d * elapsed) / tDelta;

							// ok, project the shape forwards
							AffineTransformation st = AffineTransformation.scaleInstance(
									scale, scale, startCoord.x, startCoord.y);

							// ok, apply the transform to the location
							Geometry originalGeom = thisL.getGeometry();
							Geometry newGeom = st.transform(originalGeom);

							// see if the end point is in the new geometry
							if (endPt.coveredBy(newGeom))
							{
								// cool, this route works
							}
							else
							{
								// bugger, can't do this one
								theRoute.setImpossible();
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * use a simple speed/time decision to decide if it's possible to navigate a
	 * route
	 */
	public void decideAchievableRoutes()
	{
		StraightLeg.RouteOperator speed = new RouteOperator()
		{

			@Override
			public void process(StraightRoute theRoute)
			{
				double distance = theRoute.getDistance();
				double elapsed = theRoute.getElapsedTime();
				double speed = distance / elapsed;

				SpeedRange speedR = _states.get(0).getSpeed();
				if (speedR == null)
				{
					// hey, we can't calculate it then
				}
				else if (!speedR.allows(speed))
				{
					theRoute.setImpossible();
				}
			}
		};

		StraightLeg.RouteOperator scaledPolygons = new ScaledPolygons(_states);

		applyToRoutes(speed);
		applyToRoutes(scaledPolygons);
	}

	/**
	 * apply the operator to all my routes
	 * 
	 * @param operator
	 */
	public void applyToRoutes(StraightLeg.RouteOperator operator)
	{
		for (int i = 0; i < _startLen; i++)
		{
			for (int j = 0; j < _endLen; j++)
			{
				StraightRoute theRoute = myRoutes[i][j];
				if (theRoute != null)
					operator.process(theRoute);
			}
		}
	}

	/**
	 * find out how many achievable routes there are through the area
	 * 
	 * @return how many
	 */
	public int getNumAchievable()
	{

		StraightLeg.CountPossible isPossible = new CountPossible();
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
		public void process(StraightRoute theRoute);
	}

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
	private static class CountPossible implements StraightLeg.RouteOperator
	{
		int res = 0;

		@Override
		public void process(StraightRoute theRoute)
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
		return LegType.STRAIGHT;
	}

	/**
	 * run through all the route permutation, and find the one with the highest
	 * score(s)
	 * 
	 */
	public void calculateRouteScores(final Collection<BaseContribution> contribs)
	{
		// ok, we need to be able to sort our routes in descending order
		Comparator<CoreRoute> comparator = new Comparator<CoreRoute>()
		{
			@Override
			public int compare(CoreRoute arg0, CoreRoute arg1)
			{
				Double score1 = arg0.getScore();
				Double score2 = arg1.getScore();
				return score1.compareTo(score2);
			}
		};

		// we also wish to put them in order, so get ready to remember them
		topRoutes = new TreeSet<CoreRoute>(comparator);

		RouteOperator calcError = new RouteOperator()
		{
			@Override
			public void process(StraightRoute theRoute)
			{
				Double thisScore = 0d;

				// is this one achievable?
				if (theRoute.isPossible())
				{
					// loop through the contribs
					Iterator<BaseContribution> iter = contribs.iterator();
					while (iter.hasNext())
					{
						BaseContribution thisC = (BaseContribution) iter.next();
						thisScore += thisC.calculateErrorScoreFor(theRoute);
					}

					theRoute.setScore(thisScore);

					// ok, add it to the list
					topRoutes.add(theRoute);
				}
			}
		};
		applyToRoutes(calcError);
	}

	@Override
	protected void createRouteStructure(int startLen, int endLen)
	{
		// ok, create the array
		myRoutes = new StraightRoute[_startLen][_endLen];
	}

	@Override
	public SortedSet<CoreRoute> getTopRoutes()
	{
		return topRoutes;
	}

}