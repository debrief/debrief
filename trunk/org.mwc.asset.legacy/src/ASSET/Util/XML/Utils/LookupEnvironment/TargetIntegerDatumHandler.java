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
    Vector theValues = new Vector(0,1);
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
