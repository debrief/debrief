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
package org.mwc.debrief.core.editors.painters.highlighters;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class NullHighlighter implements SWTPlotHighlighter
{

	public void highlightIt(final PlainProjection proj, final CanvasType dest,
			final WatchableList list, final Watchable watch, final boolean isPrimary)
	{
		// ignore, we don't do anything
	}
	
	/**
	 * the name of this object
	 * 
	 * @return the name of this editable object
	 */
	public final String toString()
	{
		return getName();
	}

	public EditorType getInfo()
	{
		return null;
	}

	public String getName()
	{
		return "Highlight off";
	}

	public boolean hasEditor()
	{
		return false;
	}

}
