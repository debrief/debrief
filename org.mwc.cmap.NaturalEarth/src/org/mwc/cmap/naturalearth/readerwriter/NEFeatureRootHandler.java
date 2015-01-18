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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureRoot;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Editable;

abstract public class NEFeatureRootHandler extends
		MWC.Utilities.ReaderWriter.XML.MWCXMLReader
{

	public static final String TYPE = "NEStore";
	public static final String NAME = "Name";
	public static final String VIS = "Visible";
	
	private NEFeatureRoot _myStore;

	public NEFeatureRootHandler()
	{
		// inform our parent what type of class we are
		super(TYPE);

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
				_myStore.setVisible(val);
			}
		});

		addHandler(new NEFeatureGroupHandler()
		{
			public void addGroup(NEFeatureGroup res)
			{
				_myStore.add(res);
			}
		});
		addHandler(new NEFeatureStyleHandler()
		{
			public void addStyle(NEFeatureStyle res)
			{
				_myStore.add(res);
			}
		});
	}

	// this is one of ours, so get on with it!
	protected final void handleOurselves(final String name,
			final Attributes attributes)
	{
		_myStore = new NEFeatureRoot(name);

		super.handleOurselves(name, attributes);
	}

	public final void elementClosed()
	{
		addStore(_myStore);
		_myStore = null;
	}

	public static Element exportStore(final NEFeatureRoot store,
			final org.w3c.dom.Document doc)
	{

		final Element eStore = doc.createElement(TYPE);
		
		eStore.setAttribute(NAME, store.getName());
		eStore.setAttribute(NELayerHandler.VIS, writeThis(store.getVisible()));

		// loop through layers
		Enumeration<Editable> iter = store.elements();
		while (iter.hasMoreElements())
		{
			Editable next = (Editable) iter.nextElement();
			if (next instanceof NEFeatureGroup)
			{
				NEFeatureGroup res = (NEFeatureGroup) next;
				NEFeatureGroupHandler.exportGroup(res, eStore, doc);
			}
			else
			{
				NEFeatureStyle res = (NEFeatureStyle) next;
				NEFeatureStyleHandler.exportStyle(res, eStore, doc);
			}
		}
		return eStore;
	}

	abstract public void addStore(NEFeatureRoot store);

	public static String encodeAsXML(NEFeatureRoot store)
	{
		String res = null;
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.newDocument();
			Element eStore = NEFeatureRootHandler.exportStore(store, doc);
			// ok, now convert parent to text
			final TransformerFactory tF = TransformerFactory.newInstance();
			Transformer tr;
			tr = tF.newTransformer();

			tr.setOutputProperty(OutputKeys.INDENT, "yes");

			OutputStream output = new OutputStream()
			{
				private StringBuilder string = new StringBuilder();

				@Override
				public void write(int b) throws IOException
				{
					this.string.append((char) b);
				}

				// Netbeans IDE automatically overrides this toString()
				public String toString()
				{
					return this.string.toString();
				}
			};
			final DOMSource source = new DOMSource(eStore);
			final StreamResult result = new StreamResult(output);
			tr.transform(source, result);

			res = output.toString();
		}
		catch (TransformerConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (TransformerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}
}