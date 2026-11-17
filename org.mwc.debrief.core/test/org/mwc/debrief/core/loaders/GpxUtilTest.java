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

package org.mwc.debrief.core.loaders;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mwc.debrief.core.loaders.GpxUtil.*;
import org.mwc.debrief.core.loaders.GpxUtil.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.mwc.cmap.core.CorePlugin;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import MWC.Utilities.ReaderWriter.XML.SafeXMLFactory;

public class GpxUtilTest {

	private static String asString(final Source source) throws Exception {
		final StringWriter out = new StringWriter();
		TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(out));
		return out.toString();
	}

	@Test
	public void testExternalEntityNotResolved() throws Exception {
		final File secret = File.createTempFile("gpx_secret", ".txt");
		try {
			try (FileWriter fw = new FileWriter(secret)) {
				fw.write("TOP_SECRET");
			}
			final String gpx = "<?xml version=\"1.0\"?>\n<!DOCTYPE gpx [<!ENTITY xxe SYSTEM \"" + secret.toURI()
					+ "\">]>\n<gpx version=\"1.1\" creator=\"x\" xmlns=\"http://www.topografix.com/GPX/1/1\">"
					+ "<trk><name>&xxe;</name></trk></gpx>";
			String parsed = "";
			try {
				parsed = asString(
						getDocumentSource(new ByteArrayInputStream(gpx.getBytes(StandardCharsets.UTF_8))));
			} catch (final Exception e) {
				// fine, DOCTYPE rejected
			}
			assertFalse(parsed.contains("TOP_SECRET"), "external entity must not be resolved: " + parsed);
		} finally {
			secret.delete();
		}
	}

	@Test
	public void testValidGpxStillParses() throws Exception {
		final Source source = getDocumentSource(GpxUtil.class.getResourceAsStream("gpx-1.1-data.xml"));
		assertFalse(isGpx10(source));
		assertTrue(isValid(source, false));
	}
}
