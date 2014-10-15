/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package com.planetmayo.debrief.satc.model;

public enum Precision
{
	LOW("Low", 100), MEDIUM("Medium", 200), HIGH("High", 300);

	private final String label;
	private final int numPoints;

	private Precision(String label, int numPoints)
	{
		this.label = label;
		this.numPoints = numPoints;
	}

	public String getLabel()
	{
		return label;
	}
	
	/** find out how many points should be generated when gridding 
	 * a location bounds
	 * 
	 * @return
	 */
	public int getNumPoints() 
	{
		return numPoints;
	}
}
