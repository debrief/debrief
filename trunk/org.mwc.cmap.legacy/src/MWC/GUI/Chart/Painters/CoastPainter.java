package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: CoastPainter.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.13 $
// $Log: CoastPainter.java,v $
// Revision 1.13  2006/10/26 12:34:29  Ian.Mayo
// Better constructors
//
// Revision 1.12  2006/10/26 09:59:35  Ian.Mayo
// Improve declaration of coastlines
//
// Revision 1.11  2006/05/25 14:10:38  Ian.Mayo
// Make plottables comparable
//
// Revision 1.10  2005/09/23 14:54:14  Ian.Mayo
// Better error reporting
//
// Revision 1.9  2005/09/08 15:22:08  Ian.Mayo
// Correct the area plotted by the painter
//
// Revision 1.8  2005/09/08 10:59:37  Ian.Mayo
// Lots of support for deferred instantiation
//
// Revision 1.7  2005/09/08 08:56:53  Ian.Mayo
// Minor tidying of comments
//
// Revision 1.6  2005/09/07 15:42:28  Ian.Mayo
// Better error reporting
//
// Revision 1.5  2005/09/07 15:35:41  Ian.Mayo
// Refactor ToolParent so we can log errors through it
//
// Revision 1.4  2005/09/07 15:04:22  Ian.Mayo
// Refactor, to allow easier over-riding
//
// Revision 1.3  2004/08/31 09:38:04  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 14:46:55  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:15  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:11  Ian.Mayo
// Initial import
//
// Revision 1.4  2003-07-04 11:00:51+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.3  2002-07-12 15:46:54+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:40+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:14+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:28+01  administrator
// Initial revision
//
// Revision 1.4  2001-01-24 11:38:31+00  novatech
// getBounds returns null, so that on FitToWin, we don't expand to fill whole world
//
// Revision 1.3  2001-01-22 19:40:36+00  novatech
// reflect optimised projection.toScreen plotting
//
// Revision 1.2  2001-01-22 12:29:30+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:10  ianmayo
// initial version
//
// Revision 1.14  2000-11-17 09:34:29+00  ian_mayo
// changed system.out line
//
// Revision 1.13  2000-09-21 09:06:48+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.12  2000-08-21 09:49:57+01  ian_mayo
// correct logical operator
//
// Revision 1.11  2000-08-18 13:36:05+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.10  2000-08-11 08:41:59+01  ian_mayo
// tidy beaninfo
//
// Revision 1.9  2000-03-14 09:55:06+00  ian_mayo
// attempts at speed modifications
//
// Revision 1.8  2000-02-03 15:07:56+00  ian_mayo
// First issue to Devron (modified files are mostly related to WMF)
//
// Revision 1.7  2000-02-02 14:24:31+00  ian_mayo
// handle coast data-file not being found
//
// Revision 1.6  1999-12-03 14:33:56+00  ian_mayo
// allowed to read in coast data automatically
//
// Revision 1.5  1999-11-26 15:45:33+00  ian_mayo
// adding toString methods
//
// Revision 1.4  1999-11-25 16:53:42+00  ian_mayo
// temporarily forget about reading in data
//
// Revision 1.3  1999-11-25 13:32:14+00  ian_mayo
// tidied up comments
//
// Revision 1.2  1999-10-13 17:22:49+01  ian_mayo
// set as NOT VISIBLE by default
//
// Revision 1.1  1999-10-12 15:37:01+01  ian_mayo
// Initial revision
//
// Revision 1.2  1999-08-26 09:46:57+01  administrator
// painting coast
//
// Revision 1.1  1999-08-17 10:02:09+01  administrator
// Initial revision
//

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GUI.ToolParent;
import MWC.GUI.Coast.CoastSegment;
import MWC.GUI.Coast.Coastline;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.Errors.Trace;

public class CoastPainter implements Runnable, Serializable, Plottable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////
	transient static protected Coastline _myCoast;

	transient static protected boolean _loading = false;

	/**
	 * make the coastline visible by default
	 */
	private boolean _visible = true;

	/**
	 * the default color for the coastline
	 */
	private Color _myColor = Color.gray;

	/**
	 * static copy of parent, so we can retrieve the path
	 */
	private static MWC.GUI.ToolParent _myParent = null;

	/**
	 * our editor
	 */
	transient private Editable.EditorType _myEditor;

	/**
	 * the filename to use to read the coastline data
	 */
	final public static String FILENAME = "world.dat";

	/**
	 * the property name storing the location of the hi-res coastline file
	 */
	final public static String PREF_NAME = "LOW_RES_COAST_NAME";

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	/**
	 * copy constructor
	 */
	public CoastPainter(Coastline cl)
	{
		_myCoast = cl;
	}

	/**
	 * default constructor, loads global chart
	 */
	public CoastPainter(ToolParent parent)
	{
		this();
		_myParent = parent;
	}

	public CoastPainter()
	{
		initData();
	}

	/**
	 * special constructor factory - generate constructor but don't load data.
	 * we'll do that by hand if we need to
	 */
	public static CoastPainter getCoastPainterDontLoadData()
	{
		return new CoastPainter((Coastline) null);
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	/**
	 * load the data (in a separate thread of course)
	 */
	final public void initData()
	{
		if (!_loading)
			if (_myCoast == null)
			{
				// start loading data, in a new thread
				Thread runner = new Thread(this);
				runner.setPriority(Thread.MIN_PRIORITY);
				runner.start();
			}
	}

	/**
	 * find out where our coastline file is
	 * 
	 * @return stream pointing at the coastline file
	 */
	protected InputStream getCoastLineInput()
	{
		InputStream res = null;

		// hmm, see if there is a full file-location to use
		String fProp = _myParent.getProperty(PREF_NAME);

		if (fProp == null || fProp.equals(""))
			fProp = FILENAME;

		try
		{
			res = new FileInputStream(fProp);
		}
		catch (FileNotFoundException e)
		{
			_myParent.logError(ToolParent.ERROR, "Coastline file not found", e);
		}

		return res;
	}

	/**
	 * initialise the tool, so that it knows where to get it's layers information
	 * 
	 * @param theParent
	 */
	public static void initialise(ToolParent theParent)
	{
		_myParent = theParent;
	}

	final public void run()
	{
		// load our own coastline
		try
		{
			// we will have to read it in from file
			InputStream fs = getCoastLineInput();

			if (fs != null)
			{

				long tNow = System.currentTimeMillis();
				_myCoast = new Coastline(fs);
				fs.close();
				tNow = System.currentTimeMillis() - tNow;

				if (_myParent != null)
					_myParent.logError(ToolParent.INFO, "Coastline loaded after: " + tNow
							+ " millis", null);
			}
			else
			{
				if (_myParent != null)
					_myParent.logError(ToolParent.ERROR,
							"File not available. Coastline not loaded.", null);
			}
		}
		catch (Exception e)
		{
			if (_myParent != null)
				_myParent.logError(ToolParent.ERROR,
						"World file not found, coastlines not available:" + e.getMessage(), null);
			else
				Trace.trace("World file not found, coastlines not available:"
						+ e.getLocalizedMessage(), false);
		}
		_loading = false;
	}

	final public boolean getVisible()
	{
		return _visible;
	}

	final public void setVisible(boolean val)
	{
		_visible = val;
	}

	final public Color getColor()
	{
		return _myColor;
	}

	final public void setColor(Color val)
	{
		_myColor = val;
	}

	final public double rangeFrom(MWC.GenericData.WorldLocation other)
	{
		return INVALID_RANGE;
	}

	/**
	 * return this item as a string
	 */
	final public String toString()
	{
		return getName();
	}

	final public String getName()
	{
		String res = "Coast plotter";
		if (_myCoast == null)
			res = res + ": empty";
		else
			res = res + ": ready";

		return res;
	}

	final public boolean hasEditor()
	{
		return true;
	}

	final public Editable.EditorType getInfo()
	{
		if (_myEditor == null)
			_myEditor = new CoastPainterInfo(this);

		return _myEditor;
	}

	final public void resizedEvent(PlainProjection theProj, Dimension newScreenArea)
	{
		// not really interested, although we could use this
		// to keep an internal (threaded) eye on what sections are visible
	}

	final public WorldArea getBounds()
	{
		return null;
	}

	final public void paint(CanvasType dest)
	{
		// and get on with the painting
		if (_myCoast != null)
		{ // check we have data
			if (_visible) // check we are visible
				if (dest.getProjection().getDataArea() != null) // check the plot has
				// data
				{
					// draw to the canvas
					CoastSegment cs = null;
					PlainProjection proj = dest.getProjection();
					WorldArea dArea = proj.getVisibleDataArea();

					dest.setColor(_myColor);

					for (int i = 0; i < _myCoast.size(); i++)
					{
						cs = (CoastSegment) _myCoast.elementAt(i);
						boolean firstPoint = true;
						Point lastP = new Point();

						// see of this section of coastline is in our area
						if (cs.getBounds().overlaps(dArea))
						{
							// so, it is visible, let's draw it
							for (int j = 0; j < cs.size(); j++)
							{
								// get the next position
								WorldLocation loc = (WorldLocation) cs.elementAt(j);

								// see if this is a point we are interested in
								{
									if (firstPoint)
									{
										Point tmp = proj.toScreen(loc);
										lastP.move(tmp.x, tmp.y);
										firstPoint = false;
									}
									else
									{
										Point thisP = proj.toScreen(loc);

										dest.drawLine(lastP.x, lastP.y, thisP.x, thisP.y);
										lastP.move(thisP.x, thisP.y);
									}

								}

							}

						}
					}
				}
		}
	}

	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}

	// ///////////////////////////////////////////////////////////
	// info class
	// //////////////////////////////////////////////////////////
	final public class CoastPainterInfo extends Editable.EditorType implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public CoastPainterInfo(CoastPainter data)
		{
			super(data, data.getName(), "");
		}

		final public PropertyDescriptor[] getPropertyDescriptors()
		{
			try
			{
				PropertyDescriptor[] res = { prop("Color", "the Color to draw the coast"),
						prop("Visible", "whether the coast is visible"), };

				return res;
			}
			catch (IntrospectionException e)
			{
				return super.getPropertyDescriptors();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	final static public class CoastPainterTest extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public CoastPainterTest(String val)
		{
			super(val);
		}

		final public void testMyParams()
		{
			MWC.GUI.Editable ed = new CoastPainter((Coastline) null);
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}
}
