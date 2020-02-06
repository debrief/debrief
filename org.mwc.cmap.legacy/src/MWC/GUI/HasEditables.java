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
package MWC.GUI;

import java.util.Enumeration;

/**
 * interface for a Debrief entity that contains other child elements
 *
 * @author ian
 *
 */
public interface HasEditables extends CanEnumerate {
	/**
	 * interface for class that normally provides it's elements in a tiered fashion,
	 * but is able to provide them as a single list (for when an external class
	 * wants to process all of them as one list - double-click nearest testing).
	 */
	public static interface ProvidesContiguousElements {
		public Enumeration<Editable> contiguousElements();
	}

	void add(Editable point);

	/**
	 * whether the children should be ordered
	 *
	 * @return yes/no
	 */
	boolean hasOrderedChildren();

	void removeElement(Editable point);

}
