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

package MWC.TacticalData;

import static org.junit.jupiter.api.Assertions.*;

import static MWC.TacticalData.NarrativeWrapper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import MWC.TacticalData.NarrativeWrapper.*;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.AbstractCollection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import MWC.GUI.Editable;
import MWC.GUI.GriddableSeriesMarker;
import MWC.GUI.Layer;
import MWC.GUI.Layers;
import MWC.GUI.Plottable;
import MWC.GUI.TimeStampedDataItem;
import MWC.GenericData.HiResDate;
import MWC.GenericData.TimePeriod;
import MWC.Utilities.ReaderWriter.ImportManager;
import MWC.Utilities.ReaderWriter.XML.LayerHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;
import MWC.Utilities.ReaderWriter.XML.Util.NarrativeHandler;
import MWC.Utilities.TextFormatting.GMTDateFormat;

public class NarrativeWrapperTest {

	/**
	 * note: the following test was produced to overcome a specific issue. The
	 * narrative wrapper was successfully ignoring duplicate entries, except after a
	 * file had been save/restored.
	 *
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws SAXException
	 */
	@Test
	public void testDuplicates() throws ParserConfigurationException, TransformerException, SAXException {
		final NarrativeWrapper narr = new NarrativeWrapper("Some title");
		assertEquals(0, narr.size(), "empty");

		final NarrativeEntry n1 = new NarrativeEntry("track", new HiResDate(3000), "some entry");
		narr.add(n1);

		assertEquals(1, narr.size(), "has one");

		final NarrativeEntry n2 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n2);

		assertEquals(2, narr.size(), "has two");

		final NarrativeEntry n3 = new NarrativeEntry("track", new HiResDate(3100), "some entry");
		narr.add(n3);

		assertEquals(2, narr.size(), "still has two");

		// make tiny change
		final NarrativeEntry n4 = new NarrativeEntry("track", new HiResDate(3100), "some entry.");
		narr.add(n4);

		assertEquals(3, narr.size(), "now has three");

		// hmm, we need to export then reload, to check for matching hashcode
		final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		final org.w3c.dom.Element plt = doc.createElement("narrative");
		plt.setAttribute("Name", LayerHandler.NARRATIVE_LAYER);
		doc.appendChild(plt);
		NarrativeHandler.EntryHandler.exportEntry(n4, plt, doc);

		// output to String
		final TransformerFactory tf = TransformerFactory.newInstance();
		final Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		final StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(doc), new StreamResult(writer));
		final String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

		// ok, put it into an input strean
		final InputStream stream = new ByteArrayInputStream(output.getBytes(StandardCharsets.UTF_8));

		// ok, re-import it
		final Layers parent = new Layers();
		final NarrativeHandler handler = new NarrativeHandler(parent);

		MWCXMLReaderWriter.importThis(handler, "some name", stream);

		// get the contents
		final NarrativeWrapper narrLayer = (NarrativeWrapper) parent.findLayer(LayerHandler.NARRATIVE_LAYER);

		final NarrativeEntry theEntry = (NarrativeEntry) narrLayer.elements().nextElement();

		narr.add(theEntry);

		assertEquals(3, narr.size(), "still has three");

	}

	@Test
	public void testGridding() {
		final NarrativeWrapper narrative = new NarrativeWrapper("Test narrative");
		final NarrativeEntry entry = new NarrativeEntry("some track", new HiResDate(2000000), "Some entry text");

		narrative.add(entry);

		assertEquals(1, narrative.size(), "correct size");

		final NarrativeEntry dummy = (NarrativeEntry) narrative.makeCopy(entry);
		assertEquals(dummy.getDTG(), entry.getDTG());
		assertEquals(dummy.getColor(), entry.getColor());
		assertEquals(entry.getSource(), dummy.getSource());
		assertEquals(entry.getTrackName(), dummy.getTrackName());
		assertEquals(entry.getVisible(), dummy.getVisible());

		// we force the entry to be different, since identical copies get silently
		// dropped.
		assertFalse(entry.getEntry().equals(dummy.getEntry()));

		narrative.add(dummy);
		assertEquals(2, narrative.size(), "correct size");

		// try to delete the enty
		narrative.removeElement(entry);
		assertEquals(1, narrative.size(), "correct size");

	}
}
