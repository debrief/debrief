package org.mwc.debrief.core.ContextOperations;

import java.util.Vector;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.property_support.RightClickSupport.RightClickContextItemGenerator;

import MWC.GUI.*;
import MWC.GenericData.WatchableList;

/** embedded class to generate menu-items for creating tactical plot
 * 
 */
public class XYPlotGeneratorButtons implements RightClickContextItemGenerator 
{

	/** add items to the popup menu (if suitable tracks are selected)
	 * 
	 * @param parent
	 * @param theLayers
	 * @param parentLayers
	 * @param subjects
	 */
	public void generate(IMenuManager parent, 
			Layers theLayers, 
			Layer[] parentLayers, 
			Editable[] subjects)
	{
		final Vector<Editable> candidates = new Vector<Editable>(0,1);
		boolean duffItemFound = false;
		
		// right, go through the items and have a nice look at them
		for (int i = 0; i < subjects.length; i++)
		{
			Editable thisE = subjects[i];
			
			// is this one we can watch?
			if(thisE instanceof WatchableList)
			{
				// cool, go for it
				candidates.add(thisE);
			}
			else
				duffItemFound = true;
		}
		
		if(duffItemFound)
		{
			String txt = "Sorry, not all items are suitable data-sources for an xy plot";
			MessageDialog.openInformation(
					 Display.getCurrent().getActiveShell(),
					"XY Plot",
					txt);
			return;
		}
		
		Action viewPlot = new Action("View XY plot"){
			public void run()
			{
				// ok, sort out what we're plotting
				
				// what calculation?
				
				// who is the primary?
				
				// and open the view (with it's data)
			}};
			
		parent.add(new Separator());
		parent.add(viewPlot);
		
	}

}