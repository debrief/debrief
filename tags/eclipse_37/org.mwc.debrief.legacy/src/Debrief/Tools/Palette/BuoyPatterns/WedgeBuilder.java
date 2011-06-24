// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: WedgeBuilder.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: WedgeBuilder.java,v $
// Revision 1.2  2005/12/13 09:04:51  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:52  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:17+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:37:12+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:45+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:48+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-17 20:21:29+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.0  2001-07-17 08:41:15+01  administrator
// Initial revision
//
// Revision 1.3  2001-01-22 12:31:04+00  novatech
// correct orientation tooltips
//
// Revision 1.2  2001-01-18 13:16:39+00  novatech
// make first buoy a kingpin
//
// Revision 1.1  2001-01-17 13:21:01+00  novatech
// Initial revision
//
// Revision 1.5  2001-01-11 15:35:51+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.4  2001-01-11 11:52:35+00  novatech
// change name of orientations
//
// Revision 1.3  2001-01-09 13:58:52+00  novatech
// correct use of theta 2 in Wedge
//
// Revision 1.2  2001-01-08 14:14:29+00  novatech
// better numbering of buoys, improved tooltips
//
// Revision 1.1  2001-01-08 11:40:44+00  novatech
// Initial revision
//

package Debrief.Tools.Palette.BuoyPatterns;

import java.beans.*;

import MWC.GenericData.*;

public final class WedgeBuilder extends PatternBuilderType
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  /** first orientation (degs)
   */
  private double _orientation2;

  /** second orientation (degs)
   */
  private double _orientation1;

  /** spacing of buoys (nm)
   */
  private double _spacing;


  /** our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor = null;

  //////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////
  public WedgeBuilder(WorldLocation centre,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layers theData)
  {
    super(centre, thePanel, theData);

    // initialise our variables
    _orientation2 = 45;
    _orientation1 = 315;
    _spacing = 0.5;
    setPatternName("blank wedge");

    // and the variables in our parent
    setKingpinBearing(0.0);
    setKingpinRange(new WorldDistance(0.0, WorldDistance.NM));
    setNumberOfBuoys(new Integer(7));

  }

  //////////////////////////////////////////
  // Member functions
  //////////////////////////////////////////

  /** this method is called by the 'Create' function, and it fills in the
   *  buoys into the correct pattern
   */
  protected final void addBuoys(Debrief.Wrappers.BuoyPatternWrapper pattern)
  {
    WorldLocation origin = getKingpin();
    // note that as we calculate the LH angle
    double lh_orient_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation2);
    double rh_orient_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation1);
    double spacing_degs = MWC.Algorithms.Conversions.Nm2Degs(_spacing);


    // how many bouys in each leg?
    // an even number means we don't have one at the tip, which becomes a special case
    int num_buoys = getNumberOfBuoys().intValue();
    boolean even_num = false;
    if((num_buoys % 2) == 0)
    {
      even_num = true;
    }

    // sort out how many there are in each leg
    int num_in_leg = num_buoys / 2;

    // sort out the direction
    double this_orient = rh_orient_rads;

    int buoy_counter = 0;

    // remember that we are looking at the first buoy
    boolean first_buoy = true;

    // do the first leg
    for(int i = 0;i< num_in_leg ;i++)
    {
      // create the new symbol
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("W" + (buoy_counter + 1),
                                                  origin,
                                                  java.awt.Color.red);

      buoy_counter++;

      // get the parent to do the formatting
      this.formatSymbol(lw, pattern);

      // if this is the first buoy, mark it as the kingping
      if(first_buoy)
      {
        lw.setSymbolType("Kingpin");
        first_buoy = false;
      }


      // create the step to use to get to the next buoy
      WorldVector thisStep = new MWC.GenericData.WorldVector(this_orient, spacing_degs, 0.0);

      // place buoy
      origin = origin.add(thisStep);
    }

    // if we have an even number, we need to move forward 1/2 distance for one more step before we change direction
    if(even_num)
    {
      // calculate the size of this small step
      double reverse_rh_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation1 + 180.0);
      WorldVector short_hop = new WorldVector(reverse_rh_rads, spacing_degs / 2, 0.0);

      // move the origin forward
      origin = origin.add(short_hop);

      // calculate the size of this small step
      short_hop = new WorldVector(lh_orient_rads, spacing_degs / 2, 0.0);

      // move the origin forward
      origin = origin.add(short_hop);
    }
    else
    {

      // drop a buoy at the current point
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("W" + (buoy_counter + 1),
                                                  origin,
                                                  java.awt.Color.red);
      buoy_counter++;

      // move to the correct location for the next point
      WorldVector short_hop = new WorldVector(lh_orient_rads, spacing_degs, 0.0);

      // move the origin forward
      origin = origin.add(short_hop);


      // get the parent to do the formatting
      this.formatSymbol(lw, pattern);

    }

    // now travel back down the reverse side
    // sort out the direction
    this_orient = lh_orient_rads;

    // do the first leg
    for(int i =0;i< num_in_leg ;i++)
    {

      // create the new symbol
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("W" + (buoy_counter + 1),
                                                  origin,
                                                  java.awt.Color.red);

      buoy_counter++;

      // get the parent to do the formatting
      this.formatSymbol(lw, pattern);

      // create the step to use to get to the next buoy
      WorldVector thisStep = new MWC.GenericData.WorldVector(this_orient, spacing_degs, 0.0);

      // start moving down the return leg
      // move buoy
      origin = origin.add(thisStep);


    }

  }

  //////////////////////////////////////////
  // editable accessor functions
  //////////////////////////////////////////


  public final double getOrientation2()
  {
    return _orientation2;
  }

  public final void setOrientation2(double val)
  {
    _orientation2 = val;
  }

  public final double getOrientation1()
  {
    return _orientation1;
  }

  public final void setOrientation1(double val)
  {
    _orientation1 = val;
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
      _myEditor = new WedgeInfo(this, this.getName());

    return _myEditor;
  }

  public final String toString()
  {
    return "Wedge Builder";
  }

  //////////////////////////////////////////
  // editable details
  //////////////////////////////////////////

  public final class WedgeInfo extends MWC.GUI.Editable.EditorType
  {

    public WedgeInfo(WedgeBuilder data,
                   String theName)
    {
      super(data, theName, "Wedge:");
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
          prop("PatternBuoySpacing", "the spacing of the buoys in the wedge"),
          prop("Orientation2", "the orientation of the second side of the wedge from kingpin (degs)"),
          prop("Orientation1", "the orientation of the first side of the wedge from kingpin (degs)"),
          prop("KingpinRange", "the range of the kingpin from the jig point"),
          prop("KingpinBearing", "the bearing of the kingpin from the jig point (degs)"),
          prop("JigPoint", "the jig point for the construction of this wedge"),
          prop("NumberOfBuoys", "the number of buoys in this wedge"),
          prop("Color", "the default colour for this wedge"),
          prop("DateTimeGroup", "the DTG this pattern starts (DD/MM/YY)"),
          prop("BuoyLabelVisible", "whether the buoy labels are visible")
        };
        myRes[0].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
        myRes[1].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);

        return myRes;

      }catch(IntrospectionException e)
      {
        // find out which property fell over
        MWC.Utilities.Errors.Trace.trace(e, "Creating editor for Wedge Builder");

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
      MWC.GUI.Editable ed = new WedgeBuilder(null,null,null);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }

}