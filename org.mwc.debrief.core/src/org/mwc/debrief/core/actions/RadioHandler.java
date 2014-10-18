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
package org.mwc.debrief.core.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;
import org.mwc.cmap.plotViewer.actions.Pan;
import org.mwc.cmap.plotViewer.actions.RangeBearing;
import org.mwc.cmap.plotViewer.actions.ZoomIn;

public class RadioHandler extends AbstractHandler
{

	public static final String DRAG_SEGMENT = "DragSegment";
	public static final String DRAG_COMPONENT = "DragComponent";
	public static final String DRAG_FEATURE = "DragFeature";
	public static final String RANGE_BEARING = "RangeBearing";
	public static final String PAN = "Pan";
	public static final String ZOOM_IN = "ZoomIn";
	public static final String ID = "org.mwc.debrief.core.RadioHandler";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (HandlerUtil.matchesRadioState(event))
			return null; 

		String currentState = event.getParameter(RadioState.PARAMETER_ID);

		if (ZOOM_IN.equals(currentState))
		{
			new ZoomIn().execute(event);
		} else if (PAN.equals(currentState))
		{
			new Pan().execute(event);
		} else if (RANGE_BEARING.equals(currentState))
		{
			new RangeBearing().execute(event);
		} else if (DRAG_FEATURE.equals(currentState))
		{
			new DragFeature().execute(event);
		} else if (DRAG_COMPONENT.equals(currentState))
		{
			new DragComponent().execute(event);
		} else if (DRAG_SEGMENT.equals(currentState))
		{
			new DragSegment().execute(event);
		}
		
		HandlerUtil.updateRadioState(event.getCommand(), currentState);
		return null;
	}

}
