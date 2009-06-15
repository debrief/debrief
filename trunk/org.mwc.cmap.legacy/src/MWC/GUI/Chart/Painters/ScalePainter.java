package MWC.GUI.Chart.Painters;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ScalePainter.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.12 $
// $Log: ScalePainter.java,v $
// Revision 1.12  2007/01/22 11:27:08  ian.mayo
// Correctly determine whether plural units required.
//
// Revision 1.11  2007/01/15 15:49:48  ian.mayo
// Introduce plural units, allow for unit-type not being found
//
// Revision 1.10  2006/05/25 14:10:39  Ian.Mayo
// Make plottables comparable
//
// Revision 1.9  2006/01/05 11:45:46  Ian.Mayo
// Sort out serialising problem (version id)
//
// Revision 1.8  2005/09/13 09:30:23  Ian.Mayo
// Eclipse tidying
//
// Revision 1.7  2005/09/07 13:45:46  Ian.Mayo
// Minor tidying
//
// Revision 1.6  2005/05/19 14:46:49  Ian.Mayo
// Add more categories to editable bits
//
// Revision 1.5  2005/01/28 09:34:25  Ian.Mayo
// Minor refactoring to reflect change in Diagonal Property Editor
//
// Revision 1.4  2004/08/31 09:38:07  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.3  2004/05/25 14:47:01  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:16  ian
// no message
//
// Revision 1.2  2003/10/21 13:16:54  Ian.Mayo
// Correct the way we calculate the data width
//
// Revision 1.1.1.1  2003/07/17 10:07:12  Ian.Mayo
// Initial import
//
// Revision 1.7  2003-07-04 11:00:53+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.6  2002-12-17 14:15:51+00  ian_mayo
// Tidying according to IntelliJ Idea advice
//
// Revision 1.5  2002-12-16 15:38:25+00  ian_mayo
// Support for selecting units of scale
//
// Revision 1.4  2002-10-30 16:26:54+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-07-12 15:46:55+01  ian_mayo
// Use constant to represent error value
//
// Revision 1.2  2002-05-28 09:25:39+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:07+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:15+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:29+01  administrator
// Initial revision
//
// Revision 1.3  2001-02-01 09:31:38+00  novatech
// have our own, internal font to let us calc the size correctly
//
// Revision 1.2  2001-01-22 12:29:29+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:43:00+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:44:19  ianmayo
// initial version
//
// Revision 1.9  2000-10-26 15:37:41+01  ian_mayo
// add new, larger scale size, so that we can see whole globe
//
// Revision 1.8  2000-10-09 15:48:21+01  ian_mayo
// Allow user edit of Scale values
//
// Revision 1.7  2000-09-21 09:06:41+01  ian_mayo
// make Editable.EditorType a transient parameter, to prevent it being written to file
//
// Revision 1.6  2000-08-18 13:36:03+01  ian_mayo
// implement singleton of Editable.EditorType
//
// Revision 1.5  2000-08-14 14:12:12+01  ian_mayo
// switch to kiloyards if necessary
//
// Revision 1.4  2000-08-11 08:42:04+01  ian_mayo
// tidy beaninfo
//
// Revision 1.3  2000-08-09 16:03:10+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.2  2000-05-23 16:07:44+01  ian_mayo
// improved automatic range scales
//
// Revision 1.1  2000-05-23 13:57:35+01  ian_mayo
// Initial revision
//


import java.awt.Color;
import java.awt.Point;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Plottable;
import MWC.GUI.Properties.DiagonalLocationPropertyEditor;
import MWC.GUI.Properties.UnitsPropertyEditor;
import MWC.GenericData.WorldLocation;

/**
 * Class to plot a scale onto a plot
 */
public class ScalePainter implements Plottable, Serializable
{

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////
  /**
   * version number for this painter
   */
  static final long serialVersionUID = -1;

  /**
   * colour of this scale
   */
  Color _myColor;
  /**
   * whether we are visible or not
   */
  boolean _isOn;

  /**
   * default location for the scale
   */
  protected int _location = DiagonalLocationPropertyEditor.BOTTOM_RIGHT;

  /**
   * set of values used to doDecide on what steps to use for the scale
   */
  transient private Label_Limit _limits[];

  /**
   * our editor
   */
  transient private Editable.EditorType _myEditor;

  /**
   * the maximum value to plot on the scale axis
   */
  private long _scaleMax;

  /**
   * the step size to use on the axis
   */
  private long _scaleStep;

  /**
   * whether we are in auto mode for the scale
   */
  private boolean _autoScale = true;

  /**
   * the units to use for the scale
   */
  private UnitsConverter _DisplayUnits = null;

  /**
   * the list of units types we know about (we don't remember this when serialising, we create it afresh)
   */
  private static transient java.util.HashMap<String, UnitsConverter> _unitsList;

  /**
   * the font we use for the D DifarSymbols
   */
  private static java.awt.Font _myFont = new java.awt.Font("Arial",
                                                           java.awt.Font.PLAIN,
                                                           12);

  /////////////////////////////////////////////////////////////
  // constructor
  ////////////////////////////////////////////////////////////
  /**
   * constructor, also initialises the list of limits we use
   */
  public ScalePainter()
  {
    _myColor = Color.darkGray;
    _isOn = true;

    // create our array of limits
    initialiseLimits();

    // create the list of units
    setupUnits();

    // start off in known units
    this.setDisplayUnits(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS);
  }

  /**
   * setup the list of units converters
   */
  private void setupUnits()
  {
  	
  	// just check it hasn't already been generated
  	if(_unitsList != null)
  		return;
  	
    // create the list itself
    _unitsList = new java.util.HashMap<String, UnitsConverter>();

    // and put in the converters
    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS, new UnitsConverter(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public double convertThis(double degs)
      {
        return MWC.Algorithms.Conversions.Degs2Km(degs);
      }

      public String writeThis(double myUnits)
      {
        return "" + (int) myUnits;
      }
    });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS, new UnitsConverter(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public double convertThis(double degs)
      {
        return MWC.Algorithms.Conversions.Degs2m(degs);
      }

      public String writeThis(double myUnits)
      {
        return "" + (int) myUnits;
      }
    });


    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS, new UnitsConverter(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public double convertThis(double degs)
      {
        return MWC.Algorithms.Conversions.Degs2Nm(degs);
      }

      public String writeThis(double myUnits)
      {
        return "" + (int) myUnits;
      }
    });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS, new UnitsConverter(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public double convertThis(double degs)
      {
        return MWC.Algorithms.Conversions.Degs2Yds(degs);
      }

      public String writeThis(double myUnits)
      {
        return "" + (int) myUnits;
      }
    });

    _unitsList.put(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS, new UnitsConverter(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS)
    {
      /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public double convertThis(double degs)
      {
        return MWC.Algorithms.Conversions.Degs2Yds(degs) / 1000;
      }

      public String writeThis(double myUnits)
      {
        return "" + (int) myUnits;
      }
    });


  }


  /**
   * setup the list
   */
  private void initialiseLimits()
  {
    // create the array of limits values in a tmp parameter
    Label_Limit[] tmp = {
      new Label_Limit(7, 1),
      new Label_Limit(20, 5),
      new Label_Limit(70, 10),
      new Label_Limit(200, 50),
      new Label_Limit(700, 100),
      new Label_Limit(2000, 500),
      new Label_Limit(7000, 1000),
      new Label_Limit(20000, 5000),
      new Label_Limit(70000, 10000),
      new Label_Limit(200000, 50000),
      new Label_Limit(700000, 100000),
      new Label_Limit(2000000, 500000),
      new Label_Limit(7000000, 1000000),
      new Label_Limit(20000000, 5000000),
      new Label_Limit(70000000, 10000000)};

    // and now store the array in our local variable
    _limits = tmp;
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  /**
   * whether the scale is visible or not
   *
   * @param val yes/no visibility
   */
  public void setVisible(boolean val)
  {
    _isOn = val;
  }

  /**
   * whether the scale is visible or not
   *
   * @return yes/no
   */
  public boolean getVisible()
  {
    return _isOn;
  }

  /**
   * current colour of the scale
   *
   * @param val the colour
   */
  public void setColor(Color val)
  {
    _myColor = val;
  }

  /**
   * current colour of the scale
   *
   * @return colour
   */
  public Color getColor()
  {
    return _myColor;
  }

  /**
   * which corner to position the scale
   *
   * @param loc one of the enumerated types listed earlier
   */
  public void setLocation(Integer loc)
  {
    _location = loc.intValue();
  }

  /**
   * retrieve the current location of the scale
   *
   * @return the current location, from the enumerated types defined for this class
   */
  public Integer getLocation()
  {
    return new Integer(_location);
  }

  /**
   * redraw the scale
   *
   * @param g the destination
   */
  public void paint(CanvasType g)
  {

    // check we are visible
    if (!_isOn)
      return;

    // what is the screen width in logical coordinate?
    MWC.Algorithms.PlainProjection proj = g.getProjection();

    // find the screen width
    java.awt.Dimension screen_size = proj.getScreenArea().getSize();
    long screen_width = screen_size.width;

    // generate screen points in the middle on the left & right-hand sides
    Point left = new Point(0, (int) screen_size.getHeight() / 2);
    Point right = new Point((int) screen_width, (int) screen_size.getHeight() / 2);

    // and now world locations to represent them
    WorldLocation leftLoc = new WorldLocation(proj.toWorld(left));
    WorldLocation rightLoc = proj.toWorld(right);

    // and get the distance between them
    double data_width = rightLoc.rangeFrom(leftLoc);

    // convert this data width (in degs) to our units
    data_width = _DisplayUnits.convertThis(data_width);

    // make a guess at the scale
    double scale = data_width / screen_width;

    // clip the screen width so that the scale bar doesn't go across the
    // whole screen, and so that we can offset it a bit.
    data_width *= 0.5;


    // trap the occasion where the data has zero size
    if (data_width == 0)
    {
      return;
    }

    // find the current text height
    int txtHt = g.getStringHeight(_myFont);

    // we now have to determine the labels to use on the axis

    long first;

    // since we always start the axis at zero,
    first = 0;

    // check we have our set of data
    if (_limits == null)
      initialiseLimits();


    // find the range we are working in
    int counter = 0;
    while ((counter < _limits.length) &&
      (data_width > _limits[counter].upper_limit))
    {
      counter++;
    }

    // check if we have sufficient data to perform range scale
    if (!_autoScale)
    {
      if ((_scaleMax == 0) || (_scaleStep == 0))
      {
        _autoScale = true;
      }
    }

    // doDecide if we are plotting in auto mode or not
    if (_autoScale)
    {

      // check that we aren't trying to zoom out beyond the size of our wonderful planet
      if (counter == _limits.length)
      {
        MWC.Utilities.Errors.Trace.trace("Zoomed out too far!");
        return;
      }

      // set our increment counter
      _scaleStep = _limits[counter].increment;

      // determine the value of the last scale object we are trying to
      // plot
      _scaleMax = ((long) data_width / _scaleStep * _scaleStep + _scaleStep);


      if (_myEditor != null)
      {
        _myEditor.fireChanged(this, "Calc", null, this);
      }
    }


    // find the width of the scale in screen units
    int scale_width = (int) (_scaleMax / scale);

    // determine the start / end points according to the scale location
    // variable
    java.awt.Point TL = null, BR = null;
    switch (_location)
    {
      case (DiagonalLocationPropertyEditor.TOP_LEFT):
        TL = new Point((int) (screen_size.width * 0.05), (int) (txtHt + screen_size.height * 0.032));
        BR = new Point((TL.x + scale_width), (int) (txtHt + screen_size.height * 0.035));
        break;
      case (DiagonalLocationPropertyEditor.TOP_RIGHT):
        BR = new Point((int) (screen_size.width * 0.95), (int) (txtHt + screen_size.height * 0.035));
        TL = new Point((BR.x - scale_width), (int) (txtHt + screen_size.height * 0.032));
        break;
      case (DiagonalLocationPropertyEditor.BOTTOM_LEFT):
        TL = new Point((int) (screen_size.width * 0.05), (int) (screen_size.height * 0.987));
        BR = new Point((TL.x + scale_width), (int) (screen_size.height * 0.99));
        break;
      case (DiagonalLocationPropertyEditor.BOTTOM_RIGHT):
        BR = new Point((int) (screen_size.width * 0.95), (int) (screen_size.height * 0.99));
        TL = new Point((BR.x - scale_width), (int) (screen_size.height * 0.987));
        break;
    }

    // create the figures to step along the line
    int num_ticks = (int) ((_scaleMax - first) / _scaleStep);
    int tick_step = (int) (_scaleStep / scale);

    // set our drawing flags
    boolean fill_this = true;
    boolean first_point = true;

    // setup the drawing object
    g.setColor(this.getColor());

    // first draw in 10 ticks in the first section of the scale
    double tmp_tick_step = tick_step / 10.0;
    for (int j = 0; j < 10; j++)
    {
      // put in the tick at this point
      int this_dist = TL.x + (int) (j * tmp_tick_step);

      // check if we are 1/2 way along the strip. If so the we'll draw in a
      // higher tick
      if (j == 5)
        g.drawLine(this_dist, BR.y, this_dist, TL.y - (int) (txtHt * 0.5));
      else
        g.drawLine(this_dist, BR.y, this_dist, (int) (TL.y - (txtHt * 0.3)));
    }


    // draw in the major ticks and the labels
    for (int i = 0; i <= num_ticks; i++)
    {
    	// sort out the label
      String str = "" + (int) (i * _scaleStep) + _DisplayUnits.getUnits();
      
      // make the label plural if it's > 0
      if((i * _scaleStep) > 1)
      	str += "s";

      // find the text size for this label
      int wid = g.getStringWidth(_myFont, str);

      // put in the tick at this point
      int this_dist = TL.x + i * tick_step;
      g.drawLine(this_dist, BR.y, this_dist, TL.y - (int) (txtHt * 0.5));

      if (first_point)
      {
        // skip this one
        first_point = false;
      }
      else
      {
        // we will draw in the boxes by drawing to the previous point,
        // we cant do this for the first point
        if (fill_this)
        {
          g.fillRect(this_dist - tick_step, BR.y, tick_step + 1, (BR.y - TL.y) + 1);
        }
        else
        {
          g.drawRect(this_dist - tick_step, BR.y, tick_step + 1, BR.y - TL.y);
        }

        // flip the counter to paint alternate panels
        fill_this = !fill_this;

      }

      // draw in the scale value
      g.drawText(_myFont, str, this_dist - (wid / 2), (int) (TL.y - (0.7 * txtHt)));

    }

  }

  /**
   * the area covered by the scale.  It's null in this case, since the scale resizes to suit the data area.
   *
   * @return always null - meaning the scale doesn't mind what size the visible plot is
   */
  public MWC.GenericData.WorldArea getBounds()
  {
    // doesn't return a sensible size
    return null;
  }

  /**
   * the range of the scale from a point (ignored)
   *
   * @param other the other point
   * @return INVALID_RANGE since this is value can't be calculated
   */
  public double rangeFrom(MWC.GenericData.WorldLocation other)
  {
    // doesn't return a sensible distance;
    return INVALID_RANGE;
  }

  /**
   * return this item as a string
   *
   * @return the name of the scale
   */
  public String toString()
  {
    return getName();
  }

  /**
   * get the name of the scale
   *
   * @return the name of the scale
   */
  public String getName()
  {
    return "Scale";
  }

  /**
   * get the max limit on the scale
   *
   * @return the max value
   */
  public Long getScaleMax()
  {
    return new Long(_scaleMax);
  }

  /**
   * set the max limit on the scale
   *
   * @param val the max value
   */
  public void setScaleMax(Long val)
  {
    _scaleMax = val.longValue();
  }

  /**
   * get the step size on the scale
   *
   * @return the step size
   */
  public Long getScaleStep()
  {
    return new Long(_scaleStep);
  }

  /**
   * set the step size on the scale
   *
   * @param val the step size
   */
  public void setScaleStep(Long val)
  {
    _scaleStep = val.longValue();
  }

  /**
   * whether the scale is in auto mode
   *
   * @return auto mode
   */
  public boolean getAutoMode()
  {
    return _autoScale;
  }

  /**
   * set the mode for auto-calculation of scales
   *
   * @param val the new mode for auto
   */
  public void setAutoMode(boolean val)
  {
    _autoScale = val;
  }

  public String getDisplayUnits()
  {
    return _DisplayUnits.getUnits();
  }

  public void setDisplayUnits(String DisplayUnits)
  {
  	// generate the units, if we have to
  	setupUnits();
  
  	// see what they asked for
  	UnitsConverter theConverter =(UnitsConverter) ScalePainter._unitsList.get(DisplayUnits); 
  	
  	// did we find the text string?
  	if(theConverter == null)
  	{
  		theConverter =(UnitsConverter) ScalePainter._unitsList.get(UnitsPropertyEditor.KYD_UNITS);
  	}
  	
    this._DisplayUnits = theConverter;
  }


  /**
   * whether the scale has an editor
   *
   * @return yes
   */
  public boolean hasEditor()
  {
    return true;
  }

  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new ScalePainterInfo(this);

    return _myEditor;
  }


  ////////////////////////////////////
  // static interior class to convert between units
  ////////////////////////////////////
  abstract private static class UnitsConverter implements Serializable
  {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * the label we use for our inits
     */
    private String _myUnits;

    /**
     * constructor
     *
     * @param myUnits
     */
    public UnitsConverter(String myUnits)
    {
      this._myUnits = myUnits;
    }

    abstract public double convertThis(double degs);

    abstract public String writeThis(double myUnits);

    public String getUnits()
    {
      return _myUnits;
    }

  }

  /////////////////////////////////////////////////////////////
  // info class
  ////////////////////////////////////////////////////////////
  public class ScalePainterInfo extends Editable.EditorType implements Serializable
  {

    // give it some old version id
    static final long serialVersionUID = 1L;
    
    public ScalePainterInfo(ScalePainter data)
    {
      super(data, data.getName(), "");
    }

    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] res = {
          prop("Color", "the Color to draw the Scale", FORMAT),
          prop("Visible", "whether this Scale is visible", VISIBILITY),
          prop("ScaleMax", "the maximum value of the scale in yards", FORMAT),
          prop("ScaleStep", "the step size of the scale in yards", FORMAT),
          prop("AutoMode", "whether to automatically calculate the scale values"),
          longProp("DisplayUnits", "the units to use to display ranges on the scale",
                   MWC.GUI.Properties.UnitsPropertyEditor.class, FORMAT),
          longProp("Location",
                   "the scale location",
                   MWC.GUI.Properties.DiagonalLocationPropertyEditor.class, FORMAT)
        };

        return res;
      }
      catch (IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }
  }

  /////////////////////////////////////////////////////////////
  // scale limits and labels from a data range
  ////////////////////////////////////////////////////////////
  class Label_Limit implements Serializable
  {
    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		long upper_limit;
    long increment;

    Label_Limit(long limit, long inc)
    {
      upper_limit = limit;
      increment = inc;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class ScalePainterTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ScalePainterTest(String val)
    {
      super(val);
    }

    public void testMyParams()
    {
      MWC.GUI.Editable ed = new ScalePainter();
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }

	public int compareTo(Plottable arg0)
	{
		Plottable other = (Plottable) arg0;
		return this.getName().compareTo(other.getName());
	}

}

