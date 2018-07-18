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
// $RCSfile: LineShape.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: LineShape.java,v $
// Revision 1.7  2006/05/02 13:21:38  Ian.Mayo
// Make things draggable
//
// Revision 1.6  2006/04/21 07:48:36  Ian.Mayo
// Make things draggable
//
// Revision 1.5  2006/03/22 10:45:09  Ian.Mayo
// Rename properties in correct order, tidy tests
//
// Revision 1.4  2005/05/25 08:38:50  Ian.Mayo
// Minor tidying from Eclipse
//
// Revision 1.3  2004/08/31 09:38:16  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:13  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:33  Ian.Mayo
// Initial import
//
// Revision 1.9  2003-07-04 11:00:54+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.8  2003-07-03 14:59:48+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.7  2003-07-03 14:25:55+01  ian_mayo
// Use a red-shade of line as standard, like the other shapes
//
// Revision 1.6  2003-06-25 08:51:01+01  ian_mayo
// Only plot if we are visible
//
// Revision 1.5  2003-01-23 16:04:30+00  ian_mayo
// provide methods to return the shape as a series of segments
//
// Revision 1.4  2003-01-21 16:32:13+00  ian_mayo
// move getColor property management to ShapeWrapper
//
// Revision 1.3  2002-10-30 16:26:55+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.2  2002-05-28 09:25:52+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:23+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-03-19 11:04:05+00  administrator
// Add a "type" property to indicate type of shape (label, rectangle, etc)
//
// Revision 1.0  2001-07-17 08:43:16+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 19:40:36+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.2  2001-01-22 12:29:28+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:42:24+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:12  ianmayo
// initial version
//
// Revision 1.8  2000-09-21 09:06:44+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.7  2000-08-18 13:36:04+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.6  2000-08-14 15:49:30+01  ian_mayo
// tidy up descriptions
//
// Revision 1.5  2000-08-11 08:42:00+01  ian_mayo
// tidy beaninfo
//
// Revision 1.4  2000-08-07 14:06:29+01  ian_mayo
// remove un-necessary code
//
// Revision 1.3  1999-10-15 12:36:51+01  ian_mayo
// improved relative label locating
//
// Revision 1.2  1999-10-14 11:59:01+01  ian_mayo
// new shape
//
// Revision 1.1  1999-10-12 15:36:36+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:37+01  administrator
// Initial revision
//
// Revision 1.3  1999-07-23 14:03:47+01  administrator
// Updating MWC utilities, & catching up on changes (removed deprecated code from PtPlot)
//
// Revision 1.2  1999-07-19 12:39:42+01  administrator
// Added painting to a metafile
//
// Revision 1.1  1999-07-07 11:10:04+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:57+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:46+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:02+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;

//import org.mwc.cmap.core.CorePlugin;

import MWC.Algorithms.Conversions;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;
import MWC.Utilities.Errors.Trace;

public class LineShape extends PlainShape implements Editable,
		HasDraggableComponents
{

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////
	// keep track of versions
	static final long serialVersionUID = 1;

	protected WorldLocation _start;
	protected WorldLocation _end;
	private WorldLocation _centre;

	private boolean _arrowAtEnd = false;

	private boolean _showAutoCalc = false;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////

	public LineShape(final WorldLocation start, final WorldLocation end)
	{
		this(start, end, "Line");
	}

	public LineShape(final WorldLocation start, final WorldLocation end, final String name)
	{
		super(0, name);
		// store the data
		_start = start;
		_end = end;

		calcCentre();
	}

	private void calcCentre()
	{
		_centre = new WorldArea(_start, _end).getCentre();
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	public void paint(final CanvasType dest)
	{
		// are we visible?
		if (!getVisible())
			return;

		if (this.getColor() != null)
			dest.setColor(this.getColor());
		
		if(getFont() != null)
			dest.setFont(getFont());

		// get the origin
		final Point start = new Point(dest.toScreen(_start));

		// get the width and height
		final Point end = new Point(dest.toScreen(_end));

		// and now draw it
		dest.drawLine(start.x, start.y, end.x, end.y);

		if (getArrowAtEnd())
		{

			// sort out the direction
			final double direction = _end.bearingFrom(_start) + Math.PI / 2;

			// and the size
			final double theScale = 1;

			final double len = 15d * theScale;
			final double angle = MWC.Algorithms.Conversions.Degs2Rads(20);

			// find the tip of the arrow
			final Point p1 = dest.toScreen(_end);

			// now the back corners
			final Point p2 = new Point(p1);
			p2.translate((int) (len * Math.cos(direction - angle)),
					(int) (len * Math.sin(direction - angle)));
			final Point p3 = new Point(p1);
			p3.translate((int) (len * Math.cos(direction + angle)),
					(int) (len * Math.sin(direction + angle)));

			dest.fillPolygon(new int[]
			{ p1.x, p2.x, p3.x }, new int[]
			{ p1.y, p2.y, p3.y }, 3);

		}

		// Draw range/bearing at the center of the line
		if (isShowAutoCalc())
		{			
			calcCentre();
			final Point center = new Point(dest.toScreen(_centre));
						
			String myUnits = Trace.getParent().getProperty(
					MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);
			if (myUnits == null)
				myUnits = MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS;
			
			final WorldVector wv = _end.subtract(_start);
			final double range = Conversions.convertRange(wv.getRange(), myUnits);
			final double bearing = wv.getBearing();
			final String msg = String.format("%.1f", range) + " " + myUnits + " "
					+ String.format("%d", (int)Math.toDegrees(bearing)) + "\u00B0";
			
			float rotate = (float) Math.toDegrees(bearing);
			if (start.x < end.x)
			{
				rotate -= 90;				
			}
			else
			{
				rotate += 90;
			}
			dest.drawText(msg, center.x, center.y, rotate);
		}
	}

	public boolean getArrowAtEnd()
	{
		return _arrowAtEnd;
	}

	public void setArrowAtEnd(final boolean lineAtEnd)
	{
		_arrowAtEnd = lineAtEnd;
	}

	public boolean isShowAutoCalc()
	{
		return _showAutoCalc;
	}

	public void setShowAutoCalc(final boolean showAutoCalc)
	{
		_showAutoCalc = showAutoCalc;
	}

	public MWC.GenericData.WorldArea getBounds()
	{
		return new WorldArea(_start, _end);
	}

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	public double rangeFrom(final WorldLocation point)
	{
		final double r1 = point.rangeFrom(_start);
		final double r2 = point.rangeFrom(_end);
		return Math.min(r1, r2);
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
		{
			_myEditor = new LineInfo(this, getName());
			
			// also, propagate any events up from our info object
			_myEditor.addPropertyChangeListener(new PropertyChangeListener()
      {
        
        @Override
        public void propertyChange(final PropertyChangeEvent evt)
        {
          firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
        }
      });
		}
		return _myEditor;
	}

	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public MWC.GenericData.WorldLocation getAnchor()
	{
		return _centre;
	}

	/**
	 * get the start of the line
	 * 
	 * @return WorldLocation representing the start
	 */
	public WorldLocation getLine_Start()
	{
		return _start;
	}

	/**
	 * set the start of the line
	 * 
	 * @param loc
	 *          WorldLocation representing the start of the line
	 */
	public void setLine_Start(final WorldLocation loc)
	{
		_start = loc;
		calcCentre();
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	/**
	 * get the end of the line
	 * 
	 * @return WorldLocation representing the end
	 */
	public WorldLocation getLineEnd()
	{
		return _end;
	}

	/**
	 * set the end of the line
	 * 
	 * @param loc
	 *          WorldLocation representing the end of the line
	 */
	public void setLineEnd(final WorldLocation loc)
	{
		_end = loc;
		calcCentre();
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	public Color getLineColor()
	{
		return super.getColor();
	}

	public void setLineColor(final Color val)
	{
		super.setColor(val);
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class LineInfo extends Editable.EditorType
	{

		public LineInfo(final LineShape data, final String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						displayProp("Line_Start", "Line start", "the start of the line", SPATIAL),
						displayProp("LineEnd", "Line end", "the end of the line", SPATIAL),
						displayProp("ArrowAtEnd", "Arrow at end",
								"whether to show an arrow at one end of the line", FORMAT),
						displayProp("ShowAutoCalc", "Show auto calc",
								"whether to show range/bearing in the middle of the line", FORMAT), };

				return res;

			}
			catch (final IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////
	// label/anchor support
	// ///////////////////////////////////////////////////
	public WorldLocation getAnchor(final int location)
	{
		WorldLocation loc = null;

		WorldLocation north = null;
		WorldLocation south = null;
		WorldLocation east = null;
		WorldLocation west = null;

		if (_start.getLat() > _end.getLat())
		{
			north = _start;
			south = _end;
		}
		else
		{
			north = _end;
			south = _start;
		}

		if (_start.getLong() > _end.getLong())
		{
			east = _start;
			west = _end;
		}
		else
		{
			east = _end;
			west = _start;
		}

		switch (location)
		{
		case MWC.GUI.Properties.LocationPropertyEditor.TOP:
		{
			loc = north;
			break;
		}
		case MWC.GUI.Properties.LocationPropertyEditor.BOTTOM:
		{
			loc = south;
			break;
		}
		case MWC.GUI.Properties.LocationPropertyEditor.LEFT:
		{
			loc = west;
			break;
		}
		case MWC.GUI.Properties.LocationPropertyEditor.RIGHT:
		{
			loc = east;
			break;
		}
		case MWC.GUI.Properties.LocationPropertyEditor.CENTRE:
		{
			loc = _centre;
		}
		}

		return loc;
	}

	public void shift(final WorldVector vector)
	{
		setLine_Start(getLine_Start().add(vector));
		setLineEnd(getLineEnd().add(vector));
	}

	public void shift(final WorldLocation feature, final WorldVector vector)
	{
		// ok, just shift it...
		feature.addToMe(vector);
	}

	public void findNearestHotSpotIn(final Point cursorPos, final WorldLocation cursorLoc,
			final ComponentConstruct currentNearest, final Layer parentLayer)
	{

		// right - the first two points are easy, we just pass the location directly
		// to the caller
		checkThisOne(_start, cursorLoc, currentNearest, this, parentLayer);
		checkThisOne(_end, cursorLoc, currentNearest, this, parentLayer);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class LineTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public LineTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			final WorldLocation scrap = new WorldLocation(2d, 2d, 2d);
			MWC.GUI.Editable ed = new LineShape(scrap, scrap);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}

	}

}
