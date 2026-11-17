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

import static ASSET.GUI.CommandLine.MultiScenarioCore.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ASSET.GUI.CommandLine.MultiScenarioCore.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.InterScenarioObserverType;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.ScenarioStatusObserver;
import ASSET.Scenario.Observers.TimeObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;
import MWC.Algorithms.LiveData.IAttribute;

// //////////////////////////////////////////////////////////
// testing stuff
// //////////////////////////////////////////////////////////
public class MultiScenarioCoreTest extends SupportTesting {

	@Test
	public void testCommandLineMainProcessing() {
		final String[] args = new String[2];
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
		args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_realistic.xml";

		CommandLine.main(args);
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
		args[1] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
		args[0] = "../org.mwc.asset.legacy/src/ASSET/Util/MonteCarlo/test_variance1.xml";
		// args[1] =
		// "..\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance1.xml";
		final MultiScenarioCore scen = new MultiScenarioCore();
		final ASSETProgressMonitor pMon = new ASSETProgressMonitor() {
			@Override
			public void beginTask(final String name, final int totalWork) {
			}

			@Override
			public void worked(final int work) {
			}
		};
		int res = 0;
		try {
			res = scen.prepareFiles(args[0], args[1], out, err, in, pMon, null);
		} catch (final XPathExpressionException e) {
			e.printStackTrace();
		}
		assertEquals(SUCCESS, res, "ran ok");

		// check the contents of the error message
		assertEquals(0, bes.size(), "no error reported");

		// check the scenarios got created
		final Vector<Document> scenarios = scen._myScenarioDocuments;
		assertEquals(3, scenarios.size(), "scenarios got created");
	}
}
