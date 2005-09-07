package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.debrief.core.creators.chartFeatures.SWTCoastPainter;

import MWC.GUI.Plottable;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class CoastWizardPage extends CorePlottableWizardPage
{
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CoastWizardPage(ISelection selection)
	{
		super(selection, "coastPage", "Add Coastline to Plot", 
				"This page adds a low resolution coastline to your plot");
	}

	protected Plottable createMe()
	{
		if(_plottable == null)
			 _plottable = new SWTCoastPainter();
		
		return _plottable;
	}
	
	/**
	 * @return
	 */
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
        prop("Color", "the Color to draw the coast", getPlottable())				
		};
		return descriptors;
	}
	

}