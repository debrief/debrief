// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: FieldBuilder.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: FieldBuilder.java,v $
// Revision 1.2  2005/12/13 09:04:50  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:50  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:25+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:37:08+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:10+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:46+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-23 12:28:44+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-01-17 20:21:29+00  administrator
// Reflect use of WorldDistance and duration objects
//
// Revision 1.0  2001-07-17 08:41:15+01  administrator
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
// Revision 1.3  2001-01-11 15:36:32+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.2  2001-01-08 14:14:29+00  novatech
// better numbering of buoys, improved tooltips
//
// Revision 1.1  2001-01-08 11:40:45+00  novatech
// Initial revision
//

package Debrief.Tools.Palette.BuoyPatterns;

import java.beans.*;

import MWC.GenericData.*;

public final class FieldBuilder extends PatternBuilderType
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  /** field orientation (degs)
   */
  private double _orientation;

  /** spacing of buoys (nm)
   */
  private double _buoySpacing;

  /** spacing of rows (nm)
   */
  private double _rowSpacing;

  /** offset for field (Left, Right, Centre)
   */
  private Integer _fieldOffset;

  /** the number of rows to create
   */
  private int _numRows;

  /** our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor = null;

  /** the enumerated types used for the offset
   */
  final private static Integer LEFT = new Integer(0);
  final private static Integer RIGHT = new Integer(1);
  final private static Integer CENTRE = new Integer(2);

  //////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////
  public FieldBuilder(WorldLocation centre,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layers theData)
  {
    super(centre, thePanel, theData);

    // initialise our variables
    _orientation = 270;
    _buoySpacing = 0.5;
    _rowSpacing = 0.5;
    _fieldOffset = LEFT;
    _numRows = 4;
    setPatternName("blank field");

    // and the variables in our parent
    setKingpinBearing(0.0);
    setKingpinRange(new WorldDistance(0.0, WorldDistance.NM));
    setNumberOfBuoys(new Integer(20));

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
    double row_spacing_degs = MWC.Algorithms.Conversions.Nm2Degs(_rowSpacing);
    double buoy_spacing_degs = MWC.Algorithms.Conversions.Nm2Degs(_buoySpacing);
    WorldVector along_step = new MWC.GenericData.WorldVector(orient_rads, buoy_spacing_degs, 0);
    WorldVector reverse_along_step= new MWC.GenericData.WorldVector(
        orient_rads + MWC.Algorithms.Conversions.Degs2Rads(180.0), buoy_spacing_degs, 0);
    WorldVector across_step = new MWC.GenericData.WorldVector(
          orient_rads + MWC.Algorithms.Conversions.Degs2Rads(90.0), row_spacing_degs, 0);


    // sort out in which direction we perform our offsets
    double normal_row = 0.0;
    double back_row = 0.0;
    if(_fieldOffset.equals(LEFT))
    {
      normal_row = orient_rads;
      back_row = orient_rads + MWC.Algorithms.Conversions.Degs2Rads(180.0);
    }
    else if(_fieldOffset.equals(RIGHT))
    {
      normal_row = orient_rads + MWC.Algorithms.Conversions.Degs2Rads(180.0);
      back_row = orient_rads;
    }

    //
    double half_step = buoy_spacing_degs / 2.0;
    WorldVector normal_offset = new MWC.GenericData.WorldVector(normal_row, half_step, 0.0);
    WorldVector back_offset = new MWC.GenericData.WorldVector(back_row, half_step, 0.0);

    // how many buoys in total?
    int num_buoys = getNumberOfBuoys().intValue();

    // how many buoys in each row?
    int buoys_per_row = num_buoys / _numRows;

    // are we in offset row?
    boolean in_offset_row = false;

    // set the current direction
    WorldVector current_direction = along_step;

    // remember if this is the first fix, so that we can set it to kingpin
    boolean first_buoy = true;

    for(int i =0;i<num_buoys;i++)
    {
      // create the new symbol
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("F" + (i + 1),
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

      // are we at the end of this row?
      if((i % buoys_per_row) == buoys_per_row -1)
      {
        // move across a row
        lastPoint = lastPoint.add(across_step);

        // are we already in an offset row?
        if(in_offset_row)
        {
          // back into normal row
          current_direction = along_step;

          // and shift this row across a bit
          lastPoint = lastPoint.add(back_offset);

        }
        else
        {
          // travel back up row
          current_direction = reverse_along_step;

          // and shift this row across a bit
          lastPoint = lastPoint.add(normal_offset);

        }

        // switch the row
        in_offset_row = !in_offset_row;
      }
      else
      {
        // no, just carry on along the row

        // move the location forward through the vector
        lastPoint = lastPoint.add(current_direction);
      }
    }

  }

  //////////////////////////////////////////
  // editable accessor functions
  //////////////////////////////////////////


  public final double getPatternOrientation()
  {
    return _orientation;
  }

  public final void setPatternOrientation(double val)
  {
    _orientation = val;
  }

  public final WorldDistance getPatternRowSpacing()
  {
    return new WorldDistance( _rowSpacing, WorldDistance.NM);
  }

  public final void setPatternRowSpacing(WorldDistance val)
  {
    _rowSpacing = val.getValueIn(WorldDistance.NM);
  }

  public final WorldDistance getPatternBuoySpacing()
  {
    return new WorldDistance(_buoySpacing, WorldDistance.NM);
  }

  public final void setPatternBuoySpacing(WorldDistance val)
  {
    _buoySpacing = val.getValueIn(WorldDistance.NM);
  }

  public final Integer getFieldDirection()
  {
    return _fieldOffset;
  }

  public final void setFieldDirection(Integer val)
  {
    _fieldOffset = val;
  }

  public final Integer getNumberOfRows()
  {
    return new Integer(_numRows);
  }

  public final void setNumberOfRows(Integer val)
  {
    _numRows = val.intValue();
  }

  public final Integer getPatternOffsetDirection()
  {
    return _fieldOffset;
  }

  public final void setPatternOffsetDirection(Integer val)
  {
    _fieldOffset = val;
  }



  /** get the editor for this item
 * @return the BeanInfo data for this editable object
 */
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new FieldInfo(this, this.getName());

    return _myEditor;
  }

  public final String toString()
  {
    return "Field Builder";
  }

  //////////////////////////////////////////
  // editable details
  //////////////////////////////////////////

  public final class FieldInfo extends MWC.GUI.Editable.EditorType
  {

    public FieldInfo(FieldBuilder data,
                   String theName)
    {
      super(data, theName, "Field:");
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
          prop("PatternOffsetDirection", "the direction of the offset for successive rows"),
          prop("Duration", "the lifetime of the buoy pattern"),
          prop("PatternName", "the name of this pattern"),
          prop("PatternBuoySpacing", "the spacing of the buoys along each row "),
          prop("PatternRowSpacing", "the spacing between each row of buoys "),
          prop("NumberOfRows", "the number of rows in this pattern"),
          prop("PatternOrientation", "the orientation of the pattern (degs)"),
          prop("KingpinRange", "the range of the kingpin from the jig point"),
          prop("KingpinBearing", "the bearing of the kingpin from the jig point (degs)"),
          prop("JigPoint", "the jig point for the construction of this pattern"),
          prop("NumberOfBuoys", "the number of buoys in this pattern"),
          prop("Color", "the default colour for this pattern"),
          prop("DateTimeGroup", "the DTG this pattern starts (DD/MM/YY)"),
          prop("BuoyLabelVisible", "whether the buoy labels are visible")
        };
        myRes[0].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
        myRes[1].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);
        myRes[2].setPropertyEditorClass(OffsetPropertyEditor.class);

        return myRes;

      }catch(IntrospectionException e)
      {
        // find out which property fell over
        MWC.Utilities.Errors.Trace.trace(e, "Creating editor for field Builder");

        return super.getPropertyDescriptors();
      }
    }
  }

  //////////////////////////////////
  // custom editor class to all selection of Left/Right/Centre offset from drop-down list
  //////////////////////////////////

  public static final class OffsetPropertyEditor extends PropertyEditorSupport
  {

    Integer _myLocation;

    public final String[] getTags()
    {
      String tags[] = {"Left",
                       "Right",
                       "Centre"};
      return tags;
    }

    public final Object getValue()
    {
      return _myLocation;
    }



    public final void setValue(Object p1)
    {
      if(p1 instanceof Integer)
      {
        _myLocation = (Integer)p1;
      }
      if(p1 instanceof String)
      {
        String val = (String) p1;
        setAsText(val);
      }
    }

    public final void setAsText(String val)
    {

      if(val.equals("Left"))
        _myLocation = LEFT;
      if(val.equals("Right"))
        _myLocation = RIGHT;
      if(val.equals("Centre"))
        _myLocation = CENTRE;

    }

    public final String getAsText()
    {
      String res = null;
      if(_myLocation == LEFT)
        res = "Left";
      else if(_myLocation == RIGHT)
        res = "Right";
      else if(_myLocation == CENTRE)
        res = "Centre";
      return res;
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
      MWC.GUI.Editable ed = new FieldBuilder(null,null,null);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}