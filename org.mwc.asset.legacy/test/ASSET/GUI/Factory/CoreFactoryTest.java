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

package ASSET.GUI.Factory;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.GUI.Factory.CoreFactory.*;
import ASSET.GUI.Factory.CoreFactory.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Vector;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import ASSET.ScenarioType;
import ASSET.Scenario.Genetic.Gene;
import ASSET.Scenario.Genetic.GeneticAlgorithm;
import ASSET.Scenario.Genetic.ScenarioRunner;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GUI.DragDrop.FileDropSupport;
import MWC.Utilities.ReaderWriter.XML.MWCXMLReaderWriter;

//////////////////////////////////////////////////////////////////////////////////////////////////
// testing for this public class
//////////////////////////////////////////////////////////////////////////////////////////////////
public class CoreFactoryTest {
	// todo reinstate these tests (should be UNIT)
	static public final String TEST_ALL_TEST_TYPE = "UNTIE";


	@Test
	public void testStepping() {
		// final CoreFactory cf = new CoreFactory();
		//// cf.setDocument("d:\\dev\\Asset\\ASSET2_OUT\\tstFactory.xml");
		//// cf.setVariance("d:\\dev\\Asset\\ASSET2_OUT\\vary_factory.xml");
		//
		// String root = System.getProperty("TEST_ROOT");
		// if(root == null)
		// {
		// root = "d:\\dev\\asset2_out\\";
		// }
		//
		// cf.setDocument(root + "factory_scenario.xml");
		// cf.setVariance(root + "factory_variables.xml");
		//
		//
		// // artificially set the genes & stars
		// cf._genes = 30;
		// cf._stars = 5;
		// cf._myRunner = new GeneticAlgorithm.TotalRunner();
		//
		// boolean worked = cf.build();
		//
		// // check the build worked ok
		// assertTrue("couldn't load data", worked);
		//
		// final GeneticAlgorithm ga = cf._myGA;
		//
		// // check empty
		// assertEquals("genes empty", ga.getGenes().size(), 0);
		// assertEquals("no stars got created", ga.getStarGenes().size(), 0);
		//
		// // do the step components first
		// ga.generate();
		//
		// // check some got created
		// assertEquals("genes created", ga.getGenes().size(), 30);
		// assertEquals("no stars got created", ga.getStarGenes().size(), 0);
		//
		// ga.mutate();
		//
		// // no mutation expected, since first cycle
		// assertEquals("genes created", ga.getGenes().size(), 30);
		// assertEquals("no stars got created", ga.getStarGenes().size(), 0);
		//
		// ga.step(false);
		//
		// // check changed
		// final Gene firstG = (Gene) ga.getGenes().iterator().next();
		// double firstScore = firstG.getFitness();
		// assertTrue("gene got score", firstG.getFitness() != -1);
		// assertEquals("genes still there", ga.getGenes().size(), 30);
		// assertEquals("no stars got created", ga.getStarGenes().size(), 0);
		//
		// ga.promote();
		//
		// // check changed
		// Gene firstStar = (Gene) ga.getStarGenes().iterator().next();
		// firstScore = firstG.getFitness();
		// assertTrue("star got score", firstScore != -1);
		// assertEquals("genes got scores", ga.getGenes().size(), 30);
		// assertEquals("stars got created", ga.getStarGenes().size(), 5);
		//
		// ga.retire();
		//
		// // check changed
		// assertEquals("genes got retired", ga.getGenes().size(), 0);
		// assertEquals("stars got created", ga.getStarGenes().size(), 5);
		//
		// cf.cycle();
		//
		// // check that the score has changed
		// firstStar = (Gene) ga.getStarGenes().iterator().next();
		// final double secondScore = firstStar.getFitness();
		// assertTrue("score changed", secondScore != firstScore);
		//
		// todo: reinstate this
	}
}
