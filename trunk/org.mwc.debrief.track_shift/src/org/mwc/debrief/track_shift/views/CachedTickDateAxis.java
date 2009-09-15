/**
 * 
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
	public CachedTickDateAxis(String string)
	{
		super(string);
	}
	
	@SuppressWarnings("unchecked")
	private List _myTicks;

	/** utility method to clear our cached list (when we know dates have changed)
	 * 
	 */
	public void clearTicks()
	{
		_myTicks = null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea,
			RectangleEdge edge)
	{
		if(_myTicks == null)
			_myTicks =  super.refreshTicksVertical(g2, dataArea, edge);
	
		return _myTicks;
	}
	
}