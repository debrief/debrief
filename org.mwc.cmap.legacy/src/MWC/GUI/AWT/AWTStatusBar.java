
/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *  
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *  
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *******************************************************************************/
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
  public AWTStatusBar(final MWC.GUI.Properties.PropertiesPanel panel, final MWC.GUI.ToolParent parent)
  {
    theText = new Label("  ");
    theText.setAlignment(Label.CENTER);
    final BorderLayout lm = new BorderLayout();
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

  public void setText(final String theVal)
  {
    theText.setText(theVal);
  }

  /**
   * paint
   *
   * @param p1 parameter for paint
   */
  public void paint(final Graphics p1)
  {
    super.paint(p1);

    final Rectangle rt = super.getBounds();
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
  public void setRngBearing(final double range, final double bearing)
  {
    final String rngStr = _support.formatRange(range);
    final String brgStr = _support.formatBearing(bearing);

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
    public void setParent(final MWC.GUI.ToolParent parent)
    {
      _theParent = parent;
    }

    /**
     * format this range using the customer preference
     *
     * @param range range in degrees
     */
    public String formatRange(final double range)
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
    public String formatBearing(final double brg)
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

    public void setUnits(final String val)
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
      public StatusInfo(final StatusBarSupport data)
      {
        super(data, data.getName(), "");
      }

      public PropertyDescriptor[] getPropertyDescriptors()
      {
        try{
          final PropertyDescriptor[] res={
            longProp("Units", "the units for display", UnitsPropertyEditor.class)
          };

          return res;

        }catch(final IntrospectionException e)
        {
          return super.getPropertyDescriptors();
        }
      }

    }

    ////////////////////////////////////
    // property editor class to let us set range units


  }

}
