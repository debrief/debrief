package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 09:29:10
 * To change this template use File | Settings | File Templates.
 */
abstract public class IntegerTargetTypeLookupHandler extends MWCXMLReader
{
  public static final String DATUM = "TargetAspectDatum";
  private static final String UNKNOWN_TYPE = "UnknownType";

  Vector _myDatums = null;
  Double _defaultValue;

  public IntegerTargetTypeLookupHandler(final String myType,
                                        final String mySubType,
                                        final String[] myHeadings)
  {
    super(myType);

    addHandler(new TargetIntegerDatumHandler(mySubType, myHeadings)
    {
      public void setDatum(SingleDatum value)
      {
        addDatum(value);
      }
    });

    addAttributeHandler(new HandleDoubleAttribute(UNKNOWN_TYPE)
    {
      public void setValue(String name, double value)
      {
        _defaultValue = new Double(value);
      }
    });

  }

  /**
   * store this new datum
   *
   * @param value
   */
  private void addDatum(SingleDatum value)
  {
    if (_myDatums == null)
      _myDatums = new Vector(1, 1);
    _myDatums.add(value);
  }


  public void elementClosed()
  {
    // SORT OUT THE INT HEADINGS
    // get a datum so we can count how many ints were read in
    SingleDatum single = (SingleDatum) _myDatums.elementAt(0);
    int[] headingInts = new int[single._myValues.size()];
    for (int i = 0; i < single._myValues.size(); i++)
    {
      headingInts[i] = i;
    }

    // now the text versions of the strings
    String[] targetStrings = new String[_myDatums.size()];

    // first collate the headings
    for (int i = 0; i < _myDatums.size(); i++)
    {
      SingleDatum sd = (SingleDatum) _myDatums.elementAt(i);
      targetStrings[i] = sd._myType;
    }

    // and now the string lookups
    LookupSensor.StringLookup[] vals = new LookupSensor.StringLookup[single._myValues.size()];


    // and now the values themselves
    for (int i = 0; i < headingInts.length; i++)
    {
      int myState = headingInts[i];
      double[] myValues = new double[_myDatums.size()];

      for (int j = 0; j < _myDatums.size(); j++)
      {
        SingleDatum datum = (SingleDatum) _myDatums.elementAt(j);
        Double thisVal = (Double) datum._myValues.elementAt(i);
        myValues[j] = thisVal.doubleValue();
      }

      vals[i] = new LookupSensor.StringLookup(targetStrings, myValues, null);

    }

    LookupSensor.IntegerTargetTypeLookup res = new LookupSensor.IntegerTargetTypeLookup(headingInts, vals, _defaultValue);

    // and store it
    setLookup(res);

    // ditch gash
    _myDatums = null;
  }

  /**
   * pass details back to calling class
   *
   * @param val
   */
  abstract public void setLookup(LookupSensor.IntegerTargetTypeLookup val);


  //////////////////////////////////////////////////
  // class to hold single datum
  //////////////////////////////////////////////////
  public static class SingleDatum
  {
    public String _myType;
    public Vector _myValues;
  }
}
