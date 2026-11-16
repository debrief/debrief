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

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.GUI.CommandLine.MultiScenarioCommandLine.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ASSET.GUI.CommandLine.MultiScenarioCommandLine.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.InterScenarioObserverType;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.XML.ASSETReaderWriter;

////////////////////////////////////////////////////////////
// testing stuff
////////////////////////////////////////////////////////////
public class MultiScenarioCommandLineTest extends SupportTesting {

	@Test
	public void testCommandLineMainProcessing() {
		final String[] args = new String[2];
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
		args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_realistic.xml";

		CommandLine.main(args);
	}

	@Test
	public void testInvalidStartups() {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ByteArrayOutputStream bes = new ByteArrayOutputStream();

		final PrintStream out = new PrintStream(bos);
		final PrintStream err = new PrintStream(bes);
		final InputStream in = new ByteArrayInputStream(new byte[] {});

		////////////////////////////////////////////////////////////
		// first with null args
		////////////////////////////////////////////////////////////
		bos.reset();
		bes.reset();
		String[] args = null;
		MultiScenarioCommandLine scen = new MultiScenarioCommandLine();
		int res = 0;
		try {
			res = scen.processThis(args, out, err, in);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}
		assertEquals(WRONG_PARAMETERS, res, "wrong num args error returned");

		////////////////////////////////////////////////////////////
		// now with zero args
		////////////////////////////////////////////////////////////
		bos.reset();
		bes.reset();
		args = new String[0];
		scen = new MultiScenarioCommandLine();
		try {
			res = scen.processThis(args, out, err, in);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}
		assertEquals(WRONG_PARAMETERS, res, "wrong num args error returned");

		////////////////////////////////////////////////////////////
		// now with only 1 args
		////////////////////////////////////////////////////////////
		bos.reset();
		bes.reset();
		args = new String[1];
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario.xml";

		scen = new MultiScenarioCommandLine();
		try {
			res = scen.processThis(args, out, err, in);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertEquals(WRONG_PARAMETERS, res, "wrong num args error returned");

		final String errStr = bes.toString();
		assertTrue(errStr.indexOf("Sorry, both a scenario file and command file are required") >= 0,
				"correct error message");

		////////////////////////////////////////////////////////////
		// and now with an invalid control file
		////////////////////////////////////////////////////////////
		bos.reset();
		bes.reset();
		args = new String[2];
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/small_test_scenario.xml";
		args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_invalid.xml";
		scen = new MultiScenarioCommandLine();
		try {
			res = scen.processThis(args, out, err, in);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}

		assertEquals(PROBLEM_LOADING, res, "wrong num args error returned");

		// check the contents of the error message
		final String str = bes.toString();
		assertTrue(str.indexOf("Problem loading multi-scenario generator:") >= 0, "contains some error message");
	}

	@Test
	public void testValidStartup() {
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final ByteArrayOutputStream bes = new ByteArrayOutputStream();

		final PrintStream out = new PrintStream(bos);
		final PrintStream err = new PrintStream(bes);
		final InputStream in = new ByteArrayInputStream(new byte[] {});

		bos.reset();
		bes.reset();
		final String[] args = new String[2];
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
		args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance1.xml";
		// args[1] =
		// "..\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance1.xml";
		final MultiScenarioCommandLine scen = new MultiScenarioCommandLine();
		int res = 0;
		try {
			res = scen.processThis(args, out, err, in);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}
		assertEquals(SUCCESS, res, "ran ok");

		// check the contents of the error message
		assertEquals(0, bes.size(), "no error reported");

		// check the scenarios got created
		final Vector<Document> scenarios = scen._myScenarios;
		assertEquals(3, scenarios.size(), "scenarios got created");
	}
}
