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
