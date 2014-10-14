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

import java.util.Vector;

import ASSET.Models.Sensor.Lookup.LookupSensor;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 09:37:04
 * To change this template use File | Settings | File Templates.
 */
abstract public class TargetIntegerDatumHandler extends IntegerDatumHandler
{
  public final static String TYPE = "Type";

  String _myType;
  
  public TargetIntegerDatumHandler(final String myType, final String[] categories)
  {
    super(myType, categories);

    addAttributeHandler(new HandleAttribute(TYPE)
    {
      public void setValue(String name, String value)
      {
        _myType = value;
      }
    });

  }
  
  public void elementClosed()
  {
    // ok, extract the values
    Vector<Double> theValues = new Vector<Double>(0,1);
   for (int i=0;i<_theCategories.length;i++)
		{
			Double val = _res.find(i);
			theValues.add(val);
		}
    
   // ok, create the results object
   LookupSensor.NamedList datum = new LookupSensor.NamedList(_myType, theValues);

    setDatum(datum);

    _myType = null;
  }

  abstract public void setDatum(LookupSensor.NamedList value);

  public void setDatums(LookupSensor.IntegerLookup res)
  {
    // just ignore it...
  }

}
