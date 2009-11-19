package Debrief.Wrappers;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: LocationWrapper.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: LocationWrapper.java,v $
// Revision 1.2  2005/12/13 09:04:59  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:49:23  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-03-19 15:36:54+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.3  2002-10-01 15:41:43+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.2  2002-05-28 09:25:13+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:40+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:24+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-08-29 19:17:17+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-08-13 12:51:59+01  administrator
// use the PlainWrapper colour support
//
// Revision 1.0  2001-07-17 08:41:09+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:36:58+00  novatech
// setString has changed to setLabel in label
//
// Revision 1.2  2001-01-09 10:25:44+00  novatech
// set size of new symbol
//
// Revision 1.1  2001-01-03 13:40:23+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:20  ianmayo
// initial import of files
//
// Revision 1.6  2000-11-24 10:53:04+00  ian_mayo
// we don't need to check for SquareSymbol any more
//
// Revision 1.5  2000-08-09 16:04:02+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.4  2000-05-23 13:41:42+01  ian_mayo
// provide method to fill in the symbol
//
// Revision 1.3  2000-02-04 15:51:57+00  ian_mayo
// added method to allow Location to be set after creation
//
// Revision 1.2  1999-11-26 15:51:40+00  ian_mayo
// tidying up
//
// Revision 1.1  1999-10-12 15:34:02+01  ian_mayo
// Initial revision
//
// Revision 1.3  1999-07-27 09:24:19+01  administrator
// added BeanInfo editing
//
// Revision 1.2  1999-07-12 08:09:21+01  administrator
// Property editing added
//
// Revision 1.1  1999-07-07 11:10:13+01  administrator
// Initial revision

import MWC.GUI.CanvasType;
import MWC.GenericData.WorldArea;
import MWC.GenericData.WorldLocation;

public final class LocationWrapper extends MWC.GUI.PlainWrapper {
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  // keep track of versions
  static final long serialVersionUID = 1;

  private final MWC.GUI.Shapes.Symbols.PlainSymbol _theSymbol;
  private WorldLocation _theLocation;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public LocationWrapper(final WorldLocation theLocation){
    // create the symbol for this location
    _theLocation = theLocation;

    // get the symbol for this location
    _theSymbol = MWC.GUI.Shapes.Symbols.SymbolFactory.createSymbol("Square");

    // shrink the location, we want a small symbol, not a medium one
    _theSymbol.setScaleVal(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.SMALL);
  }

  //////////////////////////////////////////////////
  // member functions
  //////////////////////////////////////////////////

  public final void paint(final CanvasType dest){
    // draw the symbol, based on the current location
    _theSymbol.setColor(getColor());

    // draw in our symbol
    _theSymbol.paint(dest, _theLocation);

  }

  public final WorldArea getBounds(){
    // get the bounds from the data object (or its location object)
    return new WorldArea(_theLocation, _theLocation);
  }

  public final String getName(){
    return "a location";
  }

  /** does this item have an editor?
   */
  public final boolean hasEditor(){
    return false;
  }

  public final void setLocation(final WorldLocation val)
  {
    _theLocation = val;
  }

  public final void setFillSymbol(final boolean val)
  {

    _theSymbol.setFillSymbol(val);
  }

  public final void setSymbolScale(final Double val)
  {
    _theSymbol.setScaleVal(val.doubleValue());
  }

  public final Double getSymbolScale()
  {
    return new Double(_theSymbol.getScaleVal());
  }

}