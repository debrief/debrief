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
// $RCSfile: SWTCanvas.java,v $
// @author $Author$
// @version $Revision$
// $Log: SWTCanvas.java,v $
// Revision 1.31  2007/06/05 14:57:37  ian.mayo
// Improved tooltip handling
//
// Revision 1.30  2007/01/25 15:53:56  ian.mayo
// Better GC maangement
//
// Revision 1.29  2007/01/19 11:39:28  ian.mayo
// Eclipse-related tidying
//
// Revision 1.28  2006/11/28 10:51:37  Ian.Mayo
// Convert multi-line layer labels to single line
//
// Revision 1.27  2006/09/22 13:25:12  Ian.Mayo
// Don't bother reporting how long it took to do screen update
//
// Revision 1.26  2006/05/31 13:40:17  Ian.Mayo
// Minor tidying
//
// Revision 1.25  2006/05/17 08:34:30  Ian.Mayo
// Reduce number of instances where we set canvas-color
//
// Revision 1.24  2006/05/16 08:45:16  Ian.Mayo
// Include another separator
//
// Revision 1.23  2006/04/06 13:31:13  Ian.Mayo
// Output time-to-plot to log for performance tracking
//
// Revision 1.22  2006/04/06 13:03:13  Ian.Mayo
// Ditch performance indicators
//
// Revision 1.21  2006/04/05 08:33:59  Ian.Mayo
// Minor tidying
//
// Revision 1.20  2006/02/23 11:49:07  Ian.Mayo
// Tidying
//
// Revision 1.19  2005/12/09 14:54:37  Ian.Mayo
// Add right-click property editing
//
// Revision 1.18  2005/11/14 10:28:24  Ian.Mayo
// Double-check everything is there before we start plotting
//
// Revision 1.17  2005/09/08 11:01:41  Ian.Mayo
// Makeing more robust when plotting fails through disposed GC
//
// Revision 1.16  2005/08/31 15:02:23  Ian.Mayo
// Do display updates in UI thread
//
// Revision 1.15  2005/06/22 13:22:00  Ian.Mayo
// Part way through implementation of copy location from plot
//
// Revision 1.14  2005/06/22 10:27:43  Ian.Mayo
// Insert tests, tidy export of location to clipboard
//
// Revision 1.13  2005/06/22 09:18:32  Ian.Mayo
// Tidy implementation of actions which receive location data
//
// Revision 1.12  2005/06/20 08:06:10  Ian.Mayo
// Experiment with right-click support (copy location)
//
// Revision 1.11  2005/06/15 14:30:11  Ian.Mayo
// Refactor, so that we can call it more easily from WMF painter
//
// Revision 1.10  2005/06/15 11:03:42  Ian.Mayo
// Overcome tidying error
//
// Revision 1.9  2005/06/14 09:49:28  Ian.Mayo
// Eclipse-triggered tidying (unused variables)
//
// Revision 1.8  2005/05/26 14:04:50  Ian.Mayo
// Tidy up double-buffering
//
// Revision 1.7  2005/05/26 07:53:56  Ian.Mayo
// Minor tidying
//
// Revision 1.6  2005/05/25 15:31:54  Ian.Mayo
// Get double-buffering going
//
// Revision 1.5  2005/05/25 14:18:17  Ian.Mayo
// Refactor to provide more useful SWT GC wrapper (hopefully suitable for buffered images)
//
// Revision 1.4  2005/05/24 13:26:42  Ian.Mayo
// Start including double-click support.
//
// Revision 1.3  2005/05/24 07:35:57  Ian.Mayo
// Ignore anti-alias bits, sort out text-writing in filling areas
//
// Revision 1.2  2005/05/20 15:34:44  Ian.Mayo
// Hey, practically working!
//
// Revision 1.1  2005/05/20 13:45:03  Ian.Mayo
// Start doing chart
//
//

package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Dimension;
import java.util.Enumeration;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.mwc.cmap.core.CorePlugin;
import org.mwc.cmap.core.ui_support.swt.SWTCanvasAdapter;

import MWC.Algorithms.PlainProjection;
import MWC.GUI.CanvasType;
import MWC.GUI.GeoToolsHandler;
import MWC.GenericData.WorldLocation;

/**
 * Swing implementation of a canvas.
 */
public class SWTCanvas extends SWTCanvasAdapter
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	protected boolean _deferPaints = true;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private org.eclipse.swt.widgets.Canvas _myCanvas = null;

	/**
	 * an event queue - where we place screen update events, to trim down lots of
	 * consecutive screen updates
	 */
//	private EventStack _eventQue = new EventStack(100);

	/**
	 * our double-buffering safe copy.
	 */
	private transient Image _dblBuff;

	private LocationSelectedAction _copyLocation;

	Shell _tooltip;

	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////

	/**
	 * default constructor.
	 * 
	 * @param projection
	 */
	public SWTCanvas(final Composite parent, final GeoToolsHandler projection)
	{
		super((PlainProjection) projection);
		// super(null);

		_myCanvas = new Canvas(parent, SWT.NO_BACKGROUND);
		_myCanvas.addMouseTrackListener(new MouseTrackAdapter()
		{

			public void mouseHover(final MouseEvent e)
			{
				String tip = getTheToolTipText(new java.awt.Point(e.x, e.y));

				// clear the existing tooltip
				_myCanvas.setToolTipText(null);

				if (tip != null)
				{
					if (tip.length() > 0)
					{
						// strip out the HTML
						tip = tip.replace("<u>", "");
						tip = tip.replace("</u>", "");
						tip = tip.replace("\\n", " ");
						tip = tip.replace("<BR>", "\n");
						tip = tip.replace("<html><font face=\"sansserif\">", "");
						tip = tip.replace("</font></html>", "");

						_myCanvas.setToolTipText(tip);
					}
				}
			}
		});
		_myCanvas.addMouseMoveListener(new MouseMoveListener()
		{
			public void mouseMove(final MouseEvent e)
			{
				if (_tooltip == null)
					return;

				_tooltip.dispose();
				_tooltip = null;
			}
		});

		// add handler to catch canvas resizes
		_myCanvas.addControlListener(new ControlAdapter()
		{

			public void controlResized(final ControlEvent e)
			{
				final Point pt = _myCanvas.getSize();
				final Dimension dim = new Dimension(pt.x, pt.y);
				setScreenSize(dim);
			}
		});

		// setup our own painter
		_myCanvas.addPaintListener(new org.eclipse.swt.events.PaintListener()
		{
			public void paintControl(final PaintEvent e)
			{
				repaintMe(e);
			}
		});

		// _myCanvas.setBackground(ColorHelper.getColor(java.awt.Color.BLUE));

		_myCanvas.addMouseListener(new MouseAdapter()
		{

			/**
			 * @param e
			 */
			public void mouseUp(final MouseEvent e)
			{
				if (e.button == 3)
				{
					// cool, right-had button. process it
					final MenuManager mmgr = new MenuManager();
					final Point display = Display.getCurrent().getCursorLocation();
					final Point scrPoint = _myCanvas.toControl(display);
					final WorldLocation targetLoc = getProjection().toWorld(
							new java.awt.Point(scrPoint.x, scrPoint.y));
					fillContextMenu(mmgr, scrPoint, targetLoc);
					final Menu thisM = mmgr.createContextMenu(_myCanvas);
					thisM.setVisible(true);
				}
			}

		});
	}

	/**
	 * indicate whether paint events should build up, with only the most recent
	 * event getting painted
	 * 
	 * @param defer
	 *          yes/no
	 */
	public void setDeferPaints(final boolean defer)
	{
		_deferPaints = defer;
	}

	/**
	 * ok - insert the right-hand button related items
	 * 
	 * @param mmgr
	 * @param scrPoint
	 */
	protected void fillContextMenu(final MenuManager mmgr, final Point scrPoint,
			final WorldLocation loc)
	{
		// right, we create the actions afresh each time here. We can't
		// automatically calculate it.
		_copyLocation = new LocationSelectedAction("Copy cursor location",
				SWT.PUSH, loc)
		{
			/**
			 * @param loc
			 *          the converted world location for the mouse-click
			 * @param pt
			 *          the screen coordinate of the click
			 */
			public void run(final WorldLocation theLoc)
			{
				// represent the location as a text-string
				final String locText = CorePlugin.toClipboard(theLoc);

				// right, copy the location to the clipboard
				final Clipboard clip = CorePlugin.getDefault().getClipboard();
				final Object[] data = new Object[]
				{ locText };
				final Transfer[] types = new Transfer[]
				{ TextTransfer.getInstance() };
				clip.setContents(data, types);

			}
		};

		mmgr.add(_copyLocation);
		mmgr.add(new Separator());

	}

	// ////////////////////////////////////////////////////
	// screen redraw related
	// ////////////////////////////////////////////////////

	protected void repaintMe(final PaintEvent pe)
	{

		// paintPlot(pe.gc);
		// get the graphics destination
		final GC gc = pe.gc;

		// put double-buffering code in here.
		if (_dblBuff == null)
		{
			// ok, create the new image
			final Point theSize = _myCanvas.getSize();

			if ((theSize.x == 0) || (theSize.y == 0))
				return;

			_dblBuff = new Image(Display.getCurrent(), theSize.x, theSize.y);
			final GC theDest = new GC(_dblBuff);

			// prepare the ground (remember the graphics dest for a start)
			startDraw(theDest);

			// and paint into it
			paintPlot(this);

			// all finished, close it now
			endDraw(null);

			// and ditch the GC
			theDest.dispose();

		}

		// finally put the required bits of the target image onto the screen
		if (_dblBuff != null)
			gc.drawImage(_dblBuff, pe.x, pe.y, pe.width, pe.height, pe.x, pe.y,
					pe.width, pe.height);
		else
		{
			CorePlugin.logError(Status.INFO,
					"Double-buffering failed, no image produced", null);
		}
	}
	
	public Image getImage()
	{
			final Point theSize = _myCanvas.getSize();
			if ((theSize.x == 0) || (theSize.y == 0))
				return null;

			Image image = new Image(Display.getCurrent(), theSize.x, theSize.y);
			final GC theDest = new GC(image);

			// prepare the ground (remember the graphics dest for a start)
			startDraw(theDest);

			// and paint into it
			paintPlot(this);

			// all finished, close it now
			endDraw(null);

			// and ditch the GC
			theDest.dispose();
			return image;
	}

	/**
	 * the real paint function, called when it's not satisfactory to just paint in
	 * our safe double-buffered image.
	 * 
	 * @param g1
	 */
	public void paintPlot(final CanvasType dest)
	{
		// go through our painters
		final Enumeration<PaintListener> enumer = _thePainters.elements();
		while (enumer.hasMoreElements())
		{
			final CanvasType.PaintListener thisPainter = (CanvasType.PaintListener) enumer
					.nextElement();

			// check the screen has been defined
			final Dimension area = this.getProjection().getScreenArea();
			if ((area == null) || (area.getWidth() <= 0) || (area.getHeight() <= 0))
			{
				return;
			}

			// it must be ok
			thisPainter.paintMe(dest);
		}
	}

	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////

	// ///////////////////////////////////////////////////////////
	// projection related
	// //////////////////////////////////////////////////////////
	/**
	 * handler for a screen resize - inform our projection of the resize then
	 * inform the painters.
	 */
	public void setScreenSize(final java.awt.Dimension p1)
	{
		super.setScreenSize(p1);

		// check if this is a real resize
		if ((_theSize == null) || (!_theSize.equals(p1)))
		{
			// inform our parent
			_myCanvas.setSize(p1.width, p1.height);

			// erase the double buffer, (if we have one)
			// since it is now invalid
			if (_dblBuff != null)
			{
				_dblBuff.dispose();
				_dblBuff = null;
			}

			// inform the listeners that we have resized
			final Enumeration<PaintListener> enumer = _thePainters.elements();
			while (enumer.hasMoreElements())
			{
				final CanvasType.PaintListener thisPainter = (CanvasType.PaintListener) enumer
						.nextElement();
				thisPainter.resizedEvent(_theProjection, p1);
			}

		}
	}

	// ///////////////////////////////////////////////////////////
	// graphics plotting related
	// //////////////////////////////////////////////////////////

	/**
	 * first repaint the plot, then trigger a screen update
	 */
	public final void updateMe()
	{
		if (_dblBuff != null)
		{
			if (!_dblBuff.isDisposed())
			{
				_dblBuff.dispose();
			}
			_dblBuff = null;
		}

		redraw();

	}

	/**
	 * provide close method, clear elements.
	 */
	public final void close()
	{
		if (_dblBuff != null)
			if (!_dblBuff.isDisposed())
				_dblBuff.dispose();
		_dblBuff = null;
	}

	public String getName()
	{
		return "SWT Canvas";
	}

	public void redraw(final int x, final int y, final int width, final int height, final boolean b)
	{
		_myCanvas.redraw(x, y, width, height, b);
	}

	private long lastRun = 0;

	/**
	 * perform an immediate redraw, not a deferred one like we do for an updateme
	 * operation
	 * 
	 */
	public void redraw()
	{

		final long TIME_INTERVAL = 150;
		
		if (_deferPaints)
		{

			final long tNow = System.currentTimeMillis();
			final long elapsed = tNow - lastRun;
			if (elapsed > TIME_INTERVAL)
			{
				lastRun = tNow;

				// nope, fire it right away
				final Display thisD = Display.getDefault();
				if (thisD != null)
					thisD.syncExec(new Runnable()
					{

						public void run()
						{
							_myCanvas.redraw();
						}
					});
			}

			// NOTE: this is the previous stacked events, used to reduce number of
			// redraws
			//
			// if (!_myCanvas.isDisposed())
			// {
			// // create the runnable to place in the que
			// Runnable runme = new Runnable()
			// {
			// public void run()
			// {
			// if (!_myCanvas.isDisposed())
			// {
			// _myCanvas.redraw();
			// }
			// }
			// };
			//
			// // add it to the cache
			// _eventQue.addEvent(runme);
			// }
		}
		else
		{
			// nope, fire it right away
			final Display thisD = Display.getDefault();
			if (thisD != null)
				thisD.syncExec(new Runnable()
				{

					public void run()
					{
						_myCanvas.redraw();
					}
				});
		}
	}

	public void addControlListener(final ControlAdapter adapter)
	{
		_myCanvas.addControlListener(adapter);
	}

	public void addMouseMoveListener(final MouseMoveListener listener)
	{
		_myCanvas.addMouseMoveListener(listener);
	}

	public void addMouseListener(final MouseListener listener)
	{
		_myCanvas.addMouseListener(listener);

	}

	public void addMouseWheelListener(final MouseWheelListener listener)
	{
		_myCanvas.addMouseWheelListener(listener);
	}

	public Control getCanvas()
	{
		return _myCanvas;
	}

	public abstract static class LocationSelectedAction extends Action
	{

		WorldLocation _theLoc;

		/**
		 * pass some parameters back to the main parent
		 * 
		 * @param text
		 * @param style
		 * @param theCanvas
		 *          - used to get the screen coords
		 * @param theProjection
		 *          - our screen/world converter
		 */
		public LocationSelectedAction(final String text, final int style, final WorldLocation theLoc)
		{
			super(text, style);
			_theLoc = theLoc;
		}

		/**
		 * 
		 */
		public void run()
		{
			// ok - trigger our geospatial operation
			run(_theLoc);
		}

		/**
		 * so, the user has selected a chart location, process the selection
		 * 
		 * @param loc
		 */
		abstract public void run(WorldLocation loc);
	}

	// ////////////////////////////////////////////////
	// testing code...
	// ////////////////////////////////////////////////
	static public class testImport extends junit.framework.TestCase
	{
		static public final String TEST_ALL_TEST_TYPE = "CONV";

		public testImport(final String val)
		{
			super(val);
		}

		public void testClipboardTextManagement()
		{
			WorldLocation theLoc = new WorldLocation(12.3, 12.555555, 1.2);
			String txt = CorePlugin.toClipboard(theLoc);
			assertEquals("correct string not produced", "LOC:12.3,12.555555,1.2", txt);

			// check for valid location
			Object validStr;
			validStr = CorePlugin.fromClipboard(txt);
			assertNotNull("is a location string", validStr);

			// and check for duff location
			validStr = CorePlugin.fromClipboard("aasdfasdfasdfadf");
			assertNull("is a location string", validStr);

			// and back to the location
			final WorldLocation loc2 = CorePlugin.fromClipboard(txt);
			assertEquals("correct location parsed back in", theLoc, loc2);

			// try southern/western location
			theLoc = new WorldLocation(-12.3, -12.555555, -1.2);
			txt = CorePlugin.toClipboard(theLoc);
			assertEquals("correct string not produced", "LOC:-12.3,-12.555555,-1.2",
					txt);

			WorldLocation loc3 = CorePlugin.fromClipboard("12.5 13.5");
			assertNotNull("is a location string", loc3);

			loc3 = CorePlugin.fromClipboard("-12.5 13.5");
			assertNotNull("is a location string", loc3);

			loc3 = CorePlugin.fromClipboard("-12.5\t 13.5");
			assertNotNull("is a location string", loc3);

			loc3 = CorePlugin.fromClipboard("-12.5t 13.5");
			assertNull("is not a location string", loc3);

			loc3 = CorePlugin.fromClipboard("12 01 02 N 14 12 32 W");
			assertNotNull("is a location string", loc3);

			loc3 = CorePlugin.fromClipboard("12 01 02.225 S 14 12 32.116 E");
			assertNotNull("is a location string", loc3);

		}
	}
}
