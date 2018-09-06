/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldDistance;

public class ScaledContainerSym extends WorldScaledSym
{

	public ScaledContainerSym()
	{
		super(new WorldDistance(100, WorldDistance.METRES), new WorldDistance(13,
				WorldDistance.METRES));
	}

	protected double getWidthNormalFactor()
	{
		return 13.4;
	}

	protected double getLengthNormalFactor()
	{
		return -133;
	}

  @Override
  public PlainSymbol create()
  {
    return new ScaledContainerSym();
  }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<double[][]> getCoords()
	{
		final Vector<double[][]> hullLines = new Vector<double[][]>();

		double[][] hullForm;

		// ///////////////////////
		hullForm = new double[][]
		{
		{ 0, -15 },
		{ 1, -14.5 },
		{ 2, -13 },
		{ 2.5, -11 },
		{ 3, -9 },
		{ 3, 0 },
		{ 3, 22 },
		{ 2.5, 28 },
		{ 2.25, 30 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -15 },
		{ -1, -14.5 },
		{ -2, -13 },
		{ -2.5, -11 },
		{ -3, -9 },
		{ -3, 0 },
		{ -3, 22 },
		{ -2.5, 28 },
		{ -2.25, 30 },
		{ 0, 30 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -10 },
		{ 2.5, -10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -11.5 },
		{ 2, -11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0.5, -9 },
		{ 0.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 1.5, -9 },
		{ 1.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2.5, -9 },
		{ 2.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -9 },
		{ 2.5, -9 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -5 },
		{ 2.5, -5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -1 },
		{ 2.5, -1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 3 },
		{ 2.5, 3 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 7 },
		{ 2.5, 7 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 11 },
		{ 2.5, 11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 15 },
		{ 2.5, 15 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 19 },
		{ 2.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 20 },
		{ 2.75, 20 },
		{ 3, 22 },
		{ 0, 22 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 2, 22 },
		{ 1.75, 29 },
		{ 0, 29 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 24 },
		{ 1.5, 24 },
		{ 1.25, 27 },
		{ 0, 27 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -10 },
		{ -2.5, -10 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -11.5 },
		{ -2, -11.5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -0.5, -9 },
		{ -0.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -1.5, -9 },
		{ -1.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2.5, -9 },
		{ -2.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -9 },
		{ -2.5, -9 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -5 },
		{ -2.5, -5 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, -1 },
		{ -2.5, -1 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 3 },
		{ -2.5, 3 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 7 },
		{ -2.5, 7 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 11 },
		{ -2.5, 11 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 15 },
		{ -2.5, 15 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 19 },
		{ -2.5, 19 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 20 },
		{ -2.75, 20 },
		{ -3, 22 },
		{ 0, 22 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ -2, 22 },
		{ -1.75, 29 },
		{ 0, 29 } };
		hullLines.add(hullForm);

		hullForm = new double[][]
		{
		{ 0, 24 },
		{ -1.5, 24 },
		{ -1.25, 27 },
		{ 0, 27 } };
		hullLines.add(hullForm);

		// done
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "ScaledContainer";
	}

}
