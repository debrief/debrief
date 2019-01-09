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
// $RCSfile: RectangleShape.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.9 $
// $Log: RectangleShape.java,v $
// Revision 1.9  2007/01/05 14:49:22  ian.mayo
// Switch to generics (minor)
//
// Revision 1.8  2006/05/02 13:21:38  Ian.Mayo
// Make things draggable
//
// Revision 1.7  2006/04/21 07:48:37  Ian.Mayo
// Make things draggable
//
// Revision 1.6  2006/03/22 10:45:09  Ian.Mayo
// Rename properties in correct order, tidy tests
//
// Revision 1.5  2005/10/10 11:02:24  Ian.Mayo
// We don't need to normalise a second time
//
// Revision 1.4  2005/05/19 14:46:49  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.3  2004/08/31 09:38:19  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:18  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:22  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.13  2003-07-04 11:00:54+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.12  2003-07-03 14:59:51+01  ian_mayo
// Reflect new signature of PlainShape constructor, where we don't need to set the default colour
//
// Revision 1.11  2003-06-25 08:50:59+01  ian_mayo
// Only plot if we are visible
//
// Revision 1.10  2003-03-18 16:18:43+00  ian_mayo
// Tidy commented out text
//
// Revision 1.9  2003-03-18 12:07:18+00  ian_mayo
// extended support for transparent filled shapes
//
// Revision 1.8  2003-03-06 15:29:20+00  ian_mayo
// Experiment with semi-transparent rectangle
//
// Revision 1.7  2003-03-03 11:54:34+00  ian_mayo
// Implement filled shape management
//
// Revision 1.6  2003-01-23 11:03:11+00  ian_mayo
// Implement method to get list of data points in shape
//
// Revision 1.5  2003-01-21 16:32:12+00  ian_mayo
// move getColor property management to ShapeWrapper
//
// Revision 1.4  2002-10-30 16:26:58+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-09-24 11:00:38+01  ian_mayo
// tidy up
//
// Revision 1.2  2002-05-28 09:25:51+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:24+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:09+01  ian_mayo
// Initial revision
//
// Revision 1.2  2002-03-19 11:05:49+00  administrator
// Add type property
//
// Revision 1.1  2001-11-06 15:43:07+00  administrator
// Slight mod to property naming so that TL/BR properties listed in more logical order
//
// Revision 1.0  2001-07-17 08:43:17+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 19:40:35+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.2  2001-01-22 12:29:28+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:42:25+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:16  ianmayo
// initial version
//
// Revision 1.7  2000-09-21 09:06:43+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.6  2000-08-18 13:36:02+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.5  2000-08-14 15:49:30+01  ian_mayo
// tidy up descriptions
//
// Revision 1.4  2000-08-11 08:41:57+01  ian_mayo
// tidy beaninfo
//
// Revision 1.3  1999-10-14 11:59:19+01  ian_mayo
// added property support and location editing
//
// Revision 1.2  1999-10-13 17:22:23+01  ian_mayo
// store rectangle as area, not pair of locations
//
// Revision 1.1  1999-10-12 15:36:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-10-12 15:05:58+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:38+01  administrator
// Initial revision
//
// Revision 1.2  1999-07-19 12:39:41+01  administrator
// Added painting to a metafile
//
// Revision 1.1  1999-07-07 11:10:05+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:57+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 14:25:01+00  sm11td
// Skeleton there, opening new sessions, window management.
//
// Revision 1.1  1999-01-31 13:33:02+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes;

import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.ExtendedCanvasType;
import MWC.GUI.Layer;
import MWC.GUI.PlainWrapper;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class RectangleShape extends PlainShape implements Editable,
		HasDraggableComponents
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////
	protected WorldArea _myArea;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	// ////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////
	public RectangleShape(final WorldLocation TL, final WorldLocation BR)
	{
		super(0, "Rectangle");

		_myArea = new WorldArea(TL, BR);
		_myArea.normalise();
	}

	// ////////////////////////////////////////////////
	// member functions
	// ////////////////////////////////////////////////

	/**
	 * paint this shape to the destination canvas
	 */
	public void paint(final CanvasType dest)
	{
		// are we visible?
		if (!getVisible())
			return;

		// create a transparent colour
		final Color newcol = getColor();
		dest.setColor(new Color(newcol.getRed(), newcol.getGreen(), newcol
				.getBlue(), TRANSPARENCY_SHADE));
		final Collection<WorldLocation> pts = getDataPoints();
		final Iterator<WorldLocation> iter = pts.iterator();
		final int STEPS = pts.size();
		final int[] xP = new int[STEPS];
		final int[] yP = new int[STEPS];
		int ctr = 0;
		while (iter.hasNext())
		{
      try
      {
        WorldLocation loc = iter.next();
        final Point pt = dest.toScreen(loc);
        // Rotate Rectangle around its center for degrees, specified in
        // Orientation
        if (pt != null)
        {
          xP[ctr] = pt.x;
          yP[ctr++] = pt.y;
        }
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
		}

		// is it to be filled?
		// and plot the polygon
		if (getFilled())
		{
			if (getSemiTransparent() && dest instanceof ExtendedCanvasType)
			{
				ExtendedCanvasType ext = (ExtendedCanvasType) dest;
				ext.semiFillPolygon(xP, yP, STEPS);
			}
			else
				dest.fillPolygon(xP, yP, STEPS);
		}
		else
		{
			dest.drawPolygon(xP, yP, STEPS);
		}

	}

	/**
	 * return the area covered by this shape
	 * 
	 * @return WorldArea the area covered
	 */
	public MWC.GenericData.WorldArea getBounds()
	{
		return _myArea;
	}

	public void setRectangleColor(final Color val)
	{
		super.setColor(val);
	}

	public Color getRectangleColor()
	{
		return super.getColor();
	}

	/**
	 * get the shape as a series of WorldLocation points. Joined up, these form a
	 * representation of the shape
	 */
	private Collection<WorldLocation> getDataPoints()
	{
		final Vector<WorldLocation> res = new Vector<WorldLocation>(0, 1);

		res.add(_myArea.getTopLeft());
		res.add(_myArea.getTopRight());
		res.add(_myArea.getBottomRight());
		res.add(_myArea.getBottomLeft());

		return res;
	}

	/**
	 * get the range from the indicated world location - making this abstract
	 * allows for individual shapes to have 'hit-spots' in various locations.
	 */
	public double rangeFrom(final WorldLocation point)
	{
		return _myArea.rangeFrom(point);
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new RectangleInfo(this, this.getName());

		return _myEditor;
	}

	/**
	 * get the 'anchor point' for any labels attached to this shape
	 */
	public MWC.GenericData.WorldLocation getAnchor()
	{
		return _myArea.getCentre();
	}

	/**
	 * get the TopLeft corner
	 * 
	 * @return WorldLocation for the TopLeft corner
	 */
	public WorldLocation getCorner_TopLeft()
	{
		return _myArea.getTopLeft();
	}

	/**
	 * set the TopLeft corner
	 * 
	 * @param loc
	 *          WorldLocation for the corner
	 */
	public void setCorner_TopLeft(final WorldLocation loc)
	{
		final WorldLocation br = _myArea.getBottomRight();
		_myArea = new WorldArea(loc, br);

		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	/**
	 * get the BottomRight corner
	 * 
	 * @return WorldLocation for the BottomRight corner
	 */
	public WorldLocation getCornerBottomRight()
	{
		return _myArea.getBottomRight();
	}

	/**
	 * set the BottomRight corner
	 * 
	 * @param loc
	 *          WorldLocation for the corner
	 */
	public void setCornerBottomRight(final WorldLocation loc)
	{
		final WorldLocation tl = _myArea.getTopLeft();
		_myArea = new WorldArea(tl, loc);

		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	// ////////////////////////////////////////////////////
	// bean info for this class
	// ///////////////////////////////////////////////////
	public class RectangleInfo extends Editable.EditorType
	{

		public RectangleInfo(final RectangleShape data, final String theName)
		{
			super(data, theName, "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				final PropertyDescriptor[] res =
				{
						displayProp("Corner_TopLeft", "Top left corner", "the top left corner", SPATIAL),
						displayProp("CornerBottomRight",  "Bottom right corner", "the bottom right corner", SPATIAL),
						prop("Filled", "whether this shape is filled", FORMAT),
						displayProp("SemiTransparent", "Semi transparent",
								"whether the filled rect is semi-transparent", FORMAT), };

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
	static public class RectangleTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public RectangleTest(final String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			final WorldLocation scrap = new WorldLocation(2d, 2d, 2d);
			MWC.GUI.Editable ed = new RectangleShape(scrap, scrap);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

	/**
	 * move the whole shape by the specified distance
	 * 
	 */
	public void shift(final WorldVector vector)
	{

		// get the old centre
		final WorldLocation oldCentre = _myArea.getCentre();

		// apply the offset
		final WorldLocation newCentre = oldCentre.add(vector);

		// ok, apply the offset to each corner
		_myArea.setCentre(newCentre);

		// and make it square again
		_myArea.normalise();

		// and inform the parent, so we can shift the label location
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	/**
	 * move one of the corners of the shape
	 * 
	 */
	public void shift(final WorldLocation feature, final WorldVector vector)
	{
		// ok, just shift it...
		feature.addToMe(vector);

		// better normalise the shape now...
		_myArea.normalise();

		// and inform the parent, so we can shift the label location
		firePropertyChange(PlainWrapper.LOCATION_CHANGED, null, null);
	}

	public void findNearestHotSpotIn(final Point cursorPos,
			final WorldLocation cursorLoc, final ComponentConstruct currentNearest,
			final Layer parentLayer)
	{

		// right - the first two points are easy, we just pass the location directly
		// to the caller
		checkThisOne(_myArea.getTopLeft(), cursorLoc, currentNearest, this,
				parentLayer);
		checkThisOne(_myArea.getBottomRight(), cursorLoc, currentNearest, this,
				parentLayer);

		// now for our 'special' corners, that we can't just shift over...
		// we've got to wrap the locations in an object that actually updates the TL
		// & BR corners of the
		// rectangle
		final WorldLocation bl = new WorldLocation(_myArea.getBottomLeft())
		{
			private static final long serialVersionUID = 1L;

			public void addToMe(final WorldVector delta)
			{
				final WorldLocation newBL = _myArea.getBottomLeft().add(delta);
				final WorldLocation newTR = _myArea.getTopRight();
				_myArea = new WorldArea(newBL, newTR);
				_myArea.normalise();
			}
		};
		final WorldLocation tr = new WorldLocation(_myArea.getTopRight())
		{
			private static final long serialVersionUID = 1L;

			public void addToMe(final WorldVector delta)
			{
				final WorldLocation newBL = _myArea.getBottomLeft();
				final WorldLocation newTR = _myArea.getTopRight().add(delta);
				_myArea = new WorldArea(newBL, newTR);
				_myArea.normalise();
			}
		};

		// now check the ranges...
		checkThisOne(bl, cursorLoc, currentNearest, this, parentLayer);
		checkThisOne(tr, cursorLoc, currentNearest, this, parentLayer);
	}

}
