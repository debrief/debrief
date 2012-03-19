/*
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 11-Jun-02
 * Time: 14:08:55
 * To change template for new interface use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.Chart.Painters;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.image.MemoryImageSource;
import java.beans.PropertyEditorSupport;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.JButton;

import MWC.GUI.BaseLayer;
import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.ETOPO.BathyProvider;
import MWC.GUI.ETOPO.Conrec;
import MWC.GUI.Properties.BoundedInteger;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

/**
 * interface which indicates that this class is capable of providing an integer value
 * at the indicated grid location .
 */
abstract public class SpatialRasterPainter extends BaseLayer implements Layer.BackgroundLayer, BathyProvider
{

  /****************************************************
   * member variables
   ***************************************************/
	
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
  /**
   * the colour to decorate the key
   */
  Color _myColor = Color.white;

  /**
   * where to plot the key
   */
  Integer _keyLocation = new Integer(KeyLocationPropertyEditor.LEFT);


  /**
   * the bit which does the painting
   */
  private static PainterComponent _painter = null;

  /**
   * the resolution of data plotted onto the screen
   */
  int _gridRes = 2;

  /**
   * do we plot the contours
   */
  protected double[] _contourDepths;

  /**
   * the default contours
   */
  protected final double[] DEFAULT_CONTOUR_DEPTHS = {-1000, -500, -200, -100};

  /**
   * whether to show the contours
   */
  protected boolean _showContours = true;
  /**
   * the grid interval to use to calculate the contours
   */
  protected int _gridInterval = 1;
  /**
   * whether to show the gridded bathy
   */
  boolean _showBathy = true;
  /**
   * whether the user wants to optimise the grid interval
   */
  private boolean _optimiseGridInterval = true;

  /**
   * *************************************************
   * constructor
   * *************************************************
   */
  public SpatialRasterPainter(final String layerName)
  {
    super.setName(layerName);
    super.setBuffered(true);
    super.setVisible(true);
  }


  
  /** allow a class to over-ride our spatial plotter
   * 
   */
  public static void overridePainter(PainterComponent newPainter)
  {
  	_painter = newPainter;
  }
  
  /** override layer-adding - it doesn't make sense for spatial layers
   * 
   * @param other
   */
  public final void append(final Layer other)
  {
    if (other instanceof SpatialRasterPainter)
      MWC.GUI.Dialogs.DialogFactory.showMessage("Gridded dataset", "Gridded dataset already loaded");
    else
      MWC.Utilities.Errors.Trace.trace("Sorry, cannot add a layer (" + other + ") to a painter layer");
  }

  /**
   * *************************************************
   * member methods
   * *************************************************
   */

  public void paint(final CanvasType dest)
  {
  	if(_painter == null)
  		 _painter = new SwingPainterComponent();
  	
    // get the painter to do its thing
    _painter.paint(dest, this);
  }

  protected final void prepareAndPlotContours(final CanvasType dest)
  {
    if (_showContours)
      if (paintContours())
      {
        // just see if we should be optimising the grid resolution
        if (_optimiseGridInterval)
        {
          // sort out the proportions
          final double wid = dest.getProjection().getVisibleDataArea().getWidth();
          final double dataPoints = wid / this.getGridDelta();
          double gridCalcs = Math.pow(dataPoints / _gridInterval, 2);

          while (gridCalcs < 2000)
          {
            _gridInterval--;
            gridCalcs = Math.pow(dataPoints / _gridInterval, 2);
          }

          while (gridCalcs > 10000)
          {
            _gridInterval++;
            gridCalcs = Math.pow(dataPoints / _gridInterval, 2);
          }
          // inform the user what we're doing
          if (_myEditor != null)
            _myEditor.fireChanged(this, "GridInterval", null, new Integer(_gridInterval));
        }

        // ok, do our extra paint now
        plotContours(dest);
      }

  }

  public WorldArea getBounds()
  {
    final WorldArea res = null;
    return res;
  }

  public final Color getColor()
  {
    return _myColor;
  }

  public final void setColor(final Color color)
  {
    this._myColor = color;
  }

  /**
   * get the screen resolution of this grid.
   * (i.e. how many screen pixels do we "blob-up" into one data value)
   */
  public final BoundedInteger getBathyRes()
  {
    return new BoundedInteger(_gridRes, 1, 50);
  }

  /**
   * set the resolution of this grid
   */
  public final void setBathyRes(final BoundedInteger gridRes)
  {
    this._gridRes = gridRes.getCurrent();
  }

  /**
   * location of the key
   *
   * @return the location
   */
  public final Integer getKeyLocation()
  {
    return _keyLocation;
  }

  /**
   * location of the key
   *
   * @param keyLocation the lcaotion
   */
  public final void setKeyLocation(final Integer keyLocation)
  {
    this._keyLocation = keyLocation;
  }

  /****************************************************
   * accessors
   ***************************************************/



  /**
   * *************************************************
   * spatial support
   * *************************************************
   */

  /* returns a color based on slope and elevation */
  public int getColor(final int elevation, 
  		final double lowerLimit, 
  		final double upperLimit,
  		ColorConverter converter)
  {
    int res;

    // switch to positive
    double val = elevation;
    if (val > upperLimit)
      val = upperLimit;

    final double value_range = upperLimit - lowerLimit;

    final double thisVal = val - lowerLimit;

    final double proportion = thisVal / value_range;

    final double color_val = proportion * 160;

    // limit the colour val to the minimum value
    int green_tone = 255 - (int) color_val;

    // just check we've got a valid colour
    green_tone = Math.min(250, green_tone);
    green_tone = Math.max(0, green_tone);

    res = converter.convertColor(green_tone, green_tone, 99);

    return res;
  }


  /**
   * create the RGB color code for the supplied colour components
   *
   * @param red
   * @return
   */
  public static int getRGB(int red, int green, int blue)
  {
    int res = ((255 & 0xFF) << 24) |
      ((red & 0xFF) << 16) |
      ((green & 0xFF) << 8) |
      ((blue & 0xFF) << 0);
    return res;
  }

  abstract public int getValueAt(WorldLocation location);

  /**
   * over-rideable method to constrain max value to zero (such as when not plotting land)
   *
   * @return yes/no
   */
  protected boolean zeroIsMax()
  {
    return false;
  }

  /**********************************************************************
   * bathy provider support
   *********************************************************************/

  /**
   * provide the depth in metres at the indicated location
   */
  public double getDepthAt(WorldLocation loc)
  {
    return getValueAt(loc);
  }

  /**
   * provide the delta for the data  (in degrees)
   */
  abstract public double getGridDelta();

  /**
   * whether the data has been loaded yet
   */
  abstract public boolean isDataLoaded();

  
  /** swing-specific painter component
   * 
   * @author ian.mayo
   *
   */
  public static class SwingPainterComponent extends PainterComponent
  {
    /**
     * the image we plot
     */
    private int[] _myImageBuffer;
    
		/** ok, do the actual (system-specific) paint operation
		 * @param dest
		 * @param width
		 * @param height
		 */
		protected void paintTheImage(final CanvasType dest, final int width, final int height)
		{
			// create the raster
			final MemoryImageSource mis = new MemoryImageSource(width, height, _myImageBuffer, 0, width);
			final Image im = Toolkit.getDefaultToolkit().createImage(mis);

			// ok, actually draw the image
			dest.drawImage(im, 0, 0, width, height, new JButton());
		}

		/** check if we need to create or update our image
		 * @param width
		 * @param height
		 */
		protected void checkImageValid(final int width, final int height)
		{
			if ((_myImageBuffer == null) || (_myImageBuffer.length != width * height))
      {
        _myImageBuffer = new int[width * height];
      }
		}
		
		/** set this pixel to the correct color
		 * @param width
		 * @param thisValue
		 * @param x_coord
		 * @param y_coord
		 */
		protected void assignPixel(final int width, final int thisValue, final int x_coord, final int y_coord)
		{
			final int idx = (y_coord) * width + (x_coord);

			// put this elevation into our array
			_myImageBuffer[idx] = thisValue; //thisCol.getRGB();
		}
		
		/** pass through the array - switching the depth value to it's colour-coded equivalent
		 * @param parent
		 * @param width
		 * @param height
		 * @param min_height
		 * @param max_height
		 * @param dest where we're painting to
		 */
		protected  void updatePixelColors(SpatialRasterPainter parent, final int width,
				final int height, int min_height, int max_height, CanvasType dest)
		{
			// do a second pass to set the actual colours
      for (int i = 0; i < width * height; i++)
      {
      	final int thisHeight = _myImageBuffer[i];
        final int thisCol = parent.getColor(thisHeight,
                                     min_height,
                                     max_height,
                                     this);
        _myImageBuffer[i] = thisCol;
      }
		}

		public int convertColor(int red, int green, int blue)
		{
			// TODO Auto-generated method stub
			return getRGB(red, green, blue);
		}
		
  	
  }

	/** embedded interface for classes capable of createing a device-specific
	 * colour (int) from the three pigments
	 * 
	 */
	public static interface ColorConverter
	{
		public int convertColor(int red, int green, int blue);

		/** color converter which always produces a swing color.  We need to call this one whether we have SWT
		 * or Swing colors - since in SWT plotting we need to produce an SWT color for drawing the image by hand,
		 * whereas we need to produce a Swing color when we're drawing the key (using Debrief canvas type)
		 * @author ian.mayo
		 *
		 */
		public static class SwingColorConverter implements ColorConverter
		{
			public int convertColor(int red, int green, int blue)
			{
				return getRGB(red, green, blue);
			}			
		}
		
	}
	
  /**
   * *************************************************
   * embedded class to produce the image
   * *************************************************
   */

  abstract public static class PainterComponent implements ColorConverter
  {


    /**
     * **************************************************************
     * WORKING VARIABLES
     * **************************************************************
     */
    private final Point _workingPoint = new Point(0, 0);

    private WorldLocation _workingLocation = new WorldLocation(0, 0, 0);

    final Font _myFont = new Font("Arial", Font.PLAIN, 10);

    protected double min_depth = 0;
    protected double max_depth = 0;

    /** always draw the key using AWT/SWING colors - since it plots rectangles via our 
     * CanvasType plotting routines
     */
		public SwingColorConverter SWING_COLOR_CONVERTER = new SwingColorConverter();
		
    

    /**
     * repaint the noise levels
     */
    public final void paint(final CanvasType dest, SpatialRasterPainter parent)
    {

      /* todo - create a series of painted rectangles, not an image buffer -
        this will probably draw to screen more quickly, but will definitely write
        to metafile more quickly.  The rectangles should be either sized to suit the
        resolution of the data, or the resolution requested by the user */


      // reset the min and max depths
      min_depth = max_depth = 0;

      // create the raster image
      if (parent.isBathyVisible())
      {
        final double[] min_max = createRasterImage(dest, parent);
        min_depth = min_max[0];
        max_depth = min_max[1];
      }

      // see if there's anything else to plot before we do the key
      if (parent.isContoursVisible())
      {
        // extend the depths, if we need to
        if (min_depth == 0)
          min_depth = Math.min(min_depth, parent._contourDepths[0]);

        // extend the depths, if we need to
        if (max_depth == 0)
          max_depth = Math.max(max_depth, parent._contourDepths[parent._contourDepths.length - 1]);

        parent.prepareAndPlotContours(dest);
      }

      // finally draw in the scale
      drawKey(dest, min_depth, max_depth, parent._keyLocation, parent);
    }

    /**
     * create a raster image using our data, and placing it into the canvas type
     *
     * @param dest where we're painting to
     * @return the min and max depths for this waterspace
     */
    private double[] createRasterImage(final CanvasType dest, SpatialRasterPainter parent)
    {

      final double[] res = {0d, 0d};

      // compute our deltas
      final int width = (int) dest.getProjection().getScreenArea().getWidth();
      final int height = (int) dest.getProjection().getScreenArea().getHeight();

  //    System.out.println("wid:" + width + " ht:" + height + " cells:" + (width * height));

      // create int array to hold colors
      checkImageValid(width, height);

      // keep track of the maximum depth
      boolean limits_set = false;
      int min_height = 0;
      int max_height = 0;

      // build array
      for (int y = parent._gridRes / 2; y < height; y += parent._gridRes)     // work our way up the screen
      {
        // process each column
        for (int x = parent._gridRes / 2; x < width; x += parent._gridRes)  // work our way across the screen
        {
          // inverse project x,y to lon,lat
          _workingPoint.setLocation(x, y);
          _workingLocation = dest.getProjection().toWorld(_workingPoint);

          if (_workingLocation.isValid())
          {

            // obtain the value at this location
            final int thisValue = parent.getValueAt(_workingLocation);

            // keep track of the maximum depth
            if (!limits_set)
            {
              // first pass, store as initial values
              min_height = thisValue;
              max_height = thisValue;
              limits_set = true;
            }
            else
            {
              // find the max/min values
              min_height = Math.min(thisValue, min_height);
              max_height = Math.max(thisValue, max_height);
            }

            // loop through the array positions for this grid resolution  -
            //   note that we put in a -gridRes/2 offset so that our participants
            // appear in the centre of the data rectangle, not at the TL corner.
            for (int y_a = -parent._gridRes / 2; (y_a < parent._gridRes) && (y + y_a < height); y_a++)
            {
              for (int x_a = -parent._gridRes / 2; (x_a < parent._gridRes) && (x + x_a < width); x_a++)
              {
                // work out the new index
              	final int x_coord = x + x_a;
              	final int y_coord = y + y_a;
              	
                assignPixel(width, thisValue, x_coord, y_coord);
              } // loop through x
            } // loop through y
          } // check this point is valid
        }
      }

      // now do a pass through to switch from actual height to our
      // color-coded value
      updatePixelColors(parent, width, height, min_height, max_height, dest);

      if (parent._showBathy)
      {
        paintTheImage(dest, width, height);
      }

      res[0] = min_height;
      res[1] = max_height;

      return res;
    }

		/** pass through the array - switching the depth value to it's colour-coded equivalent
		 * @param parent
		 * @param width
		 * @param height
		 * @param min_height
		 * @param max_height
		 * @param dest where we're painting to
		 */
		abstract protected  void updatePixelColors(SpatialRasterPainter parent, final int width, final int height, int min_height, int max_height, CanvasType dest);

		/** set this pixel to the correct color
		 * @param width
		 * @param thisValue
		 * @param x_coord
		 * @param y_coord
		 */
		abstract protected void assignPixel(final int width, final int thisValue, final int x_coord, final int y_coord);

		/**
		 * @param width
		 * @param height
		 */
		abstract protected void checkImageValid(final int width, final int height);

		/** ok, do the actual (system-specific) paint operation
		 * @param dest
		 * @param width
		 * @param height
		 */
		abstract protected void paintTheImage(final CanvasType dest, final int width, final int height);

    /**
     * method to draw in the key
     */
    final void drawKey(final CanvasType dest,
                       final double min_height,
                       double max_height,
                       final Integer keyLocation,
                       SpatialRasterPainter parent)
    {

      // how big is the screen?
      final Dimension screen_size = dest.getProjection().getScreenArea();

      // are we showing land?
      if (parent.zeroIsMax())
      {
        // yes, make zero the highest value
        max_height = 0;
      }

      // define the approximate proportions of the key
      final float MIN_WID = 0.05f;
      final float MIN_LEN = 0.05f;
      final float LEN = 0.9f;

      // how high is a piece of text at this scale?
      final int txtHt = dest.getStringHeight(_myFont) + 5;
//      final int txtHt = 85;
      final int txtWid = dest.getStringWidth(_myFont, " " + (int) Math.max(min_height * -1, max_height));

      // sort out where the key is going to go
      // determine the start / end points according to the scale location
      // variable
      Point TL = null;
      Point BR = null;

      // find out how we distribute the rectangles which make up the key
      int num_labels = 0;
      double stepHeight = 0d;
      double stepWidth = 0d;
      int total_height = 0;
      Dimension2D rectSize = new Dimension();

      switch (keyLocation.intValue())
      {
        case (KeyLocationPropertyEditor.NOT_SHOWN):
          break;
        case (KeyLocationPropertyEditor.LEFT):
          TL = new Point((int) (screen_size.width * MIN_WID), (int) (screen_size.height * MIN_LEN));
          BR = new Point(TL.x + txtWid, (int) (screen_size.height * (MIN_LEN + LEN)));
          num_labels = (BR.y - TL.y) / txtHt;
          total_height = BR.y - TL.y;
          stepHeight = total_height / (double) num_labels;
          rectSize = new Dimension(BR.x - TL.x, (int) stepHeight + 1);
          break;
        case (KeyLocationPropertyEditor.RIGHT):
          TL = new Point((int) (screen_size.width - (screen_size.width * MIN_WID) - txtWid), (int) (screen_size.height * MIN_LEN));
          BR = new Point(TL.x + txtWid, (int) (screen_size.height * (MIN_LEN + LEN)));
          num_labels = (BR.y - TL.y) / txtHt;
          total_height = BR.y - TL.y;
          stepHeight = total_height / (double) num_labels;
          rectSize = new Dimension(BR.x - TL.x, (int) stepHeight + 1);
          break;
        case (KeyLocationPropertyEditor.TOP):
          TL = new Point((int) (screen_size.width * MIN_WID), (int) (screen_size.height * MIN_LEN));
          BR = new Point((int) (screen_size.width * (MIN_LEN + LEN)), (TL.y + txtHt));
          num_labels = (BR.x - TL.x) / txtWid;
          total_height = BR.x - TL.x;
          stepHeight = 0;
          stepWidth = (BR.x - TL.x) / (double) num_labels;
          rectSize = new Dimension((int) stepWidth + 1, BR.y - TL.y);
          break;
        default:
          TL = new Point((int) (screen_size.width * MIN_WID), (int) (screen_size.height - (screen_size.height * MIN_LEN) - txtHt));
          BR = new Point((int) (screen_size.width * (MIN_LEN + LEN)), (int) (screen_size.height - (screen_size.height * MIN_LEN)));
          num_labels = (BR.x - TL.x) / txtWid;
          total_height = BR.x - TL.x;
          stepHeight = 0;
          stepWidth = (BR.x - TL.x) / (double) num_labels;
          rectSize = new Dimension((int) stepWidth + 1, BR.y - TL.y);
          break;
      }


      // check its worth bothering with
      if ((TL == null) ||(BR == null))
        return;

      // sort out how many shade steps we are going to produce (it depends on how big the text string is)
      final int depth_step = (int) ((max_height - min_height) / num_labels);

      // get ready to step through the colours
      Point thisPoint = null;

      // first pass, to plot the background shading
      if (parent.isBathyVisible())
      {
        thisPoint = new Point(TL);

        for (int i = 0; i < num_labels; i++)
        {
          final short thisDepth = (short) (max_height - (i * depth_step));

          // move forward
          thisPoint.move(TL.x + (int) (i * stepWidth), TL.y + (int) (i * stepHeight));

          // produce this new colour
          int thisCol = parent.getColor(thisDepth, min_height, max_height, SWING_COLOR_CONVERTER);
          
          Color thisColor = new Color(thisCol);
            
          // draw this rectangle
          dest.setColor(thisColor);

          dest.fillRect(thisPoint.x, thisPoint.y,
                        (int) (rectSize.getWidth()), (int) (rectSize.getHeight()));
        }
      }
      
      // second pass, insert the contour markers
      // plot the contours into the key
      if (parent._showContours && (parent._contourDepths.length > 0))
      {
        // calculate the size of the box
        final Dimension wholeSize = new Dimension((int) (stepWidth * num_labels),
                                                  (int) (stepHeight * num_labels));

        // loop through the contours
        for (int i = 0; i < parent._contourDepths.length; i++)
        {
          final double depth = parent._contourDepths[i];
          if ((depth > min_height) && (depth < max_height))
          {
            // calculate where this line would fall

            // how far through the range is it?
            final double offset = 1 - ((depth - min_height) / (max_height - min_height));

            // what does this translate to?
            final Point thisP = new Point((int) (wholeSize.getWidth() * offset),
                                          (int) (wholeSize.getHeight() * offset));

            final Point translatedPoint = new Point(thisP);
            translatedPoint.translate(TL.x, TL.y);

            // add in a single data width, to line them up
            translatedPoint.translate((int) rectSize.getWidth(), (int) rectSize.getHeight());

            final Point otherEnd = new Point(translatedPoint);

            if (stepWidth == 0)
            {
              // so, we're going down the side, give it some width
              otherEnd.translate(-(BR.x - TL.x), 0);

              // move the start up a bit
              translatedPoint.translate(0, -1);

              // move the end down a bit
              otherEnd.translate(0, 1);

              // swap around the x values
              final int pX = translatedPoint.x;
              translatedPoint.x = otherEnd.x;
              otherEnd.x = pX;

            }
            else
            {
              // we're going across the top, give it some height
              otherEnd.translate(0, -(BR.y - TL.y));

              // move the start left a bit
              translatedPoint.translate(-1, 0);

              // move the end right a bit
              otherEnd.translate(1, 0);


              // swap around the y values
              final int pY = translatedPoint.y;
              translatedPoint.y = otherEnd.y;
              otherEnd.y = pY;

            }

            // get the colour for the contour at this index
            final Color thisContourColor = parent.getContourColourFor(i);

            dest.setColor(thisContourColor);

            dest.fillRect(translatedPoint.x, translatedPoint.y,
                          otherEnd.x - translatedPoint.x,
                          otherEnd.y - translatedPoint.y);
          }
        }
      }

      // third pass, insert the depth labels
      thisPoint = new Point(TL);
      
      // cool, work through the key labels
      for (int i = 0; i < num_labels; i++)
      {

        final short thisDepth = (short) (max_height - (i * depth_step));

        // move forward
        thisPoint.move(TL.x + (int) (i * stepWidth), TL.y + (int) (i * stepHeight));

        // insert the depth value
        dest.setColor(parent._myColor);

        final String thisLabel = "" + Math.abs(thisDepth);

        final int thisTxtWid = dest.getStringWidth(_myFont, thisLabel);

        dest.drawText(_myFont, thisLabel, (int) (thisPoint.x + rectSize.getWidth() - thisTxtWid - 4), thisPoint.y + txtHt - 2);
      }



      // draw the bounding box
      dest.setColor(parent._myColor);
      dest.drawRect(TL.x - 1, TL.y - 1, (BR.x - TL.x) + 2, (BR.y - TL.y) + 2);
      dest.drawRect(TL.x, TL.y, BR.x - TL.x, BR.y - TL.y);
    }


  }


  //////////////////////////////////////////////////
  // contour related
  //////////////////////////////////////////////////

  /**
   * @param dest
   * @param startX
   * @param startY
   * @param endX
   * @param endY
   * @param contourIndex
   */
  public final void drawThisContour(final CanvasType dest,
                                    final double startX, final double startY,
                                    final double endX, final double endY,
                                    final int contourIndex)
  {
    final WorldLocation wa = new WorldLocation(startY, startX, 0);
    final WorldLocation wb = new WorldLocation(endY, endX, 0);
    final Point pa = new Point(dest.toScreen(wa));
    final Point pb = new Point(dest.toScreen(wb));
    dest.setColor(getContourColourFor(contourIndex));
    dest.drawLine(pa.x, pa.y, pb.x, pb.y);
  }

  /**
   * get the colour for this contour
   *
   * @param contourIndex
   * @return
   */
  Color getContourColourFor(final int contourIndex)
  {
    Color res;

    // are we showing a shaded bathy
    if (this.isBathyVisible())
    {
      final int col = contourIndex * 255 / _contourDepths.length;

      // yeah, use a range of gray shades
      res = new Color(col, col, col);
    }
    else
    {
      final int blueOffset = 170;
      // make it all a bit lighter than with bathy is shown
      final int blueCol = blueOffset + contourIndex * (255 - blueOffset) / _contourDepths.length;

      final int otherOffset = 20;
      // make it all a bit lighter than with bathy is shown
      final int otherCol = otherOffset + contourIndex * (255 - otherOffset) / _contourDepths.length;


      // yeah, use a range of blue-gray shades
      res = new Color(otherCol, otherCol, blueCol);

    }


    return res;
  }


  /**
   * whether we want to plot contours or not
   *
   * @return yes/no
   */
  public final boolean paintContours()
  {
    return (_contourDepths != null);
  }


  /**
   * function to retrieve a data value at the indicated index
   */
  protected double contour_valAt(final int i, final int j)
  {
    return 0;
  }

  /**
   * function to retrieve the x-location for a specific array index
   */
  protected double contour_xValAt(final int i)
  {
    return 0;
  }

  /**
   * function to retrieve the x-location for a specific array index
   */
  protected double contour_yValAt(final int i)
  {
    return 0;
  }

  protected int getLatIndex(final WorldLocation val)
  {
    return 0;
  }

  protected int getLongIndex(final WorldLocation val)
  {
    return 0;
  }

  /**
   * custom editor providing the selection of locations for the depth scale key
   */
  public static final class KeyLocationPropertyEditor extends PropertyEditorSupport
  {

    final static public int NOT_SHOWN = 0;
    final static public int LEFT = 1;
    final static public int RIGHT = 2;
    final static public int TOP = 3;
    final static public int BOTTOM = 4;

    /**
     * the list of tags we display
     */
    String[] _myTags;

    Integer _myLineLocation;

    public final String[] getTags()
    {
      if (_myTags == null)
      {
        _myTags = new String[]{"Not Shown",
                               "Left",
                               "Right",
                               "Top",
                               "Bottom"};
      }
      return _myTags;
    }

    public final Object getValue()
    {
      return _myLineLocation;
    }

    public final void setValue(final Object p1)
    {
      if (p1 instanceof Integer)
      {
        _myLineLocation = (Integer) p1;
      }
      if (p1 instanceof String)
      {
        final String val = (String) p1;
        setAsText(val);
      }
    }

    public final void setAsText(final String val)
    {
      for (int i = 0; i < getTags().length; i++)
      {
        final String thisStr = getTags()[i];
        if (thisStr.equals(val))
          _myLineLocation = new Integer(i);
      }
    }

    public final String getAsText()
    {
      String res;
      final int index = _myLineLocation.intValue();
      res = getTags()[index];
      return res;
    }
  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class RasterPainterTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public RasterPainterTest(final String val)
    {
      super(val);
    }

    public void testDepthAllocation()
    {
      final SpatialRasterPainter srp = new SpatialRasterPainter(null)
      {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public int getValueAt(final WorldLocation location)
        {
          return 0;
        }

        /**
         * provide the delta for the data
         */
        public double getGridDelta()
        {
          return 1;
        }

        /**
         * whether the data has been loaded yet
         */
        public boolean isDataLoaded()
        {
          return true;
        }
      };

      String listOfDepths = "-100,-20,-10,0,5";
      srp.setContourDepths(listOfDepths);
      assertEquals("min depth set", srp._contourDepths[0], -100, 1);
      assertEquals("max depth set", srp._contourDepths[4], 5, 1);
      String outputDepths = srp.getContourDepths();
      assertEquals("depths output correctly", outputDepths, "-100, -20, -10, 0, 5");

      // check we can depths out of sequence
      listOfDepths = "-60, -100,-10,0,5";
      srp.setContourDepths(listOfDepths);
      assertEquals("min depth set", srp._contourDepths[0], -100, 1);
      assertEquals("max depth set", srp._contourDepths[4], 5, 1);
      outputDepths = srp.getContourDepths();
      assertEquals("depths output correctly", outputDepths, "-100, -60, -10, 0, 5");


      // check we can handle mangled depths
      listOfDepths = "-60, -100,-10,0-5";
      srp.setContourDepths(listOfDepths);
      assertEquals("min depth set", srp._contourDepths[0], -100, 1);
      assertEquals("max depth set", srp._contourDepths[2], -10, 1);
      outputDepths = srp.getContourDepths();
      assertEquals("depths output correctly", outputDepths, "-100, -60, -10");


    }

    public void testRasterDrawing()
    {
      // what are we trying to do?
      /** represent a raster as a series of painted rectangles.
       * Using the memory image (as we originally did, the time to plot the image
       * is directly proportional to the resolution (num pixels) of the image.
       * Using the series of rectangles, however, will be related to the resolution of the
       * data to be plotted into the image - and is therefore independent of  the
       * screen resolution
       */

      // find how many pixels wide the screen is

      // find the data units wide the screen is

      // are the data units more than 2 pixels wide?

      // yes, draw rectangles

      // else

      // no, draw the image buffer thingy

    }
  }


  /**
   * the contouring algorithm
   *
   * @param dest where we're plotting to
   */
  protected final void plotContours(final CanvasType dest)
  {
    // get the corners
    final WorldArea wa = dest.getProjection().getVisibleDataArea();
    final WorldLocation tl = wa.getTopLeft();
    final WorldLocation br = wa.getBottomRight();

    // so, we've got our four corners
    final int ilb = getLongIndex(tl) - 1;
    final int iub = getLongIndex(br) + 1;
    final int jlb = getLatIndex(tl) - 1;
    final int jub = getLatIndex(br) + 1;

    final int nc = _contourDepths.length;
    final double[] z = _contourDepths;

    final double[][] d = null;
    final double[] x = null;
    final double[] y = null;

    // and get on with the call
    final Conrec cr = new Conrec()
    {
      public void drawContour(final double startX, final double startY,
                              final double endX, final double endY, final double contourLevel,
                              final int contourIndex)
      {
        drawThisContour(dest, startX, startY, endX, endY, contourIndex);
      }

      /**
       * function to retrieve a data value at the indicated index
       */
      protected double valAt(final int i, final int j)
      {
        return contour_valAt(i, j);
      }

      /**
       * function to retrieve the x-location for a specific array index
       */
      protected double xValAt(final int i)
      {
        return contour_xValAt(i);
      }

      /**
       * function to retrieve the x-location for a specific array index
       */
      protected double yValAt(final int i)
      {
        return contour_yValAt(i);
      }
    };

    // get the contouring algorithm to "do it's stuff"
    cr.contour(d, ilb, iub, jlb, jub, x, y, nc, z, _gridInterval);
  }

  public final String getContourDepths()
  {
    String res = "";
    for (int i = 0; i < _contourDepths.length; i++)
    {
      final double depth = _contourDepths[i];
      if (res.length() == 0)
      {
        res = "" + (int) depth;
      }
      else
        res = res + ", " + (int) depth;
    }
    return res;
  }

  public final void setContourDepths(final String contourDepths)
  {

    // keep a sorted list of the depths
    final TreeSet<Double> sortedDepths = new TreeSet<Double>();

    // parse the string
    final java.util.StringTokenizer token = new StringTokenizer(contourDepths, ",", false);
    while (token.hasMoreTokens())
    {
      try
      {
        final double d = Double.parseDouble(token.nextToken());
        sortedDepths.add(new Double(d));
      }
      catch (NumberFormatException e)
      {
        // don't worry, we'll just move onto the next one
      }

    }

    // now build up the list of depths, if we have to
    if (sortedDepths.size() > 0)
    {
      // allocate the list of depths
      _contourDepths = new double[sortedDepths.size()];
      int counter = 0;

      // and put the depths into our working array
      for (Iterator<Double> iterator = sortedDepths.iterator(); iterator.hasNext();)
      {
        final Double thisDepth = (Double) iterator.next();
        _contourDepths[counter++] = thisDepth.doubleValue();
      }
    }
    else
    {
      // just put in some dummy ones
      _contourDepths = null;
    }
  }

  public final boolean isContoursVisible()
  {
    return _showContours;
  }

  public final void setContoursVisible(final boolean showContours)
  {
    this._showContours = showContours;
  }

  public final boolean isBathyVisible()
  {
    return _showBathy;
  }

  public final void setBathyVisible(final boolean showBathy)
  {
    this._showBathy = showBathy;
  }

  public final BoundedInteger getContourGridInterval()
  {
    return new BoundedInteger(_gridInterval, 1, 10);
  }

  public final void setContourGridInterval(final BoundedInteger gridInterval)
  {
    this._gridInterval = gridInterval.getCurrent();
  }

  public final boolean isContourOptimiseGrid()
  {
    return _optimiseGridInterval;
  }

  public final void setContourOptimiseGrid(final boolean optimiseGridInterval)
  {
    this._optimiseGridInterval = optimiseGridInterval;
  }


}
