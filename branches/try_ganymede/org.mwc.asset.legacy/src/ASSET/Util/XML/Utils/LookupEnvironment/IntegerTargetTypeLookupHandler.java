package ASSET.Util.XML.Utils.LookupEnvironment;

import ASSET.Models.Sensor.Lookup.LookupSensor;
import ASSET.Models.Sensor.Lookup.LookupSensor.*;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

import java.util.*;

import org.w3c.dom.*;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 26-Oct-2004 Time: 09:29:10 To
 * change this template use File | Settings | File Templates.
 */
abstract public class IntegerTargetTypeLookupHandler extends MWCXMLReader
{
	public static final String DATUM = "TargetAspectDatum";

	private static final String UNKNOWN_TYPE = "UnknownType";

	Vector<NamedList> _myDatums = null;

	Double _defaultValue;

	public IntegerTargetTypeLookupHandler(final String myType, final String mySubType,
			final String[] myHeadings)
	{
		super(myType);

		addHandler(new TargetIntegerDatumHandler(mySubType, myHeadings)
		{
			public void setDatum(LookupSensor.NamedList value)
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
	void addDatum(LookupSensor.NamedList value)
	{
		if (_myDatums == null)
			_myDatums = new Vector<LookupSensor.NamedList>(1, 1);
		_myDatums.add(value);
	}

	public void elementClosed()
	{

		LookupSensor.IntegerTargetTypeLookup res = new LookupSensor.IntegerTargetTypeLookup(
				_myDatums, _defaultValue);

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

	public static void exportThis(String target_sea_state_set,
			String target_sea_state_datum, String[] sea_state_headings,
			IntegerTargetTypeLookup states, Element envElement, Document doc)
	{
		// ok, put us into the element
		org.w3c.dom.Element itt = doc.createElement(target_sea_state_set);

		// get on with the name attribute
		Double unknown = states.getUnknownResult();
		if (unknown != null)
			itt.setAttribute(UNKNOWN_TYPE, writeThis(unknown.doubleValue()));

		// now the matrix of sea states
		Collection<String> keys = states.getNames();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();)
		{
			String thisN = (String)iter.next();

			// ok, cycle through the sea states for this participant
			NamedList thisList = states.getThisSeries(thisN);
			exportThisSeries(thisN, target_sea_state_datum, thisList, sea_state_headings, itt,
					doc);
		}

		envElement.appendChild(itt);
	}

	private static void exportThisSeries(String name, String target_sea_state_datum,
			NamedList thisList, String[] sea_state_headings, Element itt, Document doc)
	{
		// ok, put us into the element
		org.w3c.dom.Element datum = doc.createElement(target_sea_state_datum);
		
		datum.setAttribute("Type", name);
		
		// and step through its values
		Collection<Double> indices = thisList.getValues();
		int ctr = 0;
		for (Iterator<Double> iter = indices.iterator(); iter.hasNext();)
		{
			Double val = (Double) iter.next();
			if (val != null)
			{
				datum.setAttribute(sea_state_headings[ctr], writeThis(val.doubleValue()));
				ctr++;
			}
			else
				break;
		}

		itt.appendChild(datum);
	}

}
