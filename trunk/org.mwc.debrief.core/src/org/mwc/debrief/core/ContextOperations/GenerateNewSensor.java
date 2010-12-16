package org.mwc.debrief.core.ContextOperations;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;
import org.mwc.debrief.core.DebriefPlugin;

import Debrief.Wrappers.TrackWrapper;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;

/**
 * embedded class to generate menu-items for creating a new sensor
 */
public class GenerateNewSensor implements RightClickContextItemGenerator
{

	/**
	 * add items to the popup menu (if suitable tracks are selected)
	 * 
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(IMenuManager parent, Layers theLayers,
			Layer[] parentLayers, final Editable[] subjects)
	{
		
		// check only one item is selected
		
		Layer host = null;

		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			Editable thisE = subjects[i];

			// is this one we can watch?
			if (thisE instanceof TrackWrapper)
			{
				host = (Layer) thisE;
			}
			else
			{
				System.out.println("obj is:" + thisE);
			}
			// else
			// duffItemFound = true;
		}

		if (host != null)
		{
			{
				// ok, create the action
				Action viewPlot = getAction(host);

				// ok - set the image descriptor
				viewPlot.setImageDescriptor(DebriefPlugin
						.getImageDescriptor("icons/document_chart.png"));

				parent.add(new Separator());
				parent.add(viewPlot);
			}
		}
	}

	/**
	 * wrap the action generation bits in a convenience method (suitable for
	 * overring in tests)
	 * 
	 * @param candidates
	 *          the sensors to measure the range from
	 * @param primary
	 *          the track to measure to
	 * @return
	 */
	protected Action getAction(final Layer parent)
	{
		return new Action("Add new sensor")
		{
			public void run()
			{

				System.out.println("done...");
			}
		};
	}

}