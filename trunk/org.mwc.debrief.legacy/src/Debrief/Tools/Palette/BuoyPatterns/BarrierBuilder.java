// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: BarrierBuilder.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: BarrierBuilder.java,v $
// Revision 1.2  2005/12/13 09:04:49  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:47  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:26+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:37:11+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:47+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:41+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-17 20:21:30+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.0  2001-07-17 08:41:14+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 12:30:03+00  novatech
// added JUnit testing code
//
// Revision 1.2  2001-01-18 13:16:38+00  novatech
// make first buoy a kingpin
//
// Revision 1.1  2001-01-17 13:21:02+00  novatech
// Initial revision
//
// Revision 1.8  2001-01-11 15:36:12+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.7  2001-01-09 13:59:27+00  novatech
// correct buoy naming
//
// Revision 1.6  2001-01-08 14:14:30+00  novatech
// better numbering of buoys, improved tooltips
//
// Revision 1.5  2001-01-08 11:40:00+00  novatech
// factor out common code
//
// Revision 1.4  2001-01-05 09:15:32+00  novatech
// factor out some of the processing into the parent class
//
// Revision 1.3  2001-01-04 16:34:45+00  novatech
// extended tooltip information
//
// Revision 1.2  2001-01-04 14:02:43+00  novatech
// further implementation, tidying
//
// Revision 1.1  2001-01-03 16:02:38+00  novatech
// Initial revision
//
package Debrief.Tools.Palette.BuoyPatterns;

import java.beans.*;

import MWC.GenericData.*;

public final class BarrierBuilder extends PatternBuilderType
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  /** spacing of this barrier (nm)
   */
  private double _spacing;

  /** orientation of this barrier (degs)
   */
  private double _orientation;


  /** our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor = null;




  //////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////
  public BarrierBuilder(WorldLocation centre,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layers theData)
  {
    super(centre, thePanel, theData);

    // initialise our variables
    _spacing = 5.0;
    _orientation = 90.0;
    setPatternName("blank barrier");

    // and the variables in our parent
    setKingpinBearing(0.0);
    setKingpinRange(new WorldDistance(0.0, WorldDistance.DEGS));
    setNumberOfBuoys(new Integer(5));

  }

  //////////////////////////////////////////
  // Member functions
  //////////////////////////////////////////

  /** this method is called by the 'Create' function, and it fills in the
   *  buoys into the correct pattern
   */
  protected final void addBuoys(Debrief.Wrappers.BuoyPatternWrapper pattern)
  {
    WorldLocation lastPoint = getKingpin();
    double orient_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation);
    double spacing_degs = MWC.Algorithms.Conversions.Nm2Degs(_spacing);
    WorldVector step = new MWC.GenericData.WorldVector(orient_rads, spacing_degs, 0);

    boolean first_buoy = true;

    for(int i =0;i<getNumberOfBuoys().intValue();i++)
    {
      // create the new symbol
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("B" + (i + 1),
                                                  lastPoint,
                                                  java.awt.Color.red);

      // get the parent to do the formatting
      this.formatSymbol(lw, pattern);

      // if this is the first buoy, mark it as the kingping
      if(first_buoy)
      {
        lw.setSymbolType("Kingpin");
        first_buoy = false;
      }



      // move the location forward through the vector
      lastPoint = lastPoint.add(step);
    }
  }

  //////////////////////////////////////////
  // editable accessor functions
  //////////////////////////////////////////


  public final double getOrientation()
  {
    return _orientation;
  }

  public final void setOrientation(double val)
  {
    _orientation = val;
  }

  public final WorldDistance getPatternBuoySpacing()
  {
    return new WorldDistance(_spacing, WorldDistance.NM);
  }

  public final void setPatternBuoySpacing(WorldDistance val)
  {
    _spacing = val.getValueIn(WorldDistance.NM);
  }




  /** get the editor for this item
 * @return the BeanInfo data for this editable object
 */
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new BarrierInfo(this, this.getName());

    return _myEditor;
  }

  public final String toString()
  {
    return "Barrier Builder";
  }

  //////////////////////////////////////////
  // editable details
  //////////////////////////////////////////

  public final class BarrierInfo extends MWC.GUI.Editable.EditorType
  {

    public BarrierInfo(BarrierBuilder data,
                   String theName)
    {
      super(data, theName, "Barrier:");
    }

    /** method which gets called when all parameters have
     *  been updated
     */
    public final void updatesComplete()
    {
      // get the builder to build itself
      Create();

      // inform the parent
      super.updatesComplete();
    }

    public final PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        PropertyDescriptor[] myRes=
        {
          prop("SymbolType", "the type of symbol plotted for this label"),
          prop("SymbolSize", "the scale of the symbol"),
          prop("Duration", "the lifetime of the buoy pattern"),
          prop("PatternName", "the name of this barrier"),
          prop("Orientation", "the orientation of this barrier (degs)"),
          prop("PatternBuoySpacing", "the spacing of this barrier"),
          prop("KingpinRange", "the range of the kingpin from the jig point"),
          prop("KingpinBearing", "the bearing of the kingpin from the jig point (degs)"),
          prop("JigPoint", "the jig point for the construction of this barrier"),
          prop("NumberOfBuoys", "the number of buoys in this barrier"),
          prop("Color", "the default colour for this barier"),
          prop("DateTimeGroup", "the DTG this pattern starts (DD/MM/YY)"),
          prop("BuoyLabelVisible", "whether the buoy labels are visible")
        };
        myRes[0].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
        myRes[1].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);


        return myRes;

      }catch(IntrospectionException e)
      {
        // find out which property fell over
        MWC.Utilities.Errors.Trace.trace(e, "Creating editor for Barrier Builder");

        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public final class testMe extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE  = "UNIT";
    public testMe(String val)
    {
      super(val);
    }
    public final void testMyParams()
    {
      MWC.GUI.Editable ed = new BarrierBuilder(null,null,null);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }

}