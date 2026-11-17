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

import static ASSET.Util.MonteCarlo.MultiScenarioGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Util.MonteCarlo.MultiScenarioGenerator.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ScenarioHandler;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReader;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class MultiScenarioGeneratorTest extends SupportTesting {


	// TODO FIX-TEST
	public final void NtestLoadVariances() {
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		System.out.println("curent dir is:" + System.getProperty("user.dir"));

		// create server
		Document document = null;
		try {
			final File iFile = new File(docPath + "test_variance1.xml");
			if (iFile.exists()) {
				final FileInputStream fis = new FileInputStream(iFile);
				document = ScenarioGenerator.readDocumentFrom(fis);
			}
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		final MultiScenarioGenerator xv = new MultiScenarioGenerator(document);

		// ok - check we've loaded our stuff
		assertEquals(2, xv._myVariances.size(), 0, "load participants");

		// get the first part
		final XMLVariance xa = xv._myVariances.itemAt(0);

		assertEquals("do ita1", xa.getName(), "got correct variance object");
		// now try for the range object itself
		final XMLObject op = xa.getObject();
		assertEquals(op.getName(), "Value", "got correct type");
		assertTrue(op instanceof XMLAttribute, "is of attribute type");

		// //////////////////////////////////////////////////////////
		// first the range object
		// //////////////////////////////////////////////////////////

		// now look into the range itself
		XMLAttribute xmla = (XMLAttribute) op;
		assertTrue(xmla.getOperation() instanceof XMLRange, "is of range type");
		final XMLRange xr = (XMLRange) xmla.getOperation();

		// check the data settings
		assertEquals(1, xr.getMin(), 0, "correct min");
		assertEquals(6, xr.getMax(), 0, "correct max");
		assertEquals(0.5, xr.getStep().doubleValue(), 0, "correct step");

		// //////////////////////////////////////////////////////////
		// now the choice
		// //////////////////////////////////////////////////////////
		final XMLVariance xa2 = xv._myVariances.itemAt(1);
		assertEquals("do ita2", xa2.getName(), "got correct variance object");
		// now try for the range object itself
		final XMLObject op2 = xa2.getObject();
		assertEquals(op2.getName(), "Course", "got correct type");
		assertTrue(op2 instanceof XMLAttribute, "is of attribute type");

		// now look into the range itself
		xmla = (XMLAttribute) op2;
		assertTrue(xmla.getOperation() instanceof XMLChoice, "is of choice type");
		final XMLChoice xc = (XMLChoice) xmla.getOperation();

		// check the data settings
		assertEquals(2, xc.size(), 0, "got correct num choices");
		assertEquals("180", xc.get(0), "first choice correct");
		assertEquals("090", xc.get(1), "first choice correct");
	}

	// TODO FIX-TEST
	public final void NtestPerformForceProtectionVariances() {

		// get the file to read in
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		InputStream dataStream = null;
		InputStream varianceStream = null;
		try {
			final File iFile = new File(docPath + "test_variance_scenario_area.xml");
			dataStream = new FileInputStream(iFile);
			varianceStream = new FileInputStream(docPath + "test_variance_area.xml");
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// check it worked
		assertNotNull(dataStream, "managed to load data file");
		assertNotNull(varianceStream, "managed to load variance file");

		Document thisScenarioDocument = null;
		try {
			thisScenarioDocument = ScenarioGenerator.readDocumentFrom(dataStream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		assertNotNull(thisScenarioDocument, "we loaded document ok");

		// examine the first scenario generated. see if the bits loaded
		try {

			XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/Helo//Investigate");
			NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument, XPathConstants.NODESET);
			Element thisE = (Element) nl.item(0);
			assertNotNull(thisE, "found our investigate behaviour");
			assertEquals("Find fishermen", thisE.getAttribute("Name"), "correct name");
			assertEquals("Identified", thisE.getAttribute("DetectionLevel"), "correct name");

			xp2 = NamespaceContextProvider.createPath("//Participants/Helo//Investigate//Type");
			nl = (NodeList) xp2.evaluate(thisScenarioDocument, XPathConstants.NODESET);
			thisE = (Element) nl.item(0);
			assertEquals("FISHING_VESSEL", thisE.getAttribute("Name"), "correct type");

		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}

		Document thisVarianceDocument = null;
		try {
			thisVarianceDocument = ScenarioGenerator.readDocumentFrom(varianceStream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		final MultiScenarioGenerator genny = new MultiScenarioGenerator(thisVarianceDocument);

		assertTrue(genny._myVariances.size() > 0, "loaded vars");

		// can we find a scenario generator?
		try {
			final XPathExpression xp2 = NamespaceContextProvider
					.createPath("//" + MultiParticipantGenerator.GENERATOR_TYPE);
			final NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument, XPathConstants.NODESET);
			final Element el = (Element) nl.item(0);
			if (el != null) {
				final MultiParticipantGenerator participantGenny = new MultiParticipantGenerator(
						thisVarianceDocument);
				assertNotNull(participantGenny, "didn't load participant genny");
			}
		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}

		// now fill the genny
		genny.setDocument(thisScenarioDocument);

		boolean worked = true;
		Document[] results = null;

		// store the original speed for ssn bravo - so that we can check it's
		// changed
		int originalSeaState = -12;

		// did we change the speed?
		try {

			final XPathExpression xp2 = NamespaceContextProvider.createPath("//Environment");
			final NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument, XPathConstants.NODESET);
			final Element thisA = (Element) nl.item(0);
			final String val = thisA.getAttribute("SeaState");
			originalSeaState = Integer.parseInt(val);
		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		try {
			// ok, now create the instances
			results = genny.createNewRandomisedPermutations();
		} catch (final XMLVariance.IllegalExpressionException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
			worked = false;
		} catch (final XMLVariance.MatchingException e) {
			System.err.println("XPath identifier failed" + e.getMessage());
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
			worked = false;
		}

		int numScenariosRequested = -1;

		// did we load the proximity observer
		try {
			XPathExpression xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver");
			NodeList nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			Element thisA = (Element) nl.item(0);
			String val = thisA.getAttribute("Name");
			assertEquals("HELO detected within SAM range", val, "correct name");
			val = thisA.getAttribute("Active");
			assertTrue(true, "set as active");

			xp2 = NamespaceContextProvider
					.createPath("//StopOnProximityDetectionObserver/Target/TargetType/Type[1]");
			nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			val = thisA.getAttribute("Name");
			assertEquals("HELICOPTER", val, "correct name");

			xp2 = NamespaceContextProvider.createPath("//MultiScenarioGenerator");
			nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			val = thisA.getAttribute("Number");
			assertTrue(val.length() > 0, "found number");
			numScenariosRequested = new Integer(val).intValue();

			xp2 = NamespaceContextProvider
					.createPath("//StopOnProximityDetectionObserver/Watch/TargetType/Type[1]");
			nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			val = thisA.getAttribute("Name");
			assertEquals("FISHING_VESSEL", val, "correct name");

			xp2 = NamespaceContextProvider
					.createPath("//StopOnProximityDetectionObserver/Watch/TargetType/Type[2]");
			nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			val = thisA.getAttribute("Name");
			assertEquals("RED", val, "correct name");

			xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver/Range");
			nl = (NodeList) xp2.evaluate(thisVarianceDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			val = thisA.getAttribute("Units");
			assertEquals("nm", val, "correct name");
			val = thisA.getAttribute("Value");
			assertEquals("2.5", val, "correct name");

		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		assertTrue(worked, "check no exceptions thrown");

		assertNotNull(results, "got some documents");
		if (results == null)
			return;

		assertEquals(numScenariosRequested, results.length, "correct num scenarios");

		// keep track of if a different sea state gets generated
		boolean foundDifferentSeaState = false;

		// hey, let's see how we got on
		try {
			for (int i = 0; i < results.length; i++) {

				final Document resDocument = results[i];

				XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='SAM_FISHER']");
				NodeList nl = (NodeList) xp2.evaluate(resDocument, XPathConstants.NODESET);
				Element thisE = (Element) nl.item(0);
				assertNotNull(thisE, "found our participant");
				assertEquals("SAM_FISHER", thisE.getAttribute("Name"), "correct name");

				// did we change the sea state?
				xp2 = NamespaceContextProvider.createPath("//Environment");
				nl = (NodeList) xp2.evaluate(resDocument, XPathConstants.NODESET);
				thisE = (Element) nl.item(0);

				// is this different?
				final int thisSeaState = Integer.parseInt(thisE.getAttribute("SeaState"));

				if (thisSeaState != originalSeaState) {
					foundDifferentSeaState = true;
					continue;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// check we found a different sea state
		assertTrue(foundDifferentSeaState, "we generated a different sea state");

		// re-check the len
		assertEquals(numScenariosRequested, results.length, "got some");

	}

	// TODO FIX-TEST
	public final void NtestPerformVariances() {

		// get the file to read in
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		InputStream dataStream = null;
		InputStream varianceStream = null;
		try {
			dataStream = new FileInputStream(docPath + "test_variance_scenario.xml");
			varianceStream = new FileInputStream(docPath + "test_variance1.xml");
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// check it worked
		assertNotNull(dataStream, "managed to load data file");
		assertNotNull(varianceStream, "managed to load variance file");

		Document thisDocument = null;
		try {
			thisDocument = ScenarioGenerator.readDocumentFrom(dataStream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		assertNotNull(thisDocument, "we loaded document ok");

		Document document = null;
		try {
			document = ScenarioGenerator.readDocumentFrom(varianceStream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		final MultiScenarioGenerator genny = new MultiScenarioGenerator(document);

		assertEquals(2, genny._myVariances.size(), 0, "loaded vars");

		// now fill the genny
		genny.setDocument(thisDocument);

		boolean worked = true;
		Document[] results = null;

		// store the original speed for ssn bravo - so that we can check it's
		// changed
		double originalSpeed = Double.NaN;

		// did we change the speed?
		try {
			final XPathExpression xp2 = NamespaceContextProvider.createPath("//*[@Name='bravo']/Status/Speed");
			final NodeList nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
			final Element thisA = (Element) nl.item(0);
			final String val = thisA.getAttribute("Value");
			originalSpeed = MWCXMLReader.readThisDouble(val);

		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		try {
			// ok, now create the instances
			results = genny.createNewRandomisedPermutations();
		} catch (final XMLVariance.IllegalExpressionException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
			worked = false;
		} catch (final XMLVariance.MatchingException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
			worked = false;
		}

		assertTrue(worked, "check no exceptions thrown");

		assertNotNull(results, "got some documents");
		if (results == null)
			return;
		assertEquals(3, results.length, "correct num scenarios");

		// hey, let's see how we got on
		try {
			for (int i = 0; i < 3; i++) {
				// SupportTesting.outputThis(results[i], "permutation:" + i);
			}

			final Document resDocument = results[0];

			XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo']");
			NodeList nl = (NodeList) xp2.evaluate(resDocument, XPathConstants.NODESET);
			Element thisA = (Element) nl.item(0);
			assertNotNull(thisA, "found our participant");
			assertEquals("bravo", thisA.getAttribute("Name"), "correct name");

			// did we change the speed?
			xp2 = NamespaceContextProvider.createPath("//*[@Name='bravo']/Status/Speed");
			nl = (NodeList) xp2.evaluate(resDocument, XPathConstants.NODESET);
			thisA = (Element) nl.item(0);
			assertTrue(MWCXMLReader.readThisDouble(thisA.getAttribute("Value")) != originalSpeed, "correct name");
		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// lastly, check we can export the scenarios
		// re-check the len
		assertEquals(3, results.length, "got some");

	}

}
