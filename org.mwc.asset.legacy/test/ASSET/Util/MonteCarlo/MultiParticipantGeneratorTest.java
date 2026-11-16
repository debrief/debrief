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

import static ASSET.Util.MonteCarlo.MultiParticipantGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.Util.MonteCarlo.MultiParticipantGenerator.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;

// ////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
// ////////////////////////////////////////////////////////////////////////////////////////////////
public final class MultiParticipantGeneratorTest extends SupportTesting {
	protected FileInputStream stream;


	// TODO FIX-TEST
	public final void NtestLoadVariances() throws XPathExpressionException {
		// create server
		final MultiParticipantGenerator xv = new MultiParticipantGenerator();

		String code_root = System.getProperty("CODE_ROOT");
		System.out.println("root:" + code_root);
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		Document doc = null;
		try {
			final File file = new File(docPath + "test_variance1.xml");
			assertTrue(file.exists(), "can't find data-file");
			stream = new FileInputStream(file);
			doc = ScenarioGenerator.readDocumentFrom(stream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}
		xv.loadVariances(doc);

		assertNotNull(doc, "managed to load file");

		// ok - check we've loaded our stuff
		assertEquals(2, xv._myParticipantVariances.size(), 0, "load participants");

		// get the first part
		ParticipantVariance pa = xv._myParticipantVariances.get(0);
		assertEquals(3, pa.size(), 0, "got vars for first part");
		XMLVariance xa = pa.itemAt(0);
		assertEquals("do it1", xa.getName(), "got correct variance object");
		// now try for the range object itself
		XMLObject op = xa.getObject();
		assertEquals(op.getName(), "Value", "got correct type");
		assertTrue(op instanceof XMLAttribute, "is of attribute type");

		// //////////////////////////////////////////////////////////
		// first the range object
		// //////////////////////////////////////////////////////////

		// now look into the range itself
		XMLAttribute xmla = (XMLAttribute) op;
		assertTrue(xmla.getOperation() instanceof XMLRange, "is of range type");
		XMLRange xr = (XMLRange) xmla.getOperation();

		// check the data settings
		assertEquals(1, xr.getMin(), 0, "correct min");
		assertEquals(6, xr.getMax(), 0, "correct max");
		assertEquals(1.5, xr.getStep().doubleValue(), 0, "correct step");

		// //////////////////////////////////////////////////////////
		// now the choice
		// //////////////////////////////////////////////////////////
		XMLVariance xa2 = pa.itemAt(1);
		assertEquals("do it2", xa2.getName(), "got correct variance object");
		// now try for the range object itself
		XMLObject op2 = xa2.getObject();
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

		// //////////////////////////////////////////////////////////
		// now the xml choice
		// //////////////////////////////////////////////////////////

		xa2 = pa.itemAt(2);
		assertEquals("do it3", xa2.getName(), "got correct variance object");
		assertEquals("Status/Location/shortLocation", xa2.getId(), "got correct variance object");

		// now try for the range object itself
		op2 = xa2.getObject();
		assertTrue(op2 instanceof XMLNode, "is of node type");

		// now look into the range itself
		final XMLNode xmln = (XMLNode) op2;
		assertTrue(xmln.getOperation() instanceof XMLSnippets, "is of choice type");
		final XMLSnippets xxc = xmln.getOperation();

		// check the data settings
		assertEquals(4, xxc.size(), 0, "got correct num choices");

		// test the first
		final Node first = xxc.get(0);
		// have a look at it
		assertEquals("shortLocation", first.getNodeName(), "first is of correct type");

		final Node second = xxc.get(3);
		// have a look at it
		assertEquals("longLocation", second.getNodeName(), "second is of correct type");

		// //////////////////////////////////////////////////////////
		// move onto the second participant
		// //////////////////////////////////////////////////////////
		// get the first part
		pa = xv._myParticipantVariances.get(1);
		assertEquals(1, pa.size(), 0, "got vars for seond part");
		xa = pa.itemAt(0);
		assertEquals("do it4", xa.getName(), "got correct variance object");
		// now try for the range object itself
		op = xa.getObject();
		assertEquals(op.getName(), "Value", "got correct type");
		assertTrue(op instanceof XMLAttribute, "is of attribute type");

		// //////////////////////////////////////////////////////////
		// first the range object
		// //////////////////////////////////////////////////////////

		// now look into the range itself
		xmla = (XMLAttribute) op;
		assertTrue(xmla.getOperation() instanceof XMLRange, "is of range type");
		xr = (XMLRange) xmla.getOperation();

		// check the data settings
		assertEquals(10, xr.getMin(), 0, "correct min");
		assertEquals(26, xr.getMax(), 0, "correct max");
		assertEquals(2, xr.getStep().doubleValue(), 0, "correct step");
	}

	// TODO FIX-TEST
	public final void NtestPerformFailedVariances1() {
		final MultiParticipantGenerator genny = new MultiParticipantGenerator();

		// get the file to read in
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		InputStream dataStream = null;
		InputStream varianceStream = null;
		try {
			dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
			varianceStream = new java.io.FileInputStream(docPath + "test_variance_FailsToMatch.xml");
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

		try {
			genny.loadVariances(ScenarioGenerator.readDocumentFrom(varianceStream));
		} catch (final XPathExpressionException e1) {
			e1.printStackTrace();
		} catch (final SAXParseException e1) {
			e1.printStackTrace();
		} catch (final SAXException e1) {
			e1.printStackTrace();
		}

		assertEquals(2, genny._myParticipantVariances.size(), 0, "loaded vars");

		// now fill the genny
		genny.setDocument(thisDocument);

		ParticipantVariance pa = genny._myParticipantVariances.get(0);

		Exception ex = null;

		try {
			pa.populate(thisDocument);

			// now do it for the second one
			pa = genny._myParticipantVariances.get(1);
			pa.populate(thisDocument);
		} catch (final XMLVariance.IllegalExpressionException e) {
			ex = e;
		} catch (final XMLVariance.MatchingException e) {
			ex = e;
		}

		// check that an exception got thrown
		assertNotNull(ex, "check no exceptions thrown");
		assertTrue(ex instanceof XMLVariance.MatchingException, "it failed to match anything");
	}

	// TODO FIX-TEST
	public final void NtestPerformFailedVariances2() {
		final MultiParticipantGenerator genny = new MultiParticipantGenerator();

		// get the file to read in
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		InputStream dataStream = null;
		InputStream varianceStream = null;
		try {
			dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
			varianceStream = new java.io.FileInputStream(docPath + "test_variance_IllegalExpression.xml");
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

		try {
			genny.loadVariances(ScenarioGenerator.readDocumentFrom((varianceStream)));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertEquals(2, genny._myParticipantVariances.size(), 0, "loaded vars");

		// now fill the genny
		genny.setDocument(thisDocument);

		ParticipantVariance pa = genny._myParticipantVariances.get(0);

		Exception ex = null;

		try {
			pa.populate(thisDocument);

			// now do it for the second one
			pa = genny._myParticipantVariances.get(1);
			pa.populate(thisDocument);
		} catch (final XMLVariance.IllegalExpressionException e) {
			ex = e;
		} catch (final XMLVariance.MatchingException e) {
			ex = e;
		}

		// check that an exception got thrown
		assertNotNull(ex, "check no exceptions thrown");
		assertTrue(ex instanceof XMLVariance.IllegalExpressionException, "it failed to match anything");
	}

	// TODO FIX-TEST
	public final void NtestPerformVariances() throws XPathExpressionException {
		final MultiParticipantGenerator genny = new MultiParticipantGenerator();

		// get the file to read in
		String code_root = System.getProperty("CODE_ROOT");
		if (code_root == null)
			code_root = "src";

		final String docPath = code_root + "/ASSET/Util/MonteCarlo/";

		InputStream dataStream = null;
		InputStream varianceStream = null;
		try {
			dataStream = new java.io.FileInputStream(docPath + "test_variance_scenario.xml");
			varianceStream = new java.io.FileInputStream(docPath + "test_variance1.xml");
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

		try {
			genny.loadVariances(ScenarioGenerator.readDocumentFrom(varianceStream));
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}

		assertEquals(2, genny._myParticipantVariances.size(), 0, "loaded vars");

		// now fill the genny
		genny.setDocument(thisDocument);

		ParticipantVariance pa = genny._myParticipantVariances.get(0);

		boolean worked = true;

		try {
			pa.populate(thisDocument);

			// now do it for the second one
			pa = genny._myParticipantVariances.get(1);
			pa.populate(thisDocument);
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

		// so, how does it look now?
		// super.outputThisDocument(thisDocument, "After permutations done");

		// hey, let's see how we got on
		try {
			XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo_001']");
			NodeList nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
			Element thisE = (Element) nl.item(0);
			// SupportTesting.outputThis(thisDocument,"have a look at this!");
			assertEquals("bravo_001", thisE.getAttribute("Name"), "correct name");

			// did we create the last one?
			xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo_004']");
			nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
			thisE = (Element) nl.item(0);
			assertEquals("bravo_004", thisE.getAttribute("Name"), "correct name");

			// //////////////////////////////////////////////////////////
			// now the alphas
			// //////////////////////////////////////////////////////////
			xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='alpha_001']");
			nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
			thisE = (Element) nl.item(0);
			assertEquals("alpha_001", thisE.getAttribute("Name"), "correct name");

			xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='alpha_006']");
			nl = (NodeList) xp2.evaluate(thisDocument, XPathConstants.NODESET);
			thisE = (Element) nl.item(0);
			assertEquals("alpha_006", thisE.getAttribute("Name"), "correct name");
		} catch (final Exception e) {
			e.printStackTrace(); // To change body of catch statement use Options |
									// File Templates.
		}
	}

}
