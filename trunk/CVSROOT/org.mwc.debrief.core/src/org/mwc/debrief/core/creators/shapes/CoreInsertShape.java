/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import java.awt.Color;

import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.operations.DebriefActionWrapper;
import org.mwc.cmap.plotViewer.actions.CoreEditorAction;

import Debrief.Tools.Palette.CreateShape;
import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.*;
import MWC.GUI.Shapes.PlainShape;
import MWC.GUI.Tools.Action;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertShape extends CoreEditorAction
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

			// create the shape, based on the centre
			PlainShape shape = getShape(centre);

			// and now wrap the shape
			ShapeWrapper theWrapper = new ShapeWrapper("New " + getShapeName(), shape,
					Color.red, null);

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
			res = new CreateShape.CreateShapeAction(null, theLayer, theWrapper,
					theChart.getLayers());
		}
		else
		{
			// we haven't got an area, inform the user
			CorePlugin.showMessage("Create Feature",
							"Sorry, we can't create a shape until the area is defined.  Try adding a coastline first");
		}

		return res;
	}


	/**
	 * @return
	 */
	protected String getLayerName()
	{
		final String myLayer = "Misc";
		return myLayer;
	}

	/** produce the shape for the user
	 * 
	 * @param centre the current centre of the screen
	 * @return a shape, based on the centre
	 */	
	abstract protected PlainShape getShape(WorldLocation centre);
	

	/** return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	abstract protected String getShapeName();	
}