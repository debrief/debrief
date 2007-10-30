package ASSET.Scenario.Observers.Summary;

import ASSET.Scenario.Observers.EndOfRunRecordToFileObserver;
import ASSET.ScenarioType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Abstract class containing parent methods used to support a batch observer which writes it's output at the
 * end of a run, as well as during a run.
 * <p/>
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 18-Aug-2004
 * Time: 15:56:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class EndOfRunBatchObserver extends EndOfRunRecordToFileObserver implements BatchCollator
{
  //////////////////////////////////////////////////
  // member variables
  //////////////////////////////////////////////////

  /**
   * our batch collator
   */
  protected BatchCollatorHelper _batcher = null;
  /**
   * whether to override (cancel) writing per-scenario results to file
   */
  private boolean _onlyBatch = false;

  //////////////////////////////////////////////////
  // constructor
  //////////////////////////////////////////////////
  public EndOfRunBatchObserver(final String directoryName, final String fileName, final String observerName,
                               boolean isActive)
  {
    super(directoryName, fileName, observerName, isActive);
  }

  //////////////////////////////////////////////////
  // member methods
  //////////////////////////////////////////////////


  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @return whether to override batch processing
   */
  public boolean getBatchOnly()
  {
    return _onlyBatch;
  }

  /**
   * we're getting up and running.  The observers have been created and we've remembered
   * the scenario
   *
   * @param scenario the new scenario we're looking at
   */
  protected void performSetupProcessing(ScenarioType scenario)
  {
  }

  /**
   * run is complete, and we've output our data. clear the data structures
   */
  protected void resetData()
  {
  }

  /**
   * output the results of just this run
   *
   * @param myWriter the stream to write to
   * @throws IOException if there are any problems
   */
  protected abstract void writeMyResults(FileWriter myWriter) throws IOException;


  /**
   * whether to actually write the end-of-run results to file
   */
  protected boolean doEndOfRunWrite()
  {
    return !_onlyBatch;
  }


  /**
   * configure the batch processing
   *
   * @param fileName          the filename to write to
   * @param collationMethod   how to collate the data
   * @param perCaseProcessing whether to collate the stats on a per-case basis
   * @param isActive          whether this collator is active
   */
  protected void setBatchCollationProcessing(String fileName, String collationMethod,
                                             boolean perCaseProcessing, boolean isActive,
                                             String units)
  {
    _batcher = new BatchCollatorHelper(getName(), perCaseProcessing, collationMethod, isActive, units);

    // do we have a filename?
    if (fileName == null)
      fileName = getName() + "." + getMySuffix();

    _batcher.setFileName(fileName);
  }


  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @param override
   */
  public void setBatchOnly(boolean override)
  {
    _onlyBatch = override;
  }

  /**
   * accessor to retrieve batch processing settings
   */
  public BatchCollatorHelper getBatchHelper()
  {
    return _batcher;
  }


  /**
   * configure the batch processing
   *
   * @param fileName          the filename to write to
   * @param collationMethod   how to collate the data
   * @param perCaseProcessing whether to collate the stats on a per-case basis
   * @param isActive          whether this collator is active
   */
  abstract public void setBatchCollationProcessing(String fileName, String collationMethod,
                                                   boolean perCaseProcessing, boolean isActive);


  //////////////////////////////////////////////////
  // inter-scenario observer methods
  //////////////////////////////////////////////////
  public void finish()
  {
    if (isActive())
    {
      if (_batcher != null)
      {
        // ok, get the batch thingy to do it's stuff
        _batcher.writeOutput(getHeaderInfo());
      }
    }
  }

  public void initialise(File outputDirectory)
  {
    if (isActive())
    {
      // set the output directory for the batch collator
      if (_batcher != null)
        _batcher.setDirectory(outputDirectory);
    }
  }

  /**
   * determine the normal suffix for this file type
   */
  protected String getMySuffix()
  {
    return "csv";  //To change body of implemented methods use File | Settings | File Templates.
  }


}
