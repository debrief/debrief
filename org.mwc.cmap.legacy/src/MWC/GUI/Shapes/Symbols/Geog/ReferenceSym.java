/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
// $RCSfile: ReferenceSym.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.3 $
// $Log: ReferenceSym.java,v $
// Revision 1.3  2004/08/31 09:38:11  Ian.Mayo
// Rename inner static tests to match signature **Test to make automated testing more consistent
//
// Revision 1.2  2004/05/25 15:37:49  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:36  Ian.Mayo
// Initial import
//
// Revision 1.5  2003-07-04 11:00:58+01  ian_mayo
// Reflect name change of parent editor test
//
// Revision 1.4  2003-02-07 09:49:20+00  ian_mayo
// rationalise unnecessary to da comments (that's do really)
//
// Revision 1.3  2002-10-30 16:26:57+00  ian_mayo
// tidy (shorten) up display names for editables
//
// Revision 1.2  2002-05-28 09:25:54+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:20+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:01+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:13+01  administrator
// Initial revision
//
// Revision 1.4  2001-01-22 19:39:56+00  novatech
// hide the text if this is a small size object
//
// Revision 1.3  2001-01-22 12:29:29+00  novatech
// added JUnit testing code
//
// Revision 1.2  2001-01-17 13:26:10+00  novatech
// name change
//
// Revision 1.1  2001-01-16 19:29:41+00  novatech
// Initial revision
//

package MWC.GUI.Shapes.Symbols.Geog;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import MWC.GUI.CanvasType;
import MWC.GUI.Editable;
import MWC.GUI.Shapes.Symbols.PlainSymbol;
import MWC.GenericData.WorldLocation;

public class ReferenceSym extends PlainSymbol
{

  ////////////////////////////////
  // member objects
  ////////////////////////////////

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static java.awt.Font _myFont = new java.awt.Font("Arial",
                                                           java.awt.Font.PLAIN,
                                                           12);


  private String _leftLabel = "A";
  private String _rightLabel = "A";

  /**
   * our editor
   */
  transient private Editable.EditorType _myEditor = null;

  ////////////////////////////////
  // member functions
  ////////////////////////////////


  public void getMetafile()
  {
  }

  public java.awt.Dimension getBounds()
  {
    // sort out the size of the symbol at the current scale factor
    final java.awt.Dimension res = new java.awt.Dimension((int) (6 * getScaleVal()), (int) (6 * getScaleVal()));
    return res;
  }

  public void paint(final CanvasType dest, final WorldLocation centre)
  {
    paint(dest, centre, 0.0);
  }


  public void paint(final CanvasType dest, final WorldLocation theLocation, final double direction)
  {

    // set the colour
    dest.setColor(getColor());

    // create our centre point
    final java.awt.Point centre = dest.toScreen(theLocation);

    final int wid = (int) (6 * getScaleVal());
    final int wid_2 = (int) (wid / 2d);
    final int wid_4 = (int) (wid / 4d);
    final int wid_8 = (int) (wid / 8d);

    // start with the centre object
    dest.drawLine(centre.x - wid_2, centre.y, centre.x + wid_2, centre.y);
    dest.drawLine(centre.x, centre.y - wid_4, centre.x, centre.y + wid_4);

    if (showSimplifiedSymbol())
    {
      // ignore the letter
    }
    else
    {
      // now the letters
      final int charWid = dest.getStringWidth(_myFont, _leftLabel);
      dest.drawText(_myFont, _leftLabel, centre.x - wid_8 - charWid, centre.y - wid_8);
      dest.drawText(_myFont, _rightLabel, centre.x + wid_8, centre.y - wid_8);
    }

  }

  public String getType()
  {
    return "Reference Position";
  }


  public Editable.EditorType getInfo()
  {
    if (_myEditor == null)
      _myEditor = new referenceInfo(this, this.getName());

    return _myEditor;
  }

  public void setReferenceLeftLabel(final String val)
  {
    _leftLabel = val;
  }

  public String getReferenceLeftLabel()
  {
    return _leftLabel;
  }

  public void setReferenceRightLabel(final String val)
  {
    _rightLabel = val;
  }

  public String getReferenceRightLabel()
  {
    return _rightLabel;
  }


  ////////////////////////////////////////////
  // editable support
  ////////////////////////////////////////////
  public boolean hasEditor()
  {
    return true;
  }

  //////////////////////////////////////////////////////
  // bean info for this class
  /////////////////////////////////////////////////////
  public class referenceInfo extends Editable.EditorType
  {

    public referenceInfo(final ReferenceSym data,
                         final String theName)
    {
      super(data, theName, "");
    }


    public PropertyDescriptor[] getPropertyDescriptors()
    {
      try
      {
        final PropertyDescriptor[] res = {
          displayProp("ReferenceLeftLabel", "Reference left label", "the left-hand label for reference position"),
          displayProp("ReferenceRightLabel", "Reference right label", "the right-hand label for reference position"),
        };

        return res;

      }
      catch (final IntrospectionException e)
      {
        return super.getPropertyDescriptors();
      }
    }


  }


  //////////////////////////////////////////////////////////////////////////////////////////////////
  // testing for this class
  //////////////////////////////////////////////////////////////////////////////////////////////////
  static public class ReferenceTest extends junit.framework.TestCase
  {
    static public final String TEST_ALL_TEST_TYPE = "UNIT";

    public ReferenceTest(final String val)
    {
      super(val);
    }

    public void testMyParams()
    {
      MWC.GUI.Editable ed = new ReferenceSym();
      editableTesterSupport.testParams(ed, this);
      ed = null;
    }
  }
}




