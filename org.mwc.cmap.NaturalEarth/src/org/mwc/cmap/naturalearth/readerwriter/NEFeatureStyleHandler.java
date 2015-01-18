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

import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

abstract public class NEFeatureStyleHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{
	public static final String TYPE = "NEFeature";
	public static final String NAME = "Name";
	public static final String VIS = "Visible";
	
	private NEFeatureStyle _style;

	public NEFeatureStyleHandler()
	{
		// inform our parent what type of class we are
		super(TYPE);

		addAttributeHandler(new HandleBooleanAttribute(VIS)
		{
			public void setValue(String name, boolean value)
			{
				_style.setVisible(value);
			}
		});
		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(String name, String value)
			{
				_style.setName(value);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name,
			final Attributes attributes)
	{
		_style = new NEFeatureStyle(name);

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

		eStyle.setAttribute(VIS, writeThis(style.getVisible()));
		eStyle.setAttribute(NAME, style.getName());
		parent.appendChild(eStyle);

	}

}