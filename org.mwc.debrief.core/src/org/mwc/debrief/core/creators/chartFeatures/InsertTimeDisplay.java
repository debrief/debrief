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
package org.mwc.debrief.core.creators.chartFeatures;

import org.mwc.debrief.core.editors.painters.TimeDisplayPainter;

import MWC.GUI.*;

/**
 * @author ian.mayo
 *
 */
public class InsertTimeDisplay extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
		return new TimeDisplayPainter();
	}

}
