package MWC.Utilities.ReaderWriter.XML.Features;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.BaseLayer;
import MWC.GUI.Editable;
import MWC.GUI.Layers;
import MWC.GUI.Chart.Painters.ETOPOPainter;
import MWC.GUI.Shapes.ChartFolio;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;

public class ChartFolioHandler extends LayerHandler
{

	private static final String MY_TYPE = "ChartFolio";
	private static final String SHOW_NAMES = "SHOW_NAMES";
	java.awt.Color _theColor;
	boolean _showNames;

	/**
	 * class which contains list of textual representations of scale locations
	 */
	static ETOPOPainter.KeyLocationPropertyEditor lp = new ETOPOPainter.KeyLocationPropertyEditor();

	public ChartFolioHandler(Layers theLayers)
	{
		// inform our parent what type of class we are
		super(theLayers, MY_TYPE);

		addAttributeHandler(new HandleBooleanAttribute(SHOW_NAMES)
		{
			public void setValue(String name, boolean value)
			{
				_showNames = value;
			}
		});

		addHandler(new ColourHandler()
		{
			public void setColour(java.awt.Color color)
			{
				_theColor = color;
			}
		});

	}
	
	@Override
	protected BaseLayer getLayer()
	{
		return new ChartFolio(_showNames, _theColor);
	}

	public void elementClosed()
	{
		// set our specific attributes
		ChartFolio wrapper = (ChartFolio) _myLayer;
		wrapper.setShowNames(_showNames);
		wrapper.setLineColor(_theColor);
		
		// and store it, just like our parent does
		super.elementClosed();
	}

	public static void exportThisFolio(ChartFolio folio, Element parent,
			Document doc)
	{
		// check our exporters
		checkExporters();

		org.w3c.dom.Element eLayer = doc.createElement(MY_TYPE);

		eLayer.setAttribute("Name", folio.getName());
		eLayer.setAttribute("Visible", writeThis(folio.getVisible()));
		eLayer.setAttribute("LineThickness", writeThis(folio.getLineThickness()));

		// step through the components of the layer
		java.util.Enumeration<Editable> enumer = folio.elements();
		while (enumer.hasMoreElements())
		{
			MWC.GUI.Plottable nextPlottable = (MWC.GUI.Plottable) enumer
					.nextElement();

			exportThisItem(nextPlottable, eLayer, doc);
		}

		// and our specific attributes
		eLayer.setAttribute(SHOW_NAMES, writeThis(folio.isShowNames()));
		ColourHandler.exportColour(folio.getLineColor(), eLayer, doc);

		parent.appendChild(eLayer);

		
	}

}