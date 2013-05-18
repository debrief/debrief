package com.planetmayo.debrief.satc.model.generator.impl.bf;

import java.util.Date;
import java.util.List;

import com.planetmayo.debrief.satc.model.legs.AlteringRoute;
import com.planetmayo.debrief.satc.model.legs.CoreLeg;
import com.planetmayo.debrief.satc.model.legs.CoreRoute;
import com.planetmayo.debrief.satc.model.legs.StraightRoute;
import com.vividsolutions.jts.geom.Point;

public class LegWithRoutes
{
	private final static int MAX_PERMS = 50000;

	private CoreLeg leg;
	
	private CoreRoute[][] routes;
	
	public LegWithRoutes(CoreLeg leg) 
	{
		this.leg = leg;
		routes = new CoreRoute[leg.getStartPoints().size()][leg.getEndPoints().size()];
		generateRoutes();
	}
	
	private void generateRoutes() 
	{
		int perms = 0;
		List<Point> startPoints = leg.getStartPoints();
		List<Point> endPoints = leg.getEndPoints();
		int startLength = startPoints.size();
		int endLength = endPoints.size();
		for (int i = 0; i < startLength; i++) 
		{
			for (int j = 0; j < endLength; j++)
			{
				String name = leg.getName() + "_" + perms;
				routes[i][j] = createRoute(startPoints.get(i), endPoints.get(j), name);
				perms++;
				if (perms >= MAX_PERMS) 
				{
					return;
				}
			}
		}
	}
	
	private CoreRoute createRoute(Point start, Point end, String name)
	{
		Date startDate = leg.getFirst().getTime();
		Date endDate = leg.getLast().getTime();
		CoreRoute result = null;
		switch (leg.getType()) 
		{
			case STRAIGHT:
				result = new StraightRoute(name, start, startDate, end, endDate);
				break;
			case ALTERING:
				result = new AlteringRoute(name, start, startDate, end, endDate);
				break;
		}
		if (result != null)
		{
			result.generateSegments(leg.getStates());
		}
		return result;
	}
	
	public void decideAchievableRoutes() 
	{
		for (int i = 0; i < routes.length; i++) 
		{
			for (CoreRoute route : routes[i])
			{
				if (route != null) 
				{
					leg.decideAchievableRoute(route);
				}
			}
		}
	}
	
	/**
	 * represent this set of routes as an integer matrix, with 1 for achievable
	 * and 0 for not achievable
	 * 
	 * @return
	 */
	public int[][] asMatrix()
	{
		int xLen = routes.length;
		int yLen = routes[0].length;
		int[][] res = new int[xLen][yLen];
		for (int x = 0; x < xLen; x++)
			for (int y = 0; y < yLen; y++)
			{
				CoreRoute thisR = routes[x][y];
				boolean isPoss;
				if(thisR == null)
					isPoss = false;
				else
					isPoss = thisR.isPossible();
				
				res[x][y] = (isPoss ? 1 : 0);
			}
		return res;
	}
	
	public CoreRoute[][] getRoutes() 
	{
		return routes;
	}
	
	public CoreLeg getLeg()
	{
		return leg;
	}

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
}
