// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PatternBuilderType.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: PatternBuilderType.java,v $
// Revision 1.3  2005/12/13 09:04:50  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:34  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:48:51  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.3  2003-03-19 15:36:51+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:45+01  ian_mayo
// Initial revision
//
// Revision 1.4  2002-01-24 14:23:37+00  administrator
// Reflect change in Layers reformat and modified events which take an indication of which layer has been modified - a step towards per-layer graphics repaints
//
// Revision 1.3  2002-01-17 20:21:30+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.2  2001-08-31 09:56:08+01  administrator
// Clear data properly
//
// Revision 1.1  2001-08-21 12:15:05+01  administrator
// improve tidying up
//
// Revision 1.0  2001-07-17 08:41:14+01  administrator
// Initial revision
//
// Revision 1.4  2001-02-01 09:30:47+00  novatech
// initialise buoy pattern wrapper with symbol size and type
//
// Revision 1.3  2001-01-21 21:34:57+00  novatech
// handle the buoy colours
//
// Revision 1.2  2001-01-18 13:14:41+00  novatech
// use correct units
//
// Revision 1.1  2001-01-17 13:20:44+00  novatech
// Initial revision
//
// Revision 1.8  2001-01-11 15:36:46+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.7  2001-01-11 11:52:59+00  novatech
// Before we switch from long dates to java.util.date dates.
//
// Revision 1.6  2001-01-10 09:34:20+00  novatech
// white space
//
// Revision 1.5  2001-01-08 11:40:22+00  novatech
// general tidying up & support for common code
//
// Revision 1.4  2001-01-05 09:16:02+00  novatech
// move the new symbol formatting to this parent class from the child classes
//
// Revision 1.3  2001-01-04 16:34:59+00  novatech
// removed debug lines
//
// Revision 1.2  2001-01-04 14:02:24+00  novatech
// switch from interface to Class, provide "overview" properties and processes
//
// Revision 1.1  2001-01-03 16:02:37+00  novatech
// Initial revision
//
package Debrief.Tools.Palette.BuoyPatterns;

import MWC.GenericData.*;

abstract public class PatternBuilderType implements MWC.GUI.Editable
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  /** the properties panel we are editing ourselves within
   */
  private final MWC.GUI.Properties.PropertiesPanel _thePanel;
  /** the set of layers we put the pattern into
   */
  private final MWC.GUI.Layers _theData;
  /** the default colour for this pattern
   */
  private java.awt.Color _theColor;
  /** the starting point for the pattern
   */
  private HiResDate _theStartDTG = null;
  /** the time increment before the next buoy is dropped
   */
  private long _theBuoyPatternLifetime = 1000l * 1000 * 60 * 60; // 1 hour expressed in micros
  /** whether we show the label for each buoy
   */
  private boolean _labelVisible = false;
  /** the jig point used for pattern creation
   */
  private WorldLocation _jigPoint;
  /** how far the kingpin is from the jig point
   */
  private double _kingpinRange = 0.0; // in miles
  /** the bearing of the kingpin from the jig point
   */
  private double _kingpinBearing = 0.0; // in degrees

  /** the name of this pattern
   */
  private String _name;

  /** number of buoys in this barrier
   */
  private int _number;

  /** the symbol for this label
   */
  private MWC.GUI.Shapes.Symbols.PlainSymbol _theShape;

  /** remember the last pattern added, so that we can remove it
      */
  private Debrief.Wrappers.BuoyPatternWrapper _theLastPattern = null;

  //////////////////////////////////////////
  // Member functions
  //////////////////////////////////////////

  public PatternBuilderType(WorldLocation centre,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layers theData)
  {
    _jigPoint = centre;
    _thePanel = thePanel;
    _theData = theData;
    _theColor = java.awt.Color.red;

    // initialise the shape
    _theShape = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol("Square");
    _theShape.setColor(_theColor);
  }

  /** method called by child classes to format a new symbol (LabelWrapper) with
   *  the current default formatting options
   */
  final void formatSymbol(Debrief.Wrappers.LabelWrapper wrapper, Debrief.Wrappers.BuoyPatternWrapper parent)
  {
      // get the shape for the symbol
      String type = getSymbolType();

      // create a new symbol
      wrapper.setSymbolType(type);

      // set the size of the symbol
      wrapper.setSymbolSize(getSymbolSize());

      // set the colour of this symbol to the default colour
      wrapper.setColor(getColor());

      // specify whether the buoy label should be shown
      wrapper.setLabelVisible(new Boolean(getBuoyLabelVisible()));

      // the time period is managed by the buoypattern, so assign it
      wrapper.setTimePeriod(parent);

      // store the parent data within the symbol
      wrapper.setParent(parent);

      // add this point to the pattern
      parent.add(wrapper);

  }

  /** whether there is any edit information for this item
 * this is a convenience function to save creating the EditorType data
 * first
 * @return yes/no
 */
  public final boolean hasEditor()
  {
    return true;
  }

  /** get the editor for this item
 * @return the BeanInfo data for this editable object
 */
  abstract public MWC.GUI.Editable.EditorType getInfo();

  /** add the particular pattern of buoys to this pattern
   */
  abstract protected void addBuoys(Debrief.Wrappers.BuoyPatternWrapper pattern);

  /** perform the actual creation process
   */
  final void Create()
  {
    // create the new feature
    Debrief.Wrappers.BuoyPatternWrapper bw = new Debrief.Wrappers.BuoyPatternWrapper(_jigPoint);

    // and set the name
    bw.setName(getName());

    // and the colour
    bw.setColor(getColor());

    // set the default colour for the buoys
    bw.setBuoyColor(getColor());

    // update the default symbol size for the buoys
    bw.setBuoySymbolSize(this.getSymbolSize().doubleValue());

    // update the default symbol style for the buoys
    bw.setBuoySymbolType(this.getSymbolType());

    // set the start and finish times
    bw.setStartDTG(_theStartDTG);

    // only set the finish time if we have a valid lifetime
    if(_theStartDTG != null)
    {
      bw.setEndDTG( new HiResDate(0, _theStartDTG.getMicros() + _theBuoyPatternLifetime));
    }

    // create the buoys to place into the buoypattern
    addBuoys(bw);

    // add this wrapper as a new layer
    _theData.addThisLayerAllowDuplication(bw);

    // and start editing this layer
    _thePanel.addEditor(bw.getInfo(), null);

    // inform the data editors
    _theData.fireExtended();

    // remember this buoypattern so that we can delete it, if we want to
    _theLastPattern = bw;

  //  _theData = null;
  }

  public final void execute()
  {
  }

  public final void undo()
  {
    if(_theLastPattern != null)
    {
      // remove the layer form the data
      _theData.removeThisLayer(_theLastPattern);

      _theData.fireExtended();
    }
  }

  //////////////////////////////
  // help for child classes
  //////////////////////////////
  public final WorldLocation getKingpin()
  {
    double rng_degs = MWC.Algorithms.Conversions.Nm2Degs(_kingpinRange);
    double brg_rads = MWC.Algorithms.Conversions.Degs2Rads(_kingpinBearing);
    WorldVector offset = new WorldVector(brg_rads, rng_degs, 0.0);
    WorldLocation res = _jigPoint.add(offset);
    return res;
  }

  //////////////////////////////
  // help for Bean editors
  //////////////////////////////


  public String getSymbolType()
  {
    return _theShape.getType();
  }

  public final void setSymbolType(String val)
  {
    // is this the type of our symbol?
    if(val.equals(_theShape.getType()))
    {
      // don't bother we're using it already
    }
    else
    {
      // remember the size of the symbol
      double scale = _theShape.getScaleVal();
      // replace our symbol with this new one
      _theShape = null;
      _theShape = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol(val);
      _theShape.setColor(this.getColor());

      // update the colour
      _theShape.setScaleVal(scale);
    }
  }

  public final void setSymbolSize(Double val)
  {
    _theShape.setScaleVal(val.doubleValue());
  }

  public Double getSymbolSize()
  {
    return new Double(_theShape.getScaleVal());
  }

  public final void setColor(java.awt.Color val)
  {
    _theColor = val;
  }

  public java.awt.Color getColor()
  {
    return _theColor;
  }

  public final HiResDate getDateTimeGroup()
  {
    return  _theStartDTG;
  }

  public final void setDateTimeGroup(HiResDate val)
  {
    _theStartDTG = val;
  }



  // set the lifetime of the buoypattern (expressed in hours)
  public final void setDuration(Duration val)
  {
    // convert hours to millis
    _theBuoyPatternLifetime = (long) val.getValueIn(Duration.MICROSECONDS);
  }

  public final Duration getDuration()
  {
    return new Duration(_theBuoyPatternLifetime, Duration.MICROSECONDS);
  }

  public boolean getBuoyLabelVisible()
  {
    return _labelVisible;
  }

  public final void setBuoyLabelVisible(boolean val)
  {
    _labelVisible = val;
  }

  public final WorldLocation getJigPoint()
  {
    return _jigPoint;
  }

  public final void setJigPoint(WorldLocation val)
  {
    _jigPoint = val;
  }

  public final WorldDistance getKingpinRange()
  {
    return new WorldDistance(_kingpinRange, WorldDistance.NM);
  }

  public final void setKingpinRange(WorldDistance val)
  {
    _kingpinRange = val.getValueIn(WorldDistance.NM);
  }

  public final double getKingpinBearing()
  {
    return _kingpinBearing;
  }

  public final void setKingpinBearing(double val)
  {
    _kingpinBearing = val;
  }


  public final Integer getNumberOfBuoys()
  {
    return new Integer(_number);
  }

  public final void setNumberOfBuoys(Integer val)
  {
    _number = val.intValue();
  }


  public final String getName()
  {
    return _name;
  }

  public final String getPatternName()
  {
    return getName();
  }

  public final void setPatternName(String val)
  {
    _name = val;
  }


}