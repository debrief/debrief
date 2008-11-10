// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: PlainSymbol.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: PlainSymbol.java,v $
// Revision 1.2  2004/05/25 15:37:24  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-02-11 08:37:57+00  ian_mayo
// remove unnecessary toda statement
//
// Revision 1.4  2003-01-31 11:32:19+00  ian_mayo
// Improve comments
//
// Revision 1.3  2003-01-30 16:09:26+00  ian_mayo
// New method to return shape as collection of lines
//
// Revision 1.2  2002-05-28 09:25:53+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:21+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:05+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:14+01  administrator
// Initial revision
//
// Revision 1.5  2001-01-26 11:20:29+00  novatech
// only show symbol label if LARGE
//
// Revision 1.4  2001-01-24 11:38:03+00  novatech
// size labels Tiny,Small,Regular,Large changed to Small,Medium,Large
//
// Revision 1.3  2001-01-16 19:27:17+00  novatech
// made into an editable, which returns false to hasEditor
//
// Revision 1.2  2001-01-11 15:28:16+00  novatech
// add showSimplifiedSymbol method
//
// Revision 1.1  2001-01-03 13:42:16+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 21:49:14  ianmayo
// initial version
//
// Revision 1.2  2000-11-17 09:06:54+00  ian_mayo
// store "filled" status at this high level
//
// Revision 1.1  1999-10-12 15:36:37+01  ian_mayo
// Initial revision
//
// Revision 1.1  1999-07-27 10:50:38+01  administrator
// Initial revision
//
// Revision 1.1  1999-07-07 11:10:04+01  administrator
// Initial revision
//
// Revision 1.1  1999-06-16 15:37:57+01  sm11td
// Initial revision
//
// Revision 1.2  1999-02-01 16:08:47+00  sm11td
// creating new sessions & panes, starting import management
//
// Revision 1.1  1999-01-31 13:33:02+00  sm11td
// Initial revision
//

package MWC.GUI.Shapes.Symbols;



import java.awt.*;
import java.util.Collection;

import MWC.GenericData.*;
import MWC.GUI.*;
import MWC.GUI.Shapes.*;

/** base class for our symbols
 *  the class implements the Serializable interface so that it can be copied.
 *  */
abstract public class PlainSymbol implements java.io.Serializable, MWC.GUI.Editable {

  /////////////////////////////////////////////////////////////
  // member variables
  ////////////////////////////////////////////////////////////

  /** relates to the scaling of the symbol (small/medium/large)*/
  private double _theScaleVal;

  /** the colour to draw this symbol
   */
  private java.awt.Color _theCol;

  /** whether the symbol is drawn filled or not (where applicable)
   */
  protected boolean _fillMe;

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////
  public PlainSymbol(){
    _theCol = java.awt.Color.cyan;
    _theScaleVal = 1.0;
    _fillMe = false;
  }

  /////////////////////////////////////////////////////////////
  // editable functions
  ////////////////////////////////////////////////////////////

  /** whether there is any edit information for this item
   * this is a convenience function to save creating the EditorType data
   * first
   * @return yes/no
   */
  public boolean hasEditor(){ return false;};

  /** get the editor for this item
   * @return the BeanInfo data for this editable object
   */
  public Editable.EditorType getInfo(){ return null;};

  /** the name for this symbol
   *  @return this symbol's name
   */
  public String getName()
  {
    return "unnamed";
  }

  /////////////////////////////////////////////////////////////
  // member functions
  ////////////////////////////////////////////////////////////

  public void setFillSymbol(boolean val)
  {
    _fillMe = val;
  }

  public boolean getFillSymbol()
  {
    return _fillMe;
  }

  /** accessor method for child classes to determine if we are at a
   *  scale at which use a simplified symbol
   */
  protected boolean showSimplifiedSymbol()
  {
    if(getScaleVal() == MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.LARGE)
      return false;
    else
      return true;
  }

  /** retrieve the metafile from the implementation*/
  public abstract void getMetafile();

  /** allow recipients to find out what type of symbol this is.
   * @return the name of this symbol
   */
  abstract public String getType();

  /** retrieve the extent of the shape in screen coordinates */
  public abstract java.awt.Dimension getBounds();

  /** step through metafile, drawing in shapes
   * @param dest the Canvas to draw to
   * @param theCentre the WorldLoc to centre this symbol on
    */
  public void paint(CanvasType dest,
                    WorldLocation theCentre){
    dest.setColor(_theCol);

    java.awt.Point centre = dest.toScreen(theCentre);
    dest.drawRect(centre.x -2, centre.y - 2, 4, 4);
  }

  public abstract void paint(CanvasType dest, WorldLocation centre, double direction);

  public double getScaleVal(){
    return _theScaleVal;
  }

  public void setScaleVal(double scaleVal){
    _theScaleVal = scaleVal;
  }

  public Color getColor(){
    return _theCol;
  }

  public void setColor(Color theCol){
    _theCol = theCol;
  }

  /** get this symbol as a sequence of lines.
   * The
   *
   * @return a collection of paths.  Each path is a collection of java.awt.Point objects.
   */
  public Collection getCoordinates()
  {
    Collection res = null;
    return res;
  }

}

