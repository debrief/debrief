package com.planetmayo.debrief.satc.model.legs;

import java.util.ArrayList;

import com.planetmayo.debrief.satc.model.states.BoundedState;

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

	// /**
	// * the route permutations through the leg. This array will always be
	// * rectangular
	// */
	// protected CoreRoute[][] myRoutes;

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
	
	/**
	 * find out if this is straight or altering
	 * 
	 * @return
	 */
	abstract public LegType getType();

	/**
	 * produce the set of constituent routes for this leg
	 * 
	 * @param gridNum
	 *          how many grid cells to dissect the area into
	 */
	abstract public void generateRoutes(int gridNum);

	/**
	 * determine which legs are achievable
	 * 
	 */
	abstract public void decideAchievableRoutes();

	abstract public CoreRoute[][] getRoutes();

}