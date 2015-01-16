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
package org.mwc.cmap.naturalearth.readerwriter;

/**
 * Title:        Debrief 2000
 * Description:  Debrief 2000 Track Analysis Software
 * Copyright:    Copyright (c) 2000
 * Company:      MWC
 * @author Ian Mayo
 * @version 1.0
 */

import java.awt.Color;

import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.Utilities.ReaderWriter.XML.Util.ColourHandler;
import MWC.Utilities.ReaderWriter.XML.Util.FontHandler;

abstract public class NEFeatureStyleHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	public static final String TYPE = "NEFeature";
	public static final String POLY_FILL = "PolyFill";
	public static final String LINE_COL = "LineCol";
	public static final String TEXT_COL = "TextCol";
	
	public static final String SHOW_POLY = "ShowPoly";
	public static final String SHOW_LINE = "ShowLine";
	public static final String SHOW_POINT = "ShowPoint";
	public static final String SHOW_LABEL = "ShowLabel";
	
	public static final String FOLDER_NAME = "FolderName";
	public static final String FILE_NAME = "FileName";


	NEFeatureStyle _style;

	public NEFeatureStyleHandler()
	{
		// inform our parent what type of class we are
		super(TYPE);

		addAttributeHandler(new HandleAttribute(FOLDER_NAME)
		{
			public void setValue(String name, String value)
			{
				_style.setFolderName(value);
			}
		});
		addAttributeHandler(new HandleAttribute(FILE_NAME)
		{
			public void setValue(String name, String value)
			{
				_style.setFileName(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_POLY)
		{
			public void setValue(String name, boolean value)
			{
				_style.setShowPolygons(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_LINE)
		{
			public void setValue(String name, boolean value)
			{
				_style.setShowLines(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_POINT)
		{
			public void setValue(String name, boolean value)
			{
				_style.setShowPoints(value);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(SHOW_LABEL)
		{
			public void setValue(String name, boolean value)
			{
				_style.setShowLabels(value);
			}
		});
		addHandler(new ColourHandler(POLY_FILL)
		{
			@Override
			public void setColour(Color res)
			{
				_style.setPolygonColor(res);
			}
		});
		addHandler(new ColourHandler(LINE_COL)
		{
			@Override
			public void setColour(Color res)
			{
				_style.setLineColor(res);
			}
		});
		addHandler(new ColourHandler(TEXT_COL)
		{
			@Override
			public void setColour(Color res)
			{
				_style.setTextColor(res);
			}
		});
		addHandler(new FontHandler()
		{
			public void setFont(final java.awt.Font font)
			{
				_style.setTextFont(font);
			}
		});

	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name,
			final Attributes attributes)
	{
		_style = new NEFeatureStyle();

		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		addStyle(_style);
		_style = null;
	}

	abstract public void addStyle(NEFeatureStyle style);

	public static void exportStyle(NEFeatureStyle style,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
		final Element eStyle = doc.createElement(TYPE);

		eStyle.setAttribute(NELayerHandler.VIS, writeThis(style.getVisible()));
		eStyle.setAttribute(FILE_NAME, style.getLocalFileName());
		eStyle.setAttribute(FOLDER_NAME, style.getFolderName());
    eStyle.setAttribute(SHOW_POLY, writeThis(style.isShowPolygons()));
    eStyle.setAttribute(SHOW_LINE, writeThis(style.isShowLines()));
    eStyle.setAttribute(SHOW_POINT, writeThis(style.isShowPoints()));
    eStyle.setAttribute(SHOW_LABEL, writeThis(style.isShowLabels()));

		// do the colors
		MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(
				style.getPolygonColor(), eStyle, doc, POLY_FILL);
		MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(
				style.getLineColor(), eStyle, doc, LINE_COL);
		MWC.Utilities.ReaderWriter.XML.Util.ColourHandler.exportColour(
				style.getTextColor(), eStyle, doc, TEXT_COL);

		// and the font
		FontHandler.exportFont(style.getFont(), eStyle, doc);

		parent.appendChild(eStyle);

	}

}