// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainChart.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.7 $
// $Log: PlainChart.java,v $
// Revision 1.7  2006/06/12 09:18:13  Ian.Mayo
// New data-extended message
//
// Revision 1.6  2006/04/05 08:12:25  Ian.Mayo
// Refactoring & tidying
//
// Revision 1.5  2005/06/07 15:26:23  Ian.Mayo
// Add accessibility method
//
// Revision 1.4  2004/10/21 15:30:46  Ian.Mayo
// Add support for overriding data updates
//
// Revision 1.3  2004/10/07 14:23:21  Ian.Mayo
// Reflect fact that enum is now keyword - change our usage to enumer
//
// Revision 1.2  2004/05/25 15:45:39  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:03  Ian.Mayo
// Initial import
//
// Revision 1.11  2003-06-09 09:23:21+01  ian_mayo
// refactored to remove extra rubberband parameter in chart drag listener call
//
// Revision 1.10  2003-06-09 09:20:42+01  ian_mayo
// improved working rubber band, before method refactoring (to remove band parameter)
//
// Revision 1.9  2003-06-05 16:31:48+01  ian_mayo
// Tidy up & support alternate drag mode (part way through tidying up rubber bands)
//
// Revision 1.7  2003-03-28 09:50:22+00  ian_mayo
// remove unnecessary import
//
// Revision 1.6  2003-03-28 09:50:04+00  ian_mayo
// Only do a rescale on data extended if we don't have a data area
//
// Revision 1.5  2003-03-06 15:29:34+00  ian_mayo
// Better check for not ready to Paint
//
// Revision 1.4  2003-02-07 09:49:26+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.3  2002-12-16 15:36:15+00  ian_mayo
// minor tidying
//
// Revision 1.2  2002-05-28 09:25:35+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:13+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:29+01  ian_mayo
// Initial revision
//
// Revision 1.6  2002-02-26 09:48:52+00  administrator
// Let the Layers object sort out its own bounds
//
// Revision 1.5  2002-01-24 14:22:28+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.4  2001-11-14 19:54:27+00  administrator
// Provide accessor for the layers
//
// Revision 1.3  2001-08-31 09:58:05+01  administrator
// Don't clear the layers, not just yet
//
// Revision 1.2  2001-08-17 07:57:05+01  administrator
// Switch anonymous adapters to local instantiations
//
// Revision 1.1  2001-07-27 17:08:25+01  administrator
// make getPanel() method part of this parent class
//
// Revision 1.0  2001-07-17 08:46:37+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 12:11:56+00  novatech
// handle mouse operations without any data on plot
//
// Revision 1.2  2001-01-21 21:36:36+00  novatech
// set ourselves as a listener for data being refformatted
//
// Revision 1.1  2001-01-03 13:43:08+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:42:55  ianmayo
// initial version
//
// Revision 1.12  2000-11-02 15:13:16+00  ian_mayo
// remove PRINT lines, to make jdk1.1 compliant
//
// Revision 1.11  2000-10-10 14:09:34+01  ian_mayo
// added print method
//
// Revision 1.10  2000-10-09 13:35:52+01  ian_mayo
// Switched stack traces to go to log file
//
// Revision 1.9  2000-05-19 11:23:14+01  ian_mayo
// white space only
//
// Revision 1.8  2000-04-19 11:39:21+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.7  2000-04-03 10:56:27+01  ian_mayo
// add method comments for the abstract methods
//
// Revision 1.6  2000-03-27 14:37:58+01  ian_mayo
// "only differs by white space"
//
// Revision 1.5  1999-12-13 10:37:16+00  ian_mayo
// removed paint method from update call
//
// Revision 1.4  1999-12-03 14:36:36+00  ian_mayo
// redraw after resize
//
// Revision 1.3  1999-11-30 11:17:51+00  ian_mayo
// prevent mouse events cascading when we don't yet have a valid projection
//
// Revision 1.2  1999-11-26 15:45:06+00  ian_mayo
// adding toString method
//
// Revision 1.1  1999-10-12 15:37:09+01  ian_mayo
// Initial revision
//
// Revision 1.4  1999-08-17 08:14:41+01  administrator
// changes to way layers data is passed, and how rubberbands are handled
//
// Revision 1.3  1999-08-04 14:03:10+01  administrator
// pass Layers data to Right-click tool
//
// Revision 1.2  1999-08-04 09:43:05+01  administrator
// make tools serializable
//
// Revision 1.1  1999-07-27 10:50:51+01  administrator
// Initial revision
//
// Revision 1.4  1999-07-16 10:01:46+01  administrator
// Nearing end of phase 2
//
// Revision 1.3  1999-07-12 08:09:19+01  administrator
// Property editing added
//
// Revision 1.2  1999-07-08 13:08:45+01  administrator
// <>
//
// Revision 1.1  1999-07-07 11:10:09+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:38:01+01  sm11td
// Initial revision
//
// Revision 1.3  1999-06-01 16:49:17+01  sm11td
// Reading in tracks aswell as fixes, commenting large portions of source code
//
// Revision 1.2  1999-02-04 08:02:29+00  sm11td
// Plotting to canvas, scaling canvas,
//
// Revision 1.1  1999-01-31 13:33:13+00  sm11td
// Initial revision
//

package MWC.GUI;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Stack;
import java.util.Vector;

import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * GUI-independent implemententation of a chart
 */
abstract public class PlainChart implements Pane, CanvasType.PaintListener, Serializable,
		MouseListener, MouseMotionListener
{

	// ////////////////////////////////////////////////////////
	// member variables
	// ////////////////////////////////////////////////////////

	/**
	 * serialisation constant, to manage file version control
	 */
	static final long serialVersionUID = 1;

	/**
	 * keep track of if we have resized this panel or not yet
	 */
	private Dimension _theSize;

	/**
	 * the data we are plotting/handling
	 */
	protected Layers _theLayers;

	/**
	 * classes which want to listen out for area selections
	 */
	transient protected ChartDragListener _theChartDragListener;

	/**
	 * classes which want to listen out for area selections
	 */
	transient protected ChartDragListener _theAlternateChartDragListener;

	/**
	 * class which is listening out for double clicks
	 */
	transient protected ChartDoubleClickListener _theChartDblClickListener;

	transient protected Stack<ChartDoubleClickListener> _theDblClickListeners;

	/**
	 * classes which is listening out for left and right (res) single clicks on
	 * the chart
	 */
	transient protected ChartClickListener _theLeftClickListener;

	transient protected ChartClickListener _theRightClickListener;

	/**
	 * classes which want to listen out for any cursor movements
	 */
	transient protected Vector<ChartCursorMovedListener> _movementListeners;

	/**
	 * let people listen out for reformatting changes
	 */
	private transient DataListenerAdaptor _dataReformattedListener;

	// ////////////////////////////////////////////////////////
	// constructor
	// ////////////////////////////////////////////////////////
	public PlainChart(Layers theLayers)
	{
		// create a common listener for both types of change
		_dataReformattedListener = new DataListenerAdaptor()
		{

			public void dataReformatted(Layers theData, Layer changedLayer)
			{
				if (!_suspendUpdates)
					update(changedLayer);
			}

			public void dataModified(Layers theData, Layer changedLayer)
			{
				if (!_suspendUpdates)
					update(changedLayer);
			}

			public void dataExtended(Layers theData)
			{
				if (!_suspendUpdates)
				{

					// do we need to rescale the data?
					if (getCanvas().getProjection().getDataArea() == null)
						rescale();

					// and trigger a full repaint
					update();
				}
			}

			/** ok, we know which specific layer has been updated. fire away...
			 * 
			 * @param theData
			 * @param newItem
			 * @param parent
			 */
			public void dataExtended(Layers theData, Plottable newItem, Layer parent)
			{
				if (!_suspendUpdates)
				{

					// do we need to rescale the data?
					if (getCanvas().getProjection().getDataArea() == null)
						rescale();

					// and trigger a full repaint
					update(parent);
				}
			}
		};

		// store the layers object, and listen to it, if we gave to..
		setLayers(theLayers);

		produceListeners();

	}

	public void setLayers(Layers theLayers)
	{
		// hmm, do we have an exising set of layers?
		if (_theLayers != null)
		{
			// yup, better remove ourselves
			// listen out for new data being added
			_theLayers.removeDataExtendedListener(_dataReformattedListener);

			// listen out for the data being reformatted
			_theLayers.removeDataReformattedListener(_dataReformattedListener);

			// listen out for the data being modified
			_theLayers.removeDataModifiedListener(_dataReformattedListener);
		}

		// register interest in the layers
		_theLayers = theLayers;

		// is it a valid layesr object
		if (_theLayers != null)
		{
			// listen out for new data being added
			_theLayers.addDataExtendedListener(_dataReformattedListener);

			// listen out for the data being reformatted
			_theLayers.addDataReformattedListener(_dataReformattedListener);

			// listen out for the data being modified
			_theLayers.addDataModifiedListener(_dataReformattedListener);
		}
	}

	// ////////////////////////////////////////////////////////
	// member functions, mostly callbacks
	// ////////////////////////////////////////////////////////

	protected void produceListeners()
	{
		_movementListeners = new Vector<ChartCursorMovedListener>(0, 1);
		_theDblClickListeners = new Stack<ChartDoubleClickListener>();
	}

	/** select a zoom that shows all visible data
	 * 
	 */
	abstract public void rescale();

	/**
	 * redraw the screen from scratch
	 */
	abstract public void update();

	/**
	 * redraw the indicated layer (or all layers if null)
	 * 
	 * @param changedLayer
	 *          the layer which needs repaining (or null for all)
	 */
	abstract public void update(Layer changedLayer);

	/**
	 * just refresh the double-buffer
	 */
	abstract public void repaint();

	/**
	 * refresh a portion of the double-buffer
	 */
	abstract public void repaintNow(java.awt.Rectangle rect);

	public abstract Dimension getScreenSize();

	public void canvasResized()
	{
		/**
		 * HACK: in this function we're having to overcome the fact that the panel
		 * gets 3 resize calls for each real resize
		 */

		// see if we are still initialising our chart
		Dimension sz = getScreenSize();
		if ((sz.width != 0) && (sz.height != 0))
		{
			_theSize = sz;
			getCanvas().getProjection().zoom(0.0);
			getCanvas().updateMe();
		}
	}

	/**
	 * tell the layers that even though they've been modified, they don't need to
	 * do a repaint
	 * 
	 * @param suspendUpdates
	 *          yes/no
	 */
	public void setSuspendUpdates(boolean suspendUpdates)
	{
		_dataReformattedListener.setSuspendUpdates(suspendUpdates);
	}

	// ////////////////////////////////////////////////////////
	// methods for handling requests from our canvas
	// ////////////////////////////////////////////////////////
	public void paintMe(CanvasType dest)
	{
		// do a repaint, instruct the layers to paint
		_theLayers.paint(dest);
	}

	public WorldArea getDataArea()
	{
		WorldArea res = _theLayers.getBounds();
		return res;
	}

	public void resizedEvent(MWC.Algorithms.PlainProjection theProj, Dimension newScreenArea)
	{
		// don't really bother doing anything here - since the canvas repaints
		// itself
	}

	public String toString()
	{
		return getName();
	}

	public String getName()
	{
		return "Chart";
	}

	public abstract CanvasType getCanvas();

	/**
	 * get the layers object being plotted by this canvas
	 */
	public Layers getLayers()
	{
		return _theLayers;
	}

	// /////////////////////////////////////////////////////////
	// interfaces for listener classes
	// /////////////////////////////////////////////////////////

	/**
	 * definition of function to be implemented if an area is selected on the
	 * chart
	 */
	public interface ChartDragListener
	{
		public void areaSelected(WorldLocation theLocation, Point thePoint);

		public void startDrag(WorldLocation theLocation, Point thePoint);

		public void dragging(WorldLocation theLocation, Point thePoint);

		public Rubberband getRubberband();
	}

	/**
	 * add an area listener
	 */
	public void setAlternateChartDragListener(ChartDragListener theListener)
	{
		// stop the old listener from listening
		if (_theAlternateChartDragListener != null)
		{
			Rubberband oldBand = _theAlternateChartDragListener.getRubberband();
			if (oldBand != null)
			{
				oldBand.removeFromComponent(this.getPanel());
				oldBand.setActive(false);
			}
		}

		// remember the new listener
		_theAlternateChartDragListener = theListener;

		// set it's rubberband to active, if we have one
		Rubberband newBand = _theAlternateChartDragListener.getRubberband();
		if (newBand != null)
		{
			newBand.setActive(true);
		}
	}

	/**
	 * add an area listener
	 */
	public void setChartDragListener(ChartDragListener theListener)
	{

		// stop the old listener from listening
		if (_theChartDragListener != null)
		{
			Rubberband oldBand = _theChartDragListener.getRubberband();
			if (oldBand != null)
			{
				oldBand.removeFromComponent(this.getPanel());
				oldBand.setActive(false);
			}
		}

		_theChartDragListener = theListener;

		// set the new rubber band as active
		// if(theBand != null)
		// theBand.setActive(true);

		// // and store the rubber
		// setRubberBand(theListener.getRubberband());
		//
		// if(_myRubber != null){
		// _myRubber.setActive(true);
		// }
	}

	/**
	 * remove an area listener
	 */
	public void removeChartDragListener(ChartDragListener theListener)
	{
		_theChartDragListener = null;
	}

	public ChartDragListener getChartDragListener()
	{
		return _theChartDragListener;
	}

	/**
	 * return the gui component for the chart
	 */
	abstract public java.awt.Component getPanel();

	// //////////////////////////////////////////////////////////
	// more listener convenience methods
	// //////////////////////////////////////////////////////////
	/**
	 * definition of function to be implemented for each mouse movement
	 */
	public interface ChartCursorMovedListener
	{
		public void cursorMoved(WorldLocation thePos, boolean dragging, Layers theData);
	}

	/**
	 * add a cursor movement listener
	 */
	public void addCursorMovedListener(ChartCursorMovedListener theListener)
	{
		_movementListeners.addElement(theListener);
	}

	/**
	 * remove a cursor movement listener
	 */
	public void removeCursorMovedListener(ChartCursorMovedListener theListener)
	{
		_movementListeners.removeElement(theListener);
	}

	// //////////////////////////////////////////////////////////
	// double-click listener interface
	// //////////////////////////////////////////////////////////
	public interface ChartDoubleClickListener
	{
		public void cursorDblClicked(PlainChart theChart, WorldLocation theLocation,
				Point thePoint);
	}

	/**
	 * add a cursor movement listener
	 */
	public void addCursorDblClickedListener(ChartDoubleClickListener theListener)
	{
		_theDblClickListeners.addElement(theListener);
	}

	/**
	 * remove a cursor movement listener
	 */
	public void removeCursorDblClickedListener(ChartDoubleClickListener theListener)
	{
		_theDblClickListeners.removeElement(theListener);
	}

	// //////////////////////////////////////////////////////////
	// more listener convenience methods
	// //////////////////////////////////////////////////////////
	/**
	 * definition of function to be implemented for each mouse movement
	 */
	public interface ChartClickListener
	{
		public void CursorClicked(Point thePoint, WorldLocation thePos, CanvasType theCanvas,
				Layers theData);
	}

	/**
	 * add a cursor movement listener
	 */
	public void addLeftClickListener(ChartClickListener theListener)
	{
		_theLeftClickListener = theListener;
	}

	/**
	 * remove a cursor movement listener
	 */
	public void removeLeftClickListener(ChartClickListener theListener)
	{
		_theLeftClickListener = theListener;
	}

	/**
	 * add a cursor movement listener
	 */
	public void addRightClickListener(ChartClickListener theListener)
	{
		_theRightClickListener = theListener;
	}

	/**
	 * remove a cursor movement listener
	 */
	public void removeRightClickListener(ChartClickListener theListener)
	{
		_theRightClickListener = theListener;
	}

	// //////////////////////////////////////////////////////////
	// mouse events
	// //////////////////////////////////////////////////////////
	public void mouseClicked(MouseEvent p1)
	{
		// get the world location for this point
		WorldLocation val = getCanvas().toWorld(p1.getPoint());

		// is it duff? - drop out if it is
		if (val == null)
			return;

		WorldLocation wl = new WorldLocation(val);

		if (p1.getClickCount() == 2)
		{
			if (_theDblClickListeners.size() > 0)
			{

				// get the top one off the stack
				ChartDoubleClickListener lc = (ChartDoubleClickListener) _theDblClickListeners
						.lastElement();
				lc.cursorDblClicked(this, wl, p1.getPoint());
			}
		}
		else
		{
			// single click,

			// see if it is left or right
			if ((p1.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
			{
				// popup trigger is a right-mouse click, tell the listener
				if (this._theRightClickListener != null)
				{
					_theRightClickListener
							.CursorClicked(p1.getPoint(), wl, getCanvas(), _theLayers);
				}
			}
			else
			{
				if (_theLeftClickListener != null)
				{
					_theLeftClickListener.CursorClicked(p1.getPoint(), wl, getCanvas(), _theLayers);
				}
			}

		}

	}

	public void mousePressed(MouseEvent p1)
	{
		// see if it was a right-click
		if ((p1.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
			return;

		// see if we have a valid position
		WorldLocation val = getCanvas().toWorld(p1.getPoint());
		// do we have a valid position?
		if (val != null)
		{
			WorldLocation theLoc = new WorldLocation(val);

			Rubberband theRubber = null;

			if ((p1.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
			{
				if (_theAlternateChartDragListener != null)
				{
					theRubber = _theAlternateChartDragListener.getRubberband();
					_theAlternateChartDragListener.startDrag(theLoc, p1.getPoint());
				}
			}
			else
			{
				if (_theChartDragListener != null)
				{
					theRubber = _theChartDragListener.getRubberband();
					_theChartDragListener.startDrag(theLoc, p1.getPoint());
				}
			}

			if (theRubber != null)
			{
				theRubber.setComponent(getPanel());
				theRubber.setActive(true);
				theRubber.mousePressed(p1);
			}

		}

	}

	public void mouseReleased(MouseEvent p1)
	{
		// see if it was a right-click
		if ((p1.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
			return;

		WorldLocation val = getCanvas().toWorld(p1.getPoint());

		// do we have a valid position?
		if (val != null)
		{
			WorldLocation theLoc = new WorldLocation(val);

			Rubberband theRubber = null;

			// was this the middle button?
			if ((p1.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
			{
				if (_theAlternateChartDragListener != null)
				{
					_theAlternateChartDragListener.areaSelected(theLoc, p1.getPoint());
					theRubber = _theAlternateChartDragListener.getRubberband();
				}
			}
			else
			{
				if (_theChartDragListener != null)
				{
					_theChartDragListener.areaSelected(theLoc, p1.getPoint());
					theRubber = _theChartDragListener.getRubberband();
				}
			}

			if (theRubber != null)
			{
				theRubber.setActive(false);
				theRubber.removeFromComponent(getPanel());
			}

		}
	}

	public void mouseEntered(MouseEvent p1)
	{
	}

	public void mouseExited(MouseEvent p1)
	{
	}

	public void mouseDragged(MouseEvent p1)
	{
		// see if it was a right-click
		if ((p1.getModifiers() & MouseEvent.BUTTON3_MASK) != 0)
			return;

		// get the location, we'll prob want it anyway
		WorldLocation val = getCanvas().toWorld(p1.getPoint());

		// do we have a valid position?
		if (val != null)
		{
			WorldLocation theLoc = new WorldLocation(val);

			// see which mouse was being dragged
			if ((p1.getModifiers() & MouseEvent.BUTTON2_MASK) != 0)
			{
				// middle mouse button being dragged - do our alternate behaviour!!
				// see if we have a drag listener
				if (_theAlternateChartDragListener != null)
				{
					_theAlternateChartDragListener.dragging(theLoc, p1.getPoint());
				}
			}
			else
			{
				if (_movementListeners != null)
				{
					Enumeration<ChartCursorMovedListener> enumer = _movementListeners.elements();
					while (enumer.hasMoreElements())
					{
						ChartCursorMovedListener cl = (ChartCursorMovedListener) enumer.nextElement();
						// check that we have managed to create a
						// location
						if (theLoc != null)
						{
							cl.cursorMoved(theLoc, true, _theLayers);
						}
					}
				}

				// see if we have a drag listener
				if (_theChartDragListener != null)
				{
					_theChartDragListener.dragging(theLoc, p1.getPoint());
				}

			} // whether it was middle or right
		} // whether we have a valid position
	}

	public void mouseMoved(MouseEvent p1)
	{
		mouseMoved(p1.getPoint());
	}

	/**
	 * simpler-to-access version of mouse-moved event handler
	 * 
	 * @param x
	 * @param y
	 */
	public void mouseMoved(Point thePoint)
	{
		if (_movementListeners != null)
		{
			Enumeration<ChartCursorMovedListener> enumer = _movementListeners.elements();
			while (enumer.hasMoreElements())
			{
				ChartCursorMovedListener cl = (ChartCursorMovedListener) enumer.nextElement();
				WorldLocation val = getCanvas().toWorld(thePoint);
				// check that we have managed to create a
				// location
				if (val != null)
				{
					WorldLocation lp = new WorldLocation(getCanvas().toWorld(thePoint));
					cl.cursorMoved(lp, false, _theLayers);
				}
			}
		}

	}

	/**
	 * provide method to clear stored data
	 */
	public void close()
	{
		// ok, forget the layesr object
		setLayers(null);
		
		// and our reformatted listener
		_dataReformattedListener = null;

		// remove the layer listeners
		_theSize = null;
		_theLayers = null;
		_theChartDragListener = null;
		_theChartDblClickListener = null;
		_theDblClickListeners = null;
		_theLeftClickListener = null;
		_theRightClickListener = null;
		_movementListeners.removeAllElements();
		_movementListeners = null;

	}

	public void save(ObjectOutputStream os)
	{
		// create the output stream
		try
		{
			// save this
			os.writeObject(_theSize);
			os.writeObject(_theLayers);
			// os.writeObject(this._theDblClickListeners);
			// os.writeObject(this._theLeftClickListener);
			// os.writeObject(this._theRightClickListener);
			// os.writeObject(this._movementListeners);
			// os.writeObject(this._myRubber);
		}
		catch (IOException e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	public void restore(ObjectInputStream is)
	{
		try
		{
			_theSize = (Dimension) is.readObject();
			_theLayers = (Layers) is.readObject();

		}
		catch (Exception e)
		{
			MWC.Utilities.Errors.Trace.trace(e);
		}
	}

	/*
	 * public int print(final java.awt.Graphics dest,final
	 * java.awt.print.PageFormat format,int pageIndex) throws
	 * java.awt.print.PrinterException { if(pageIndex != 0) return NO_SUCH_PAGE; //
	 * get the graphics as a 2D, then offset it by the imageable area, so that we //
	 * don't try to print into the margins Graphics2D g2 = (Graphics2D)dest;
	 * g2.translate(format.getImageableX(), format.getImageableY()); // put this
	 * canvas into an adaptor MWC.GUI.Canvas.CanvasAdaptor canv = new
	 * MWC.GUI.Canvas.CanvasAdaptor(getCanvas().getProjection(), dest); // pant
	 * the object this.paintMe(canv); // return the success flag return
	 * PAGE_EXISTS; }
	 */
}
