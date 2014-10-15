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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
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
		if(_myTicks == null)
			_myTicks =  super.refreshTicksVertical(g2, dataArea, edge);
	
		return _myTicks;
	}
	
}