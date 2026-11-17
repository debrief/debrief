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

package MWC.Utilities.ReaderWriter.XML;

import static MWC.Utilities.ReaderWriter.XML.SafeXMLFactory.*;
import static org.junit.jupiter.api.Assertions.*;

import MWC.Utilities.ReaderWriter.XML.SafeXMLFactory.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import MWC.Utilities.ReaderWriter.PlainImporter;

public class SafeXMLFactoryTest {

	private static String xxeDoc(final File secret, final String root, final String body) {
		return "<?xml version=\"1.0\"?>\n<!DOCTYPE " + root + " [<!ENTITY xxe SYSTEM \"" + secret.toURI()
				+ "\">]>\n" + body;
	}

	private File secret;

	@BeforeEach
	public void setUp() throws Exception {
		secret = File.createTempFile("xxe_secret", ".txt");
		secret.deleteOnExit();
		try (FileWriter fw = new FileWriter(secret)) {
			fw.write("TOP_SECRET");
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		secret.delete();
	}

	@Test
	public void testDOMRejectsExternalEntity() throws Exception {
		final DocumentBuilder builder = newDocumentBuilder();
		final String doc = xxeDoc(secret, "kml", "<kml><name>&xxe;</name></kml>");
		try {
			final org.w3c.dom.Document res = builder.parse(new InputSource(new StringReader(doc)));
			assertFalse(res.getDocumentElement().getTextContent().contains("TOP_SECRET"),
					"entity must not be expanded");
			fail("DOCTYPE should have been rejected");
		} catch (final SAXException e) {
			// expected
		}
	}

	@Test
	public void testDOMStillReadsNormalXml() throws Exception {
		final org.w3c.dom.Document res = newDocumentBuilder()
				.parse(new InputSource(new StringReader("<a><b>text</b></a>")));
		assertEquals("text", res.getDocumentElement().getTextContent());
	}

	@Test
	public void testSAXImportRejectsExternalEntity() throws Exception {
		// note: external entities can't appear in attribute values, but they can
		// in element content (e.g. narrative entries)
		final String doc = xxeDoc(secret, "plot", "<plot>&xxe;</plot>");
		final StringBuilder captured = new StringBuilder();
		final MWCXMLReader handler = new MWCXMLReader("plot") {
			@Override
			public void characters(final char[] ch, final int start, final int length) throws SAXException {
				captured.append(ch, start, length);
			}
		};
		try {
			MWCXMLReaderWriter.importThis(handler, "test.dpf",
					new ByteArrayInputStream(doc.getBytes(StandardCharsets.UTF_8)));
			fail("DOCTYPE should have been rejected");
		} catch (final PlainImporter.ImportException e) {
			// expected - DOCTYPE rejected
		}
		assertFalse(captured.toString().contains("TOP_SECRET"),
				"external entity must not be resolved: " + captured);
	}

	@Test
	public void testSAXParserRejectsDoctype() throws Exception {
		final String doc = xxeDoc(secret, "plot", "<plot Name=\"&xxe;\"/>");
		final StringBuilder captured = new StringBuilder();
		try {
			newSAXParser().parse(new InputSource(new StringReader(doc)), new org.xml.sax.helpers.DefaultHandler() {
				@Override
				public void startElement(final String uri, final String localName, final String qName,
						final Attributes attributes) throws SAXException {
					captured.append(attributes.getValue("Name"));
				}
			});
			fail("DOCTYPE should have been rejected");
		} catch (final SAXException e) {
			// expected
		}
		assertEquals("", captured.toString());
	}

	@Test
	public void testValidatorRejectsExternalEntity() throws Exception {
		final String xsd = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
				+ "<xs:element name=\"a\" type=\"xs:string\"/></xs:schema>";
		final Schema schema = newSchemaFactory().newSchema(new StreamSource(new StringReader(xsd)));
		final Validator validator = newValidator(schema);
		final String doc = xxeDoc(secret, "a", "<a>&xxe;</a>");
		try {
			validator.validate(new StreamSource(new StringReader(doc)));
			fail("external entity should have been refused");
		} catch (final SAXException e) {
			// expected
		}
	}
}
