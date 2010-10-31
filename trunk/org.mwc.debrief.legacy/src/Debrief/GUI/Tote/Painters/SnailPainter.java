package Debrief.GUI.Tote.Painters;

// Copyright MWC 2000, Debrief 3 Project
// $RCSfile: SnailPainter.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.12 $
// $Log: SnailPainter.java,v $
// Revision 1.12  2007/04/16 09:48:08  ian.mayo
// Remove debug lines, slight JDK1.5 syntax updates (generics)
//
// Revision 1.11  2007/04/16 08:23:13  ian.mayo
// Include debug code
//
// Revision 1.10  2007/04/05 13:38:10  ian.mayo
// Improve how we decide whether to show plot highlight, make sure debrief legacy plugin gets installed
//
// Revision 1.9  2007/04/04 14:12:13  ian.mayo
// Correct how buoy patterns displayed by plot highlighter
//
// Revision 1.8  2005/07/01 15:34:25  Ian.Mayo
// Tidy how we retrieve non-watchables
//
// Revision 1.7  2005/07/01 08:49:45  Ian.Mayo
// Refactor, to allow use in Eclipse implementation
//
// Revision 1.6  2005/06/13 11:02:51  Ian.Mayo
// Minor tidying whilst overcoming annotation line widths in snail mode.
//
// Revision 1.5  2004/12/16 11:17:51  Ian.Mayo
// Only trigger redraw if area to be redrawn is visible
//
// Revision 1.4  2004/11/25 10:24:05  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.3  2004/11/22 13:40:50  Ian.Mayo
// Replace old variable name used for stepping through enumeration, since it is now part of language (Jdk1.5)
//
// Revision 1.2  2004/09/09 10:22:53  Ian.Mayo
// Reflect method name change in Layer interface
//
// Revision 1.1.1.2  2003/07/21 14:47:23  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.11  2003-07-04 10:59:18+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.10  2003-07-01 16:35:18+01  ian_mayo
// Change default value of vector stretch
//
// Revision 1.9  2003-06-23 13:41:33+01  ian_mayo
// Add TMA solution handling
//
// Revision 1.8  2003-03-19 15:37:59+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.7  2003-02-07 09:02:39+00  ian_mayo
// Remove unnecessary
//
// Revision 1.6  2002-12-16 15:12:24+00  ian_mayo
// Tidying & better Vector Stretch
//
// Revision 1.5  2002-10-28 09:04:33+00  ian_mayo
// provide support for variable thickness of lines in tracks, etc
//
// Revision 1.4  2002-07-10 14:59:25+01  ian_mayo
// handle correct returning of nearest points - zero length list instead of null when no matches
//
// Revision 1.3  2002-07-09 15:26:55+01  ian_mayo
// Minor renaming, and add BackgroundLayers as nonWatchables when in view
//
// Revision 1.2  2002-05-28 12:28:00+01  ian_mayo
// after update
//
// Revision 1.1  2002-05-28 09:12:17+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:30:01+01  ian_mayo
// Initial revision
//
// Revision 1.8  2002-03-18 20:36:15+00  administrator
// Only plot items if their layer is visible
//
// Revision 1.7  2002-03-13 08:57:59+00  administrator
// Reflect name change
//
// Revision 1.6  2002-01-17 20:21:45+00  administrator
// Reflect switch to Duration object
//
// Revision 1.5  2001-10-22 11:26:11+01  administrator
// Handle instance where no area of screen has been updated
//
// Revision 1.4  2001-10-03 16:07:35+01  administrator
// Provide flexibility in how we show our name (to allow us to be overwritten)
//
// Revision 1.3  2001-10-01 12:49:48+01  administrator
// the getNearest method of WatchableList now returns an array of points (since a contact wrapper may contain several points at the same DTG).  We have had to reflect this across the application
//
// Revision 1.2  2001-08-24 16:36:29+01  administrator
// Handle stepping before tracks assigned
//
// Revision 1.1  2001-08-14 14:07:09+01  administrator
// Add the new SnailDrawContact, and extend getWatchables to recognise any SensorWrapper's which are in a Track
//
// Revision 1.0  2001-07-17 08:41:39+01  administrator
// Initial revision
//
// Revision 1.8  2001-04-08 10:45:57+01  novatech
// Correct problem where LabelWrapper with times are stored as Watchables and Non-Watchables (since we did not recognise their type)
//
// Revision 1.7  2001-02-01 09:29:42+00  novatech
// implement correct handling of null time (-1, not 0 as before) and reflect fact that we no longer create/re-create our oldWatchables list, we empty and fill it
//
// Revision 1.6  2001-01-22 12:30:04+00  novatech
// added JUnit testing code
//
// Revision 1.5  2001-01-18 13:15:07+00  novatech
// create buoy plotter for snail mode
//
// Revision 1.4  2001-01-17 13:23:45+00  novatech
// reflect use of -1 as null time, rather than 0
//
// Revision 1.3  2001-01-15 11:21:28+00  novatech
// store the old points in a hashmap instead of a vector, so that the track can be stored aswell as the fix
//
// Revision 1.2  2001-01-09 10:27:25+00  novatech
// use WatchableList as well as  TrackWrapper
//
// Revision 1.1  2001-01-03 13:40:53+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:45:49  ianmayo
// initial import of files
//
// Revision 1.26  2000-11-24 10:53:23+00  ian_mayo
// tidying up
//
// Revision 1.25  2000-11-17 09:15:32+00  ian_mayo
// allow code to drop out if we can't create our graphics object (ie before panels are setVisible)
//
// Revision 1.24  2000-11-08 11:48:25+00  ian_mayo
// reflect change in status of TrackWrapper to Layer
//
// Revision 1.23  2000-11-02 16:45:48+00  ian_mayo
// changing Layer into Interface, replaced by BaseLayer, also changed TrackWrapper so that it implements Layer,  and as we read in files, we put them into track and add Track to Layers, not to Layer then Layers
//
// Revision 1.22  2000-10-17 16:07:09+01  ian_mayo
// move HighlightPlotting to before vector plotting, so that vectors are visible.  Play around with when we plot non-watchables, so that scale is always plotted
//
// Revision 1.21  2000-10-09 13:37:47+01  ian_mayo
// Switch stackTrace to go to file
//
// Revision 1.20  2000-10-03 14:17:17+01  ian_mayo
// draw primary with specified highlighter
//
// Revision 1.19  2000-09-27 14:47:40+01  ian_mayo
// name changes
//
// Revision 1.18  2000-09-27 14:31:45+01  ian_mayo
// put relativePlotting into correct place
//
// Revision 1.17  2000-09-26 09:50:37+01  ian_mayo
// support for relative plotting
//
// Revision 1.16  2000-09-22 11:44:51+01  ian_mayo
// add AnnotationPlotter, & improve method for detecting if a plottable should be added to the Watchable list or not
//
// Revision 1.15  2000-09-18 09:14:37+01  ian_mayo
// GUI name changes
//
// Revision 1.14  2000-08-30 14:49:05+01  ian_mayo
// rx background colour, instead of retrieving it yourself
//
// Revision 1.13  2000-08-14 15:50:05+01  ian_mayo
// GUI name changes
//
// Revision 1.12  2000-08-11 08:41:00+01  ian_mayo
// tidy beaninfo
//
// Revision 1.11  2000-07-07 09:58:59+01  ian_mayo
// Tidy up name of panel
//
// Revision 1.10  2000-06-19 15:06:19+01  ian_mayo
// newlines tidied up
//
// Revision 1.9  2000-06-06 12:43:47+01  ian_mayo
// replot full diagram, not just small areas (to overcome problem in JDK1.3)
//
// Revision 1.8  2000-04-03 10:19:21+01  ian_mayo
// switch to returning editable belonging to Painter, not us
//
// Revision 1.7  2000-03-27 14:44:01+01  ian_mayo
// redraw chart after we have been changed
//
// Revision 1.6  2000-03-17 13:38:44+00  ian_mayo
// Tidying up
//
// Revision 1.5  2000-03-14 09:52:39+00  ian_mayo
// allow configurable "leg" for vector plotting
//
// Revision 1.4  2000-03-09 11:26:31+00  ian_mayo
// add method/accessor to allow user to request vessel name on track
//
// Revision 1.3  2000-03-08 16:23:35+00  ian_mayo
// represent symbol shape size as bounded integer
//
// Revision 1.2  2000-03-08 14:26:10+00  ian_mayo
// further through implementation
//
// Revision 1.1  2000-03-07 13:44:08+00  ian_mayo
// Initial revision
//

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import Debrief.GUI.Tote.AnalysisTote;
import Debrief.Wrappers.BuoyPatternWrapper;
import Debrief.Wrappers.FixWrapper;
import Debrief.Wrappers.LabelWrapper;
import Debrief.Wrappers.SensorContactWrapper;
import Debrief.Wrappers.SensorWrapper;
import Debrief.Wrappers.ShapeWrapper;
import Debrief.Wrappers.TMAContactWrapper;
import Debrief.Wrappers.TMAWrapper;
import Debrief.Wrappers.TrackWrapper;
import Debrief.Wrappers.Track.TrackSegment;
import Debrief.Wrappers.Track.TrackWrapper_Support.SegmentList;
import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GUI.Plottable;
import MWC.GUI.CanvasType.PaintListener;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.HiResDate;
import MWC.GenericData.Watchable;
import MWC.GenericData.WatchableList;
import MWC.GenericData.WorldArea;

public class SnailPainter extends TotePainter
{

	public static final String SNAIL_NAME = "Snail";

	// /////////////////////////////////
	// member variables
	// ////////////////////////////////

	/**
	 * this is the list of items which we know have been plotted to the screen, so
	 * that we know which ones to hide
	 */
	private final HashMap<Watchable, WatchableList> _oldWatchables;

	/**
	 * the highlight plotters we know about
	 */
	private final Vector<SnailPainter.drawHighLight> _myHighlightPlotters;

	/**
	 * the size to draw myself
	 */
	protected final int _mySize = 5;

	/**
	 * the list of painters previously used by the canvas
	 */
	private Vector<PaintListener> _oldPainters;

	/**
	 * whether we are currently painting(hiding) the old points
	 */
	private boolean _paintingOldies;

	/**
	 * the snail track plotter to use
	 */
	private final SnailDrawFix _mySnailPlotter;

	/**
	 * the snail buoy-pattern plotter to use
	 */
	private final SnailDrawBuoyPattern _mySnailBuoyPlotter;

	// /////////////////////////////////
	// constructorsna
	// ////////////////////////////////
	public SnailPainter(final PlainChart theChart, final Layers theData,
			final AnalysisTote theTote)
	{
		this(theChart, theData, theTote, "Snail");
	}

	public SnailPainter(final PlainChart theChart, final Layers theData,
			final AnalysisTote theTote, final String myName)
	{
		super(theChart, theData, theTote);

		_oldWatchables = new HashMap<Watchable, WatchableList>();

		_mySnailPlotter = new SnailDrawFix(myName);
		_mySnailBuoyPlotter = new SnailDrawBuoyPattern();

		_myHighlightPlotters = new Vector<SnailPainter.drawHighLight>(0, 1);
		_myHighlightPlotters.addElement(_mySnailPlotter);
		_myHighlightPlotters.addElement(_mySnailBuoyPlotter);
		_myHighlightPlotters.addElement(new SnailDrawAnnotation());
		_myHighlightPlotters.addElement(new SnailDrawSensorContact(_mySnailPlotter));
		_myHighlightPlotters.addElement(new SnailDrawTMAContact(_mySnailPlotter));

		_mySnailPlotter.setPointSize(new BoundedInteger(5, 0, 0));
		_mySnailPlotter.setVectorStretch(1);

	}

	// /////////////////////////////////
	// member functions
	// ////////////////////////////////

	/**
	 * how much to stretch this vector
	 * 
	 * @return the stretch to apply
	 */
	public final double getVectorStretch()
	{
		return _mySnailPlotter.getVectorStretch();
	}

	/**
	 * the stretch to apply to the speed vector (pixels per knot)
	 * 
	 * @param val
	 *          the strech to use = 1 is 1 pixel per knot
	 */
	public final void setVectorStretch(final double val)
	{
		_mySnailPlotter.setVectorStretch(val);
	}

	/**
	 * ok, get the watchables for this layer
	 * 
	 * @param theData
	 *          the layer to extract the watchables for
	 * @return the set of watchable items in this layer
	 */
	public static Vector<Plottable> getWatchables(final Layer thisLayer)
	{
		// get the output ready
		final Vector<Plottable> res = new Vector<Plottable>(0, 1);

		// is this layer visible?
		if (thisLayer.getVisible())
		{
			// is this a watchable?
			if (thisLayer instanceof WatchableList)
			{
				// just double-check this isn't a buoy-pattern, we don't want to display
				// them
				if (thisLayer instanceof DoNotHighlightMe)
				{
					// ignore it, we don't want to plot it.
				}
				else
				{

					res.addElement(thisLayer);

					// just have a look if it's a track - if so we want to add it's
					// sensors
					if (thisLayer instanceof TrackWrapper)
					{
						final TrackWrapper trw = (TrackWrapper) thisLayer;

						// first plot the sensors
						final Enumeration<Editable> sensors = trw.getSensors().elements();
						if (sensors != null)
						{
							while (sensors.hasMoreElements())
							{
								final SensorWrapper sw = (SensorWrapper) sensors.nextElement();
								// just check if it's visible
								if (sw.getVisible())
								{
									res.add(sw);
								}
							}
						}

						// now the TMA solutons
						final Enumeration<Editable> solutions = trw.getSolutions().elements();
						if (solutions != null)
						{
							while (solutions.hasMoreElements())
							{
								final TMAWrapper sw = (TMAWrapper) solutions.nextElement();
								// just check if it's visible
								if (sw.getVisible())
								{
									res.add(sw);
								}
							}
						}
					}
				}
			}
			else
			{
				final Enumeration<Editable> iter = thisLayer.elements();
				while (iter.hasMoreElements())
				{
					final Plottable p = (Plottable) iter.nextElement();
					if (p instanceof WatchableList)
					{
						// look at the date date
						WatchableList wl = (WatchableList) p;
						HiResDate startDTG = wl.getStartDTG();

						// is it a real date?
						if (startDTG != null)
						{
							// yup, add to list
							res.addElement(p);
						}
					}
				}
			}
		}

		return res;
	}

	/**
	 * ok, get the watchables for this set of layers
	 * 
	 * @param theData
	 *          the layers to extract the watchables for
	 * @return the set of watchable items in these layers
	 */
	private static Vector<Plottable> getWatchables(final Layers theData)
	{
		final Vector<Plottable> res = new Vector<Plottable>(0, 1);
		// step through the layers
		final int cnt = theData.size();
		for (int i = 0; i < cnt; i++)
		{
			// ok, do this layer
			final Layer thisLayer = theData.elementAt(i);

			// get the watchables from this layer
			Vector<Plottable> newElements = getWatchables(thisLayer);

			// and add to our growing total
			res.addAll(newElements);
		}
		return res;
	}

	/**
	 * method to return the non-tactical items on the plot, such as scale, grid,
	 * coast etc.
	 */
	public static Vector<Plottable> getNonWatchables(final Layer thisLayer)
	{
		final Vector<Plottable> res = new Vector<Plottable>(0, 1);
		// is this layer visible?
		if (thisLayer.getVisible())
		{
			if (thisLayer instanceof Layer.BackgroundLayer)
			{
				res.addElement(thisLayer);
			}
			else
			{
				// just see if this layer is one of our back-ground layesr
				final Enumeration<Editable> iter = thisLayer.elements();
				while (iter.hasMoreElements())
				{
					final Plottable thisPlottable = (Plottable) iter.nextElement();
					if (thisPlottable instanceof ShapeWrapper)
					{
						// see if has a valid DTG -- IS IT TIME-RELATED?
						final ShapeWrapper swp = (ShapeWrapper) thisPlottable;
						final HiResDate dat = swp.getStartDTG();
						if (dat == null)
						{
							// let's use it
							res.addElement(thisPlottable);
						}
						else
						{
							// so it's got a date, check if the date represents our null
							// value
							// anyway
							if (dat.getMicros() == -1)
								res.addElement(thisPlottable);
						}
					}
					else if (thisPlottable instanceof LabelWrapper)
					{
						// see if has a valid DTG -- IS IT TIME-RELATED?
						final LabelWrapper lwp = (LabelWrapper) thisPlottable;
						final HiResDate dat = lwp.getStartDTG();
						// check if it is using our "null" date value
						if (dat == null)
						{
							// let's use it
							res.addElement(thisPlottable);
						}
						else
						{
							// it's got a date, which makes it one of our watchables, it
							// will
							// get caught elsewhere
						}
					}
					else
					{
						if ((thisPlottable instanceof FixWrapper)
								|| (thisPlottable instanceof TrackWrapper)
								|| (thisPlottable instanceof BuoyPatternWrapper)
								|| (thisPlottable instanceof SensorWrapper)
								|| (thisPlottable instanceof SensorContactWrapper)
								|| (thisPlottable instanceof TMAWrapper)
								|| (thisPlottable instanceof TrackSegment)
								|| (thisPlottable instanceof SegmentList)
								|| (thisPlottable instanceof TMAContactWrapper))
						{
							// leave it, it's track related
						}
						else
						{
							// it's not a shape - it's probably the grid or the scale,
							res.addElement(thisPlottable);
						} // whether it's one of our dynamic objects
					} // whether it's a labelwrapper
				} // looping through the elements of this layer
			} // if this is a background layer (or not)
		} // whether this layer is visible
		return res;
	}

	/**
	 * method to return the non-tactical items on the plot, such as scale, grid,
	 * coast etc.
	 */
	private static Vector<Plottable> getNonWatchables(final Layers theData)
	{
		final Vector<Plottable> res = new Vector<Plottable>(0, 1);
		// step through the layers
		final int cnt = theData.size();
		for (int i = 0; i < cnt; i++)
		{
			// right, now for the next layer
			final Layer thisLayer = theData.elementAt(i);

			// get the non-watchables for this layer
			final Vector<Plottable> theseElements = getNonWatchables(thisLayer);

			// and add them to our list
			res.addAll(theseElements);

		} // loop through the layers
		return res;
	}

	//
	public void steppingModeChanged(final boolean on)
	{
		if (on)
		{
			// remove the current painters for the canvas
			final Enumeration<CanvasType.PaintListener> iter = _theChart.getCanvas().getPainters();

			_oldPainters = new Vector<PaintListener>(0, 1);

			// take a copy of these painters
			while (iter.hasMoreElements())
			{
				_oldPainters.addElement((PaintListener) iter.nextElement());
			}

			// and remove the painters
			final Enumeration<CanvasType.PaintListener> oldies = _oldPainters.elements();
			while (oldies.hasMoreElements())
			{
				_theChart.getCanvas().removePainter(
						(CanvasType.PaintListener) oldies.nextElement());
			}

			// add us as a painter
			_theChart.getCanvas().addPainter(this);

			// and redraw the chart
			_theChart.update();

		}
		else
		{
			// remove us as a painter
			_theChart.getCanvas().removePainter(this);

			// restore the painters
			final Enumeration<CanvasType.PaintListener> oldies = _oldPainters.elements();
			while (oldies.hasMoreElements())
			{
				_theChart.getCanvas().addPainter((CanvasType.PaintListener) oldies.nextElement());
			}

		}

	}

	public final void newTime(final HiResDate oldDTG, final HiResDate newDTG,
			MWC.GUI.CanvasType canvas)
	{

		// check if we have any data
		if (_theTote.getPrimary() == null)
			return;

		// check we have a valid new DTG
		if (newDTG == null)
		{
			return;
		}

		// initialise the area covered
		_areaCovered = null;

		// prepare the chart
		if (canvas == null)
			canvas = _theChart.getCanvas();

		final Graphics2D dest = (Graphics2D) canvas.getGraphicsTemp();

		// just drop out if we can't create any graphics though
		if (dest == null)
			return;

		// switch to paint mode, so we can draw the background correctly
		dest.setPaintMode();

		// see if we are doing a fresh plot (in which case
		// we will plot the non-watchables aswell)
		if ((oldDTG == null) || (_oldWatchables.size() == 0))
		{
			final Vector<Plottable> nonWatches = getNonWatchables(super._theData);
			final Enumeration<Plottable> iter = nonWatches.elements();
			while (iter.hasMoreElements())
			{
				final Plottable p = (Plottable) iter.nextElement();
				p.paint(new MWC.GUI.Canvas.CanvasAdaptor(canvas.getProjection(), dest));
			}
		}

		dest.setXORMode(canvas.getBackgroundColor());

		final java.awt.Color backColor = canvas.getBackgroundColor();

		// get the primary track
		final WatchableList _thePrimary = _theTote.getPrimary();

		// hide the old watchables
		if (_oldWatchables.size() > 0)
		{
			_paintingOldies = true;

			// check this isn't the first step
			if (!_firstStep)
			{
				// find the watchable representing the current data point
				final Watchable[] wList = _thePrimary.getNearestTo(_lastDTG);

				// is the primary an instance of layer (with it's own line thickness?)
				if (_thePrimary instanceof Layer)
				{
					final Layer ly = (Layer) _thePrimary;
					dest.setStroke(new BasicStroke(ly.getLineThickness()));
				}

				// and carry on!

				Watchable oldPrimary = null;
				if (wList.length > 0)
					oldPrimary = wList[0];
				if (oldPrimary != null)
				{
					final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter thisHighlighter = getCurrentPrimaryHighlighter();
					if (thisHighlighter.getName().equals("Range Rings"))
					{
						thisHighlighter.highlightIt(canvas.getProjection(), dest, _theTote
								.getPrimary(), oldPrimary, true);
					}
				}

				// sort out the oldies
				final Iterator<Watchable> oldies = _oldWatchables.keySet().iterator();
				while (oldies.hasNext())
				{
					final Watchable thisOne = (Watchable) oldies.next();
					// is this one instance of layer (with it's own line thickness?)
					if (thisOne instanceof Layer)
					{
						final Layer ly = (Layer) thisOne;
						if (dest instanceof Graphics2D)
						{
							final Graphics2D g2 = (Graphics2D) dest;
							g2.setStroke(new BasicStroke(ly.getLineThickness()));
						}
					}

					// ok, clear the nearest items
					final WatchableList list = (WatchableList) _oldWatchables.get(thisOne);
					highlightIt(canvas.getProjection(), dest, list, thisOne, _lastDTG, backColor);
				}
			}

			// now remove all of the watchables from it
			_oldWatchables.clear();
		}

		// determine the new items
		final Vector<Plottable> theWatchableLists = getWatchables(super._theData);
		_paintingOldies = false;

		// sort out the line width of the primary
		if (_thePrimary instanceof Layer)
		{
			final Layer ly = (Layer) _thePrimary;
			if (dest instanceof Graphics2D)
			{
				final Graphics2D g2 = (Graphics2D) dest;
				g2.setStroke(new BasicStroke(ly.getLineThickness()));
			}
		}

		// show the current highlighter
		Watchable[] wList = _theTote.getPrimary().getNearestTo(newDTG);

		Watchable newPrimary = null;
		if (wList.length > 0)
			newPrimary = wList[0];
		if (newPrimary != null)
		{
			final Debrief.GUI.Tote.Painters.Highlighters.PlotHighlighter thisHighlighter = getCurrentPrimaryHighlighter();
			if (thisHighlighter.getName().equals("Range Rings"))
			{

				thisHighlighter.highlightIt(canvas.getProjection(), dest, _theTote.getPrimary(),
						newPrimary, true);
			}
		}

		// got through to highlight the data
		final Enumeration<Plottable> watches = theWatchableLists.elements();
		while (watches.hasMoreElements())
		{
			final WatchableList list = (WatchableList) watches.nextElement();
			// is the primary an instance of layer (with it's own line thickness?)
			if (list instanceof Layer)
			{
				final Layer ly = (Layer) list;
				if (dest instanceof Graphics2D)
				{
					final Graphics2D g2 = (Graphics2D) dest;
					g2.setStroke(new BasicStroke(ly.getLineThickness()));
				}
			}

			// ok, clear the nearest items
			wList = list.getNearestTo(newDTG);
			Watchable watch = null;
			if (wList.length > 0)
				watch = wList[0];

			if (watch != null)
			{
				// plot it
				highlightIt(canvas.getProjection(), dest, list, watch, newDTG, backColor);
			}
		}

		// restore the painting setup
		dest.setPaintMode();
		dest.dispose();

		// we know we're finished with the first step now anyway
		_firstStep = false;
		_lastDTG = newDTG;

		// do a repaint, if we have to
		if (!_inRepaint)
		{

			// are any of the bits which changed visible?
			if (_areaCovered != null)
			{

				// force a repaint of the plot

				// grow the area covered by a shade,
				_areaCovered.grow(2, 2);

				// see if we are trying to plot in relative mode - in which
				// case we need a full repaint
				if (canvas.getProjection().getNonStandardPlotting())
				{
					_theChart.update();
				}
				else
				{
					// and ask for an instant update
					// NOTE: changed this to stop JDK1.3
					// plotting in a purple background _theChart.repaintNow(_areaCovered);
					_theChart.repaint();
				}
			}
		}
		else
		{
		}

	}

	/**
	 * method to highlight a watchable.
	 */
	private void highlightIt(final PlainProjection proj, final Graphics dest,
			final WatchableList list, final Watchable watch, final HiResDate dtg,
			final java.awt.Color backColor)
	{
		// check that our graphics context is still valid -
		// we can't, so we will just have to trap any exceptions it raises
		try
		{
			// set the highlight colour
			dest.setColor(Color.white);

			// see if our plotters can plot this type of watchable
			final Enumeration<SnailPainter.drawHighLight> iter = _myHighlightPlotters.elements();
			while (iter.hasMoreElements())
			{
				final SnailPainter.drawHighLight plotter = (SnailPainter.drawHighLight) iter
						.nextElement();

				if (plotter.canPlot(watch))
				{
					// does this list have a width?
					if (list instanceof Layer)
					{
						final Layer ly = (Layer) list;
						if (dest instanceof Graphics2D)
						{
							final Graphics2D g2 = (Graphics2D) dest;
							g2.setStroke(new BasicStroke(ly.getLineThickness()));
						}
					}
					else if(list instanceof ShapeWrapper)
					{
						final ShapeWrapper sw = (ShapeWrapper)list;
						if(dest instanceof Graphics2D)
						{
							final Graphics2D g2 = (Graphics2D)dest;
							g2.setStroke(new BasicStroke(sw.getLineThickness()));
						}
					}

					final Rectangle rec = plotter.drawMe(proj, dest, list, watch, this, dtg,
							backColor);

					// add this to the list to be hidden at a later date
					if (!_paintingOldies)
						_oldWatchables.put(watch, list);

					// just check if a rectangle got returned at all (there may not
					// have been any valid data
					if (rec != null)
					{
						if (_areaCovered == null)
							_areaCovered = rec;
						else
							_areaCovered.add(rec);
					}

					// and drop out of the loop
					break;
				}
			}

		}
		catch (IllegalStateException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	public String toString()
	{
		return SNAIL_NAME;
	}

	public final String getName()
	{
		return toString();
	}

	public final boolean hasEditor()
	{
		return true;
	}

	/**
	 * NON-STANDARD implementation, we are returning the editor for our snail
	 * plotter object, not ourself
	 */
	public final Editable.EditorType getInfo()
	{
		return _mySnailPlotter.getInfo();
	}

	/**
	 * override the method provided by TotePainter. That method returns null,
	 * since it can rely on the other painters to generate the current data area
	 * --> there aren't any other painters here though, so we need to calculate it
	 */
	public final WorldArea getDataArea()
	{
		return this._theChart.getDataArea();
	}

	// ///////////////////////////////////////
	// accessors for the beaninfo
	// //////////////////////////////////////
	// ////////////////////////////////////////////////////////
	// accessors for editable parameters
	// ///////////////////////////////////////////////////////

	/**
	 * whether to plot in the name of the vessel
	 */
	public final boolean getPlotTrackName()
	{
		return _mySnailPlotter.getPlotTrackName();
	}

	/**
	 * whether to plot in the name of the vessel
	 */
	public final void setPlotTrackName(final boolean val)
	{
		_mySnailPlotter.setPlotTrackName(val);
	}

	// //////////////////////////////////////////////////////////
	// nested class describing how to edit this class
	// //////////////////////////////////////////////////////////
	// public class SnailPainterInfo extends Editable.EditorType
	// {
	//
	// public SnailPainterInfo(SnailPainter data)
	// {
	// super(data, "Snail","");
	// }
	//
	// /** extra constructor which may be over-ridden by the relative painter
	// *
	// */
	// public SnailPainterInfo(SnailPainter data, String name)
	// {
	// super(data, name,"");
	// }
	//
	// public BeanInfo[] getAdditionalBeanInfo()
	// {
	// BeanInfo[] res = {_mySnailPlotter.getInfo()};
	// return res;
	// }
	//
	// public PropertyDescriptor[] getPropertyDescriptors()
	// {
	// try{
	// PropertyDescriptor[] res=
	// {
	// prop("PlotTrackName", "whether to plot the name of the track"),
	// prop("LinkPositions", "whether to join the points in the trail"),
	// prop("PointSize", "the size of the points in the trail"),
	// prop("TrailLength", "the length of trail to draw"),
	// prop("VectorStretch", "how far to stretch the speed vector"),
	// };
	// return res;
	// }
	// catch(Exception e)
	// {
	// MWC.Utilities.Errors.Trace.trace(e);
	// return super.getPropertyDescriptors();
	// }
	//
	// }
	//
	// }

	// /////////////////////////////////////////////////
	// nested interface for painters which can draw snail trail components
	// /////////////////////////////////////////////////
	public interface drawHighLight
	{
		public java.awt.Rectangle drawMe(MWC.Algorithms.PlainProjection proj, Graphics dest,
				WatchableList list, Watchable watch, SnailPainter parent, HiResDate dtg,
				Color backColor);

		public boolean canPlot(Watchable wt);
	}
	
	/** marker interface used by classes that don't want to be highlighted
	 * 
	 * @author ian.mayo
	 *
	 */
	public interface DoNotHighlightMe
	{
		
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	static public final class testMe extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "UNIT";

		public testMe(final String val)
		{
			super(val);
		}

		public final void testMyParams()
		{
			Editable ed = new SnailPainter(null, null, null);
			editableTesterSupport.testParams(ed, this);
			ed = null;
		}
	}

}
