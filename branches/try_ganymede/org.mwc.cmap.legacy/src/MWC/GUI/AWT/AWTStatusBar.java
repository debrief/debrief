
/**
 * AWT implementation of a status bar. Also contains the support class which
 * is used in both AWT and Swing implementations
 */package MWC.GUI.AWT;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: AWTStatusBar.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: AWTStatusBar.java,v $
// Revision 1.2  2004/05/24 16:29:22  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:14  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:05  Ian.Mayo
// Initial import
//
// Revision 1.5  2002-12-16 15:39:05+00  ian_mayo
// Reflect fact that location of units labels has changed
//
// Revision 1.4  2002-10-30 16:27:02+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.3  2002-10-28 09:24:29+00  ian_mayo
// minor tidying (from IntelliJ Idea)
//
// Revision 1.2  2002-05-28 09:25:37+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:15:10+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:02:22+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:46:33+01  administrator
// Initial revision
//
// Revision 1.2  2001-06-14 11:58:52+01  novatech
// include comments, support class to format range data, editable type and property editor for range units
//
// Revision 1.1  2001-01-03 13:43:04+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:43:03  ianmayo
// initial version
//
// Revision 1.1  1999-10-12 15:37:04+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:49+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-23 14:04:01+01  administrator
// Initial revision
//

import java.awt.*;
import MWC.GUI.*;
import MWC.GUI.Properties.UnitsPropertyEditor;

import java.beans.*;

public class AWTStatusBar extends Panel implements StatusBar
{
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
   * MWC.GUI.AWT.AWTStatusBar.StatusBarSupport
   */
  /////////////////////////////////////////////////////////////
  // member variables
  /**
   * ////////////////////////////////////////////////////////////
   * the text label we are handling
   */
  protected Label theText;

  /**
   * support class to help us format the text & set the correct unites
   */
  protected MWC.GUI.AWT.AWTStatusBar.StatusBarSupport _support;

  /////////////////////////////////////////////////////////////
  // constructor
  /**
   * ////////////////////////////////////////////////////////////
   *
   */
  public AWTStatusBar(MWC.GUI.Properties.PropertiesPanel panel, MWC.GUI.ToolParent parent)
  {
    theText = new Label("  ");
    theText.setAlignment(Label.CENTER);
    BorderLayout lm = new BorderLayout();
    setLayout(lm);
    add("Center", theText);

    _support = new MWC.GUI.AWT.AWTStatusBar.StatusBarSupport();
    _support.setParent(parent);

    // we should probably do something about the properties panel, another day..
  }

  /////////////////////////////////////////////////////////////
  // member functions
  /**
   * ////////////////////////////////////////////////////////////
   * set the text in the label
   *
   * @param theVal the text to display
   */

  public void setText(String theVal)
  {
    theText.setText(theVal);
  }

  /**
   * paint
   *
   * @param p1 parameter for paint
   */
  public void paint(Graphics p1)
  {
    super.paint(p1);

    Rectangle rt = super.getBounds();
    p1.setColor(Color.lightGray);
    p1.draw3DRect(1, 1, rt.width-3, rt.height-3, false);
  }


  /**
   * getInsets
   *
   * @return the returned Insets
   */
  public Insets getInsets()
  {
    return new Insets(6,6,6,6);
  }

  /**
   * set range and bearing data in this text panel
   *
   * @param range the range in degrees
   * @param bearing the bearing in radians
   */
  public void setRngBearing(double range, double bearing)
  {
    String rngStr = _support.formatRange(range);
    String brgStr = _support.formatBearing(bearing);

    setText(rngStr + " " + brgStr);
  }

  ////////////////////////////////////////////////////
  // support class which provides formatting support for range/bearing
  ////////////////////////////////////////////////////

  static public class StatusBarSupport implements MWC.GUI.Editable
  {
    /**
     * the parent which provides us with our properties
     */
    protected MWC.GUI.ToolParent _theParent;


    /** our editor
     */
    transient private Editable.EditorType _myEditor;

    /**
     * <init>
     *
     */
    public StatusBarSupport()
    {
      // load the data
    }

    public String getName()
    {
      return "Status bar properties";
    }

    public String toString()
    {
      return getName();
    }

    /**
     * set the application properties for the status bar.
     * it's this properties file which provides the status bar
     * with the details of the units it is to use
     *
     */
    public void setParent(MWC.GUI.ToolParent parent)
    {
      _theParent = parent;
    }

    /**
     * format this range using the customer preference
     *
     * @param range range in degrees
     */
    public String formatRange(double range)
    {

      String theUnits = "";
      double theRng= 0;
      String rngStr;

      // find out which type it is
      if(_theParent != null)
        theUnits = _theParent.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

      if(theUnits == null)
        theUnits =MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS;

      if(theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.YDS_UNITS) ||
        theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.OLD_YDS_UNITS))
      {
        theRng = MWC.Algorithms.Conversions.Degs2Yds(range);
      }
      else if(theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KYD_UNITS))
      {
        theRng = MWC.Algorithms.Conversions.Degs2Yds(range) / 1000;
      }
      else if(theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.METRES_UNITS))
      {
        theRng = MWC.Algorithms.Conversions.Degs2m(range);
      }
      else if(theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.KM_UNITS))
      {
        theRng = MWC.Algorithms.Conversions.Degs2Km(range);
      }
      else if(theUnits.equals(MWC.GUI.Properties.UnitsPropertyEditor.NM_UNITS))
      {
        theRng = MWC.Algorithms.Conversions.Degs2Nm(range);
      }
      else
      {
        MWC.Utilities.Errors.Trace.trace("Range/Bearing units in properties file may be corrupt");
      }

      rngStr = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(theRng);

      return rngStr + " " + theUnits;
    }

    /**
     * format this bearing using the customer preference
     *
     * @param brg the bearing in radians
     */
    public String formatBearing(double brg)
    {
      // prepare the bearing, since there's little doubt about this
      double bearing = MWC.Algorithms.Conversions.Rads2Degs(brg);

      // clip the data
      if(bearing < 0){
        bearing += 360.0;
      }

      // format it using the standard style
      return MWC.Utilities.TextFormatting.GeneralFormat.formatBearing(bearing);
    }

    /////////////////////////
    // editable stuff
    /////////////////////////

    public boolean hasEditor()
    {
      return true;
    }

    public Editable.EditorType getInfo()
    {
    if(_myEditor == null)
      _myEditor = new StatusInfo(this);

    return _myEditor;
    }

    /////////////////////
    // support for the editor class
    /////////////////////
    public String getUnits()
    {
      String theUnits = "";

      // find out which type it is
      if(_theParent != null)
        theUnits = _theParent.getProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY);

      return theUnits;
    }

    public void setUnits(String val)
    {
      if(_theParent != null)
        _theParent.setProperty(MWC.GUI.Properties.UnitsPropertyEditor.UNITS_PROPERTY, val);
    }

    //////////////////////////////////////////////////////
    // bean info for this class
    /**
   * /////////////////////////////////////////////////////
   * the set of editable properties for a status bar
   */
    public class StatusInfo extends Editable.EditorType
    {

      /**
       * constructor, takes the status bar we are editing
       *
       */
      public StatusInfo(StatusBarSupport data)
      {
        super(data, data.getName(), "");
      }

      public PropertyDescriptor[] getPropertyDescriptors()
      {
        try{
          PropertyDescriptor[] res={
            longProp("Units", "the units for display", UnitsPropertyEditor.class)
          };

          return res;

        }catch(IntrospectionException e)
        {
          return super.getPropertyDescriptors();
        }
      }

    }

    ////////////////////////////////////
    // property editor class to let us set range units


  }

}
