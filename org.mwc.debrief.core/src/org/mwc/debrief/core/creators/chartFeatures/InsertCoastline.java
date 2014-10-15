/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.debrief.core.creators.chartFeatures;

import MWC.GUI.*;
import MWC.GUI.Chart.Painters.CoastPainter;
import MWC.GenericData.WorldArea;

/**
 * @author ian.mayo
 *
 */
public class InsertCoastline extends CoreInsertChartFeature
{

	/**
	 * @return
	 */
	protected Plottable getPlottable(final PlainChart theChart)
	{
		final CoastPainter cp = new CoastPainter();
		// see if the chart has a data area defined.  If not, make it cover our
		final WorldArea wa = theChart.getDataArea();

		if(wa == null)
		{
			cp.setVisible(true);
			final WorldArea ca = cp.getBounds();
			cp.setVisible(true);
			theChart.getCanvas().getProjection().setDataArea(ca);
		}

		//coastline
		return cp;
	
	}

}
