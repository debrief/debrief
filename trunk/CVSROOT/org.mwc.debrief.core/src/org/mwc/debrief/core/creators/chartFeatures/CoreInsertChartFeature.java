/**
 * 
 */
package org.mwc.debrief.core.creators.chartFeatures;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import MWC.GUI.*;
import MWC.GUI.Tools.Action;
import MWC.GUI.Tools.Palette.PlainCreate;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertChartFeature extends CoreEditorAction
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
	protected void execute()
	{
		final PlainChart theChart = getChart();

		Action res = getData(theChart);

		// ok, now wrap the action
		DebriefActionWrapper daw = new DebriefActionWrapper(res, theChart.getLayers());

		// and add it to our buffer (which will execute it anyway)
		CorePlugin.run(daw);

		// res.execute();

	}

	public Action getData(PlainChart theChart)
	{
		Action res = null;

		// create the shape, based on the centre
		Plottable myFeature = getPlottable(theChart);

		// lastly, get the data
		Layers theData = theChart.getLayers();

		// ok, get our layer name
		final String myLayer = getLayerName();

		// aah, and the misc layer, in which we will store the shape
		Layer theLayer = theData.findLayer(myLayer);

		// did we find it?
		if (theLayer == null)
		{
			// nope, better create it.
			theLayer = new BaseLayer();
			theLayer.setName(myLayer);
			theData.addThisLayer(theLayer);
		}

		// and put it into an action (so we can undo it)
		res = new PlainCreate.CreateLabelAction(null, theLayer, theChart.getLayers(),
				myFeature);

		return res;
	}

	/**
	 * ok, create whatever we're after
	 * 
	 * @param theChart
	 * @return
	 */
	abstract protected Plottable getPlottable(PlainChart theChart);

	/**
	 * @return
	 */
	protected String getLayerName()
	{
		final String myLayer = Layers.CHART_FEATURES;
		return myLayer;
	}
}