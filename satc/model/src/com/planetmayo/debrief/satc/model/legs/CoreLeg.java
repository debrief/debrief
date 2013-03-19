package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;
import java.util.SortedSet;

import com.planetmayo.debrief.satc.model.Precision;
import com.planetmayo.debrief.satc.model.states.BoundedState;
import com.planetmayo.debrief.satc.model.states.LocationRange;
import com.planetmayo.debrief.satc.util.GeoSupport;
import com.planetmayo.debrief.satc.util.MakeGrid;
import com.vividsolutions.jts.geom.Point;

public abstract class CoreLeg
{
	/**
	 * perform matrix multiplication on these two integer arrays taken from:
	 * http://blog.ryanrampersad.com/2010/01/matrix-multiplication-in-java/
	 * 
	 * Tested using: {{2,3},{1,2},{1,1}} multiplied by {{0,2,3},{1,2,0}};
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int[][] multiply(int a[][], int b[][])
	{

		int aRows = a.length, aColumns = a[0].length, bRows = b.length, bColumns = b[0].length;

		if (aColumns != bRows)
		{
			throw new IllegalArgumentException("A:Rows: " + aColumns
					+ " did not match B:Columns " + bRows + ".");
		}

		int[][] resultant = new int[aRows][bColumns];

		for (int i = 0; i < aRows; i++)
		{ // aRow
			for (int j = 0; j < bColumns; j++)
			{ // bColumn
				for (int k = 0; k < aColumns; k++)
				{ // aColumn
					resultant[i][j] += a[i][k] * b[k][j];
				}
			}
		}

		return resultant;
	}

	/**
	 * represent this set of routes as an integer matrix, with 1 for achievable
	 * and 0 for not achievable
	 * 
	 * @return
	 */
	public int[][] asMatrix()
	{
		CoreRoute[][] myRoutes = getRoutes();
		int xLen = myRoutes.length;
		int yLen = myRoutes[0].length;
		int[][] res = new int[xLen][yLen];
		for (int x = 0; x < xLen; x++)
			for (int y = 0; y < yLen; y++)
			{
				boolean isPoss = myRoutes[x][y].isPossible();
				res[x][y] = (isPoss ? 1 : 0);
			}
		return res;
	}

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
	final public void add(BoundedState thisS)
	{
		_states.add(thisS);
	}

	final public BoundedState getFirst()
	{
		return _states.get(0);
	}

	final public BoundedState getLast()
	{
		return _states.get(_states.size() - 1);
	}

	final public String getName()
	{
		return _name;
	}

	final public ArrayList<BoundedState> getStates()
	{
		return _states;
	}

	/**
	 * produce the set of constituent routes for this leg
	 * 
	 * @param precision
	 *          how many grid cells to dissect the area into
	 */
	public void generateRoutes(Precision precision)
	{
		// produce the grid of cells
		LocationRange firstLoc = getFirst().getLocation();
		LocationRange lastLoc = getLast().getLocation();

		if ((firstLoc == null) || (lastLoc == null))
			throw new IllegalArgumentException(
					"The end states must have location bounds");

		final double delta;
		switch (precision)
			{
			case LOW:
				delta = GeoSupport.m2deg(800);
				break;
			case MEDIUM:
				delta = GeoSupport.m2deg(600);
				break;
			case HIGH:
				delta = GeoSupport.m2deg(200);
				break;

			default:
				throw new RuntimeException(
						"We've failed to implement case for a precision type");
			}
		;

		// right, what's the area of the start?
		double startArea = firstLoc.getGeometry().getArea();
		double endArea = lastLoc.getGeometry().getArea();

		final int numStart = (int) (startArea / (delta * delta));
		final int numEnd = (int) (endArea / (delta * delta));

		ArrayList<Point> startP = MakeGrid.ST_Tile(firstLoc.getGeometry(),
				numStart, 6);
		ArrayList<Point> endP = MakeGrid.ST_Tile(lastLoc.getGeometry(), numEnd, 6);

		// just check we've been able to create some points
		if((startP.size() == 0) || (endP.size() == 0))
				throw new RuntimeException("Unable to generate enough points for leg:" + this.getName());
		
		// now calculate the routes through these points
		calculatePerms(startP, endP);
	}

	public void calculatePerms(ArrayList<Point> startP, ArrayList<Point> endP)
	{
		// ok, now generate the array of routes
		_startLen = startP.size();
		_endLen = endP.size();

		// create the target results object
		createRouteStructure(_startLen, _endLen);

		// now populate it
		int ctr = 1;
		for (int i = 0; i < _startLen; i++)
		{
			for (int j = 0; j < _endLen; j++)
			{
				String thisName = _name + "_" + ctr++;
				createAndStoreLeg(startP, endP, i, j, thisName);
			}
		}
	}

	abstract protected void createRouteStructure(int startLen, int endLen);

	/**
	 * find out if this is straight or altering
	 * 
	 * @return
	 */
	abstract public LegType getType();

	/**
	 * determine which legs are achievable
	 * 
	 */
	abstract public void decideAchievableRoutes();

	abstract public CoreRoute[][] getRoutes();

	/**
	 * create a leg of the correct type between these points, and store it
	 * 
	 * @param startP
	 * @param endP
	 * @param i
	 * @param j
	 * @param thisName
	 */
	abstract protected void createAndStoreLeg(ArrayList<Point> startP,
			ArrayList<Point> endP, int i, int j, String thisName);

	/**
	 * return the routes in this leg, in descending order
	 * 
	 * @param i
	 *          how many legs to retrieve
	 * @return
	 */
	abstract public SortedSet<CoreRoute> getTopRoutes();

}