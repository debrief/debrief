/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package org.mwc.cmap.gridharness.data;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.mwc.cmap.core.property_support.EditorHelper;


public class GriddableItemDescriptorExtension extends GriddableItemDescriptor implements IAdaptable {

	private final String mySampleString;

	public GriddableItemDescriptorExtension(final String name, final String displayName, final Class<?> typeClass, final EditorHelper editor, final String sampleString) {
		super(name, displayName, typeClass, editor);
		mySampleString = sampleString;
	}

	public String getSampleString() {
		return mySampleString;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
