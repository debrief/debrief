/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package MWC.Utilities.ReaderWriter.XML.Util;


/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import MWC.GenericData.Duration;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;


abstract public class DurationHandler extends MWCXMLReader
{

  String _units;
  double _value;


  public DurationHandler(final String myType)
  {
    super(myType);
    addAttributeHandler(new HandleAttribute("Units")
    { public void setValue(final String name, final String val)
      {
        _units = val;
      }});
    addAttributeHandler(new HandleDoubleAttribute("Value")
      { public void setValue(final String name, final double val){
        _value = val;      }});
  }

  public DurationHandler()
  {
    // inform our parent what type of class we are
    this("Duration");
  }



  public void elementClosed()
  {
    // produce a value using these units

    final int theUnits = Duration.getUnitIndexFor(_units);
    final Duration res = new Duration(_value, theUnits);

    setDuration(res);

    _units = null;
    _value = -1;


  }

  abstract public void setDuration(Duration res);


  public static void exportDuration(final String element_type, final Duration duration,
                                    final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
  {
    final org.w3c.dom.Element eLoc = doc.createElement(element_type);

    // set the attributes
    final int theUnit = Duration.selectUnitsFor(duration.getValueIn(Duration.MILLISECONDS));

    // and get value
    final double value = duration.getValueIn(theUnit);


    // get the name of the units
    final String units = Duration.getLabelFor(theUnit);

    eLoc.setAttribute("Value", writeThis(value));
    eLoc.setAttribute("Units", units);

    parent.appendChild(eLoc);
  }

  public static void exportDuration(final Duration duration, final org.w3c.dom.Element parent,
                                    final org.w3c.dom.Document doc)
  {
    exportDuration("Duration", duration, parent, doc);
  }

}