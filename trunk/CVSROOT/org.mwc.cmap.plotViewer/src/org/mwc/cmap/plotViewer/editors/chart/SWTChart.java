
// Copyright MWC 1999, Debrief 3 Project
// $RCSfile$
// @author $Author$
// @version $Revision$
// $Log$
// Revision 1.1  2005-05-20 13:45:04  Ian.Mayo
// Start doing chart
//
//


package org.mwc.cmap.plotViewer.editors.chart;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.HashMap;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.PlainChart;
import MWC.GenericData.WorldArea;



/**
 * The Chart is a canvas placed in a panel.
 * the majority of functionality is contained
 * in the PlainChart parent class, only the
 * raw comms is in this class.
 * This is configured by setting the listeners to the
 * chart/panel to be the listener functions defined in
 * the parent.
 */
public class SWTChart extends PlainChart implements Serializable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

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
   * the data area we last plotted (so that we know when a full layered repaint is needed).
   */
  private WorldArea _lastDataArea = null;

  /**
   * the image we paint into the corner of the canvas.
   */
//  private Image _ourImage;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * constructor, providing us with the set of layers to plot.
   *
   * @param theLayers the data to plot
   */
  public SWTChart(final Layers theLayers, Composite parent, int style)
  {
    super(theLayers);
    _theCanvas = createCanvas(parent, style);
    _theCanvas.setProjection(new MWC.Algorithms.Projections.FlatProjection());

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
    _theCanvas.getProjection().setScreenArea(dim);

    _theCanvas.addMouseMoveListener(new MouseMoveListener(){

			public void mouseMove(MouseEvent e)
			{
				doMouseMove(e);
			}});
    _theCanvas.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent e)
			{
				doMouseDoubleClick(e);
			}

			public void mouseDown(MouseEvent e)
			{			}

			public void mouseUp(MouseEvent e)
			{			}});
    

    // store the rubber band
    //   setRubberBand(new MWC.GUI.RubberBanding.RubberbandRectangle());

    // create the tooltip handler
    _theCanvas.setTooltipHandler(new MWC.GUI.Canvas.BasicTooltipHandler(theLayers));

  }

  protected void doMouseDoubleClick(MouseEvent e)
	{
		// TODO Auto-generated method stub
		System.err.println("send double-click event!!");
	}

	/**
   * constructor, providing us with a set of layers to plot, together with a background
   * image.
   *
   * @param theLayers the data to plot
   * @param imageName the image to show as a water-mark
   */
//  public SWTChart(final Layers theLayers, final String imageName)
//  {
//    this(theLayers);
//
//    // try to open the image
//    final URL imageURL = getClass().getClassLoader().getResource(imageName);
//    if (imageURL != null)
//    {
//      final ImageIcon io = new ImageIcon(imageURL);
//      _ourImage = io.getImage();
//    }
//  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void canvasResized()
  {
    // and clear out our buffered layers (they all need to be repainted anyway)
    _myLayers.clear();

    // now we've cleared the layers, call the parent resize method (which causes a repaint
    //  of the layers)
    super.canvasResized();
  }


  /**
   * over-rideable member function which allows us to over-ride the
   * canvas which gets used.
   *
   * @return the Canvas to use
   */
  public SWTCanvas createCanvas(Composite parent, int style)
  {
    return new SWTCanvas(parent, style);
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
//    return _theCanvas;
  }

  public final void update()
  {
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
   * over-ride the parent's version of paint, so that we can try to do it by layers.
   */
  public final void dont_paintMe(final CanvasType dest)
  {
//    // check that we have a valid canvas (that the sizes are set)
//    final java.awt.Dimension sArea = dest.getProjection().getScreenArea();
//    if (sArea != null)
//    {
//      if (sArea.width > 0)
//      {
//
//        // hey, we've plotted at least once, has the data area changed?
//        if (_lastDataArea != _theCanvas.getProjection().getDataArea())
//        {
//          // remember the data area for next time
//          _lastDataArea = _theCanvas.getProjection().getDataArea();
//
//          // clear out all of the layers we are using
//          _myLayers.clear();
//        }
//
//        // draw in the solid background
//        paintBackground(dest);
//
//        int canvasHeight = _theCanvas.getSize().y;
//        int canvasWidth = _theCanvas.getSize().x;
//        
//        // ok, pass through the layers, repainting any which need it
//        final int len = _theLayers.size();
//        for (int i = 0; i < len; i++)
//        {
//          final Layer thisLayer = _theLayers.elementAt(i);
//
//          boolean isAlreadyPlotted = false;
//
//          // just check if this layer is visible
//          if (thisLayer.getVisible())
//          {
//
//            if (doubleBufferPlot())
//            {
//
//              // check we're plotting to a SwingCanvas, because we don't double-buffer anything else
//              if (dest instanceof MWC.GUI.Canvas.Swing.SwingCanvas)
//              {
//
//                // does this layer want to be double-buffered?
//                if (thisLayer instanceof BaseLayer)
//                {
//
//                  // just check if there is a property which over-rides the double-buffering
//                  final BaseLayer bl = (BaseLayer) thisLayer;
//                  if (bl.isBuffered())
//                  {
//                    isAlreadyPlotted = true;
//
//                    // do our double-buffering bit
//                    // do we have a layer for this object
//                    org.eclipse.swt.graphics.Image image = (org.eclipse.swt.graphics.Image) _myLayers.get(thisLayer);
//                    if (image == null)
//                    {
//                      // sure it is, create an image to paint into (the TYPE_INT_ARGB ensures it has a transparent background)
//                    	image = new org.eclipse.swt.graphics.Image(Display.getCurrent(), canvasWidth, canvasHeight);
////                      image = new BufferedImage(_theCanvas.getWidth(), _theCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
//
//                      final Graphics g2 = image.getGraphics();
//
//                      // wrap the Graphics to make it look like a CanvasType
//                      final CanvasAdaptor ca = new CanvasAdaptor(_theCanvas.getProjection(), g2);
//
//                      // draw into it
//                      thisLayer.paint(ca);
//
//                      // ditch the graphics
//                      g2.dispose();
//
//                      // store this image in our list, indexed by the layer object itself
//                      _myLayers.put(thisLayer, image);
//                    }
//
//                    // have we ended up with an image to paint?
//                    if (image != null)
//                    {
//                      // get the graphics to paint to
//                      final Graphics gr = dest.getGraphicsTemp();
//
//                      if (gr != null)
//                      {
//                        // lastly add this image to our Graphics object
//                        gr.drawImage(image, 0, 0, _theCanvas);
//
//                        // and ditch it
//                        gr.dispose();
//
//                      }
//                      else
//                        MWC.Utilities.Errors.Trace.trace("SwingChart.PaintMe() :FAILED TO GET GRAPHICS TEMP");
//                    }
//
//                  }
//                }
//              }  // whether we were plotting to a SwingCanvas (which may be double-buffered
//            } // whther we are happy to do double-buffering
//
//            // did we manage to paint it
//            if (!isAlreadyPlotted)
//            {
//              thisLayer.paint(dest);
//
//              isAlreadyPlotted = true;
//            }
//          }
//        }
//      }
//    }

  }


  /**
   * property to indicate if we are happy to perform double-buffering.
   * - override it to change the response
   */
  protected boolean doubleBufferPlot()
  {
    return true;
  }

  /**
   * paint the solid background.
   *
   * @param dest where we're painting to
   */
  private void paintBackground(final CanvasType dest)
  {
//  	Point size = _theCanvas.getSize();
//  	
//    // fill the background, to start with
//    final Dimension sz = new Dimension(size.x, size.y);
//    dest.setColor(dest.getBackgroundColor());
//    dest.fillRect(0, 0, sz.width, sz.height);
//
//    // do we have an image?
//    if (_ourImage != null)
//    {
//      // find the coords
//      final int imgWidth = _ourImage.getWidth(getPanel());
//      final int imgHeight = _ourImage.getHeight(getPanel());
//
//      // find the point to paint at
//      final Point thePt = new Point((int) dest.getSize().getWidth() - imgWidth - 3,
//                                    (int) dest.getSize().getHeight() - imgHeight - 3);
//
//      // paint in our logo
//      dest.drawImage(_ourImage, thePt.x, thePt.y, imgWidth, imgHeight, getPanel());
//    }
  }


  //////////////////////////////////////////////////////////
  // methods for handling requests from our canvas
  //////////////////////////////////////////////////////////

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
//    _theCanvas.paintImmediately(rect);
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
  	System.err.println("PRODUCE NEW MOUSE EVENT TRANSLATOR!!!");
//  	super.mouseMoved()
  }

}












