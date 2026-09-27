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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import MWC.Utilities.ReaderWriter.XML.SafeXMLFactory;
import junit.framework.TestCase;

public final class GpxUtil {
	public static class TestGpxXXE extends TestCase {

		private static String asString(final Source source) throws Exception {
			final StringWriter out = new StringWriter();
			TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(out));
			return out.toString();
		}

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
				assertFalse("external entity must not be resolved: " + parsed, parsed.contains("TOP_SECRET"));
			} finally {
				secret.delete();
			}
		}

		public void testValidGpxStillParses() throws Exception {
			final Source source = getDocumentSource(GpxUtil.class.getResourceAsStream("gpx-1.1-data.xml"));
			assertFalse(isGpx10(source));
			assertTrue(isValid(source, false));
		}
	}

	private static final class DirectoryCollector implements Runnable {
		private String selectedFolder = null;

		public String getSelectedFolder() {
			return selectedFolder;
		}

		@Override
		public void run() {
			final DirectoryDialog dlg = new DirectoryDialog(Display.getDefault().getActiveShell());

			// Change the title bar text
			dlg.setText("Export to GPS");

			// Customizable message displayed in the dialog
			dlg.setMessage("Select a directory to save the exported GPS file");

			// Calling open() will open and run the dialog.
			// It will return the selected directory, or
			// null if user cancels
			selectedFolder = dlg.open();
		}
	}

	private static final SchemaFactory FACTORY = SafeXMLFactory.newSchemaFactory();
	private static Schema GPX_1_0_SCHEMA;

	private static Schema GPX_1_1_SCHEMA;

	static {
		try {
			GPX_1_0_SCHEMA = FACTORY.newSchema(new StreamSource(GpxUtil.class.getResourceAsStream("gpx_1.0.xsd")));
			GPX_1_1_SCHEMA = FACTORY.newSchema(new StreamSource(GpxUtil.class.getResourceAsStream("gpx_1.1.xsd")));
		} catch (final SAXException e) {
			throw new IllegalStateException(
					"Unable to load GPX schema. Cannot perform validation of imported documents", e);
		}
	}

	public static String collectDirecotryPath() {
		final DirectoryCollector collector = new DirectoryCollector();
		Display.getDefault().syncExec(collector);

		return collector.getSelectedFolder();
	}

	/**
	 * parse the GPX file into a DOM. The parser is hardened against XML External
	 * Entity attacks: DOCTYPE declarations are rejected and external entities are
	 * never resolved.
	 */
	public static DOMSource getDocumentSource(final InputStream gpxStream)
			throws SAXException, IOException, ParserConfigurationException {
		final Document document = SafeXMLFactory.newDocumentBuilder(true).parse(gpxStream);
		return new DOMSource(document);
	}

	public static boolean isGpx10(final Source source) {
		final Document document = (Document) ((DOMSource) source).getNode();
		if ("1.0".equals(document.getDocumentElement().getAttribute("version"))) {
			return true;
		}
		return false;
	}

	public static boolean isValid(final File f) throws SAXException, IOException {
		Validator validator;

		if (GPX_1_0_SCHEMA == null) {
			throw new IllegalStateException(
					"Unable to load GPX 1.0 schema. Cannot perform validation of imported documents");
		}
		validator = SafeXMLFactory.newValidator(GPX_1_0_SCHEMA);

		try {
			validator.validate(new StreamSource(f));
			return true;
		} catch (final SAXException ex) {
			CorePlugin.logError(IStatus.ERROR, "GPX failed validation. Reason: " + ex.getMessage(), ex);
			CorePlugin.errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}

	public static boolean isValid(final Source source, final boolean isGpx10) throws SAXException, IOException {
		Validator validator;

		if (isGpx10) {
			if (GPX_1_0_SCHEMA == null) {
				throw new IllegalStateException(
						"Unable to load GPX 1.0 schema. Cannot perform validation of imported documents");
			}
			validator = SafeXMLFactory.newValidator(GPX_1_0_SCHEMA);
		} else {
			if (GPX_1_1_SCHEMA == null) {
				throw new IllegalStateException(
						"Unable to load GPX 1.1 schema. Cannot perform validation of imported documents");
			}
			validator = SafeXMLFactory.newValidator(GPX_1_1_SCHEMA);
		}

		try {
			validator.validate(source);
			return true;
		} catch (final SAXException ex) {
			CorePlugin.logError(IStatus.ERROR, "GPX file trying to import is not valid because " + ex.getMessage(), ex);
			CorePlugin.errorDialog("Load GPS File", "GPX failed validation. Reason: " + ex.getMessage());
		}
		return false;
	}
}