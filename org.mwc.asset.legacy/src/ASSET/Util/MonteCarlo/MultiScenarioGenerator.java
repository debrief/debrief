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

import java.io.File;
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

import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ScenarioHandler;

/**
 * list of items in a particular file which may be changed
 */

public final class MultiScenarioGenerator {

	private static final String OUTPUT_DIRECTORY = "OutputDirectory";

	private static final String NAME_TEMPLATE = "NameTemplate";

	private static final String NUMBER_PERMS = "Number";

	private static final String MAX_INSTANCES = "MaxPerms";

	public static final String GENERATOR_TYPE = "MultiScenarioGenerator";

	public static final String SCENARIO_NAME_ATTRIBUTE = "Name";

	private static final String SCENARIO_CASE_ATTRIBUTE = "Case";

	/**
	 * rename the scenario using the supplied index
	 *
	 * @param newDoc document containing the scenario to edit
	 * @param caseId a description of this specific case
	 */
	private static void setScenarioCase(final Document newDoc, final String caseId) {
		// get the scenario object
		final NodeList list = newDoc.getElementsByTagName(ScenarioHandler.type);
		final Element scen = (Element) list.item(0);
		scen.setAttribute(SCENARIO_CASE_ATTRIBUTE, caseId);
	}

	/**
	 * method to write a list of scenarios to file, each in their own directory
	 *
	 * @param scenarios   the list of scenarios to write to file
	 * @param path_prefix the path to put the scenarios into
	 */
	public static void writeTheseToFile(final Vector<Document> scenarios, final String path_prefix) {
		for (int counter = 0; counter < scenarios.size(); counter++) {
			final Document thisDoc = scenarios.elementAt(counter);

			final String asString = ScenarioGenerator.writeToString(thisDoc);

			// and output this string to file
			final int thisId = counter + 1;

			// create the path to the new file
			final String thePath = path_prefix + "/" + thisId + "/" + "scen_" + thisId;

			// declare it as a file
			final File outFile = new File(thePath);

			// create any parent directories we need
			outFile.mkdirs();

			try {
				// put it into a writer
				final FileWriter fw = new FileWriter(outFile);

				// write it out
				fw.write(asString);

				// and close it
				fw.close();
			} catch (final IOException e) {
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

		}
	}

	/**
	 * the list of participants we manage
	 */
	XMLVarianceList _myVariances = null;

	/**
	 * our document
	 */
	private Document _myDocument;

	/**
	 * the number of permutations to produce
	 */
	private int _numPerms;

	/**
	 * the directory to place the new files
	 */
	protected String _myDirectory;

	/*****************************************************************************
	 * member methods
	 ****************************************************************************/

	/**
	 * the template to use for creating new filenames
	 */
	private String _myFileTemplate;

	/**
	 * allow the analyst to specify how many instances of each scenario he wants
	 */
	private long _maxPerms = -1;

	/*****************************************************************************
	 * constructor
	 ****************************************************************************/
	/**
	 * constructor, received a stream containing the list of variances we are going
	 * to manage
	 */
	public MultiScenarioGenerator(final Document doc) {
		this.loadVariances(doc);
	}

	/**
	 * apply my set of variances to this document
	 *
	 * @param newDoc the scenario to apply the variances to
	 * @return a hash-code representing the specific variances applied
	 * @throws XMLVariance.IllegalExpressionException
	 * @throws XMLVariance.MatchingException
	 */
	public String applyVariances(final Document newDoc)
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {
		// apply the variances to it
		final String thisHash = _myVariances.apply(null, newDoc);
		return thisHash;
	}

	private Document createClone(final String currentDoc) {
		Document res = null;
		// put the string into a stream
		final StringReader reader = new StringReader(currentDoc);
		final InputSource source = new InputSource(reader);

		// put the string into a stream
		res = ScenarioGenerator.readDocumentFrom(source);

		return res;
	}

	public final Document[] createNewRandomisedPermutations()
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {
		final Vector<Document> results = new Vector<Document>(0, 1);

		// take a copy of the document
		final String currentDoc = ScenarioGenerator.writeToString(_myDocument);

		// keep a record of the scenarios we create, since
		// the user may want to create a specific number of
		// permutations of each test-case
		final HashMap<String, Integer> map = new HashMap<String, Integer>();

		// keep track of number of compliant instances created
		int counter = 0;

		// keep track of how many we've tried to generate
		int attempts = 0;

		// set a max limit - to stop mad error conditions

		// loop through our permutations
		while (counter < _numPerms) {
			// create a clone of the provided document
			Document newDoc = createClone(currentDoc);

			attempts++;

			final String thisHash = applyVariances(newDoc);

			// have we already found this hash?
			boolean isValid = true;
			if (map.containsKey(thisHash)) {
				// yes, how many have been set?
				final Integer count = map.get(thisHash);

				// are we counting permutations
				if (_maxPerms != -1) {
					// so, how many are we at
					if (count.intValue() < _maxPerms) {
						isValid = true;
						// and increment the counter
						map.put(thisHash, new Integer(count.intValue() + 1));
					} else {
						isValid = false;
					}
				} else {
					// not tracking - don't bother
					isValid = true;
				}
			} else {
				// first time we've found this one
				isValid = true;

				// and create it
				map.put(thisHash, new Integer(1));
			}

			if (isValid) {
				//
				counter++;

				// rename the scenario - we only do it at this point since
				// there are situations where scenarios get ditched, we ignore those
				// in order to get a continuous series of scenario numbers
				renameScenario(newDoc, counter);

				// store the case description for this scenario
				setScenarioCase(newDoc, thisHash);

				// and now store it
				results.add(newDoc);

				// and announce the progress to the command line
				ScenarioGenerator.outputProgress(counter);
			} else {
				// just ditch it
				newDoc = null;
			}

			// include drop-dead case for a insolveable loop
			if (attempts > 2 * _numPerms) {
				System.err.println("Not possible to create valid number of acceptable permutations");
			}

		}

		// make sure the command line's on a new line
		System.out.println(" attempted:" + attempts + " created:" + counter);

		final Document[] res = results.toArray(new Document[] {});

		return res;
	}

	public final Document[] createNewRandomisedPermutationsOld()
			throws XMLVariance.IllegalExpressionException, XMLVariance.MatchingException {
		final Vector<Document> results = new Vector<Document>(0, 1);

		// take a copy of the document
		final String currentDoc = ScenarioGenerator.writeToString(_myDocument);

		// keep a record of the scenarios we create, since
		// the user may want to create a specific number of
		// permutations of each test-case
		final HashMap<String, Integer> map = new HashMap<String, Integer>();

		// keep track of number of compliant instances created
		int counter = 0;

		// keep track of how many we've tried to generate
		int attempts = 0;

		// set a max limit - to stop mad error conditions

		// loop through our permutations
		while (counter < _numPerms) {
			// create a clone of the provided document
			Document newDoc = createClone(currentDoc);

			attempts++;

			final String thisHash = applyVariances(newDoc);

			// have we already found this hash?
			boolean isValid = true;
			if (map.containsKey(thisHash)) {
				// yes, how many have been set?
				final Integer count = map.get(thisHash);

				// are we counting permutations
				if (_maxPerms != -1) {
					// so, how many are we at
					if (count.intValue() < _maxPerms) {
						isValid = true;
						// and increment the counter
						map.put(thisHash, new Integer(count.intValue() + 1));
					} else {
						isValid = false;
					}
				} else {
					// not tracking - don't bother
					isValid = true;
				}
			} else {
				// first time we've found this one
				isValid = true;

				// and create it
				map.put(thisHash, new Integer(1));
			}

			if (isValid) {
				//
				counter++;

				// rename the scenario - we only do it at this point since
				// there are situations where scenarios get ditched, we ignore those
				// in order to get a continuous series of scenario numbers
				renameScenario(newDoc, counter);

				// store the case description for this scenario
				setScenarioCase(newDoc, thisHash);

				// and now store it
				results.add(newDoc);

				// and announce the progress to the command line
				ScenarioGenerator.outputProgress(counter);
			} else {
				// just ditch it
				newDoc = null;
			}

			// include drop-dead case for a insolveable loop
			if (attempts > 2 * _numPerms) {
				System.err.println("Not possible to create valid number of acceptable permutations");
			}

		}

		// make sure the command line's on a new line
		System.out.println(" attempted:" + attempts + " created:" + counter);

		final Document[] res = results.toArray(new Document[] {});

		return res;
	}

	public OutputStream createOutput(final String title, final String directory) {
		OutputStream res = null;

		try {
			res = new FileOutputStream(directory + "\\" + title + ".xml");
		} catch (final FileNotFoundException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		return res;
	}

	public int getNumPerms() {
		return _numPerms;
	}

	public final Document getRawDocument() {
		return _myDocument;
	}

	/**
	 * read in the list of variances, and collate them into our list
	 */
	private void loadVariances(final Document document) {
		try {
			// get the root of our variances
			final XPathExpression xp2 = NamespaceContextProvider.createPath("//ScenarioGenerator/" + GENERATOR_TYPE);
			final NodeList nl = (NodeList) xp2.evaluate(document, XPathConstants.NODESET);
			final Element el = (Element) nl.item(0);

			// did we find it?
			if (el != null) {
				// retrieve our working values
				_myDirectory = el.getAttribute(OUTPUT_DIRECTORY);
				_myFileTemplate = el.getAttribute(NAME_TEMPLATE);
				final String numPerms = el.getAttribute(NUMBER_PERMS);
				_numPerms = Integer.parseInt(numPerms);

				// NOTE: there is also an Active attribute, but we actually use that at a level
				// of
				// processing above this:
				// to decide if we bother with multi-sceanrio genny at all

				final String maxPerms = el.getAttribute(MAX_INSTANCES);
				if (maxPerms != "") {
					_maxPerms = Integer.parseInt(maxPerms);
				}

				if (el != null) {
					// build up our list of variances from this document
					final NodeList lis = el.getElementsByTagName("VarianceList");

					final int len = lis.getLength();
					for (int i = 0; i < len; i++) {
						final Element o = (Element) lis.item(i);

						// and store it
						_myVariances = new XMLVarianceList(o);
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	protected String newScenarioName(final int index) {
		return _myFileTemplate + index;
	}

	/**
	 * rename the scenario using the supplied index
	 *
	 * @param newDoc document containing the scenario to edit
	 * @param index
	 */
	private void renameScenario(final Document newDoc, final int index) {
		// get the scenario object
		final NodeList list = newDoc.getElementsByTagName(ScenarioHandler.type);
		final Element scen = (Element) list.item(0);
		scen.setAttribute(SCENARIO_NAME_ATTRIBUTE, newScenarioName(index));
	}

	/**
	 * set the document we are going to be changing
	 */
	public final void setDocument(final Document rawDoc) {
		_myDocument = rawDoc;
	}

	/**
	 * set the document which we are going to be changing
	 */
	public final void setDocument(final InputStream istream) {
		// get a document from the istream
		Document thisDocument = null;
		try {
			thisDocument = ScenarioGenerator.readDocumentFrom(istream);
		} catch (final SAXException e) {
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// and store it
		setDocument(thisDocument);
	}

}
