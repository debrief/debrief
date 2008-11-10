// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: ArcBuilder.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: ArcBuilder.java,v $
// Revision 1.2  2005/12/13 09:04:48  Ian.Mayo
// Tidying - as recommended by Eclipse
//
// Revision 1.1.1.2  2003/07/21 14:48:47  Ian.Mayo
// Re-import Java files to keep correct line spacing
//
// Revision 1.4  2003-07-04 10:59:27+01  ian_mayo
// reflect name change in parent testing class
//
// Revision 1.3  2003-03-19 15:37:11+00  ian_mayo
// improvements according to IntelliJ inspector
//
// Revision 1.2  2002-05-28 09:25:09+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:11:48+01  ian_mayo
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
// Revision 1.2  2001-01-22 12:30:03+00  novatech
// added JUnit testing code
//
// Revision 1.1  2001-01-17 13:21:03+00  novatech
// Initial revision
//
// Revision 1.4  2001-01-11 15:36:06+00  novatech
// time details have been passed to BuoyField
//
// Revision 1.3  2001-01-09 13:59:16+00  novatech
// add "direction" parameter which specifies clockwise/anti-clockwise
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

public final class ArcBuilder extends PatternBuilderType
{

  //////////////////////////////////////////
  // Member variables
  //////////////////////////////////////////

  /** radius of this barrier (nm)
   */
  private double _radius;

  /** orientation of this barrier (degs)
   */
  private double _orientation;

  /** size of arc either size Degs)
   */
  private double _arcs;

  /** direction to lay circle (yes/no)
   */
  private boolean _clockwise = true;


  /** our editor
   */
  transient private MWC.GUI.Editable.EditorType _myEditor = null;

  //////////////////////////////////////////
  // Constructor
  //////////////////////////////////////////
  public ArcBuilder(WorldLocation centre,
                        MWC.GUI.Properties.PropertiesPanel thePanel,
                        MWC.GUI.Layers theData)
  {
    super(centre, thePanel, theData);

    // initialise our variables
    _radius = 5.0;
    _orientation = 0.0;
    _arcs = 45.0;
    setPatternName("blank arc");

    // and the variables in our parent
    setKingpinBearing(0.0);
    setKingpinRange(new WorldDistance(0.0, WorldDistance.DEGS));
    setNumberOfBuoys(new Integer(12));

  }

  //////////////////////////////////////////
  // Member functions
  //////////////////////////////////////////

  /** this method is called by the 'Create' function, and it fills in the
   *  buoys into the correct pattern
   */
  protected final void addBuoys(Debrief.Wrappers.BuoyPatternWrapper pattern)
  {
    WorldLocation centre = getKingpin();
    double orient_rads = MWC.Algorithms.Conversions.Degs2Rads(_orientation);
    double radius_degs = MWC.Algorithms.Conversions.Nm2Degs(_radius);
    double arcs_rads = MWC.Algorithms.Conversions.Degs2Rads(_arcs);

    // find out the angle between each buoy
    int numArcs = getNumberOfBuoys().intValue();

    // note that we decrement the number of arcs by one,
    // since we do not want to show a sector "after" the last one
    double theta = 2 * _arcs / (double)(numArcs - 1);
    theta = MWC.Algorithms.Conversions.Degs2Rads(theta);

    double currentAngle = 0.0;

    if(_clockwise)
    {
      // we're travelling clockwise, so start to the left of the orientation
      currentAngle = orient_rads - arcs_rads;
    }
    else
    {
      // we're anti-clockwise, so we start to the right of the orientation and travel left
      currentAngle = orient_rads + arcs_rads;
      theta = -1.0 * theta;
    }

    for(int i =0;i<numArcs;i++)
    {

      // create the location for this buoy, starting with the correct orientation
      WorldVector thisStep = new MWC.GenericData.WorldVector(currentAngle, radius_degs, 0.0);
      WorldLocation thisLoc = centre.add(thisStep);

      // create the new symbol
      Debrief.Wrappers.LabelWrapper lw = new Debrief.Wrappers.LabelWrapper("A" + (i + 1),
                                                  thisLoc,
                                                  java.awt.Color.red);

      // get the parent to do the formatting
      this.formatSymbol(lw, pattern);

      currentAngle += theta;
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

  public final WorldDistance getPatternRadius()
  {
    return new WorldDistance(_radius, WorldDistance.NM);
  }

  public final void setPatternRadius(WorldDistance val)
  {
    _radius = val.getValueIn(WorldDistance.NM);
  }

  public final double getPatternArcs()
  {
    return _arcs;
  }

  public final void setPatternArcs(double val)
  {
    _arcs = val;
  }


  public final boolean getPatternClockwise()
  {
    return _clockwise;
  }

  public final void setPatternClockwise(boolean val)
  {
    _clockwise = val;
  }


  /** get the editor for this item
 * @return the BeanInfo data for this editable object
 */
  public final MWC.GUI.Editable.EditorType getInfo()
  {
    if(_myEditor == null)
      _myEditor = new ArcInfo(this, this.getName());

    return _myEditor;
  }

  public final String toString()
  {
    return "Arc Builder";
  }

  //////////////////////////////////////////
  // editable details
  //////////////////////////////////////////

  public final class ArcInfo extends MWC.GUI.Editable.EditorType
  {

    public ArcInfo(ArcBuilder data,
                   String theName)
    {
      super(data, theName, "Arc:");
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
          prop("PatternName", "the name of this arc"),
          prop("PatternOrientation", "the orientation of the centre point of this arc (degs)"),
          prop("PatternRadius", "the radius of this arc"),
          prop("PatternArcs", "the size of the arcs either size of the orientation (degs)"),
          prop("KingpinRange", "the range of the kingpin from the jig point"),
          prop("KingpinBearing", "the bearing of the kingpin from the jig point (degs)"),
          prop("JigPoint", "the jig point for the construction of this arc"),
          prop("NumberOfBuoys", "the number of buoys in this arc"),
          prop("Color", "the default colour for this arc"),
          prop("DateTimeGroup", "the DTG this pattern starts (DD/MM/YY)"),
          prop("BuoyLabelVisible", "whether the buoy labels are visible"),
          prop("PatternClockwise", "whether the buoys are laid clockwise")
        };
        myRes[0].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolFactoryPropertyEditor.SymbolFactoryBuoyPropertyEditor.class);
        myRes[1].setPropertyEditorClass(MWC.GUI.Shapes.Symbols.SymbolScalePropertyEditor.class);

        return myRes;

      }catch(IntrospectionException e)
      {
        // find out which property fell over
        MWC.Utilities.Errors.Trace.trace(e, "Creating editor for Arc Builder");
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
      MWC.GUI.Editable ed = new ArcBuilder(null,null,null);
      MWC.GUI.Editable.editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }

}