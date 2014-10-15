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
package ASSET.GUI.Workbench.Plotters;

import java.util.Enumeration;

import ASSET.Models.Vessels.Radiated.RadiatedCharacteristics;
import MWC.GUI.Editable;
import MWC.GUI.Layer;

public class RadCharsPlottable extends BasePlottable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RadCharsPlottable(RadiatedCharacteristics chars, Layer parentLayer)
	{
		super(chars, parentLayer);
	}

	public Enumeration<Editable> elements()
	{
		return null;
	}
	
}
