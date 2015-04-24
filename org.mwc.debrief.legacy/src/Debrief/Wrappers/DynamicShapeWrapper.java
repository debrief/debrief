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
package Debrief.Wrappers;

import java.awt.Color;

import MWC.GUI.Plottable;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.HiResDate;

@SuppressWarnings("serial")
public class DynamicShapeWrapper extends ShapeWrapper
{
	
	private String _theTrackName;

	public DynamicShapeWrapper(String label, PlainShape theShape, Color theColor,
			HiResDate theDate, String name)
	{
		super(label, theShape, theColor, theDate);
		_theTrackName = name;
	}

	public String getTrackName()
	{
		return _theTrackName;
	}

	public void setTrackName(String theTrackName)
	{
		this._theTrackName = theTrackName;
	}
	
	/** override the default sort order (name), since
	 * we wish to sort by DTG
	 */
	public int compareTo(Plottable o)
	{
		final DynamicShapeWrapper sw = (DynamicShapeWrapper) o;
		return this.getStartDTG().compareTo(sw.getStartDTG());
	}
	

}
