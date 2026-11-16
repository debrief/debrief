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

package ASSET.GUI.CommandLine;

import static ASSET.GUI.CommandLine.CommandLine.*;
import static org.junit.jupiter.api.Assertions.*;

import ASSET.GUI.CommandLine.CommandLine.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.MultiScenarioGenerator;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GenericData.WorldLocation;

public class CommandLineTest extends SupportTesting {
	private String TEST_ROOT;


	@Test
	public void testInvalidStartups() {
		/**********************************************************************
		 * first test invalid combinations
		 *********************************************************************/
		boolean res = false;

		// final String sep = "\r\n";

		TEST_ROOT = System.getProperty("TEST_ROOT");
		if (TEST_ROOT == null) {
			TEST_ROOT = "../org.mwc.asset.core.feature/root_installs/AssetData/Samples/legacy";
		}

		System.out.println("root is:" + TEST_ROOT);

		final CommandLine cl = new CommandLine();

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final PrintStream output = new PrintStream(bos);

		// check handles missing scenario, missing control
		res = cl.setup(null, null, output);
		assertFalse(res, "correctly failed to open");
		// String outMsg = bos.toString().trim();
		// final String testMsg = "Setup failed:Scenario file (null)" +
		// CommandLine.FILE_NOT_SPECIFIED;
		bos.reset();

		// check handles invalid scenario, missing control
		res = cl.setup(TEST_ROOT + "/test1_scenario_valid.xml", null, output);
		assertFalse(res, "correctly failed to open");
		final String testMsg2 = "Setup failed:Control file (null)" + CommandLine.FILE_NOT_SPECIFIED;
		final String bosString = bos.toString().trim();
		assertEquals(testMsg2, bosString, "message is correct");
		bos.reset();

		// check handles valid scenario, missing control
		res = cl.setup(TEST_ROOT + "\test1_scenario.xml", null, output);
		assertFalse(res, "correctly failed to open");
		final String bos3 = bos.toString();
		assertTrue(bos3.indexOf("Setup failed:Scenario file") > -1, "message is correct");
		bos.reset();

		// check handles missing scenario, invalid control
		res = cl.setup(null, TEST_ROOT + "\\test1_control_invalid.xml", output);
		assertFalse(res, "correctly failed to open");
		assertTrue(bos.toString().indexOf("Setup failed:Scenario file") > -1, "message is correct");
		bos.reset();

		// check handles valid scenario, invalid control
		// res = cl.setup(TEST_ROOT + "/test1_scenario_valid.xml",
		// TEST_ROOT + "/test1_control_invalid.xml", output);
		// assertFalse("correctly failed to open", res);
		// final String bos6 = bos.toString();
		// assertTrue("message is correct",
		// bos6.toString().indexOf("handler not found") > -1);
		// bos.reset();

		// check handles missing scenario, valid control
		res = cl.setup(null, TEST_ROOT + "\\test1_control.xml", output);
		assertFalse(res, "correctly failed to open");
		assertTrue(bos.toString().indexOf("Setup failed:Scenario file (n") > -1, "message is correct");
		bos.reset();

		// check handles invalid scenario, valid control
		// res = cl.setup(TEST_ROOT + "\\test1_scenario_invalid.xml",
		// TEST_ROOT + "\\test1_control_valid.xml", output);
		// assertFalse("correctly failed to open", res);
		// assertTrue("message is correct",
		// bos.toString().indexOf("handler not found") > -1);
		// bos.reset();

		/**********************************************************************
		 * ok, just check a valid startup combination
		 *********************************************************************/

		System.err.println("all tests passed!");

		// check handles valid scenario, valid control
		// res = cl.setup(TEST_ROOT + "\\test1_scenario_valid.xml",
		// TEST_ROOT + "\\test1_control_valid.xml", output);
		//
		// assertTrue("correctly opened", res);

		// cl.run();

	}

	@Test
	public void testMonteCarlo() {

		TEST_ROOT = System.getProperty("TEST_ROOT");
		if (TEST_ROOT == null) {
			TEST_ROOT = "test_reports";
		}

		// todo - create new command line extension which will handle multiple
		// scenario generation

		// run scenario genny

	}

	@Test
	public void testRunScenario() {
		// todo: implement thest tests

		// check we don't run when empty

		// load the data

		// check data is being output to the control destination

		// check we run

		// check we have completed

		// check everything's in a tidy state
	}

}
