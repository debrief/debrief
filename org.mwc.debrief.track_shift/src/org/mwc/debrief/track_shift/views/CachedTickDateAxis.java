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
package org.mwc.debrief.track_shift.views;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.jfree.chart.axis.DateAxis;
import org.jfree.ui.RectangleEdge;

public class CachedTickDateAxis extends DateAxis
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Double _lastHeight = null;

	/** pass the constructor args back to the parent
	 * 
	 * @param string
	 */
	public CachedTickDateAxis(final String string)
	{
		super(string);
	}
	
	@SuppressWarnings("rawtypes")
	private List _myTicks;

	/** utility method to clear our cached list (when we know dates have changed)
	 * 
	 */
	public void clearTicks()
	{
		_myTicks = null;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected List refreshTicksVertical(final Graphics2D g2, final Rectangle2D dataArea,
			final RectangleEdge edge)
	{
		// do we have a height?
		if(_lastHeight != null)
		{
			// is the new height different to our last one?
			if(Math.abs( dataArea.getHeight() - _lastHeight.doubleValue()) > 60)
			{
				// yes - ditch the ticks
				_myTicks = null;
			}
		}
		
		// do we have any ticks?
		if(_myTicks == null)
		{
			// nope, better create some
			_myTicks =  super.refreshTicksVertical(g2, dataArea, edge);
			_lastHeight = dataArea.getHeight();
		}
		
		
		return _myTicks;
	}
	
}