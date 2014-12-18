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

import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

public class NELayerHandler extends MWCXMLReader implements LayerHandlerExtension
{
	private static final String MY_TYPE = "natural_earth";

	private static final String NAME = "NAME";

	private String _name;

	private Layers _theLayers;

	public NELayerHandler()
	{
		this(MY_TYPE);
	}

	public NELayerHandler(String theType)
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
	}

	public void elementClosed()
	{
		NELayer newL = new NELayer(_name, null);
		_theLayers.addThisLayer(newL);
	}


	@Override
	public void setLayers(Layers theLayers)
	{
		_theLayers = theLayers;
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return (subject instanceof NELayer);
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
		@SuppressWarnings("unused")
		NELayer solution = (NELayer) theLayer;

		// TODO: handle the export

	}

}