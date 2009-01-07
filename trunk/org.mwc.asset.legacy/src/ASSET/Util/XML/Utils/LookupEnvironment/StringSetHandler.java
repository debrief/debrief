package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.StringLookup;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.*;

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
  Vector<String> _myTypes;
  Vector<Double> _myValues;


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
      _myTypes = new Vector<String>(1, 1);
      _myValues = new Vector<Double>(1, 1);
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



	public static void exportThis(String target_vis, String target_vis_datum, String ATTRIBUTE_LABEL, StringLookup atten, Element env, Document doc)
	{
    // ok, put us into the element
    org.w3c.dom.Element envElement = doc.createElement(target_vis);

    // get on with the name attribute
    Double unknownVal = atten.getUnknownResult();
    if(unknownVal != null)
    	envElement.setAttribute(UNKNOWN_TYPE, writeThisLong(unknownVal.doubleValue()));
		
    // now cycle through the elements themselves
    Collection<String> theIndices = atten.getIndices();
    for (Iterator<String> iter = theIndices.iterator(); iter.hasNext();)
		{
			String thisIndex = (String) iter.next();
			Double res = atten.find(thisIndex);
			StringDatumHandler.exportThis(target_vis_datum, thisIndex, ATTRIBUTE_LABEL,  res.doubleValue(), envElement, doc);
		}
    
    env.appendChild(envElement);
	}

  /**
   * *******************************************************************
   * embedded class which records pairs of type/value datums
   * *******************************************************************
   */
  abstract static public class StringDatumHandler extends MWCXMLReader
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

    public static void exportThis(String target_vis_datum, String thisIndex, String attribute_label, double d, Element envElement, Document doc)
		{
      // ok, put us into the element
      org.w3c.dom.Element datum = doc.createElement(target_vis_datum);
      datum.setAttribute(NAME, thisIndex);
      datum.setAttribute(attribute_label, writeThisLong(d));
      
      envElement.appendChild(datum);

			
		}

		public void elementClosed()
    {
      setDatum(_name, _value);
      _name = null;
    }

    abstract public void setDatum(String name, double value);

  }


}
