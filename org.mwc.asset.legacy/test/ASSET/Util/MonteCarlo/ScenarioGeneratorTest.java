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

package ASSET.Util.MonteCarlo;

import static ASSET.Util.MonteCarlo.ScenarioGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Util.MonteCarlo.ScenarioGenerator.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.Util.RandomGenerator;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ScenarioHandler;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class ScenarioGeneratorTest extends SupportTesting {
	public static final String VARIANCE_FILE = "test_variance1.xml";
	public static final String SCENARIO_FILE = "test_variance_scenario.xml";


	/**
	 * Emulation of File.createNewFile for JDK 1.1.
	 * <p/>
	 * <p>
	 * This method does <strong>not</strong> guarantee that the operation is atomic.
	 * </p>
	 *
	 * @since 1.21, Ant 1.5
	 */
	private boolean createNewFile(final File f) throws IOException {
		if (f != null) {
			if (f.exists()) {
				return false;
			}

			FileOutputStream fos = null;
			try {
				// first create the parent directory(s) if needed
				final File parentFile = f.getParentFile();
				parentFile.mkdirs();

				// and now the file itself
				fos = new FileOutputStream(f);
				fos.write(new byte[0]);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}

			return true;
		}
		return false;
	}

	// TODO FIX-TEST
	public final void NtestLoadVariances() throws XPathExpressionException {
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		Document doc = null;
		try {
			final File var = new File(docPath + VARIANCE_FILE);
			assertTrue(var.exists(), "can find data-file");
			doc = ScenarioGenerator.readDocumentFrom(new FileInputStream(var));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}
		final ScenarioGenerator genny = new ScenarioGenerator();
		genny.setVariances(doc, null);

		// check they got loaded
		assertEquals("test_reports/asset_test_output/test_variance1", genny.getDirectory(), "loaded template name");
	}

	// TODO FIX-TEST
	public final void NtestPerformVariances() throws XPathExpressionException {
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		final Vector<Document> list = new Vector<Document>(0, 1);

		final ScenarioGenerator genny = new ScenarioGenerator();

		Document var = null;
		try {
			final File iFile = new File(docPath + VARIANCE_FILE);
			final FileInputStream fis = new FileInputStream(iFile);
			var = ScenarioGenerator.readDocumentFrom(fis);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		Document doc = null;
		try {
			doc = ScenarioGenerator.readDocumentFrom(new FileInputStream(docPath + SCENARIO_FILE));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		final String res = genny.doScenarioGeneration(doc, var, list, null, null);

		assertNull(res, "success - no error");

		// check there's stuff in the lst
		assertEquals(3, list.size(), "got some scenarios");
	}

	// TODO FIX-TEST
	public final void NtestPerformVariancesFromFilenames() {
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		final Vector<Document> list = new Vector<Document>(0, 1);

		final ScenarioGenerator genny = new ScenarioGenerator();

		String res = null;
		try {
			res = genny.createScenarios(docPath + SCENARIO_FILE, docPath + VARIANCE_FILE, list, null, null);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertNull(res, "success - no error");

		// check there's stuff in the lst
		assertEquals(3, list.size(), "got some scenarios");
	}

	// TODO FIX-TEST
	public final void NtestPerformVariancesFromInvalidFilenames() {
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		final Vector<Document> list = new Vector<Document>(0, 1);

		final ScenarioGenerator genny = new ScenarioGenerator();

		String res = null;
		try {
			res = genny.createScenarios(docPath + "test_variance_scnario.xml", docPath + SCENARIO_FILE, list, null,
					null);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertNotNull(res, "success - error returned");
		assertTrue(res.indexOf(TEMPLATE_FILE_ERROR) > -1, "correct error returned");

		// check there's stuff in the lst
		assertEquals(0, list.size(), "got no scenarios");

		try {
			res = genny.createScenarios(docPath + SCENARIO_FILE, docPath + "test_varince1.xml", list, null, null);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertNotNull(res, "success - error returned");
		assertTrue(res.indexOf(CONTROL_FILE_ERROR) > -1, "correct error returned");

		// check there's stuff in the lst
		assertEquals(0, list.size(), "got no scenarios");

	}

	// TODO FIX-TEST
	public void NtestValidation() {
		// String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		// str +=
		// "<Scenario xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		// xsi:noNamespaceSchemaLocation=\"file:///E:/dev/Asset/src/schemas/ASSET.xsd/\"
		// Created=\"2003-12-12T12:00:00\" Name=\"the name\"
		// StartTime=\"2003-12-12T12:00:00\">";
		// str += "<StepTime Units=\"seconds\" Value=\"45\"/>";
		// str += "<Participants/>";
		// str += "</Scenario>";

		String res = null;

		try {
			final FileInputStream doc = new FileInputStream(
					"../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario.xml");
			assertNotNull(doc, "we found test document");
			res = ScenarioGenerator.validateThisDocument(doc);
		} catch (final FileNotFoundException e1) {
			e1.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// check if/why validation isn't working
		assertNull(res, "valid file didn't throw any errors");

		try {
			final FileInputStream doc = new FileInputStream(
					"../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario_invalid.xml");
			assertNotNull(doc, "we found test document");
			res = ScenarioGenerator.validateThisDocument(doc);
		} catch (final FileNotFoundException e1) {
			e1.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final Exception se) {
			System.out.println("OTHER EXCEPTION THROWN");
			se.printStackTrace();
		}

		// check if/why validation isn't working
		assertNotNull(res, "valid file didn't throw any errors");

		// and check it's contents
		// assertEquals("correct error message",
		// "cvc-elt.1: Cannot find the declaration of element 'Scenaio'.", res);
		assertEquals("The element type \"Scenaio\" must be terminated by the matching end-tag \"</Scenaio>\".",
				res, "correct error message");
	}

	// TODO FIX-TEST
	public void NtestWriteScenariosToFile() {
		final String outputDir = "c:\\temp\\asset_test_output";

		// check the output directory is clear
		final File outputDirectory = new File(outputDir);

		deleteThisDirectory(outputDirectory);

		assertFalse(outputDirectory.exists(), "we should have deleted the output directory");
		// ok, put dummy seed into the output directory to check that it's been
		// deleted

		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		final Vector<Document> list = new Vector<Document>(0, 1);

		final ScenarioGenerator genny = new ScenarioGenerator();

		Document var = null;
		try {
			var = ScenarioGenerator.readDocumentFrom(new FileInputStream(docPath + VARIANCE_FILE));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		Document doc = null;
		try {
			doc = ScenarioGenerator.readDocumentFrom(new FileInputStream(docPath + SCENARIO_FILE));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		String res = null;
		try {
			res = genny.doScenarioGeneration(doc, var, list, null, null);
		} catch (final XPathExpressionException e1) {
			e1.printStackTrace();
		}

		assertNull(res, "success - no error");

		// check there's stuff in the lst
		assertEquals(3, list.size(), "got some scenarios");

		// ok, we've read in the data - find out where the output directory is, so
		// that we can seed it with an old file (to check that they get deleted)
		final String theDir = genny.getDirectory();
		final File touchFile = new File(theDir + "/" + "seed_file.txt");
		try {
			createNewFile(touchFile);
		} catch (final IOException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		assertTrue(touchFile.exists(), "check we created the touch file");

		// and now write them out
		genny.writeTheseToFile(list, true);

		// and check that the touch file has been deleted
		assertFalse(touchFile.exists(), "check that the touch file has been deleted");
	}

}
