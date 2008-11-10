/**
 * 
 */
package org.mwc.debrief.core.creators.shapes;

import java.util.*;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.mwc.debrief.core.creators.chartFeatures.CoreInsertChartFeature;

import Debrief.Wrappers.ShapeWrapper;
import MWC.GUI.*;
import MWC.GUI.Shapes.PlainShape;
import MWC.GenericData.*;

/**
 * @author ian.mayo
 */
abstract public class CoreInsertShape extends CoreInsertChartFeature
{

	private static final String NEW_LAYER_COMMAND = "[Add new layer...]";
	/** the target layer where we dump new items
	 * 
	 */
	private static final String DEFAULT_TARGET_LAYER = "Misc";



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

		// create the shape, based on the centre
		PlainShape shape = getShape(centre);

		// and now wrap the shape
		ShapeWrapper theWrapper = new ShapeWrapper("New " + getShapeName(), shape,
				PlainShape.DEFAULT_COLOR, null);
		
		return theWrapper;

	}
	
	/**
	 * @return
	 */
	protected String getLayerName()
	{
		String res = null;
		// ok, are we auto-deciding? 
		if(!AutoSelectTarget.getAutoSelectTarget())
		{
			// nope, just use the default layer
			res = DEFAULT_TARGET_LAYER;
		}
		else
		{
			// ok, get the non-track layers for the current plot
			
			// get the current plot
			final PlainChart theChart = getChart();
			
			// get the non-track layers
			Layers theLayers = theChart.getLayers();
			final String[] ourLayers = trimmedLayers(theLayers);
			
			// popup the layers in a question dialog
			IStructuredContentProvider theVals = new ArrayContentProvider();
			ILabelProvider theLabels = new LabelProvider();

			// collate the dialog
			ListDialog list = new ListDialog(Display.getCurrent().getActiveShell());
			list.setContentProvider(theVals);
			list.setLabelProvider(theLabels);
			list.setInput(ourLayers);
			list.setMessage("Please select the destination layer for new feature");
			list.setTitle("Adding new drawing feature");
			list.setHelpAvailable(false);
			
			// open it
			int selection = list.open();
			
			// did user say yes?
			if(selection != ListDialog.CANCEL)
			{
				// yup, store it's name
				Object[] val = list.getResult();
				res = val[0].toString();
				
				// hmm, is it our add layer command?
				if(res == NEW_LAYER_COMMAND)
				{
					// better create one.  Ask the user
					
					// create input box dialog
					InputDialog inp = new InputDialog(Display.getCurrent().getActiveShell(), "New layer",
							"Enter name for new layer", "name here...", null);
					
					// did he cancel?
					if(inp.open() == InputDialog.OK)
					{
						// get the results
						String txt = inp.getValue();

						// check there's something there
						if(txt.length() > 0)
						{
							res = txt;
						// create base layer
						Layer newLayer = new BaseLayer();
						newLayer.setName(res);
												
						// add to layers object
						theLayers.addThisLayer(newLayer);
						}
						else
						{
							res = null;
						}
					}
					else
					{
						res = null;
					}
				}
			}
		}
		
		return res;
	}

	/** find the list of layers that could receive a new gui item. We don't
	 * want to add a rectangle to a track, silly.
	 * @param theLayers the list to search through
	 * @return receptive layers (those derived from BaseLayer).
	 */
	private String[] trimmedLayers(Layers theLayers)
	{
		Vector<String> res = new Vector<String>(0,1);
		Enumeration<Editable> enumer = theLayers.elements();
		while(enumer.hasMoreElements())
		{
			Layer thisLayer = (Layer) enumer.nextElement();
			if(thisLayer instanceof BaseLayer)
			{
				res.add(thisLayer.getName());
			}
		}
		
		res.add(NEW_LAYER_COMMAND);
		
		String[] sampleArray = new String[]{"aa"};
		return res.toArray(sampleArray);
	}

	/**
	 * produce the shape for the user
	 * 
	 * @param centre
	 *          the current centre of the screen
	 * @return a shape, based on the centre
	 */
	abstract protected PlainShape getShape(WorldLocation centre);
	


	/**
	 * return the name of this shape, used give the shape an initial name
	 * 
	 * @return the name of this type of shape, eg: rectangle
	 */
	abstract protected String getShapeName();
}