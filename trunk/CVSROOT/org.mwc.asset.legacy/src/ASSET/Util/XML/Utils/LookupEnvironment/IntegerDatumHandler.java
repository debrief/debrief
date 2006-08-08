package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 11:42:47
 * To change this template use File | Settings | File Templates.
 */
abstract public class IntegerDatumHandler extends MWCXMLReader
{
  Vector _myValues;

  public IntegerDatumHandler(final String myType, String[] headings)
  {
    super(myType);


    for (int i = 0; i < headings.length; i++)
    {
      String heading = headings[i];
      addAttributeHandler(new HandleDoubleAttribute(heading)
      {
        public void setValue(String name, double value)
        {
          addValue(value);
        }
      });
    }
  }

  protected void addValue(double value)
  {
    if(_myValues == null)
      _myValues = new Vector(0,1);
    _myValues.add(new Double(value));
  }

  public void elementClosed()
  {
    // first the indices
    int[] indices = new int[_myValues.size()];
    for (int i = 0; i < indices.length; i++)
    {
      indices[i] = i;
    }

    // and now the values themselves
    double[] vals = new double[indices.length];
    for (int i = 0; i < _myValues.size(); i++)
    {
      Double aDouble = (Double) _myValues.elementAt(i);
      vals[i] = aDouble.doubleValue();
    }

    LookupSensor.IntegerLookup res = new LookupSensor.IntegerLookup(indices, vals);

    setDatums(res);

    _myValues = null;
  }

  abstract public void setDatums(LookupSensor.IntegerLookup res);

  public static void exportThis(String type, LookupSensor.IntegerLookup lightLevel, Element parent, Document doc)
  {
   // ok, put us into the element
    org.w3c.dom.Element envElement = doc.createElement(type);

    // now the child bits


    // and hang us off the parent
    parent.appendChild(envElement);
  }
}
