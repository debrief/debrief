 package Debrief.Wrappers;

// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ContactWrapper.java,v $
// @author $Author: ian.mayo $
// @version $Revision: 1.4 $
// $Log: ContactWrapper.java,v $
// Revision 1.4  2007/03/12 11:40:24  ian.mayo
// Change default font size to 9px
//
// Revision 1.3  2005/12/13 09:04:57  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.2  2004/11/25 10:24:44  Ian.Mayo
// Switch to Hi Res dates
//
// Revision 1.1.1.2  2003/07/21 14:49:20  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.6  2003-07-04 10:59:29+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.5  2003-03-19 15:36:55+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.4  2002-10-30 16:27:27+00  ian_mayo
// tidy up (shorten) display names of editables
//
// Revision 1.3  2002-10-01 15:41:47+01  ian_mayo
// make final methods & classes final (following IDEA analysis)
//
// Revision 1.2  2002-05-28 09:25:12+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:20+01  ian_mayo
// Initial revision
//
// Revision 1.2  2001-08-29 19:17:41+01  administrator
// Reflect package change of PlainWrapper
//
// Revision 1.1  2001-08-13 12:52:06+01  administrator
// use the PlainWrapper colour support
//
// Revision 1.0  2001-07-17 08:41:08+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-24 11:35:53+00  novatech
// recognise optimised toScreen handling which reduces object creation
//
// Revision 1.2  2001-01-22 12:30:02+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-03 13:40:22+00  novatech
// Initial revision
//
// Revision 1.1.1.1  2000/12/12 20:49:08  ianmayo
// initial import of files
//
// Revision 1.11  2000-11-24 10:54:36+00  ian_mayo
// tidying up
//
// Revision 1.10  2000-09-21 09:05:22+01  ian_mayo
// make Editable.EditorType a transient parameter, to save it being written to file
//
// Revision 1.9  2000-08-18 13:34:04+01  ian_mayo
// Editable.EditorType
//
// Revision 1.8  2000-08-11 08:41:04+01  ian_mayo
// tidy beaninfo
//
// Revision 1.7  2000-08-09 16:04:00+01  ian_mayo
// remove stray semi-colons
//
// Revision 1.6  2000-04-05 08:39:46+01  ian_mayo
// remove getVisible method (since it's present in PlainWrapper
//
// Revision 1.5  2000-03-07 14:48:17+00  ian_mayo
// optimised algorithms
//
// Revision 1.4  2000-02-22 13:48:32+00  ian_mayo
// exportShape name changed to exportThis
//
// Revision 1.3  2000-01-20 10:09:05+00  ian_mayo
// changed plotting code
//
// Revision 1.2  2000-01-13 15:31:52+00  ian_mayo
// improved plotting, & editing
//
// Revision 1.1  2000-01-12 15:40:31+00  ian_mayo
// Initial revision
//

import MWC.GenericData.*;
import MWC.TacticalData.*;
import MWC.GUI.*;
import MWC.Utilities.TextFormatting.DebriefFormatDateTime;

import java.io.*;
import java.beans.*;
import java.awt.*;

/** The fix wrapper has the responsibility for the GUI
 * and data aspects of the fix, tying the two together.
*/
public final class ContactWrapper extends MWC.GUI.PlainWrapper implements Serializable,
      DynamicPlottable{

  ////////////////////////////////////////
  // member variables
  ////////////////////////////////////////

  // keep track of versions
  static final long serialVersionUID = 1;

  /** the (nearest) fix where we were recorded
   */
  private FixWrapper _theFix;

  /** the contact we are storing
   */
  private final Contact _theContact;

  /** the label describing this fix
   */
  private final MWC.GUI.Shapes.TextLabel _theLabel;

  /** flag for whether to show the label
   */
  private Boolean _showLabel;


  /** the font to draw this track in.
   */
  private Font _theFont;

  /** my editor details
   */
  transient private Editable.EditorType _myEditor;

  ////////////////////////////////////////
  // constructors
  ////////////////////////////////////////

  public ContactWrapper(final Contact theContact,
                        final FixWrapper theFix)
  {
    // store the fix
    _theFix = theFix;

    // store the contact data
    _theContact = theContact;

    // reset (recalculate) the name bit
    resetName();

    WorldLocation centre = null;

    // check if we have a fix - if we have,
    if(_theFix != null)
    {
      // find the centre point of the fix
      final WorldArea ar = new WorldArea(getStart(),
                                   getEnd());
      centre = ar.getCentre();
    }

    // create the text label
    _theLabel = new MWC.GUI.Shapes.TextLabel(centre,
                                             theContact.getString());


    // hide the name, by default
    _showLabel = new Boolean(false);

    // clear any colors
    setColor(null);
    // start us off with a nice font
    _theFont = new Font("Sans Serif", Font.PLAIN, 9);
  }

  ////////////////////////////////////////
  // member functions
  ////////////////////////////////////////


  public final Contact getContact()
  {
    return _theContact;
  }

  public final void setFixWrapper(final FixWrapper theFix){
    _theFix = theFix;
  }

  public final FixWrapper getFixWrapper()
  {
    return _theFix;
  }

  public final void resetColor(){
    setColor(null);
  }

  public final Color getColor(){
    if(super.getColor() == null){
      return _theLabel.getColor();
    }
    else
      return super.getColor();
  }

  public final WorldLocation getStart()
  {
    // produce the true direction of the sensor datum
    double relDir = _theContact.getOffset().getBearing();
    final double relDist = _theContact.getOffset().getRange();
    final double thisCourse = _theFix.getCourse();
    relDir = thisCourse + relDir;

    // get the screen loc of the fix
    final WorldLocation start = _theFix.getLocation().add(new WorldVector(relDir,
                                                                    relDist,
                                                                    0));
    return start;
  }

  public final WorldLocation getEnd()
  {
    // add the vector to the start
    final WorldLocation end = getStart().add(new WorldVector(_theContact.getBearing(),
                                                  _theContact.getRange(), 0));
    return end;
  }

  public final void paint(final CanvasType dest)
  {
    if(_theFix == null)
      return;

    // get the screen loc of the fix
    final WorldLocation start = getStart();
    final WorldLocation end = getEnd();

    ////////////////////////
    // do the line first
    ////////////////////////

    // set the colour (either as our colour, or that of our parent)
    dest.setColor(getColor());

    // convert to screen coordinates
    final java.awt.Point startP = new Point(dest.toScreen(start));
    final java.awt.Point endP = new Point(dest.toScreen(end));

    // and draw the line
    dest.drawLine(startP.x, startP.y, endP.x, endP.y);

    ////////////////////////
    // now the label
    ////////////////////////

    // check the label has it's fix
    if(_theLabel.getLocation() == null)
    {
      final WorldArea ar = new WorldArea(start, end);
      _theLabel.setLocation(ar.getCentre());
    }

    // draw the label
    if(_showLabel.booleanValue())
      _theLabel.paint(dest);


  }

  public final Font getFont(){
    return _theFont;
  }

  public final void setLabelLocation(final Integer loc)
  {
    _theLabel.setRelativeLocation(loc);
  }

  public final Integer getLabelLocation()
  {
    return _theLabel.getRelativeLocation();
  }


  public final void setFont(final Font theFont){
    _theFont = theFont;
  }

  public final WorldArea getBounds(){
    // get the bounds from the data object (or its location object)
   // return new WorldArea(_theFix.getLocation(), _theFix.getLocation());
    return null;
  }

  private static void resetName(){
  }

  public final String toString()
  {
    return getName();
  }

  public final String getName(){
    return "Contact:" + DebriefFormatDateTime.toStringHiRes(_theFix.getTime())
           + " " + MWC.Algorithms.Conversions.Rads2Degs(_theContact.getBearing());
  }

  public final void setName(final String val){
    _theLabel.setString(val);
  }

  public final boolean isShowing()
  {
   // if(_showLabel == null)
   //   return _trackWrapper.getShowTimeLabel();
   // else
      return _showLabel.booleanValue();
  }

  public final void setShowing(final boolean val){
    _showLabel = new Boolean(val);
  }

  /** get the editing information for this type
   */
  public final Editable.EditorType getInfo(){
    if(_myEditor == null)
      _myEditor = new contactInfo(this, this.getName());

    return _myEditor;
  }

  public final boolean hasEditor(){ return true; }

  /** how far away are we from this point?
   * or return null if it can't be calculated
   */
  public final double rangeFrom(final WorldLocation other)
  {
  	// calculate the perpendicular distance from this line
  	WorldDistance theDist = other.rangeFrom(getStart(), getEnd());
  	
  	// and convert back to degrees
  	return theDist.getValueIn(WorldDistance.DEGS);
  }


  public final String getTrackName()
  {
    return _theContact.getTrackName();
  }

  /** return the depth (in metres)
   */
 /* public double getDepth()
  {
    return _theFix.getLocation().getDepth();
  }
  */

  /** return the time of the fix (as long)
   */
  private HiResDate getTime()
  {
    return _theContact.getTime();
  }

  public final boolean visibleBetween(final HiResDate start, final HiResDate end)
  {
    return ((this.getTime().greaterThan(start)) && (this.getTime().lessThan(end)));
  }


  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public final class contactInfo extends Editable.EditorType
  {

    public contactInfo(final ContactWrapper data,
                   final String theName)
    {
      super(data, theName, "");
    }

    public final String getName()
    {
      return ContactWrapper.this.getName();
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try{
        final PropertyDescriptor[] res={
          prop("Color", "the fix color"),
          prop("Name", "the fix color"),
          prop("Font", "the label font"),
          prop("Showing", "whether the label is showing"),
          longProp("LabelLocation",
                   "the label location",
                   MWC.GUI.Properties.LocationPropertyEditor.class)
        };

        return res;
      }catch(IntrospectionException e){
        return super.getPropertyDescriptors();
      }
    }

    public final MethodDescriptor[] getMethodDescriptors()
    {
      // just add the reset color field first
      final Class c = ContactWrapper.class;
      final MethodDescriptor[] mds = {
        method(c, "exportThis", null, "Export Shape")
      };
      return mds;
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE  = "UNIT";
    public testMe(final String val)
    {
      super(val);
    }
    public final void testMyParams()
    {
      final Contact ct = new Contact("name", new HiResDate(0), new WorldVector(12,12,12), 12d, 12d, null, "msg");
      final Fix fx = new Fix(new HiResDate(12, 0), new WorldLocation(12d,12d,12d), 12d, 12d);
      final FixWrapper fw = new FixWrapper(fx);
      MWC.GUI.Editable ed = new ContactWrapper(ct, fw);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}
