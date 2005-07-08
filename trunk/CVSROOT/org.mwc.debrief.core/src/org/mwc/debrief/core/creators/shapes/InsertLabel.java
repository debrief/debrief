/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import java.awt.Color;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.Tools.Palette.CreateLabel;
import Debrief.Wrappers.LabelWrapper;
import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
public class InsertLabel extends CoreEditorAction
{
	public static ToolParent _theParent = null;

	/**
	 * ok, store who the parent is for the operation
	 * 
	 * @param theParent
	 */
	public static void init(ToolParent theParent)
	{
		_theParent = theParent;
	}

	/**
	 * and execute..
	 */
	protected void run()
	{
		final PlainChart theChart = getChart();

		Action res = getData(theChart);
		
		// ok, now wrap the action
		DebriefActionWrapper daw = new DebriefActionWrapper(res);
		
		// and add it to our buffer (which will execute it anyway)
		CorePlugin.run(daw);

		// res.execute();
		
		

	}

	public final Action getData(PlainChart theChart)
	{
		Action res = null;
		WorldArea wa = theChart.getDataArea();

		// see if we have an area defined
		if (wa != null)
		{

			// get centre of area (at zero depth)
			WorldLocation centre = wa.getCentreAtSurface();

			// and now wrap the shape
			LabelWrapper theWrapper = new LabelWrapper("Blank label", centre,
					Color.red);

			// lastly, get the data
			Layers theData = theChart.getLayers();

			// aah, and the misc layer, in which we will store the shape
			Layer theLayer = theData.findLayer("Misc");

			// did we find it?
			if (theLayer == null)
			{
				// nope, better create it.
				theLayer = new BaseLayer();
				theLayer.setName("Misc");
				theData.addThisLayer(theLayer);
			}

			// and put it into an action (so we can undo it)
			res = new CreateLabel.CreateLabelAction(null, theLayer, theWrapper,
					theChart.getLayers());
		}
		else
		{
			// we haven't got an area, inform the user
			CorePlugin.showMessage("Create Feature",
							"Sorry, we can't create a label until the area is defined.  Try adding a coastline first");
		}

		return res;
	}

}