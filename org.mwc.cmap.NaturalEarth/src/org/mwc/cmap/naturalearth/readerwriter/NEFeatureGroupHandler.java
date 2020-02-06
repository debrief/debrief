
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

import java.util.Enumeration;

import org.mwc.cmap.naturalearth.view.NEFeatureGroup;
import org.mwc.cmap.naturalearth.view.NEFeatureStyle;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Editable;

abstract public class NEFeatureGroupHandler extends MWC.Utilities.ReaderWriter.XML.MWCXMLReader {
	private static final String TYPE = "NEGroup";

	public static final String NAME = "Name";
	public static final String VIS = "Visible";

	public static void exportGroup(final NEFeatureGroup group, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final Element eGroup = doc.createElement(TYPE);
		eGroup.setAttribute(NELayerHandler.VIS, writeThis(group.getVisible()));
		if (group.getName() != null)
			eGroup.setAttribute(NAME, group.getName());

		final Enumeration<Editable> iter = group.elements();
		while (iter.hasMoreElements()) {
			final Editable next = iter.nextElement();
			if (next instanceof NEFeatureStyle) {
				NEFeatureStyleHandler.exportStyle((NEFeatureStyle) next, eGroup, doc);
			} else if (next instanceof NEFeatureGroup) {
				NEFeatureGroupHandler.exportGroup((NEFeatureGroup) next, eGroup, doc);
			}
		}

		parent.appendChild(eGroup);
	}

	String _name;

	boolean _isVis;

	protected NEFeatureGroup _list;

	public NEFeatureGroupHandler() {
		// inform our parent what type of class we are
		this(TYPE);
	}

	public NEFeatureGroupHandler(final String type) {
		// inform our parent what type of class we are
		super(type);

		addAttributeHandler(new HandleAttribute(NAME) {
			@Override
			public void setValue(final String name, final String value) {
				_name = value;
			}
		});

		addAttributeHandler(new HandleBooleanAttribute(VIS) {
			@Override
			public void setValue(final String name, final boolean val) {
				_isVis = val;
			}
		});

		addHandler(new NEFeatureStyleHandler() {
			@Override
			public void addStyle(final NEFeatureStyle style) {
				_list.add(style);
			}
		});
	}

	abstract public void addGroup(NEFeatureGroup group);

	protected NEFeatureGroup createGroup() {
		return new NEFeatureGroup("pending feature");
	}

	@Override
	public void elementClosed() {
		_list.setVisible(_isVis);
		addGroup(_list);
		_list = null;
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		_list = createGroup();

		super.handleOurselves(name, attributes);
	}
}