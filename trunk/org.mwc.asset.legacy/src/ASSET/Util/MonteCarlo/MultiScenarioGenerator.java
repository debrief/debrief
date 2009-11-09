/*
 * Created by IntelliJ IDEA.
 * User: administrator
 * Date: Oct 31, 2001
 * Time: 1:22:06 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package ASSET.Util.MonteCarlo;

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

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.XMLVariance.NamespaceContextProvider;
import ASSET.Util.XML.ScenarioHandler;

/**
 * list of items in a particular file which may be changed
 */

public final class MultiScenarioGenerator
{
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

	private static final String OUTPUT_DIRECTORY = "OutputDirectory";

	private static final String NAME_TEMPLATE = "NameTemplate";

	private static final String NUMBER_PERMS = "Number";

	private static final String MAX_INSTANCES = "MaxPerms";

	/**
	 * the directory to place the new files
	 */
	protected String _myDirectory;

	/**
	 * the template to use for creating new filenames
	 */
	private String _myFileTemplate;

	public static final String GENERATOR_TYPE = "MultiScenarioGenerator";

	/**
	 * allow the analyst to specify how many instances of each scenario he wants
	 */
	private long _maxPerms = -1;

	public static final String SCENARIO_NAME_ATTRIBUTE = "Name";

	private static final String SCENARIO_CASE_ATTRIBUTE = "Case";

	/*****************************************************************************
	 * constructor
	 ****************************************************************************/
	/**
	 * constructor, received a stream containing the list of variances we are
	 * going to manage
	 */
	public MultiScenarioGenerator(Document doc)
	{
		this.loadVariances(doc);
	}

	/*****************************************************************************
	 * member methods
	 ****************************************************************************/

	/**
	 * read in the list of variances, and collate them into our list
	 */
	private void loadVariances(final Document document)
	{
		try
		{
			// get the root of our variances
			XPathExpression xp2 = NamespaceContextProvider.createPath("//ScenarioGenerator/" + GENERATOR_TYPE);
			NodeList nl = (NodeList) xp2.evaluate(document, XPathConstants.NODESET);
			Element el = (Element) nl.item(0);

			// retrieve our working values
			_myDirectory = el.getAttribute(OUTPUT_DIRECTORY);
			_myFileTemplate = el.getAttribute(NAME_TEMPLATE);
			String numPerms = el.getAttribute(NUMBER_PERMS);
			_numPerms = Integer.parseInt(numPerms);

			String maxPerms = el.getAttribute(MAX_INSTANCES);
			if (maxPerms != "")
			{
				_maxPerms = Integer.parseInt(maxPerms);
			}

			if (el != null)
			{
				// build up our list of variances from this document
				final NodeList lis = el.getElementsByTagName("VarianceList");

				final int len = lis.getLength();
				for (int i = 0; i < len; i++)
				{
					final Element o = (Element) lis.item(i);

					// and store it
					_myVariances = new XMLVarianceList(o);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getNumPerms()
	{
		return _numPerms;
	}
	
	/**
	 * set the document we are going to be changing
	 */
	public final void setDocument(final Document rawDoc)
	{
		_myDocument = rawDoc;
	}

	/**
	 * set the document which we are going to be changing
	 */
	public final void setDocument(final InputStream istream)
	{
		// get a document from the istream
		Document thisDocument = null;
		try
		{
			thisDocument = ScenarioGenerator.readDocumentFrom(istream);
		}
		catch (SAXException e)
		{
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		// and store it
		setDocument(thisDocument);
	}

	public final Document getRawDocument()
	{
		return _myDocument;
	}

	protected String newScenarioName(int index)
	{
		return _myFileTemplate + index;
	}

  public final Document[] createNewRandomisedPermutations() throws XMLVariance.IllegalExpressionException,
    XMLVariance.MatchingException
  {
		Vector<Document> results = new Vector<Document>(0, 1);


    // take a copy of the document
    final String currentDoc = ScenarioGenerator.writeToString(_myDocument);

    // keep a record of the scenarios we create, since
    // the user may want to create a specific number of
    // permutations of each test-case
    HashMap<String, Integer> map = new HashMap<String, Integer>();

    // keep track of number of compliant instances created
    int counter = 0;

    // keep track of how many we've tried to generate
    int attempts = 0;

    // set a max limit - to stop mad error conditions

    // loop through our permutations
    while (counter < _numPerms)
    {
      // create a clone of the provided document
      Document newDoc = createClone(currentDoc);

      attempts++;

      String thisHash = applyVariances(newDoc);

      // have we already found this hash?
      boolean isValid = true;
      if (map.containsKey(thisHash))
      {
        // yes, how many have been set?
        Integer count = map.get(thisHash);

        // are we counting permutations
        if (_maxPerms != -1)
        {
          // so, how many are we at
          if (count.intValue() < _maxPerms)
          {
            isValid = true;
            // and increment the counter
            map.put(thisHash, new Integer(count.intValue() + 1));
          }
          else
          {
            isValid = false;
          }
        }
        else
        {
          // not tracking - don't bother
          isValid = true;
        }
      }
      else
      {
        // first time we've found this one
        isValid = true;

        // and create it
        map.put(thisHash, new Integer(1));
      }

      if (isValid)
      {
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
      }
      else
      {
        // just ditch it
        newDoc = null;
      }

      // include drop-dead case for a insolveable loop
      if (attempts > 2 * _numPerms)
      {
        System.err.println("Not possible to create valid number of acceptable permutations");
      }

    }

    // make sure the command line's on a new line
    System.out.println(" attempted:" + attempts + " created:" + counter);

    Document[] res = results.toArray(new Document[]{});

    return res;
  }



	public final Document[] createNewRandomisedPermutationsOld()
			throws XMLVariance.IllegalExpressionException,
			XMLVariance.MatchingException
	{
		Vector<Document> results = new Vector<Document>(0, 1);

		// take a copy of the document
		final String currentDoc = ScenarioGenerator.writeToString(_myDocument);

		// keep a record of the scenarios we create, since
		// the user may want to create a specific number of
		// permutations of each test-case
		HashMap<String, Integer> map = new HashMap<String, Integer>();

		// keep track of number of compliant instances created
		int counter = 0;

		// keep track of how many we've tried to generate
		int attempts = 0;

		// set a max limit - to stop mad error conditions

		// loop through our permutations
		while (counter < _numPerms)
		{
			// create a clone of the provided document
			Document newDoc = createClone(currentDoc);

			attempts++;

			String thisHash = applyVariances(newDoc);

			// have we already found this hash?
			boolean isValid = true;
			if (map.containsKey(thisHash))
			{
				// yes, how many have been set?
				Integer count = map.get(thisHash);

				// are we counting permutations
				if (_maxPerms != -1)
				{
					// so, how many are we at
					if (count.intValue() < _maxPerms)
					{
						isValid = true;
						// and increment the counter
						map.put(thisHash, new Integer(count.intValue() + 1));
					}
					else
					{
						isValid = false;
					}
				}
				else
				{
					// not tracking - don't bother
					isValid = true;
				}
			}
			else
			{
				// first time we've found this one
				isValid = true;

				// and create it
				map.put(thisHash, new Integer(1));
			}

			if (isValid)
			{
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
			}
			else
			{
				// just ditch it
				newDoc = null;
			}

			// include drop-dead case for a insolveable loop
			if (attempts > 2 * _numPerms)
			{
				System.err
						.println("Not possible to create valid number of acceptable permutations");
			}

		}

		// make sure the command line's on a new line
		System.out.println(" attempted:" + attempts + " created:" + counter);

		Document[] res = results.toArray(new Document[]
		{});

		return res;
	}

	/**
	 * apply my set of variances to this document
	 * 
	 * @param newDoc
	 *          the scenario to apply the variances to
	 * @return a hash-code representing the specific variances applied
	 * @throws XMLVariance.IllegalExpressionException
	 * @throws XMLVariance.MatchingException
	 */
	public String applyVariances(Document newDoc)
			throws XMLVariance.IllegalExpressionException,
			XMLVariance.MatchingException
	{
		// apply the variances to it
		String thisHash = _myVariances.apply(null, newDoc);
		return thisHash;
	}

	/**
	 * method to write a list of scenarios to file, each in their own directory
	 * 
	 * @param scenarios
	 *          the list of scenarios to write to file
	 * @param path_prefix
	 *          the path to put the scenarios into
	 */
	public static void writeTheseToFile(Vector<Document> scenarios,
			String path_prefix)
	{
		for (int counter = 0; counter < scenarios.size(); counter++)
		{
			Document thisDoc = scenarios.elementAt(counter);

			String asString = ScenarioGenerator.writeToString(thisDoc);

			// and output this string to file
			int thisId = counter + 1;

			// create the path to the new file
			String thePath = path_prefix + "/" + thisId + "/" + "scen_" + thisId;

			// declare it as a file
			File outFile = new File(thePath);

			// create any parent directories we need
			outFile.mkdirs();

			try
			{
				// put it into a writer
				FileWriter fw = new FileWriter(outFile);

				// write it out
				fw.write(asString);

				// and close it
				fw.close();
			}
			catch (IOException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

		}
	}

	/**
	 * rename the scenario using the supplied index
	 * 
	 * @param newDoc
	 *          document containing the scenario to edit
	 * @param index
	 */
	private void renameScenario(Document newDoc, int index)
	{
		// get the scenario object
		NodeList list = newDoc.getElementsByTagName(ScenarioHandler.SCENARIO_NAME);
		Element scen = (Element) list.item(0);
		scen.setAttribute(SCENARIO_NAME_ATTRIBUTE, newScenarioName(index));
	}

	/**
	 * rename the scenario using the supplied index
	 * 
	 * @param newDoc
	 *          document containing the scenario to edit
	 * @param caseId
	 *          a description of this specific case
	 */
	private static void setScenarioCase(Document newDoc, String caseId)
	{
		// get the scenario object
		NodeList list = newDoc.getElementsByTagName(ScenarioHandler.SCENARIO_NAME);
		Element scen = (Element) list.item(0);
		scen.setAttribute(SCENARIO_CASE_ATTRIBUTE, caseId);
	}

	private Document createClone(final String currentDoc)
	{
		Document res = null;
		// put the string into a stream
		final StringReader reader = new StringReader(currentDoc);
		InputSource source = new InputSource(reader);

		// put the string into a stream
		res = ScenarioGenerator.readDocumentFrom(source);

		return res;
	}

	public OutputStream createOutput(String title, String directory)
	{
		OutputStream res = null;

		try
		{
			res = new FileOutputStream(directory + "\\" + title + ".xml");
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace(); // To change body of catch statement use Options |
			// File Templates.
		}

		return res;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////
	// testing for this class
	// ////////////////////////////////////////////////////////////////////////////////////////////////
	public static final class MultiScenarioGennyTest extends SupportTesting
	{

		public MultiScenarioGennyTest(final String val)
		{
			super(val);
		}

		public final void testLoadVariances()
		{
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root
					+ "/ASSET/Util/MonteCarlo/";
			
			System.out.println("curent dir is:" + System.getProperty("user.dir"));

			// create server
			Document document = null;
			try
			{
				document = ScenarioGenerator.readDocumentFrom(new FileInputStream(
						docPath + "test_variance1.xml"));
			}
			catch (SAXException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			final MultiScenarioGenerator xv = new MultiScenarioGenerator(document);

			// ok - check we've loaded our stuff
			assertEquals("load participants", 2, xv._myVariances.size(), 0);

			// get the first part
			XMLVariance xa = xv._myVariances.itemAt(0);

			assertEquals("got correct variance object", "do ita1", xa.getName());
			// now try for the range object itself
			XMLObject op = xa.getObject();
			assertEquals("got correct type", op.getName(), "Value");
			assertTrue("is of attribute type", op instanceof XMLAttribute);

			// //////////////////////////////////////////////////////////
			// first the range object
			// //////////////////////////////////////////////////////////

			// now look into the range itself
			XMLAttribute xmla = (XMLAttribute) op;
			assertTrue("is of range type", xmla.getOperation() instanceof XMLRange);
			XMLRange xr = (XMLRange) xmla.getOperation();

			// check the data settings
			assertEquals("correct min", 1, xr.getMin(), 0);
			assertEquals("correct max", 6, xr.getMax(), 0);
			assertEquals("correct step", 0.5, xr.getStep().doubleValue(), 0);

			// //////////////////////////////////////////////////////////
			// now the choice
			// //////////////////////////////////////////////////////////
			XMLVariance xa2 = xv._myVariances.itemAt(1);
			assertEquals("got correct variance object", "do ita2", xa2.getName());
			// now try for the range object itself
			XMLObject op2 = xa2.getObject();
			assertEquals("got correct type", op2.getName(), "Course");
			assertTrue("is of attribute type", op2 instanceof XMLAttribute);

			// now look into the range itself
			xmla = (XMLAttribute) op2;
			assertTrue("is of choice type", xmla.getOperation() instanceof XMLChoice);
			final XMLChoice xc = (XMLChoice) xmla.getOperation();

			// check the data settings
			assertEquals("got correct num choices", 2, xc.size(), 0);
			assertEquals("first choice correct", "180", xc.get(0));
			assertEquals("first choice correct", "090", xc.get(1));
		}

		public final void testPerformVariances()
		{

			// get the file to read in
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root
					+ "/ASSET/Util/MonteCarlo/";

			InputStream dataStream = null;
			InputStream varianceStream = null;
			try
			{
				dataStream = new FileInputStream(docPath + "test_variance_scenario.xml");
				varianceStream = new FileInputStream(docPath + "test_variance1.xml");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// check it worked
			assertNotNull("managed to load data file", dataStream);
			assertNotNull("managed to load variance file", varianceStream);

			Document thisDocument = null;
			try
			{
				thisDocument = ScenarioGenerator.readDocumentFrom(dataStream);
			}
			catch (SAXException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			assertNotNull("we loaded document ok", thisDocument);

			Document document = null;
			try
			{
				document = ScenarioGenerator.readDocumentFrom(varianceStream);
			}
			catch (SAXException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			final MultiScenarioGenerator genny = new MultiScenarioGenerator(document);

			assertEquals("loaded vars", 2, genny._myVariances.size(), 0);

			// now fill the genny
			genny.setDocument(thisDocument);

			boolean worked = true;
			Document[] results = null;

			// store the original speed for ssn bravo - so that we can check it's
			// changed
			double originalSpeed = Double.NaN;

			// did we change the speed?
			try
			{
				XPathExpression xp2 = NamespaceContextProvider.createPath("//*[@Name='bravo']/Status/Speed");
				NodeList nl = (NodeList) xp2.evaluate(thisDocument,
						XPathConstants.NODESET);
				Element thisA = (Element) nl.item(0);
				String val = thisA.getAttribute("Value");
				originalSpeed = Double.parseDouble(val);

			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			try
			{
				// ok, now create the instances
				results = genny.createNewRandomisedPermutations();
			}
			catch (XMLVariance.IllegalExpressionException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
				worked = false;
			}
			catch (XMLVariance.MatchingException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
				worked = false;
			}

			assertTrue("check no exceptions thrown", worked);

			assertNotNull("got some documents", results);
			assertEquals("correct num scenarios", 3, results.length);

			// hey, let's see how we got on
			try
			{
				for (int i = 0; i < 3; i++)
				{
					// SupportTesting.outputThis(results[i], "permutation:" + i);
				}

				Document resDocument = results[0];

				XPathFactory xpf = XPathFactory.newInstance();
				XPath xp = xpf.newXPath();
				XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='bravo']");
				NodeList nl = (NodeList) xp2.evaluate(resDocument,
						XPathConstants.NODESET);
				Element thisA = (Element) nl.item(0);
				assertNotNull("found our participant", thisA);
				assertEquals("correct name", "bravo", thisA.getAttribute("Name"));

				// did we change the speed?
				xp = xpf.newXPath();
				xp2 = NamespaceContextProvider.createPath("//*[@Name='bravo']/Status/Speed");
				nl = (NodeList) xp2.evaluate(resDocument, XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				assertTrue("correct name", Double.parseDouble(thisA
						.getAttribute("Value")) != originalSpeed);
			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// lastly, check we can export the scenarios
			// re-check the len
			assertEquals("got some", 3, results.length);

		}

		public final void testPerformForceProtectionVariances()
		{

			// get the file to read in
			String code_root = System.getProperty("CODE_ROOT");
			if (code_root == null)
				code_root = "src";

			final String docPath = code_root
					+ "/ASSET/Util/MonteCarlo/";

			InputStream dataStream = null;
			InputStream varianceStream = null;
			try
			{
				dataStream = new FileInputStream(docPath
						+ "test_variance_scenario_area.xml");
				varianceStream = new FileInputStream(docPath + "test_variance_area.xml");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// check it worked
			assertNotNull("managed to load data file", dataStream);
			assertNotNull("managed to load variance file", varianceStream);

			Document thisScenarioDocument = null;
			try
			{
				thisScenarioDocument = ScenarioGenerator.readDocumentFrom(dataStream);
			}
			catch (SAXException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			assertNotNull("we loaded document ok", thisScenarioDocument);

			// examine the first scenario generated. see if the bits loaded
			try
			{

				XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/Helo//Investigate");
				NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument,
						XPathConstants.NODESET);
				Element thisE = (Element) nl.item(0);
				assertNotNull("found our investigate behaviour", thisE);
				assertEquals("correct name", "Find fishermen", thisE
						.getAttribute("Name"));
				assertEquals("correct name", "Identified", thisE
						.getAttribute("DetectionLevel"));

				xp2 = NamespaceContextProvider.createPath("//Participants/Helo//Investigate//Type");
				nl = (NodeList) xp2.evaluate(thisScenarioDocument,
						XPathConstants.NODESET);
				thisE = (Element) nl.item(0);
				assertEquals("correct type", "FISHING_VESSEL", thisE
						.getAttribute("Name"));

			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use File |
				// Settings | File Templates.
			}

			Document thisVarianceDocument = null;
			try
			{
				thisVarianceDocument = ScenarioGenerator
						.readDocumentFrom(varianceStream);
			}
			catch (SAXException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			final MultiScenarioGenerator genny = new MultiScenarioGenerator(
					thisVarianceDocument);

			assertTrue("loaded vars", genny._myVariances.size() > 0);

			// can we find a scenario generator?
			try
			{
				XPathExpression xp2 = NamespaceContextProvider.createPath("//MultiParticipantGenerator");
				NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument,
						XPathConstants.NODESET);
				Element el = (Element) nl.item(0);
				if (el != null)
				{
					MultiParticipantGenerator participantGenny = new MultiParticipantGenerator(
							thisVarianceDocument);
					assertNotNull("didn't load participant genny", participantGenny);
				}
			}
			catch (Exception e)
			{
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
			try
			{

				XPathExpression xp2 = NamespaceContextProvider.createPath("//Environment");
				NodeList nl = (NodeList) xp2.evaluate(thisScenarioDocument,
						XPathConstants.NODESET);
				Element thisA = (Element) nl.item(0);
				String val = thisA.getAttribute("SeaState");
				originalSeaState = Integer.parseInt(val);
			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			try
			{
				// ok, now create the instances
				results = genny.createNewRandomisedPermutations();
			}
			catch (XMLVariance.IllegalExpressionException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
				worked = false;
			}
			catch (XMLVariance.MatchingException e)
			{
				System.err.println("XPath identifier failed" + e.getMessage());
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
				worked = false;
			}

			int numScenariosRequested = -1;

			// did we load the proximity observer
			try
			{
				XPathExpression xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver");
				NodeList nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				Element thisA = (Element) nl.item(0);
				String val = thisA.getAttribute("Name");
				assertEquals("correct name", "HELO detected within SAM range", val);
				val = thisA.getAttribute("Active");
				assertTrue("set as active", true);

				xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver/Target/TargetType/Type[1]");
				nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				val = thisA.getAttribute("Name");
				assertEquals("correct name", "HELICOPTER", val);

				xp2 = NamespaceContextProvider.createPath("//MultiScenarioGenerator");
				nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				val = thisA.getAttribute("Number");
				assertTrue("found number", val.length() > 0);
				numScenariosRequested = new Integer(val).intValue();

				xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver/Watch/TargetType/Type[1]");
				nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				val = thisA.getAttribute("Name");
				assertEquals("correct name", "FISHING_VESSEL", val);

				xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver/Watch/TargetType/Type[2]");
				nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				val = thisA.getAttribute("Name");
				assertEquals("correct name", "RED", val);

				xp2 = NamespaceContextProvider.createPath("//StopOnProximityDetectionObserver/Range");
				nl = (NodeList) xp2.evaluate(thisVarianceDocument,
						XPathConstants.NODESET);
				thisA = (Element) nl.item(0);
				val = thisA.getAttribute("Units");
				assertEquals("correct name", "nm", val);
				val = thisA.getAttribute("Value");
				assertEquals("correct name", "2.5", val);

			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			assertTrue("check no exceptions thrown", worked);

			assertNotNull("got some documents", results);
			assertEquals("correct num scenarios", numScenariosRequested,
					results.length);

			// keep track of if a different sea state gets generated
			boolean foundDifferentSeaState = false;

			// hey, let's see how we got on
			try
			{
				for (int i = 0; i < results.length; i++)
				{

					Document resDocument = results[i];

					XPathExpression xp2 = NamespaceContextProvider.createPath("//Participants/*[@Name='SAM_FISHER']");
					NodeList nl = (NodeList) xp2.evaluate(resDocument,
							XPathConstants.NODESET);
					Element thisE = (Element) nl.item(0);
					assertNotNull("found our participant", thisE);
					assertEquals("correct name", "SAM_FISHER", thisE.getAttribute("Name"));

					// did we change the sea state?
					xp2 = NamespaceContextProvider.createPath("//Environment");
					nl = (NodeList) xp2.evaluate(resDocument,
							XPathConstants.NODESET);
					thisE = (Element) nl.item(0);

					// is this different?
					final int thisSeaState = Integer.parseInt(thisE
							.getAttribute("SeaState"));

					if (thisSeaState != originalSeaState)
					{
						foundDifferentSeaState = true;
						continue;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// check we found a different sea state
			assertTrue("we generated a different sea state", foundDifferentSeaState);

			// re-check the len
			assertEquals("got some", numScenariosRequested, results.length);

		}

	}

}
