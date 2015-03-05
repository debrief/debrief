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

import org.mwc.cmap.naturalearth.view.NEFeatureRoot;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.Utilities.ReaderWriter.XML.LayerHandlerExtension;

public class NELayerHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader implements LayerHandlerExtension
{

	public static final String TYPE = "NaturalEarth";
	public static final String NAME = "Name";
	public static final String VIS = "Visible";
	
	private NEFeatureRoot _myStore;
	private Layers _theLayers;
	
	private boolean _isVis;
	private String _myName;
	
	public NELayerHandler()
	{
		this(TYPE);
	}
	
	public NELayerHandler(String theType)
	{
		// inform our parent what type of class we are
		super(theType);
		
		//_theLayers = theLayers;

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(final String name, final String val)
			{
				_myName = val;
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VIS)
		{
			public void setValue(final String name, final boolean val)
			{
				_isVis = val;
			}
		});
		
		addHandler(new NEFeatureRootHandler()
		{
			@Override
			public void addStore(NEFeatureRoot store)
			{
				_myStore = store;
			}
		});
	
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		//_myStore.setName(_myName);
		NELayer nel = new NELayer(_myStore);
		nel.setVisible(_myStore.getVisible());
		_theLayers.addThisLayer(nel);
		_myStore = null;
		_myName = null;
	}

//	public static void exportLayer(final NELayer layer,
//			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
//	{
//			final Element eStore = doc.createElement(TYPE);		
//			NEFeatureRoot store = layer.getStore();	
//			eStore.appendChild(NEFeatureRootHandler.exportStore(store, doc));
//			parent.appendChild(eStore);			
//	}
	
	@Override
	public void setLayers(Layers theLayers)
	{
		_theLayers = theLayers;
	}

	@Override
	public boolean canExportThis(Layer subject)
	{
		return subject instanceof NELayer;
	}

	@Override
	public void exportThis(Layer theLayer, Element parent, Document doc)
	{
		NELayer neLayer = (NELayer) theLayer;
		Element neStyle = doc.createElement(TYPE);
		//neStyle.setAttribute(NAME, neLayer.getName());
		//neStyle.setAttribute(NELayerHandler.VIS, writeThis(neLayer.getVisible()));
		parent.appendChild(neStyle);
		
		NEFeatureRoot store = neLayer.getStore();
		Element neStoreElement = NEFeatureRootHandler.exportStore(store, doc);
		neStyle.appendChild(neStoreElement);
	}
		
}