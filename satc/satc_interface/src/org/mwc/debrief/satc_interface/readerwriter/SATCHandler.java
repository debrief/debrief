package org.mwc.debrief.satc_interface.readerwriter;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.runtime.Status;
import org.mwc.debrief.satc_interface.data.SATC_Solution;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

import com.planetmayo.debrief.satc.model.generator.ISolver;
import com.planetmayo.debrief.satc.model.manager.ISolversManager;
import com.planetmayo.debrief.satc_rcp.SATC_Activator;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamReader;
import com.planetmayo.debrief.satc_rcp.io.XStreamIO.XStreamWriter;

public class SATCHandler extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String MY_TYPE = "satc_solution";

	private static final String NAME = "NAME";
	private static final String SHOW_BOUNDS = "ShowBounds";
	private static final String ONLY_ENDS = "OnlyPlotEnds";
	private static final String SHOW_SOLUTIONS = "ShowSolutions";
	private static final String AUTO_SOLUTIONS = "AutoSolutions";

	protected String _myContents;

	private String _name;

	private Layers _theLayers;
	
	private Color _myColor = Color.green;

	private CharArrayWriter _cdataCharacters;

	protected boolean _showSolutions = false;

	protected boolean _showBounds = false;
	protected boolean _autoGenerate = false;

	protected boolean _onlyPlotEnds = false;

	public SATCHandler()
	{
		this(MY_TYPE);
	}

	public SATCHandler(String theType)
	{
		// inform our parent what type of class we are
		super(theType);

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String val)
			{
				_name = val;
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
		addAttributeHandler(new HandleBooleanAttribute(ONLY_ENDS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_onlyPlotEnds  = value;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(AUTO_SOLUTIONS)
		{
			@Override
			public void setValue(String name, boolean value)
			{
				_autoGenerate = value;
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
				_myColor = res;
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
		// create the solver
		ISolversManager solvMgr = SATC_Activator.getDefault().getService(ISolversManager.class, true);
		ISolver newSolvr = solvMgr.createSolver(_name);
		SATC_Solution solution = new SATC_Solution(newSolvr);

		// and the preferences
		solution.setShowLocationBounds(_showBounds);
		solution.setOnlyPlotLegEnds(_onlyPlotEnds);
		solution.setShowSolutions(_showSolutions);
		solution.setColor(_myColor);
		solution.getSolver().setAutoGenerateSolutions(_autoGenerate);

		// ok, repopulate the solver from the contents
		if (_cdataCharacters.size() > 0)
		{
			try
			{
				InputStream inputStream = new ByteArrayInputStream(_cdataCharacters
						.toString().getBytes("utf-8"));
				XStreamReader reader = XStreamIO.newReader(inputStream, "Unknown");
				if (reader.isLoaded())
				{
					solution.getSolver().load(reader);
				}
			}
			catch (UnsupportedEncodingException e)
			{
				SATC_Activator.log(Status.ERROR,
						"Problem laoding SATC Solution from XML", e);
			}
		}

		// ok, the solver knows it's contributions, but we've got update to Debrief one.
		solution.selfScan();
		
		// put it into the solution.

		// and save it.
		_theLayers.addThisLayer(solution);
	}

	@Override
	public void setLayers(Layers theLayers)
	{
		_theLayers = theLayers;
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return (subject instanceof SATC_Solution);
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
		SATC_Solution solution = (SATC_Solution) theLayer;

		// ok, marshall it into a String

		XStreamWriter writer = XStreamIO.newWriter();
		solution.getSolver().save(writer);

		// and get the writer as a string
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		writer.process(oStream);
		try
		{
			String res = oStream.toString("utf-8");

			// ok, now convert it to an object
			Element newI = doc.createElement(MY_TYPE);

			// store the name
			newI.setAttribute(NAME, solution.getName());
			newI.setAttribute(SHOW_BOUNDS,
					writeThis(solution.getShowLocationBounds()));
			newI.setAttribute(ONLY_ENDS,
					writeThis(solution.getOnlyPlotLegEnds()));
			newI.setAttribute(SHOW_SOLUTIONS,
					writeThis(solution.getShowSolutions()));
			newI.setAttribute(AUTO_SOLUTIONS, writeThis(solution.getSolver().isAutoGenerateSolutions()));
			ColourHandler.exportColour(solution.getColor(), newI, doc);

			// insert the CDATA child node
			CDATASection cd = doc.createCDATASection(res);

			newI.appendChild(cd);

			// and store it
			parent.appendChild(newI);

		}
		catch (UnsupportedEncodingException e)
		{
			SATC_Activator.log(Status.ERROR,
					"Problem reading in stored SATC Solution", e);
		}

	}

}