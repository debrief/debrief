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

package org.mwc.debrief.core.editors.painters.highlighters;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;

public class NullHighlighter implements SWTPlotHighlighter {

	@Override
	public EditorType getInfo() {
		return null;
	}

	@Override
	public String getName() {
		return "Highlight off";
	}

	@Override
	public boolean hasEditor() {
		return false;
	}

	@Override
	public void highlightIt(final PlainProjection proj, final CanvasType dest, final WatchableList list,
			final Watchable watch, final boolean isPrimary) {
		// ignore, we don't do anything
	}

	/**
	 * the name of this object
	 *
	 * @return the name of this editable object
	 */
	@Override
	public final String toString() {
		return getName();
	}

}
