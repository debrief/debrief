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

import org.mwc.cmap.core.property_support.EditorHelper;


public class GriddableItemDescriptor {

	private final String _myTitle;
	private final String _myName;
	private final Class<?> _myTypeClass;
	
	private final EditorHelper _myEditor;

	public GriddableItemDescriptor(final String name, final String displayName, final Class<?> typeClass, 
			final EditorHelper editor) {
		_myTitle = displayName;
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
