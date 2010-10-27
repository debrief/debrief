package ASSET.GUI.CommandLine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import org.w3c.dom.Document;

import ASSET.ScenarioType;
import ASSET.GUI.CommandLine.CommandLine.ASSETProgressMonitor;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.LiveScenario.ISimulation;
import ASSET.Scenario.LiveScenario.ISimulationQue;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.InterScenarioObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.Observers.ScenarioStatusObserver;
import ASSET.Scenario.Observers.TimeObserver;
import ASSET.Util.SupportTesting;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.XML.ASSETReaderWriter;
import ASSET.Util.XML.ASSETReaderWriter.ResultsContainer;
import MWC.Algorithms.LiveData.IAttribute;

/**
 * Created by IntelliJ IDEA. User: Ian.Mayo Date: 02-Jun-2003 Time: 15:05:23
 * Class providing multi scenario support to the command line class Log:
 */

public class MultiScenarioCore implements ISimulationQue
{
	/**
	 * success code to prove it ran ok
	 */
	static int SUCCESS = 0;

	/**
	 * error code to return when we've rx the wrong parameters
	 */
	static int WRONG_PARAMETERS = 1;

	/**
	 * error code to return when we can't load our data
	 */
	static int PROBLEM_LOADING = 2;

	/**
	 * error code to indicate we couldn't find one of the files
	 */
	static int FILE_NOT_FOUND = 3;

	/**
	 * error code to indicate we couldn't create the output files
	 */
	static int TROUBLE_MAKING_FILES = 4;

	/**
	 * the scenario generator that does all the work
	 */
	private ScenarioGenerator _myGenny;

	/**
	 * the set of scenarios we're going to run through
	 */
	protected Vector<Document> _myScenarioDocuments;

	private Vector<InterScenarioObserverType> _theInterObservers;

	private Vector<ScenarioObserver> _thePlainObservers;

	private Vector<ScenarioObserver> _allObservers;

	private ResultsContainer _resultsStore;

	private Vector<InstanceWrapper> _theScenarios;

	private Vector<IAttribute> _myAttributes;

	private ScenarioStatusObserver _stateObserver;

	/**
	 * ok, get things up and running. Load the data-files
	 * 
	 * @param scenario
	 *          the scenario file
	 * @param control
	 *          the control file
	 * @param pMon
	 *          who tell what we're up to
	 * @param outputDirectory
	 *          where to write to
	 * @return null for success, message for failure
	 */
	private String setup(String scenario, String control,
			ASSETProgressMonitor pMon, File outputDirectory)
	{
		// ok, create our genny
		_myGenny = new ScenarioGenerator();

		// now create somewhere for the scenarios to go
		_myScenarioDocuments = new Vector<Document>(0, 1);

		// and now create the list of scenarios
		String res = _myGenny.createScenarios(scenario, control,
				_myScenarioDocuments, pMon, outputDirectory);

		return res;
	}

	/**
	 * write this set of scenarios to disk, for later examination
	 * 
	 * @param out
	 *          standard out
	 * @param err
	 *          error out
	 * @param in
	 *          input (to receive user input)
	 * @return success code (0) or failure codes
	 */
	private int writeToDisk(PrintStream out, PrintStream err, InputStream in)
	{
		int res = 0;
		// so,
		try
		{
			String failure = _myGenny.writeTheseToFile(_myScenarioDocuments, false);
			// just check for any other probs
			if (failure != null)
			{
				res = TROUBLE_MAKING_FILES;
			}
		}
		catch (Exception e)
		{
			res = TROUBLE_MAKING_FILES;
		}

		return res;
	}

	/**
	 * ok, let's get going...
	 * 
	 * @param out
	 * @param err
	 * @param scenarioRunningListener
	 */
	private int runAll(OutputStream out, OutputStream err, InputStream in,
			Document controlFile, NewScenarioListener listener)
	{
		int result = SUCCESS;

		final int scenarioLen = _myScenarioDocuments.size();

		// get the data we're after
		String controlStr = ScenarioGenerator.writeToString(_myGenny
				.getControlFile());
		InputStream controlStream = new ByteArrayInputStream(controlStr.getBytes());

		// ok, we've got our scenarios up and running, might as well run through
		// them
		int ctr = 0;
		ScenarioType oldScenario = null;
		boolean firstRun = true;
		for (Iterator<InstanceWrapper> iterator = _theScenarios.iterator(); iterator
				.hasNext();)
		{
			InstanceWrapper wrapper = iterator.next();
			ScenarioType thisS = wrapper.scenario;

			// tell the listener what's up
			if (listener != null)
				listener.newScenario(oldScenario, thisS);

			if (firstRun)
			{
				firstRun = false;
				// we don't need to initialise the listeners for the first scenario, it
				// gets done in advance.
			}
			else
			{
				// get the observers sorted
				wrapper.initialise(_allObservers);
			}

			// now run this one
			CommandLine runner = wrapper.commandLine;

			System.out.print("Run " + (ctr + 1) + " of " + scenarioLen + " ");

			// now set the seed
			thisS.setSeed(_resultsStore.randomSeed);

			// and get going....
			runner.run();

			// ok, tell the observers it's time for bed
			for (int i = 0; i < _allObservers.size(); i++)
			{
				CoreObserver thisObs = (CoreObserver) _allObservers.elementAt(i);

				// go for it
				thisObs.tearDown(runner.getScenario());
			}

			// and remove the observers
			runner.clearObservers();

			try
			{
				// and reset the control stream
				controlStream.reset();
			}
			catch (IOException e)
			{
				e.printStackTrace(); // To change body of catch statement use Options |
				// File Templates.
			}

			// and remember the scenario
			oldScenario = thisS;

			ctr++;
		}

		// ok, everything's finished running. Just have a pass through to
		// close any i-scenario observers
		for (int thisObs = 0; thisObs < _theInterObservers.size(); thisObs++)
		{
			ScenarioObserver scen = _theInterObservers.elementAt(thisObs);
			if (scen.isActive())
			{
				InterScenarioObserverType obs = _theInterObservers.elementAt(thisObs);
				obs.finish();
			}
		}

		return result;
	}

	/**
	 * member method, effectively to handle "main" processing.
	 * 
	 * @param args
	 *          the arguments we received from the command line
	 * @param out
	 *          standard out
	 * @param err
	 *          error out
	 * @param in
	 *          input (to receive user input)
	 * @param pMon
	 * @param outputDirectory
	 *          - where to put the working files
	 * @return success code (0) or failure codes
	 */

	public int prepareFiles(String controlFile, String scenarioFile,
			PrintStream out, PrintStream err, InputStream in,
			ASSETProgressMonitor pMon, File outputDirectory)
	{
		int resCode = 0;

		// do a little tidying
		_myAttributes = null;
		_theInterObservers = null;
		_thePlainObservers = null;

		System.out.println("about to generate scenarios");

		// and set it up (including generating the scenarios)
		String res = setup(scenarioFile, controlFile, pMon, outputDirectory);

		if (res != null)
		{
			// see what it was, file not found?
			if (res.indexOf("not found") >= 0)
			{
				err.println("Problem finding control file:" + res);
				resCode = FILE_NOT_FOUND;
			}
			else
			{
				err.println("Problem loading multi-scenario generator:" + res);
				resCode = PROBLEM_LOADING;
			}
		}
		else
		{
			out.println("about to write new scenarios to disk");

			pMon.beginTask("Writing generated scenarios to disk", 1);

			// ok, now write the scenarios to disk
			resCode = writeToDisk(out, err, in);

			pMon.worked(1);

			// and let our generator ditch some gash
			// _myGenny = null;

			// there was lots of stuff read in by the scenario generator. Whilst
			// we've removed our only reference to
			// it on the previous line, the system won't necessarily do a GC just
			// yet - so we'll trigger an artificial one.
			System.gc();

			if (resCode != SUCCESS)
			{
				if (resCode == TROUBLE_MAKING_FILES)
				{
					err
							.println("Failed to write new scenarios to disk.  Is an old copy of an output file currently open?");
					err
							.println("  Alternately, is a file-browser currently looking at the output directory?");
				}
			}
		}

		return resCode;
	}

	public int prepareControllers(ResultsContainer multiRunResultsStore,
			ASSETProgressMonitor pMon)
	{
		int resCode = 0;

		_resultsStore = multiRunResultsStore;

		// sort out observers (inter & intra)
		_theInterObservers = new Vector<InterScenarioObserverType>(0, 1);
		_thePlainObservers = new Vector<ScenarioObserver>();

		// start off by generating the time/state observers that we create for
		// everybody
		_stateObserver = new ScenarioStatusObserver();
		_thePlainObservers.add(_stateObserver);
		_thePlainObservers.add(new TimeObserver());

		// also add those from the file
		Vector<ScenarioObserver> theObservers = _resultsStore.observerList;
		for (int i = 0; i < theObservers.size(); i++)
		{
			ScenarioObserver observer = theObservers.elementAt(i);
			if (observer instanceof InterScenarioObserverType)
			{
				_theInterObservers.add((InterScenarioObserverType) observer);
			}
			else
				_thePlainObservers.add(observer);
		}

		// also collate the collected set of observers
		// combine the two sets of observers
		_allObservers = new Vector<ScenarioObserver>();
		_allObservers.addAll(_theInterObservers);
		_allObservers.addAll(_thePlainObservers);

		// also read in the collection of scenarios
		_theScenarios = new Vector<InstanceWrapper>(0, 1);

		pMon
				.beginTask("Reading in block of scenarios", _myScenarioDocuments.size());

		for (Iterator<Document> iterator = _myScenarioDocuments.iterator(); iterator
				.hasNext();)
		{
			Document thisD = iterator.next();
			String scenarioStr = ScenarioGenerator.writeToString(thisD);
			InputStream scenarioStream = new ByteArrayInputStream(scenarioStr
					.getBytes());
			CoreScenario newS = new CoreScenario();
			ASSETReaderWriter.importThis(newS, null, scenarioStream);
			// wrap the scenario
			CommandLine runner = new CommandLine(newS);

			InstanceWrapper wrapper = new InstanceWrapper(newS, runner);

			_theScenarios.add(wrapper);
			pMon.worked(1);
		}

		// ok, everything's loaded. Just have a pass through to
		// initialise any intra-scenario observers
		for (int thisObs = 0; thisObs < _theInterObservers.size(); thisObs++)
		{
			ScenarioObserver scen = _theInterObservers.elementAt(thisObs);
			if (scen.isActive())
			{
				InterScenarioObserverType obs = (InterScenarioObserverType) scen;
				// is it active?
				obs.initialise(_resultsStore.outputDirectory);
			}
		}

		// right, just setup the listeners for the first scenario, so it can be
		// controlled form
		// the time controller
		if (!_theScenarios.isEmpty())
		{
			InstanceWrapper firstS = _theScenarios.firstElement();
			firstS.initialise(_allObservers);
		}

		return resCode;
	}

	public int nowRun(PrintStream out, PrintStream err, InputStream in,
			NewScenarioListener scenarioListener)
	{
		return runAll(out, err, in, _myGenny.getControlFile(), scenarioListener);
	}

	// //////////////////////////////////////////////////////////
	// testing stuff
	// //////////////////////////////////////////////////////////
	public static class MultiServerTest extends SupportTesting
	{
		public MultiServerTest(final String val)
		{
			super(val);
		}

		public void testValidStartup()
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ByteArrayOutputStream bes = new ByteArrayOutputStream();

			PrintStream out = new PrintStream(bos);
			PrintStream err = new PrintStream(bes);
			InputStream in = new ByteArrayInputStream(new byte[]
			{});

			bos.reset();
			bes.reset();
			String[] args = new String[2];
			args[1] = "src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
			args[0] = "src/ASSET/Util/MonteCarlo/test_variance1.xml";
			// args[1] =
			// "..\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance1.xml";
			MultiScenarioCore scen = new MultiScenarioCore();
			ASSETProgressMonitor pMon = new ASSETProgressMonitor()
			{
				public void beginTask(String name, int totalWork)
				{
				}

				public void worked(int work)
				{
				}
			};
			int res = scen.prepareFiles(args[0], args[1], out, err, in, pMon, null);
			assertEquals("ran ok", SUCCESS, res);

			// check the contents of the error message
			assertEquals("no error reported", 0, bes.size());

			// check the scenarios got created
			Vector<Document> scenarios = scen._myScenarioDocuments;
			assertEquals("scenarios got created", 3, scenarios.size());
		}

		public void testCommandLineMainProcessing()
		{
			String[] args = new String[2];
			args[0] = "src/ASSET/Util/MonteCarlo/test_variance_scenario.xml";
			args[1] = "src/ASSET/Util/MonteCarlo/test_variance_realistic.xml";

			CommandLine.main(args);
		}
	}

	// //////////////////////////////////////////////////////////
	// and now the main method
	// //////////////////////////////////////////////////////////

	/**
	 * main method, of course - decides whether to handle this ourselves, or to
	 * pass it on to the command line
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		MultiServerTest tm = new MultiServerTest("me");
		SupportTesting.callTestMethods(tm);
	}

	@Override
	public Vector<IAttribute> getAttributes()
	{
		if (_myAttributes == null)
		{
			// look at our observers, find any attributes
			_myAttributes = new Vector<IAttribute>();

			// start off with the single-scenario observers
			for (Iterator<ScenarioObserver> iterator = _thePlainObservers.iterator(); iterator
					.hasNext();)
			{
				ScenarioObserver thisS = iterator.next();
				if (thisS instanceof IAttribute)
					_myAttributes.add((IAttribute) thisS);
			}

			// now the multi-scenario observers
			for (Iterator<InterScenarioObserverType> iterator = _theInterObservers
					.iterator(); iterator.hasNext();)
			{
				InterScenarioObserverType thisS = iterator.next();
				if (thisS instanceof IAttribute)
					_myAttributes.add((IAttribute) thisS);
			}

		}
		// done.
		return _myAttributes;
	}

	@Override
	public Vector<ISimulation> getSimulations()
	{
		Vector<ISimulation> res = new Vector<ISimulation>();
		for (Iterator<InstanceWrapper> iter = _theScenarios.iterator(); iter
				.hasNext();)
			res.add((ISimulation) iter.next().scenario);
		// return my list of simulations
		return res;
	}

	@Override
	public boolean isRunning()
	{
		return false;
	}

	public void startQue(NewScenarioListener listener)
	{
		// ok, go for it
		nowRun(System.out, System.err, System.in, listener);
	}

	@Override
	public void stopQue()
	{
	}

	@Override
	public IAttribute getState()
	{
		return _stateObserver;
	}

	protected static class InstanceWrapper
	{
		final ScenarioType scenario;
		final CommandLine commandLine;

		public InstanceWrapper(ScenarioType theScenario, CommandLine theCommandLine)
		{
			scenario = theScenario;
			commandLine = theCommandLine;
		}

		public void initialise(Vector<ScenarioObserver> allObservers)
		{

			// ok, get the scenario, so we can set up our observers
			for (int i = 0; i < allObservers.size(); i++)
			{
				CoreObserver thisObs = (CoreObserver) allObservers.elementAt(i);

				// and set it up
				thisObs.setup(scenario);

				// and add to the runner
				commandLine.addObserver(thisObs);
			}
		}
	}

}
