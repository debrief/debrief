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

import org.mwc.cmap.naturalearth.view.NEFeatureStore;
import org.mwc.cmap.naturalearth.wrapper.NELayer;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Layers;

abstract public class NELayerHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader 
{

	public static final String TYPE = "NaturalEarth";
	public final String NAME = "Name";
	public final String VIS = "VISIBLE";
	
	private NEFeatureStore _myStore;
	final private Layers _theLayers;
	
	private boolean _isVis;
	
	public NELayerHandler(Layers theLayers)
	{
		// inform our parent what type of class we are
		super(TYPE);
		
		_theLayers = theLayers;

		addAttributeHandler(new HandleAttribute(NAME)
		{
			public void setValue(final String name, final String val)
			{
				_myStore.setName(val);
			}
		});
		addAttributeHandler(new HandleBooleanAttribute(VIS)
		{
			public void setValue(final String name, final boolean val)
			{
				_isVis = val;
			}
		});
		
		addHandler(new NEFeatureStoreHandler()
		{
			@Override
			public void addStore(NEFeatureStore store)
			{
				_myStore = store;
			}
		});
	
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name, final Attributes attributes)
	{
		_myStore = new NEFeatureStore();

		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		NELayer nel = new NELayer(_myStore);
		nel.setVisible(_isVis);
		_theLayers.addThisLayer(nel);
		_myStore = null;
	}

	public static void exportLayer(final NELayer layer,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc)
	{
			final Element eStore = doc.createElement(TYPE);		
			NEFeatureStore store = layer.getStore();	
			eStore.appendChild(NEFeatureStoreHandler.exportStore(store, doc));
			parent.appendChild(eStore);			
	}
		
}