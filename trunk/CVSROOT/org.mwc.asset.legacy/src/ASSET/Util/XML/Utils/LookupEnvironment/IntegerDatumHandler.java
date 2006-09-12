package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.IntegerLookup;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 26-Oct-2004
 * Time: 11:42:47
 * To change this template use File | Settings | File Templates.
 */
abstract public class IntegerDatumHandler extends MWCXMLReader
{
  LookupSensor.IntegerLookup _res;
  
  // remember our list of text strings
  final protected String[] _theCategories;

  public IntegerDatumHandler(final String myType, String[] categories)
  {
    super(myType);
    
    _theCategories = categories;


    for (int i = 0; i < categories.length; i++)
    {
    	final int index = i;
      final String heading = categories[i];
      addAttributeHandler(new HandleDoubleAttribute(heading)
      {
        public void setValue(String name, double value)
        {
          addValue(index, value, heading);
        }
      });
    }
  }

  protected void addValue(int type, double value, String category)
  {
  	if(_res == null)
  	{
  		_res = new IntegerLookup();
  	}
  	
  	_res.add(type, value);
  }

  public void elementClosed()
  {
    setDatums(_res);
    _res = null;
  }

  abstract public void setDatums(LookupSensor.IntegerLookup res);

  public static void exportThis(String type, LookupSensor.IntegerLookup lightLevel,
  		Element parent, Document doc, String[] headings)
  {
   // ok, put us into the element
    org.w3c.dom.Element envElement = doc.createElement(type);

    // now the child bits
    Collection indices = lightLevel.indices();
    for (Iterator iter = indices.iterator(); iter.hasNext();)
		{
			Integer thisIndex = (Integer) iter.next();
			Double val = lightLevel.find(thisIndex.intValue());
			// ok, export it
			envElement.setAttribute(headings[thisIndex.intValue()],  writeThisLong(val.doubleValue()));			
		}

    // and hang us off the parent
    parent.appendChild(envElement);
  }
}
