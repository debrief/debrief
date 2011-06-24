package ASSET.GUI.MonteCarlo;

import ASSET.ScenarioType;
import ASSET.Scenario.CoreScenario;
import ASSET.Scenario.Observers.ScenarioObserver;
import ASSET.Scenario.ScenarioRunningListener;
import ASSET.Util.MonteCarlo.MultiParticipantGenerator;
import ASSET.Util.MonteCarlo.ScenarioGenerator;
import ASSET.Util.MonteCarlo.XMLVariance;
import ASSET.Util.XML.ASSETReaderWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.Vector;

import javax.xml.xpath.XPathExpressionException;

/**
 * *******************************************************************
 * embedded class to handle the core (non-gui) functionality.
 * *******************************************************************
 */
abstract class LoaderCore
{
  /**
   * the file to read the scenario from
   */
  private File _scenarioFile;

  /**
   * the file to read control data from
   */
  private File _controlFile;

  /**
   * our scenario
   */
  final CoreScenario _myScenario;

  /**
   * the set of observers we've loaded and now manage.
   */
  Vector<ScenarioObserver> _myObservers;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  protected LoaderCore(CoreScenario myScenario)
  {
    this._myScenario = myScenario;

    // add ourselves as a listener to this scenario

    // we also want to listen out for the scenario finishing - so we can trigger our _myObservers
    _myScenario.addScenarioRunningListener(new ScenarioRunningListener()
    {
      public void newScenarioStepTime(int val)
      {
      }

      public void newStepTime(int val)
      {
      }

      public void restart(ScenarioType scenario)
      {
      }

      public void paused()
      {
      }

      public void started()
      {
      }

      public void finished(long elapsedTime, String reason)
      {
        if (_myObservers != null)
        {
          // ok, run through our observers telling them to stop
          for (int i = 0; i < _myObservers.size(); i++)
          {
            ScenarioObserver observer = (ScenarioObserver) _myObservers.elementAt(i);
            observer.tearDown(_myScenario);
          }
        }

        // and lastly, remove ourselves from the scenario
        _myScenario.removeScenarioRunningListener(this);
      }
    });
  }


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * write a message to a message tracking window
   */
  abstract void writeMessage(String msg);


  public void buildScenario() throws XPathExpressionException
  {
    writeMessage("Building scenario");

    // are we doing multi-participant?
    if (_controlFile != null)
    {
      // read in the control file
      Document controlDoc = null;
      Document scenarioDoc = null;
      Document mutatedScenario = null;
      try
      {
        writeMessage("Starting file import");
        // read in the scenario file
        scenarioDoc = ScenarioGenerator.readDocumentFrom(new FileInputStream(_scenarioFile));
        writeMessage("Scenario file imported");

        // also read the control file into a document (used to manage the participant variances)
        controlDoc = ScenarioGenerator.readDocumentFrom(new FileInputStream(_controlFile));
        writeMessage("Control file imported");
      }
      catch (FileNotFoundException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        MWC.Utilities.Errors.Trace.trace("Failed to find file:" + e.getMessage());
        return;
      }
      catch (SAXException se)
      {
        se.printStackTrace();
        MWC.Utilities.Errors.Trace.trace("File import failed");
        return;
      }
      // mutate a new scenario
      MultiParticipantGenerator partGenny = new MultiParticipantGenerator(controlDoc);
      partGenny.setDocument(scenarioDoc);
      try
      {
        writeMessage("Starting participant generation");
        mutatedScenario = partGenny.createNewRandomisedPermutation();
        writeMessage("New participants inserted");
      }
      catch (XMLVariance.IllegalExpressionException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
      catch (XMLVariance.MatchingException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

      // store the scenario to file
      File tmpFile = null;
      try
      {
        tmpFile = File.createTempFile("asset_working", ".xml");
        writeScenarioToDisk(mutatedScenario, tmpFile);
        writeMessage("Working scenario written to disk");
        writeMessage("  " + tmpFile.getCanonicalPath());
      }
      catch (IOException e)
      {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }

      // and load the temp working scenario
      loadScenario(tmpFile);
    }
    else
    {
      // no control file, just load the plain scenario
      loadScenario(_scenarioFile);
    }

    // and load the control file (if there is one) so we can do the observers
    if (_controlFile != null)
    {
      loadControlFile();
    }
  }

  /**
   * write this single scenario to the specified temporary file
   *
   * @param scenario the scenario to write
   * @param tmpFile  the destination
   */
  private void writeScenarioToDisk(Document scenario, File tmpFile)
  {
    String asString = ScenarioGenerator.writeToString(scenario);

    try
    {
      // put it into a writer
      FileWriter fw = new FileWriter(tmpFile);

      // write it out
      fw.write(asString);

      // and close it
      fw.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }

  }

  public void loadData()
  {

  }

  /**
   * store the scenario
   *
   * @param thisFile
   */
  public void setScenarioFile(File thisFile)
  {
    _scenarioFile = thisFile;

    checkEnable();
  }

  /**
   * check whether we have sufficient details to do a build
   */
  private void checkEnable()
  {
    if (_scenarioFile != null)
    {
      buildEnabled(true);
    }
    else
      buildEnabled(false);
  }

  /**
   * enable/disable the build button
   *
   * @param enabled
   */
  public abstract void buildEnabled(boolean enabled);

  /**
   * store the control file
   *
   * @param thisFile
   */
  public void setControllerFile(File thisFile)
  {
    _controlFile = thisFile;
    checkEnable();
  }

  public void loadScenario(File tmpFile)
  {
    // setup the _myObservers
    try
    {
      // load the scenario
      writeMessage("About to import working scenario");
      InputStream inputStream = new FileInputStream(tmpFile);
      ASSETReaderWriter.importThis(_myScenario, tmpFile.getName(), inputStream);
      writeMessage("Working scenario imported");

    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    writeMessage("Observers configured");


  }

  private void loadControlFile()
  {
    try
    {
      ASSETReaderWriter.ResultsContainer controlStuff;
      InputStream is = new FileInputStream(_controlFile);
      controlStuff = ASSETReaderWriter.importThisControlFile(_controlFile.getName(),
        is);

      // sort out the _myObservers
      _myObservers = controlStuff.observerList;
      for (int i = 0; i < _myObservers.size(); i++)
      {
        ScenarioObserver observer = (ScenarioObserver) _myObservers.elementAt(i);
        observer.setup(_myScenario);
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }
}
