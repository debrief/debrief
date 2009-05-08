package org.mwc.debrief.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.CoastPainter;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class CoastWizardPage extends CoreEditableWizardPage
{

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CoastWizardPage(ISelection selection)
	{
		super(selection, "coastPage", "Add Coastline to Plot",
				"This page adds a low resolution coastline to your plot",
				"images/coast_wizard.gif");
	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
			_editable = CoastPainter.getCoastPainterDontLoadData();

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final PropertyDescriptor[] descriptors =
		{ prop("Color", "the Color to draw the coast", getEditable()) };
		return descriptors;
	}

}