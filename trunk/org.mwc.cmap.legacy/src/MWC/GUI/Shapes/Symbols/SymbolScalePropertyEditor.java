// Copyright MWC 1999, Debrief 3 Project
// $RCSfile: SymbolScalePropertyEditor.java,v $
// @author $Author: Ian.Mayo $
// @version $Revision: 1.2 $
// $Log: SymbolScalePropertyEditor.java,v $
// Revision 1.2  2004/05/25 15:37:31  Ian.Mayo
// Commit updates from home
//
// Revision 1.1.1.1  2004/03/04 20:31:23  ian
// no message
//
// Revision 1.1.1.1  2003/07/17 10:07:34  Ian.Mayo
// Initial import
//
// Revision 1.2  2002-05-28 09:25:52+01  ian_mayo
// after switch to new system
//
// Revision 1.1  2002-05-28 09:14:22+01  ian_mayo
// Initial revision
//
// Revision 1.1  2002-04-11 14:01:06+01  ian_mayo
// Initial revision
//
// Revision 1.0  2001-07-17 08:43:15+01  administrator
// Initial revision
//
// Revision 1.4  2001-02-02 15:52:33+00  novatech
// handle the old "Regular" and "Tiny" values of scale
//
// Revision 1.3  2001-01-24 11:38:03+00  novatech
// size labels Tiny,Small,Regular,Large changed to Small,Medium,Large
//
// Revision 1.2  2001-01-09 10:31:20+00  novatech
// Create public constants defining sizes, and add TINY size
//
// Revision 1.1  2001-01-03 13:42:17+00  novatech
// Initial revision
//



package MWC.GUI.Shapes.Symbols;

import java.beans.PropertyEditorSupport;

public class SymbolScalePropertyEditor extends PropertyEditorSupport
{

  // publicly accessable values used for setting scale from code
  final public static double SMALL = 0.5;
  final public static double MEDIUM = 1;
  final public static double LARGE = 2;

  protected Double _mySize;

  private String stringTags[] =
  {
                     "Small",
                     "Medium",
                     "Large"};

  private double sizes[] =
  {
    SMALL,
    MEDIUM,
    LARGE
  };


  public String[] getTags()
  {
    return stringTags;
  }

  public Object getValue()
  {
    return _mySize;
  }



  public void setValue(Object p1)
  {
    if(p1 instanceof Double)
    {
      _mySize = (Double)p1;
    }
    if(p1 instanceof String)
    {
      String val = (String) p1;
      setAsText(val);
    }

    // check we have worked correctly
    if(_mySize == null)
    {
      // throw an error - this should only occur during development
      System.out.println("CRASH ME: " + _mySize.toString());

    }
  }

  public void setAsText(String val)
  {
    // handle our two "old" values, Tiny is now small, and regular is now medium
    if(val.equals("Tiny"))
    {
      val = "Small";
    }
    else
    {
      if(val.equals("Regular"))
      {
        val = "Medium";
      }
    }

    for(int i=0;i<stringTags.length;i++)
    {
      String thisS = stringTags[i];
      if(thisS.equals(val))
      {
        _mySize = new Double(sizes[i]);
      }
    }

  }

  public String getAsText()
  {
    String res = null;
    double current = _mySize.doubleValue();
    for(int i=0;i<sizes.length;i++)
    {
      double v = sizes[i];
      if(v == current)
      {
        res = stringTags[i];
      }

    }

    return res;
  }
}

