package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 11:42:47
 * To change this template use File | Settings | File Templates.
 */
abstract public class StringSetHandler extends MWCXMLReader
{
  Vector _myTypes;
  Vector _myValues;


  private static final String UNKNOWN_TYPE = "UnknownType";
  Double _defaultValue;

  public StringSetHandler(final String myType, String datumName, String dataValue)
  {
    super(myType);

    addHandler(new StringDatumHandler(datumName, dataValue)
    {
      public void setDatum(String name, double value)
      {
        addValue(name, value);
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

  protected void addValue(String name, double value)
  {
    if (_myValues == null)
    {
      _myTypes = new Vector(1, 1);
      _myValues = new Vector(1, 1);
    }

    _myTypes.add(name);
    _myValues.add(new Double(value));

  }

  public void elementClosed()
  {
    // first the indices
    String[] strs = new String[_myValues.size()];
    for (int i = 0; i < strs.length; i++)
    {
      strs[i] = (String) _myTypes.elementAt(i);
    }

    // and now the values themselves
    double[] vals = new double[strs.length];
    for (int i = 0; i < _myValues.size(); i++)
    {
      Double aDouble = (Double) _myValues.elementAt(i);
      vals[i] = aDouble.doubleValue();
    }

    LookupSensor.StringLookup res = new LookupSensor.StringLookup(strs, vals, _defaultValue);

    setDatums(res);

    _myValues = null;
    _defaultValue = null;
  }

  abstract public void setDatums(LookupSensor.StringLookup res);


  public static void exportThis(String setName, String datumName, Element parent, Document doc)
  {
    System.err.println("NOT EXPORTING STRING SETS yet.");
  }


  /**
   * *******************************************************************
   * embedded class which records pairs of type/value datums
   * *******************************************************************
   */
  abstract public class StringDatumHandler extends MWCXMLReader
  {
    String _name;
    double _value;
    private static final String NAME = "Type";

    protected StringDatumHandler(final String myType, final String visLabel)
    {
      super(myType);

      addAttributeHandler(new HandleAttribute(NAME)
      {
        public void setValue(String name, String value)
        {
          _name = value;
        }
      });
      addAttributeHandler(new HandleDoubleAttribute(visLabel)
      {
        public void setValue(String name, double value)
        {
          _value = value;
        }
      });

    }

    public void elementClosed()
    {
      setDatum(_name, _value);
      _name = null;
    }

    abstract public void setDatum(String name, double value);
  }

}
