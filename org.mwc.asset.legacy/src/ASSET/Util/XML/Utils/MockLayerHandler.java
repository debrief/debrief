
package ASSET.Util.XML.Utils;

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

import Debrief.ReaderWriter.XML.DebriefLayerHandler;
import MWC.GUI.BaseLayer;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class MockLayerHandler extends MWCXMLReader {
	public static void exportLocation(final MWC.GenericData.WorldLocation loc, final String title,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eLoc = doc.createElement(title);
		// for now, stick with exporting locations in short form
		ASSETShortLocationHandler.exportLocation(loc, eLoc, doc);
		parent.appendChild(eLoc);
	}

	BaseLayer _res = null;

	public MockLayerHandler(final String elementName) {
		// inform our parent what type of class we are
		super(elementName);

		// and add the layer handler...
		addHandler(new DebriefLayerHandler(null) {
			@Override
			public void elementClosed() {
				// pass on the layer
				setLayer(_myLayer);
				// and empty it
				_myLayer = null;
			}

		});
	}

	abstract public void setLayer(BaseLayer theLayer);

}