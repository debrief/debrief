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
	public ColourHandler(String name)
	{
		// pass our attribute name up the line
		super(name);

		// check that the colours have been defined
		checkColours();
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(String name, Attributes attributes)
	{
		// initialise data
		_r = _g = _b = 0;

		int len = attributes.getLength();
		for (int i = 0; i < len; i++)
		{

			String nm = attributes.getLocalName(i);
			String val = attributes.getValue(i);
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

	private static void checkColours()
	{
		if (_myColours == null)
		{
			_myColours = new java.util.Hashtable<String, java.awt.Color>();
			_myColours.put("RED", java.awt.Color.red);
			_myColours.put("BLUE", java.awt.Color.blue);
			_myColours.put("GREEN", java.awt.Color.green);
			_myColours.put("YELLOW", java.awt.Color.yellow);
			_myColours.put("MAGENTA", java.awt.Color.magenta);
			_myColours.put("PURPLE", new java.awt.Color(169, 1, 132));
			_myColours.put("ORANGE", java.awt.Color.orange);
			_myColours.put("BROWN", new java.awt.Color(188, 93, 6));
			_myColours.put("CYAN", java.awt.Color.cyan);
			_myColours.put("LIGHT_GREEN", new java.awt.Color(100, 240, 100));
			_myColours.put("GOLD", new java.awt.Color(230, 200, 20));
			_myColours.put("PINK", java.awt.Color.pink);
			_myColours.put("LIGHT_GREY", java.awt.Color.lightGray);
			_myColours.put("GREY", java.awt.Color.gray);
			_myColours.put("DARK_GREY", java.awt.Color.darkGray);
			_myColours.put("WHITE", java.awt.Color.white);
			_myColours.put("BLACK", java.awt.Color.black);
		}
	}

	private void _setColour(String val)
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
		java.util.Enumeration<String> enumer = _myColours.keys();
		while (enumer.hasMoreElements())
		{
			String thisK = enumer.nextElement();
			if (thisK.equals(val))
			{
				_res = _myColours.get(val);
				break;
			}
		}
	}

	public Color resolveColor(String val)
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
		java.util.Enumeration<String> enumer = _myColours.keys();
		while (enumer.hasMoreElements())
		{
			String thisK = enumer.nextElement();
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
	public static void exportColour(java.awt.Color color, org.w3c.dom.Element parent, org.w3c.dom.Document doc, String name)
	{

		if (color == null)
		{
			return;
		}

		// have the colours been specified?
		checkColours();

		// see if this is one of our export values
		java.util.Enumeration<String> enumer = _myColours.keys();
		String ourKey = null;
		while (enumer.hasMoreElements())
		{
			Object thisKey = enumer.nextElement();
			java.awt.Color thisCol = _myColours.get(thisKey);
			if (thisCol.equals(color))
			{
				// found it!
				ourKey = (String) thisKey;
				break;
			}
		}

		org.w3c.dom.Element eLoc = doc.createElement(name);
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
	public static Color fromString(String val)
	{
		Color res = null;
		String[] items = val.split(",");
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
	public static String toString(Color val)
	{
		return "" + val.getRed() + "," + val.getGreen() + "," + val.getBlue();
	}

	/**
	 * standard exporter, using our proper attribute name
	 */
	public static void exportColour(java.awt.Color color, org.w3c.dom.Element parent, org.w3c.dom.Document doc)
	{
		if (color != null)
		{
			exportColour(color, parent, doc, "colour");
		}
	}

}