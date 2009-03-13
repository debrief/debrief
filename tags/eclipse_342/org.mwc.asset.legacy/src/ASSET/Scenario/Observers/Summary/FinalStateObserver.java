package ASSET.Scenario.Observers.Summary;

import ASSET.Scenario.ScenarioRunningListener;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Record the final state (reason) recorded  at the end of a scenario
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 11-Aug-2004
 * Time: 16:14:50
 * To change this template use File | Settings | File Templates.
 */

public class FinalStateObserver extends EndOfRunBatchObserver implements ScenarioRunningListener
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////


  /**
   * the reason returned when the scenario finished
   */
  private String _finalState;


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  /**
   * @param directoryName the directory to write the results to
   * @param fileName      the filename to write the results to
   * @param observerName  the name of this observer
   * @param isActive      whether this observer is active
   */
  public FinalStateObserver(final String directoryName, final String fileName, final String observerName,
                            boolean isActive)
  {
    super(directoryName, fileName, observerName, isActive);
  }

  //////////////////////////////////////////////////
  // setup management
  //////////////////////////////////////////////////

  /**
   * ok, run complete. output my results
   *
   * @param myWriter the writer to use
   * @throws IOException in case there's a problem
   */
  protected void writeMyResults(FileWriter myWriter) throws IOException
  {
    myWriter.write("Final reason:" + _finalState);
  }



  //////////////////////////////////////////////////
  // batch collation methods
  //////////////////////////////////////////////////


  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////

  /**
   * configure the batch processing
   *
   * @param fileName          the filename to write to
   * @param collationMethod   how to collate the data
   * @param perCaseProcessing whether to collate the stats on a per-case basis
   * @param isActive          whether this collator is active
   */
  public void setBatchCollationProcessing(String fileName, String collationMethod,
                                          boolean perCaseProcessing, boolean isActive)
  {
    setBatchCollationProcessing(fileName, collationMethod, perCaseProcessing, isActive, "n/a");
  }


  /**
   * add any applicable listeners
   */
  protected void addListeners()
  {
    _myScenario.addScenarioRunningListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners()
  {
    _myScenario.removeScenarioRunningListener(this);
  }
  //////////////////////////////////////////////////
  // scenario running support
  //////////////////////////////////////////////////
  /**
   * the scenario step time has changed
   */
  public void newScenarioStepTime(int val)
  {
    // ignore
  }

  /**
   * the GUI step time has changed
   */
  public void newStepTime(int val)
  {
    // ignore
  }

  /**
   * the scenario has started running on auto
   */
  public void started()
  {
    _finalState = null;
  }

  /**
   * the scenario has stopped running on auto
   */
  public void paused()
  {
    // let's not worry about this little thing
  }


  /**
   * the scenario has stopped running on auto
   */
  public void finished(long elapsedTime, String reason)
  {
    // right, not record the end time
    _finalState = reason;

    // are we recording to batch?
    if (_batcher != null)
    {
      _batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(), _finalState);
    }
  }
}

