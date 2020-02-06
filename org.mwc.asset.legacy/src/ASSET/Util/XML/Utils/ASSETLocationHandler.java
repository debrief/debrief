
package ASSET.Util.XML.Utils;

import org.xml.sax.Attributes;

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

import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

abstract public class ASSETLocationHandler extends MWCXMLReader {

	public static void exportLocation(final MWC.GenericData.WorldLocation loc, final String title,
			final org.w3c.dom.Element parent, final org.w3c.dom.Document doc) {
		final org.w3c.dom.Element eLoc = doc.createElement(title);
		// for now, stick with exporting locations in short form
		ASSETShortLocationHandler.exportLocation(loc, eLoc, doc);
		parent.appendChild(eLoc);
	}

	MWC.GenericData.WorldLocation _res = null;

	public ASSETLocationHandler(final String name) {
		// inform our parent what type of class we are
		super(name);

		addHandler(new ASSETShortLocationHandler() {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_res = res;
			}
		});
		addHandler(new ASSETLongLocationHandler() {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_res = res;
			}
		});
		addHandler(new ASSETRelativeLocationHandler() {
			@Override
			public void setLocation(final MWC.GenericData.WorldLocation res) {
				_res = res;
			}
		});

	}

	@Override
	public void elementClosed() {
		// pass on to the listener class
		setLocation(_res);
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		// ignore attributes
	}

	abstract public void setLocation(MWC.GenericData.WorldLocation res);

}