// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.19  2005-09-16 10:11:37  Ian.Mayo
// Reflect changed mouse event signature
//
// Revision 1.18  2005/09/13 15:43:25  Ian.Mayo
// Try to get dragging modes working
//
// Revision 1.17  2005/09/13 13:46:25  Ian.Mayo
// Better drag mode support
//
// Revision 1.16  2005/08/31 15:03:38  Ian.Mayo
// Minor tidying
//
// Revision 1.15  2005/07/01 08:17:46  Ian.Mayo
// refactor, so we can override layer painting
//
// Revision 1.14  2005/06/14 15:21:03  Ian.Mayo
// fire update after zoom
//
// Revision 1.13  2005/06/14 09:49:29  Ian.Mayo
// Eclipse-triggered tidying (unused variables)
//
// Revision 1.12  2005/06/09 14:51:51  Ian.Mayo
// Implement SWT plotting
//
// Revision 1.11  2005/06/07 15:29:57  Ian.Mayo
// Add panel to show current cursor position
//
// Revision 1.10  2005/06/07 10:50:02  Ian.Mayo
// Ignore right-clicks for drag,mouse-up
//
// Revision 1.9  2005/05/26 14:04:51  Ian.Mayo
// Tidy up double-buffering
//
// Revision 1.8  2005/05/26 07:34:47  Ian.Mayo
// Minor tidying
//
// Revision 1.7  2005/05/25 15:31:55  Ian.Mayo
// Get double-buffering going
//
// Revision 1.6  2005/05/25 13:24:42  Ian.Mayo
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

import java.awt.*;
import java.awt.Color;
import java.util.HashMap;

import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;

import MWC.GUI.*;
import MWC.GenericData.*;

/**
 * The Chart is a canvas placed in a panel. the majority of functionality is
 * contained in the PlainChart parent class, only the raw comms is in this
 * class. This is configured by setting the listeners to the chart/panel to be
 * the listener functions defined in the parent.
 */
public class SWTChart extends PlainChart
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

	private final int JITTER = 3;

	/**
	 * track drag operations
	 */
	private Point _startPoint = null;
	
	/** the last point dragged over
	 * 
	 */
	private Point _draggedPoint = null;

	private PlotMouseDragger _myDragMode;

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
		_theCanvas.setTooltipHandler(new MWC.GUI.Canvas.BasicTooltipHandler(theLayers));

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

	public final SWTCanvas getSWTCanvas()
	{
		return _theCanvas;
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

		Image template = null;

		// check that we have a valid canvas (that the sizes are set)
		final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
		if (sArea != null)
		{
			if (sArea.width > 0)
			{

				// hey, we've plotted at least once, has the data area changed?
				if (_lastDataArea != _theCanvas.getProjection().getDataArea())
				{
					// remember the data area for next time
					_lastDataArea = _theCanvas.getProjection().getDataArea();

					// clear out all of the layers we are using
					_myLayers.clear();
				}

				int canvasHeight = _theCanvas.getSize().height;
				int canvasWidth = _theCanvas.getSize().width;

				paintBackground(dest);

				// ok, pass through the layers, repainting any which need it
				final int len = _theLayers.size();
				for (int i = 0; i < len; i++)
				{
					final Layer thisLayer = _theLayers.elementAt(i);

					boolean isAlreadyPlotted = false;

					// just check if this layer is visible
					if (thisLayer.getVisible())
					{
						if (doubleBufferPlot())
						{
							// check we're plotting to a SwingCanvas, because we don't
							// double-buffer anything else
							if (dest instanceof SWTCanvas)
							{
								// does this layer want to be double-buffered?
								if (thisLayer instanceof BaseLayer)
								{
									// just check if there is a property which over-rides the
									// double-buffering
									final BaseLayer bl = (BaseLayer) thisLayer;
									if (bl.isBuffered())
									{
										isAlreadyPlotted = true;

										// do our double-buffering bit
										// do we have a layer for this object
										org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) _myLayers
												.get(thisLayer);
										if (image == null)
										{
											// ok - create our image
											if (template == null)
											{
												template = new Image(Display.getCurrent(), canvasWidth,
														canvasHeight);
											}
											image = createSWTImage(template);

											GC newGC = new GC(image);

											// wrap the GC into something we know how to plot to.
											SWTCanvasAdapter ca = new SWTCanvasAdapter(dest.getProjection());

											// and store the GC
											ca.startDraw(newGC);

											// ok, paint the layer into this canvas
											thisLayer.paint(ca);

											// done.
											ca.endDraw(null);

											// store this image in our list, indexed by the layer
											// object itself
											_myLayers.put(thisLayer, image);
										}

										// have we ended up with an image to paint?
										if (image != null)
										{
											// get the graphics to paint to
											SWTCanvas canv = (SWTCanvas) dest;

											// lastly add this image to our Graphics object
											canv.drawSWTImage(image, 0, 0, canvasWidth, canvasHeight);
										}

									}
								}
							} // whether we were plotting to a SwingCanvas (which may be
							// double-buffered
						} // whther we are happy to do double-buffering

						// did we manage to paint it
						if (!isAlreadyPlotted)
						{
							paintThisLayer(thisLayer, dest);

							isAlreadyPlotted = true;
						}
					}
				}
			}
		}

	}

	/**
	 * Convenience method added, to allow child classes to override how we plot
	 * non-background layers. This was originally inserted to let us support snail
	 * trails
	 * 
	 * @param thisLayer
	 * @param dest
	 */
	protected void paintThisLayer(final Layer thisLayer, CanvasType dest)
	{
		// draw into it
		thisLayer.paint(dest);
	}

	/**
	 * create the transparent image we need to for collating multiple layers into
	 * an image
	 * 
	 * @param canvasHeight
	 * @param canvasWidth
	 * @return
	 */
	private org.eclipse.swt.graphics.Image createSWTImage(Image template)
	{
		ImageData id = template.getImageData();
		id.transparentPixel = id.palette.getPixel(new RGB(255, 255, 255));
		org.eclipse.swt.graphics.Image image = new org.eclipse.swt.graphics.Image(Display
				.getCurrent(), id);
		return image;
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
		// dest.setColor(java.awt.Color.black);
		// dest.setBackgroundColor(Color.black);
		dest.fillRect(0, 0, sz.width, sz.height);

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
		super.mouseMoved(new java.awt.Point(e.x, e.y));

		if (_startPoint == null)
			return;

		// was this the right-hand button
		if (e.button != 3)
		{
			_draggedPoint = new Point(e.x, e.y);
			
			// ok - pass the drag to our drag control
			if(_myDragMode != null)
				_myDragMode.doMouseMove(_draggedPoint, JITTER, super.getLayers());
		}
	}

	protected void doMouseUp(MouseEvent e)
	{
		// was this the right-hand button
		if (e.button != 3)
		{
			// ok. did we move at all?
			if(_draggedPoint != null)
			{
				// yes, process the drag
				if(_myDragMode != null)
					_myDragMode.doMouseUp(new Point(e.x, e.y), e.stateMask);
			}
			else
			{
				// nope
				
				// hey, it was just a click - process it
				if (_theLeftClickListener != null)
				{
					// get the world location
					java.awt.Point jPoint = new java.awt.Point(e.x, e.y);
					WorldLocation loc = getCanvas().getProjection().toWorld(jPoint);
					_theLeftClickListener.CursorClicked(jPoint, loc, getCanvas(), _theLayers);
				}
			}
		}

		_startPoint = null;
	}

	protected void doMouseDown(MouseEvent e)
	{
		// was this the right-hand button?
		if (e.button != 3)
		{
			_startPoint = new Point(e.x, e.y);
			_draggedPoint = null;

			if(_myDragMode != null)				
				_myDragMode.mouseDown(_startPoint, _theCanvas, this);
			
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
	
	final public void setDragMode(final PlotMouseDragger newMode)
	{
		_myDragMode = newMode;
		
		// and reset the start point so we know where we are.
		_startPoint = null;
	}
	
	final public PlotMouseDragger getDragMode()
	{
		return _myDragMode;
	}

	/** embedded interface for classes that are able to handle drag events
	 * 
	 * @author ian.mayo
	 *
	 */
	abstract public static class PlotMouseDragger
	{

		/** handle the mouse being dragged
		 * 
		 * @param pt the new cursor location
		 */
		abstract public void doMouseMove(final Point pt, final int JITTER, final Layers theLayers);

		/** handle the mouse drag finishing
		 * @param keyState TODO
		 * @param pt the final cursor location
		 */
		abstract public void doMouseUp(Point point, int keyState);

		/** handle the mouse drag starting
		 * @param canvas the control it's dragging over
		 * @param theChart TODO
		 * @param pt the first cursor location
		 */
		abstract public void mouseDown(Point point, SWTCanvas canvas, PlainChart theChart);
		
	}	
}
