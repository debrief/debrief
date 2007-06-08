package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.ScalePainter;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class ScaleWizardPage extends CorePlottableWizardPage
{
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ScaleWizardPage(ISelection selection)
	{
		super(selection, "scalePage", "Add Scale to Plot", 
				"This page adds a scale to your plot", "images/scale_wizard.gif");
	}

	protected Plottable createMe()
	{
		if(_plottable == null)
			 _plottable = new ScalePainter();
		
		return _plottable;
	}
	
	/**
	 * @return
	 */
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
        prop("Color", "the Color to draw the Scale", getPlottable()),
        longProp("DisplayUnits", "the units to use for the scale", getPlottable(),
                 MWC.GUI.Properties.UnitsPropertyEditor.class),
        longProp("Location",
                 "the scale location",  getPlottable(),
                 MWC.GUI.Properties.DiagonalLocationPropertyEditor.class)
		};
		return descriptors;
	}
	
	
}