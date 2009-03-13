package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: GridPainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.17 $
// $Log: GridPainter.java,v $
// Revision 1.17  2006/06/06 09:19:32  Ian.Mayo
// Give grids an editable name
//
// Revision 1.16  2006/05/25 14:10:38  Ian.Mayo
// Make plottables comparable
//
// Revision 1.15  2005/09/13 09:28:29  Ian.Mayo
// Eclipse tidying
//
// Revision 1.14  2005/09/13 09:16:07  Ian.Mayo
// Minor tidying
//
// Revision 1.13  2005/09/07 13:45:45  Ian.Mayo
// Minor tidying
//
// Revision 1.12  2005/05/19 14:46:48  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.11  2005/01/11 15:18:04  Ian.Mayo
// make the grid slightly thicker
//
// Revision 1.10  2004/10/25 09:02:48  Ian.Mayo
// Correctly label grid lines when in relative mode (non-angular units)
//
// Revision 1.9  2004/10/19 13:27:05  Ian.Mayo
// More refactoring to support override by local grid painter
//
// Revision 1.8  2004/10/19 11:13:16  Ian.Mayo
// Getting closer - our new origin is respected when using angular units
//
// Revision 1.7  2004/10/19 10:15:02  Ian.Mayo
// Add local grid support
//
// Revision 1.6  2004/10/19 08:28:20  Ian.Mayo
// Refactor to allow over-riding by LocalGridPainter
//
// Revision 1.5  2004/08/31 09:38:06  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.4  2004/05/25 14:46:57  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.3  2003/10/21 08:15:36  Ian.Mayo
// Tidily format distance grid labels, correctly plot long as long
//
// Revision 1.2  2003/10/17 14:52:57  Ian.Mayo
// Better drawing of distance-related units
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.6  2003-07-04 11:00:55+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.5  2003-01-30 10:25:10+00  ian_mayo
// Check we only plot sensible grid lines
//
// Revision 1.4  2002-10-30 16:26:59+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:53+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:14+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-17 20:40:26+00  administrator
// Reflect switch to Duration/WorldDistance
//
// Revision 1.3  2002-01-17 14:49:09+00  administrator
// Reflect fact that distance now in WorldDistance units
//
// Revision 1.2  2001-08-21 12:06:03+01  administrator
// Make grids visible by default
//
// Revision 1.1  2001-08-01 13:00:42+01  administrator
// Add code and properties necessary for plotting labels on grid lines
//
// Revision 1.0  2001-07-17 08:46:29+01  administrator
// Initial revision
//
// Revision 1.5  2001-06-04 09:37:37+01  novatech
// don't plot grid points if they're too close
//
// Revision 1.4  2001-01-22 19:40:37+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.3  2001-01-22 14:49:00+00  novatech
// Projection.toWorld() always return the same object, so completely handle the TL corner before we start processing the BR corner
//
// Revision 1.2  2001-01-22 12:29:30+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:13  ianmayo
// initial version
//
// Revision 1.7  2000-09-21 09:06:46+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.6  2000-08-18 13:36:07+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.5  2000-08-11 08:41:59+01  ian_mayo
// tidy beaninfo
//
// Revision 1.4  2000-08-09 16:03:13+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.3  1999-11-26 15:45:32+00  ian_mayo
// adding toString methods
//
// Revision 1.2  1999-11-11 18:19:19+00  ian_mayo
// minor tidying up
//
// Revision 1.1  1999-10-12 15:37:01+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-09-14 14:17:55+01  administrator
// working with values other than whole degrees
//
// Revision 1.1  1999-08-17 10:02:08+01  administrator
// Initial revision
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import junit.framework.Assert;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Plottable;
import MWC.GUI.Shapes.DraggableItem;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldDistance;
import MWC.GenericData.WorldDistanceWithUnits;
import MWC.GenericData.WorldLocation;
import MWC.GenericData.WorldVector;

public class Grid4WPainter implements Plottable, Serializable, DraggableItem
{
	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * flag for invalid 4w index
	 * 
	 */
	private static final int INVALID_INDEX = 0;

	/**
	 * the colour for this grid
	 */
	protected Color _myColor;

	/**
	 * the horizontal grid separation (in degrees)
	 */
	protected WorldDistanceWithUnits _myXDelta;

	/**
	 * the vertical grid separation (in degrees)
	 */
	protected WorldDistanceWithUnits _myYDelta;

	/**
	 * whether this grid is visible
	 */
	protected boolean _isOn;

	/**
	 * are we plotting lines?
	 */
	protected boolean _plotLines = true;

	/**
	 * are we plotting lat/long labels?
	 */
	protected boolean _plotLabels = true;

	/**
	 * whether to fill the grid
	 */
	protected boolean _fillGrid = false;

	/**
	 * the color to fill in the grid
	 * 
	 */
	protected Color _fillColor = Color.white;

	/**
	 * the min x-axis square we're plotting
	 * 
	 */
	protected int _xMin = 0;

	/**
	 * the max x-axis square we're plotting
	 * 
	 */
	protected int _xMax = 23;

	/**
	 * the min y-axis square we're plotting
	 * 
	 */
	protected int _yMin = 0;

	/**
	 * the max y-axis square we're plotting
	 * 
	 */
	protected int _yMax = 23;

	/**
	 * the bottom-left corner of the grid
	 */
	protected WorldLocation _origin;

	/**
	 * the orientation of the 4W grid
	 */
	private double _orientation;

	/**
	 * our editor
	 */
	transient protected Editable.EditorType _myEditor;

	/**
	 * the name of this grid
	 * 
	 */
	private String _myName = DEFAULT_NAME;

	public static final String DEFAULT_NAME = "4W Grid";

	/**
	 * the font to use
	 */
	private Font _theFont = new Font("Sans Serif", Font.PLAIN, 8);

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	public Grid4WPainter(WorldLocation origin)
	{
		_myColor = Color.darkGray;

		_origin = origin;

		// give it some default deltas
		setXDelta(new WorldDistanceWithUnits(10, WorldDistanceWithUnits.NM));
		setYDelta(new WorldDistanceWithUnits(10, WorldDistanceWithUnits.NM));

		_orientation = 0;

		// make it visible to start with
		setVisible(true);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	public void setVisible(boolean val)
	{
		_isOn = val;
	}

	public boolean getVisible()
	{
		return _isOn;
	}

	public void setColor(Color val)
	{
		_myColor = val;
	}

	public Color getColor()
	{
		return _myColor;
	}

	/**
	 * set the y delta for this grid
	 * 
	 * @param val
	 *          the size
	 */
	public void setYDelta(WorldDistanceWithUnits val)
	{
		_myYDelta = val;
	}

	/**
	 * get the y delta for the grid
	 * 
	 * @return the size
	 */
	public WorldDistanceWithUnits getYDelta()
	{
		return _myYDelta;
	}

	/**
	 * set the x delta for this grid
	 * 
	 * @param val
	 *          the size
	 */
	public void setXDelta(WorldDistanceWithUnits val)
	{
		_myXDelta = val;
	}

	/**
	 * get the x delta for the grid
	 * 
	 * @return the size
	 */
	public WorldDistanceWithUnits getXDelta()
	{
		return _myXDelta;
	}

	/**
	 * whether to plot the labels or not
	 */
	public boolean getPlotLabels()
	{
		return _plotLabels;
	}

	/**
	 * whether to plot the labels or not
	 */
	public void setPlotLabels(boolean val)
	{
		_plotLabels = val;
	}

	public Font getFont()
	{
		return _theFont;
	}

	public void setFont(Font theFont)
	{
		_theFont = theFont;
	}

	/**
	 * whether to plot the labels or not
	 */
	public void setName(String name)
	{
		_myName = name;
	}

	public void paint(CanvasType g)
	{

		// check we are visible
		if (!_isOn)
			return;

		// create a transparent colour
		g.setColor(new Color(_myColor.getRed(), _myColor.getGreen(), _myColor
				.getBlue(), 160));

		float oldLineWidth = g.getLineWidth();

		// get the screen dimensions
		Dimension dim = g.getSize();

		g.setLineWidth(1.0f);

		// is it filled?
		if (_fillGrid)
		{
			// sort out the bounds
			int[] xPoints = new int[4];
			int[] yPoints = new int[4];
			int ctr = 0;

			Point thisP = g.toScreen(calcLocationFor(_xMin, _yMin));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMin, _yMax + 1));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMax + 1, _yMax + 1));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			thisP = g.toScreen(calcLocationFor(_xMax + 1, _yMin));
			xPoints[ctr] = thisP.x;
			yPoints[ctr++] = thisP.y;

			g.setColor(_fillColor);
			g.fillPolygon(xPoints, yPoints, xPoints.length);
		}

		// ok, draw the vertical lines
		for (int x = _xMin; x <= _xMax + 1; x++)
		{
			if (_plotLines)
			{
				Point start = new Point(g.toScreen(calcLocationFor(x, _yMin)));
				Point end = new Point(g.toScreen(calcLocationFor(x, _yMax + 1)));
				g.drawLine(start.x, start.y, end.x, end.y);
			}

			if ((x <= _xMax) && _plotLabels)
			{
				// find the centre-point for the label
				Point start = new Point(g.toScreen(calcLocationFor(x, _yMin)));
				Point end = new Point(g.toScreen(calcLocationFor(x + 1, _yMin)));

				Point centre = new Point(start.x + (end.x - start.x) / 2, start.y);

				// what's this label
				String thisLbl = labelFor(x);

				// sort out the dimensions of the font
				int ht = g.getStringHeight(_theFont);
				int wid = g.getStringWidth(_theFont, thisLbl);

				if (dim != null)
				{
					// sometimes we don't have a dimension - such as when the grid is
					// being dragged
					centre.y = Math.min(centre.y, dim.height - 2 * ht);
				}

				// and draw it
				g.drawText(_theFont, thisLbl, centre.x - wid / 2, centre.y + ht);
			}

		}

		// ok, now the horizontal lines
		for (int y = _yMin; y <= _yMax + 1; y++)
		{
			if (_plotLines)
			{
				Point start = new Point(g.toScreen(calcLocationFor(_xMin, y)));
				Point end = new Point(g.toScreen(calcLocationFor(_xMax + 1, y)));
				g.drawLine(start.x, start.y, end.x, end.y);
			}

			if ((y <= _yMax) && _plotLabels)
			{
				// find the centre-point for the label
				Point start = new Point(g.toScreen(calcLocationFor(_xMin, y)));
				Point end = new Point(g.toScreen(calcLocationFor(_xMin, y + 1)));

				Point centre = new Point(start.x, start.y + (end.y - start.y) / 2);

				// move this into the visible area if it's outside.
				if (dim != null)
				{
					// sometimes we don't have a dimension - such as when the grid is
					// being dragged
					centre.x = Math.max(centre.x, (int) (dim.getWidth() * 0.03));
				}

				// what's this label
				String thisLbl = "" + (y + 1);

				// sort out the dimensions of the font
				int ht = g.getStringHeight(_theFont);
				int wid = g.getStringWidth(_theFont, thisLbl);

				// and draw it
				g.drawText(_theFont, thisLbl, centre.x - (wid + 2), centre.y + ht / 2);
			}
		}

		// and restore the line width
		g.setLineWidth(oldLineWidth);

	}

	/**
	 * determine where to start counting our grid labels from
	 * 
	 * @param bounds
	 * @return
	 */
	protected WorldLocation getGridLabelOrigin(WorldArea bounds)
	{
		return bounds.getBottomLeft();
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
		return false;
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
	protected WorldArea getOuterBounds(CanvasType g, Dimension screenArea,
			double deltaDegs)
	{
		// create data coordinates from the current corners of the screen
		WorldLocation topLeft = g.toWorld(new Point(0, 0));

		// create new corners just outside the current plot area, and clip
		// them to the nearest 'delta' value
		double maxLat1 = Math.ceil(topLeft.getLat() / deltaDegs) * deltaDegs;
		double minLong1 = (int) Math.floor(topLeft.getLong() / deltaDegs)
				* deltaDegs;

		// now for the bottom right
		WorldLocation bottomRight = g.toWorld(new Point(screenArea.width,
				screenArea.height));

		// create new corners just outside the current plot area, and clip
		// them to the nearest 'delta' value
		double maxLong1 = Math.ceil(bottomRight.getLong() / deltaDegs) * deltaDegs;
		double minLat1 = Math.floor(bottomRight.getLat() / deltaDegs) * deltaDegs;

		WorldArea bounds = new WorldArea(new WorldLocation(maxLat1, minLong1, 0),
				new WorldLocation(minLat1, maxLong1, 0));
		return bounds;
	}

	public MWC.GenericData.WorldArea getBounds()
	{
		return null;
	}

	/**
	 * sort out the location of this point
	 * 
	 * @param x
	 *          how far across to go
	 * @param y
	 *          how far down to go
	 * @return the location at this index
	 */
	protected WorldLocation calcLocationFor(int x, int y)
	{
		// convert the orientation to radians
		double orient = MWC.Algorithms.Conversions.Degs2Rads(_orientation);

		// calculate the deltas
		final double xComponent = x * _myXDelta.getValueIn(WorldDistance.DEGS);
		final double yComponent = y * _myYDelta.getValueIn(WorldDistance.DEGS);
		double xNew = xComponent * Math.cos(orient) + yComponent * Math.sin(orient);
		double yNew = -xComponent * Math.sin(orient) + yComponent
				* Math.cos(orient);

		WorldLocation res = new WorldLocation(_origin.getLat() + yNew, _origin
				.getLong()
				+ xNew, 0);
		return res;
	}

	public double rangeFrom(MWC.GenericData.WorldLocation other)
	{
		// doesn't return a sensible distance;
		return INVALID_RANGE;
	}

	/**
	 * return this item as a string
	 */
	public String toString()
	{
		return getName();
	}

	public String getName()
	{
		return _myName;
	}

	public boolean hasEditor()
	{
		return true;
	}

	public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new GridPainterInfo(this);

		return _myEditor;
	}

	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		String myName = this.getName() + this.hashCode();
		String hisName = other.getName() + arg0.hashCode();
		return myName.compareTo(hisName);
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	public class GridPainterInfo extends Editable.EditorType implements
			Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public GridPainterInfo(Grid4WPainter data)
		{
			super(data, data.getName(), "");
		}

		public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res =
				{ prop("Color", "the Color to draw the grid", FORMAT),
						prop("Visible", "whether this grid is visible", VISIBILITY),
						prop("PlotLabels", "whether to plot grid labels", VISIBILITY),
						prop("PlotLines", "whether to plot grid lines", VISIBILITY),
						prop("FillGrid", "whether to fill the grid", VISIBILITY),
						prop("Name", "name of this grid", FORMAT),
						prop("Font", "font to use for labels", FORMAT),
						prop("FillColor", "color to use to fill the grid", FORMAT),
						prop("XDelta", "the x step size for the grid", SPATIAL),
						prop("YDelta", "the y step size for the grid", SPATIAL),
						prop("Orientation", "the orientation of the grid", SPATIAL),
						prop("XMin", "the min index shown on the x-axis", SPATIAL),
						prop("XMax", "the max index shown on the x-axis", SPATIAL),
						prop("YMin", "the min index shown on the y-axis", SPATIAL),
						prop("YMax", "the max index shown on the y-axis", SPATIAL),
						prop("Origin", "the bottom-left corner of the grid", SPATIAL) };

				return res;
			} catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public class Grid4WTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public Grid4WTest(String val)
		{
			super(val);
		}

		public void testMyParams()
		{
			MWC.GUI.Editable ed = new Grid4WPainter(null);
			MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
			ed = null;
		}

		public void testIndexMgt()
		{
			// first some valid ones
			assertEquals("index not calculated properly", 1, Grid4WPainter
					.indexOf("B"));
			assertEquals("index not calculated properly", 0, Grid4WPainter
					.indexOf("A"));
			assertEquals("index not calculated properly", 5, Grid4WPainter
					.indexOf("F"));
			assertEquals("index not calculated properly", 22, Grid4WPainter
					.indexOf("Y"));
			assertEquals("index not calculated properly", 24, Grid4WPainter
					.indexOf("AA"));
			assertEquals("index not calculated properly", 26, Grid4WPainter
					.indexOf("AC"));
			assertEquals("index not calculated properly", 47, Grid4WPainter
					.indexOf("AZ"));
			// and some invalid ones
			assertEquals("invalid index not rejected properly", INVALID_INDEX,
					Grid4WPainter.indexOf("3"));
			assertEquals("invalid index not rejected properly", INVALID_INDEX,
					Grid4WPainter.indexOf(""));
			assertEquals("invalid index not rejected properly", INVALID_INDEX,
					Grid4WPainter.indexOf("CC"));
			assertEquals("invalid index not rejected properly", INVALID_INDEX,
					Grid4WPainter.indexOf("?A"));
			// now some reverse checks
			// first some valid ones
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(1),
					"B");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(0),
					"A");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(5),
					"F");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(22),
					"Y");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(24),
					"AA");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(26),
					"AC");
			assertEquals("index not calculated properly", Grid4WPainter.labelFor(47),
					"AZ");

		}

		public void testInit()
		{
			WorldLocation origin = new WorldLocation(2, 3, 2);
			Grid4WPainter pt = new Grid4WPainter(origin);
			Assert.assertEquals("wrong name", pt.getName(), DEFAULT_NAME);
			Assert.assertEquals("wrong x def", new WorldDistanceWithUnits(10,
					WorldDistanceWithUnits.NM), pt.getXDelta());
			Assert.assertEquals("wrong y def", new WorldDistanceWithUnits(10,
					WorldDistanceWithUnits.NM), pt.getYDelta());
			Assert.assertEquals("wrong init index", 1, pt.getYMin().intValue());
			Assert.assertEquals("wrong init index", 24, pt.getYMax().intValue());
			Assert.assertEquals("wrong init index", "A", pt.getXMin());
			Assert.assertEquals("wrong init index", "Z", pt.getXMax());
			Assert.assertEquals("wrong origin lat", 2d, pt._origin.getLat());
			Assert.assertEquals("wrong origin lat", 3d, pt._origin.getLong());
		}

		public void testProperties()
		{
			Grid4WPainter pt = new Grid4WPainter(null);
			pt.setName("new grid");
			pt.setXDelta(new WorldDistanceWithUnits(12, WorldDistanceWithUnits.DEGS));
			pt.setYDelta(new WorldDistanceWithUnits(5, WorldDistanceWithUnits.DEGS));
			pt.setXMin("C");
			pt.setXMax("E");
			pt.setYMin(7);
			pt.setYMax(12);
			WorldLocation origin = new WorldLocation(2, 2, 0);
			pt.setOrigin(origin);
			Assert.assertEquals("wrong name", "new grid", pt.getName());
			Assert.assertEquals("wrong x val", new WorldDistanceWithUnits(12,
					WorldDistanceWithUnits.DEGS), pt.getXDelta());
			Assert.assertEquals("wrong y val", new WorldDistanceWithUnits(5,
					WorldDistanceWithUnits.DEGS), pt.getYDelta());
			Assert.assertEquals("wrong x index", "C", pt.getXMin());
			Assert.assertEquals("wrong x index", "E", pt.getXMax());
			Assert.assertEquals("wrong y index", 7, pt.getYMin().intValue());
			Assert.assertEquals("wrong y index", 12, pt.getYMax().intValue());
			Assert.assertEquals("wrong origin", origin, pt.getOrigin());
		}

		public void testPosCalc()
		{
			// coords to get
			int x = 1, y = 1;
			WorldLocation origin = new WorldLocation(0, 0, 0);
			double orientation = 0;
			WorldDistanceWithUnits xDelta = new WorldDistanceWithUnits(1,
					WorldDistanceWithUnits.DEGS);
			WorldDistanceWithUnits yDelta = new WorldDistanceWithUnits(1,
					WorldDistanceWithUnits.DEGS);

			Grid4WPainter pt = new Grid4WPainter(origin);
			pt.setXDelta(xDelta);
			pt.setYDelta(yDelta);
			pt.setOrientation(orientation);
			WorldLocation newLoc = pt.calcLocationFor(1, 1);
			Assert.assertEquals("easy Lat wrong", 0d, newLoc.getLat(), 0.001);
			Assert.assertEquals("easy Long wrong", 0d, newLoc.getLong(), 0.001);
			newLoc = pt.calcLocationFor(2, 2);
			Assert.assertEquals("easy Lat wrong", 1d, newLoc.getLat(), 0.001);
			Assert.assertEquals("easy Long wrong", 1d, newLoc.getLong(), 0.001);

			// turn, baby
			pt.setOrientation(90d);
			newLoc = pt.calcLocationFor(x, y);
			Assert.assertEquals("easy Lat wrong", 1d, newLoc.getLat(), 0.001);
			Assert.assertEquals("easy Long wrong", 0d, newLoc.getLong(), 0.001);

			// have another go
			pt.setXDelta(new WorldDistanceWithUnits(2, WorldDistanceWithUnits.DEGS));
			pt.setYDelta(new WorldDistanceWithUnits(3, WorldDistanceWithUnits.DEGS));
			newLoc = pt.calcLocationFor(x, y);
			Assert.assertEquals("easy Lat wrong", 2d, newLoc.getLat(), 0.001);
			Assert.assertEquals("easy Long wrong", 0d, newLoc.getLong(), 0.001);

			// and turn back
			pt.setOrientation(0d);
			newLoc = pt.calcLocationFor(x, y);
			Assert.assertEquals("easy Lat wrong", 0d, newLoc.getLat());
			Assert.assertEquals("easy Long wrong", 3d, newLoc.getLong());

			// now try more complex orientations

		}
		// public void testBounds()
		// {
		// Grid4WPainter pt = new Grid4WPainter();
		// pt.setOrigin(new WorldLocation(2,2,0));
		// pt.setXDelta(new WorldDistanceWithUnits(1, WorldDistanceWithUnits.DEGS));
		// pt.setYDelta(new WorldDistanceWithUnits(1, WorldDistanceWithUnits.DEGS));
		// pt.setXMin("C");
		// pt.setXMax("D");
		// pt.setYMin(3);
		// pt.setYMax(5);
		//		  
		// WorldArea bounds = pt.getBounds();
		// WorldLocation topLeft = new WorldLocation(4,7,0);
		// WorldLocation bottomRight= new WorldLocation(6,4,0);
		// WorldArea eBounds = new WorldArea(topLeft, bottomRight);
		// Assert.assertEquals("wrong bounds", eBounds, bounds);
		//		  
		// }

	}

	private static String indices[] =
	{ "A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD",
			"AE", "AF", "AG", "AH", "AJ", "AK", "AL", "AM", "AN", "AP", "AQ", "AR",
			"AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ" };

	/**
	 * find the index of the supplied string
	 * 
	 * @param target
	 * @return
	 */
	public static int indexOf(String target)
	{
		// trim it down
		target = target.trim();

		// move to upper case
		target = target.toUpperCase();

		// now find a match
		int res = 0;
		for (int i = 0; i < indices.length; i++)
		{
			if (indices[i].equals(target))
			{
				res = i;
				break;
			}
		}
		return res;
	}

	/**
	 * convert the integer index to a string
	 * 
	 * @param index
	 *          4w grid horizontal index
	 * @return character representation of index
	 */
	protected static String labelFor(int index)
	{
		return indices[index];
	}

	/**
	 * @return the xMin
	 */
	public String getXMin()
	{
		return labelFor(_xMin);
	}

	/**
	 * @param min
	 *          the xMin to set
	 */
	public void setXMin(String min)
	{
		_xMin = indexOf(min);
	}

	/**
	 * @return the xMax
	 */
	public String getXMax()
	{
		return labelFor(_xMax);
	}

	/**
	 * @param max
	 *          the xMax to set
	 */
	public void setXMax(String max)
	{
		_xMax = indexOf(max);
	}

	/**
	 * @return the yMin
	 */
	public Integer getYMin()
	{
		return _yMin + 1;
	}

	/**
	 * @param min
	 *          the yMin to set
	 */
	public void setYMin(Integer min)
	{
		_yMin = min - 1;
	}

	/**
	 * @return the yMax
	 */
	public Integer getYMax()
	{
		return _yMax + 1;
	}

	/**
	 * @param max
	 *          the yMax to set
	 */
	public void setYMax(Integer max)
	{
		_yMax = max - 1;
	}

	/**
	 * @return the origin
	 */
	public WorldLocation getOrigin()
	{
		return _origin;
	}

	/**
	 * @param origin
	 *          the origin to set
	 */
	public void setOrigin(WorldLocation origin)
	{
		_origin = origin;
	}

	/**
	 * @return the orientation
	 */
	public double getOrientation()
	{
		return _orientation;
	}

	/**
	 * @param orientation
	 *          the orientation to set
	 */
	public void setOrientation(double orientation)
	{
		_orientation = orientation;
	}

	/**
	 * @return the fillGrid
	 */
	public boolean getFillGrid()
	{
		return _fillGrid;
	}

	/**
	 * @param fillGrid
	 *          the fillGrid to set
	 */
	public void setFillGrid(boolean fillGrid)
	{
		_fillGrid = fillGrid;
	}

	/**
	 * @return the fillColor
	 */
	public Color getFillColor()
	{
		return _fillColor;
	}

	/**
	 * @param fillColor
	 *          the fillColor to set
	 */
	public void setFillColor(Color fillColor)
	{
		_fillColor = fillColor;
	}

	/**
	 * @return the plotLines
	 */
	public boolean getPlotLines()
	{
		return _plotLines;
	}

	/**
	 * @param plotLines
	 *          the plotLines to set
	 */
	public void setPlotLines(boolean plotLines)
	{
		_plotLines = plotLines;
	}

	@Override
	public void findNearestHotSpotIn(Point cursorPos, WorldLocation cursorLoc,
			LocationConstruct currentNearest, Layer parentLayer)
	{

		// initialise thisDist, since we're going to be over-writing it
		WorldDistance thisDist = new WorldDistance(calcLocationFor(_xMin, _yMin)
				.rangeFrom(cursorLoc), WorldDistance.DEGS);

		// is this our first item?
		currentNearest.checkMe(this, thisDist, null, parentLayer);

	}

	@Override
	public void shift(WorldVector vector)
	{
		_origin.addToMe(vector);
	}

}