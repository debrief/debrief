/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.core.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;

import MWC.GUI.Editable;
import MWC.GUI.Chart.Painters.ScalePainter;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class ScaleWizardPage extends CoreEditableWizardPage
{

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public ScaleWizardPage(final ISelection selection)
	{
		super(selection, "scalePage", "Add Scale to Plot",
				"This page adds a scale to your plot", "images/scale_wizard.gif", null);
	}

	@Override
	protected Editable createMe()
	{
		if (_editable == null)
			_editable = new ScalePainter();

		return _editable;
	}

	/**
	 * @return
	 */
	@Override
	protected PropertyDescriptor[] getPropertyDescriptors()
	{
		final PropertyDescriptor[] descriptors =
		{
				prop("Color", "the Color to draw the Scale", getEditable()),
				longProp("DisplayUnits", "the units to use for the scale",
						getEditable(), MWC.GUI.Properties.UnitsPropertyEditor.class),
				longProp("Location", "the scale location", getEditable(),
						MWC.GUI.Properties.DiagonalLocationPropertyEditor.class) };
		return descriptors;
	}

}