/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.LocalGridPainter;
import MWC.GenericData.WorldLocation;

/**
 * @author ian.mayo
 *
 */
public class InsertLocalGrid extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
	  final WorldLocation theOrigin = new WorldLocation(theChart.getCanvas().getProjection().getVisibleDataArea().getCentre());
    final LocalGridPainter res = new LocalGridPainter();
    res.setOrigin(theOrigin);
    return res;
	}

}
