/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.mwc.cmap.gridharness.data;

import org.mwc.cmap.core.property_support.EditorHelper;


public class GriddableItemDescriptor {

	private final String _myTitle;
	private final String _myName;
	private final Class<?> _myTypeClass;
	
	private final EditorHelper _myEditor;

	public GriddableItemDescriptor(final String title, final String name, final Class<?> typeClass, 
			final EditorHelper editor) {
		_myTitle = title;
		_myName = name;
		_myTypeClass = typeClass;
		_myEditor = editor;
	}

	public String getTitle() // used for column title
	{
		return _myTitle;
	}

	/**
	 * used to access the attribute getter/setter (as used in Bean descriptors)
	 * 
	 * @return
	 */
	public String getName() {
		return _myName;
	}
	
	public Class<?> getType(){
		return _myTypeClass;
	}

	/**
	 * editor used for items in this column
	 * 
	 * @return
	 */
	public EditorHelper getEditor() {
		return _myEditor;
	}
	
}
