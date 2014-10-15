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
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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

  public BaseDataHandler(final String myType)
  {
    super(myType);
    addAttributeHandler(new HandleAttribute(UNITS)
    { public void setValue(final String name, final String val)
      {
        _units = val;
      }});
    addAttributeHandler(new HandleDoubleAttribute(VALUE)
      { public void setValue(final String name, final double val){
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
