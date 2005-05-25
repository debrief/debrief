// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.6  2005-05-25 13:24:42  Ian.Mayo
// Tidy background painting
//
// Revision 1.5  2005/05/24 14:09:49  Ian.Mayo
// Sort out mouse-clicked events
//
// Revision 1.4  2005/05/24 13:26:43  Ian.Mayo
// Start including double-click support.
//
// Revision 1.3  2005/05/24 07:39:54  Ian.Mayo
// Start mouse support
//
// Revision 1.2  2005/05/20 15:34:45  Ian.Mayo
// Hey, practically working!
//
// Revision 1.1  2005/05/20 13:45:04  Ian.Mayo
// Start doing chart
//
//

package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Tracker;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * The Chart is a canvas placed in a panel. the majority of functionality is
 * contained in the PlainChart parent class, only the raw comms is in this
 * class. This is configured by setting the listeners to the chart/panel to be
 * the listener functions defined in the parent.
 */
public class SWTChart extends PlainChart implements Serializable
{

	// ///////////////////////////////////////////////////////////
	// member variables
	// //////////////////////////////////////////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SWTCanvas _theCanvas;

	/**
	 * our list of layered images.
	 */
	private HashMap _myLayers = new HashMap();

	/**
	 * the data area we last plotted (so that we know when a full layered repaint
	 * is needed).
	 */
	private WorldArea _lastDataArea = null;

	/**
	 * the image we paint into the corner of the canvas.
	 */
	// private Image _ourImage;
	// ///////////////////////////////////////////////////////////
	// constructor
	// //////////////////////////////////////////////////////////
	/**
	 * constructor, providing us with the set of layers to plot.
	 * 
	 * @param theLayers
	 *          the data to plot
	 */
	public SWTChart(final Layers theLayers, Composite parent)
	{
		super(theLayers);
		_theCanvas = createCanvas(parent);
		_theCanvas.setProjection(new MWC.Algorithms.Projections.FlatProjection());

		// sort out the area of coverage of the plot
		WorldArea area = theLayers.getBounds();
		_theCanvas.getProjection().setDataArea(area);

		// add us as a painter to the canvas
		_theCanvas.addPainter(this);

		// catch any resize events
		_theCanvas.addControlListener(new ControlAdapter()
		{
			public void controlResized(final ControlEvent e)
			{
				canvasResized();
			}
		});

		Dimension dim = _theCanvas.getSize();

		if (dim != null)
			_theCanvas.getProjection().setScreenArea(dim);

		_theCanvas.addMouseMoveListener(new MouseMoveListener()
		{

			public void mouseMove(MouseEvent e)
			{
				doMouseMove(e);
			}
		});
		_theCanvas.addMouseListener(new MouseListener()
		{

			public void mouseDoubleClick(MouseEvent e)
			{
				doMouseDoubleClick(e);
			}

			public void mouseDown(MouseEvent e)
			{
				doMouseDown(e);
			}

			public void mouseUp(MouseEvent e)
			{
				doMouseUp(e);
			}
		});

		// store the rubber band
		// setRubberBand(new MWC.GUI.RubberBanding.RubberbandRectangle());

		// create the tooltip handler
		_theCanvas.setTooltipHandler(new MWC.GUI.Canvas.BasicTooltipHandler(
				theLayers));

	}

	/**
	 * constructor, providing us with a set of layers to plot, together with a
	 * background image.
	 * 
	 * @param theLayers
	 *          the data to plot
	 * @param imageName
	 *          the image to show as a water-mark
	 */
	// public SWTChart(final Layers theLayers, final String imageName)
	// {
	// this(theLayers);
	//
	// // try to open the image
	// final URL imageURL = getClass().getClassLoader().getResource(imageName);
	// if (imageURL != null)
	// {
	// final ImageIcon io = new ImageIcon(imageURL);
	// _ourImage = io.getImage();
	// }
	// }
	// ///////////////////////////////////////////////////////////
	// member functions
	// //////////////////////////////////////////////////////////
	public void canvasResized()
	{
		// and clear out our buffered layers (they all need to be repainted anyway)
		_myLayers.clear();

		// now we've cleared the layers, call the parent resize method (which causes
		// a repaint
		// of the layers)
		super.canvasResized();
	}

	/**
	 * over-rideable member function which allows us to over-ride the canvas which
	 * gets used.
	 * 
	 * @return the Canvas to use
	 */
	public SWTCanvas createCanvas(Composite parent)
	{
		return new SWTCanvas(parent);
	}

	/**
	 * get the size of the canvas.
	 * 
	 * @return the dimensions of the canvas
	 */
	public final java.awt.Dimension getScreenSize()
	{
		Dimension dim = _theCanvas.getSize();
		// get the current size of the canvas
		return dim;
	}

	public final Component getPanel()
	{
		System.err.println("NOT RETURNING PANEL");
		return null;
		// return _theCanvas;
	}

	public final Control getCanvasControl()
	{
		return _theCanvas.getCanvas();
	}

	public final void update()
	{
		// just check we have some data

		// clear out the layers object
		_myLayers.clear();

		// and start the update
		_theCanvas.updateMe();
	}

	public final void update(final Layer changedLayer)
	{
		if (changedLayer == null)
		{
			_theCanvas.updateMe();
		}
		else
		{
			// just delete that layer
			_myLayers.remove(changedLayer);

			// chuck in a GC, to clear the old image allocation
			System.gc();

			// and trigger update
			_theCanvas.updateMe();
		}
	}

	/**
	 * over-ride the parent's version of paint, so that we can try to do it by
	 * layers.
	 */
	public final void paintMe(final CanvasType dest)
	{

		 // draw in the solid background
		 paintBackground(dest);
		
		 super.paintMe(dest);
//
//		// check that we have a valid canvas (that the sizes are set)
//		final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
//		if (sArea != null)
//		{
//			if (sArea.width > 0)
//			{
//
//				// hey, we've plotted at least once, has the data area changed?
//				if (_lastDataArea != _theCanvas.getProjection().getDataArea())
//				{
//					// remember the data area for next time
//					_lastDataArea = _theCanvas.getProjection().getDataArea();
//
//					// clear out all of the layers we are using
//					_myLayers.clear();
//				}
//
//				// draw in the solid background
//				paintBackground(dest);
//
//				int canvasHeight = _theCanvas.getSize().height;
//				int canvasWidth = _theCanvas.getSize().width;
//
//				// ok, pass through the layers, repainting any which need it
//				final int len = _theLayers.size();
//				for (int i = 0; i < len; i++)
//				{
//					final Layer thisLayer = _theLayers.elementAt(i);
//
//					boolean isAlreadyPlotted = false;
//
//					// just check if this layer is visible
//					if (thisLayer.getVisible())
//					{
//
//						if (doubleBufferPlot())
//						{
//
//							// check we're plotting to a SwingCanvas, because we don't
//							// double-buffer
//							// anything else
//							if (dest instanceof SWTCanvas)
//							{
//
//								// does this layer want to be double-buffered?
//								if (thisLayer instanceof BaseLayer)
//								{
//
//									// just check if there is a property which over-rides the
//									// double-buffering
//									final BaseLayer bl = (BaseLayer) thisLayer;
//									if (bl.isBuffered())
//									{
//										isAlreadyPlotted = true;
//
//										// do our double-buffering bit
//										// do we have a layer for this object
//										org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) _myLayers
//												.get(thisLayer);
//										if (image == null)
//										{
//											// sure it is, create an image to paint into (the
//											// TYPE_INT_ARGB ensures it
//											// has a transparent background)
//											image = new org.eclipse.swt.graphics.Image(Display.getCurrent(), canvasWidth, canvasHeight);
//											
//											GC newGC = new GC(image);
//											
//											// image = new BufferedImage(_theCanvas.getWidth(),
//											// _theCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
////											image.
////											final Graphics g2 = image.getGraphics();
////
////											// wrap the Graphics to make it look like a CanvasType
////											final CanvasAdaptor ca = new CanvasAdaptor(_theCanvas
////													.getProjection(), g2);
//
//											// draw into it
//											thisLayer.paint(newGC);
//
//											// ditch the graphics
//											g2.dispose();
//
//											// store this image in our list, indexed by the layer
//											// object itself
//											_myLayers.put(thisLayer, image);
//										}
//
//										// have we ended up with an image to paint?
//										if (image != null)
//										{
//											// get the graphics to paint to
//											final Graphics gr = dest.getGraphicsTemp();
//
//											if (gr != null)
//											{
//												// lastly add this image to our Graphics object
//												gr.drawImage(image, 0, 0, _theCanvas);
//
//												// and ditch it
//												gr.dispose();
//
//											}
//											else
//												MWC.Utilities.Errors.Trace
//														.trace("SwingChart.PaintMe() :FAILED TO GET GRAPHICS TEMP");
//										}
//
//									}
//								}
//							} // whether we were plotting to a SwingCanvas (which may be
//							// double-buffered
//						} // whther we are happy to do double-buffering
//
//						// did we manage to paint it
//						if (!isAlreadyPlotted)
//						{
//							thisLayer.paint(dest);
//
//							isAlreadyPlotted = true;
//						}
//					}
//				}
//			}
//		}

	}

	/**
	 * property to indicate if we are happy to perform double-buffering. -
	 * override it to change the response
	 */
	protected boolean doubleBufferPlot()
	{
		return true;
	}

	/**
	 * paint the solid background.
	 * 
	 * @param dest
	 *          where we're painting to
	 */
	private void paintBackground(final CanvasType dest)
	{
		// fill the background, to start with
		final Dimension sz = _theCanvas.getSize();
		
		final Color theCol = dest.getBackgroundColor();
		dest.setBackgroundColor(theCol);
//		dest.setColor(java.awt.Color.black);
//		dest.setBackgroundColor(Color.black);
		dest.fillRect(0, 0, sz.width, sz.height);
		System.out.print("b");

		// // do we have an image?
		// if (_ourImage != null)
		// {
		// // find the coords
		// final int imgWidth = _ourImage.getWidth(getPanel());
		// final int imgHeight = _ourImage.getHeight(getPanel());
		//
		// // find the point to paint at
		// final Point thePt = new Point((int) dest.getSize().getWidth() - imgWidth
		// - 3,
		// (int) dest.getSize().getHeight() - imgHeight - 3);
		//
		// // paint in our logo
		// dest.drawImage(_ourImage, thePt.x, thePt.y, imgWidth, imgHeight,
		// getPanel());
		// }
	}

	// ////////////////////////////////////////////////////////
	// methods for handling requests from our canvas
	// ////////////////////////////////////////////////////////

	public final void rescale()
	{
		// do a rescale
		_theCanvas.rescale();

	}

	public final void repaint()
	{
		// we were doing a repaint = now an updaet
		_theCanvas.updateMe();
	}

	public final void repaintNow(final java.awt.Rectangle rect)
	{
		_theCanvas.redraw(rect.x, rect.y, rect.width, rect.height, true);
		// _theCanvas.paintImmediately(rect);
	}

	public final CanvasType getCanvas()
	{
		return _theCanvas;
	}

	/**
	 * provide method to clear stored data.
	 */
	public void close()
	{
		// clear the layers
		_myLayers.clear();
		_myLayers = null;

		// instruct the canvas to close
		_theCanvas.close();
		_theCanvas = null;

		super.close();
	}

	public void doMouseMove(MouseEvent e)
	{
		// todo: PRODUCE NEW MOUSE EVENT TRANSLATOR!!!
		if (_startPoint == null)
			return;
		if (_dragTracker != null)
			return;

		int deltaX = _startPoint.x - e.x;
		int deltaY = _startPoint.y - e.y;
		if (Math.abs(deltaX) < JITTER && Math.abs(deltaY) < JITTER)
			return;
		_dragTracker = new Tracker((Composite) _theCanvas.getCanvas(), SWT.RESIZE);
		Rectangle rect = new Rectangle(_startPoint.x, _startPoint.y, deltaX, deltaY);
		_dragTracker.setRectangles(new Rectangle[] { rect });
		boolean dragResult = _dragTracker.open();
		if (dragResult)
		{
			Rectangle[] rects = _dragTracker.getRectangles();
			Rectangle res = rects[0];
			// get world area
			java.awt.Point tl = new java.awt.Point(res.x, res.y);
			java.awt.Point br = new java.awt.Point(res.x + res.width, res.y
					+ res.height);
			WorldLocation locA = new WorldLocation(_theCanvas.getProjection()
					.toWorld(tl));
			WorldLocation locB = new WorldLocation(_theCanvas.getProjection()
					.toWorld(br));
			WorldArea area = new WorldArea(locA, locB);

			System.out.println("zooming in on:" + area);

			_theCanvas.getProjection().setDataArea(area);
			_theCanvas.updateMe();

			_dragTracker = null;
			_startPoint = null;
		}
		else
		{
			System.out.println("user cancelled drag operation!");
		}
	}

	private final int JITTER = 2;

	private Tracker _dragTracker = null;

	private Point _startPoint = null;

	protected void doMouseUp(MouseEvent e)
	{
		// ok. did we move at all?
		Point thisP = new Point(e.x, e.y);
		if (thisP.equals(_startPoint))
		{
			// hey, it was just a click - process it
			if (_theLeftClickListener != null)
			{
				// get the world location
				java.awt.Point jPoint = new java.awt.Point(e.x, e.y);
				WorldLocation loc = getCanvas().getProjection().toWorld(jPoint);
				_theLeftClickListener.CursorClicked(jPoint, loc, getCanvas(),
						_theLayers);
			}
		}
		_startPoint = null;
	}

	protected void doMouseDown(MouseEvent e)
	{
		if (_dragTracker == null)
		{
			_startPoint = new Point(e.x, e.y);
		}
	}

	protected void doMouseDoubleClick(MouseEvent e)
	{

		// was this the right-hand button
		if (e.button == 3)
		{
			_theCanvas.rescale();
			_theCanvas.updateMe();
		}
		else
		{
			// right, find out which one it was.
			java.awt.Point pt = new java.awt.Point(e.x, e.y);

			// and now the WorldLocation
			WorldLocation loc = getCanvas().getProjection().toWorld(pt);

			// and now see if we are near anything..
			if (_theDblClickListeners.size() > 0)
			{
				// get the top one off the stack
				ChartDoubleClickListener lc = (ChartDoubleClickListener) _theDblClickListeners
						.lastElement();
				lc.cursorDblClicked(this, loc, pt);
			}
		}
	}
}
