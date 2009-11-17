package Debrief.ReaderWriter.XML.Tactical;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GenericData.WorldDistance;
import MWC.Utilities.ReaderWriter.XML.Util.*;

abstract public class SensorContactHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private static final String SENSOR_CONTACT = "sensor_contact";

	private static final String LINE_STYLE = "LineStyle";

	private static final String PUT_LABEL_AT = "PutLabelAt";

	private static final String LABEL_LOCATION = "LabelLocation";

	private static final String VISIBLE = "Visible";

	private static final String LABEL_SHOWING = "LabelShowing";

	private static final String LABEL = "Label";

	private static final String BEARING = "Bearing";

	private static final String CENTRE = "centre";

	private static final String DTG = "Dtg";

	private static final String AMBIGUOUS_BEARING = "AmbiguousBearing";
	private static final String HAS_AMBIGUOUS_BEARING = "HasAmbiguousBearing";

	private static final String FREQUENCY = "Frequency";
	private static final String HAS_FREQUENCY = "HasFrequency";

	private static final String RANGE = "Range";

	Debrief.Wrappers.SensorContactWrapper _theContact;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor lp = new MWC.GUI.Properties.LocationPropertyEditor();

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LineLocationPropertyEditor ll = new MWC.GUI.Properties.LineLocationPropertyEditor();

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LineStylePropertyEditor ls = new MWC.GUI.Properties.LineStylePropertyEditor();

	private WorldDistance _myRange = null;

	/** default constructor - using fixed name
	 * 
	 */
	public SensorContactHandler()
	{
		// inform our parent what type of class we are
		this(SENSOR_CONTACT);
	}

	/** versatile constructor that allows any item name to be specified
	 * 
	 * @param typeName the element name that we're looking for
	 */
	public SensorContactHandler(final String typeName)
	{
		// inform our parent what type of class we are
		super(typeName);

		addAttributeHandler(new HandleAttribute(LABEL)
		{
			public void setValue(String name, String value)
			{
				_theContact.setLabel(fromXML(value));
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(RANGE)
		{
			public void setValue(String name, double value)
			{
				_theContact.setRange(new WorldDistance(value, WorldDistance.YARDS));
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(BEARING)
		{
			public void setValue(String name, double value)
			{
				_theContact.setBearing(value);
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(AMBIGUOUS_BEARING)
		{
			public void setValue(String name, double value)
			{
				_theContact.setAmbiguousBearing(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(HAS_AMBIGUOUS_BEARING)
		{
			public void setValue(String name, boolean value)
			{
				_theContact.setHasAmbiguousBearing(value);
			}
		});

		addAttributeHandler(new HandleDoubleAttribute(FREQUENCY)
		{
			public void setValue(String name, double value)
			{
				_theContact.setFrequency(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(HAS_FREQUENCY)
		{
			public void setValue(String name, boolean value)
			{
				_theContact.setHasFrequency(value);
			}
		});

		addAttributeHandler(new HandleAttribute(DTG)
		{
			public void setValue(String name, String value)
			{
				_theContact.setDTG(parseThisDate(value));
			}
		});

		addHandler(new LocationHandler(CENTRE)
		{
			public void setLocation(MWC.GenericData.WorldLocation res)
			{
				_theContact.setOrigin(res);
			}
		});

		addHandler(new ColourHandler()
		{
			public void setColour(java.awt.Color theVal)
			{
				_theContact.setColor(theVal);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(LABEL_SHOWING)
		{
			public void setValue(String name, boolean value)
			{
				_theContact.setLabelVisible(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			public void setValue(String name, boolean value)
			{
				_theContact.setVisible(value);
			}
		});

		addAttributeHandler(new HandleAttribute(LABEL_LOCATION)
		{
			public void setValue(String name, String val)
			{
				lp.setAsText(val);
				Integer res = (Integer) lp.getValue();
				if (res != null)
					_theContact.setLabelLocation(res);
			}
		});
		addAttributeHandler(new HandleAttribute(PUT_LABEL_AT)
		{
			public void setValue(String name, String val)
			{
				ll.setAsText(val);
				Integer res = (Integer) ll.getValue();
				if (res != null)
					_theContact.setPutLabelAt(res);
			}
		});
		addAttributeHandler(new HandleAttribute(LINE_STYLE)
		{
			public void setValue(String name, String val)
			{
				ls.setAsText(val.replace('_', ' '));
				Integer res = (Integer) ls.getValue();
				if (res != null)
					_theContact.setLineStyle(res);
			}
		});

		addHandler(new WorldDistanceHandler(RANGE)
		{
			public void setWorldDistance(WorldDistance value)
			{
				_myRange = value;
			}
		});
	}

	public final void handleOurselves(String name, Attributes atts)
	{
		// create the new items
		_theContact = new Debrief.Wrappers.SensorContactWrapper();

		lp.setValue(null);
		ls.setValue(null);
		ll.setValue(null);

		super.handleOurselves(name, atts);
	}

	public final void elementClosed()
	{
		// do we have a range?
		if (_myRange != null)
		{
			_theContact.setRange(_myRange);

			// and clear it
			_myRange = null;
		}

		// and store it
		addContact(_theContact);

		// reset our variables
		_theContact = null;
		_myRange = null;
	}

	abstract public void addContact(MWC.GUI.Plottable plottable);


	/** export this item using the default xml element name
	 * 
	 * @param contact the item to export
	 * @param parent the xml parent element
	 * @param doc the document we're being written to
	 */
	public static void exportFix(Debrief.Wrappers.SensorContactWrapper contact,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		exportFix(SENSOR_CONTACT, contact, parent, doc);
	}
	
	
	/** export this item using the specified element name
	 * 
	 * @param typeName the xml element name to use
	 * @param contact the item to export
	 * @param parent the xml parent element
	 * @param doc the document we're being written to
	 */
	public static void exportFix(final String typeName, Debrief.Wrappers.SensorContactWrapper contact,
			org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		/*
		 * 
		 * Dtg CDATA #REQUIRED Track CDATA #REQUIRED Bearing CDATA #REQUIRED Range
		 * CDATA #REQUIRED Visible (TRUE|FALSE) "TRUE" Label CDATA #REQUIRED
		 * LabelShowing (TRUE|FALSE) "TRUE" LabelLocation
		 * (Top|Left|Bottom|Centre|Right) "Left" LineStyle
		 * (SOLID|DOTTED|DOT_DASH|SHORT_DASHES|LONG_DASHES|UNCONNECTED) "SOLID"
		 */
		Element eFix = doc.createElement(typeName);

		eFix.setAttribute(DTG, writeThis(contact.getDTG()));

		eFix.setAttribute(VISIBLE, writeThis(contact.getVisible()));
		eFix.setAttribute(BEARING, writeThis(contact.getBearing()));
		eFix.setAttribute(LABEL_SHOWING, writeThis(contact.getLabelVisible()));
		eFix.setAttribute(LABEL, toXML(contact.getLabel()));
		// we no longer export range as an attribute, but as an element-so we can
		// store units
		// eFix.setAttribute("Range", writeThis(contact.getRange().getValueIn(
		// WorldDistance.YARDS)));

		// do we have ambiguous data?
		eFix.setAttribute(AMBIGUOUS_BEARING, writeThis(contact
				.getAmbiguousBearing()));
		eFix.setAttribute(HAS_AMBIGUOUS_BEARING, writeThis(contact
				.getHasAmbiguousBearing()));

		// do we have frequency data?
		eFix.setAttribute(FREQUENCY, writeThis(contact.getFrequency()));
		eFix.setAttribute(HAS_FREQUENCY, writeThis(contact.getHasFrequency()));

		// sort out the range
		// do we have range?
		if (contact.getRange() != null)
			WorldDistanceHandler.exportDistance(RANGE, contact.getRange(), eFix, doc);

		// sort out the line style
		ls.setValue(contact.getLineStyle());
		eFix.setAttribute(LINE_STYLE, ls.getAsText().replace(' ', '_')); // note,
		// we
		// swap
		// spaces
		// for
		// underscores

		// and the line label location
		ll.setValue(contact.getPutLabelAt());
		eFix.setAttribute(PUT_LABEL_AT, ll.getAsText());

		// and the label itself
		lp.setValue(contact.getLabelLocation());
		eFix.setAttribute(LABEL_LOCATION, lp.getAsText());

		// note, we are accessing the "actual" colour for this fix, we are not
		// using the
		// normal getColor method which may return the track colour
		java.awt.Color fCol = contact.getActualColor();
		if (fCol != null)
			MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(fCol,
					eFix, doc);

		// and now the centre item,
		MWC.GenericData.WorldLocation origin = contact.getOrigin();
		if (origin != null)
			MWC.Utilities.ReaderWriter.XML.Util.LocationHandler.exportLocation(
					origin, CENTRE, eFix, doc);

		parent.appendChild(eFix);

	}

}