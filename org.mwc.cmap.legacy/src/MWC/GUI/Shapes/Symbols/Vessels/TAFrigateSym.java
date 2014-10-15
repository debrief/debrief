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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.GUI.Shapes.Symbols.Vessels;

import java.util.Vector;

public class TAFrigateSym extends ScreenScaledSym
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** initialise, and set the scale factor
	 * 
	 */	
	public TAFrigateSym()
	{
		super();
		// set the scale
		this.setScaleVal(2);
	}


	protected Vector<double[][]> getCoords()
	{
		final Vector<double[][]> hullLines = new Vector<double[][]>();

		// start with the hull
		hullLines.add(new double[][]
		                       		{
		                       		{ -4, -2 },
		                       		{ -4, 2 },
		                       		{ 4, -2 },
		                       		{ 4, 2 },
		                       		{ -4, -2 }});

		// now the keel
		hullLines.add(new double[][]
			                       		{
			                       		{ -4, 2 }, { 0, 5 }, { 4, 2 } });

		// and the tail
		hullLines.add(new double[][]	{
		{ 0,	5},
		{ -0.8,	5.3},
		{ -1,	6},
		{ -0.8,	6.7},
		{ 0,	7},
		{ 0.8,	7.3},
		{ 1,	8},
		{ 0.8,	8.7},
		{ 0,	9 },
		{ -0.4,	9.1 }
		});
	
		return hullLines;

	}

	/**
	 * getType
	 * 
	 * @return the returned String
	 */
	public String getType()
	{
		return "TA Frigate";
	}

}
