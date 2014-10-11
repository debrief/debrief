/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.OpticLookupSensor;
import ASSET.Models.Sensor.Lookup.MADLookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 09:24:08
 * To change this template use File | Settings | File Templates.
 */
abstract public class MADLookupTableHandler extends MWCXMLReader
{
  private static final String PRED_RANGE_SET = "PredictedRangeSet";
  private static final String PRED_RANGE_DATUM = "PredictedRangeDatum";

  LookupSensor.StringLookup _visibility;

  String _myName = null;
  private static final String NAME_ATTRIBUTE = "Name";
  private static final String VISIBLILITY = "PredictedRange";


  public MADLookupTableHandler(final String myType)
  {
    super(myType);

    addAttributeHandler(new HandleAttribute(NAME_ATTRIBUTE)
    {
      public void setValue(String name, String value)
      {
        _myName = value;
      }
    });


    addHandler(new StringSetHandler(PRED_RANGE_SET, PRED_RANGE_DATUM, VISIBLILITY)
    {
      public void setDatums(LookupSensor.StringLookup myValues)
      {
        _visibility = myValues;
      }
    });

  }

  public void elementClosed()
  {
    MADLookupSensor.MADEnvironment res = new MADLookupSensor.MADEnvironment(_myName, _visibility);

    setMADEnvironment(res);
    _visibility = null;
    _myName = null;
  }

  abstract public void setMADEnvironment(MADLookupSensor.MADEnvironment env);

  public static void exportThis(String type, OpticLookupSensor.OpticEnvironment optic, Element parent,
                                Document doc)
  {
    // ok, put us into the element
    Element envElement = doc.createElement(type);

    // get on with the name attribute
    envElement.setAttribute(NAME_ATTRIBUTE, optic.getName());

    // now the child bits
    System.err.println("EXPORT OF MAD TABLE NOT IMPLEMENTED");


    // and hang us off the parent
    parent.appendChild(envElement);

  }
}
