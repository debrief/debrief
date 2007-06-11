package MWC.Utilities.ReaderWriter.XML.Util;

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

/**
 * PlanetMayo Ltd.  2003
 * User: Ian.Mayo
 * Date: 18-Sep-2003
 * Time: 10:57:13
 * Log:  
 *  $Log: BaseDataHandler.java,v $
 *  Revision 1.2  2004/05/24 16:24:53  Ian.Mayo
 *  Commit updates from home
 *
 *  Revision 1.1.1.1  2004/03/04 20:31:28  ian
 *  no message
 *
 *  Revision 1.1  2003/09/18 12:11:37  Ian.Mayo
 *  Initial implementation
 *
 *
 */
public class BaseDataHandler extends MWCXMLReader
{



  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////
  protected static final String UNITS = "Units";
  protected static final String VALUE = "Value";
  protected String _units;
  protected double _value;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////

  public BaseDataHandler(String myType)
  {
    super(myType);
    addAttributeHandler(new HandleAttribute(UNITS)
    { public void setValue(String name, String val)
      {
        _units = val;
      }});
    addAttributeHandler(new HandleDoubleAttribute(VALUE)
      { public void setValue(String name, double val){
        _value = val;      }});
  }


  /////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////
  public void elementClosed()
  {
    // reset the data
    _units = null;
    _value = -1;
  }

  //////////////////////////////////////////////////
  // testing
  //////////////////////////////////////////////////
}
