/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 */
package Debrief.ReaderWriter.XML.Tactical;

import java.text.ParseException;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackCoverageWrapper;
import Debrief.Wrappers.DynamicTrackShapes.DynamicTrackShapeWrapper;
import MWC.Utilities.Errors.Trace;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

abstract public class DynamicTrackCoverageHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	private static final String MY_TYPE = "DynamicCoverage";

	private static final String LINE_STYLE = "LineStyle";

	private static final String VISIBLE = "Visible";

	private static final String SEMI_TRANS = "SemiTransparent";

	private static final String START_DTG = "StartDtg";
	private static final String END_DTG = "EndDtg";
	private static final String ARCS = "arcs";

	DynamicTrackShapeWrapper _theContact;

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LocationPropertyEditor labelLocation = new MWC.GUI.Properties.LocationPropertyEditor();

	/**
	 * class which contains list of textual representations of label locations
	 */
	static final MWC.GUI.Properties.LineStylePropertyEditor lineStyle = new MWC.GUI.Properties.LineStylePropertyEditor();

	/** default constructor - using fixed name
	 * 
	 */
	public DynamicTrackCoverageHandler()
	{
		// inform our parent what type of class we are
		this(MY_TYPE);
	}

	/** versatile constructor that allows any item name to be specified
	 * 
	 * @param typeName the element name that we're looking for
	 */
	public DynamicTrackCoverageHandler(final String typeName)
	{
		// inform our parent what type of class we are
		super(typeName);

		addAttributeHandler(new HandleAttribute(START_DTG)
		{
			public void setValue(final String name, final String value)
			{
				try
        {
          _theContact.setStartDTG(parseThisDate(value));
        }
        catch (ParseException e)
        {
          Trace.trace(e, "While parsing date");
        }
			}
		});
		
		addAttributeHandler(new HandleAttribute(END_DTG)
		{
			public void setValue(final String name, final String value)
			{
				try
        {
          _theContact.setEndDTG(parseThisDate(value));
        }
        catch (ParseException e)
        {
          Trace.trace(e, "While parsing date");
        }
			}
		});
		
		addAttributeHandler(new HandleAttribute(ARCS)
		{
			public void setValue(final String name, final String value)
			{
				_theContact.setConstraints(value);
			}
		});

		addHandler(new ColourHandler()
		{
			public void setColour(final java.awt.Color theVal)
			{
				_theContact.setColor(theVal);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(SEMI_TRANS)
		{
			public void setValue(final String name, final boolean value)
			{
				_theContact.setSemiTransparent(value);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(VISIBLE)
		{
			public void setValue(final String name, final boolean value)
			{
				_theContact.setVisible(value);
			}
		});

		addAttributeHandler(new HandleAttribute(LINE_STYLE)
		{
			public void setValue(final String name, final String val)
			{
				lineStyle.setAsText(val.replace('_', ' '));
				final Integer res = (Integer) lineStyle.getValue();
				if (res != null)
					_theContact.setLineStyle(res);
			}
		});

	}

	public final void handleOurselves(final String name, final Attributes atts)
	{
		// create the new items
		_theContact = new DynamicTrackCoverageWrapper();

		labelLocation.setValue(null);
		lineStyle.setValue(null);

		super.handleOurselves(name, atts);
	}

	public final void elementClosed()
	{
		// and store it
		addContact(_theContact);

		// reset our variables
		_theContact = null;
	}

	abstract public void addContact(MWC.GUI.Plottable plottable);


	/** export this item using the default xml element name
	 * 
	 * @param contact the item to export
	 * @param parent the xml parent element
	 * @param doc the document we're being written to
	 */
	public static void exportFix(final DynamicTrackShapeWrapper contact,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		exportFix(MY_TYPE, contact, parent, doc);
	}
	
	
	/** export this item using the specified element name
	 * 
	 * @param typeName the xml element name to use
	 * @param contact the item to export
	 * @param parent the xml parent element
	 * @param doc the document we're being written to
	 */
	public static void exportFix(final String typeName, final DynamicTrackShapeWrapper contact,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		final Element eFix = doc.createElement(typeName);

		if (contact.getStartDTG() != null)
		{
			eFix.setAttribute(START_DTG, writeThis(contact.getStartDTG()));
		}
		if (contact.getEndDTG() != null)
		{
			eFix.setAttribute(END_DTG, writeThis(contact.getEndDTG()));
		}
		eFix.setAttribute(ARCS, contact.getConstraints());

		eFix.setAttribute(VISIBLE, writeThis(contact.getVisible()));
		
		eFix.setAttribute(SEMI_TRANS, writeThis(contact.getSemiTransparent()));
		
		// sort out the line style
		lineStyle.setValue(contact.getLineStyle());
		eFix.setAttribute(LINE_STYLE, lineStyle.getAsText().replace(' ', '_'));

		// note, we are accessing the "actual" colour for this fix, we are not
		// using the
		// normal getColor method which may return the track colour
		final java.awt.Color fCol = contact.getActualColor();
		if (fCol != null)
			MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(fCol,
					eFix, doc);

		parent.appendChild(eFix);

	}

}