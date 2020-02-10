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
package org.mwc.cmap.naturalearth.view;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import MWC.GUI.Editable;
import MWC.GenericData.WorldLocation;

public class NEFeatureStyle extends NEFeature {
	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public final class StyleInfo extends Editable.EditorType {

		public StyleInfo(final NEFeatureStyle data, final String theName) {
			super(data, theName, "Natural Earth");
		}

		/**
		 * whether the normal editable properties should be combined with the additional
		 * editable properties into a single list. This is typically used for a
		 * composite object which has two lists of editable properties but which is seen
		 * by the user as a single object To be overwritten to change it
		 */
		@Override
		public final boolean combinePropertyLists() {
			return true;
		}

		@Override
		public final PropertyDescriptor[] getPropertyDescriptors() {
			try {
				final PropertyDescriptor[] myRes = { prop("Visible", "if the layer is visible", FORMAT), };

				return myRes;

			} catch (final IntrospectionException e) {
				e.printStackTrace();
				return super.getPropertyDescriptors();
			}
		}
	}

	private static final long serialVersionUID = 1L;

	private List<String> _fileNames = new ArrayList<String>();

	/**
	 * the editor settings for this object
	 *
	 */
	private StyleInfo _myEditor;

	public NEFeatureStyle(final String name) {
		super(name);
	}

	public List<String> getFileNames() {
		return _fileNames;
	}

	@Override
	public EditorType getInfo() {
		if (_myEditor == null)
			_myEditor = new StyleInfo(this, getName());
		return _myEditor;
	}

	@Override
	public boolean hasEditor() {
		return true;
	}

	@Override
	public double rangeFrom(final WorldLocation other) {
		return 0;
	}

	public void setFileNames(final List<String> fileNames) {
		this._fileNames = fileNames;
	}
}
