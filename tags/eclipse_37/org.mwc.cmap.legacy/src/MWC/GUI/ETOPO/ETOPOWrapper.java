/*
 * Created by IntelliJ IDEA.
 * User: Ian
 * Date: 23-May-02
 * Time: 13:43:47
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package MWC.GUI.ETOPO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Dimension2D;
import java.awt.image.MemoryImageSource;

import MWC.GUI.CanvasType;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Chart.Painters.SpatialRasterPainter;
import MWC.GenericData.WorldLocation;

import com.bbn.openmap.layer.etopo.ETOPOLayer;

/** class which wraps the unusedMWCETOPOLayer defined in OpenMap
 *
 */
public class ETOPOWrapper extends MWCETOPOLayer implements Runnable, BathyProvider
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** the delta for the step size of the grid (in degs)
   *
   */
  static double GRID_DELTA = MWC.Algorithms.Conversions.Nm2Degs(5);

  /** the scale factors to convert a coordinate in degs to the position in the
   * data array
   */
  float _scx;
  float _scy;

  /** where will we draw the key?
   *
   */
  protected Integer _keyLocation = null;

  /** flag for whether the loader thread is currently running
   *
   */
  transient static protected boolean _loading=false;

  /** flag for whether loading is complete
   *
   */
  transient static protected boolean _isLoaded = false;

  /** whether to show above-water data
   *
   */
  protected boolean _showLand = true;

  /** colour of this scale
   */
  private Color _myColor;

  /** the font we use for the key */
  private static Font _myFont = new Font("Arial", Font.PLAIN, 12);


  /** the image we plot
   *
   */
  private int[] _myImageBuffer;

  /** the parent set of layers we update when loading is complete
   *
   */
  private Layers _parentLayers = null;


  /** the layer this data belongs to
   *
   */
  private Layer _ourLayer = null;

  /** the thickness to plot the line
   *
   */
  private int _myThickness = 1;

  ////////////////////////////////////////////////////
  //  the colours we use to produce the range of colours
  ////////////////////////////////////////////////////

  /** the base value of the green shade
   *
   */
  public final static double GREEN_BASE_VALUE = 45;

  /** the factor we apply to the green shade
   *
   */
  public final static double GREEN_MULTIPLIER = 0.65;

  /** the factor we apply to the blue shade
   *
   */
  public final static double BLUE_MULTIPLIER = 125;

  /** the red component of the resultant colour
   *
   */
  public final static int RED_COMPONENT = 0;

  /*****************************************************************
   * WORKING VARIABLES
   ****************************************************************/
  private static Point _workingPoint = new Point(0,0);
  private static WorldLocation _workingLocation = new WorldLocation(0,0,0);




  /*****************************************************************
   * CONSTRUCTOR
   ****************************************************************/
  /** constructor, just pass the path name to the base class
   *
   */
  public ETOPOWrapper(String pathToETOPODir, Layers parentLayers, Layer ourLayer)
  {
    super(pathToETOPODir);

    // initialise the ETOPO object to our favourite settings
    super.minuteSpacing = 5;
    super.viewType = ETOPOLayer.COLOREDSHADING;

    // initialise the key location
    _keyLocation = new Integer(ETOPOPainter.KeyLocationPropertyEditor.LEFT);
    _myColor = Color.black;

    // remember the layers
    _parentLayers = parentLayers;
    _ourLayer = ourLayer;

  }

  /** accessor for whether to show land
   *
   */
  public boolean getShowLand()
  {
    return _showLand;
  }

  /** setter for whether to show land
   *
   */
  public void setShowLand(boolean val)
  {
    _showLand = val;
  }

  /** accessor to get the grid delta
   *
   */
  public double getGridDelta()
  {
    return GRID_DELTA;
  }

  /** whether the data has been loaded yet
   *
   */
  public boolean isDataLoaded()
  {
    return _isLoaded;
  }



  /** support to allow the data to be loaded in a background thread
   *
   */
  public void run()
  {
    _loading = true;

    // start the depth data loading
    try{
      long current = System.currentTimeMillis();
      loadBuffer();
      _isLoaded = true;
      System.out.println("loading complete after:" + (System.currentTimeMillis() - current)/1000 + " secs");

      // compute scalers for lat/lon indicies
      _scy = (float)bufferHeight/180F;
      _scx = (float)bufferWidth/360F;

      _parentLayers.fireModified(_ourLayer);
    }
    catch(Exception e)
    {
      MWC.Utilities.Errors.Trace.trace(e, "Failed loading ETOPO depth data");
    }

    _loading = false;
  }

  /** retrieve the current key location
   *
   */
  public Integer getKeyLocation()
  {
    return _keyLocation;
  }

  /** set the current key location
   *
   */
  public void setKeyLocation(Integer val)
  {
    _keyLocation = val;
  }

  /** current colour of the scale
   * @param val the colour
   */
    public void setColor(Color val)
    {
      _myColor = val;
    }

  /** current colour of the scale
   * @return colour
   */
    public Color getColor()
    {
      return _myColor;
    }

  public int getThickness()
  {
    return _myThickness;
  }

  public void setThickness(int thickness)
  {
    this._myThickness = thickness;
  }

  /* returns a color based on slope and elevation */
  public static int getColor(short elevation, double lowerLimit, double upperLimit, boolean showLand)
  {
    int res = 0;

    // so, just produce a shade of blue depending on how deep we are.

    // see if we are above or beneath water level
    if((elevation > 0) && (showLand))
    {
      // ABOVE WATER

      // switch to positive
      double val = elevation;
      if(val > upperLimit)
        val = upperLimit;

      double proportion = val / upperLimit;

      double color_val = proportion * 125;

      // limit the colour val to the minimum value
      int green_tone = 255 - (int)color_val;

      // just check we've got a valid colour
      green_tone = Math.min(250, green_tone);

      res = SpatialRasterPainter.getRGB(88, green_tone, 88);

     // res = new Color(88, green_tone, 88);

    }
    else
    {
      // BELOW WATER

      // switch to positive
      double val = elevation;
      if(val < lowerLimit)
        val = lowerLimit;

      double proportion = val / lowerLimit;

      double color_val = proportion * BLUE_MULTIPLIER;

      // limit the colour val to the minimum value
      int blue_tone = 255 - (int)color_val;

      // just check we've got a valid colour
      blue_tone = Math.min(250, blue_tone);

      int green =  (int)GREEN_BASE_VALUE + (int)(blue_tone * GREEN_MULTIPLIER);

      res = SpatialRasterPainter.getRGB(RED_COMPONENT, green, blue_tone);

    }

    return res;
  }

  /** paint method - produce an image for this location, and paint to the canvas
   *
   */
  public void doPaint(CanvasType dest)
  {
    // see if we have our data
    if((super.dataBuffer == null))
    {
      if(!_loading)
      {
        if (dataBuffer == null)
        {
          // start loading data, in a new thread
          Thread runner = new Thread(this);
          runner.start();
        }
      }
      else
      {
        System.out.println("waiting to load data");
      }
      return;
    }

    // set our width
    float oldThickness = dest.getLineWidth();
    dest.setLineWidth(_myThickness);

    // compute our deltas
    int width = (int)dest.getProjection().getScreenArea().getWidth();
    int height = (int)dest.getProjection().getScreenArea().getHeight();

    // create int array to hold colors
    if((_myImageBuffer == null) || (_myImageBuffer.length != width * height))
    {
      System.out.println("creating new image buffer, size:" + width * height);
      _myImageBuffer = new int[width*height];
    }

    // keep track of the maximum depth
    int min_height = 0;
    int max_height = 0;

    // build array
    for (int y=0; y<height; y++)     // work our way up the screen
    {
      // process each column
      for (int x=0; x<width;  x++)  // work our way across the screen
      {
        // inverse project x,y to lon,lat
        _workingPoint.setLocation(x, y);
        _workingLocation = dest.getProjection().toWorld(_workingPoint);

        short thisDepth = (short)getDepthAt(_workingLocation);

        // keep track of the maximum depth
        min_height = Math.min(thisDepth, min_height);
        max_height = Math.max(thisDepth, max_height);

        // find out the index of where we are going to put this data
        int idx = y*width + x;

        // put this elevation into our array
        _myImageBuffer[idx] = thisDepth;

      }
    }

    // do a second pass to set the actual colours
    for(int i=0;i<width*height;i++)
    {
      _myImageBuffer[i] = getColor((short)_myImageBuffer[i], min_height, max_height, _showLand);
    }

    // create the raster
    //  ret = new OMRaster(0,0,width,height,colors);
    MemoryImageSource mis = new MemoryImageSource(width, height, _myImageBuffer, 0, width);
    Image im = Toolkit.getDefaultToolkit().createImage(mis);

    // ok, actually draw the image
    dest.drawImage(im, 0, 0, width, height, this);

    // finally draw in the scale
    drawKey(dest, min_height, max_height, _keyLocation);

    // restore width
    dest.setLineWidth(oldThickness);
  }


  /** method to extract the depth at a particular coordinate
   *
   */
  public double getDepthAt(WorldLocation location)
  {
    double res = 0;

    // get point values
    double lat = location.getLat();
    double lon = location.getLong();

    // check
    if (lon<0.) lon += 360.;

    // find indicies
    int lat_idx = (int)((90.0 - lat)*_scy);
    int lon_idx = (int)(lon*_scx);

    // check we don't go off the plot
    if((lat_idx < 0) || (lat_idx > bufferHeight) || (lon_idx < 0) || (lon_idx > bufferWidth))
    {
//      int idx = y*width + x;
//      _myImageBuffer[idx] = 0;
//      continue;
      res = 0;
    }
    else
    {

      // offset
      int ofs = lon_idx+lat_idx*bufferWidth;

      // WORKAROUND, to handle the missing zero longitude data
      // just do our hack to handle the missing elevation at long == 0
      if(lon_idx != 4320)
      {
        // hey, no problem. just take the elevation directly
        res = dataBuffer[ofs];
      }
      else
      {
        // hey, we've got no depth for this longitude. take the average
        // of the depths either side
        short sum = (short) (dataBuffer[ofs - 1] + dataBuffer[ofs + 1]);
        res = (short)(sum / 2);
      }
    }

    return res;

  }

  /** method to draw in the key
   *
   */
  protected void drawKey(CanvasType dest, double min_height, double max_height, Integer keyLocation)
  {

    // how big is the screen?
    Dimension screen_size = dest.getProjection().getScreenArea();

    // are we showing land?
    if(!_showLand)
    {
      // no, just make it very shallow
      max_height = 0;
    }

    // define the approximate proportions of the key
    final float MIN_WID = 0.05f;
//    final float WID = 0.05f;
    final float MIN_LEN = 0.05f;
    final float LEN = 0.9f;

    // how high is a piece of text at this scale?
    int txtHt = dest.getStringHeight(_myFont) + 5;
    int txtWid = dest.getStringWidth(_myFont, " " + (int)Math.max(min_height * -1, max_height));

    // sort out where the key is going to go
    // determine the start / end points according to the scale location
    // variable
    Point TL=null;
    Point BR=null;

    // find out how we distribute the rectangles which make up the key
    int num_labels = 0;
    double stepHeight = 0d;
    double stepWidth = 0d;
    int total_height = 0;
    Dimension2D rectSize = new Dimension();

    switch(keyLocation.intValue())
    {
      case(ETOPOPainter.KeyLocationPropertyEditor.NOT_SHOWN):
        break;
      case(ETOPOPainter.KeyLocationPropertyEditor.LEFT):
        TL = new Point((int)(screen_size.width * MIN_WID), (int)(screen_size.height * MIN_LEN));
        BR = new Point(TL.x + txtWid, (int)(screen_size.height * (MIN_LEN + LEN)));
        num_labels = (BR.y - TL.y) / txtHt;
        total_height = BR.y - TL.y;
        stepHeight = total_height / (double)num_labels;
        rectSize = new Dimension(BR.x - TL.x, (int) stepHeight + 1);
        break;
      case(ETOPOPainter.KeyLocationPropertyEditor.RIGHT):
        TL = new Point((int)(screen_size.width - (screen_size.width * MIN_WID) - txtWid), (int)(screen_size.height * MIN_LEN));
        BR = new Point(TL.x + txtWid, (int)(screen_size.height * (MIN_LEN + LEN)));
        num_labels = (BR.y - TL.y) / txtHt;
        total_height = BR.y - TL.y;
        stepHeight = total_height / (double)num_labels;
        rectSize = new Dimension(BR.x - TL.x, (int) stepHeight + 1);
        break;
      case(ETOPOPainter.KeyLocationPropertyEditor.TOP):
        TL = new Point((int)(screen_size.width * MIN_WID), (int)(screen_size.height * MIN_LEN));
        BR = new Point((int)(screen_size.width * (MIN_LEN + LEN)), (TL.y + txtHt));
        num_labels = (BR.x - TL.x) / txtWid;
        total_height = BR.x - TL.x;
        stepHeight = 0;
        stepWidth = (BR.x - TL.x)/(double)num_labels;
        rectSize = new Dimension((int) stepWidth + 1, BR.y - TL.y  );
        break;
      default:
        TL = new Point((int)(screen_size.width * MIN_WID), (int)(screen_size.height - (screen_size.height * MIN_LEN) - txtHt));
        BR = new Point((int)(screen_size.width * (MIN_LEN + LEN)), (int)(screen_size.height - (screen_size.height * MIN_LEN)));
        num_labels = (BR.x - TL.x) / txtWid;
        total_height = BR.x - TL.x;
        stepHeight = 0;
        stepWidth = (BR.x - TL.x)/(double)num_labels;
        rectSize = new Dimension((int) stepWidth + 1, BR.y - TL.y  );
        break;
    }

    // check its worth bothering with
    if((BR == null) || (TL == null))
      return;

    // sort out how many shade steps we are going to produce (it depends on how big the text string is)
    int depth_step = (int)((max_height - min_height)/ num_labels);

    // get ready to step through the colours
    Color thisColor = new Color(0,0,0);
    Point thisPoint = new Point(TL);

    for(int i=0;i<num_labels;i++)
    {

      short thisDepth =  (short)(max_height-(i * depth_step));

      // produce this new colour
      thisColor = new Color(getColor(thisDepth, min_height, max_height, _showLand));

      // draw this rectangle
      dest.setColor(thisColor);

      thisPoint.move(TL.x + (int)(i * stepWidth), TL.y + (int)(i * stepHeight));

      dest.fillRect(thisPoint.x, thisPoint.y,
        (int)(rectSize.getWidth()), (int)(rectSize.getHeight()));

      // insert the depth value
      dest.setColor(_myColor);

      String thisLabel = "" + Math.abs(thisDepth);

      int thisTxtWid = dest.getStringWidth(_myFont,thisLabel);

      dest.drawText(thisLabel, (int)(thisPoint.x + rectSize.getWidth() - thisTxtWid - 1), thisPoint.y + txtHt - 2);


    }

    dest.setColor(_myColor);
 //   dest.drawRect(TL.x-1, TL.y-1, (BR.x - TL.x)+2, (BR.y - TL.y)+2);
    dest.drawRect(TL.x, TL.y, BR.x - TL.x, BR.y - TL.y);
  }

}
