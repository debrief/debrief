/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import java.awt.Color;

import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.*;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class InsertLabel extends CoreInsertShape
{

	/** get a plottable object
	 * 
	 * @param centre
	 * @param theChart
	 * @return
	 */
	protected Plottable getPlottable(PlainChart theChart)
	{
		
		// right, what's the area we're looking at
		WorldArea wa = theChart.getDataArea();
		
		// get centre of area (at zero depth)
		WorldLocation centre = wa.getCentreAtSurface();

		// and now wrap the shape
		LabelWrapper theWrapper = new LabelWrapper("Blank label", centre,
				Color.red);
		
		return theWrapper;

	}

	@Override
	protected PlainShape getShape(WorldLocation centre)
	{
		// don't bother, we're not generating shapes this way...
		return null;
	}

	@Override
	protected String getShapeName()
	{
		// don't bother, we're not generating shapes this way...
		return null;
	}
}