// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SwingChart.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.4 $
// $Log: SwingChart.java,v $
// Revision 1.4  2005/05/24 13:15:44  Ian.Mayo
// Add serialisable version id
//
// Revision 1.3  2004/10/20 08:30:41  Ian.Mayo
// Slight change to paint order.  On resize, we clear all layers before repaint - so that all layers get repainted.  Previously we were only repainting changed layers.  Bugger.
//
// Revision 1.2  2004/05/25 14:47:04  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.8  2003-07-01 09:50:15+01  ian_mayo
// Implement inspector recommendations
//
// Revision 1.7  2003-06-09 09:20:36+01  ian_mayo
// improved working rubber band, before method refactoring (to remove band parameter)
//
// Revision 1.6  2003-03-14 14:05:37+00  ian_mayo
// get full plot to redraw after screen resize
//
// Revision 1.5  2003-03-10 10:22:44+00  ian_mayo
// Check whether we are allowed to double-buffer, and use over-rideable method to create canvas  - both in support of the overview chart
//
// Revision 1.4  2002-11-25 14:40:47+00  ian_mayo
// Allow developer to put image in BR corner of chart
//
// Revision 1.3  2002-10-28 09:24:00+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.2  2002-05-28 09:25:40+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:06+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:13+01  ian_mayo
// Initial revision
//
// Revision 1.3  2002-02-18 09:19:03+00  administrator
// Only double-buffer painting to SwingCanvas - prevent bug appearing when writing Metafile
//
// Revision 1.2  2002-01-29 07:56:06+00  administrator
// Use a layered canvas if the layer requests it
//
// Revision 1.1  2002-01-24 14:22:33+00  administrator
// Reflect fact that Layers events for reformat and modified take a Layer parameter (which is possibly null).  These changes are a step towards implementing per-layer graphics updates
//
// Revision 1.0  2001-07-17 08:46:28+01  administrator
// Initial revision
//
// Revision 1.1  2001-01-03 13:42:59+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:22  ianmayo
// initial version
//
// Revision 1.8  2000-07-05 16:37:28+01  ian_mayo
// white space only
//
// Revision 1.7  2000-04-19 11:39:03+01  ian_mayo
// implement Close method, clear local storage
//
// Revision 1.6  1999-12-13 11:27:48+00  ian_mayo
// added tooltip handlers
//
// Revision 1.5  1999-12-13 10:40:46+00  ian_mayo
// remove screen update following rescale operation
//
// Revision 1.4  1999-12-03 14:35:05+00  ian_mayo
// remove d-lines
//
// Revision 1.3  1999-11-25 16:54:04+00  ian_mayo
// tidied up locations
//
// Revision 1.2  1999-11-23 10:37:54+00  ian_mayo
// moved directory to more sensible location
//
// Revision 1.1  1999-11-23 09:15:13+00  ian_mayo
// Initial revision
//
// Revision 1.1  1999-11-18 11:13:24+00  ian_mayo
// new Swing versions
//


package MWC.GUI.Chart.Swing;

import MWC.GUI.*;
import MWC.GUI.Canvas.CanvasAdaptor;
import MWC.GUI.Canvas.Swing.SwingCanvas;
import MWC.GenericData.WorldArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;


/**
 * The Chart is a canvas placed in a panel.
 * the majority of functionality is contained
 * in the PlainChart parent class, only the
 * raw comms is in this class.
 * This is configured by setting the listeners to the
 * chart/panel to be the listener functions defined in
 * the parent.
 */
public class SwingChart extends PlainChart implements Serializable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SwingCanvas _theCanvas;

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
  private Image _ourImage;

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////

  /**
   * constructor, providing us with the set of layers to plot.
   *
   * @param theLayers the data to plot
   */
  public SwingChart(final Layers theLayers)
  {
    super(theLayers);
    _theCanvas = createCanvas();
    _theCanvas.setProjection(new MWC.Algorithms.Projections.FlatProjection());

    // add us as a painter to the canvas
    _theCanvas.addPainter(this);

    // catch any resize events
    _theCanvas.addComponentListener(new ComponentAdapter()
    {
      public void componentResized(final ComponentEvent e)
      {
        canvasResized();
      }
    });

    _theCanvas.getProjection().setScreenArea(_theCanvas.getSize());

    _theCanvas.addMouseMotionListener(this);
    _theCanvas.addMouseListener(this);

    // store the rubber band
    //   setRubberBand(new MWC.GUI.RubberBanding.RubberbandRectangle());

    // create the tooltip handler
    _theCanvas.setTooltipHandler(new MWC.GUI.Canvas.BasicTooltipHandler(theLayers));

  }

  /**
   * constructor, providing us with a set of layers to plot, together with a background
   * image.
   *
   * @param theLayers the data to plot
   * @param imageName the image to show as a water-mark
   */
  public SwingChart(final Layers theLayers, final String imageName)
  {
    this(theLayers);

    // try to open the image
    final URL imageURL = getClass().getClassLoader().getResource(imageName);
    if (imageURL != null)
    {
      final ImageIcon io = new ImageIcon(imageURL);
      _ourImage = io.getImage();
    }
  }

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
  public SwingCanvas createCanvas()
  {
    return new SwingCanvas();
  }

  /**
   * get the size of the canvas.
   *
   * @return the dimensions of the canvas
   */
  public final java.awt.Dimension getScreenSize()
  {
    // get the current size of the canvas
    return _theCanvas.getSize();
  }

  public final Component getPanel()
  {
    return _theCanvas;
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
  public final void paintMe(final CanvasType dest)
  {
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

        // draw in the solid background
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

              // check we're plotting to a SwingCanvas, because we don't double-buffer anything else
              if (dest instanceof MWC.GUI.Canvas.Swing.SwingCanvas)
              {

                // does this layer want to be double-buffered?
                if (thisLayer instanceof BaseLayer)
                {

                  // just check if there is a property which over-rides the double-buffering
                  final BaseLayer bl = (BaseLayer) thisLayer;
                  if (bl.isBuffered())
                  {
                    isAlreadyPlotted = true;

                    // do our double-buffering bit
                    // do we have a layer for this object
                    Image image = (Image) _myLayers.get(thisLayer);
                    if (image == null)
                    {

                      // sure it is, create an image to paint into (the TYPE_INT_ARGB ensures it has a transparent background)
                      image = new BufferedImage(_theCanvas.getWidth(), _theCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);

                      final Graphics g2 = image.getGraphics();

                      // wrap the Graphics to make it look like a CanvasType
                      final CanvasAdaptor ca = new CanvasAdaptor(_theCanvas.getProjection(), g2);

                      // draw into it
                      thisLayer.paint(ca);

                      // ditch the graphics
                      g2.dispose();

                      // store this image in our list, indexed by the layer object itself
                      _myLayers.put(thisLayer, image);
                    }

                    // have we ended up with an image to paint?
                    if (image != null)
                    {
                      // get the graphics to paint to
                      final Graphics gr = dest.getGraphicsTemp();

                      if (gr != null)
                      {
                        // lastly add this image to our Graphics object
                        gr.drawImage(image, 0, 0, _theCanvas);

                        // and ditch it
                        gr.dispose();

                      }
                      else
                        MWC.Utilities.Errors.Trace.trace("SwingChart.PaintMe() :FAILED TO GET GRAPHICS TEMP");
                    }

                  }
                }
              }  // whether we were plotting to a SwingCanvas (which may be double-buffered
            } // whther we are happy to do double-buffering

            // did we manage to paint it
            if (!isAlreadyPlotted)
            {
              thisLayer.paint(dest);

              isAlreadyPlotted = true;
            }
          }
        }
      }
    }

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
    // fill the background, to start with
    final Dimension sz = new Dimension(_theCanvas.getWidth(), _theCanvas.getHeight());
    dest.setColor(dest.getBackgroundColor());
    dest.fillRect(0, 0, sz.width, sz.height);

    // do we have an image?
    if (_ourImage != null)
    {
      // find the coords
      final int imgWidth = _ourImage.getWidth(getPanel());
      final int imgHeight = _ourImage.getHeight(getPanel());

      // find the point to paint at
      final Point thePt = new Point((int) dest.getSize().getWidth() - imgWidth - 3,
                                    (int) dest.getSize().getHeight() - imgHeight - 3);

      // paint in our logo
      dest.drawImage(_ourImage, thePt.x, thePt.y, imgWidth, imgHeight, getPanel());
    }
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
    _theCanvas.repaint();
  }

  public final void repaintNow(final java.awt.Rectangle rect)
  {
    _theCanvas.paintImmediately(rect);
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


}












