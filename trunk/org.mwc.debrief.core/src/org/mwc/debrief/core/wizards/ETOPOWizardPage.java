package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.SpatialRasterPainter.KeyLocationPropertyEditor;
import MWC.GUI.Tools.Palette.CreateTOPO;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class ETOPOWizardPage extends CorePlottableWizardPage
{
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ETOPOWizardPage(ISelection selection)
	{
		super(selection, "etopoPage", "Add Gridded depth data", 
				"This page adds a 2-minute resolution gridded depth layer to your plot", "images/etopo_wizard.gif");
	}

	protected Plottable createMe()
	{
		if(_plottable == null)
			 _plottable = CreateTOPO.load2MinBathyData();
		
		return _plottable;
	}
	
	/**
	 * @return
	 */
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final java.beans.PropertyDescriptor[] descriptors = {
				longProp("KeyLocation", "the current location of the color-key", getPlottable(), KeyLocationPropertyEditor.class),
				prop("ShowLand", "whether to shade land-data", getPlottable()),
				prop("BathyVisible", "whether to show the gridded contours", getPlottable()),
				prop("ContoursVisible", "whether to show the contours", getPlottable()),
		};
		return descriptors;
	}
	

}