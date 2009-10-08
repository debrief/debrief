package ASSET.Scenario.Observers.Summary;

import ASSET.ScenarioType;
import ASSET.Scenario.ScenarioRunningListener;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Measure the elapsed time in a scenario
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 11-Aug-2004
 * Time: 16:14:50
 * To change this template use File | Settings | File Templates.
 */
public class ElapsedTimeObserver extends EndOfRunBatchObserver implements ScenarioRunningListener
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////


  /**
   * the start time for the scenario
   */
  private long _startTime;


  /**
   * the elapsed time within the scenario
   */
  private long _elapsedTime;


  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  /**
   * @param directoryName the directory to write the results to
   * @param fileName      the filename to write the results to
   * @param observerName  the name of this observer
   * @param isActive      whether this observer is active
   */
  public ElapsedTimeObserver(final String directoryName, final String fileName, final String observerName,
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
    myWriter.write("Elapsed time (secs):" + (_elapsedTime / 1000d));
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
    setBatchCollationProcessing(fileName, collationMethod, perCaseProcessing, isActive, "secs");
  }


  /**
   * add any applicable listeners
   */
  protected void addListeners(ScenarioType scenario)
  {
    _myScenario.addScenarioRunningListener(this);
  }

  /**
   * remove any listeners
   */
  protected void removeListeners(ScenarioType scenario)
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
    _startTime = _myScenario.getTime();
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
    _elapsedTime = _myScenario.getTime() - _startTime;

    // are we recording to batch?
    if (_batcher != null)
    {
      _batcher.submitResult(_myScenario.getName(), _myScenario.getCaseId(), (_elapsedTime/1000d));
    }
  }
}
