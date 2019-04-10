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
package Debrief.ReaderWriter.XML.dummy;

import java.awt.Color;
import java.io.CharArrayWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import Debrief.GUI.Frames.Application;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

/** mock handler that will read in an SATC_Solution object, but which 
 * won't actually load the data
 * @author ian
 *
 */
public class SATCHandler_Mock extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String MY_TYPE = "satc_solution";

	private static final String NAME = "NAME";
	private static final String SHOW_BOUNDS = "ShowBounds";
	private static final String ONLY_ENDS = "OnlyPlotEnds";
	private static final String SHOW_SOLUTIONS = "ShowSolutions";
	private static final String SHOW_ALTERATIONS = "ShowAlterationBounds";
	private static final String LIVE_RUNNING = "LiveRunning";

	protected String _myContents;

	private CharArrayWriter _cdataCharacters;

	protected boolean _showSolutions = false;

	protected boolean _showBounds = false;
	protected boolean _showAlterations = false;
	protected boolean _liveRunning = true;

	protected boolean _onlyPlotEnds = false;

	public SATCHandler_Mock()
	{
		this(MY_TYPE);
	}

	public SATCHandler_Mock(String theType)
	{
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String val)
			{
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_BOUNDS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_showBounds = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_ALTERATIONS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_showAlterations = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(ONLY_ENDS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_onlyPlotEnds = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(LIVE_RUNNING)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_liveRunning = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_SOLUTIONS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_showSolutions = value;
			}
		});
		addHandler(new ColourHandler()
		{

			@Override
			public void setColour(Color res)
			{
			}
		});
	}

	@Override
	public void startElement(String nameSpace, String localName, String qName,
			Attributes attributes) throws SAXException
	{
		super.startElement(nameSpace, localName, qName, attributes);

		// clear the characters buffer
		_cdataCharacters = new CharArrayWriter();
	}

	/**
	 * the data is in a CDATA element. The only way to catch this is to use the
	 * characters handler
	 * 
	 */
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		super.characters(ch, start, length);

		_cdataCharacters.write(ch, start, length);
	}

  public void elementClosed()
  {
    Application.logError2(Application.WARNING,
        "SATC element has been dropped, this is a mock handler", null);
  }

	@Override
	public void setLayers(Layers theLayers)
	{
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return false;
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
	  throw new IllegalArgumentException("This is a mock handler, it cannot be used for export");
	}

}