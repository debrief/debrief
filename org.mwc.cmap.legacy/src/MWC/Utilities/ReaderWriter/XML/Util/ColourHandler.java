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
package MWC.Utilities.ReaderWriter.XML.Util;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import org.xml.sax.Attributes;

import MWC.GUI.Properties.DebriefColors;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ColourHandler extends MWCXMLReader
{

	private java.awt.Color _res;
	private int _r;
	private int _g;
	private int _b;

	static private java.util.Hashtable<String, java.awt.Color> _myColours;

	public ColourHandler()
	{
		// handle this in the "normal" constructor
		this("colour");
	}

	/**
	 * custom constructor, for when the attribute does not contain the standard
	 * name
	 * 
	 * @param name
	 *          the name of the attribute we are handling
	 */
	public ColourHandler(final String name)
	{
		// pass our attribute name up the line
		super(name);

		// check that the colours have been defined
		checkColours();
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes)
	{
		// initialise data
		_r = _g = _b = 0;

		final int len = attributes.getLength();
		for (int i = 0; i < len; i++)
		{

			final String nm = attributes.getLocalName(i);
			final String val = attributes.getValue(i);
			if (nm.equals("Value"))
			{
				_setColour(val);
			}
			else if (nm.equals("CustomRed"))
			{
				_r = Integer.valueOf(val).intValue();
			}
			else if (nm.equals("CustomGreen"))
			{
				_g = Integer.valueOf(val).intValue();
			}
			else if (nm.equals("CustomBlue"))
			{
				_b = Integer.valueOf(val).intValue();
			}
		}

	}

	private synchronized static void checkColours()
	{
		if (_myColours == null)
		{
			_myColours = new java.util.Hashtable<String, java.awt.Color>();
			_myColours.put("RED", DebriefColors.RED);
			_myColours.put("BLUE", DebriefColors.BLUE);
			_myColours.put("GREEN", DebriefColors.GREEN);
			_myColours.put("YELLOW", DebriefColors.YELLOW);
			_myColours.put("MAGENTA", DebriefColors.MAGENTA);
			_myColours.put("PURPLE", DebriefColors.PURPLE);
			_myColours.put("ORANGE", DebriefColors.ORANGE);
			_myColours.put("BROWN", DebriefColors.BROWN);
			_myColours.put("CYAN", DebriefColors.CYAN);
			_myColours.put("LIGHT_GREEN", DebriefColors.LIGHT_GREEN);
			_myColours.put("GOLD", DebriefColors.GOLD);
			_myColours.put("PINK", DebriefColors.PINK);
			_myColours.put("LIGHT_GREY", DebriefColors.LIGHT_GRAY);
			_myColours.put("GREY", DebriefColors.GRAY);
			_myColours.put("DARK_GREY", DebriefColors.DARK_GRAY);
			_myColours.put("WHITE", DebriefColors.WHITE);
			_myColours.put("BLACK", DebriefColors.BLACK);
			_myColours.put("DARK_BLUE", DebriefColors.DARK_BLUE);
			_myColours.put("MEDIUM_BLUE", DebriefColors.MEDIUM_BLUE);
		}
	}

	private void _setColour(final String val)
	{
		// try to convert this string to a colour
		if (val.equals("CUSTOM"))
		{
			// this is clearly a custom colour, leave colour definition
			// until we close the element, so that we can be sure we've
			// got all three r,g,b components
			_res = null;
		}

		// have the colours been defined?
		checkColours();

		// step through the colours, to see if we find one which matches
		final java.util.Enumeration<String> enumer = _myColours.keys();
		while (enumer.hasMoreElements())
		{
			final String thisK = enumer.nextElement();
			if (thisK.equals(val))
			{
				_res = _myColours.get(val);
				break;
			}
		}
	}

	public Color resolveColor(final String val)
	{
		// try to convert this string to a colour
		if (val.equals("CUSTOM"))
		{
			// this is clearly a custom colour, leave colour definition
			// until we close the element, so that we can be sure we've
			// got all three r,g,b components
			return null;
		}

		// have the colours been defined?
		checkColours();

		// step through the colours, to see if we find one which matches
		final java.util.Enumeration<String> enumer = _myColours.keys();
		while (enumer.hasMoreElements())
		{
			final String thisK = enumer.nextElement();
			if (thisK.equals(val))
			{
				return _myColours.get(val);
			}
		}

		return null;
	}

	@Override
	public void elementClosed()
	{
		// has a a predefined colour been selected?
		if (_res == null)
		{
			// no, create our own
			_res = new java.awt.Color(_r, _g, _b);
		}

		// return the result
		setColour(_res);

		// reset
		_res = null;
	}

	abstract public void setColour(java.awt.Color res);

	/**
	 * custom exporter, for when we are not using the expected attribute name
	 */
	public static void exportColour(final java.awt.Color color, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc, final String name)
	{

		if (color == null)
		{
			return;
		}

		// have the colours been specified?
		checkColours();

		// see if this is one of our export values
		final java.util.Enumeration<String> enumer = _myColours.keys();
		String ourKey = null;
		while (enumer.hasMoreElements())
		{
			final Object thisKey = enumer.nextElement();
			final java.awt.Color thisCol = _myColours.get(thisKey);
			if (thisCol.equals(color))
			{
				// found it!
				ourKey = (String) thisKey;
				break;
			}
		}

		final org.w3c.dom.Element eLoc = doc.createElement(name);
		if (ourKey != null)
		{
			eLoc.setAttribute("Value", ourKey);
		}
		else
		{
			eLoc.setAttribute("CustomRed", "" + color.getRed());
			eLoc.setAttribute("CustomGreen", "" + color.getGreen());
			eLoc.setAttribute("CustomBlue", "" + color.getBlue());
			eLoc.setAttribute("Value", "custom");
		}
		parent.appendChild(eLoc);

	}

	/**
	 * produce a color from the comma-separated string
	 * 
	 * @param val
	 * @return
	 */
	public static Color fromString(final String val)
	{
		Color res = null;
		final String[] items = val.split(",");
		if (items.length == 3)
		{
			res = new Color(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]));
		}
		return res;
	}

	/**
	 * produce a comma-separated string from the color
	 * 
	 * @param val
	 * @return
	 */
	public static String toString(final Color val)
	{
		return "" + val.getRed() + "," + val.getGreen() + "," + val.getBlue();
	}

	/**
	 * standard exporter, using our proper attribute name
	 */
	public static void exportColour(final java.awt.Color color, final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		if (color != null)
		{
			exportColour(color, parent, doc, "colour");
		}
	}

}