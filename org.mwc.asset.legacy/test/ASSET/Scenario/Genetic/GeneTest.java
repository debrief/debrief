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

package ASSET.Scenario.Genetic;

import static org.junit.jupiter.api.Assertions.*;

import static ASSET.Scenario.Genetic.Gene.*;
import ASSET.Scenario.Genetic.Gene.*;
import java.io.InputStream;
import java.util.Comparator;
import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import ASSET.Util.MonteCarlo.XMLVariance;
import ASSET.Util.MonteCarlo.XMLVarianceList;

/**
 * ************************************************************ testing code
 * *************************************************************
 */
public class GeneTest {
	static public final String TEST_ALL_TEST_TYPE = "UNIT";

	/**
	 * are these genes completely different?
	 */
	protected static boolean allDiff(final Gene a, final Gene b) {
		boolean diff = true;
		// merge random genes
		final Iterator<XMLVariance> it_a = a._myList.getIterator();
		final Iterator<XMLVariance> it_b = b._myList.getIterator();
		while (it_a.hasNext()) {
			final XMLVariance my_next = it_a.next();
			final XMLVariance his_next = it_b.next();
			if (my_next.equals(his_next)) {
				diff = false;
			}
		}
		return diff;
	}


	/**
	 * output this gene
	 *
	 */
	// private static String showGene(final Gene g)
	// {
	// String res = "";
	// final XMLVarianceList lst = g._myList;
	// final Iterator it = lst.getIterator();
	// while (it.hasNext())
	// {
	// final XMLVariance variable = (XMLVariance) it.next();
	// res += variable.toString() + "\n";
	// }
	// return res;
	// }

	@Test
	public void testIt() {

		// String code_root = System.getProperty("CODE_ROOT");
		// if(code_root == null)
		// code_root = "D:\\dev\\asset\\src\\java";
		//
		// final String docPath = code_root +
		// "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\SuperSearchSSK.xml";
		// final String varyPath = code_root +
		// "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\vary_ssk.xml";
		// final String badVaryPath = code_root +
		// "\\ASSET_SRC\\ASSET\\Util\\XMLFactory\\BAD_vary_ssk.xml";
		//
		// /***************************************************************
		// * initialise from stream (files missing)
		// ***************************************************************/
		// boolean ex_thrown = false;
		// try{
		// final Gene noFile = new Gene(new java.io.FileInputStream("none"), new
		// java.io.FileInputStream("none"));
		// }
		// catch(java.io.FileNotFoundException fe)
		// {
		// ex_thrown = true;
		// }
		// assertTrue("FileNotFound thrown", ex_thrown);
		//
		// /***************************************************************
		// * initialise from stream (invalid files)
		// ***************************************************************/
		// ex_thrown = false;
		// try{
		// java.io.FileInputStream inputDoc = new java.io.FileInputStream(docPath);
		// assertNotNull("Failed to find input document", inputDoc);
		// java.io.FileInputStream inputVary = new java.io.FileInputStream(badVaryPath);
		// assertNotNull("Failed to find input variance", inputVary);
		// final Gene noFile = new Gene(inputDoc, inputVary);
		// }
		// catch(java.lang.RuntimeException re)
		// {
		// // check it's the error we're after
		// final String msg = re.getMessage();
		// if(msg.indexOf("VaribleList") > 0)
		// ex_thrown = true;
		// }
		// catch(java.io.FileNotFoundException fe)
		// {
		// fe.printStackTrace();
		// ex_thrown = true;
		// }
		// assertTrue("Bad file report thrown", ex_thrown);
		//
		// /***************************************************************
		// * initialise from stream (valid)
		// ***************************************************************/
		// Gene ng = null;
		// try{
		//
		// // check the files exist
		// assertTrue("input document not found", new File(docPath).exists());
		// assertTrue("variance document not found", new File(varyPath).exists());
		//
		// java.io.FileInputStream inputDoc = new java.io.FileInputStream(docPath);
		// assertNotNull("Failed to find input document", inputDoc);
		// java.io.FileInputStream inputVary = new java.io.FileInputStream(varyPath);
		// assertNotNull("Failed to find input variance", inputVary);
		// ng = new Gene(inputDoc, inputVary);
		// }
		// catch(java.io.FileNotFoundException fe)
		// {
		// fe.printStackTrace();
		// }
		//
		// assertNotNull("Gene created", ng);
		//
		// /***************************************************************
		// * create a new random gene
		// ***************************************************************/
		// final XMLVarianceList xl = ng._myList;
		//
		// final Gene rg = ng.createRandom();
		//
		// final XMLVarianceList xl2 = rg._myList;
		//
		// assertEquals("Random list of correct length", xl2.size(), xl.size());
		// assertTrue("Random List contains different genes", (xl != xl2));
		//
		// /***************************************************************
		// * setting fitness
		// ***************************************************************/
		// assertEquals("fitness initialised",
		// ScenarioRunner.ScenarioOutcome.INVALID_SCORE,
		// ng.getFitness(), 0);
		//
		// final double newFit = 12d;
		//
		// ng.setFitness(newFit, "null");
		//
		// assertEquals("fitness assigned", newFit, ng.getFitness(), 0);
		//
		// /***************************************************************
		// * checking equality
		// ***************************************************************/
		// assertTrue("same genes are equal", (ng.equals(ng)));
		// assertTrue("different genes aren't equal", (!ng.equals(rg)));
		//
		// /***************************************************************
		// * comparing genes
		// ***************************************************************/
		// final double otherFit = 15d;
		// rg.setFitness(otherFit, null);
		//
		// assertEquals("comparison of genes", ng.compareTo(rg), -1);
		//
		// ex_thrown = false;
		// try
		// {
		// final int res = ng.compareTo(new java.util.Date());
		// }
		// catch(java.lang.ClassCastException ce)
		// {
		// ex_thrown = true;
		// }
		// assertTrue("recognised comparison of invalid object", ex_thrown);
		//
		// /***************************************************************
		// * able to produce document
		// ***************************************************************/
		// final String newDoc = ng.getDocument();
		//
		// assertNotNull("document created", newDoc);
		// assertTrue("document contains stuff", newDoc.length() > 0);
		// assertTrue("document is different to original (we only value in dem course):"
		// +
		// newDoc.indexOf("134.293"),
		// 511 < newDoc.indexOf("134.293"));
		//
		// /***************************************************************
		// * merge operation
		// ***************************************************************/
		// final double prob_a = 0.0;
		// final double prob_b = 1.0;
		//
		// // take copy of gene
		// final Gene safeG = new Gene(ng._myList);
		//
		// ng.mergeWith(rg, prob_a);
		//
		// assertTrue("1 makes no change", safeG.equals(ng));
		//
		// ng.mergeWith(rg, prob_b);
		//
		// assertTrue("zero thresh makes all changes", allDiff(safeG, ng));
		// todo: reinstate this
	}
}
