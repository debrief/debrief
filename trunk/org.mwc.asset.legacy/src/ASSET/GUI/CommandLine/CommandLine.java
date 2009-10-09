package ASSET.GUI.CommandLine;

import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.CoreObserver;
import ASSET.Scenario.Observers.RecordToFileObserverType;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Util.MonteCarlo.MultiParticipantGenerator;
import ASSET.Util.MonteCarlo.MultiScenarioGenerator;
import ASSET.Util.SupportTesting;
import ASSET.Util.XML.ASSETReaderWriter;
import MWC.GenericData.WorldLocation;

import java.io.*;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 02-Jun-2003
 * Time: 15:05:23
 * Class providing command line access to the ASSET engine
 * Log:
 * $Log: CommandLine.java,v $
 * Revision 1.2  2006/11/06 15:00:25  Ian.Mayo
 * Only use hard-coded if we don't receive any
 *
 * Revision 1.1  2006/08/08 14:21:07  Ian.Mayo
 * Second import
 *
 * Revision 1.1  2006/08/07 12:25:15  Ian.Mayo
 * First versions
 *
 * Revision 1.22  2005/04/14 13:24:57  Ian.Mayo
 * Don't initialise with completely flat earth
 *
 * Revision 1.21  2004/11/01 14:12:55  Ian.Mayo
 * Don't bother hard-coding filenames int
 * <p/>
 * Revision 1.20  2004/10/08 08:20:06  Ian.Mayo
 * Provide proper support for Pause and Stop of Scenario
 * <p/>
 * Revision 1.19  2004/08/31 09:35:38  Ian.Mayo
 * Rename inner static tests to match signature **Test to make automated testing more consistent
 * <p/>
 * Revision 1.18  2004/08/10 15:17:52  Ian.Mayo
 * Recognise that we now pass a reason for the scenario closing
 * <p/>
 * Revision 1.17  2004/08/09 15:37:57  Ian.Mayo
 * Trim spaces from arguments
 * <p/>
 * Revision 1.16  2004/08/02 15:02:05  Ian.Mayo
 * Correct hard-coded path name
 * <p/>
 * Revision 1.15  2004/06/07 14:55:06  Ian.Mayo
 * Dummy change to test CVS Commit
 * <p/>
 * Revision 1.14  2004/05/24 15:01:31  Ian.Mayo
 * Lots of tidying
 * <p/>
 * Revision 1.2  2004/04/03 21:56:58  ian
 * Handle failure better
 * <p/>
 * Revision 1.1.1.1  2004/03/04 20:30:49  ian
 * no message
 * <p/>
 * Revision 1.13  2003/11/05 14:28:40  Ian.Mayo
 * correct path for testing
 * <p/>
 * Revision 1.12  2003/11/05 09:20:50  Ian.Mayo
 * General improvements
 * <p/>
 * Revision 1.10  2003/09/23 14:55:50  Ian.Mayo
 * remove unnecessary test
 * <p/>
 * Revision 1.9  2003/09/04 14:42:12  Ian.Mayo
 * Correct header comment
 */
public class CommandLine
{

  /**********************************************************************
   * local methods
   *********************************************************************/

  /**
   * message for file not specified
   */
  static public final String FILE_NOT_SPECIFIED = " not specified";

  /**
   * message for file not found
   */
  static public final String FILE_NOT_FOUND = " not found";

  /**
   * message for invalid file found
   */
  static public final String FILE_INVALID = " is invalid";

  /**
   * the scenario we are going to run
   */
  private final CoreScenario _myScenario;

  /**
   * the list of observers loaded from the control file
   */
  private Vector<ScenarioObserver> _myObservers;

  /**
   * keep track of whether we're alive or not
   */
  protected boolean _isRunning = false;

  /**
   * *******************************************************************
   * constructor - receives parsed elements
   * *******************************************************************
   */
  public CommandLine()
  {
  	this( new CoreScenario());
  }
  
  public CommandLine(CoreScenario theScenario)
  {
    // do some kind of checks?
    _myScenario = theScenario;

    // and create the list of observers
    _myObservers = new Vector<ScenarioObserver>(0, 1);
  	
  }

  /**
   * run through the scenario until its natural conclusion
   */
  public void run()
  {

    // ok, listen out for the scenario finishing
    _myScenario.addScenarioRunningListener(new ScenarioRunningListener()
    {
      public void newScenarioStepTime(int val)
      {
      }

      /**
       * the scenario has stopped running on auto
       */
      public void paused()
      {
      }

      public void newStepTime(int val)
      {
      }

      public void restart(ScenarioType scenario)
      {
      }

      public void started()
      {
        System.out.print("STARTED. ");
      }

      public void finished(long elapsedTime, String reason)
      {
        double secs = elapsedTime / 1000d;
        String elapsedTimeStr = MWC.Utilities.TextFormatting.GeneralFormat.formatOneDecimalPlace(secs);
        System.out.println("STOPPED after:" + elapsedTimeStr + " secs");

        _isRunning = false;
      }
    });

    _isRunning = true;

    _myScenario.start();

    // wait for it to finish
    while (_isRunning)
    {
      try
      {
        Thread.sleep(50);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }

    }
    // and clear up
    scenarioComplete();
  }


  private void scenarioComplete()
  {
    // clear out the observers
    if (_myObservers != null)
    {
      for (int i = 0; i < _myObservers.size(); i++)
      {
        ScenarioObserver observer = (ScenarioObserver) _myObservers.elementAt(i);
        observer.tearDown(_myScenario);
      }
    }

  }


  /**
   * command to setup and execute the run
   *
   * @param scenario
   * @param control
   */
  public boolean setup(String scenario,
                       String control,
                       PrintStream out)
  {
    boolean success = true;

    try
    {

      // get the scenario
      InputStream scenarioStream = getInput(scenario, "Scenario file (" + scenario + ")");

      // get the control data
      InputStream controlStream = getInput(control, "Control file (" + control + ")");

      // and load the data
      importStreams(scenario, scenarioStream, control, controlStream);

    }
    catch (RuntimeException e)
    {
      // so, we've failed to get the data, output it
      String msg = e.getMessage();

      out.println("Setup failed:" + msg);

      if (msg != null)
      {
        // just see if we may have the files the wrong way around
        if (msg.indexOf("ObserverList") != -1)
        {
          out.println("Could you have the scenario and control files in the wrong order?");
        }
      }
      else
      {
        System.err.println("Unknown runtimrerror");
        e.printStackTrace();
      }

      // and remember it failed
      success = false;
    }

    return success;
  }

  public CoreScenario getScenario()
  {
    return _myScenario;
  }

  public void addObserver(CoreObserver obs)
  {
    _myObservers.add(obs);
  }

  public void clearObservers()
  {
    _myObservers.clear();
  }

  /**
   * method to handle loading the data from the streams
   *
   * @param scenario
   * @param scenarioStream
   * @param control
   * @param controlStream
   */
  public void importStreams(String scenario, InputStream scenarioStream,
                            String control, InputStream controlStream)
  {


    // todo: handle the multi-participant generation aspects

    // ok, read in the scenario
    ASSET.Util.XML.ASSETReaderWriter.importThis(_myScenario, scenario, scenarioStream);

    // now get the control data
    ASSETReaderWriter.ResultsContainer controller = ASSET.Util.XML.ASSETReaderWriter.importThisControlFile(control,
                                                                                                           controlStream);

    // and do our stuff with the observers (tell them about our scenario)
    configureObservers(controller.observerList, controller.outputDirectory);

    // and setup the random number seed
    _myScenario.setSeed(controller.randomSeed);
  }

  public void configureObservers(Vector<ScenarioObserver> observers, File outputPath)
  {
    Iterator<ScenarioObserver> iter = observers.iterator();
    while (iter.hasNext())
    {
      ScenarioObserver observer = iter.next();

      // is this an observer which is interested in the output path
      if (observer instanceof RecordToFileObserverType)
      {
        RecordToFileObserverType obs = (RecordToFileObserverType) observer;
        obs.setDirectory(outputPath);
      }

      // ok, let it set itself up
      observer.setup(_myScenario);

      // and remember it for when we finish
      _myObservers.add(observer);
    }
  }

  /**
   * check if this is a valid file
   *
   * @param fileName the supplied filename
   * @param type     the type of file being specified (scenario or control)
   * @return an error message, or null for satisfactory
   */
  static InputStream getInput(String fileName, String type)
    throws RuntimeException
  {
    InputStream res = null;

    // check name isn't null
    if (fileName == null)
    {
      throw new RuntimeException(type + FILE_NOT_SPECIFIED);
    }
    else
    {
      // check file exists
      File tmpFile = new File(fileName);

      if (tmpFile.exists())
      {
        // hey, everything's ok - read it into the string
        try
        {
          res = new FileInputStream(tmpFile);
        }
        catch (FileNotFoundException e)
        {
          throw new RuntimeException(type + FILE_NOT_FOUND);
        }
      }
      else
        throw new RuntimeException(type + FILE_NOT_FOUND);
    }

    // ok, all done.
    return res;
  }


  public static class ServerTest extends SupportTesting
  {
    private String TEST_ROOT;

    public ServerTest(final String val)
    {
      super(val);
    }

    public void testInvalidStartups()
    {
      /**********************************************************************
       * first test invalid combinations
       *********************************************************************/
      boolean res = false;

//      final String sep = "\r\n";

      TEST_ROOT = System.getProperty("TEST_ROOT");
      if (TEST_ROOT == null)
      {
        TEST_ROOT = "../org.mwc.asset.sample_data/data";
      }

      System.out.println("root is:" + TEST_ROOT);

      CommandLine cl = new CommandLine();

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      PrintStream output = new PrintStream(bos);

      // check handles missing scenario, missing control
      res = cl.setup(null, null, output);
      assertFalse("correctly failed to open", res);
//      String outMsg = bos.toString().trim();
//      final String testMsg = "Setup failed:Scenario file (null)" + CommandLine.FILE_NOT_SPECIFIED;
      bos.reset();

      // check handles invalid scenario, missing control
      res = cl.setup(TEST_ROOT + "/test1_scenario_valid.xml", null, output);
      assertFalse("correctly failed to open", res);
      final String testMsg2 = "Setup failed:Control file (null)" + CommandLine.FILE_NOT_SPECIFIED;
      final String bosString = bos.toString().trim();
      assertEquals("message is correct", testMsg2, bosString);
      bos.reset();

      // check handles valid scenario, missing control
      res = cl.setup(TEST_ROOT + "\test1_scenario.xml", null, output);
      assertFalse("correctly failed to open", res);
      final String bos3 = bos.toString();
      assertTrue("message is correct", bos3.indexOf("Setup failed:Scenario file") > -1);
      bos.reset();

      // check handles missing scenario, invalid control
      res = cl.setup(null, TEST_ROOT + "\\test1_control_invalid.xml", output);
      assertFalse("correctly failed to open", res);
      assertTrue("message is correct", bos.toString().indexOf("Setup failed:Scenario file") > -1);
      bos.reset();

      // check handles valid scenario, invalid control
//      res = cl.setup(TEST_ROOT + "/test1_scenario_valid.xml",
//                     TEST_ROOT + "/test1_control_invalid.xml", output);
//      assertFalse("correctly failed to open", res);
//      final String bos6 = bos.toString();
//      assertTrue("message is correct", bos6.toString().indexOf("handler not found") > -1);
//      bos.reset();

      // check handles missing scenario, valid control
      res = cl.setup(null,
                     TEST_ROOT + "\\test1_control.xml", output);
      assertFalse("correctly failed to open", res);
      assertTrue("message is correct", bos.toString().indexOf("Setup failed:Scenario file (n") > -1);
      bos.reset();

      // check handles invalid scenario, valid control
//      res = cl.setup(TEST_ROOT + "\\test1_scenario_invalid.xml",
//                     TEST_ROOT + "\\test1_control_valid.xml", output);
//      assertFalse("correctly failed to open", res);
//      assertTrue("message is correct", bos.toString().indexOf("handler not found") > -1);
//      bos.reset();

      /**********************************************************************
       * ok, just check a valid startup combination
       *********************************************************************/

      System.err.println("all tests passed!");

      // check handles valid scenario, valid control
//      res = cl.setup(TEST_ROOT + "\\test1_scenario_valid.xml",
//                     TEST_ROOT + "\\test1_control_valid.xml", output);
//
//      assertTrue("correctly opened", res);

   //   cl.run();


    }

    public void testRunScenario()
    {
      // todo: implement thest tests

      // check we don't run when empty

      // load the data

      // check data is being output to the control destination

      // check we run

      // check we have completed

      // check everything's in a tidy state
    }

    public void testMonteCarlo()
    {

      TEST_ROOT = System.getProperty("TEST_ROOT");
      if (TEST_ROOT == null)
      {
        TEST_ROOT = "test_reports";
      }

      //   todo - create new command line extension which will handle multiple scenario generation


      // run scenario genny

    }


  }


  public static void main(String[] args)
  {

    CommandLine cl = new CommandLine();

    if(args.length == 0)
    {
    	System.out.println("Using hard-coded scenario files");
	    args = new String[]{"../org.mwc.asset.sample_data/data/CQB_Scenario.xml",
	    		"../org.mwc.asset.sample_data/data/CQB_Control.xml"};
    }

    //    args = new String[]{"D:/Dev/herding/scenario_1.xml","D:/Dev/herding/control_1.xml"};
    //    args = new String[]{"D:\\Dev\\Asset\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance_scenario.xml",
    //                        "D:\\Dev\\Asset\\src\\java\\ASSET_SRC\\ASSET\\Util\\MonteCarlo\\test_variance_area.xml"};

    // TEMPORARILY OVERRIDE THE EARTH MODEL
    WorldLocation.setModel(new MWC.Algorithms.EarthModels.CompletelyFlatEarth());

    if (args.length == 2)
    {

      // find out if this is a multi-scenario run
      // get the control file
      File controlFile = new File(args[1].trim());

      boolean isMultiScenario = false;

      try
      {
        // see if it contains multi-scenario instructions
        isMultiScenario = checkIfGenerationRequired(controlFile);
      }
      catch (FileNotFoundException e)
      {
        System.err.println("Sorry, control file:" + controlFile + " not found");
      }

      // so, is it?
      if (isMultiScenario)
      {
        // ok, pass it on to the multi scenario handler
        MultiScenarioCommandLine multi = new MultiScenarioCommandLine();
        int res = multi.processThis(args, System.out, System.err, System.in);
        if (res != MultiScenarioCommandLine.SUCCESS)
        {
          System.err.println("Multi-scenario run failed. Terminated");
        }
      }
      else
      {

        // ok, load single scenario
        boolean success = cl.setup(args[0], args[1], System.err);

        // did it work?
        if (success)
        {
          // ok, give it a go
          cl.run();

          while (cl._isRunning)
          {
            try
            {
              Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
              e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
          }

        } // whether we got a scenario or not
      }


    }
    else
    {
      if (args.length == 1)
      {
        System.err.println("Sorry, both a scenario file and command file are required");
        System.err.println("");
      }

      System.err.println("Usage:");
      System.err.println("    asset scenario_file control_file");
    }
  }
  
  public static boolean checkIfGenerationRequired(String controlFileName) throws FileNotFoundException
  {
  	File controlFile = new File(controlFileName);
  	return checkIfGenerationRequired(controlFile);
  }

  private static boolean checkIfGenerationRequired(File controlFile) throws FileNotFoundException
  {
    boolean multiScenario = false;

    // does this contain a nulti-scenario generator?

    // first read the file into a string
    FileReader fr = new FileReader(controlFile);

    char[] charArray = new char[30000];
    try
    {
      fr.read(charArray);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    String str = new String(charArray);

    // ok, now have a look inside it.
    if (str.indexOf(MultiScenarioGenerator.GENERATOR_TYPE) > 0)
    {
      multiScenario = true;
    }
    if (str.indexOf(MultiParticipantGenerator.GENERATOR_TYPE) > 0)
    {
      multiScenario = true;
    }
    return multiScenario;
  }
}
