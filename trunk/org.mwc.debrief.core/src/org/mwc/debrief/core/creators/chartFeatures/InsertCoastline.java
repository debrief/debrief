/**
 * 
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
