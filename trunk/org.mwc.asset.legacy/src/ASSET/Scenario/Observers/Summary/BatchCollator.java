package ASSET.Scenario.Observers.Summary;

import ASSET.Scenario.Observers.InterScenarioObserverType;

/**
 * Definition of type which is able to summarize performance across a series of runs
 * Created by IntelliJ IDEA.
 * User: Ian.Mayo
 * Date: 12-Aug-2004
 * Time: 13:32:20
 * To change this template use File | Settings | File Templates.
 */
public interface BatchCollator extends InterScenarioObserverType
{
  /**
   * produce the average of the supplied values
   */
  final public static String AVERAGE = "AVERAGE";

  /**
   * count the number of results instances provided
   */
  final public static String COUNT = "COUNT";

  /**
   * just provide a listing of the instances provided
   */
  final public static String LIST = "LIST";

  /**
   * provide a listing of the instances provided, each annotated with it's scenario
   */
  final public static String ITEMIZED_LIST = "ITEMIZED_LIST";

  /**
   * provide a listing of the the frequency of reach result
   */
  final public static String FREQUENCY_LIST = "FREQUENCY_LIST";


  /**
   * configure the batch processing
   *
   * @param fileName          the filename to write to
   * @param collationMethod   how to collate the data
   * @param perCaseProcessing whether to collate the stats on a per-case basis
   * @param isActive          whether batch collation is active
   */
  public void setBatchCollationProcessing(String fileName,
                                          String collationMethod, boolean perCaseProcessing,
                                          boolean isActive);

  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @param override
   */
  public void setBatchOnly(boolean override);

  /**
   * whether to override (cancel) writing per-scenario results to file
   *
   * @return whether to override batch processing
   */
  public boolean getBatchOnly();

  /** accessor to retrieve batch processing settings
   *
   */
  public BatchCollatorHelper getBatchHelper();

}
