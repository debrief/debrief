package ASSET.Util.XML.Utils.LookupEnvironment;

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

  public TargetIntegerDatumHandler(final String myType, final String[] headings)
  {
    super(myType, headings);

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
    // ok, create the results object
    IntegerTargetTypeLookupHandler.SingleDatum datum = new IntegerTargetTypeLookupHandler.SingleDatum();
    datum._myType = _myType;
    datum._myValues = _myValues;

    setDatum(datum);

    _myType = null;
    _myValues = null;
  }

  abstract public void setDatum(IntegerTargetTypeLookupHandler.SingleDatum value);

  public void setDatums(LookupSensor.IntegerLookup res)
  {
    // just ignore it...
  }

}
