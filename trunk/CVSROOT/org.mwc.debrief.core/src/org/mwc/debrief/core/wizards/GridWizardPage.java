package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Plottable;
import MWC.GUI.Chart.Painters.*;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class GridWizardPage extends CorePlottableWizardPage
{
	

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public GridWizardPage(ISelection selection)
	{
		super(selection, "gridPage", "Add Grid to Plot", 
				"This page adds a grid to your plot");
	}

	protected Plottable createMe()
	{
		if(_plottable == null)
			 _plottable = new GridPainter();
		
		return _plottable;
	}
	
	/**
	 * @return
	 */
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		PropertyDescriptor[] descriptors = {
        prop("Color", "the Color to draw the grid", getPlottable()),
        prop("PlotLabels", "whether to plot grid labels", getPlottable()),
        prop("Delta", "the step size for the grid", getPlottable())
		};
		return descriptors;
	}
	

}