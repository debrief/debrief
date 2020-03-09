/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

package org.mwc.debrief.satc_interface.wizards;

import java.beans.PropertyDescriptor;

import org.eclipse.jface.viewers.ISelection;
import org.mwc.cmap.core.wizards.CoreEditableWizardPage;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xml).
 */

public class LegNameWizardPage extends CoreEditableWizardPage {

	public static class NameHolder implements Plottable {
		private String _name = "Pending";

		@Override
		public int compareTo(final Plottable arg0) {
			return 0;
		}

		@Override
		public WorldArea getBounds() {
			return null;
		}

		@Override
		public EditorType getInfo() {
			return null;
			// if (_myEditor == null)
			// _myEditor = new NameInfo(this);
			//
			// return _myEditor;
		}

		@Override
		public String getName() {
			return _name;
		}

		@Override
		public boolean getVisible() {
			return false;
		}

		@Override
		public boolean hasEditor() {
			return false;
		}

		@Override
		public void paint(final CanvasType dest) {

		}

		@Override
		public double rangeFrom(final WorldLocation other) {
			return 0;
		}

		public void setName(final String name) {
			_name = name;
		}

		@Override
		public void setVisible(final boolean val) {

		}

	}

	/**
	 * Constructor for SampleNewWizardPage.
	 *
	 * @param pageName
	 */
	public LegNameWizardPage(final ISelection selection) {
		super(selection, "namePage", "Set Leg Name", "Please provide a name for this leg", "images/scale_wizard.gif",
				null, false, null);
	}

	@Override
	protected Editable createMe() {
		if (_editable == null)
			_editable = new NameHolder();

		return _editable;
	}

	@Override
	public String getName() {
		return _editable.getName();
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {

		final PropertyDescriptor[] res = { prop("Name", "the name for this leg", getEditable()) };

		return res;

	}

}