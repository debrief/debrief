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
package MWC.GUI.Chart.Painters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 19-Oct-2004 Time: 08:59:54 To
 * change this template use File | Settings | File Templates.
 */
public class LocalGridPainter extends GridPainter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member objects
	// ////////////////////////////////////////////////
	/**
	 * the origin for this grid
	 */
	private WorldLocation _myOrigin;

	/**
	 * whether to plot the origin of the grid
	 */
	private boolean _plotOrigin = true;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	// ////////////////////////////////////////////////
	// member methods
	// ////////////////////////////////////////////////

	public void paint(final CanvasType g)
	{
		super.paint(g); // To change body of overridden methods use File | Settings
										// | File Templates.

		if (getVisible())
		{
			// do we plot the origin?
			if (getPlotOrigin())
			{
				final Point originPoint = g.toScreen(getOrigin());
				g.setColor(Color.white);
				g.fillRect(originPoint.x - 1, originPoint.y - 1, 3, 3);
				// done.
			}
		}
	}

	/**
	 * find the top, bottom, left and right limits to plot. We've refactored it to
	 * a child class so that it can be overwritten
	 * 
	 * @param g
	 *          the plotting converter
	 * @param screenArea
	 *          the visible screen area
	 * @param deltaDegs
	 *          the grid separation requested
	 * @return an area providing the coverage requested
	 */
	protected WorldArea getOuterBounds(final CanvasType g, final Dimension screenArea,
			final double deltaDegs)
	{
		double minLat, maxLat, minLong, maxLong;

		// the way we will calculate the limits, is to start at the origin and walk
		// out in each direction,
		// stopping when we have gone outside the screen area.

		// create data coordinates from the current corners of the screen
		final WorldLocation topLeft = new WorldLocation(g.toWorld(new Point(0, 0)));
		final WorldLocation bottomRight = g.toWorld(new Point(screenArea.width,
				screenArea.height));

		double workingVal;

		// /////////////////////////
		// LAT FIRST
		// /////////////////////////

		workingVal = _myOrigin.getLat();
		while (workingVal >= bottomRight.getLat())
		{
			workingVal -= deltaDegs;
		}

		// so, we've just gone off the edge. go back one
		minLat = workingVal;

		workingVal = _myOrigin.getLat();
		while (workingVal <= topLeft.getLat())
		{
			workingVal += deltaDegs;
		}

		// so, we've just gone off the edge. go back one
		maxLat = workingVal;

		// /////////////////////////
		// NOW BOTTOM RIGHT
		// /////////////////////////

		workingVal = _myOrigin.getLong();

		while (workingVal <= bottomRight.getLong())
		{
			workingVal += deltaDegs;
		}

		// so, we've just gone off the edge. go back one
		maxLong = workingVal;

		workingVal = _myOrigin.getLong();
		final WorldVector longOffset = new WorldVector(MWC.Algorithms.Conversions
				.Degs2Rads(270), deltaDegs, 0);

		while (workingVal >= topLeft.getLong())
		{
			if (getDelta().isAngular())
			{
				workingVal -= deltaDegs;
			}
			else
			{
				// aah, special processing here.
				final WorldLocation loc = new WorldLocation(getOrigin().getLat(), workingVal,
						0);
				final WorldLocation newPos = loc.add(longOffset);
				workingVal = newPos.getLong();
			}
		}

		// so, we've just gone off the edge. go back one
		minLong = workingVal;

		// ////////////////////////////////////////////////
		// collate results
		// ////////////////////////////////////////////////

		final WorldArea bounds = new WorldArea(new WorldLocation(maxLat, minLong, 0),
				new WorldLocation(minLat, maxLong, 0));
		return bounds;
	}

	/**
	 * unfortunately we need to do some plotting tricks when we're doing a
	 * locally-origined grid. This method is over-ridden by the LocalGrid to allow
	 * this
	 * 
	 * @return
	 */
	protected boolean isLocalPlotting()
	{
		return true;
	}

	/**
	 * determine where to start counting our grid labels from
	 * 
	 * @param bounds
	 * @return
	 */
	protected WorldLocation getGridLabelOrigin(final WorldArea bounds)
	{
		return _myOrigin;
	}

	/**
	 * the origin for this grid
	 * 
	 * @return
	 */
	public WorldLocation getOrigin()
	{
		return _myOrigin;
	}

	/**
	 * the origin for this grid
	 * 
	 * @param origin
	 */
	public void setOrigin(final WorldLocation origin)
	{
		this._myOrigin = origin;
		
    // and inform the parent (so it can move the label)
    firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, origin);
	}

	/**
	 * whether to plot the origin of the grid
	 * 
	 * @return yes/no
	 */
	public boolean getPlotOrigin()
	{
		return _plotOrigin;
	}

	/**
	 * whether to plot the origin of the grid
	 * 
	 * @param plotOrigin
	 *          yes/no
	 */
	public void setPlotOrigin(final boolean plotOrigin)
	{
		this._plotOrigin = plotOrigin;

	   // and inform the parent (so it can move the label)
    firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, plotOrigin);
}

	/**
	 * whether the plotting algorithm should offset the origin to the nearest
	 * whole number of selected units
	 * 
	 * @return yes/no
	 */
	protected boolean offsetOrigin()
	{
		// no, we don't want to. We just want to use the specified origin
		return false;
	}

	// ////////////////////////////////////////////////
	// editor support
	// ////////////////////////////////////////////////

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new LocalGridPainterInfo(this);

		return _myEditor;
	}

	public String getName()
	{
		return "Local Grid";
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class LocalGridPainterInfo extends GridPainterInfo
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LocalGridPainterInfo(final LocalGridPainter data)
		{
			super(data);
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				// get the parent attributes
				final PropertyDescriptor[] parentAttributes = super.getPropertyDescriptors();

				// get my attributes
				final PropertyDescriptor[] myAttributes =
				{ 
						prop("Origin", "the origin (bottom left corner) of the grid"),
						displayProp("PlotOrigin", "Plot origin", "whether to plot the origin of the grid")
				};

				// ok, now try to combine the two
				final PropertyDescriptor[] res = new PropertyDescriptor[parentAttributes.length
						+ myAttributes.length];

				// copy the arrays into it
				System.arraycopy(parentAttributes, 0, res, 0, parentAttributes.length);
				System.arraycopy(myAttributes, 0, res, parentAttributes.length,
						myAttributes.length);

				return res;
			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class LocalGridPainterTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public LocalGridPainterTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			LocalGridPainter ed = new LocalGridPainter();
			ed.setOrigin(new WorldLocation(0, 0, 0));
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

}
