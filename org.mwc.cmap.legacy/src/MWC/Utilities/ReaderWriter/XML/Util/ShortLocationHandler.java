
package MWC.Utilities.ReaderWriter.XML.Util;

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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;

import MWC.GUI.Dialogs.DialogFactory;
import MWC.GenericData.WorldLocation;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;
import junit.framework.TestCase;

abstract public class ShortLocationHandler extends MWCXMLReader {

	public static class ShortLocationTest extends TestCase {
		private static List<WorldLocation> read(final String element, final MWCXMLReaderWriter rw) {
			DialogFactory.setRunHeadless(true);
			final List<WorldLocation> res = new ArrayList<WorldLocation>();
			final ShortLocationHandler handler = new ShortLocationHandler() {
				@Override
				public void setLocation(final WorldLocation loc) {
					res.add(loc);
				}
			};
			final String doc = "<?xml version=\"1.0\"?>\n" + element;
			rw.importThis("test.dpf", new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8)), handler);
			return res;
		}

		public void testBadLatitudeSkipped() {
			final MWCXMLReaderWriter rw = new MWCXMLReaderWriter();
			final List<WorldLocation> locs = read("<shortLocation Lat=\"N50.5\" Long=\"-1.2\" Depth=\"0\"/>", rw);
			assertEquals("no location at 0,0", 0, locs.size());
			assertEquals("problem reported", 1, rw.getImportProblems().size());
		}

		public void testGoodLocation() {
			final MWCXMLReaderWriter rw = new MWCXMLReaderWriter();
			final List<WorldLocation> locs = read("<shortLocation Lat=\"5.0E1\" Long=\"-1,5\" Depth=\"12\"/>", rw);
			assertEquals(1, locs.size());
			assertEquals(50, locs.get(0).getLat(), 0);
			assertEquals(-1.5, locs.get(0).getLong(), 0);
			assertEquals(12, locs.get(0).getDepth(), 0);
			assertTrue(rw.getImportProblems().isEmpty());
		}
	}

	public static void exportLocation(final MWC.GenericData.WorldLocation loc, final org.w3c.dom.Element parent,
			final org.w3c.dom.Document doc) {
		final Element eLoc = doc.createElement("shortLocation");
		eLoc.setAttribute("Lat", writeThisLong(loc.getLat()));
		eLoc.setAttribute("Long", writeThisLong(loc.getLong()));
		eLoc.setAttribute("Depth", writeThis(loc.getDepth()));
		parent.appendChild(eLoc);
	}

	private double _lat;
	private double _long;

	private double _depth;

	public ShortLocationHandler() {
		// inform our parent what type of class we are
		super("shortLocation");

	}

	/**
	 * whether the lat/long couldn't be read, in which case we don't produce a
	 * location (rather than one at 0,0)
	 */
	private boolean _invalid;

	@Override
	public void elementClosed() {
		if (_invalid) {
			// the problem has already been reported
			return;
		}
		final MWC.GenericData.WorldLocation res = new MWC.GenericData.WorldLocation(_lat, _long, _depth);
		setLocation(res);
	}

	// this is one of ours, so get on with it!
	@Override
	protected void handleOurselves(final String name, final Attributes attributes) {
		// initialise data
		_lat = _long = _depth = 0.0;
		_invalid = false;

		final int len = attributes.getLength();
		for (int i = 0; i < len; i++) {

			final String nm = attributes.getQName(i);// getLocalName(i);
			final String val = attributes.getValue(i);
			try {
				if (nm.equals("Lat"))
					_lat = readThisDouble(val);
				else if (nm.equals("Long"))
					_long = readThisDouble(val);
				else if (nm.equals("Depth"))
					_depth = readThisDouble(val);
			} catch (final java.text.ParseException e) {
				if (nm.equals("Depth")) {
					reportProblem("Couldn't read Depth of location: \"" + val + "\", using zero", e);
				} else {
					_invalid = true;
					reportProblem("Couldn't read " + nm + " of location: \"" + val + "\", location skipped", e);
				}
			}
		}
	}

	abstract public void setLocation(MWC.GenericData.WorldLocation res);

}