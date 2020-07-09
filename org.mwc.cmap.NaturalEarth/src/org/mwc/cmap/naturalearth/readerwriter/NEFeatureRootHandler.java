
package org.mwc.cmap.naturalearth.readerwriter;

/*******************************************************************************
 * Debrief - the Open Source Maritime Analysis Application
 * http://debrief.info
 *
 * (C) 2000-2020, Deep Blue C Technology Ltd
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html)
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *******************************************************************************/

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

abstract public class NEFeatureRootHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {

	public static final String TYPE = "NEStore";
	public static final String NAME = "Name";
	public static final String VIS = "Visible";

	public static String encodeAsXML(final NEFeatureRoot store) {
		String res = null;
		try {
			final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			final Element eStore = NEFeatureRootHandler.exportStore(store, doc);
			// ok, now convert parent to text
			final TransformerFactory tF = TransformerFactory.newInstance();
			Transformer tr;
			tr = tF.newTransformer();

			tr.setOutputProperty(OutputKeys.INDENT, "yes");

			final OutputStream output = new OutputStream() {
				private final StringBuilder string = new StringBuilder();

				// Netbeans IDE automatically overrides this toString()
				@Override
				public String toString() {
					return this.string.toString();
				}

				@Override
				public void write(final int b) throws IOException {
					this.string.append((char) b);
				}
			};
			final DOMSource source = new DOMSource(eStore);
			final StreamResult result = new StreamResult(output);
			tr.transform(source, result);

			res = output.toString();
		} catch (final TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public static Element exportStore(final NEFeatureRoot store, final org.w3c.dom.Document doc) {

		final Element eStore = doc.createElement(TYPE);

		eStore.setAttribute(NAME, store.getName());
		eStore.setAttribute(NELayerHandler.VIS, writeThis(store.getVisible()));

		// loop through layers
		final Enumeration<Editable> iter = store.elements();
		while (iter.hasMoreElements()) {
			final Editable next = iter.nextElement();
			if (next instanceof NEFeatureGroup) {
				final NEFeatureGroup res = (NEFeatureGroup) next;
				NEFeatureGroupHandler.exportGroup(res, eStore, doc);
			} else {
				final NEFeatureStyle res = (NEFeatureStyle) next;
				NEFeatureStyleHandler.exportStyle(res, eStore, doc);
			}
		}
		return eStore;
	}

	private NEFeatureRoot _myStore;

	public NEFeatureRootHandler() {
		// inform our parent what type of class we are
		super(TYPE);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String val) {
				_myStore.setName(val);
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(VIS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_myStore.setVisible(val);
			}
		});

		addHandler(new NEFeatureGroupHandler() {
			@Override
			public void addGroup(final NEFeatureGroup res) {
				_myStore.add(res);
			}
		});
		addHandler(new NEFeatureStyleHandler() {
			@Override
			public void addStyle(final NEFeatureStyle res) {
				_myStore.add(res);
			}
		});
	}

	abstract public void addStore(NEFeatureRoot store);

	@Override
	public final void elementClosed() {
		addStore(_myStore);
		_myStore = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected final void handleOurselves(final String name, final Attributes attributes) {
		_myStore = new NEFeatureRoot(name);

		super.handleOurselves(name, attributes);
	}
}